package com.pj.magic.dao;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.InventoryCheck;

public interface InventoryCheckDao {

	void save(InventoryCheck inventoryCheck);
	
	InventoryCheck get(long id);
	
	List<InventoryCheck> getAll();
	
	List<InventoryCheck> search(InventoryCheck criteria);

	InventoryCheck getMostRecent();

	InventoryCheck findByInventoryDate(Date inventoryDate);
	
}
