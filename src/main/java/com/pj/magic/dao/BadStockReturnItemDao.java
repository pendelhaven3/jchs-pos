package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;

public interface BadStockReturnItemDao {

	void save(BadStockReturnItem item);
	
	List<BadStockReturnItem> findAllByBadStockReturn(BadStockReturn badStockReturn);

	void delete(BadStockReturnItem item);

}