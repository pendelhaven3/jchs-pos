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

import com.pj.magic.dao.PurchasePaymentCashPaymentDao;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCashPayment;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PurchasePaymentCashPaymentSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class PurchasePaymentCashPaymentDaoImpl extends MagicDao implements PurchasePaymentCashPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PURCHASE_PAYMENT_ID, a.AMOUNT, PAID_DT, PAID_BY,"
			+ " b.USERNAME as PAID_BY_USERNAME"
			+ " from PURCHASE_PAYMENT_CASH_PAYMENT a"
			+ " join USER b"
			+ "   on b.ID = a.PAID_BY"
			+ " join PURCHASE_PAYMENT c"
			+ "   on c.ID = a.PURCHASE_PAYMENT_ID";
	
	private PurchasePaymentCashPaymentRowMapper cashPaymentRowMapper = 
			new PurchasePaymentCashPaymentRowMapper();
	
	@Override
	public void save(PurchasePaymentCashPayment cashPayment) {
		if (cashPayment.getId() == null) {
			insert(cashPayment);
		} else {
			update(cashPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PURCHASE_PAYMENT_CASH_PAYMENT"
			+ " (PURCHASE_PAYMENT_ID, AMOUNT, PAID_DT, PAID_BY) values (?, ?, ?, ?)";
	
	private void insert(final PurchasePaymentCashPayment cashPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, cashPayment.getParent().getId());
				ps.setBigDecimal(2, cashPayment.getAmount());
				ps.setDate(3, new Date(cashPayment.getPaidDate().getTime()));
				ps.setLong(4, cashPayment.getPaidBy().getId());
				return ps;
			}
		}, holder);
		
		cashPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update PURCHASE_PAYMENT_CASH_PAYMENT"
			+ " set AMOUNT = ?, PAID_DT = ?, PAID_BY = ?"
			+ " where ID = ?";
	
	private void update(PurchasePaymentCashPayment cashPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				cashPayment.getAmount(), 
				cashPayment.getPaidDate(),
				cashPayment.getPaidBy().getId(),
				cashPayment.getId());
	}

	private static final String FIND_ALL_BY_PURCHASE_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.PURCHASE_PAYMENT_ID = ?";
	
	@Override
	public List<PurchasePaymentCashPayment> findAllByPurchasePayment(PurchasePayment purchasePayment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_PAYMENT_SQL, cashPaymentRowMapper, 
				purchasePayment.getId());
	}

	private class PurchasePaymentCashPaymentRowMapper implements RowMapper<PurchasePaymentCashPayment> {

		@Override
		public PurchasePaymentCashPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchasePaymentCashPayment cashPayment = new PurchasePaymentCashPayment();
			cashPayment.setId(rs.getLong("ID"));
			cashPayment.setParent(new PurchasePayment(rs.getLong("PURCHASE_PAYMENT_ID")));
			cashPayment.setAmount(rs.getBigDecimal("AMOUNT"));
			cashPayment.setPaidDate(rs.getDate("PAID_DT"));
			cashPayment.setPaidBy(
					new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
			return cashPayment;
		}
		
	}
	
	private static final String DELETE_SQL = "delete from PURCHASE_PAYMENT_CASH_PAYMENT where ID = ?";
	
	@Override
	public void delete(PurchasePaymentCashPayment cashPayment) {
		getJdbcTemplate().update(DELETE_SQL, cashPayment.getId());
	}

	@Override
	public List<PurchasePaymentCashPayment> search(PurchasePaymentCashPaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getPosted() != null) {
			sql.append(" and c.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getFromDate() != null) {
			sql.append(" and a.PAID_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getFromDate()));
		}
		
		if (criteria.getToDate() != null) {
			sql.append(" and a.PAID_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getToDate()));
		}
		
		return getJdbcTemplate().query(sql.toString(), cashPaymentRowMapper, params.toArray());
	}

}