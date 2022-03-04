package com.pj.magic.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.InventoryCheckSummaryItemDao;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Product2;

@Repository
public class InventoryCheckSummaryItemDaoImpl extends MagicDao implements InventoryCheckSummaryItemDao {

	private InventoryCheckSummaryItemRowMapper rowMapper = new InventoryCheckSummaryItemRowMapper();
	
	private static final String FIND_ALL_BY_INVENTORY_CHECK_SQL =
			" select a.ID, a.CODE, a.DESCRIPTION, a.UNIT, a.BEGINNING_INV, a.FINAL_COST, b.ACTUAL_COUNT"
			+ " from ("
			+ "   select p2.ID, p1.CODE, p2.DESCRIPTION, 'CASE' as UNIT,"
			+ "   AVAIL_QTY_CASE as BEGINNING_INV, FINAL_COST_CASE as FINAL_COST"
			+ "   from PRODUCT2 p2"
			+ "   join PRODUCT p1"
			+ "     on p1.PRODUCT2_ID = p2.ID"
			+ "     and p1.UOM_CODE = 'CASE'"
			+ "   where UNIT_IND_CASE = 'Y'"
			+ "   union all"
			+ "   select p2.ID, p1.CODE, p2.DESCRIPTION, 'TIES' as UNIT,"
			+ "   AVAIL_QTY_TIES as BEGINNING_INV, FINAL_COST_TIES as FINAL_COST"
			+ "   from PRODUCT2 p2"
			+ "   join PRODUCT p1"
			+ "     on p1.PRODUCT2_ID = p2.ID"
			+ "     and p1.UOM_CODE = 'TIES'"
			+ "   where UNIT_IND_TIES = 'Y'"
			+ "   union all"
			+ "   select p2.ID, p1.CODE, p2.DESCRIPTION, 'PACK' as UNIT,"
			+ "   AVAIL_QTY_PACK as BEGINNING_INV, FINAL_COST_PACK as FINAL_COST"
			+ "   from PRODUCT2 p2"
			+ "   join PRODUCT p1"
			+ "     on p1.PRODUCT2_ID = p2.ID"
			+ "     and p1.UOM_CODE = 'PACK'"
			+ "   where UNIT_IND_PACK = 'Y'"
			+ "   union all"
			+ "   select p2.ID, p1.CODE, p2.DESCRIPTION, 'HDZN' as UNIT,"
			+ "   AVAIL_QTY_HDZN as BEGINNING_INV, FINAL_COST_HDZN as FINAL_COST"
			+ "   from PRODUCT2 p2"
			+ "   join PRODUCT p1"
			+ "     on p1.PRODUCT2_ID = p2.ID"
			+ "     and p1.UOM_CODE = 'HDZN'"
			+ "   where UNIT_IND_HDZN = 'Y'"
			+ "   union all"
			+ "   select p2.ID, p1.CODE, p2.DESCRIPTION, 'PCS' as UNIT,"
			+ "   AVAIL_QTY_PCS as BEGINNING_INV, FINAL_COST_PCS as FINAL_COST"
			+ "   from PRODUCT2 p2"
			+ "   join PRODUCT p1"
			+ "     on p1.PRODUCT2_ID = p2.ID"
			+ "     and p1.UOM_CODE = 'PCS'"
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
			+ " order by DESCRIPTION";
	
	@Override
	public List<InventoryCheckSummaryItem> findAllByInventoryCheck(InventoryCheck inventoryCheck) {
		return getJdbcTemplate().query(FIND_ALL_BY_INVENTORY_CHECK_SQL, rowMapper, inventoryCheck.getId());
	}

	private static final String FIND_ALL_BY_POSTED_INVENTORY_CHECK_SQL =
			" select b.ID, c.CODE, b.DESCRIPTION, a.UNIT, a.BEGINNING_INV, a.COST as FINAL_COST, a.ACTUAL_COUNT" 
			+ " from INVENTORY_CHECK_SUMMARY_ITEM a"
			+ " join PRODUCT2 b"
			+ "   on b.ID = a.PRODUCT_ID"
			+ " join PRODUCT c"
			+ "   on c.PRODUCT2_ID = b.ID"
			+ "   and c.UOM_CODE = a.UNIT"
			+ " where a.INVENTORY_CHECK_ID = ?"
			+ " order by b.DESCRIPTION";
	
	@Override
	public List<InventoryCheckSummaryItem> findAllByPostedInventoryCheck(InventoryCheck inventoryCheck) {
		return getJdbcTemplate().query(FIND_ALL_BY_POSTED_INVENTORY_CHECK_SQL,
				rowMapper, inventoryCheck.getId());
	}

	private class InventoryCheckSummaryItemRowMapper implements RowMapper<InventoryCheckSummaryItem> {

		@Override
		public InventoryCheckSummaryItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			Product2 product = new Product2();
			product.setId(rs.getLong("ID"));
			product.setDescription(rs.getString("DESCRIPTION"));
			product.addUnit(rs.getString("UNIT"));
			product.addUnitQuantity(rs.getString("UNIT"), rs.getInt("BEGINNING_INV"));
			product.setFinalCost(rs.getString("UNIT"), rs.getBigDecimal("FINAL_COST"));

			InventoryCheckSummaryItem item = new InventoryCheckSummaryItem();
			item.setProduct(product);
			item.setUnit(rs.getString("UNIT"));
			item.setCode(rs.getString("CODE"));
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
