package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.SalesReturn;
import com.pj.magic.model.SalesReturnItem;
import com.pj.magic.model.search.SalesReturnItemSearchCriteria;

public interface SalesReturnItemDao {

	List<SalesReturnItem> findAllBySalesReturn(SalesReturn salesReturn);

	void save(SalesReturnItem item);

	void delete(SalesReturnItem item);

	void deleteAllBySalesReturn(SalesReturn salesReturn);
	
	List<SalesReturnItem> search(SalesReturnItemSearchCriteria criteria);
	
}