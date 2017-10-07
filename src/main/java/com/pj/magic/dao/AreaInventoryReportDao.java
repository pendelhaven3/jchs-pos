package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.search.AreaInventoryReportSearchCriteria;

public interface AreaInventoryReportDao {

	AreaInventoryReport get(long id);

	void save(AreaInventoryReport areaInventoryReport);

	List<AreaInventoryReport> getAll();
	
	AreaInventoryReport findByInventoryCheckAndReportNumber(InventoryCheck inventoryCheck, int reportNumber);

	List<AreaInventoryReport> findAllByInventoryCheck(InventoryCheck inventoryCheck);

	List<AreaInventoryReport> search(AreaInventoryReportSearchCriteria criteria);
	
}