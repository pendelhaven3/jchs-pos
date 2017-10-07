package com.pj.magic.repository.impl;

import java.util.Date;

import org.springframework.stereotype.Repository;

import com.pj.magic.dao.impl.MagicDao;
import com.pj.magic.repository.DailyProductStartingQuantityRepository;
import com.pj.magic.util.DbUtil;

@Repository
public class DailyProductStartingQuantityRepositoryImpl extends MagicDao implements DailyProductStartingQuantityRepository {

	private static final String SAVE_QUANTITIES_FOR_TODAY_SQL =
			"insert into DAILY_PRODUCT_STARTING_QUANTITY"
			+ " (DATE, PRODUCT_ID, AVAIL_QTY_CSE, AVAIL_QTY_TIE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS)"
			+ " select ?, ID, AVAIL_QTY_CSE, AVAIL_QTY_TIE, AVAIL_QTY_CTN, AVAIL_QTY_DOZ, AVAIL_QTY_PCS"
			+ " from PRODUCT"
			+ " where ACTIVE_IND = 'Y'";
	
	@Override
	public void saveQuantities(Date date) {
		getJdbcTemplate().update(SAVE_QUANTITIES_FOR_TODAY_SQL, DbUtil.toMySqlDateString(date));
	}

	private static final String GET_COUNT_BY_DATE_SQL =
			"select count(*)"
			+ " from DAILY_PRODUCT_STARTING_QUANTITY"
			+ " where DATE = ?";
	
	@Override
	public int getCountByDate(Date date) {
		return getJdbcTemplate().queryForObject(GET_COUNT_BY_DATE_SQL, Integer.class, DbUtil.toMySqlDateString(date));
	}

}
