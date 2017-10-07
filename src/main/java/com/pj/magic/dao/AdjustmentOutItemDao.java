package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product;

public interface AdjustmentOutItemDao {

	void save(AdjustmentOutItem item);
	
	List<AdjustmentOutItem> findAllByAdjustmentOut(AdjustmentOut adjustmentOut);

	void delete(AdjustmentOutItem item);

	void deleteAllByAdjustmentOut(AdjustmentOut adjustmentOut);

	AdjustmentOutItem findFirstByProduct(Product product);
	
}
