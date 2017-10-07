package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CreditCardStatementDao;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.CreditCardStatement;
import com.pj.magic.util.DbUtil;

@Repository
public class CreditCardStatementDaoImpl extends MagicDao implements CreditCardStatementDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.CUSTOMER_NUMBER, CREDIT_CARD_ID, STATEMENT_DT, POST_IND,"
			+ " b.USER, b.BANK"
			+ " from CREDIT_CARD_STATEMENT a"
			+ " left join CREDIT_CARD b"
			+ "   on b.ID = a.CREDIT_CARD_ID";
	
	private CreditCardStatementRowMapper statementRowMapper = new CreditCardStatementRowMapper();
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.STATEMENT_DT desc";
	
	@Override
	public List<CreditCardStatement> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, statementRowMapper);
	}
	
	private class CreditCardStatementRowMapper implements RowMapper<CreditCardStatement> {

		@Override
		public CreditCardStatement mapRow(ResultSet rs, int rowNum) throws SQLException {
			CreditCardStatement statement = new CreditCardStatement();
			statement.setId(rs.getLong("ID"));
			statement.setCustomerNumber(rs.getString("CUSTOMER_NUMBER"));
			if (rs.getLong("CREDIT_CARD_ID") != 0) {
				statement.setCreditCard(mapCreditCard(rs));
			}
			statement.setStatementDate(rs.getDate("STATEMENT_DT"));
			statement.setPosted("Y".equals(rs.getString("POST_IND")));
			return statement;
		}

		private CreditCard mapCreditCard(ResultSet rs) throws SQLException {
			CreditCard creditCard = new CreditCard();
			creditCard.setId(rs.getLong("CREDIT_CARD_ID"));
			creditCard.setBank(rs.getString("BANK"));
			creditCard.setUser(rs.getString("USER"));
			return creditCard;
		}
	}
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";

	@Override
	public CreditCardStatement get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, statementRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(CreditCardStatement statement) {
		if (statement.isNew()) {
			insert(statement);
		} else {
			update(statement);
		}
	}

	private static final String INSERT_SQL =
			"insert into CREDIT_CARD_STATEMENT (CUSTOMER_NUMBER, STATEMENT_DT) values (?, ?)";
	
	private void insert(final CreditCardStatement statement) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, statement.getCustomerNumber());
				ps.setDate(2, DbUtil.toSqlDate(statement.getStatementDate()));
				return ps;
			}
		}, holder);
		
		statement.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update CREDIT_CARD_STATEMENT"
			+ " set POST_IND = ? where ID = ?";
	
	private void update(CreditCardStatement statement) {
		getJdbcTemplate().update(UPDATE_SQL,
				statement.isPosted() ? "Y" : "N",
				statement.getId());
	}

	private static final String FIND_ALL_BY_CREDIT_CARD_SQL = 
			BASE_SELECT_SQL + " where a.CREDIT_CARD_ID = ? order by a.STATEMENT_DT desc";
	
	@Override
	public List<CreditCardStatement> findAllByCreditCard(CreditCard creditCard) {
		return getJdbcTemplate().query(FIND_ALL_BY_CREDIT_CARD_SQL, statementRowMapper, creditCard.getId());
	}

	private static final String FIND_BY_CREDIT_CARD_AND_STATEMENT_DATE_SQL = 
			BASE_SELECT_SQL + " where a.CREDIT_CARD_ID = ? and a.STATEMENT_DT = ?";
	
	@Override
	public CreditCardStatement findByCreditCardAndStatementDate(CreditCard creditCard, Date statementDate) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_CREDIT_CARD_AND_STATEMENT_DATE_SQL, 
					statementRowMapper, creditCard.getId(), DbUtil.toMySqlDateString(statementDate));
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String DELETE_SQL = "delete from CREDIT_CARD_STATEMENT where ID = ?";
	
	@Override
	public void delete(CreditCardStatement statement) {
		getJdbcTemplate().update(DELETE_SQL, statement.getId());
	}

}
