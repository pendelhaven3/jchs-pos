package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.search.AreaInventoryReportSearchCriteria;

public interface AreaInventoryReportService {

	List<AreaInventoryReport> getAllAreaInventoryReports();

	void save(AreaInventoryReport areaInventoryReport);

	AreaInventoryReport getAreaInventoryReport(long id);

	void delete(AreaInventoryReportItem item);

	void save(AreaInventoryReportItem item);
	
	AreaInventoryReport findByInventoryCheckAndReportNumber(
			InventoryCheck inventoryCheck, int reportNumber);

	List<AreaInventoryReport> findAllAreaInventoryReportsByInventoryCheck(InventoryCheck inventoryCheck);

	void markAsReviewed(AreaInventoryReport areaInventoryReport);

	List<AreaInventoryReport> search(AreaInventoryReportSearchCriteria criteria);
	
}