package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.InventoryCheckDao;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.User;
import com.pj.magic.util.DbUtil;

@Repository
public class InventoryCheckDaoImpl extends MagicDao implements InventoryCheckDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, INVENTORY_DT, POST_IND, POST_DT, POST_BY,"
			+ " b.USERNAME as POST_BY_USERNAME"
			+ " from INVENTORY_CHECK a"
			+ " left join USER b"
			+ "   on b.ID = a.POST_BY";
	
	private InventoryCheckRowMapper inventoryCheckRowMapper = new InventoryCheckRowMapper();
	
	@Override
	public void save(InventoryCheck inventoryCheck) {
		if (inventoryCheck.getId() == null) {
			insert(inventoryCheck);
		} else {
			update(inventoryCheck);
		}
	}

	private static final String INSERT_SQL = "insert into INVENTORY_CHECK (INVENTORY_DT) values (?)";
	
	private void insert(final InventoryCheck inventoryCheck) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setDate(1, new java.sql.Date(inventoryCheck.getInventoryDate().getTime()));
				return ps;
			}
		}, holder);
		
		inventoryCheck.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update INVENTORY_CHECK set POST_IND = ?, POST_BY = ?, POST_DT = ? where ID = ?";
	
	private void update(InventoryCheck inventoryCheck) {
		getJdbcTemplate().update(UPDATE_SQL,
				inventoryCheck.isPosted() ? "Y" : "N",
				inventoryCheck.isPosted() ? inventoryCheck.getPostedBy().getId() : null,
				inventoryCheck.isPosted() ? inventoryCheck.getPostDate() : null,
				inventoryCheck.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public InventoryCheck get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, inventoryCheckRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by INVENTORY_DT desc";
	
	@Override
	public List<InventoryCheck> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, inventoryCheckRowMapper);
	}

	private class InventoryCheckRowMapper implements RowMapper<InventoryCheck> {

		@Override
		public InventoryCheck mapRow(ResultSet rs, int rowNum) throws SQLException {
			InventoryCheck inventoryCheck = new InventoryCheck();
			inventoryCheck.setId(rs.getLong("ID"));
			inventoryCheck.setInventoryDate(rs.getDate("INVENTORY_DT"));
			
			inventoryCheck.setPosted("Y".equals(rs.getString("POST_IND")));
			if (inventoryCheck.isPosted()) {
				inventoryCheck.setPostDate(rs.getTimestamp("POST_DT"));
				inventoryCheck.setPostedBy(new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			return inventoryCheck;
		}
		
	}

	@Override
	public List<InventoryCheck> search(InventoryCheck criteria) {
		List<Object> params = new ArrayList<>();
		StringBuilder sb = new StringBuilder(BASE_SELECT_SQL);
		sb.append(" where POST_IND = ?");
		params.add(criteria.isPosted() ? "Y" : "N");
		
		return getJdbcTemplate().query(sb.toString(), inventoryCheckRowMapper, params.toArray());
	}

	private static final String GET_MOST_RECENT_SQL = BASE_SELECT_SQL + " where post_ind = 'Y' order by id desc limit 1";
	
	@Override
	public InventoryCheck getMostRecent() {
		return getJdbcTemplate().queryForObject(GET_MOST_RECENT_SQL, inventoryCheckRowMapper);
	}

	private static final String FIND_BY_INVENTORY_DATE_SQL = BASE_SELECT_SQL + " where INVENTORY_DT = ?";
	
	@Override
	public InventoryCheck findByInventoryDate(Date inventoryDate) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_INVENTORY_DATE_SQL, inventoryCheckRowMapper, 
					DbUtil.toMySqlDateString(inventoryDate));
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}