package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
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
import org.springframework.util.StringUtils;

import com.pj.magic.dao.PurchasePaymentCreditCardPaymentDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.Supplier;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchasePaymentCreditCardPaymentDaoImpl extends MagicDao implements PurchasePaymentCreditCardPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PURCHASE_PAYMENT_ID, AMOUNT, CREDIT_CARD_ID, TRANSACTION_DT, APPROVAL_CODE,"
			+ " b.PURCHASE_PAYMENT_NO,"
			+ " c.USER as CREDIT_CARD_USER, c.BANK as CREDIT_CARD_BANK, c.CUSTOMER_NUMBER,"
			+ " d.NAME as SUPPLIER_NAME"
			+ " from PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT a"
			+ " join PURCHASE_PAYMENT b"
			+ "   on b.ID = a.PURCHASE_PAYMENT_ID"
			+ " join CREDIT_CARD c"
			+ "   on c.ID = a.CREDIT_CARD_ID"
			+ " join SUPPLIER d"
			+ "   on d.ID = b.SUPPLIER_ID";
	
	private PurchasePaymentCreditCardPaymentRowMapper creditCardPaymentRowMapper = 
			new PurchasePaymentCreditCardPaymentRowMapper();
	
	@Override
	public void save(PurchasePaymentCreditCardPayment creditCardPayment) {
		if (creditCardPayment.getId() == null) {
			insert(creditCardPayment);
		} else {
			update(creditCardPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT"
			+ " (PURCHASE_PAYMENT_ID, AMOUNT, CREDIT_CARD_ID, TRANSACTION_DT, APPROVAL_CODE) values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentCreditCardPayment creditCardPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, creditCardPayment.getParent().getId());
				ps.setBigDecimal(2, creditCardPayment.getAmount());
				ps.setLong(3, creditCardPayment.getCreditCard().getId());
				ps.setDate(4, new Date(creditCardPayment.getTransactionDate().getTime()));
				ps.setString(5, creditCardPayment.getApprovalCode());
				return ps;
			}
		}, holder);
		
		creditCardPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT"
			+ " set AMOUNT = ?, CREDIT_CARD_ID = ?, TRANSACTION_DT = ?, APPROVAL_CODE = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentCreditCardPayment creditCardPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				creditCardPayment.getAmount(),
				creditCardPayment.getCreditCard().getId(),
				creditCardPayment.getTransactionDate(),
				creditCardPayment.getApprovalCode(),
				creditCardPayment.getId());
	}

	private static final String FIND_ALL_BY_PURCHASE_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.PURCHASE_PAYMENT_ID = ?";
	
	@Override
	public List<PurchasePaymentCreditCardPayment> findAllByPurchasePayment(PurchasePayment purchasePayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_PAYMENT_SQL, creditCardPaymentRowMapper, 
				purchasePayment.getId());
	}

	private class PurchasePaymentCreditCardPaymentRowMapper implements RowMapper<PurchasePaymentCreditCardPayment> {

		@Override
		public PurchasePaymentCreditCardPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentCreditCardPayment creditCardPayment = new PurchasePaymentCreditCardPayment();
			creditCardPayment.setId(rs.getLong("ID"));
			
			PurchasePayment purchasePayment = new PurchasePayment();
			purchasePayment.setId(rs.getLong("PURCHASE_PAYMENT_ID"));
			purchasePayment.setPurchasePaymentNumber(rs.getLong("PURCHASE_PAYMENT_NO"));
			
			Supplier supplier = new Supplier();
			supplier.setName(rs.getString("SUPPLIER_NAME"));
			purchasePayment.setSupplier(supplier);
			creditCardPayment.setParent(purchasePayment);
			
			creditCardPayment.setAmount(rs.getBigDecimal("AMOUNT"));
			
			CreditCard creditCard = new CreditCard();
			creditCard.setId(rs.getLong("CREDIT_CARD_ID"));
			creditCard.setUser(rs.getString("CREDIT_CARD_USER"));
			creditCard.setBank(rs.getString("CREDIT_CARD_BANK"));
			creditCard.setCustomerNumber(rs.getString("CUSTOMER_NUMBER"));
			creditCardPayment.setCreditCard(creditCard);
			
			creditCardPayment.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			creditCardPayment.setApprovalCode(rs.getString("APPROVAL_CODE"));
			return creditCardPayment;
		}
		
	}
	
	private static final String DELETE_SQL = "delete from PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT where ID = ?";
	
	@Override
	public void delete(PurchasePaymentCreditCardPayment cashPayment) {
		getJdbcTemplate().update(DELETE_SQL, cashPayment.getId());
	}

	private static final String NOT_INCLUDED_IN_STATEMENT_WHERE_CLAUSE =
			" and not exists ("
			+ "   select *"
			+ "   from CREDIT_CARD_STATEMENT_ITEM ccsi"
			+ "   where ccsi.PURCHASE_PAYMENT_CREDIT_CARD_PAYMENT_ID = a.ID"
			+ " )"
			+ " and a.TRANSACTION_DT >= ?";
	
	@Override
	public List<PurchasePaymentCreditCardPayment> search(
			PurchasePaymentCreditCardPaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getSupplier() != null) {
			sql.append(" and d.ID = ?");
			params.add(criteria.getSupplier().getId());
		}
		
		if (criteria.getCreditCard() != null) {
			sql.append(" and c.ID = ?");
			params.add(criteria.getCreditCard().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and b.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getFromDate() != null) {
			sql.append(" and a.TRANSACTION_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and a.TRANSACTION_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
		if (criteria.getNotIncludedInStatement() != null) {
			if (criteria.getNotIncludedInStatement()) {
				sql.append(NOT_INCLUDED_IN_STATEMENT_WHERE_CLAUSE);
				params.add("2015-03-01");
			}
		}
		
		if (!StringUtils.isEmpty(criteria.getCustomerNumber())) {
			sql.append(" and c.CUSTOMER_NUMBER = ?");
			params.add(criteria.getCustomerNumber());
		}
		
		if (!StringUtils.isEmpty(criteria.getOrderBy())) {
			sql.append(" order by ").append(criteria.getOrderBy());
		} else {
			sql.append(" order by a.TRANSACTION_DT, d.NAME, b.PURCHASE_PAYMENT_NO");
		}
		
		return getJdbcTemplate().query(sql.toString(), creditCardPaymentRowMapper, params.toArray());
	}

}