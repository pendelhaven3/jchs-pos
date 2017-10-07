package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PaymentTerminalAssignmentDao;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.PaymentTerminalAssignment;
import com.pj.magic.model.User;

@Repository
public class PaymentTerminalAssignmentDaoImpl extends MagicDao implements PaymentTerminalAssignmentDao {

	private static final String BASE_SELECT_SQL =
			"   select PAYMENT_TERMINAL_ID, b.NAME as PAYMENT_TERMINAL_NAME, USER_ID, c.USERNAME"
			+ " from PAYMENT_TERMINAL_USER a"
			+ " join PAYMENT_TERMINAL b"
			+ "   on b.ID = a.PAYMENT_TERMINAL_ID"
			+ " join USER c"
			+ "   on c.ID = a.USER_ID";
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by c.USERNAME";
	
	private PaymentTerminalAssignmentRowMapper rowMapper =
			new PaymentTerminalAssignmentRowMapper();
	
	@Override
	public List<PaymentTerminalAssignment> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

	private class PaymentTerminalAssignmentRowMapper implements RowMapper<PaymentTerminalAssignment> {

		@Override
		public PaymentTerminalAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentTerminalAssignment pta = new PaymentTerminalAssignment();
			pta.setPaymentTerminal(new PaymentTerminal(
					rs.getLong("PAYMENT_TERMINAL_ID"), rs.getString("PAYMENT_TERMINAL_NAME")));
			pta.setUser(new User(rs.getLong("USER_ID"), rs.getString("USERNAME")));
			return pta;
		}
		
	}

	@Override
	public void save(PaymentTerminalAssignment paymentTerminalAssignment) {
		PaymentTerminalAssignment existing = findByUser(paymentTerminalAssignment.getUser());
		if (existing != null) {
			update(paymentTerminalAssignment);
		} else {
			insert(paymentTerminalAssignment);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into PAYMENT_TERMINAL_USER (USER_ID, PAYMENT_TERMINAL_ID) values (?, ?)";
	
	private void insert(PaymentTerminalAssignment paymentTerminalAssignment) {
		getJdbcTemplate().update(INSERT_SQL, 
				paymentTerminalAssignment.getUser().getId(),
				paymentTerminalAssignment.getPaymentTerminal().getId());
	}

	private static final String UPDATE_SQL =
			"update PAYMENT_TERMINAL_USER set PAYMENT_TERMINAL_ID = ? where USER_ID = ?";
	
	private void update(PaymentTerminalAssignment paymentTerminalAssignment) {
		getJdbcTemplate().update(UPDATE_SQL, 
				paymentTerminalAssignment.getPaymentTerminal().getId(),
				paymentTerminalAssignment.getUser().getId());
	}

	private static final String FIND_BY_USER_SQL = BASE_SELECT_SQL + " where a.USER_ID = ?";
	
	@Override
	public PaymentTerminalAssignment findByUser(User user) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_USER_SQL, rowMapper, user.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String DELETE_SQL = "delete from PAYMENT_TERMINAL_USER where USER_ID = ?";
	
	@Override
	public void delete(PaymentTerminalAssignment paymentTerminalAssignment) {
		getJdbcTemplate().update(DELETE_SQL, paymentTerminalAssignment.getUser().getId());
	}
	
}