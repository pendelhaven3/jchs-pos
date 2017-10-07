package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PromoRedemptionSalesInvoiceDao;
import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.SalesInvoice;

@Repository
public class PromoRedemptionSalesInvoiceDaoImpl extends MagicDao implements PromoRedemptionSalesInvoiceDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PROMO_REDEMPTION_ID, SALES_INVOICE_ID,"
			+ " b.SALES_INVOICE_NO, b.TRANSACTION_DT, b.PRICING_SCHEME_ID"
			+ " from PROMO_REDEMPTION_SALES_INVOICE a"
			+ " join SALES_INVOICE b"
			+ "   on b.ID = a.SALES_INVOICE_ID";
	
	private static final String SAVE_SQL = "insert into PROMO_REDEMPTION_SALES_INVOICE"
			+ " (PROMO_REDEMPTION_ID, SALES_INVOICE_ID) values (?, ?)";
	
	private PromoRedemptionSalesInvoiceRowMapper rowMapper = new PromoRedemptionSalesInvoiceRowMapper();
	
	@Override
	public void save(final PromoRedemptionSalesInvoice salesInvoice) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, salesInvoice.getParent().getId());
				ps.setLong(2, salesInvoice.getSalesInvoice().getId());
				return ps;
			}
		}, holder);
		
		salesInvoice.setId(holder.getKey().longValue());
	}

	private static final String FIND_ALL_BY_PROMO_REDEMPTION_SQL = BASE_SELECT_SQL
			+ " where a.PROMO_REDEMPTION_ID = ?";
	
	@Override
	public List<PromoRedemptionSalesInvoice> findAllByPromoRedemption(PromoRedemption promoRedemption) {
		return getJdbcTemplate().query(FIND_ALL_BY_PROMO_REDEMPTION_SQL, 
				rowMapper, promoRedemption.getId());
	}

	private class PromoRedemptionSalesInvoiceRowMapper implements RowMapper<PromoRedemptionSalesInvoice> {

		@Override
		public PromoRedemptionSalesInvoice mapRow(ResultSet rs, int rowNum) throws SQLException {
			PromoRedemptionSalesInvoice promoRedemptionSalesInvoice = new PromoRedemptionSalesInvoice();
			promoRedemptionSalesInvoice.setId(rs.getLong("ID"));
			promoRedemptionSalesInvoice.setParent(new PromoRedemption(rs.getLong("PROMO_REDEMPTION_ID")));
			
			SalesInvoice salesInvoice = new SalesInvoice();
			salesInvoice.setId(rs.getLong("SALES_INVOICE_ID"));
			salesInvoice.setSalesInvoiceNumber(rs.getLong("SALES_INVOICE_NO"));
			salesInvoice.setTransactionDate(rs.getDate("TRANSACTION_DT"));
			salesInvoice.setPricingScheme(new PricingScheme(rs.getLong("PRICING_SCHEME_ID")));
			promoRedemptionSalesInvoice.setSalesInvoice(salesInvoice);
			
			return promoRedemptionSalesInvoice;
		}
		
	}

	private static final String DELETE_SQL = "delete from PROMO_REDEMPTION_SALES_INVOICE where ID = ?";
	
	@Override
	public void delete(PromoRedemptionSalesInvoice salesInvoice) {
		getJdbcTemplate().update(DELETE_SQL, salesInvoice.getId());
	}

	private static final String DELETE_ALL_BY_PROMO_REDEMPTION_SQL = 
			"delete from PROMO_REDEMPTION_SALES_INVOICE where PROMO_REDEMPTION_ID = ?";
	
	@Override
	public void deleteAllByPromoRedemption(PromoRedemption promoRedemption) {
		getJdbcTemplate().update(DELETE_ALL_BY_PROMO_REDEMPTION_SQL, promoRedemption.getId());
	}
	
}