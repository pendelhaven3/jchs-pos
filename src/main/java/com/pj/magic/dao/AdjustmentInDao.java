package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.search.AdjustmentInSearchCriteria;

public interface AdjustmentInDao {

	AdjustmentIn get(long id);

	void save(AdjustmentIn adjustmentOut);

	void delete(AdjustmentIn adjustmentOut);

	List<AdjustmentIn> search(AdjustmentInSearchCriteria criteria);
	
}
