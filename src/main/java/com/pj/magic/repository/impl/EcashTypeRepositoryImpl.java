package com.pj.magic.repository.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.model.EcashType;
import com.pj.magic.repository.EcashTypeRepository;

@Repository
public class EcashTypeRepositoryImpl extends MagicDao implements EcashTypeRepository {

	private static final String BASE_SELECT_SQL = "select ID, CODE from ECASH_TYPE order by CODE";

	private RowMapper<EcashType> rowMapper = new RowMapper<EcashType>() {

		@Override
		public EcashType mapRow(ResultSet rs, int rowNum) throws SQLException {
			return new EcashType(rs.getLong("ID"), rs.getString("CODE"));
		}
	};
	
	@Override
	public List<EcashType> getAll() {
		return getJdbcTemplate().query(BASE_SELECT_SQL, rowMapper);
	}
	
}