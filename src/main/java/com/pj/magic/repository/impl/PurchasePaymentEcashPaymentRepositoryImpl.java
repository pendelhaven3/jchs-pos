package com.pj.magic.repository.impl;

import java.sql.Connection;
import java.sql.Date;
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

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.EcashType;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentEcashPayment;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PurchasePaymentEcashPaymentSearchCriteria;
import com.pj.magic.repository.PurchasePaymentEcashPaymentRepository;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchasePaymentEcashPaymentRepositoryImpl extends MagicDao implements PurchasePaymentEcashPaymentRepository {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PURCHASE_PAYMENT_ID, a.AMOUNT, a.REFERENCE_NO, PAID_DT, PAID_BY,"
			+ " b.USERNAME as PAID_BY_USERNAME,"
			+ " a.ECASH_RECEIVER_ID, d.NAME as ECASH_RECEIVER_NAME,"
			+ " d.ECASH_TYPE_ID, e.CODE as ECASH_TYPE_CODE,"
			+ " c.PURCHASE_PAYMENT_NO"
			+ " from PURCHASE_PAYMENT_ECASH_PAYMENT a"
			+ " join USER b"
			+ "   on b.ID = a.PAID_BY"
			+ " join PURCHASE_PAYMENT c"
			+ "   on c.ID = a.PURCHASE_PAYMENT_ID"
			+ " join ECASH_RECEIVER d"
			+ "   on d.ID = a.ECASH_RECEIVER_ID"
			+ " join ECASH_TYPE e"
			+ "   on e.ID = d.ECASH_TYPE_ID"
			+ " where 1 = 1";
	
	private RowMapper<PurchasePaymentEcashPayment> rowMapper = new RowMapper<PurchasePaymentEcashPayment>() {

		@Override
		public PurchasePaymentEcashPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentEcashPayment ecashPayment = new PurchasePaymentEcashPayment();
			ecashPayment.setId(rs.getLong("ID"));
			ecashPayment.setParent(new PurchasePayment(rs.getLong("PURCHASE_PAYMENT_ID")));
			ecashPayment.getParent().setPurchasePaymentNumber(rs.getLong("PURCHASE_PAYMENT_NO"));
			ecashPayment.setAmount(rs.getBigDecimal("AMOUNT"));
			ecashPayment.setEcashReceiver(
					new EcashReceiver(rs.getLong("ECASH_RECEIVER_ID"), rs.getString("ECASH_RECEIVER_NAME")));
			ecashPayment.getEcashReceiver().setEcashType(
					new EcashType(rs.getLong("ECASH_TYPE_ID"), rs.getString("ECASH_TYPE_CODE")));
			ecashPayment.setReferenceNumber(rs.getString("REFERENCE_NO"));
			ecashPayment.setPaidDate(rs.getDate("PAID_DT"));
			ecashPayment.setPaidBy(
					new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
			return ecashPayment;
		}
	};
	
	@Override
	public void save(PurchasePaymentEcashPayment ecashPayment) {
		if (ecashPayment.getId() == null) {
			insert(ecashPayment);
		} else {
			update(ecashPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PURCHASE_PAYMENT_ECASH_PAYMENT"
			+ " (PURCHASE_PAYMENT_ID, AMOUNT, ECASH_RECEIVER_ID, REFERENCE_NO, PAID_DT, PAID_BY) values (?, ?, ?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentEcashPayment ecashPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, ecashPayment.getParent().getId());
				ps.setBigDecimal(2, ecashPayment.getAmount());
				ps.setLong(3, ecashPayment.getEcashReceiver().getId());
				ps.setString(4, ecashPayment.getReferenceNumber());
				ps.setDate(5, new Date(ecashPayment.getPaidDate().getTime()));
				ps.setLong(6, ecashPayment.getPaidBy().getId());
				return ps;
			}
		}, holder);
		
		ecashPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update PURCHASE_PAYMENT_ECASH_PAYMENT"
			+ " set AMOUNT = ?, ECASH_RECEIVER_ID = ?, REFERENCE_NO = ?, PAID_DT = ?, PAID_BY = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentEcashPayment ecashPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				ecashPayment.getAmount(),
				ecashPayment.getEcashReceiver().getId(),
				ecashPayment.getReferenceNumber(),
				ecashPayment.getPaidDate(),
				ecashPayment.getPaidBy().getId(),
				ecashPayment.getId());
	}

	private static final String FIND_ALL_BY_PURCHASE_PAYMENT_SQL = BASE_SELECT_SQL
			+ " and a.PURCHASE_PAYMENT_ID = ?";
	
	@Override
	public List<PurchasePaymentEcashPayment> findAllByPurchasePayment(PurchasePayment payment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_PAYMENT_SQL, rowMapper, payment.getId());
	}

	private static final String DELETE_SQL = "delete from PURCHASE_PAYMENT_ECASH_PAYMENT where ID = ?";
	
	@Override
	public void delete(PurchasePaymentEcashPayment ecashPayment) {
		getJdbcTemplate().update(DELETE_SQL, ecashPayment.getId());
	}

	@Override
	public List<PurchasePaymentEcashPayment> search(PurchasePaymentEcashPaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		
		if (criteria.getEcashReceiver() != null) {
			sql.append(" and a.ECASH_RECEIVER_ID = ?");
			params.add(criteria.getEcashReceiver().getId());
		}
		
		if (criteria.getDateFrom() != null) {
			sql.append(" and a.PAID_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getDateFrom()));
		}
		
		if (criteria.getDateTo() != null) {
			sql.append(" and a.PAID_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getDateTo()));
		}
		
		if (criteria.getEcashType() != null) {
			sql.append(" and d.ECASH_TYPE_ID = ?");
			params.add(criteria.getEcashType().getId());
		}
		
		sql.append(" order by a.PAID_DT, c.PURCHASE_PAYMENT_NO");
		
		return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
	}

	private static final String FIND_ONE_BY_ECASH_RECEIVER_SQL = BASE_SELECT_SQL 
			+ " and a.ECASH_RECEIVER_ID = ? limit 1";
	
	@Override
	public PurchasePaymentEcashPayment findOneByEcashReceiver(EcashReceiver ecashReceiver) {
		try {
			return getJdbcTemplate().queryForObject(FIND_ONE_BY_ECASH_RECEIVER_SQL, rowMapper, ecashReceiver.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
 	}
	
}