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

import com.pj.magic.dao.NoMoreStockAdjustmentDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;
import com.pj.magic.model.util.TimePeriod;
import com.pj.magic.util.DbUtil;

@Repository
public class NoMoreStockAdjustmentDaoImpl extends MagicDao implements NoMoreStockAdjustmentDao {

	private static final String NO_MORE_STOCK_ADJUSTMENT_NUMBER_SEQUENCE =
			"NO_MORE_STOCK_ADJUSTMENT_NO_SEQ";
	
	private static final String BASE_SELECT_SQL = 
			"select a.ID, NO_MORE_STOCK_ADJUSTMENT_NO, SALES_INVOICE_ID, b.SALES_INVOICE_NO, a.PAYMENT_NO,"
			+ " a.POST_IND, a.POST_DT, a.POST_BY,"
			+ " PAID_IND, PAID_BY, PAID_DT, a.REMARKS, a.PILFERAGE_IND,"
			+ " b.CUSTOMER_ID, c.NAME as CUSTOMER_NAME,"
			+ " d.USERNAME as POST_BY_USERNAME,"
			+ " e.USERNAME as PAID_BY_USERNAME,"
			+ " a.PAYMENT_TERMINAL_ID, f.NAME as PAYMENT_TERMINAL_NAME"
			+ " from NO_MORE_STOCK_ADJUSTMENT a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID"
			+ " join CUSTOMER c"
			+ "   on c.ID = b.CUSTOMER_ID"
			+ " left join USER d"
			+ "   on d.ID = a.POST_BY"
			+ " left join USER e"
			+ "   on e.ID = a.PAID_BY"
			+ " left join PAYMENT_TERMINAL f"
			+ "   on f.ID = a.PAYMENT_TERMINAL_ID";
	
	private NoMoreStockAdjustmentRowMapper rowMapper = new NoMoreStockAdjustmentRowMapper();
	
	private class NoMoreStockAdjustmentRowMapper implements RowMapper<NoMoreStockAdjustment> {

		@Override
		public NoMoreStockAdjustment mapRow(ResultSet rs, int rowNum) throws SQLException {
			NoMoreStockAdjustment salesReturn = new NoMoreStockAdjustment();
			salesReturn.setId(rs.getLong("ID"));
			salesReturn.setNoMoreStockAdjustmentNumber(rs.getLong("NO_MORE_STOCK_ADJUSTMENT_NO"));
			
			SalesInvoice salesInvoice = new SalesInvoice();
			salesInvoice.setId(rs.getLong("SALES_INVOICE_ID"));
			salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
			salesInvoice.setCustomer(new Customer(rs.getLong("CUSTOMER_ID"), rs.getString("CUSTOMER_NAME")));
			salesReturn.setSalesInvoice(salesInvoice);
			
			salesReturn.setPosted("Y".equals(rs.getString("POST_IND")));
			if (salesReturn.isPosted()) {
				salesReturn.setPostDate(rs.getTimestamp("POST_DT"));
				salesReturn.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			
			salesReturn.setPaid("Y".equals(rs.getString("PAID_IND")));
			if (salesReturn.isPaid()) {
				salesReturn.setPaidDate(rs.getTimestamp("PAID_DT"));
				salesReturn.setPaidBy(new User(rs.getLong("PAID_BY"), rs.getString("PAID_BY_USERNAME")));
				salesReturn.setPaymentTerminal(new PaymentTerminal(
						rs.getLong("PAYMENT_TERMINAL_ID"), rs.getString("PAYMENT_TERMINAL_NAME")));
			}
			
			salesReturn.setRemarks(rs.getString("REMARKS"));
			if (rs.getLong("PAYMENT_NO") != 0) {
				salesReturn.setPaymentNumber(rs.getLong("PAYMENT_NO"));
			}
			
			salesReturn.setPilferageFlag("Y".equals(rs.getString("PILFERAGE_IND")));
			
			return salesReturn;
		}
		
	}

	@Override
	public void save(NoMoreStockAdjustment noMoreStockAdjustment) {
		if (noMoreStockAdjustment.getId() == null) {
			insert(noMoreStockAdjustment);
		} else {
			update(noMoreStockAdjustment);
		}
	}

	private static final String UPDATE_SQL =
			"update NO_MORE_STOCK_ADJUSTMENT set SALES_INVOICE_ID = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?,"
			+ " PAID_IND = ?, PAID_DT = ?, PAID_BY = ?, PAYMENT_TERMINAL_ID = ?, REMARKS = ?,"
			+ " PAYMENT_NO = ?, PILFERAGE_IND = ? where ID = ?";
	
	private void update(NoMoreStockAdjustment noMoreStockAdjustment) {
		getJdbcTemplate().update(UPDATE_SQL,
				noMoreStockAdjustment.getSalesInvoice().getId(),
				noMoreStockAdjustment.isPosted() ? "Y" : "N",
				noMoreStockAdjustment.isPosted() ? noMoreStockAdjustment.getPostDate() : null,
				noMoreStockAdjustment.isPosted() ? noMoreStockAdjustment.getPostedBy().getId() : null,
				noMoreStockAdjustment.isPaid() ? "Y" : "N",
				noMoreStockAdjustment.isPaid() ? noMoreStockAdjustment.getPaidDate() : null,
				noMoreStockAdjustment.isPaid() ? noMoreStockAdjustment.getPaidBy().getId() : null,
				noMoreStockAdjustment.isPaid() ? noMoreStockAdjustment.getPaymentTerminal().getId() : null,
				noMoreStockAdjustment.getRemarks(),
				noMoreStockAdjustment.getPaymentNumber(),
				noMoreStockAdjustment.getPilferageFlag() ? "Y" : "N",
				noMoreStockAdjustment.getId());
	}
	
	private static final String INSERT_SQL = 
			"insert into NO_MORE_STOCK_ADJUSTMENT"
			+ " (NO_MORE_STOCK_ADJUSTMENT_NO, SALES_INVOICE_ID, PILFERAGE_IND) values (?, ?, 'Y')";

	private void insert(final NoMoreStockAdjustment noMoreStockAdjustment) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextNoMoreStockAdjustmentNumber());
				ps.setLong(2, noMoreStockAdjustment.getSalesInvoice().getId());
				return ps;
			}
		}, holder);
		
		NoMoreStockAdjustment updated = get(holder.getKey().longValue());
		noMoreStockAdjustment.setId(updated.getId());
		noMoreStockAdjustment.setNoMoreStockAdjustmentNumber(updated.getNoMoreStockAdjustmentNumber());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public NoMoreStockAdjustment get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private long getNextNoMoreStockAdjustmentNumber() {
		return getNextSequenceValue(NO_MORE_STOCK_ADJUSTMENT_NUMBER_SEQUENCE);
	}

	private static final String FIND_BY_NO_MORE_STOCK_ADJUSTMENT_NUMBER_SQL = BASE_SELECT_SQL 
			+ " where a.NO_MORE_STOCK_ADJUSTMENT_NO = ?";
	
	@Override
	public NoMoreStockAdjustment findByNoMoreStockAdjustmentNumber(long noMoreStockAdjustmentNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_NO_MORE_STOCK_ADJUSTMENT_NUMBER_SQL, rowMapper, 
					noMoreStockAdjustmentNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<NoMoreStockAdjustment> search(NoMoreStockAdjustmentSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getNoMoreStockAdjustmentNumber() != null) {
			sql.append(" and a.NO_MORE_STOCK_ADJUSTMENT_NO = ?");
			params.add(criteria.getNoMoreStockAdjustmentNumber());
		}
		
		if (criteria.getPosted() != null) {
			sql.append(" and a.POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getCustomer() != null) {
			sql.append(" and b.CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getSalesInvoice() != null) {
			sql.append(" and SALES_INVOICE_ID = ?");
			params.add(criteria.getSalesInvoice().getId());
		}
		
		if (criteria.getPostDate() != null) {
			sql.append(" and a.POST_DT >= ? and a.POST_DT <= DATE_ADD(?, INTERVAL 1 DAY)");
			params.add(DbUtil.toMySqlDateString(criteria.getPostDate()));
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
		
		sql.append(" order by NO_MORE_STOCK_ADJUSTMENT_NO desc");
		
		return getJdbcTemplate().query(sql.toString(), rowMapper, params.toArray());
	}

	private static final String FIND_ALL_BY_SALES_INVOICE_SQL = BASE_SELECT_SQL + " where a.SALES_INVOICE_ID = ?";
	
	@Override
	public List<NoMoreStockAdjustment> findAllBySalesInvoice(SalesInvoice salesInvoice) {
		return getJdbcTemplate().query(FIND_ALL_BY_SALES_INVOICE_SQL, rowMapper, salesInvoice.getId());
	}
	
}