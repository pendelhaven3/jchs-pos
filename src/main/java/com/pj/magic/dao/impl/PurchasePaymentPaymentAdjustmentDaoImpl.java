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

import com.pj.magic.dao.PurchasePaymentPaymentAdjustmentDao;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentPaymentAdjustment;

@Repository
public class PurchasePaymentPaymentAdjustmentDaoImpl extends MagicDao 
		implements PurchasePaymentPaymentAdjustmentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PURCHASE_PAYMENT_ID, PURCHASE_PAYMENT_ADJ_TYPE_ID, REFERENCE_NO, a.AMOUNT,"
			+ " b.CODE as ADJUSTMENT_TYPE_CODE, b.DESCRIPTION as ADJUSTMENT_TYPE_DESCRIPTION"
			+ " from PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT a"
			+ " join PURCHASE_PAYMENT_ADJ_TYPE b"
			+ "   on b.ID = a.PURCHASE_PAYMENT_ADJ_TYPE_ID";
	
	private PurchasePaymentAdjustmentRowMapper adjustmentRowMapper = new PurchasePaymentAdjustmentRowMapper();
	
	@Override
	public void save(PurchasePaymentPaymentAdjustment adjustment) {
		if (adjustment.getId() == null) {
			insert(adjustment);
		} else {
			update(adjustment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT"
			+ " (PURCHASE_PAYMENT_ID, PURCHASE_PAYMENT_ADJ_TYPE_ID, REFERENCE_NO, AMOUNT) values (?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentPaymentAdjustment adjustment) {
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
			"update PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT"
			+ " set PURCHASE_PAYMENT_ADJ_TYPE_ID = ?, REFERENCE_NO = ?, AMOUNT = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentPaymentAdjustment adjustment) {
		getJdbcTemplate().update(UPDATE_SQL,
				adjustment.getAdjustmentType().getId(),
				adjustment.getReferenceNumber(),
				adjustment.getAmount(), 
				adjustment.getId());
	}

	private static final String FIND_ALL_BY_PURCHASE_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.PURCHASE_PAYMENT_ID = ?";
	
	@Override
	public List<PurchasePaymentPaymentAdjustment> findAllByPurchasePayment(PurchasePayment purchasePayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_PAYMENT_SQL, adjustmentRowMapper, purchasePayment.getId());
	}

	private class PurchasePaymentAdjustmentRowMapper implements RowMapper<PurchasePaymentPaymentAdjustment> {

		@Override
		public PurchasePaymentPaymentAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentPaymentAdjustment adjustment = new PurchasePaymentPaymentAdjustment();
			adjustment.setId(rs.getLong("ID"));
			adjustment.setParent(new PurchasePayment(rs.getLong("PURCHASE_PAYMENT_ID")));
			
			PurchasePaymentAdjustmentType adjustmentType = new PurchasePaymentAdjustmentType();
			adjustmentType.setId(rs.getLong("PURCHASE_PAYMENT_ADJ_TYPE_ID"));
			adjustmentType.setCode(rs.getString("ADJUSTMENT_TYPE_CODE"));
			adjustmentType.setDescription(rs.getString("ADJUSTMENT_TYPE_DESCRIPTION"));
			adjustment.setAdjustmentType(adjustmentType);
			
			adjustment.setReferenceNumber(rs.getString("REFERENCE_NO"));
			adjustment.setAmount(rs.getBigDecimal("AMOUNT"));
			return adjustment;
		}
		
	}
	
	private static final String DELETE_ALL_BY_PURCHASE_PAYMENT_SQL =
			"delete from PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT where PURCHASE_PAYMENT_ID = ?";

	@Override
	public void deleteAllByPurchasePayment(PurchasePayment purchasePayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_PURCHASE_PAYMENT_SQL, purchasePayment.getId());
	}

	private static final String DELETE_SQL = "delete from PURCHASE_PAYMENT_PAYMENT_ADJUSTMENT where ID = ?";
	
	@Override
	public void delete(PurchasePaymentPaymentAdjustment paymentAdjustment) {
		getJdbcTemplate().update(DELETE_SQL, paymentAdjustment.getId());
	}
	
}