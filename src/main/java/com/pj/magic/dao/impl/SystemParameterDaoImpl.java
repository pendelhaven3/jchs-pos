package com.pj.magic.dao.impl;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SystemParameterDao;

@Repository
public class SystemParameterDaoImpl extends MagicDao implements SystemParameterDao {

	private static final String GET_SYSTEM_PARAMETER_SQL =
			"select VALUE from SYSTEM_PARAMETER where NAME = ?";
	
	@Override
	public String getSystemParameterValue(String name) {
		return getJdbcTemplate().queryForObject(GET_SYSTEM_PARAMETER_SQL, String.class, name);
	}

}
