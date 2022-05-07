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

import com.pj.magic.dao.PaymentTermDao;
import com.pj.magic.model.PaymentTerm;

@Repository
public class PaymentTermDaoImpl extends MagicDao implements PaymentTermDao {

	private static final String BASE_SELECT_SQL =
			"select ID, NAME, NUMBER_OF_DAYS"
			+ " from PAYMENT_TERM"
			+ " where 1 = 1";
	
	private RowMapper<PaymentTerm> rowMapper = new RowMapper<PaymentTerm>() {

		@Override
		public PaymentTerm mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentTerm paymentTerm = new PaymentTerm();
			paymentTerm.setId(rs.getLong("ID"));
			paymentTerm.setName(rs.getString("NAME"));
			paymentTerm.setNumberOfDays(rs.getInt("NUMBER_OF_DAYS"));
			return paymentTerm;
		}
		
	};
	
	@Override
	public void save(PaymentTerm paymentTerm) {
		if (paymentTerm.getId() == null) {
			insert(paymentTerm);
		} else {
			update(paymentTerm);
		}
	}

	private static final String INSERT_SQL =
			"insert into PAYMENT_TERM"
			+ " (NAME, NUMBER_OF_DAYS)"
			+ " values"
			+ " (?, ?)";
	
	private void insert(final PaymentTerm paymentTerm) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, paymentTerm.getName());
				ps.setInt(2, paymentTerm.getNumberOfDays());
				return ps;
			}
		}, holder);

		paymentTerm.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update PAYMENT_TERM"
			+ " set NAME = ?, NUMBER_OF_DAYS = ?"
			+ " where ID = ?";
	
	private void update(PaymentTerm paymentTerm) {
		getJdbcTemplate().update(UPDATE_SQL,
				paymentTerm.getName(),
				paymentTerm.getNumberOfDays(),
				paymentTerm.getId());
	}
	
	private static final String GET_SQL = BASE_SELECT_SQL + " and ID = ?";
	
	@Override
	public PaymentTerm get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by NAME";
	
	@Override
	public List<PaymentTerm> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

}
