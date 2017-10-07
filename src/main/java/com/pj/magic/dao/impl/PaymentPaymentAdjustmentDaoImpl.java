package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PaymentPaymentAdjustmentDao;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentPaymentAdjustment;

@Repository
public class PaymentPaymentAdjustmentDaoImpl extends MagicDao implements PaymentPaymentAdjustmentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PAYMENT_ID, ADJUSTMENT_TYPE_ID, REFERENCE_NO, a.AMOUNT,"
			+ " b.CODE as ADJUSTMENT_TYPE_CODE"
			+ " from PAYMENT_PAYMENT_ADJUSTMENT a"
			+ " join ADJUSTMENT_TYPE b"
			+ "   on b.ID = a.ADJUSTMENT_TYPE_ID";
	
	private PaymentAdjustmentRowMapper adjustmentRowMapper = new PaymentAdjustmentRowMapper();
	
	@Override
	public void save(PaymentPaymentAdjustment adjustment) {
		if (adjustment.getId() == null) {
			insert(adjustment);
		} else {
			update(adjustment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PAYMENT_PAYMENT_ADJUSTMENT"
			+ " (PAYMENT_ID, ADJUSTMENT_TYPE_ID, REFERENCE_NO, AMOUNT) values (?, ?, ?, ?)";
	
	private void insert(final PaymentPaymentAdjustment adjustment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, adjustment.getParent().getId());
				ps.setLong(2, adjustment.getAdjustmentType().getId());
				ps.setString(3, adjustment.getReferenceNumber());
				ps.setBigDecimal(4, adjustment.getAmount());
				return ps;
			}
		}, holder);
		
		adjustment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update PAYMENT_PAYMENT_ADJUSTMENT"
			+ " set ADJUSTMENT_TYPE_ID = ?, REFERENCE_NO = ?, AMOUNT = ?"
			+ " where ID = ?";
	
	private void update(PaymentPaymentAdjustment adjustment) {
		getJdbcTemplate().update(UPDATE_SQL,
				adjustment.getAdjustmentType().getId(),
				adjustment.getReferenceNumber(),
				adjustment.getAmount(), 
				adjustment.getId());
	}

	private static final String FIND_ALL_BY_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.PAYMENT_ID = ?";
	
	@Override
	public List<PaymentPaymentAdjustment> findAllByPayment(Payment payment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PAYMENT_SQL, adjustmentRowMapper, payment.getId());
	}

	private class PaymentAdjustmentRowMapper implements RowMapper<PaymentPaymentAdjustment> {

		@Override
		public PaymentPaymentAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentPaymentAdjustment adjustment = new PaymentPaymentAdjustment();
			adjustment.setId(rs.getLong("ID"));
			adjustment.setParent(new Payment(rs.getLong("PAYMENT_ID")));
			adjustment.setAdjustmentType(new AdjustmentType(
					rs.getLong("ADJUSTMENT_TYPE_ID"), rs.getString("ADJUSTMENT_TYPE_CODE")));
			adjustment.setReferenceNumber(rs.getString("REFERENCE_NO"));
			adjustment.setAmount(rs.getBigDecimal("AMOUNT"));
			return adjustment;
		}
		
	}
	
	private static final String DELETE_ALL_BY_PAYMENT_SQL =
			"delete from PAYMENT_PAYMENT_ADJUSTMENT where PAYMENT_ID = ?";

	@Override
	public void deleteAllByPayment(Payment payment) {
		getJdbcTemplate().update(DELETE_ALL_BY_PAYMENT_SQL, payment.getId());
	}

	private static final String DELETE_SQL = "delete from PAYMENT_PAYMENT_ADJUSTMENT where ID = ?";
	
	@Override
	public void delete(PaymentPaymentAdjustment adjustment) {
		getJdbcTemplate().update(DELETE_SQL, adjustment.getId());
	}
	
}