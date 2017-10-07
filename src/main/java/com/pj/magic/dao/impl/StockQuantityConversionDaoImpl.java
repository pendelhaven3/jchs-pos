package com.pj.magic.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.StockQuantityConversionDao;
import com.pj.magic.model.StockQuantityConversion;
import com.pj.magic.model.User;
import com.pj.magic.model.search.StockQuantityConversionSearchCriteria;
import com.pj.magic.util.DbUtil;

@Repository
public class StockQuantityConversionDaoImpl extends MagicDao implements StockQuantityConversionDao {
	
	private static final String BASE_SELECT_SQL = 
			"select a.ID, STOCK_QTY_CONV_NO, REMARKS, POST_IND, POST_DT, POST_BY, PRINT_IND,"
			+ " b.USERNAME as POST_BY_USERNAME"
			+ " from STOCK_QTY_CONVERSION a"
			+ " left join USER b"
			+ "   on b.ID = a.POST_BY";
	
	private static final String STOCK_QUANTITY_CONVERSION_NUMBER_SEQUENCE = "STOCK_QTY_CONV_NO_SEQ";
	
	private StockQuantityConversionRowMapper stockQuantityConversionRowMapper = new StockQuantityConversionRowMapper();
	
	@Override
	public void save(StockQuantityConversion stockQuantityConversion) {
		if (stockQuantityConversion.getId() == null) {
			insert(stockQuantityConversion);
		} else {
			update(stockQuantityConversion);
		}
	}

	private static final String INSERT_SQL = "insert into STOCK_QTY_CONVERSION"
			+ " (STOCK_QTY_CONV_NO, REMARKS) values (?, ?)";
	
	private void insert(final StockQuantityConversion stockQuantityConversion) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setLong(1, getNextStockQuantityConversionNumber());
				ps.setString(2, stockQuantityConversion.getRemarks());
				return ps;
			}
		}, holder);
		
		StockQuantityConversion updated = get(holder.getKey().longValue());
		stockQuantityConversion.setId(updated.getId());
		stockQuantityConversion.setStockQuantityConversionNumber(updated.getStockQuantityConversionNumber());
	}

	private long getNextStockQuantityConversionNumber() {
		return getNextSequenceValue(STOCK_QUANTITY_CONVERSION_NUMBER_SEQUENCE);
	}

	private static final String UPDATE_SQL = "update STOCK_QTY_CONVERSION"
			+ " set REMARKS = ?, POST_IND = ?, POST_DT = ?, POST_BY = ?, PRINT_IND = ? where ID = ?";
	
	private void update(StockQuantityConversion stockQuantityConversion) {
		getJdbcTemplate().update(UPDATE_SQL, 
				stockQuantityConversion.getRemarks(),
				stockQuantityConversion.isPosted() ? "Y" : "N",
				stockQuantityConversion.getPostDate(),
				stockQuantityConversion.isPosted() ? stockQuantityConversion.getPostedBy().getId() : null,
				stockQuantityConversion.isPrinted() ? "Y" : "N",
				stockQuantityConversion.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public StockQuantityConversion get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, stockQuantityConversionRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by POST_IND asc, POST_DT desc, a.ID desc";
	
	@Override
	public List<StockQuantityConversion> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, stockQuantityConversionRowMapper);
	}

	private class StockQuantityConversionRowMapper implements RowMapper<StockQuantityConversion> {

		@Override
		public StockQuantityConversion mapRow(ResultSet rs, int rowNum) throws SQLException {
			StockQuantityConversion stockQuantityConversion = new StockQuantityConversion();
			stockQuantityConversion.setId(rs.getLong("ID"));
			stockQuantityConversion.setStockQuantityConversionNumber(rs.getLong("STOCK_QTY_CONV_NO"));
			stockQuantityConversion.setRemarks(rs.getString("REMARKS"));
			stockQuantityConversion.setPosted("Y".equals(rs.getString("POST_IND")));
			if (stockQuantityConversion.isPosted()) {
				stockQuantityConversion.setPostDate(rs.getTimestamp("POST_DT"));
				stockQuantityConversion.setPostedBy(
						new User(rs.getLong("POST_BY"), rs.getString("POST_BY_USERNAME")));
			}
			stockQuantityConversion.setPrinted("Y".equals(rs.getString("PRINT_IND")));
			return stockQuantityConversion;
		}
		
	}

	private static final String DELETE_SQL = "delete from STOCK_QTY_CONVERSION where ID = ?";
	
	@Override
	public void delete(StockQuantityConversion stockQuantityConversion) {
		getJdbcTemplate().update(DELETE_SQL, stockQuantityConversion.getId());
	}

	@Override
	public List<StockQuantityConversion> search(StockQuantityConversionSearchCriteria criteria) {
		List<Object> params = new ArrayList<>();
		
		StringBuilder sb = new StringBuilder(BASE_SELECT_SQL);
		sb.append(" where 1 = 1");
		
		if (criteria.getStockQuantityConversionNumber() != null) {
			sb.append(" and STOCK_QTY_CONV_NO = ?");
			params.add(criteria.getStockQuantityConversionNumber());
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
		
		if (criteria.getPrinted() != null) {
			sb.append(" and PRINT_IND = ?");
			params.add(criteria.getPrinted() ? "Y" : "N");
		}
		
		sb.append(" order by STOCK_QTY_CONV_NO desc");
		
		return getJdbcTemplate().query(sb.toString(), stockQuantityConversionRowMapper, params.toArray());
	}

	private static final String GET_NEXT_PAGE_NUMBER_SQL =
			"select coalesce(max(cast(replace(REMARKS, 'P', '') as unsigned)), 0) + 1"
			+ " from STOCK_QTY_CONVERSION"
			+ " where CREATE_DT >= current_date() and CREATE_DT < date_add(current_date(), interval 1 day)"
			+ " and REMARKS like 'P%'";
	
	@Override
	public int getNextPageNumber() {
		return getJdbcTemplate().queryForObject(GET_NEXT_PAGE_NUMBER_SQL, Integer.class);
	}

	private static final String GET_ALL_PENDING_SQL = BASE_SELECT_SQL + " where POST_IND = 'N' or PRINT_IND = 'N'";
	
	@Override
	public List<StockQuantityConversion> getAllPending() {
		return getJdbcTemplate().query(GET_ALL_PENDING_SQL, stockQuantityConversionRowMapper);
	}
	
	private static final String UPDATE_CREATE_DATE_OF_UNPOSTED_SQL =
			"update STOCK_QTY_CONVERSION"
			+ " set CREATE_DT = :createDate"
			+ " where CREATE_DT < :createDate"
			+ " and POST_IND = 'N'";
			
	
	@Override
	public void updateCreateDateOfUnposted(Date referenceDate) {
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("createDate", DbUtil.toMySqlDateString(referenceDate));
		
		getNamedParameterJdbcTemplate().update(UPDATE_CREATE_DATE_OF_UNPOSTED_SQL, paramMap);
	}
	
}