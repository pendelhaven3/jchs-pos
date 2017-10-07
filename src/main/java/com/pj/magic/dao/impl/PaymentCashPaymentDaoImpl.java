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

import com.pj.magic.dao.PaymentCashPaymentDao;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PaymentCashPaymentSearchCriteria;
import com.pj.magic.model.util.TimePeriod;
import com.pj.magic.util.DbUtil;

@Repository
public class PaymentCashPaymentDaoImpl extends MagicDao implements PaymentCashPaymentDao {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, PAYMENT_ID, a.AMOUNT, RECEIVED_DT, RECEIVED_BY,"
			+ " b.USERNAME as RECEIVED_BY_USERNAME"
			+ " from PAYMENT_CASH_PAYMENT a"
			+ " join USER b"
			+ "   on b.ID = a.RECEIVED_BY"
			+ " join PAYMENT c"
			+ "   on c.ID = a.PAYMENT_ID";
	
	private PaymentCashPaymentRowMapper cashPaymentRowMapper = new PaymentCashPaymentRowMapper();
	
	@Override
	public void save(PaymentCashPayment cashPayment) {
		if (cashPayment.getId() == null) {
			insert(cashPayment);
		} else {
			update(cashPayment);
		}
	}

	private static final String INSERT_SQL = 
			"insert into PAYMENT_CASH_PAYMENT"
			+ " (PAYMENT_ID, AMOUNT, RECEIVED_DT, RECEIVED_BY) values (?, ?, ?, ?)";
	
	private void insert(final PaymentCashPayment cashPayment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, cashPayment.getParent().getId());
				ps.setBigDecimal(2, cashPayment.getAmount());
				ps.setDate(3, new Date(cashPayment.getReceivedDate().getTime()));
				ps.setLong(4, cashPayment.getReceivedBy().getId());
				return ps;
			}
		}, holder);
		
		cashPayment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update PAYMENT_CASH_PAYMENT"
			+ " set AMOUNT = ?, RECEIVED_DT = ?, RECEIVED_BY = ?"
			+ " where ID = ?";
	
	private void update(PaymentCashPayment cashPayment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				cashPayment.getAmount(), 
				cashPayment.getReceivedDate(),
				cashPayment.getReceivedBy().getId(),
				cashPayment.getId());
	}

	private static final String FIND_ALL_BY_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where a.PAYMENT_ID = ?";
	
	@Override
	public List<PaymentCashPayment> findAllByPayment(Payment payment) {
		return getJdbcTemplate().query(FIND_ALL_BY_PAYMENT_SQL, cashPaymentRowMapper, payment.getId());
	}

	private class PaymentCashPaymentRowMapper implements RowMapper<PaymentCashPayment> {

		@Override
		public PaymentCashPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentCashPayment cashPayment = new PaymentCashPayment();
			cashPayment.setId(rs.getLong("ID"));
			cashPayment.setParent(new Payment(rs.getLong("PAYMENT_ID")));
			cashPayment.setAmount(rs.getBigDecimal("AMOUNT"));
			cashPayment.setReceivedDate(rs.getDate("RECEIVED_DT"));
			cashPayment.setReceivedBy(
					new User(rs.getLong("RECEIVED_BY"), rs.getString("RECEIVED_BY_USERNAME")));
			return cashPayment;
		}
		
	}
	
	private static final String DELETE_ALL_BY_PAYMENT_SQL =
			"delete from PAYMENT_CASH_PAYMENT where PAYMENT_ID = ?";

	@Override
	public void deleteAllByPayment(Payment payment) {
		getJdbcTemplate().update(DELETE_ALL_BY_PAYMENT_SQL, payment.getId());
	}

	private static final String DELETE_SQL = "delete from PAYMENT_CASH_PAYMENT where ID = ?";
	
	@Override
	public void delete(PaymentCashPayment cashPayment) {
		getJdbcTemplate().update(DELETE_SQL, cashPayment.getId());
	}

	@Override
	public List<PaymentCashPayment> search(PaymentCashPaymentSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getPaid() != null) {
			sql.append(" and c.POST_IND = ?");
			params.add(criteria.getPaid() ? "Y" : "N");
		}
		
		if (criteria.getPaymentDate() != null) {
			if (criteria.getTimePeriod() != null) {
				if (criteria.getTimePeriod() == TimePeriod.MORNING_ONLY) {
					sql.append(" and c.POST_DT >= ? and c.POST_DT < date_add(?, interval 13 hour)");
					params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
					params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
				} else if (criteria.getTimePeriod() == TimePeriod.AFTERNOON_ONLY) {
					sql.append(" and c.POST_DT >= date_add(?, interval 13 hour)"
							+ " and c.POST_DT < date_add(?, interval 1 day)");
					params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
					params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
				}
			} else {
				sql.append(" and c.POST_DT >= ? and c.POST_DT < date_add(?, interval 1 day)");
				params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
				params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
			}
		}
		
		if (criteria.getPaymentTerminal() != null) {
			sql.append(" and c.PAYMENT_TERMINAL_ID = ?");
			params.add(criteria.getPaymentTerminal().getId());
		}
		
		sql.append(" order by c.POST_DT");
		
		return getJdbcTemplate().query(sql.toString(), cashPaymentRowMapper, params.toArray());
	}
	
}