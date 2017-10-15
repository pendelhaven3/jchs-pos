package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PaymentSalesInvoiceDao;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentSalesInvoice;
import com.pj.magic.model.PaymentTerm;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.PaymentSalesInvoiceSearchCriteria;
import com.pj.magic.model.util.TimePeriod;
import com.pj.magic.util.DbUtil;

@Repository
public class PaymentSalesInvoiceDaoImpl extends MagicDao implements PaymentSalesInvoiceDao {

    private static final Logger logger = LoggerFactory.getLogger(PaymentSalesInvoiceDaoImpl.class);
    
	private static final String BASE_SELECT_SQL =
			"   select a.ID, PAYMENT_ID, SALES_INVOICE_ID, b.SALES_INVOICE_NO, ADJUSTMENT_AMOUNT,"
			+ " c.PAYMENT_NO, c.POST_DT,"
			+ " b.CUSTOMER_ID, d.NAME as CUSTOMER_NAME,"
			+ " b.TRANSACTION_DT, e.NUMBER_OF_DAYS as PAYMENT_TERM_NUMBER_OF_DAYS,"
			+ " c.PAYMENT_TERMINAL_ID, f.NAME as PAYMENT_TERMINAL_NAME"
			+ " from PAYMENT_SALES_INVOICE a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID"
			+ " join PAYMENT c"
			+ "   on c.ID = a.PAYMENT_ID"
			+ " join CUSTOMER d"
			+ "   on d.ID = b.CUSTOMER_ID"
			+ " join PAYMENT_TERM e"
			+ "   on e.ID = b.PAYMENT_TERM_ID"
			+ " left join PAYMENT_TERMINAL f"
			+ "   on f.ID = c.PAYMENT_TERMINAL_ID";
	
	private PaymentSalesInvoiceRowMapper paymentSalesInvoiceRowMapper = new PaymentSalesInvoiceRowMapper();
	
	@Override
	public void save(PaymentSalesInvoice item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			logger.warn("Updating Payment Sales Invoice not possible");
		}
	}

	private static final String INSERT_SQL =
			"insert into PAYMENT_SALES_INVOICE (PAYMENT_ID, SALES_INVOICE_ID) values (?, ?)";
	
	private void insert(final PaymentSalesInvoice paymentSalesInvoice) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, paymentSalesInvoice.getParent().getId());
				ps.setLong(2, paymentSalesInvoice.getSalesInvoice().getId());
				return ps;
			}
		}, holder);
		
		paymentSalesInvoice.setId(holder.getKey().longValue());
	}

	private static final String FIND_ALL_BY_PAYMENT_SQL = BASE_SELECT_SQL
			+ " where PAYMENT_ID = ?"
			+ " order by b.TRANSACTION_DT, b.SALES_INVOICE_NO";
	
	@Override
	public List<PaymentSalesInvoice> findAllByPayment(Payment payment) {
		List<PaymentSalesInvoice> salesInvoices = 
				getJdbcTemplate().query(FIND_ALL_BY_PAYMENT_SQL, paymentSalesInvoiceRowMapper, payment.getId());
		for (PaymentSalesInvoice salesInvoice : salesInvoices) {
			salesInvoice.setParent(payment);
		}
		return salesInvoices;
	}

	private static final String DELETE_ALL_BY_PAYMENT_SQL = 
			"delete from PAYMENT_SALES_INVOICE where PAYMENT_ID = ?";
	
	@Override
	public void deleteAllByPayment(Payment payment) {
		getJdbcTemplate().update(DELETE_ALL_BY_PAYMENT_SQL, payment.getId());
	}

	private static final String DELETE_SQL = "delete from PAYMENT_SALES_INVOICE where ID = ?";
	
	@Override
	public void delete(PaymentSalesInvoice paymentSalesInvoice) {
		getJdbcTemplate().update(DELETE_SQL, paymentSalesInvoice.getId());
	}

	private class PaymentSalesInvoiceRowMapper implements RowMapper<PaymentSalesInvoice> {

		@Override
		public PaymentSalesInvoice mapRow(ResultSet rs, int rowNum) throws SQLException {
			PaymentSalesInvoice item = new PaymentSalesInvoice();
			item.setId(rs.getLong("ID"));
			
			Payment payment = new Payment();
			payment.setId(rs.getLong("PAYMENT_ID"));
			payment.setPaymentNumber(rs.getLong("PAYMENT_NO"));
			payment.setPostDate(rs.getTimestamp("POST_DT"));
			item.setParent(payment);
			
			SalesInvoice salesInvoice = new SalesInvoice();
			salesInvoice.setId(rs.getLong("SALES_INVOICE_ID"));
			salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
			salesInvoice.setCustomer(new Customer(rs.getLong("CUSTOMER_ID"), rs.getString("CUSTOMER_NAME")));
			salesInvoice.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			item.setSalesInvoice(salesInvoice);
			
			PaymentTerm paymentTerm = new PaymentTerm();
			paymentTerm.setNumberOfDays(rs.getInt("PAYMENT_TERM_NUMBER_OF_DAYS"));
			salesInvoice.setPaymentTerm(paymentTerm);
			
			if (rs.getLong("PAYMENT_TERMINAL_ID") != 0) {
				PaymentTerminal terminal = new PaymentTerminal();
				terminal.setId(rs.getLong("PAYMENT_TERMINAL_ID"));
				terminal.setName(rs.getString("PAYMENT_TERMINAL_NAME"));
				payment.setPaymentTerminal(terminal);
			}
			
			item.setAdjustedAmount(rs.getBigDecimal("ADJUSTMENT_AMOUNT"));
			
			return item;
		}
		
	}

	@Override
	public List<PaymentSalesInvoice> search(PaymentSalesInvoiceSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		if (criteria.getPaid() != null) {
			sql.append(" and c.POST_IND = ?");
			params.add(criteria.getPaid() ? "Y" : "N");
		}
		
		if (criteria.getPaymentDate() != null) {
			if (criteria.getTimePeriod() != null) {
				if (criteria.getTimePeriod() == TimePeriod.MORNING_ONLY) {
					sql.append(" and c.POST_DT >= ? and c.POST_DT < date_add(?, interval 13 hour)");
					params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
					params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
				} else if (criteria.getTimePeriod() == TimePeriod.AFTERNOON_ONLY) {
					sql.append(" and c.POST_DT >= date_add(?, interval 13 hour)"
							+ " and c.POST_DT < date_add(?, interval 1 day)");
					params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
					params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
				}
			} else {
				sql.append(" and c.POST_DT >= ? and c.POST_DT < date_add(?, interval 1 day)");
				params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
				params.add(DbUtil.toMySqlDateString(criteria.getPaymentDate()));
			}
		}
		
		if (criteria.getCustomer() != null) {
			sql.append(" and c.CUSTOMER_ID = ?");
			params.add(criteria.getCustomer().getId());
		}
		
		if (criteria.getPaymentTerminal() != null) {
			sql.append(" and c.PAYMENT_TERMINAL_ID = ?");
			params.add(criteria.getPaymentTerminal().getId());
		}
		
		sql.append(" order by c.POST_DT, b.SALES_INVOICE_NO");
		
		return getJdbcTemplate().query(sql.toString(), paymentSalesInvoiceRowMapper, params.toArray());
	}
	
}