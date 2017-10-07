package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.AdjustmentOutDao;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.User;
import com.pj.magic.model.search.AdjustmentOutSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class AdjustmentOutDaoImpl extends MagicDao implements AdjustmentOutDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, ADJUSTMENT_OUT_NO, a.REMARKS, POST_IND, POST_DT, POSTED_BY, PILFERAGE_IND,"
			+ " b.USERNAME as POSTED_BY_USERNAME"
			+ " from ADJUSTMENT_OUT a"
			+ " left join USER b"
			+ "   on b.ID = a.POSTED_BY";
	
	private static final String ADJUSTMENT_OUT_NUMBER_SEQUENCE = "ADJUSTMENT_OUT_NO_SEQ";
	
	private AdjustmentOutRowMapper adjustmentOutRowMapper = new AdjustmentOutRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public AdjustmentOut get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, adjustmentOutRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(AdjustmentOut adjustmentOut) {
		if (adjustmentOut.getId() == null) {
			insert(adjustmentOut);
		} else {
			update(adjustmentOut);
		}
	}
	
	private static final String INSERT_SQL =
			"insert into ADJUSTMENT_OUT"
			+ " (ADJUSTMENT_OUT_NO, REMARKS)"
			+ " values (?, ?)";
	
	private void insert(final AdjustmentOut adjustmentOut) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextAdjustmentOutNumber());
				ps.setString(2, adjustmentOut.getRemarks());
				return ps;
			}
		}, holder);
		
		AdjustmentOut updated = get(holder.getKey().longValue());
		adjustmentOut.setId(updated.getId());
		adjustmentOut.setAdjustmentOutNumber(updated.getAdjustmentOutNumber());
	}
	
	private long getNextAdjustmentOutNumber() {
		return getNextSequenceValue(ADJUSTMENT_OUT_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL =
			"update ADJUSTMENT_OUT"
			+ " set REMARKS = ?, POST_IND = ?, POST_DT = ?, POSTED_BY = ?, PILFERAGE_IND = ?"
			+ " where ID = ?";
	
	private void update(AdjustmentOut adjustmentOut) {
		getJdbcTemplate().update(UPDATE_SQL, adjustmentOut.getRemarks(), 
				adjustmentOut.isPosted() ? "Y" : "N",
				adjustmentOut.getPostDate(),
				adjustmentOut.isPosted() ? adjustmentOut.getPostedBy().getId() : null,
				adjustmentOut.getPilferageFlag() ? "Y" : "N",
				adjustmentOut.getId());
	}
	
	private class AdjustmentOutRowMapper implements RowMapper<AdjustmentOut> {

		@Override
		public AdjustmentOut mapRow(ResultSet rs, int rowNum) throws SQLException {
			AdjustmentOut adjustmentOut = new AdjustmentOut();
			adjustmentOut.setId(rs.getLong("ID"));
			adjustmentOut.setAdjustmentOutNumber(rs.getLong("ADJUSTMENT_OUT_NO"));
			adjustmentOut.setRemarks(rs.getString("REMARKS"));
			adjustmentOut.setPosted("Y".equals(rs.getString("POST_IND")));
			adjustmentOut.setPostDate(rs.getTimestamp("POST_DT"));
			if (rs.getLong("POSTED_BY") != 0) {
				User user = new User();
				user.setId(rs.getLong("POSTED_BY"));
				user.setUsername(rs.getString("POSTED_BY_USERNAME"));
				adjustmentOut.setPostedBy(user);
			}
			
			adjustmentOut.setPilferageFlag("Y".equals(rs.getString("PILFERAGE_IND")));
			
			return adjustmentOut;
		}
	}

	private static final String DELETE_SQL = "delete from ADJUSTMENT_OUT where ID = ?";
	
	@Override
	public void delete(AdjustmentOut adjustmentOut) {
		getJdbcTemplate().update(DELETE_SQL, adjustmentOut.getId());
	}

	@Override
	public List<AdjustmentOut> search(AdjustmentOutSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sb = new StringBuilder(BASE_SELECT_SQL);
		sb.append(" where 1 = 1");
		
		if (criteria.getAdjustmentOutNumber() != null) {
			sb.append(" and ADJUSTMENT_OUT_NO = ?");
			params.add(criteria.getAdjustmentOutNumber());
		}
		
		if (criteria.getPosted() != null) {
			sb.append(" and POST_IND = ?");
			params.add(criteria.getPosted() ? "Y" : "N");
		}
		
		if (criteria.getPostDate() != null) {
			sb.append(" and POST_DT >= ? and POST_DT < date_add(?, interval 1 day)");
			
			String postDateString = DbUtil.toMySqlDateString(criteria.getPostDate());
			params.add(postDateString);
			params.add(postDateString);
		}
		
		sb.append(" order by ADJUSTMENT_OUT_NO desc");
		
		return getJdbcTemplate().query(sb.toString(), adjustmentOutRowMapper, params.toArray());
	}

}