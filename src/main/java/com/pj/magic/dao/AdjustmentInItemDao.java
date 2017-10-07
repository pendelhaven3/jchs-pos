package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.model.Product;

public interface AdjustmentInItemDao {

	void save(AdjustmentInItem item);
	
	List<AdjustmentInItem> findAllByAdjustmentIn(AdjustmentIn adjustmentIn);

	void delete(AdjustmentInItem item);

	void deleteAllByAdjustmentIn(AdjustmentIn adjustmentIn);

	AdjustmentInItem findFirstByProduct(Product product);
	
}
