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

import com.pj.magic.dao.PurchaseReturnDao;
import com.pj.magic.model.PurchaseReturn;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PurchaseReturnSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchaseReturnDaoImpl extends MagicDao implements PurchaseReturnDao {

	private static final String PURCHASE_RETURN_NUMBER_SEQUENCE = "PURCHASE_RETURN_NO_SEQ";
	
	private static final String BASE_SELECT_SQL = 
			"select a.ID, PURCHASE_RETURN_NO, RECEIVING_RECEIPT_ID, b.RECEIVING_RECEIPT_NO, "
			+ " a.POST_IND, a.POST_DT, a.POST_BY, a.REMARKS,"
			+ " a.PAID_IND, a.PAID_DT, a.PAID_BY,"
			+ " b.SUPPLIER_ID, c.CODE as SUPPLIER_CODE, c.NAME as SUPPLIER_NAME,"
			+ " d.USERNAME as POST_BY_USERNAME,"
			+ " e.USERNAME as PAID_BY_USERNAME"
			+ " from PURCHASE_RETURN a"
			+ " join RECEIVING_RECEIPT b"
			+ "   on b.ID = a.RECEIVING_RECEIPT_ID"
			+ " join SUPPLIER c"
			+ "   on c.ID = b.SUPPLIER_ID"
			+ " left join USER d"
			+ "   on d.ID = a.POST_BY"
			+ " left join USER e"
			+ "   on e.ID = a.PAID_BY";
	
	private PurchaseReturnRowMapper purchaseReturnRowMapper = new PurchaseReturnRowMapper();
	
	private class PurchaseReturnRowMapper implements RowMapper<PurchaseReturn> {

		@Override
		public PurchaseReturn mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchaseReturn purchaseReturn = new PurchaseReturn();
			purchaseReturn.setId(rs.getLong("ID"));
			purchaseReturn.setPurchaseReturnNumber(rs.getLong("PURCHASE_RETURN_NO"));
			
			ReceivingReceipt receivingReceipt = new ReceivingReceipt();
			receivingReceipt.setId(rs.getLong("RECEIVING_RECEIPT_ID"));
			receivingReceipt.setReceivingReceiptNumber(rs.getLong("RECEIVING_RECEIPT_NO"));
			
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("SUPPLIER_ID"));
			supplier.setCode(rs.getString("SUPPLIER_CODE"));
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			receivingReceipt.setSupplier(supplier);
			purchaseReturn.setReceivingReceipt(receivingReceipt);
			
			purchaseReturn.setPosted("Y".equals(rs.getString("POST_IND")));
			if (purchaseReturn.isPosted()) {
				purchaseReturn.setPostDate(rs.getDate("POST_DT"));
				purchaseReturn.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			purchaseReturn.setPaid("Y".equals(rs.getString("PAID_IND")));
			if (purchaseReturn.isPaid()) {
				purchaseReturn.setPaidDate(rs.getDate("PAID_DT"));
				purchaseReturn.setPaidBy(new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
			}
			
			purchaseReturn.setRemarks(rs.getString("REMARKS"));
			
			return purchaseReturn;
		}
		
	}

	@Override
	public void save(PurchaseReturn purchaseReturn) {
		if (purchaseReturn.getId() == null) {
			insert(purchaseReturn);
		} else {
			update(purchaseReturn);
		}
	}

	private static final String UPDATE_SQL =
			"update PURCHASE_RETURN set RECEIVING_RECEIPT_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " REMARKS = ?, PAID_IND = ?, PAID_DT = ?, PAID_BY = ? where ID = ?";
	
	private void update(PurchaseReturn purchaseReturn) {
		getJdbcTemplate().update(UPDATE_SQL,
				purchaseReturn.getReceivingReceipt().getId(),
				purchaseReturn.isPosted() ? "Y" : "N",
				purchaseReturn.isPosted() ? purchaseReturn.getPostDate() : null,
				purchaseReturn.isPosted() ? purchaseReturn.getPostedBy().getId() : null,
				purchaseReturn.getRemarks(),
				purchaseReturn.isPaid() ? "Y" : "N",
				purchaseReturn.isPaid() ? purchaseReturn.getPaidDate() : null,
				purchaseReturn.isPaid() ? purchaseReturn.getPaidBy().getId() : null,
				purchaseReturn.getId());
	}
	
	private static final String INSERT_SQL = 
			"insert into PURCHASE_RETURN (PURCHASE_RETURN_NO, RECEIVING_RECEIPT_ID) values (?, ?)";

	private void insert(final PurchaseReturn purchaseReturn) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPurchaseReturnNumber());
				ps.setLong(2, purchaseReturn.getReceivingReceipt().getId());
				return ps;
			}
		}, holder);
		
		PurchaseReturn updated = get(holder.getKey().longValue());
		purchaseReturn.setId(updated.getId());
		purchaseReturn.setPurchaseReturnNumber(updated.getPurchaseReturnNumber());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public PurchaseReturn get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, purchaseReturnRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private long getNextPurchaseReturnNumber() {
		return getNextSequenceValue(PURCHASE_RETURN_NUMBER_SEQUENCE);
	}

	@Override
	public List<PurchaseReturn> search(PurchaseReturnSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getPurchaseReturnNumber() != null) {
			sql.append(" and a.PURCHASE_RETURN_NO = ?");
			params.add(criteria.getPurchaseReturnNumber());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getPaid() != null) {
			sql.append(" and a.PAID_IND = ?");
			params.add(criteria.getPaid() ? "Y" : "N");
		}
		
		if (criteria.getSupplier() != null) {
			sql.append(" and b.SUPPLIER_ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getReceivingReceipt() != null) {
			sql.append(" and RECEIVING_RECEIPT_ID = ?");
			params.add(criteria.getReceivingReceipt().getId());
		}
		
		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
		}
		
		if (criteria.getPostDateFrom() != null) {
			sql.append(" and a.POST_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateFrom()));
		}
		
		if (criteria.getPostDateTo() != null) {
			sql.append(" and a.POST_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateTo()));
		}
		
		sql.append(" order by PURCHASE_RETURN_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), purchaseReturnRowMapper, params.toArray());
	}

	private static final String FIND_BY_PURCHASE_RETURN_NUMBER_SQL = BASE_SELECT_SQL 
			+ " where a.PURCHASE_RETURN_NO = ?";
	
	@Override
	public PurchaseReturn findByPurchaseReturnNumber(long purchaseReturnNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PURCHASE_RETURN_NUMBER_SQL, purchaseReturnRowMapper, 
					purchaseReturnNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}