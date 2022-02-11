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
import com.pj.magic.model.TrisysSales;
import com.pj.magic.model.TrisysSalesItem;
import com.pj.magic.repository.TrisysSalesItemRepository;

@Repository
public class TrisysSalesItemRepositoryImpl extends MagicDao implements TrisysSalesItemRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.TRISYS_SALES_ID, a.PRODUCT_CODE, a.QUANTITY, a.UNIT_COST, a.SELL_PRICE,"
			+ " b.DESCRIPTION as PRODUCT_DESCRIPTION, b.UOM_CODE as UNIT"
			+ " from TRISYS_SALES_ITEM a"
			+ " left join PRODUCT b"
			+ "   on b.CODE = a.PRODUCT_CODE";

	private RowMapper<TrisysSalesItem> rowMapper = new RowMapper<TrisysSalesItem>() {

		@Override
		public TrisysSalesItem mapRow(ResultSet rs, int rowNum) throws SQLException {
			TrisysSalesItem item = new TrisysSalesItem();
			item.setId(rs.getLong("ID"));
			item.setSales(new TrisysSales(rs.getLong("TRISYS_SALES_ID")));
			item.setProductCode(rs.getString("PRODUCT_CODE"));
			item.setQuantity(rs.getInt("QUANTITY"));
			item.setUnitCost(rs.getBigDecimal("UNIT_COST"));
			item.setSellPrice(rs.getBigDecimal("SELL_PRICE"));
			item.setProductDescription(rs.getString("PRODUCT_DESCRIPTION"));
			item.setUnit(rs.getString("UNIT"));
			return item;
		}
	};
	
	private static final String FIND_ALL_BY_TRISYS_SALES_SQL = BASE_SELECT_SQL
			+ " where a.TRISYS_SALES_ID = ? order by b.DESCRIPTION, a.PRODUCT_CODE";

	@Override
	public List<TrisysSalesItem> findAllByTrisysSales(TrisysSales sales) {
		return getJdbcTemplate().query(FIND_ALL_BY_TRISYS_SALES_SQL, rowMapper, sales.getId());
	}
	
	private static final String INSERT_SQL =
			"insert into TRISYS_SALES_ITEM"
			+ " (TRISYS_SALES_ID, PRODUCT_CODE, QUANTITY, UNIT_COST, SELL_PRICE)"
			+ " values"
			+ " (?, ?, ?, ?, ?)";

	@Override
	public void save(TrisysSalesItem item) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, item.getSales().getId());
				ps.setString(2, item.getProductCode());
				ps.setInt(3, item.getQuantity());
				ps.setBigDecimal(4, item.getUnitCost());
				ps.setBigDecimal(5, item.getSellPrice());
				return ps;
			}
		}, holder);
		
		item.setId(holder.getKey().longValue());
	}
	
}
