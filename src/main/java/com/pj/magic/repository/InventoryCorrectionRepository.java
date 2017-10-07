package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.InventoryCorrection;

public interface InventoryCorrectionRepository {

	List<InventoryCorrection> getAll();
	
	InventoryCorrection get(long id);
	
	void save(InventoryCorrection inventoryCorrection);

}
