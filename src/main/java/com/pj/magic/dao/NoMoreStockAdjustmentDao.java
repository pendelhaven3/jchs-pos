package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.NoMoreStockAdjustment;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.NoMoreStockAdjustmentSearchCriteria;

public interface NoMoreStockAdjustmentDao {

	void save(NoMoreStockAdjustment noMoreStockAdjustment);
	
	NoMoreStockAdjustment get(long id);

	NoMoreStockAdjustment findByNoMoreStockAdjustmentNumber(long noMoreStockAdjustmentNumber);

	List<NoMoreStockAdjustment> search(NoMoreStockAdjustmentSearchCriteria criteria);

	List<NoMoreStockAdjustment> findAllBySalesInvoice(SalesInvoice salesInvoice);
	
}