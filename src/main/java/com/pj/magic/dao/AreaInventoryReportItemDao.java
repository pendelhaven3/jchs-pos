package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.AreaInventoryReportItemSearchCriteria;

public interface AreaInventoryReportItemDao {

	void save(AreaInventoryReportItem item);
	
	List<AreaInventoryReportItem> findAllByAreaInventoryReport(AreaInventoryReport areaInventoryReport);

	void delete(AreaInventoryReportItem item);

	void deleteAllByAreaInventoryReport(AreaInventoryReport areaInventoryReport);
	
	List<AreaInventoryReportItem> search(AreaInventoryReportItemSearchCriteria criteria);

	AreaInventoryReportItem findFirstByProduct(Product product);
	
}
