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

import com.pj.magic.dao.UserDao;
import com.pj.magic.model.User;

@Repository
public class UserDaoImpl extends MagicDao implements UserDao {

	private static final String BASE_SELECT_SQL = 
			"select ID, USERNAME, PASSWORD, SUPERVISOR_IND, MODIFY_PRICING_IND"
			+ " from USER";
	
	private UserRowMapper userRowMapper = new UserRowMapper();
	
	@Override
	public void save(User user) {
		if (user.getId() == null) {
			insert(user);
		} else {
			update(user);
		}
	}

	private static final String INSERT_SQL = "insert into USER (USERNAME, PASSWORD, SUPERVISOR_IND,"
			+ " MODIFY_PRICING_IND)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(final User user) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, user.getUsername());
				ps.setString(2, user.getPassword());
				ps.setString(3, user.isSupervisor() ? "Y" : "N");
				ps.setString(4, user.isModifyPricing() ? "Y" : "N");
				return ps;
			}
		}, holder);
		
		user.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update USER set USERNAME = ?, SUPERVISOR_IND = ?,"
			+ " MODIFY_PRICING_IND = ? where ID = ?";
	
	private void update(User user) {
		getJdbcTemplate().update(UPDATE_SQL, 
				user.getUsername(),
				user.isSupervisor() ? "Y" : "N",
				user.isModifyPricing() ? "Y" : "N",
				user.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where ID = ?";
	
	@Override
	public User get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, userRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private class UserRowMapper implements RowMapper<User> {

		@Override
		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
			User user = new User();
			user.setId(rs.getLong("ID"));
			user.setUsername(rs.getString("USERNAME"));
			user.setPassword(rs.getString("PASSWORD"));
			user.setSupervisor("Y".equals(rs.getString("SUPERVISOR_IND")));
			user.setModifyPricing("Y".equals(rs.getString("MODIFY_PRICING_IND")));
			return user;
		}
	}

	private static final String FIND_BY_USERNAME_SQL = BASE_SELECT_SQL + " where USERNAME = ?";
	
	@Override
	public User findByUsername(String username) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_USERNAME_SQL, userRowMapper, username);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by USERNAME";
	
	@Override
	public List<User> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, userRowMapper);
	}

	private static final String UPDATE_PASSWORD_SQL = "update USER set PASSWORD = ? where ID = ?";
	
	@Override
	public void updatePassword(User user, String password) {
		getJdbcTemplate().update(UPDATE_PASSWORD_SQL, password, user.getId());
	}

}
