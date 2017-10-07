package com.pj.magic.repository;

import java.util.Date;

public interface DailyProductStartingQuantityRepository {

	void saveQuantities(Date date);

	int getCountByDate(Date date);
	
}
