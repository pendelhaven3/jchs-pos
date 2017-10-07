package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.search.AdjustmentOutSearchCriteria;

public interface AdjustmentOutDao {

	AdjustmentOut get(long id);

	void save(AdjustmentOut adjustmentOut);

	void delete(AdjustmentOut adjustmentOut);

	List<AdjustmentOut> search(AdjustmentOutSearchCriteria criteria);
	
}
