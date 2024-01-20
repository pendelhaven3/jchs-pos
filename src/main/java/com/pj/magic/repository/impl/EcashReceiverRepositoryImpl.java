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
import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.EcashType;
import com.pj.magic.repository.EcashReceiverRepository;

@Repository
public class EcashReceiverRepositoryImpl extends MagicDao implements EcashReceiverRepository {

	private static final String BASE_SELECT_SQL = 
			"select a.ID, a.NAME, a.ECASH_TYPE_ID, b.code as ECASH_TYPE_CODE"
			+ " from ECASH_RECEIVER a"
			+ " join ECASH_TYPE b"
			+ "   on b.ID = a.ECASH_TYPE_ID"
			+ " where 1 = 1";
	
	private RowMapper<EcashReceiver> rowMapper = new RowMapper<EcashReceiver>() {

		@Override
		public EcashReceiver mapRow(ResultSet rs, int rowNum) throws SQLException {
			EcashReceiver ecashReceiver = new EcashReceiver(rs.getLong("ID"), rs.getString("NAME"));
			ecashReceiver.setEcashType(new EcashType(rs.getLong("ECASH_TYPE_ID"), rs.getString("ECASH_TYPE_CODE")));
			return ecashReceiver;
		}
	};
	
	@Override
	public void save(EcashReceiver ecashReceiver) {
		if (ecashReceiver.getId() == null) {
			insert(ecashReceiver);
		} else {
			update(ecashReceiver);
		}
	}

	private static final String INSERT_SQL = "insert into ECASH_RECEIVER (NAME, ECASH_TYPE_ID) values (?, ?)";
	
	private void insert(final EcashReceiver ecashReceiver) {
		KeyHolder holder = new GeneratedKeyHolder();
		getJdbcTemplate().update(new PreparedStatementCreator() {
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				PreparedStatement ps = con.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, ecashReceiver.getName());
				ps.setLong(2, ecashReceiver.getEcashType().getId());
				return ps;
			}
		}, holder);
		
		ecashReceiver.setId(holder.getKey().longValue());
	}

	private static final String UPDATE_SQL =
			"update ECASH_RECEIVER set NAME = ?, ECASH_TYPE_ID = ? where ID = ?";
	
	private void update(EcashReceiver ecashReceiver) {
		getJdbcTemplate().update(UPDATE_SQL, 
				ecashReceiver.getName(),
				ecashReceiver.getEcashType().getId(),
				ecashReceiver.getId());
	}

	private static final String GET_SQL = BASE_SELECT_SQL + " where a.ID = ?";
	
	@Override
	public EcashReceiver get(long id) {
		try {
			return getJdbcTemplate().queryForObject(GET_SQL, rowMapper, id);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}
	
	private static final String GET_ALL_SQL = BASE_SELECT_SQL + " order by a.NAME";
	
	@Override
	public List<EcashReceiver> getAll() {
		return getJdbcTemplate().query(GET_ALL_SQL, rowMapper);
	}

	private static final String DELETE_SQL = "delete from ECASH_RECEIVER where ID = ?";
	
	@Override
	public void delete(EcashReceiver ecashReceiver) {
		getJdbcTemplate().update(DELETE_SQL, ecashReceiver.getId());
	}

	private static final String FIND_BY_NAME_SQL = BASE_SELECT_SQL + " and a.NAME = ?";
	
	@Override
	public EcashReceiver findByName(String name) {
		try {
			return getJdbcTemplate().queryForObject(FIND_BY_NAME_SQL, rowMapper, name);
		} catch (IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}
