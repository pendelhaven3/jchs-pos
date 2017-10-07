package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.NoMoreStockAdjustmentItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoiceItem;
import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.NoMoreStockAdjustmentItem;
import com.pj.magic.model.search.NoMoreStockAdjustmentItemSearchCriteria;

@Repository
public class NoMoreStockAdjustmentItemDaoImpl extends MagicDao implements NoMoreStockAdjustmentItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, NO_MORE_STOCK_ADJUSTMENT_ID, a.QUANTITY, b.PRODUCT_ID, c.CODE, c.DESCRIPTION,"
			+ " a.SALES_INVOICE_ITEM_ID, b.UNIT, b.COST, b.QUANTITY as SALES_INVOICE_ITEM_QUANTITY,"
			+ " b.UNIT_PRICE, b.DISCOUNT_1, b.DISCOUNT_2, b.DISCOUNT_3, b.FLAT_RATE_DISCOUNT"
			+ " from NO_MORE_STOCK_ADJUSTMENT_ITEM a"
			+ " join SALES_INVOICE_ITEM b"
			+ "   on b.ID = a.SALES_INVOICE_ITEM_ID"
			+ " join PRODUCT c"
			+ "   on c.ID = b.PRODUCT_ID"
			+ " join NO_MORE_STOCK_ADJUSTMENT d"
			+ "   on d.ID = a.NO_MORE_STOCK_ADJUSTMENT_ID";
	
	private NoMoreStockAdjustmentItemRowMapper noMoreStockAdjustmentItemRowMapper = new NoMoreStockAdjustmentItemRowMapper();
	
	private static final String FIND_ALL_BY_NO_MORE_STOCK_ADJUSTMENT_SQL = BASE_SELECT_SQL
			+ " where a.NO_MORE_STOCK_ADJUSTMENT_ID = ?";
	
	@Override
	public List<NoMoreStockAdjustmentItem> findAllByNoMoreStockAdjustment(
			final NoMoreStockAdjustment noMoreStockAdjustment) {
		List<NoMoreStockAdjustmentItem> items = 
				getJdbcTemplate().query(FIND_ALL_BY_NO_MORE_STOCK_ADJUSTMENT_SQL, 
						noMoreStockAdjustmentItemRowMapper, noMoreStockAdjustment.getId());
		for (NoMoreStockAdjustmentItem item : items) {
			item.setParent(noMoreStockAdjustment);
		}
		return items;
	}

	private class NoMoreStockAdjustmentItemRowMapper implements RowMapper<NoMoreStockAdjustmentItem> {

		@Override
		public NoMoreStockAdjustmentItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			NoMoreStockAdjustmentItem item = new NoMoreStockAdjustmentItem();
			item.setId(rs.getLong("ID"));
			item.setParent(new NoMoreStockAdjustment(rs.getLong("NO_MORE_STOCK_ADJUSTMENT_ID")));
			item.setQuantity(rs.getInt("QUANTITY"));
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("CODE"));
			product.setDescription(rs.getString("DESCRIPTION"));
			
			SalesInvoiceItem salesInvoiceItem = new SalesInvoiceItem();
			salesInvoiceItem.setId(rs.getLong("SALES_INVOICE_ITEM_ID"));
			salesInvoiceItem.setProduct(product);
			salesInvoiceItem.setUnit(rs.getString("UNIT"));
			salesInvoiceItem.setQuantity(rs.getInt("SALES_INVOICE_ITEM_QUANTITY"));
			salesInvoiceItem.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
			salesInvoiceItem.setDiscount1(rs.getBigDecimal("DISCOUNT_1"));
			salesInvoiceItem.setDiscount2(rs.getBigDecimal("DISCOUNT_2"));
			salesInvoiceItem.setDiscount3(rs.getBigDecimal("DISCOUNT_3"));
			salesInvoiceItem.setFlatRateDiscount(rs.getBigDecimal("FLAT_RATE_DISCOUNT"));
			salesInvoiceItem.setCost(rs.getBigDecimal("COST"));
			item.setSalesInvoiceItem(salesInvoiceItem);
			
			return item;
		}
		
	}

	@Override
	public void save(NoMoreStockAdjustmentItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL = "insert into NO_MORE_STOCK_ADJUSTMENT_ITEM"
			+ " (NO_MORE_STOCK_ADJUSTMENT_ID, SALES_INVOICE_ITEM_ID, QUANTITY) values (?, ?, ?)";
	
	private void insert(final NoMoreStockAdjustmentItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getParent().getId());
				ps.setLong(2, item.getSalesInvoiceItem().getId());
				ps.setInt(3, item.getQuantity());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update NO_MORE_STOCK_ADJUSTMENT_ITEM"
			+ " set SALES_INVOICE_ITEM_ID = ?, QUANTITY = ? where ID = ?";
	
	private void update(NoMoreStockAdjustmentItem item) {
		getJdbcTemplate().update(UPDATE_SQL, 
				item.getSalesInvoiceItem().getId(),
				item.getQuantity(),
				item.getId());
	}

	private static final String DELETE_SQL = "delete from NO_MORE_STOCK_ADJUSTMENT_ITEM where ID = ?";
	
	@Override
	public void delete(NoMoreStockAdjustmentItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_NO_MORE_STOCK_ADJUSTMENT_SQL =
			"delete from NO_MORE_STOCK_ADJUSTMENT_ITEM where NO_MORE_STOCK_ADJUSTMENT_ID = ?";
	
	@Override
	public void deleteAllByNoMoreStockAdjustment(NoMoreStockAdjustment noMoreStockAdjustment) {
		getJdbcTemplate().update(DELETE_ALL_BY_NO_MORE_STOCK_ADJUSTMENT_SQL, noMoreStockAdjustment.getId());
	}

	@Override
	public List<NoMoreStockAdjustmentItem> search(
			NoMoreStockAdjustmentItemSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		sql.append(" where 1 = 1");
		
		List<Object> params = new ArrayList<>();
		
		if (criteria.getProduct() != null) {
			sql.append(" and b.PRODUCT_ID = ?");
			params.add(criteria.getProduct().getId());
		}
		
		if (!StringUtils.isEmpty(criteria.getUnit())) {
			sql.append(" and b.UNIT = ?");
			params.add(criteria.getUnit());
		}
		
		if (criteria.getSalesInvoice() != null) {
			sql.append(" and d.SALES_INVOICE_ID = ?");
			params.add(criteria.getSalesInvoice().getId());
		}
		
		return getJdbcTemplate().query(sql.toString(), noMoreStockAdjustmentItemRowMapper, params.toArray());
	}
	
}