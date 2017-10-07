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

import com.pj.magic.dao.CreditCardStatementPaymentDao;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.model.CreditCardStatementPayment;
import com.pj.magic.util.DbUtil;

@Repository
public class CreditCardStatementPaymentDaoImpl extends MagicDao implements CreditCardStatementPaymentDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, CREDIT_CARD_STATEMENT_ID, PAYMENT_DT, AMOUNT, PAYMENT_TYPE, REMARKS"
			+ " from CREDIT_CARD_STATEMENT_PAYMENT a";
	
	private CreditCardStatementPaymentRowMapper rowMapper =
			new CreditCardStatementPaymentRowMapper();
	
	private static final String FIND_ALL_BY_CREDIT_CARD_STATEMENT_SQL = BASE_SELECT_SQL 
			+ " where CREDIT_CARD_STATEMENT_ID = ? order by PAYMENT_DT desc";
	
	@Override
	public List<CreditCardStatementPayment> findAllByCreditCardStatement(CreditCardStatement statement) {
		List<CreditCardStatementPayment> items = getJdbcTemplate().query(FIND_ALL_BY_CREDIT_CARD_STATEMENT_SQL, 
				rowMapper, statement.getId());
		for (CreditCardStatementPayment item : items) {
			item.setParent(statement);
		}
		return items;
	}

	private class CreditCardStatementPaymentRowMapper implements RowMapper<CreditCardStatementPayment> {

		@Override
		public CreditCardStatementPayment mapRow(ResultSet rs, int rowNum) throws SQLException {
			CreditCardStatementPayment payment = new CreditCardStatementPayment();
			payment.setId(rs.getLong("ID"));
			payment.setParent(new CreditCardStatement(rs.getLong("CREDIT_CARD_STATEMENT_ID")));
			payment.setPaymentDate(rs.getDate("PAYMENT_DT"));
			payment.setAmount(rs.getBigDecimal("AMOUNT"));
			payment.setPaymentType(rs.getString("PAYMENT_TYPE"));
			payment.setRemarks(rs.getString("REMARKS"));
			return payment;
		}

	}

	@Override
	public void save(CreditCardStatementPayment item) {
		if (item.isNew()) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into CREDIT_CARD_STATEMENT_PAYMENT"
			+ " (CREDIT_CARD_STATEMENT_ID, PAYMENT_DT, AMOUNT, PAYMENT_TYPE, REMARKS)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final CreditCardStatementPayment payment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, payment.getParent().getId());
				ps.setDate(2, DbUtil.toSqlDate(payment.getPaymentDate()));
				ps.setBigDecimal(3, payment.getAmount());
				ps.setString(4, payment.getPaymentType());
				ps.setString(5, payment.getRemarks());
				return ps;
			}
		}, holder);
		
		payment.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = 
			"update CREDIT_CARD_STATEMENT_PAYMENT"
			+ " set PAYMENT_DT = ?, AMOUNT = ?, PAYMENT_TYPE = ?, REMARKS = ? where ID = ?";
	
	private void update(CreditCardStatementPayment payment) {
		getJdbcTemplate().update(UPDATE_SQL,
				payment.getPaymentDate(),
				payment.getAmount(),
				payment.getPaymentType(),
				payment.getRemarks(),
				payment.getId());
	}

	private static final String DELETE_SQL = "delete from CREDIT_CARD_STATEMENT_PAYMENT where ID = ?";
	
	@Override
	public void delete(CreditCardStatementPayment item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_STATEMENT_SQL = 
			"delete from CREDIT_CARD_STATEMENT_PAYMENT where CREDIT_CARD_STATEMENT_ID = ?";
	
	@Override
	public void deleteAllByCreditCardStatement(CreditCardStatement statement) {
		getJdbcTemplate().update(DELETE_ALL_BY_STATEMENT_SQL, statement.getId());
	}

}
