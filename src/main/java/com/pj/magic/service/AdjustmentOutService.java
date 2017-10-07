package com.pj.magic.service;

import java.util.List;

import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.search.AdjustmentOutSearchCriteria;

public interface AdjustmentOutService {

	void save(AdjustmentOut adjustmentOut);
	
	AdjustmentOut getAdjustmentOut(long id);

	void save(AdjustmentOutItem item);

	void delete(AdjustmentOutItem item);

	void delete(AdjustmentOut adjustmentOut);

	void post(AdjustmentOut adjustmentOut) throws NotEnoughStocksException;
	
	List<AdjustmentOut> getAllNonPostedAdjustmentOuts();

	List<AdjustmentOut> search(AdjustmentOutSearchCriteria criteria);
	
}
