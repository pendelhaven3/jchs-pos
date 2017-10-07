package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;

public interface InventoryCheckService {

	List<InventoryCheck> getAllInventoryChecks();

	void save(InventoryCheck inventoryCheck);
	
	InventoryCheck getNonPostedInventoryCheck();
	
	InventoryCheck getInventoryCheck(long id);

	void post(InventoryCheck inventoryCheck);

	List<AreaInventoryReportItem> getItemActualCountDetails(InventoryCheckSummaryItem item);

}