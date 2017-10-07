package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SalesInvoiceItemDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.SalesInvoiceItem;

@Repository
public class SalesInvoiceItemDaoImpl extends MagicDao implements SalesInvoiceItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE,"
			+ " DISCOUNT_1, DISCOUNT_2, DISCOUNT_3, FLAT_RATE_DISCOUNT, COST,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION,"
			+ " b.MANUFACTURER_ID, c.NAME as MANUFACTURER_NAME"
			+ " from SALES_INVOICE_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " left join MANUFACTURER c"
			+ "   on c.ID = b.MANUFACTURER_ID";
	
	private SalesInvoiceItemRowMapper salesInvoiceItemRowMapper = new SalesInvoiceItemRowMapper();
	
	@Override
	public void save(SalesInvoiceItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String UPDATE_SQL =
			"update SALES_INVOICE_ITEM set DISCOUNT_1 = ?, DISCOUNT_2 = ?, DISCOUNT_3 = ?,"
			+ " FLAT_RATE_DISCOUNT = ? where ID = ?";
	
	private void update(SalesInvoiceItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getDiscount1(),
				item.getDiscount2(),
				item.getDiscount3(),
				item.getFlatRateDiscount(),
				item.getId());
	}

	private static final String INSERT_SQL =
			"insert into SALES_INVOICE_ITEM (SALES_INVOICE_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE, COST)"
			+ " values (?, ?, ?, ?, ?, ?)";
	
	private void insert(SalesInvoiceItem item) {
		getJdbcTemplate().update(INSERT_SQL,
				item.getParent().getId(),
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getUnitPrice(),
				item.getCost());
	}

	private static final String FIND_ALL_BY_SALES_INVOICE_SQL = BASE_SELECT_SQL
			+ " where SALES_INVOICE_ID = ?";
	
	@Override
	public List<SalesInvoiceItem> findAllBySalesInvoice(final SalesInvoice salesInvoice) {
		return getJdbcTemplate().query(FIND_ALL_BY_SALES_INVOICE_SQL, salesInvoiceItemRowMapper, 
				salesInvoice.getId());
	}

	private class SalesInvoiceItemRowMapper implements RowMapper<SalesInvoiceItem> {

		@Override
		public SalesInvoiceItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesInvoiceItem item = new SalesInvoiceItem();
			item.setId(rs.getLong("ID"));
			item.setProduct(mapProduct(rs));
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
			item.setDiscount1(rs.getBigDecimal("DISCOUNT_1"));
			item.setDiscount2(rs.getBigDecimal("DISCOUNT_2"));
			item.setDiscount3(rs.getBigDecimal("DISCOUNT_3"));
			item.setFlatRateDiscount(rs.getBigDecimal("FLAT_RATE_DISCOUNT"));
			item.setCost(rs.getBigDecimal("COST"));
			return item;
		}
		
		private Product mapProduct(ResultSet rs) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			
			if (rs.getLong("MANUFACTURER_ID") != 0) {
				Manufacturer manufacturer = new Manufacturer();
				manufacturer.setId(rs.getLong("MANUFACTURER_ID"));
				manufacturer.setName(rs.getString("MANUFACTURER_NAME"));
				product.setManufacturer(manufacturer);
			}
			
			return product;
		}
		
	}

}