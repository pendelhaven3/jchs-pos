package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.InventoryCorrection;

public interface InventoryCorrectionService {

	List<InventoryCorrection> getAllInventoryCorrections();
	
	InventoryCorrection getInventoryCorrection(long id);
	
	void save(InventoryCorrection inventoryCorrection);
	
}
