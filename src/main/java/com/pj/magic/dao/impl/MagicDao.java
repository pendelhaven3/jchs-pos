package com.pj.magic.dao.impl;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

public abstract class MagicDao extends JdbcDaoSupport {

	@Autowired private DataSource dataSource;
	
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@PostConstruct
	public void initialize() {
		setDataSource(dataSource);
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	private static final String GET_SEQUENCE_NEXT_VALUE_SQL = 
			"select VALUE + 1 from SEQUENCE where NAME = ? for update";
	
	private static final String UPDATE_SEQUENCE_VALUE_SQL =
			"update SEQUENCE set VALUE = ? where NAME = ?";
	
	protected Long getNextSequenceValue(String sequenceName) {
		Long value = getJdbcTemplate().queryForObject(GET_SEQUENCE_NEXT_VALUE_SQL, Long.class, sequenceName);
		getJdbcTemplate().update(UPDATE_SEQUENCE_VALUE_SQL, value, sequenceName);
		return value;
	}

	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}
	
}