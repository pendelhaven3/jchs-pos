package com.pj.magic.repository.impl;

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

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.TrisysSalesImport;
import com.pj.magic.model.User;
import com.pj.magic.repository.TrisysSalesImportRepository;

@Repository
public class TrisysSalesImportRepositoryImpl extends MagicDao implements TrisysSalesImportRepository {

	private static final String BASE_SELECT_SQL =
			"select a.ID, a.FILE, a.IMPORT_DT, a.IMPORT_BY,"
			+ " b.USERNAME as IMPORT_BY_USERNAME"
			+ " from TRISYS_SALES_IMPORT a"
			+ " join USER b"
			+ "   on b.ID = a.IMPORT_BY";
	
	private RowMapper<TrisysSalesImport> rowMapper = new RowMapper<TrisysSalesImport>() {

		@Override
		public TrisysSalesImport mapRow(ResultSet rs, int rowNum) throws SQLException {
			TrisysSalesImport salesImport = new TrisysSalesImport();
			salesImport.setId(rs.getLong("ID"));
			salesImport.setFile(rs.getString("FILE"));
			salesImport.setImportDate(rs.getTimestamp("IMPORT_DT"));
			salesImport.setImportBy(new User(rs.getLong("IMPORT_BY"), rs.getString("IMPORT_BY_USERNAME")));
			return salesImport;
		}
	};
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.IMPORT_DT desc";
	
	@Override
	public List<TrisysSalesImport> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public TrisysSalesImport get(long id) {
		return getJdbcTemplate().queryForObject(GET_SQL, new Object[] {id}, rowMapper);
	}

	private static final String INSERT_SQL =
			"insert into TRISYS_SALES_IMPORT"
			+ " (FILE, IMPORT_DT, IMPORT_BY)"
			+ " values"
			+ " (?, current_timestamp(), ?)";

	@Override
	public void save(TrisysSalesImport salesImport) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, salesImport.getFile());
				ps.setLong(2, salesImport.getImportBy().getId());
				return ps;
			}
		}, holder);
		
		salesImport.setId(holder.getKey().longValue());
	}

	private static final String FIND_BY_FILE_SQL = BASE_SELECT_SQL + " where a.FILE = ?";
	
	@Override
	public TrisysSalesImport findByFile(String file) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_FILE_SQL, new Object[] {file}, rowMapper);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
}
