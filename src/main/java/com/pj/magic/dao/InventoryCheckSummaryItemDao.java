package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;

public interface InventoryCheckSummaryItemDao {

	List<InventoryCheckSummaryItem> findAllByInventoryCheck(InventoryCheck inventoryCheck);
	
	List<InventoryCheckSummaryItem> findAllByPostedInventoryCheck(InventoryCheck inventoryCheck);

	void save(InventoryCheckSummaryItem item);
	
}
