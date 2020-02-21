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

import com.pj.magic.dao.AreaInventoryReportDao;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.search.AreaInventoryReportSearchCriteria;

@Repository
public class AreaInventoryReportDaoImpl extends MagicDao implements AreaInventoryReportDao {

	private static final String BASE_SELECT_SQL =
			"select a.ID, INVENTORY_CHECK_ID, REPORT_NO, AREA, CHECKER, DOUBLE_CHECKER,"
			+ " a.REVIEW_IND, a.REVIEWER,"
			+ " b.INVENTORY_DT, b.POST_IND"
			+ " from AREA_INV_REPORT a"
			+ " join INVENTORY_CHECK b"
			+ "   on b.ID = a.INVENTORY_CHECK_ID";
	
	private AreaInventoryReportRowMapper areaInventoryReportRowMapper = new AreaInventoryReportRowMapper();
	
	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public AreaInventoryReport get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, areaInventoryReportRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	@Override
	public void save(AreaInventoryReport areaInventoryReport) {
		if (areaInventoryReport.getId() == null) {
			insert(areaInventoryReport);
		} else {
			update(areaInventoryReport);
		}
	}

	private static final String INSERT_SQL =
			"insert into AREA_INV_REPORT"
			+ " (INVENTORY_CHECK_ID, REPORT_NO, AREA, CHECKER, DOUBLE_CHECKER, REVIEWER)"
			+ " values (?, ?, ?, ?, ?, ?)";
	
	private void insert(final AreaInventoryReport areaInventoryReport) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, areaInventoryReport.getParent().getId());
				ps.setInt(2, areaInventoryReport.getReportNumber());
				ps.setString(3, areaInventoryReport.getArea());
				ps.setString(4, areaInventoryReport.getChecker());
				ps.setString(5, areaInventoryReport.getDoubleChecker());
				ps.setString(6, areaInventoryReport.getReviewer());
				return ps;
			}
		}, holder);
		
		areaInventoryReport.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update AREA_INV_REPORT"
			+ " set REPORT_NO = ?, AREA = ?, CHECKER = ?, DOUBLE_CHECKER = ?, REVIEW_IND = ?,"
			+ " REVIEWER = ? where ID = ?";
	
	private void update(AreaInventoryReport areaInventoryReport) {
		getJdbcTemplate().update(UPDATE_SQL,
				areaInventoryReport.getReportNumber(),
				areaInventoryReport.getArea(),
				areaInventoryReport.getChecker(),
				areaInventoryReport.getDoubleChecker(),
				(areaInventoryReport.isReviewed()) ? "Y" : "N",
				areaInventoryReport.getReviewer(),
				areaInventoryReport.getId());
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by ID desc";
	
	@Override
	public List<AreaInventoryReport> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, areaInventoryReportRowMapper);
	}

	private class AreaInventoryReportRowMapper implements RowMapper<AreaInventoryReport> {

		@Override
		public AreaInventoryReport mapRow(ResultSet rs, int rowNum) throws SQLException {
			AreaInventoryReport areaInventoryReport = new AreaInventoryReport();
			areaInventoryReport.setId(rs.getLong("ID"));
			
			InventoryCheck parent = new InventoryCheck();
			parent.setId(rs.getLong("INVENTORY_CHECK_ID"));
			parent.setInventoryDate(rs.getDate("INVENTORY_DT"));
			parent.setPosted("Y".equals(rs.getString("POST_IND")));
			areaInventoryReport.setParent(parent);
			
			areaInventoryReport.setReportNumber(rs.getInt("REPORT_NO"));
			areaInventoryReport.setArea(rs.getString("AREA"));
			areaInventoryReport.setChecker(rs.getString("CHECKER"));
			areaInventoryReport.setDoubleChecker(rs.getString("DOUBLE_CHECKER"));
			areaInventoryReport.setReviewed("Y".equals(rs.getString("REVIEW_IND")));
			areaInventoryReport.setReviewer(rs.getString("REVIEWER"));
			return areaInventoryReport;
		}
		
	}

	private static final String FIND_BY_INVENTORY_CHECK_AND_REPORT_NO = 
			BASE_SELECT_SQL + " where b.ID = ? and a.REPORT_NO = ?";
	
	@Override
	public AreaInventoryReport findByInventoryCheckAndReportNumber(
			InventoryCheck inventoryCheck, int reportNumber) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_INVENTORY_CHECK_AND_REPORT_NO, 
					areaInventoryReportRowMapper, inventoryCheck.getId(), reportNumber);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String FIND_ALL_BY_INVENTORY_CHECK_SQL = BASE_SELECT_SQL
			+ " where a.INVENTORY_CHECK_ID = ?";
	
	@Override
	public List<AreaInventoryReport> findAllByInventoryCheck(InventoryCheck inventoryCheck) {
		return getJdbcTemplate().query(FIND_ALL_BY_INVENTORY_CHECK_SQL, areaInventoryReportRowMapper,
				inventoryCheck.getId());
	}

	@Override
	public List<AreaInventoryReport> search(AreaInventoryReportSearchCriteria criteria) {
		StringBuilder sql = new StringBuilder(BASE_SELECT_SQL);
		List<Object> params = new ArrayList<>();
		
		if (criteria.getInventoryCheck() != null) {
			sql.append(" and a.INVENTORY_CHECK_ID = ?");
			params.add(criteria.getInventoryCheck().getId());
		}
		
		if (criteria.getReviewed() != null) {
			sql.append(" and a.REVIEW_IND = ?");
			params.add(criteria.getReviewed() ? "Y" : "N");
		}
		
		sql.append(" order by b.INVENTORY_DT, a.REPORT_NO");
		
		return getJdbcTemplate().query(sql.toString(), areaInventoryReportRowMapper, params.toArray());
	}
	
}