package com.pj.magic.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class MagicDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String GET_SEQUENCE_NEXT_VALUE_SQL = 
			"select VALUE + 1 from SEQUENCE where NAME = ? for update";
	
	private static final String UPDATE_SEQUENCE_VALUE_SQL =
			"update SEQUENCE set VALUE = ? where NAME = ?";
	
	protected Long getNextSequenceValue(String sequenceName) {
		Long value = jdbcTemplate.queryForObject(GET_SEQUENCE_NEXT_VALUE_SQL, Long.class, sequenceName);
		jdbcTemplate.update(UPDATE_SEQUENCE_VALUE_SQL, value, sequenceName);
		return value;
	}

	public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}
	
}