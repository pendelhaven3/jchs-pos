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

import com.pj.magic.dao.PaymentAdjustmentDao;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.User;
import com.pj.magic.model.search.PaymentAdjustmentSearchCriteria;
import com.pj.magic.model.util.TimePeriod;
import com.pj.magic.util.DbUtil;

@Repository
public class PaymentAdjustmentDaoImpl extends MagicDao implements PaymentAdjustmentDao {

	private static final String PAYMENT_ADJUSTMENT_NUMBER_SEQUENCE = "PAYMENT_ADJUSTMENT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, PAYMENT_ADJUSTMENT_NO, CUSTOMER_ID, ADJUSTMENT_TYPE_ID, AMOUNT, a.PAYMENT_NO,"
			+ " POST_IND, POST_DT, POST_BY,"
			+ " PAID_IND, PAID_DT, PAID_BY, PAYMENT_TERMINAL_ID, a.REMARKS,"
			+ " b.CODE as CUSTOMER_CODE, b.NAME as CUSTOMER_NAME,"
			+ " c.USERNAME as POST_BY_USERNAME,"
			+ " d.USERNAME as PAID_BY_USERNAME,"
			+ " e.NAME as PAYMENT_TERMINAL_NAME,"
			+ " f.CODE as ADJUSTMENT_TYPE_CODE"
			+ " from PAYMENT_ADJUSTMENT a"
			+ " join CUSTOMER b"
			+ "   on b.ID = a.CUSTOMER_ID"
			+ " left join USER c"
			+ "   on c.ID = a.POST_BY"
			+ " left join USER d"
			+ "   on d.ID = a.PAID_BY"
			+ " left join PAYMENT_TERMINAL e"
			+ "   on e.ID = a.PAYMENT_TERMINAL_ID"
			+ " join ADJUSTMENT_TYPE f"
			+ "   on f.ID = a.ADJUSTMENT_TYPE_ID";

	private PaymentAdjustmentRowMapper paymentAdjustmentRowMapper = new PaymentAdjustmentRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public PaymentAdjustment get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, paymentAdjustmentRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(PaymentAdjustment adjustment) {
		if (adjustment.getId() == null) {
			insert(adjustment);
		} else {
			update(adjustment);
		}
	}

	private static final String UPDATE_SQL = 
			"update PAYMENT_ADJUSTMENT set CUSTOMER_ID = ?, ADJUSTMENT_TYPE_ID = ?, AMOUNT = ?,"
			+ " POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " PAID_IND = ?, PAID_DT = ?, PAID_BY = ?, PAYMENT_TERMINAL_ID = ?, REMARKS = ?,"
			+ " PAYMENT_NO = ? where ID = ?";
	
	private void update(PaymentAdjustment paymentAdjustment) {
		getJdbcTemplate().update(UPDATE_SQL,
				paymentAdjustment.getCustomer().getId(),
				paymentAdjustment.getAdjustmentType().getId(),
				paymentAdjustment.getAmount(),
				paymentAdjustment.isPosted() ? "Y" : "N",
				paymentAdjustment.getPostDate(),
				paymentAdjustment.isPosted() ? paymentAdjustment.getPostedBy().getId() : null,
				paymentAdjustment.isPaid() ? "Y" : "N",
				paymentAdjustment.isPaid() ? paymentAdjustment.getPaidDate() : null,
				paymentAdjustment.isPaid() ? paymentAdjustment.getPaidBy().getId() : null,
				paymentAdjustment.isPaid() ? paymentAdjustment.getPaymentTerminal().getId() : null,
				paymentAdjustment.getRemarks(),
				paymentAdjustment.getPaymentNumber(),
				paymentAdjustment.getId());
	}

	private static final String INSERT_SQL =
			"insert into PAYMENT_ADJUSTMENT"
			+ " (PAYMENT_ADJUSTMENT_NO, CUSTOMER_ID, ADJUSTMENT_TYPE_ID, AMOUNT, REMARKS)"
			+ " values"
			+ " (?, ?, ?, ?, ?)";
	
	private void insert(final PaymentAdjustment paymentAdjustment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextPaymentAdjustmentNumber());
				ps.setLong(2, paymentAdjustment.getCustomer().getId());
				ps.setLong(3, paymentAdjustment.getAdjustmentType().getId());
				ps.setBigDecimal(4, paymentAdjustment.getAmount());
				ps.setString(5, paymentAdjustment.getRemarks());
				return ps;
			}
		}, holder);
		
		PaymentAdjustment updated = get(holder.getKey().longValue());
		paymentAdjustment.setId(updated.getId());
		paymentAdjustment.setPaymentAdjustmentNumber(updated.getPaymentAdjustmentNumber());
	}
	
	private Long getNextPaymentAdjustmentNumber() {
		return getNextSequenceValue(PAYMENT_ADJUSTMENT_NUMBER_SEQUENCE);
	}

	private class PaymentAdjustmentRowMapper implements RowMapper<PaymentAdjustment> {

		@Override
		public PaymentAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentAdjustment paymentAdjustment = new PaymentAdjustment();
			paymentAdjustment.setId(rs.getLong("ID"));
			paymentAdjustment.setPaymentAdjustmentNumber(rs.getLong("PAYMENT_ADJUSTMENT_NO"));
			paymentAdjustment.setAmount(rs.getBigDecimal("AMOUNT"));
			
			Customer customer = new Customer();
			customer.setId(rs.getLong("CUSTOMER_ID"));
			customer.setCode(rs.getString("CUSTOMER_CODE"));
			customer.setName(rs.getString("CUSTOMER_NAME"));
			paymentAdjustment.setCustomer(customer);

			AdjustmentType adjustmentType = new AdjustmentType();
			adjustmentType.setId(rs.getLong("ADJUSTMENT_TYPE_ID"));
			adjustmentType.setCode(rs.getString("ADJUSTMENT_TYPE_CODE"));
			paymentAdjustment.setAdjustmentType(adjustmentType);
			
			paymentAdjustment.setPosted("Y".equals(rs.getString("POST_IND")));
			if (paymentAdjustment.isPosted()) {
				paymentAdjustment.setPostDate(rs.getDate("POST_DT"));
				paymentAdjustment.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			paymentAdjustment.setPaid("Y".equals(rs.getString("PAID_IND")));
			if (paymentAdjustment.isPaid()) {
				paymentAdjustment.setPaidDate(rs.getTimestamp("PAID_DT"));
				paymentAdjustment.setPaidBy(new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
				paymentAdjustment.setPaymentTerminal(new PaymentTerminal(
						rs.getLong("PAYMENT_TERMINAL_ID"), rs.getString("PAYMENT_TERMINAL_NAME")));
			}
			
			paymentAdjustment.setRemarks(rs.getString("REMARKS"));
			
			if (rs.getLong("PAYMENT_NO") != 0) {
				paymentAdjustment.setPaymentNumber(rs.getLong("PAYMENT_NO"));
			}
			
			return paymentAdjustment;
		}
		
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL
			+ " order by PAYMENT_ADJUSTMENT_NO desc";
	
	@Override
	public List<PaymentAdjustment> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, paymentAdjustmentRowMapper);
	}

	private static final String FIND_BY_PAYMENT_ADJUSTMENT_NUMBER_SQL = BASE_SELECT_SQL
			+ " where a.PAYMENT_ADJUSTMENT_NO = ?";
	
	@Override
	public PaymentAdjustment findByPaymentAdjustmentNumber(long paymentAdjustmentNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_PAYMENT_ADJUSTMENT_NUMBER_SQL, 
					paymentAdjustmentRowMapper, paymentAdjustmentNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<PaymentAdjustment> search(PaymentAdjustmentSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getPaymentAdjustmentNumber() != null) {
			sql.append(" and a.PAYMENT_ADJUSTMENT_NO = ?");
			params.add(criteria.getPaymentAdjustmentNumber());
		}
		
		if (criteria.getCustomer() != null) {
			sql.append(" and a.CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getAdjustmentType() != null) {
			sql.append(" and a.ADJUSTMENT_TYPE_ID = ?");
			params.add(criteria.getAdjustmentType().getId());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
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
			sql.append(" and a.POST_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDateTo()));
		}
		
		if (criteria.getPaid() != null) {
			sql.append(" and a.PAID_IND = ?");
			params.add(criteria.getPaid() ? "Y" : "N");
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
		
		sql.append(" order by PAYMENT_ADJUSTMENT_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), paymentAdjustmentRowMapper, params.toArray());
	}

}