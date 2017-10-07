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

import com.pj.magic.dao.PurchaseReturnBadStockItemDao;
import com.pj.magic.model.PurchaseReturnBadStock;
import com.pj.magic.model.PurchaseReturnBadStockItem;
import com.pj.magic.model.Product;

@Repository
public class PurchaseReturnBadStockItemDaoImpl extends MagicDao implements PurchaseReturnBadStockItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, PURCHASE_RETURN_BAD_STOCK_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_COST,"
			+ " b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " from PURCHASE_RETURN_BAD_STOCK_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID";
	
	private PurchaseReturnBadStockItemRowMapper purchaseReturnBadStockItemRowMapper = 
			new PurchaseReturnBadStockItemRowMapper();
	
	@Override
	public void save(PurchaseReturnBadStockItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into PURCHASE_RETURN_BAD_STOCK_ITEM"
			+ " (PURCHASE_RETURN_BAD_STOCK_ID, PRODUCT_ID, UNIT, QUANTITY, UNIT_COST)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchaseReturnBadStockItem item) {
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
				ps.setBigDecimal(5, item.getUnitCost());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update PURCHASE_RETURN_BAD_STOCK_ITEM set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, UNIT_COST = ?"
			+ " where ID = ?";
	
	private void update(PurchaseReturnBadStockItem item) {
		getJdbcTemplate().update(UPDATE_SQL,
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getUnitCost(),
				item.getId());
	}

	private static final String FIND_ALL_BY_PURCHASE_RETURN_BAD_STOCK_SQL = BASE_SELECT_SQL
			+ " where a.PURCHASE_RETURN_BAD_STOCK_ID = ?";
	
	@Override
	public List<PurchaseReturnBadStockItem> findAllByPurchaseReturnBadStock(
			PurchaseReturnBadStock purchaseReturnBadStock) {
		return getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_RETURN_BAD_STOCK_SQL, purchaseReturnBadStockItemRowMapper, 
				purchaseReturnBadStock.getId());
	}

	private static final String DELETE_SQL = "delete from PURCHASE_RETURN_BAD_STOCK_ITEM where ID = ?";
	
	@Override
	public void delete(PurchaseReturnBadStockItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private class PurchaseReturnBadStockItemRowMapper implements RowMapper<PurchaseReturnBadStockItem> {

		@Override
		public PurchaseReturnBadStockItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchaseReturnBadStockItem item = new PurchaseReturnBadStockItem();
			item.setId(rs.getLong("ID"));
			
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			item.setProduct(product);
			
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setUnitCost(rs.getBigDecimal("UNIT_COST"));
			
			return item;
		}
		
	}
	
}