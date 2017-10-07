package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.BadStockReturn;
import com.pj.magic.model.BadStockReturnItem;
import com.pj.magic.model.search.BadStockReturnSearchCriteria;

public interface BadStockReturnService {

	void save(BadStockReturn badStockReturn);
	
	BadStockReturn getBadStockReturn(long id);

	List<BadStockReturn> getAllNewBadStockReturns();

	void save(BadStockReturnItem item);

	void delete(BadStockReturnItem item);

	void post(BadStockReturn badStockReturn);

	void markAsPaid(BadStockReturn badStockReturn);

	BadStockReturn findBadStockReturnByBadStockReturnNumber(long badStockReturnNumber);

	List<BadStockReturn> getUnpaidBadStockReturns();

	List<BadStockReturn> search(BadStockReturnSearchCriteria criteria);

	void cancel(BadStockReturn badStockReturn);
	
}