package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.InventoryCheckSummaryItemDao;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Product;

@Repository
public class InventoryCheckSummaryItemDaoImpl extends MagicDao implements InventoryCheckSummaryItemDao {

	private InventoryCheckSummaryItemRowMapper rowMapper = new InventoryCheckSummaryItemRowMapper();
	
	private static final String FIND_ALL_BY_INVENTORY_CHECK_SQL =
			" select c.ID, c.CODE, c.DESCRIPTION, a.UNIT, case when a.UNIT = c.UOM_CODE then c.FINAL_COST else c.FINAL_COST1 end as FINAL_COST, sum(a.QUANTITY) as QUANTITY"
			+ " from AREA_INV_REPORT_ITEM a"
			+ " join AREA_INV_REPORT b"
			+ "    on b.ID = a.AREA_INV_REPORT_ID"
			+ " join PRODUCT c"
			+ "    on c.ID = a.PRODUCT_ID"
			+ " where b.INVENTORY_CHECK_ID = ?"
			+ " group by c.CODE, c.DESCRIPTION, a.UNIT, c.FINAL_COST"
			+ " order by c.DESCRIPTION, a.UNIT";
	
	@Override
	public List<InventoryCheckSummaryItem> findAllByInventoryCheck(InventoryCheck inventoryCheck) {
		return getJdbcTemplate().query(FIND_ALL_BY_INVENTORY_CHECK_SQL, rowMapper, inventoryCheck.getId());
	}

	private static final String FIND_ALL_BY_POSTED_INVENTORY_CHECK_SQL =
			" select b.ID, b.CODE, b.DESCRIPTION, a.UNIT, a.COST as FINAL_COST, a.QUANTITY" 
			+ " from INVENTORY_CHECK_SUMMARY_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " where a.INVENTORY_CHECK_ID = ?"
			+ " order by b.DESCRIPTION, a.UNIT";
	
	@Override
	public List<InventoryCheckSummaryItem> findAllByPostedInventoryCheck(InventoryCheck inventoryCheck) {
		return getJdbcTemplate().query(FIND_ALL_BY_POSTED_INVENTORY_CHECK_SQL,
				rowMapper, inventoryCheck.getId());
	}

	private class InventoryCheckSummaryItemRowMapper implements RowMapper<InventoryCheckSummaryItem> {

		@Override
		public InventoryCheckSummaryItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("ID"));
			product.setCode(rs.getString("CODE"));
			product.setDescription(rs.getString("DESCRIPTION"));
			product.setFinalCost(rs.getString("UNIT"), rs.getBigDecimal("FINAL_COST"));

			InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
			item.setProduct(product);
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			return item;
		}
		
	}

	private static final String INSERT_SQL =
			"insert into INVENTORY_CHECK_SUMMARY_ITEM"
			+ " (INVENTORY_CHECK_ID, PRODUCT_ID, UNIT, QUANTITY, COST)"
			+ " values"
			+ " (?, ?, ?, ?, ?)";
	
	@Override
	public void save(InventoryCheckSummaryItem item) {
		getJdbcTemplate().update(INSERT_SQL,
				item.getParent().getId(),
				item.getProduct().getId(),
				item.getUnit(),
				item.getQuantity(),
				item.getProduct().getFinalCost(item.getUnit()));
	}
	
}
