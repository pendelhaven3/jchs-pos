package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;

public interface BadStockReturnDao {

	BadStockReturn get(long id);

	void save(BadStockReturn badStockReturn);

	List<BadStockReturn> search(BadStockReturnSearchCriteria criteria);

	BadStockReturn findByBadStockReturnNumber(long badStockReturnNumber);

}