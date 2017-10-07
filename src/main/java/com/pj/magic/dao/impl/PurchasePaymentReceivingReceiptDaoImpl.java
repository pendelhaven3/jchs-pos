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

import com.pj.magic.dao.PurchasePaymentReceivingReceiptDao;
import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;

@Repository
public class PurchasePaymentReceivingReceiptDaoImpl extends MagicDao implements PurchasePaymentReceivingReceiptDao {

	private static final String BASE_SELECT_SQL =
			"   select a.ID, PURCHASE_PAYMENT_ID, RECEIVING_RECEIPT_ID,"
			+ " b.RECEIVING_RECEIPT_NO, b.RECEIVED_DT, b.VAT_INCLUSIVE, b.VAT_RATE, b.REFERENCE_NO"
			+ " from PURCHASE_PAYMENT_RECEIVING_RECEIPT a"
			+ " join RECEIVING_RECEIPT b"
			+ "   on b.ID = a.RECEIVING_RECEIPT_ID";
	
	private PurchasePaymentReceivingReceiptRowMapper rowMapper = new PurchasePaymentReceivingReceiptRowMapper();
	
	private static final String INSERT_SQL =
			"insert into PURCHASE_PAYMENT_RECEIVING_RECEIPT"
			+ " (PURCHASE_PAYMENT_ID, RECEIVING_RECEIPT_ID) values (?, ?)";
	
	@Override
	public void insert(final PurchasePaymentReceivingReceipt paymentReceivingReceipt) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, paymentReceivingReceipt.getParent().getId());
				ps.setLong(2, paymentReceivingReceipt.getReceivingReceipt().getId());
				return ps;
			}
		}, holder);
		
		paymentReceivingReceipt.setId(holder.getKey().longValue());
	}

	private static final String FIND_ALL_BY_PURCHASE_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where PURCHASE_PAYMENT_ID = ?"
			+ " order by b.RECEIVING_RECEIPT_NO";
	
	@Override
	public List<PurchasePaymentReceivingReceipt> findAllByPurchasePayment(PurchasePayment payment) {
		List<PurchasePaymentReceivingReceipt> receivingReceipts = 
				getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_PAYMENT_SQL, rowMapper, payment.getId());
		for (PurchasePaymentReceivingReceipt receivingReceipt : receivingReceipts) {
			receivingReceipt.setParent(payment);
		}
		return receivingReceipts;
	}

	private static final String DELETE_ALL_BY_PURCHASE_PAYMENT_SQL = 
			"delete from PURCHASE_PAYMENT_RECEIVING_RECEIPT where PURCHASE_PAYMENT_ID = ?";
	
	@Override
	public void deleteAllByPurchasePayment(PurchasePayment purchasePayment) {
		getJdbcTemplate().update(DELETE_ALL_BY_PURCHASE_PAYMENT_SQL, purchasePayment.getId());
	}

	private static final String DELETE_SQL = "delete from PURCHASE_PAYMENT_RECEIVING_RECEIPT where ID = ?";
	
	@Override
	public void delete(PurchasePaymentReceivingReceipt paymentReceivingReceipt) {
		getJdbcTemplate().update(DELETE_SQL, paymentReceivingReceipt.getId());
	}

	private class PurchasePaymentReceivingReceiptRowMapper implements RowMapper<PurchasePaymentReceivingReceipt> {

		@Override
		public PurchasePaymentReceivingReceipt mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentReceivingReceipt paymentReceivingReceipt = new PurchasePaymentReceivingReceipt();
			paymentReceivingReceipt.setId(rs.getLong("ID"));
			
			PurchasePayment purchasePayment = new PurchasePayment();
			purchasePayment.setId(rs.getLong("PURCHASE_PAYMENT_ID"));
			paymentReceivingReceipt.setParent(purchasePayment);
			
			ReceivingReceipt receivingReceipt = new ReceivingReceipt();
			receivingReceipt.setId(rs.getLong("RECEIVING_RECEIPT_ID"));
			receivingReceipt.setReceivingReceiptNumber(rs.getLong("RECEIVING_RECEIPT_NO"));
			receivingReceipt.setReceivedDate(rs.getDate("RECEIVED_DT"));
			receivingReceipt.setVatInclusive("Y".equals(rs.getString("VAT_INCLUSIVE")));
			receivingReceipt.setVatRate(rs.getBigDecimal("VAT_RATE"));
			receivingReceipt.setReferenceNumber(rs.getString("REFERENCE_NO"));
			paymentReceivingReceipt.setReceivingReceipt(receivingReceipt);
			
			return paymentReceivingReceipt;
		}
		
	}

}