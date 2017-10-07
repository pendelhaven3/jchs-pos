package com.pj.magic.dao.impl;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.SystemDao;

@Repository
public class SystemDaoImpl extends MagicDao implements SystemDao {

	private static final String GET_CURRENT_DATE_SQL = "select current_timestamp()";
	
	@Override
	public Date getCurrentDateTime() {
		return getJdbcTemplate().queryForObject(GET_CURRENT_DATE_SQL, Date.class);
	}

}
