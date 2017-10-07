package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesInvoiceDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.User;
import com.pj.magic.model.search.SalesInvoiceSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class SalesInvoiceDaoImpl extends MagicDao implements SalesInvoiceDao {
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, SALES_INVOICE_NO, CREATE_DT, RELATED_SALES_REQUISITION_NO, MODE, a.REMARKS, CUSTOMER_ID,"
			+ " POST_DT, MARK_IND, MARK_DT, CANCEL_DT, CANCEL_IND, TRANSACTION_DT, VAT_AMOUNT, PRINT_IND,"
			+ " PRICING_SCHEME_ID, d.NAME as PRICING_SCHEME_NAME,"
			+ " ENCODER, b.USERNAME as ENCODER_USERNAME,"
			+ " a.PAYMENT_TERM_ID, e.NAME as PAYMENT_TERM_NAME, e.NUMBER_OF_DAYS as PAYMENT_TERM_NUMBER_OF_DAYS,"
			+ " POST_BY, f.USERNAME as POST_BY_USERNAME,"
			+ " MARK_BY, g.USERNAME as MARK_BY_USERNAME,"
			+ " CANCEL_BY, h.USERNAME as CANCEL_BY_USERNAME,"
			+ " i.NAME as CUSTOMER_NAME"
			+ " from SALES_INVOICE a"
			+ " join USER b"
			+ "   on b.ID = a.ENCODER"
			+ " join PRICING_SCHEME d"
			+ "   on d.ID = a.PRICING_SCHEME_ID"
			+ " join PAYMENT_TERM e"
			+ "   on e.ID = a.PAYMENT_TERM_ID"
			+ " left join USER f"
			+ "   on f.ID = a.POST_BY"
			+ " left join USER g"
			+ "   on g.ID = a.MARK_BY"
			+ " left join USER h"
			+ "   on h.ID = a.CANCEL_BY"
			+ " join CUSTOMER i"
			+ "   on i.ID = a.CUSTOMER_ID"
			+ " where 1 = 1";
	
	private static final String SALES_INVOICE_NUMBER_SEQUENCE = "SALES_INVOICE_NO_SEQ";
	
	private SalesInvoiceRowMapper salesInvoiceRowMapper = new SalesInvoiceRowMapper();
	
	@Override
	public void save(SalesInvoice salesInvoice) {
		if (salesInvoice.getId() == null) {
			insert(salesInvoice);
		} else {
			update(salesInvoice);
		}
	}

	private static final String INSERT_SQL =
			"insert into SALES_INVOICE "
			+ " (SALES_INVOICE_NO, CUSTOMER_ID, CREATE_DT, TRANSACTION_DT, ENCODER, RELATED_SALES_REQUISITION_NO,"
			+ "  PRICING_SCHEME_ID, MODE, REMARKS, PAYMENT_TERM_ID, POST_DT, POST_BY, VAT_AMOUNT)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
	private void insert(final SalesInvoice salesInvoice) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextSalesInvoiceNumber());
				ps.setLong(2, salesInvoice.getCustomer().getId());
				ps.setDate(3, new Date(salesInvoice.getCreateDate().getTime()));
				ps.setDate(4, new Date(salesInvoice.getTransactionDate().getTime()));
				ps.setLong(5, salesInvoice.getEncoder().getId());
				ps.setLong(6, salesInvoice.getRelatedSalesRequisitionNumber());
				ps.setLong(7, salesInvoice.getPricingScheme().getId());
				ps.setString(8, salesInvoice.getMode());
				ps.setString(9, salesInvoice.getRemarks());
				ps.setLong(10, salesInvoice.getPaymentTerm().getId());
				ps.setTimestamp(11, new Timestamp(salesInvoice.getPostDate().getTime()));
				ps.setLong(12, salesInvoice.getPostedBy().getId());
				ps.setBigDecimal(13, salesInvoice.getVatAmount());
				return ps;
			}
		}, holder);
		
		SalesInvoice updated = get(holder.getKey().longValue());
		salesInvoice.setId(updated.getId());
		salesInvoice.setSalesInvoiceNumber(updated.getSalesInvoiceNumber());
	}

	private long getNextSalesInvoiceNumber() {
		return getNextSequenceValue(SALES_INVOICE_NUMBER_SEQUENCE);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " and a.ID = ?";
	
	@Override
	public SalesInvoice get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, salesInvoiceRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private class SalesInvoiceRowMapper implements RowMapper<SalesInvoice> {

		@Override
		public SalesInvoice mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesInvoice salesInvoice = new SalesInvoice();
			salesInvoice.setId(rs.getLong("ID"));
			salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
			salesInvoice.setCreateDate(rs.getDate("CREATE_DT"));
			salesInvoice.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			salesInvoice.setEncoder(new User(rs.getLong("ENCODER"), rs.getString("ENCODER_USERNAME")));
			salesInvoice.setRelatedSalesRequisitionNumber(rs.getLong("RELATED_SALES_REQUISITION_NO"));
			salesInvoice.setCustomer(new Customer(rs.getLong("CUSTOMER_ID"), rs.getString("CUSTOMER_NAME")));
			salesInvoice.setPricingScheme(
					new PricingScheme(rs.getLong("PRICING_SCHEME_ID"), rs.getString("PRICING_SCHEME_NAME")));
			salesInvoice.setMode(rs.getString("MODE"));
			salesInvoice.setRemarks(rs.getString("REMARKS"));
			
			PaymentTerm paymentTerm = new PaymentTerm();
			paymentTerm.setId(rs.getLong("PAYMENT_TERM_ID"));
			paymentTerm.setName(rs.getString("PAYMENT_TERM_NAME"));
			paymentTerm.setNumberOfDays(rs.getInt("PAYMENT_TERM_NUMBER_OF_DAYS"));
			salesInvoice.setPaymentTerm(paymentTerm);
			
			salesInvoice.setPostDate(rs.getTimestamp("POST_DT"));
			salesInvoice.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			salesInvoice.setMarked("Y".equals(rs.getString("MARK_IND")));
			salesInvoice.setMarkDate(rs.getDate("MARK_DT"));
			if (rs.getLong("MARK_BY") != 0) {
				salesInvoice.setMarkedBy(
						new User(rs.getLong("MARK_BY"), rs.getString("MARK_BY_USERNAME")));
			}
			salesInvoice.setCancelled("Y".equals(rs.getString("CANCEL_IND")));
			salesInvoice.setCancelDate(rs.getTimestamp("CANCEL_DT"));
			if (rs.getLong("CANCEL_BY") != 0) {
				salesInvoice.setCancelledBy(
						new User(rs.getLong("CANCEL_BY"), rs.getString("CANCEL_BY_USERNAME")));
			}
			salesInvoice.setVatAmount(rs.getBigDecimal("VAT_AMOUNT"));
			salesInvoice.setPrinted("Y".equals(rs.getString("PRINT_IND")));
			return salesInvoice;
		}
		
	}

	private static final String UPDATE_SQL =
			"update SALES_INVOICE"
			+ " set MARK_IND = ?, MARK_DT = ?, MARK_BY = ?,"
			+ " CANCEL_IND = ?, CANCEL_DT = ?, CANCEL_BY = ?, PRINT_IND = ?"
			+ " where ID = ?";
	
	private void update(SalesInvoice salesInvoice) {
		getJdbcTemplate().update(UPDATE_SQL,
				salesInvoice.isMarked() ? "Y" : "N",
				salesInvoice.isMarked() ? new Date(salesInvoice.getMarkDate().getTime()) : null,
				salesInvoice.isMarked() ? salesInvoice.getMarkedBy().getId() : null,
				salesInvoice.isCancelled() ? "Y" : "N",
				salesInvoice.isCancelled() ? salesInvoice.getCancelDate() : null,
				salesInvoice.isCancelled() ? salesInvoice.getCancelledBy().getId() : null,
				salesInvoice.isPrinted() ? "Y" : "N",
				salesInvoice.getId());
	}

	private static final String PAID_WHERE_CLAUSE_SQL =
			" and exists("
			+ "   select 1"
			+ "   from PAYMENT_SALES_INVOICE psi"
			+ "   join PAYMENT p"
			+ "     on p.ID = psi.PAYMENT_ID"
			+ "   where psi.SALES_INVOICE_ID = a.ID"
			+ "   and p.POST_IND = 'Y'"
			+ " )";
	
	private static final String UNPAID_WHERE_CLAUSE_SQL =
			" and CANCEL_IND = 'N'"
			+ " and not exists("
			+ "   select 1"
			+ "   from PAYMENT_SALES_INVOICE psi"
			+ "   join PAYMENT p"
			+ "     on p.ID = psi.PAYMENT_ID"
			+ "   where psi.SALES_INVOICE_ID = a.ID"
			+ "   and p.POST_IND = 'Y'"
			+ " )";
	
	private static final String UNREDEEMED_PROMO_WHERE_CLAUSE_SQL =
			"   and not exists("
			+ "   select 1"
			+ "   from PROMO_REDEMPTION_SALES_INVOICE prsi"
			+ "   join PROMO_REDEMPTION pr"
			+ "     on pr.ID = prsi.PROMO_REDEMPTION_ID"
			+ "   where prsi.SALES_INVOICE_ID = a.ID"
			+ "   and pr.PROMO_ID = ?"
			+ " )"
			+ " and a.CANCEL_IND = 'N'";
	
	@Override
	public List<SalesInvoice> search(SalesInvoiceSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.isMarked() != null) {
			sql.append(" and MARK_IND = ?");
			params.add(criteria.isMarked() ? "Y" : "N");
		}
		
		if (criteria.isCancelled() != null) {
			sql.append(" and CANCEL_IND = ?");
			params.add(criteria.isCancelled() ? "Y" : "N");
		}
		
		if (criteria.getSalesInvoiceNumber() != null) {
			sql.append(" and SALES_INVOICE_NO = ?");
			params.add(criteria.getSalesInvoiceNumber());
		}
		
		if (criteria.getCustomer() != null) {
			sql.append(" and CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getTransactionDate() != null) {
			sql.append(" and a.TRANSACTION_DT = ?");
			params.add(DbUtil.toMySqlDateString(criteria.getTransactionDate()));
		}
		
		if (criteria.getTransactionDateFrom() != null) {
			sql.append(" and a.TRANSACTION_DT >= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getTransactionDateFrom()));
		}
		
		if (criteria.getTransactionDateTo() != null) {
			sql.append(" and a.TRANSACTION_DT <= ?");
			params.add(DbUtil.toMySqlDateString(criteria.getTransactionDateTo()));
		}
		
		if (criteria.getPricingScheme() != null) {
			sql.append(" and a.PRICING_SCHEME_ID = ?");
			params.add(criteria.getPricingScheme().getId());
		}
		
		if (criteria.getPaid() != null) {
			if (criteria.getPaid()) {
				sql.append(PAID_WHERE_CLAUSE_SQL);
			} else {
				sql.append(UNPAID_WHERE_CLAUSE_SQL);
			}
		}
		
		if (criteria.getUnredeemedPromo() != null) {
			sql.append(UNREDEEMED_PROMO_WHERE_CLAUSE_SQL);
			params.add(criteria.getUnredeemedPromo().getId());
		}
		
		if (!StringUtils.isEmpty(criteria.getOrderBy())) {
			sql.append(" order by ").append(criteria.getOrderBy());
		} else {
			sql.append(" order by SALES_INVOICE_NO desc");
		}
		
		return getJdbcTemplate().query(sql.toString(), salesInvoiceRowMapper, params.toArray());
	}

	private static final String FIND_BY_SALES_INVOICE_NUMBER_SQL = BASE_SELECT_SQL
			+ " and a.SALES_INVOICE_NO = ?";
	
	@Override
	public SalesInvoice findBySalesInvoiceNumber(long salesInvoiceNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_SALES_INVOICE_NUMBER_SQL, salesInvoiceRowMapper,
					salesInvoiceNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_ALL_FOR_PAYMENT_BY_CUSTOMER_SQL = BASE_SELECT_SQL
			+ " and a.CUSTOMER_ID = ?"
			+ " and a.CANCEL_IND = 'N'"
			+ " and not exists("
			+ "   select 1"
			+ "   from PAYMENT_SALES_INVOICE psi"
			+ "   join PAYMENT pay"
			+ "     on pay.ID = psi.PAYMENT_ID"
			+ "   where SALES_INVOICE_ID = a.ID"
			+ "   and pay.CANCEL_IND = 'N'"
			+ " )";
	
	@Override
	public List<SalesInvoice> findAllForPaymentByCustomer(Customer customer) {
		return getJdbcTemplate().query(FIND_ALL_FOR_PAYMENT_BY_CUSTOMER_SQL, salesInvoiceRowMapper, 
				customer.getId());
	}

	private static final String FIND_MOST_RECENT_BY_CUSTOMER_AND_PRODUCT_SQL =
			"select a.ID"
			+ " from SALES_INVOICE a"
			+ " join SALES_INVOICE_ITEM b"
			+ "   on b.SALES_INVOICE_ID = a.ID"
			+ " where a.CUSTOMER_ID = ?"
			+ " and b.PRODUCT_ID = ?"
			+ " and a.MARK_IND = 'Y'"
			+ " and a.CANCEL_IND = 'N'"
			+ " order by a.POST_DT desc"
			+ " limit 1";
	
	@Override
	public SalesInvoice findMostRecentByCustomerAndProduct(Customer customer, Product product) {
		Long id = null;
		try {
			id = getJdbcTemplate().queryForObject(FIND_MOST_RECENT_BY_CUSTOMER_AND_PRODUCT_SQL, Long.class,
					customer.getId(), product.getId());
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
		return get(id);
	}

}