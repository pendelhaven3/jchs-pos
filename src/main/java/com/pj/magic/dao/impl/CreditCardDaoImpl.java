package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.CreditCardDao;
import com.pj.magic.model.CreditCard;

@Repository
public class CreditCardDaoImpl extends MagicDao implements CreditCardDao {

	private static final String BASE_SELECT_SQL =
			"select ID, USER, BANK, CARD_NUMBER, CUSTOMER_NUMBER"
			+ " from CREDIT_CARD a"
			+ " where 1 = 1";
	
	private RowMapper<CreditCard> rowMapper = new RowMapper<CreditCard>() {

		@Override
		public CreditCard mapRow(ResultSet rs, int rowNum) throws SQLException {
			CreditCard creditCard = new CreditCard();
			creditCard.setId(rs.getLong("ID"));
			creditCard.setUser(rs.getString("USER"));
			creditCard.setBank(rs.getString("BANK"));
			creditCard.setCardNumber(rs.getString("CARD_NUMBER"));
			creditCard.setCustomerNumber(rs.getString("CUSTOMER_NUMBER"));
			return creditCard;
		}
		
	};
	
	@Override
	public void save(CreditCard creditCard) {
		if (creditCard.getId() == null) {
			insert(creditCard);
		} else {
			update(creditCard);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into CREDIT_CARD"
			+ " (USER, BANK, CARD_NUMBER, CUSTOMER_NUMBER)"
			+ " values"
			+ " (?, ?, ?, ?)";
	
	private void insert(final CreditCard creditCard) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, creditCard.getUser());
				ps.setString(2, creditCard.getBank());
				ps.setString(3, creditCard.getCardNumber());
				ps.setString(4, creditCard.getCustomerNumber());
				return ps;
			}
		}, holder);

		creditCard.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update CREDIT_CARD"
			+ " set USER = ?, BANK = ?, CARD_NUMBER = ?, CUSTOMER_NUMBER = ?"
			+ " where ID = ?";
	
	private void update(CreditCard creditCard) {
		getJdbcTemplate().update(UPDATE_SQL,
				creditCard.getUser(),
				creditCard.getBank(),
				creditCard.getCardNumber(),
				creditCard.getCustomerNumber(),
				creditCard.getId());
	}
	
	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public CreditCard get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.USER, a.BANK, a.CARD_NUMBER";
	
	@Override
	public List<CreditCard> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

}