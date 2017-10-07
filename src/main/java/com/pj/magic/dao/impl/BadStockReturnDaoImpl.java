package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockReturnDao;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.User;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;
import com.pj.magic.model.util.TimePeriod;
import com.pj.magic.util.DbUtil;

@Repository
public class BadStockReturnDaoImpl extends MagicDao implements BadStockReturnDao {

	private static final String BAD_STOCK_RETURN_NUMBER_SEQUENCE = "BAD_STOCK_RETURN_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_STOCK_RETURN_NO, CUSTOMER_ID, POST_IND, POST_DT, POST_BY,"
			+ " PAID_IND, PAID_DT, PAID_BY, PAYMENT_TERMINAL_ID, a.REMARKS, CANCEL_IND, CANCEL_DT, CANCEL_BY,"
			+ " a.PAYMENT_NO,"
			+ " b.CODE as CUSTOMER_CODE, b.NAME as CUSTOMER_NAME,"
			+ " c.USERNAME as POST_BY_USERNAME,"
			+ " d.USERNAME as PAID_BY_USERNAME,"
			+ " e.NAME as PAYMENT_TERMINAL_NAME,"
			+ " f.USERNAME as CANCEL_BY_USERNAME"
			+ " from BAD_STOCK_RETURN a"
			+ " join CUSTOMER b"
			+ "   on b.ID = a.CUSTOMER_ID"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY"
			+ " left join USER d"
			+ "   on d.ID = a.PAID_BY"
			+ " left join PAYMENT_TERMINAL e"
			+ "   on e.ID = a.PAYMENT_TERMINAL_ID"
			+ " left join USER f"
			+ "   on f.ID = a.CANCEL_BY";

	private BadStockReturnRowMapper badStockReturnRowMapper = new BadStockReturnRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public BadStockReturn get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, badStockReturnRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(BadStockReturn badStockReturn) {
		if (badStockReturn.getId() == null) {
			insert(badStockReturn);
		} else {
			update(badStockReturn);
		}
	}

	private static final String UPDATE_SQL = 
			"update BAD_STOCK_RETURN set CUSTOMER_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " PAID_IND = ?, PAID_DT = ?, PAID_BY = ?, PAYMENT_TERMINAL_ID = ?,"
			+ " CANCEL_IND = ?, CANCEL_DT = ?, CANCEL_BY = ?, REMARKS = ?, PAYMENT_NO = ? where ID = ?";
	
	private void update(BadStockReturn badStockReturn) {
		getJdbcTemplate().update(UPDATE_SQL,
				badStockReturn.getCustomer().getId(),
				badStockReturn.isPosted() ? "Y" : "N",
				badStockReturn.getPostDate(),
				badStockReturn.isPosted() ? badStockReturn.getPostedBy().getId() : null,
				badStockReturn.isPaid() ? "Y" : "N",
				badStockReturn.isPaid() ? badStockReturn.getPaidDate() : null,
				badStockReturn.isPaid() ? badStockReturn.getPaidBy().getId() : null,
				badStockReturn.isPaid() ? badStockReturn.getPaymentTerminal().getId() : null,
				badStockReturn.isCancelled() ? "Y" : "N",
				badStockReturn.isCancelled() ? badStockReturn.getCancelDate() : null,
				badStockReturn.isCancelled() ? badStockReturn.getCancelledBy().getId() : null,
				badStockReturn.getRemarks(),
				badStockReturn.getPaymentNumber(),
				badStockReturn.getId());
	}

	private static final String INSERT_SQL =
			"insert into BAD_STOCK_RETURN (BAD_STOCK_RETURN_NO, CUSTOMER_ID) values (?, ?)";
	
	private void insert(final BadStockReturn badStockReturn) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextBadStockReturnNumber());
				ps.setLong(2, badStockReturn.getCustomer().getId());
				return ps;
			}
		}, holder);
		
		BadStockReturn updated = get(holder.getKey().longValue());
		badStockReturn.setId(updated.getId());
		badStockReturn.setBadStockReturnNumber(updated.getBadStockReturnNumber());
	}
	
	private Long getNextBadStockReturnNumber() {
		return getNextSequenceValue(BAD_STOCK_RETURN_NUMBER_SEQUENCE);
	}

	private class BadStockReturnRowMapper implements RowMapper<BadStockReturn> {

		@Override
		public BadStockReturn mapRow(ResultSet rs, int rowNum) throws SQLException {
			BadStockReturn badStockReturn = new BadStockReturn();
			badStockReturn.setId(rs.getLong("ID"));
			badStockReturn.setBadStockReturnNumber(rs.getLong("BAD_STOCK_RETURN_NO"));
			
			Customer customer = new Customer();
			customer.setId(rs.getLong("CUSTOMER_ID"));
			customer.setCode(rs.getString("CUSTOMER_CODE"));
			customer.setName(rs.getString("CUSTOMER_NAME"));
			badStockReturn.setCustomer(customer);
			
			badStockReturn.setPosted("Y".equals(rs.getString("POST_IND")));
			if (badStockReturn.isPosted()) {
				badStockReturn.setPostDate(rs.getTimestamp("POST_DT"));
				badStockReturn.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			badStockReturn.setPaid("Y".equals(rs.getString("PAID_IND")));
			if (badStockReturn.isPaid()) {
				badStockReturn.setPaidDate(rs.getTimestamp("PAID_DT"));
				badStockReturn.setPaidBy(new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
				badStockReturn.setPaymentTerminal(new PaymentTerminal(
						rs.getLong("PAYMENT_TERMINAL_ID"), rs.getString("PAYMENT_TERMINAL_NAME")));
			}
			
			badStockReturn.setCancelled("Y".equals(rs.getString("CANCEL_IND")));
			if (badStockReturn.isCancelled()) {
				badStockReturn.setCancelDate(rs.getTimestamp("CANCEL_DT"));
				badStockReturn.setCancelledBy(new User(rs.getLong("CANCEL_BY"), rs.getString("CANCEL_BY_USERNAME")));
			}
			
			badStockReturn.setRemarks(rs.getString("REMARKS"));
			if (rs.getLong("PAYMENT_NO") != 0) {
				badStockReturn.setPaymentNumber(rs.getLong("PAYMENT_NO"));
			}
			
			return badStockReturn;
		}
		
	}

	@Override
	public List<BadStockReturn> search(BadStockReturnSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getBadStockReturnNumber() != null) {
			sql.append(" and a.BAD_STOCK_RETURN_NO = ?");
			params.add(criteria.getBadStockReturnNumber());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}

		if (criteria.getPaid() != null) {
			sql.append(" and a.PAID_IND = ?");
			params.add(criteria.getPaid() ? "Y" : "N");
		}
		
		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
		}
		
		if (criteria.getPostDateFrom() != null) {
			sql.append(" and a.POST_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateFrom()));
		}
		
		if (criteria.getPostDateTo() != null) {
			sql.append(" and a.POST_DT < date_add(?, interval 1 day)");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateTo()));
		}
		
		if (criteria.getCustomer() != null) {
			sql.append(" and a.CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getPaidDate() != null) {
			if (criteria.getTimePeriod() != null) {
				if (criteria.getTimePeriod() == TimePeriod.MORNING_ONLY) {
					sql.append(" and PAID_DT >= ? and PAID_DT < date_add(?, interval 13 hour)");
					params.add(DbUtil.toMySqlDateString(criteria.getPaidDate()));
					params.add(DbUtil.toMySqlDateString(criteria.getPaidDate()));
				} else if (criteria.getTimePeriod() == TimePeriod.AFTERNOON_ONLY) {
					sql.append(" and PAID_DT >= date_add(?, interval 13 hour)"
							+ " and PAID_DT < date_add(?, interval 1 day)");
					params.add(DbUtil.toMySqlDateString(criteria.getPaidDate()));
					params.add(DbUtil.toMySqlDateString(criteria.getPaidDate()));
				}
			} else {
				sql.append(" and PAID_DT >= ? and PAID_DT < date_add(?, interval 1 day)");
				params.add(DbUtil.toMySqlDateString(criteria.getPaidDate()));
				params.add(DbUtil.toMySqlDateString(criteria.getPaidDate()));
			}
		}
		
		if (criteria.getPaymentTerminal() != null) {
			sql.append(" and PAYMENT_TERMINAL_ID = ?");
			params.add(criteria.getPaymentTerminal().getId());
		}
		
		if (criteria.getCancelled() != null) {
			sql.append(" and a.CANCEL_IND = ?");
			params.add(criteria.getCancelled() ? "Y" : "N");
		}
		
		sql.append(" order by BAD_STOCK_RETURN_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), badStockReturnRowMapper, params.toArray());
	}

	private static final String FIND_BY_BAD_STOCK_RETURN_NUMBER_SQL = BASE_SELECT_SQL +
			" where a.BAD_STOCK_RETURN_NO = ?";
	
	@Override
	public BadStockReturn findByBadStockReturnNumber(long badStockReturnNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_BAD_STOCK_RETURN_NUMBER_SQL,
					badStockReturnRowMapper, badStockReturnNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}