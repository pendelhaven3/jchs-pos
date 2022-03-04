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
import com.pj.magic.model.Area;
import com.pj.magic.repository.AreaRepository;

@Repository
public class AreaRepositoryImpl extends MagicDao implements AreaRepository {

	private static final String BASE_SELECT_SQL = "select ID, NAME from AREA";

	private RowMapper<Area> rowMapper=new RowMapper<Area>(){

	@Override public Area mapRow(ResultSet rs,int rowNum)throws SQLException{Area area=new Area();area.setId(rs.getLong("ID"));area.setName(rs.getString("NAME"));return area;}};

	private static final String INSERT_SQL = "insert into AREA (NAME) values (?)";

	@Override
	public void save(Area area) {
		if (area.getId() == null) {
			insert(area);
		} else {
			update(area);
		}
	}
	
	private void insert(final Area area) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, area.getName());
				return ps;
			}
		}, holder);

		area.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL = "update AREA set NAME = ? where ID = ?";
	
	private void update(Area area) {
		getJdbcTemplate().update(UPDATE_SQL, area.getName(), area.getId());
	}

	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by NAME";

	@Override
	public List<Area> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where ID = ?";

	@Override
	public Area get(long id) {
		return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
	}

	private static final String FIND_BY_NAME_SQL = BASE_SELECT_SQL + " where NAME = ?";

	@Override
	public Area findByName(String name) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_NAME_SQL, rowMapper, name);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}
