package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.AreaInventoryReportItemDao;
import com.pj.magic.model.Area;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.AreaInventoryReportItemSearchCriteria;

@Repository
public class AreaInventoryReportItemDaoImpl extends MagicDao implements AreaInventoryReportItemDao {
	
	private static final String BASE_SELECT_SQL =
			"select a.ID, AREA_INV_REPORT_ID, PRODUCT_ID, UNIT, QUANTITY,"
			+ " b.REPORT_NO, b.AREA_ID,"
			+ " c.NAME as AREA_NAME"
			+ " from AREA_INV_REPORT_ITEM a"
			+ " join AREA_INV_REPORT b"
			+ "   on b.ID = a.AREA_INV_REPORT_ID"
			+ " left join AREA c"
			+ "   on c.ID = b.AREA_ID"
			+ " where 1 = 1";

	private AreaInventoryReportItemRowMapper rowMapper = new AreaInventoryReportItemRowMapper();
	
	@Override
	public void save(AreaInventoryReportItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into AREA_INV_REPORT_ITEM (AREA_INV_REPORT_ID, PRODUCT_ID, UNIT, QUANTITY)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(final AreaInventoryReportItem item) {
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
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}
	
	private static final String UPDATE_SQL =
			"update AREA_INV_REPORT_ITEM"
			+ " set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?"
			+ " where ID = ?";
	
	private void update(AreaInventoryReportItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getProduct().getId(), item.getUnit(),
				item.getQuantity(), item.getId());
	}

	private static final String FIND_ALL_BY_AREA_INVENTORY_REPORT_SQL =
			BASE_SELECT_SQL + " and AREA_INV_REPORT_ID = ?";
	
	@Override
	public List<AreaInventoryReportItem> findAllByAreaInventoryReport(final AreaInventoryReport areaInventoryReport) {
		return getJdbcTemplate().query(FIND_ALL_BY_AREA_INVENTORY_REPORT_SQL, rowMapper, 
				areaInventoryReport.getId());
	}

	private static final String DELETE_SQL = "delete from AREA_INV_REPORT_ITEM where ID = ?";
	
	@Override
	public void delete(AreaInventoryReportItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_AREA_INVENTORY_REPORT_SQL =
			"delete from AREA_INV_REPORT_ITEM where AREA_INV_REPORT_ID = ?";
	
	@Override
	public void deleteAllByAreaInventoryReport(AreaInventoryReport areaInventoryReport) {
		getJdbcTemplate().update(DELETE_ALL_BY_AREA_INVENTORY_REPORT_SQL, areaInventoryReport.getId());
	}

	@Override
	public List<AreaInventoryReportItem> search(AreaInventoryReportItemSearchCriteria criteria) {
		StringBuilder sb = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		if (criteria.getInventoryCheck() != null) {
			sb.append(" and exists (select 1 from INVENTORY_CHECK d where d.ID = b.INVENTORY_CHECK_ID and d.ID = ?)");
			params.add(criteria.getInventoryCheck().getId());
		}
		if (criteria.getProduct() != null) {
			sb.append(" and a.PRODUCT_ID = ?");
			params.add(criteria.getProduct().getId());
		}
		if (!StringUtils.isEmpty(criteria.getUnit())) {
			sb.append(" and a.UNIT = ?");
			params.add(criteria.getUnit());
		}
		
		sb.append(" order by b.REPORT_NO");
		
		return getJdbcTemplate().query(sb.toString(), rowMapper, params.toArray());
	}
	
	private class AreaInventoryReportItemRowMapper implements RowMapper<AreaInventoryReportItem> {

		@Override
		public AreaInventoryReportItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			AreaInventoryReportItem item = new AreaInventoryReportItem();
			item.setId(rs.getLong("ID"));
			
			AreaInventoryReport parent = new AreaInventoryReport();
			parent.setId(rs.getLong("AREA_INV_REPORT_ID"));
			parent.setReportNumber(rs.getInt("REPORT_NO"));
			if (rs.getLong("AREA_ID") != 0) {
				parent.setArea(new Area(rs.getLong("AREA_ID"), rs.getString("AREA_NAME")));
			}
			item.setParent(parent);
			
			item.setProduct(new Product(rs.getLong("PRODUCT_ID")));
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			return item;
		}
		
	}

	private static final String FIND_FIRST_BY_PRODUCT_SQL = BASE_SELECT_SQL
			+ " and a.PRODUCT_ID = ? limit 1";
	
	@Override
	public AreaInventoryReportItem findFirstByProduct(Product product) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_BY_PRODUCT_SQL,
					rowMapper, product.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}
