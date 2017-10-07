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

import com.pj.magic.dao.ManufacturerDao;
import com.pj.magic.model.Manufacturer;

@Repository
public class ManufacturerDaoImpl extends MagicDao implements ManufacturerDao {
	
	private static final String BASE_SELECT_SQL = "select ID, NAME from MANUFACTURER";
	
	private ManufacturerRowMapper manufacturerRowMapper = new ManufacturerRowMapper();
	
	@Override
	public void save(Manufacturer manufacturer) {
		if (manufacturer.getId() == null) {
			insert(manufacturer);
		} else {
			update(manufacturer);
		}
	}

	private static final String INSERT_SQL = "insert into MANUFACTURER (NAME) values (?)";
	
	private void insert(final Manufacturer manufacturer) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, manufacturer.getName());
				return ps;
			}
		}, holder);
		
		manufacturer.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update MANUFACTURER set NAME = ? where ID = ?";
	
	private void update(Manufacturer manufacturer) {
		getJdbcTemplate().update(UPDATE_SQL, manufacturer.getName(), manufacturer.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where id = ?";
	
	@Override
	public Manufacturer get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, manufacturerRowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by NAME";
	
	@Override
	public List<Manufacturer> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, manufacturerRowMapper);
	}

	private class ManufacturerRowMapper implements RowMapper<Manufacturer> {

		@Override
		public Manufacturer mapRow(ResultSet rs, int rowNum) throws SQLException {
			Manufacturer manufacturer = new Manufacturer();
			manufacturer.setId(rs.getLong("ID"));
			manufacturer.setName(rs.getString("NAME"));
			return manufacturer;
		}
		
	}
	
}
