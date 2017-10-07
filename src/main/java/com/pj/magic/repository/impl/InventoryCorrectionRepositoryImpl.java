package com.pj.magic.repository.impl;

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

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.InventoryCorrection;
import com.pj.magic.model.Product;
import com.pj.magic.model.User;
import com.pj.magic.repository.InventoryCorrectionRepository;
import com.pj.magic.util.DbUtil;

@Repository
public class InventoryCorrectionRepositoryImpl extends MagicDao implements InventoryCorrectionRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, INVENTORY_CORRECTION_NO, POST_DT, PRODUCT_ID, UNIT, QUANTITY, UPDATE_DT, UPDATE_BY, DELETE_IND, REMARKS"
			+ " , b.CODE as PRODUCT_CODE, b.DESCRIPTION as PRODUCT_DESCRIPTION"
			+ " , c.USERNAME as UPDATE_BY_USERNAME"
			+ " from INVENTORY_CORRECTION a"
			+ " join PRODUCT b"
			+ "	  on b.ID = a.PRODUCT_ID"
			+ " join USER c"
			+ "   on c.ID = a.UPDATE_BY";
	
	private static final String INVENTORY_CORRECTION_NUMBER_SEQUENCE = "INVENTORY_CORRECTION_NO_SEQ";
	
	private InventoryCorrectionRowMapper rowMapper = new InventoryCorrectionRowMapper();
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " where a.DELETE_IND = 'N' order by a.UPDATE_DT desc";
	
	@Override
	public List<InventoryCorrection> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public InventoryCorrection get(long id) {
		return getJdbcTemplate().queryForObject(GET_SQL, new Object[] {id}, rowMapper);
	}

	private static final String INSERT_SQL =
			"insert into INVENTORY_CORRECTION"
			+ " (INVENTORY_CORRECTION_NO, POST_DT, PRODUCT_ID, UNIT, QUANTITY, UPDATE_DT, UPDATE_BY, REMARKS)"
			+ " values"
			+ " (?, ?, ?, ?, ?, current_timestamp(), ?, ?)";

	@Override
	public void save(InventoryCorrection inventoryCorrection) {
		if (inventoryCorrection.isNew()) {
			insert(inventoryCorrection);
		} else {
			update(inventoryCorrection);
		}
	}
	
	private void insert(final InventoryCorrection inventoryCorrection) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextInventoryCorrectionNumber());
				ps.setDate(2, DbUtil.toSqlDate(inventoryCorrection.getPostDate()));
				ps.setLong(3, inventoryCorrection.getProduct().getId());
				ps.setString(4, inventoryCorrection.getUnit());
				ps.setInt(5, inventoryCorrection.getQuantity());
				ps.setLong(6, inventoryCorrection.getUpdatedBy().getId());
				ps.setString(7, inventoryCorrection.getRemarks());
				return ps;
			}
		}, holder);
		
		inventoryCorrection.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update INVENTORY_CORRECTION"
			+ " set POST_DT = ?, PRODUCT_ID = ?, UNIT = ?, QUANTITY = ?, UPDATE_DT = current_timestamp(), UPDATE_BY = ?, "
			+ " REMARKS = ?, DELETE_IND = ? where ID = ?";

	private void update(InventoryCorrection inventoryCorrection) {
		getJdbcTemplate().update(UPDATE_SQL,
				inventoryCorrection.getPostDate(),
				inventoryCorrection.getProduct().getId(),
				inventoryCorrection.getUnit(),
				inventoryCorrection.getQuantity(),
				inventoryCorrection.getUpdatedBy().getId(),
				inventoryCorrection.getRemarks(),
				inventoryCorrection.isDeleted() ? "Y" : "N",
				inventoryCorrection.getId());
	}

	protected Long getNextInventoryCorrectionNumber() {
		return getNextSequenceValue(INVENTORY_CORRECTION_NUMBER_SEQUENCE);
	}

	private class InventoryCorrectionRowMapper implements RowMapper<InventoryCorrection> {

		@Override
		public InventoryCorrection mapRow(ResultSet rs, int rowNum) throws SQLException {
			InventoryCorrection inventoryCorrection = new InventoryCorrection();
			inventoryCorrection.setId(rs.getLong("ID"));
			inventoryCorrection.setInventoryCorrectionNumber(rs.getLong("INVENTORY_CORRECTION_NO"));
			inventoryCorrection.setPostDate(rs.getTimestamp("POST_DT"));
			inventoryCorrection.setProduct(mapProduct(rs));
			inventoryCorrection.setUnit(rs.getString("UNIT"));
			inventoryCorrection.setQuantity(rs.getInt("QUANTITY"));
			inventoryCorrection.setUpdateDate(rs.getTimestamp("UPDATE_DT"));
			inventoryCorrection.setUpdatedBy(new User(rs.getLong("UPDATE_BY"), rs.getString("UPDATE_BY_USERNAME")));
			inventoryCorrection.setRemarks(rs.getString("REMARKS"));
			inventoryCorrection.setDeleted("Y".equals(rs.getString("DELETE_IND")));
			return inventoryCorrection;
		}

		private Product mapProduct(ResultSet rs) throws SQLException {
			Product product = new Product();
			product.setId(rs.getLong("PRODUCT_ID"));
			product.setCode(rs.getString("PRODUCT_CODE"));
			product.setDescription(rs.getString("PRODUCT_DESCRIPTION"));
			return product;
		}
		
	}

}
