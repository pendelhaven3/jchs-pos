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
			" select a.ID, a.CODE, a.DESCRIPTION, a.UNIT, a.BEGINNING_INV, a.FINAL_COST, b.ACTUAL_COUNT"
			+ " from ("
			+ "   select ID, CODE, DESCRIPTION, 'CSE' as UNIT,"
			+ "   AVAIL_QTY_CSE as BEGINNING_INV, FINAL_COST_CSE as FINAL_COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_CSE = 'Y'"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'TIE' as UNIT,"
			+ "   AVAIL_QTY_TIE as BEGINNING_INV, FINAL_COST_TIE as FINAL_COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_TIE = 'Y'"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'CTN' as UNIT,"
			+ "   AVAIL_QTY_CTN as BEGINNING_INV, FINAL_COST_CTN as FINAL_COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_CTN = 'Y'"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'DOZ' as UNIT,"
			+ "   AVAIL_QTY_DOZ as BEGINNING_INV, FINAL_COST_DOZ as FINAL_COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_DOZ = 'Y'"
			+ "   union all"
			+ "   select ID, CODE, DESCRIPTION, 'PCS' as UNIT,"
			+ "   AVAIL_QTY_PCS as BEGINNING_INV, FINAL_COST_PCS as FINAL_COST"
			+ "   from PRODUCT"
			+ "   where UNIT_IND_PCS = 'Y'"
			+ " ) a"
			+ " left join ("
			+ "   select airi.PRODUCT_ID, airi.UNIT, sum(airi.QUANTITY) as ACTUAL_COUNT"
			+ "   from AREA_INV_REPORT air"
			+ "   join AREA_INV_REPORT_ITEM airi"
			+ "     on airi.AREA_INV_REPORT_ID = air.ID"
			+ "   where air.INVENTORY_CHECK_ID = ?"
			+ "   group by airi.PRODUCT_ID, airi.UNIT"
			+ " ) b"
			+ "   on b.PRODUCT_ID = a.ID"
			+ "   and b.UNIT = a.UNIT"
			+ " order by CODE";
	
	@Override
	public List<InventoryCheckSummaryItem> findAllByInventoryCheck(InventoryCheck inventoryCheck) {
		return getJdbcTemplate().query(FIND_ALL_BY_INVENTORY_CHECK_SQL, rowMapper, inventoryCheck.getId());
	}

	private static final String FIND_ALL_BY_POSTED_INVENTORY_CHECK_SQL =
			" select b.ID, b.CODE, b.DESCRIPTION, a.UNIT, a.BEGINNING_INV, a.COST as FINAL_COST, a.ACTUAL_COUNT" 
			+ " from INVENTORY_CHECK_SUMMARY_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " where a.INVENTORY_CHECK_ID = ?"
			+ " order by b.CODE";
	
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
			product.addUnit(rs.getString("UNIT"));
			product.addUnitQuantity(rs.getString("UNIT"), rs.getInt("BEGINNING_INV"));
			product.setFinalCost(rs.getString("UNIT"), rs.getBigDecimal("FINAL_COST"));

			InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
			item.setProduct(product);
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("ACTUAL_COUNT"));
			return item;
		}
		
	}

	private static final String INSERT_SQL =
			"insert into INVENTORY_CHECK_SUMMARY_ITEM"
			+ " (INVENTORY_CHECK_ID, PRODUCT_ID, UNIT, BEGINNING_INV, ACTUAL_COUNT, COST)"
			+ " values"
			+ " (?, ?, ?, ?, ?, ?)";
	
	@Override
	public void save(InventoryCheckSummaryItem item) {
		getJdbcTemplate().update(INSERT_SQL,
				item.getParent().getId(),
				item.getProduct().getId(),
				item.getUnit(),
				item.getProduct().getUnitQuantity(item.getUnit()),
				item.getQuantity(),
				item.getProduct().getFinalCost(item.getUnit()));
	}
	
}
