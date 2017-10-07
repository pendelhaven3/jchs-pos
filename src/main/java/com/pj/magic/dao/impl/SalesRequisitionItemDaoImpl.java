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

import com.pj.magic.dao.SalesRequisitionItemDao;
import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Product;
import com.pj.magic.model.SalesRequisition;
import com.pj.magic.model.SalesRequisitionItem;
import com.pj.magic.model.Unit;

@Repository
public class SalesRequisitionItemDaoImpl extends MagicDao implements SalesRequisitionItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, SALES_REQUISITION_ID, a.PRODUCT_ID, UNIT, QUANTITY,"
			+ " c.UNIT_IND_CSE, c.UNIT_IND_TIE, c.UNIT_IND_CTN, c.UNIT_IND_DOZ, c.UNIT_IND_PCS,"
			+ " d.UNIT_PRICE_CSE, d.UNIT_PRICE_TIE, d.UNIT_PRICE_CTN, d.UNIT_PRICE_DOZ, d.UNIT_PRICE_PCS,"
			+ " c.MANUFACTURER_ID, e.NAME as MANUFACTURER_NAME"
			+ " from SALES_REQUISITION_ITEM a"
			+ " join SALES_REQUISITION b"
			+ "   on b.ID = a.SALES_REQUISITION_ID"
			+ " join PRODUCT c"
			+ "   on c.ID = a.PRODUCT_ID"
			+ " join PRODUCT_PRICE d"
			+ "   on d.PRODUCT_ID = c.ID"
			+ "   and d.PRICING_SCHEME_ID = b.PRICING_SCHEME_ID"
			+ " left join MANUFACTURER e"
			+ "   on e.ID = c.MANUFACTURER_ID";

	private SalesRequisitionItemRowMapper salesRequisitionItemRowMapper =
			new SalesRequisitionItemRowMapper();
	
	@Override
	public void save(SalesRequisitionItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into SALES_REQUISITION_ITEM (SALES_REQUISITION_ID, PRODUCT_ID, UNIT, QUANTITY)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(final SalesRequisitionItem item) {
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
			"update SALES_REQUISITION_ITEM"
			+ " set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?"
			+ " where ID = ?";
	
	private void update(SalesRequisitionItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getProduct().getId(), item.getUnit(),
				item.getQuantity(), item.getId());
	}

	private static final String FIND_ALL_BY_SALES_REQUISITION_SQL = BASE_SELECT_SQL
			+ " where SALES_REQUISITION_ID = ?";
	
	@Override
	public List<SalesRequisitionItem> findAllBySalesRequisition(SalesRequisition salesRequisition) {
		List<SalesRequisitionItem> items = getJdbcTemplate().query(FIND_ALL_BY_SALES_REQUISITION_SQL, 
				salesRequisitionItemRowMapper, salesRequisition.getId());
		for (SalesRequisitionItem item : items) {
			item.setParent(salesRequisition);
		}
		return items;
	}

	private static final String DELETE_SQL = "delete from SALES_REQUISITION_ITEM where ID = ?";
	
	@Override
	public void delete(SalesRequisitionItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_SALES_REQUISITION_SQL =
			"delete from SALES_REQUISITION_ITEM where SALES_REQUISITION_ID = ?";
	
	@Override
	public void deleteAllBySalesRequisition(SalesRequisition salesRequisition) {
		getJdbcTemplate().update(DELETE_ALL_BY_SALES_REQUISITION_SQL, salesRequisition.getId());
	}

	private static final String FIND_FIRST_BY_PRODUCT_SQL = BASE_SELECT_SQL
			+ " where PRODUCT_ID = ? limit 1";
	
	@Override
	public SalesRequisitionItem findFirstByProduct(Product product) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_BY_PRODUCT_SQL, salesRequisitionItemRowMapper,
					product.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private class SalesRequisitionItemRowMapper implements RowMapper<SalesRequisitionItem> {

		@Override
		public SalesRequisitionItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			SalesRequisitionItem item = new SalesRequisitionItem();
			item.setId(rs.getLong("ID"));
			item.setParent(new SalesRequisition(rs.getLong("SALES_REQUISITION_ID")));
			item.setProduct(mapProduct(rs));
			item.setUnit(rs.getString("UNIT"));
			item.setQuantity(rs.getInt("QUANTITY"));
			return item;
		}
		
		private Product mapProduct(ResultSet rs) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			if ("Y".equals(rs.getString("UNIT_IND_CSE"))) {
				product.addUnit(Unit.CASE);
				product.setUnitPrice(Unit.CASE, rs.getBigDecimal("UNIT_PRICE_CSE"));
			}
			if ("Y".equals(rs.getString("UNIT_IND_TIE"))) {
				product.addUnit(Unit.TIE);
				product.setUnitPrice(Unit.TIE, rs.getBigDecimal("UNIT_PRICE_TIE"));
			}
			if ("Y".equals(rs.getString("UNIT_IND_CTN"))) {
				product.addUnit(Unit.CARTON);
				product.setUnitPrice(Unit.CARTON, rs.getBigDecimal("UNIT_PRICE_CTN"));
			}
			if ("Y".equals(rs.getString("UNIT_IND_DOZ"))) {
				product.addUnit(Unit.DOZEN);
				product.setUnitPrice(Unit.DOZEN, rs.getBigDecimal("UNIT_PRICE_DOZ"));
			}
			if ("Y".equals(rs.getString("UNIT_IND_PCS"))) {
				product.addUnit(Unit.PIECES);
				product.setUnitPrice(Unit.PIECES, rs.getBigDecimal("UNIT_PRICE_PCS"));
			}
			
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