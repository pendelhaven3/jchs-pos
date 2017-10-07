package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.BadStockReturnItemDao;
import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Unit;
import com.pj.magic.model.UnitConversion;

@Repository
public class BadStockReturnItemDaoImpl extends MagicDao implements BadStockReturnItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, BAD_STOCK_RETURN_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE, COST, SALES_INVOICE_NO,"
			+ " b.UNIT_IND_CSE, b.UNIT_IND_TIE, b.UNIT_IND_CTN, b.UNIT_IND_DOZ, b.UNIT_IND_PCS,"
			+ " b.UNIT_CONV_CSE, b.UNIT_CONV_TIE, b.UNIT_CONV_CTN, b.UNIT_CONV_DOZ, b.UNIT_CONV_PCS,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from BAD_STOCK_RETURN_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private BadStockReturnItemRowMapper badStockReturnItemRowMapper = new BadStockReturnItemRowMapper();
	
	@Override
	public void save(BadStockReturnItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into BAD_STOCK_RETURN_ITEM"
			+ " (BAD_STOCK_RETURN_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_PRICE, SALES_INVOICE_NO)"
			+ " values (?, ?, ?, ?, ?, ?)";
	
	private void insert(final BadStockReturnItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getParent().getId());
				ps.setLong(2, item.getProduct().getId());
				ps.setString(3, item.getUnit());
				ps.setInt(4, item.getQuantity());
				ps.setBigDecimal(5, item.getUnitPrice());
				if (item.getSalesInvoiceNumber() != null) {
					ps.setLong(6, item.getSalesInvoiceNumber());
				} else {
					ps.setNull(6, Types.BIGINT);
				}
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update BAD_STOCK_RETURN_ITEM set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, UNIT_PRICE = ?,"
			+ " COST = ?, SALES_INVOICE_NO = ? where ID = ?";
	
	private void update(BadStockReturnItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getUnitPrice(),
				item.getCost(),
				item.getSalesInvoiceNumber() != null ? item.getSalesInvoiceNumber() : null,
				item.getId());
	}

	private static final String FIND_ALL_BY_BAD_STOCK_RETURN_SQL = BASE_SELECT_SQL
			+ " where a.BAD_STOCK_RETURN_ID = ?";
	
	@Override
	public List<BadStockReturnItem> findAllByBadStockReturn(BadStockReturn badStockReturn) {
		return getJdbcTemplate().query(FIND_ALL_BY_BAD_STOCK_RETURN_SQL, badStockReturnItemRowMapper, 
				badStockReturn.getId());
	}

	private static final String DELETE_SQL = "delete from BAD_STOCK_RETURN_ITEM where ID = ?";
	
	@Override
	public void delete(BadStockReturnItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private class BadStockReturnItemRowMapper implements RowMapper<BadStockReturnItem> {

		@Override
		public BadStockReturnItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			BadStockReturnItem item = new BadStockReturnItem();
			item.setId(rs.getLong("ID"));
			
			item.setProduct(mapProduct(rs));
			
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
			item.setCost(rs.getBigDecimal("COST"));
			
			long salesInvoiceNumber = rs.getLong("SALES_INVOICE_NO");
			if (salesInvoiceNumber != 0) {
				item.setSalesInvoiceNumber(salesInvoiceNumber);
			}
			
			return item;
		}

		private Product mapProduct(ResultSet rs) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			
			if ("Y".equals(rs.getString("UNIT_IND_CSE"))) {
				product.addUnit(Unit.CASE);
				product.getUnitConversions().add(new UnitConversion(Unit.CASE, rs.getInt("UNIT_CONV_CSE")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_TIE"))) {
				product.addUnit(Unit.TIE);
				product.getUnitConversions().add(new UnitConversion(Unit.TIE, rs.getInt("UNIT_CONV_TIE")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_CTN"))) {
				product.addUnit(Unit.CARTON);
				product.getUnitConversions().add(new UnitConversion(Unit.CARTON, rs.getInt("UNIT_CONV_CTN")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_DOZ"))) {
				product.addUnit(Unit.DOZEN);
				product.getUnitConversions().add(new UnitConversion(Unit.DOZEN, rs.getInt("UNIT_CONV_DOZ")));
			}
			if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
				product.addUnit(Unit.PIECES);
				product.getUnitConversions().add(new UnitConversion(Unit.PIECES, rs.getInt("UNIT_CONV_PCS")));
			}
			
			return product;
		}
		
	}
	
}