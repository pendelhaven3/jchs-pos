package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PurchasePaymentBankTransferDao;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentBankTransfer;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentBankTransferSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchasePaymentBankTransferDaoImpl extends MagicDao implements PurchasePaymentBankTransferDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PURCHASE_PAYMENT_ID, BANK, AMOUNT, REFERENCE_NO, TRANSFER_DT,"
			+ " b.PURCHASE_PAYMENT_NO, b.SUPPLIER_ID, c.NAME as SUPPLIER_NAME"
			+ " from PURCHASE_PAYMENT_BANK_TRANSFER a"
			+ " join PURCHASE_PAYMENT b"
			+ "   on b.ID = a.PURCHASE_PAYMENT_ID"
			+ " join SUPPLIER c"
			+ "   on c.ID = b.SUPPLIER_ID";
	
	private PurchasePaymentBankTransferRowMapper bankTransferRowMapper = 
			new PurchasePaymentBankTransferRowMapper();
	
	@Override
	public void save(PurchasePaymentBankTransfer bankTransfer) {
		if (bankTransfer.getId() == null) {
			insert(bankTransfer);
		} else {
			update(bankTransfer);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PURCHASE_PAYMENT_BANK_TRANSFER"
			+ " (PURCHASE_PAYMENT_ID, BANK, AMOUNT, REFERENCE_NO, TRANSFER_DT) values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentBankTransfer bankTransfer) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, bankTransfer.getParent().getId());
				ps.setString(2, bankTransfer.getBank());
				ps.setBigDecimal(3, bankTransfer.getAmount());
				ps.setString(4, bankTransfer.getReferenceNumber());
				ps.setDate(5, new java.sql.Date(bankTransfer.getTransferDate().getTime()));
				return ps;
			}
		}, holder);
		
		bankTransfer.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update PURCHASE_PAYMENT_BANK_TRANSFER"
			+ " set BANK = ?, AMOUNT = ?, REFERENCE_NO = ?, TRANSFER_DT = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentBankTransfer bankTransfer) {
		getJdbcTemplate().update(UPDATE_SQL, 
				bankTransfer.getBank(),
				bankTransfer.getAmount(),
				bankTransfer.getReferenceNumber(),
				bankTransfer.getTransferDate(),
				bankTransfer.getId());
	}

	private static final String FIND_ALL_BY_PURCHASE_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.PURCHASE_PAYMENT_ID = ?";
	
	@Override
	public List<PurchasePaymentBankTransfer> findAllByPurchasePayment(PurchasePayment purchasePayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_PAYMENT_SQL, bankTransferRowMapper, 
				purchasePayment.getId());
	}

	private class PurchasePaymentBankTransferRowMapper implements RowMapper<PurchasePaymentBankTransfer> {

		@Override
		public PurchasePaymentBankTransfer mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentBankTransfer bankTransfer = new PurchasePaymentBankTransfer();
			bankTransfer.setId(rs.getLong("ID"));
			
			PurchasePayment payment = new PurchasePayment();
			payment.setId(rs.getLong("PURCHASE_PAYMENT_ID"));
			payment.setPurchasePaymentNumber(rs.getLong("PURCHASE_PAYMENT_NO"));
			
			Supplier supplier = new Supplier();
			supplier.setId(rs.getLong("SUPPLIER_ID"));
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			payment.setSupplier(supplier);
			
			bankTransfer.setParent(payment);
			
			bankTransfer.setBank(rs.getString("BANK"));
			bankTransfer.setReferenceNumber(rs.getString("REFERENCE_NO"));
			bankTransfer.setAmount(rs.getBigDecimal("AMOUNT"));
			bankTransfer.setTransferDate(rs.getDate("TRANSFER_DT"));
			
			return bankTransfer;
		}
		
	}
	
	private static final String DELETE_SQL = "delete from PURCHASE_PAYMENT_BANK_TRANSFER where ID = ?";
	
	@Override
	public void delete(PurchasePaymentBankTransfer bankTransfer) {
		getJdbcTemplate().update(DELETE_SQL, bankTransfer.getId());
	}

	@Override
	public List<PurchasePaymentBankTransfer> search(PurchasePaymentBankTransferSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getSupplier() != null) {
			sql.append(" and c.ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and b.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getFromDate() != null) {
			sql.append(" and a.TRANSFER_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and a.TRANSFER_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
		sql.append(" order by a.TRANSFER_DT, c.NAME, b.PURCHASE_PAYMENT_NO, a.BANK");
		
		return getJdbcTemplate().query(sql.toString(), bankTransferRowMapper, params.toArray());
	}

}