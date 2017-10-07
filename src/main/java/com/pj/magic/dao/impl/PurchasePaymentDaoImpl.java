package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PurchasePaymentDao;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PurchasePaymentSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchasePaymentDaoImpl extends MagicDao implements PurchasePaymentDao {

	private static final String PURCHASE_PAYMENT_NUMBER_SEQUENCE = "PURCHASE_PAYMENT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, PURCHASE_PAYMENT_NO, SUPPLIER_ID, ENCODER,"
			+ " POST_IND, POST_DT, POST_BY,"
			+ " b.CODE as SUPPLIER_CODE, b.NAME as SUPPLIER_NAME, "
			+ " c.USERNAME as ENCODER_USERNAME,"
			+ " d.USERNAME as POST_BY_USERNAME,"
			+ " e.NAME as PAYMENT_TERM_NAME"
			+ " from PURCHASE_PAYMENT a"
			+ " join SUPPLIER b"
			+ "   on b.ID = a.SUPPLIER_ID"
			+ " join USER c"
			+ "   on c.ID = a.ENCODER"
			+ " left join USER d"
			+ "   on d.ID = a.POST_BY"
			+ " left join PAYMENT_TERM e"
			+ "   on e.ID = b.PAYMENT_TERM_ID";
	
	private PurchasePaymentRowMapper purchasePaymentRowMapper = new PurchasePaymentRowMapper();
	
	@Override
	public void save(PurchasePayment purchasePayment) {
		if (purchasePayment.getId() == null) {
			insert(purchasePayment);
		} else {
			update(purchasePayment);
		}
	}

	private static final String INSERT_SQL =
			"insert into PURCHASE_PAYMENT"
			+ " (PURCHASE_PAYMENT_NO, SUPPLIER_ID, CREATE_DT, ENCODER) values (?, ?, curdate(), ?)";
	
	private void insert(final PurchasePayment purchasePayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPurchasePaymentNumber());
				ps.setLong(2, purchasePayment.getSupplier().getId());
				ps.setLong(3, purchasePayment.getEncoder().getId());
				return ps;
			}
		}, holder);
		
		PurchasePayment updated = get(holder.getKey().longValue());
		purchasePayment.setId(updated.getId());
		purchasePayment.setPurchasePaymentNumber(updated.getPurchasePaymentNumber());
	}

	private long getNextPurchasePaymentNumber() {
		return getNextSequenceValue(PURCHASE_PAYMENT_NUMBER_SEQUENCE);
	}
	
	private static final String UPDATE_SQL =
			"update PURCHASE_PAYMENT set SUPPLIER_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " CANCEL_IND = ?, CANCEL_DT = ?, CANCEL_BY = ? where ID = ?";
	
	private void update(PurchasePayment payment) {
		getJdbcTemplate().update(UPDATE_SQL,
				payment.getSupplier().getId(),
				payment.isPosted() ? "Y" : "N",
				payment.isPosted() ? payment.getPostDate() : null,
				payment.isPosted() ? payment.getPostedBy().getId() : null,
				payment.isCancelled() ? "Y" : "N",
				payment.isCancelled() ? payment.getCancelDate() : null,
				payment.isCancelled() ? payment.getCancelledBy().getId() : null,
				payment.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL
			+ " where a.ID = ?";
	
	@Override
	public PurchasePayment get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, purchasePaymentRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String CONTAINS_RECEIVING_RECEIPT_NUMBER_WHERE_CLAUSE =
			" and exists("
			+ "  select *"
			+ "  from PURCHASE_PAYMENT_RECEIVING_RECEIPT pprr"
			+ "  join RECEIVING_RECEIPT rr"
			+ "    on rr.ID = pprr.RECEIVING_RECEIPT_ID"
			+ "  where pprr.PURCHASE_PAYMENT_ID = a.ID"
			+ "  and rr.RECEIVING_RECEIPT_NO = ?"
			+ ")";
	
	@Override
	public List<PurchasePayment> search(PurchasePaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getCancelled() != null) {
			sql.append(" and a.CANCEL_IND = ?");
			params.add(criteria.getCancelled() ? "Y" : "N");
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and a.SUPPLIER_ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getPaymentNumber() != null) {
			sql.append(" and a.PURCHASE_PAYMENT_NO = ?");
			params.add(criteria.getPaymentNumber());
		}
		
		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
		}
		
		if (criteria.getReceivingReceiptNumber() != null) {
			sql.append(CONTAINS_RECEIVING_RECEIPT_NUMBER_WHERE_CLAUSE);
			params.add(criteria.getReceivingReceiptNumber());
		}
		
		sql.append(" order by a.PURCHASE_PAYMENT_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), purchasePaymentRowMapper, params.toArray());
	}

	private class PurchasePaymentRowMapper implements RowMapper<PurchasePayment> {

		@Override
		public PurchasePayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePayment purchasePayment = new PurchasePayment();
			purchasePayment.setId(rs.getLong("ID"));
			purchasePayment.setPurchasePaymentNumber(rs.getLong("PURCHASE_PAYMENT_NO"));
			purchasePayment.setEncoder(new User(rs.getLong("ENCODER"), rs.getString("ENCODER_USERNAME")));
			purchasePayment.setSupplier(mapSupplier(rs));
			
			purchasePayment.setPosted("Y".equals(rs.getString("POST_IND")));
			if (purchasePayment.isPosted()) {
				purchasePayment.setPostDate(rs.getDate("POST_DT"));
				purchasePayment.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			return purchasePayment;
		}
		
		private Supplier mapSupplier(ResultSet rs) throws SQLException {
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("SUPPLIER_ID"));
			supplier.setCode(rs.getString("SUPPLIER_CODE"));
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			
			PaymentTerm paymentTerm = new PaymentTerm();
			paymentTerm.setName(rs.getString("PAYMENT_TERM_NAME"));
			supplier.setPaymentTerm(paymentTerm);
			
			return supplier;
		}
		
	}

}