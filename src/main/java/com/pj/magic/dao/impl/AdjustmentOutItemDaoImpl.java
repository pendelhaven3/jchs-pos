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

import com.pj.magic.dao.AdjustmentOutItemDao;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.Product2;

@Repository
public class AdjustmentOutItemDaoImpl extends MagicDao implements AdjustmentOutItemDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.ADJUSTMENT_OUT_ID, a.PRODUCT_ID, a.UNIT, a.QUANTITY, a.UNIT_PRICE, b.CODE"
			+ " from ADJUSTMENT_OUT_ITEM a"
			+ " join PRODUCT b"
			+ "   on b.PRODUCT2_ID = a.PRODUCT_ID"
			+ "   and b.UOM_CODE = a.UNIT";
	
	private RowMapper<AdjustmentOutItem> rowMapper = new RowMapper<AdjustmentOutItem>() {

		@Override
		public AdjustmentOutItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			AdjustmentOutItem item = new AdjustmentOutItem();
			item.setId(rs.getLong("ID"));
			item.setParent(new AdjustmentOut(rs.getLong("ADJUSTMENT_OUT_ID")));
			item.setProduct(new Product2(rs.getLong("PRODUCT_ID")));
			item.setUnit(rs.getString("UNIT"));
			item.setCode(rs.getString("CODE"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setUnitPrice(rs.getBigDecimal("UNIT_PRICE"));
			return item;
		}
		
	};
	
	@Override
	public void save(AdjustmentOutItem item) {
		if (item.getId() == null) {
			insert(item);
		} else {
			update(item);
		}
	}

	private static final String INSERT_SQL =
			"insert into ADJUSTMENT_OUT_ITEM (ADJUSTMENT_OUT_ID, PRODUCT_ID, UNIT, QUANTITY)"
			+ " values (?, ?, ?, ?)";
	
	private void insert(final AdjustmentOutItem item) {
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
			"update ADJUSTMENT_OUT_ITEM"
			+ " set PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, UNIT_PRICE = ?"
			+ " where ID = ?";
	
	private void update(AdjustmentOutItem item) {
		getJdbcTemplate().update(UPDATE_SQL, item.getProduct().getId(), item.getUnit(),
				item.getQuantity(), item.getUnitPrice(), item.getId());
	}

	private static final String FIND_ALL_BY_ADJUSTMENT_OUT_SQL = BASE_SELECT_SQL
			+ " where ADJUSTMENT_OUT_ID = ?";
	
	@Override
	public List<AdjustmentOutItem> findAllByAdjustmentOut(AdjustmentOut adjustmentOut) {
		List<AdjustmentOutItem> items = getJdbcTemplate().query(FIND_ALL_BY_ADJUSTMENT_OUT_SQL, rowMapper, adjustmentOut.getId());
		for (AdjustmentOutItem item : items) {
			item.setParent(adjustmentOut);
		}
		return items;
	}

	private static final String DELETE_SQL = "delete from ADJUSTMENT_OUT_ITEM where ID = ?";
	
	@Override
	public void delete(AdjustmentOutItem item) {
		getJdbcTemplate().update(DELETE_SQL, item.getId());
	}

	private static final String DELETE_ALL_BY_ADJUSTMENT_OUT_SQL =
			"delete from ADJUSTMENT_OUT_ITEM where ADJUSTMENT_OUT_ID = ?";
	
	@Override
	public void deleteAllByAdjustmentOut(AdjustmentOut adjustmentOut) {
		getJdbcTemplate().update(DELETE_ALL_BY_ADJUSTMENT_OUT_SQL, adjustmentOut.getId());
	}

	private static final String FIND_FIRST_BY_PRODUCT_SQL = BASE_SELECT_SQL
			+ " where PRODUCT_ID = ? limit 1";
	
	@Override
	public AdjustmentOutItem findFirstByProduct(Product product) {
		try {
			return getJdbcTemplate().queryForObject(FIND_FIRST_BY_PRODUCT_SQL, rowMapper, product.getId());
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}
