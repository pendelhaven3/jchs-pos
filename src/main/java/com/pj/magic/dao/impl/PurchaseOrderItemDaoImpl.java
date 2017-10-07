package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.PurchaseOrderItemDao;
import com.pj.magic.model.Product;
import com.pj.magic.model.PurchaseOrder;
import com.pj.magic.model.PurchaseOrderItem;

@Repository
public class PurchaseOrderItemDaoImpl extends MagicDao implements PurchaseOrderItemDao {

	private static final String BASE_SELECT_SQL =
			"select ID, PURCHASE_ORDER_ID, PRODUCT_ID, UNIT, QUANTITY, COST, ACTUAL_QUANTITY, ORDER_IND"
			+ " from PURCHASE_ORDER_ITEM";
	
	private PurchaseOrderItemRowMapper purchaseOrderItemRowMapper = new PurchaseOrderItemRowMapper();
	
	@Override
	public void save(PurchaseOrderItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into PURCHASE_ORDER_ITEM"
			+ " (PURCHASE_ORDER_ID, PRODUCT_ID, UNIT, QUANTITY, COST)"
			+ " values (?, ?, ?, ?, ?)";
	
	private void insert(final PurchaseOrderItem item) {
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
				ps.setBigDecimal(5, item.getCost());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}
	
	private static final String UPDATE_SQL =
			"update PURCHASE_ORDER_ITEM"
			+ " set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, COST = ?, ACTUAL_QUANTITY = ?, ORDER_IND = ?"
			+ " where ID = ?";
	
	private void update(PurchaseOrderItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getProduct().getId(), item.getUnit(),
				item.getQuantity(), item.getCost(), item.getActualQuantity(),
				item.isOrdered() ? "Y" : "N", item.getId());
	}

	private static final String FIND_ALL_BY_PURCHASE_ORDER_SQL = BASE_SELECT_SQL
			+ " where PURCHASE_ORDER_ID = ?";
	
	@Override
	public List<PurchaseOrderItem> findAllByPurchaseOrder(PurchaseOrder purchaseOrder) {
		List<PurchaseOrderItem> items = getJdbcTemplate().query(FIND_ALL_BY_PURCHASE_ORDER_SQL, 
				purchaseOrderItemRowMapper, purchaseOrder.getId());
		for (PurchaseOrderItem item : items) {
			item.setParent(purchaseOrder);
		}
		return items;
	}

	private static final String DELETE_SQL = "delete from PURCHASE_ORDER_ITEM where ID = ?";
	
	@Override
	public void delete(PurchaseOrderItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_STOCK_QUANTITY_CONVERSION_SQL =
			"delete from PURCHASE_ORDER_ITEM where PURCHASE_ORDER_ID = ?";
	
	@Override
	public void deleteAllByPurchaseOrder(PurchaseOrder purchaseOrder) {
		getJdbcTemplate().update(DELETE_ALL_BY_STOCK_QUANTITY_CONVERSION_SQL, purchaseOrder.getId());
	}

	private static final String UPDATE_ALL_BY_PURCHASE_ORDER_AS_ORDERED_SQL = 
			"update PURCHASE_ORDER_ITEM set ORDER_IND = 'Y' where PURCHASE_ORDER_ID = ?";
	
	@Override
	public void updateAllByPurchaseOrderAsOrdered(PurchaseOrder purchaseOrder) {
		getJdbcTemplate().update(UPDATE_ALL_BY_PURCHASE_ORDER_AS_ORDERED_SQL, purchaseOrder.getId());
	}

	private static final String FIND_FIRST_BY_PRODUCT_SQL = BASE_SELECT_SQL
			+ " where PRODUCT_ID = ? limit 1";
	
	@Override
	public PurchaseOrderItem findFirstByProduct(Product product) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_BY_PRODUCT_SQL, 
					purchaseOrderItemRowMapper, product.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private class PurchaseOrderItemRowMapper implements RowMapper<PurchaseOrderItem> {

		@Override
		public PurchaseOrderItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			PurchaseOrderItem item = new PurchaseOrderItem();
			item.setId(rs.getLong("ID"));
			item.setParent(new PurchaseOrder(rs.getLong("PURCHASE_ORDER_ID")));
			item.setProduct(new Product(rs.getLong("PRODUCT_ID")));
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setCost(rs.getBigDecimal("COST").setScale(2));
			if (rs.getObject("ACTUAL_QUANTITY") != null) {
				item.setActualQuantity(rs.getInt("ACTUAL_QUANTITY"));
			}
			item.setOrdered("Y".equals(rs.getString("ORDER_IND")));
			return item;
		}
		
	}
	
}
