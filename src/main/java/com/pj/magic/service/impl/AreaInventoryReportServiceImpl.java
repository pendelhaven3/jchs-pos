package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AreaInventoryReportDao;
import com.pj.magic.dao.AreaInventoryReportItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.model.AreaInventoryReport;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.search.AreaInventoryReportSearchCriteria;
import com.pj.magic.service.AreaInventoryReportService;
import com.pj.magic.service.LoginService;

@Service
public class AreaInventoryReportServiceImpl implements AreaInventoryReportService {

	@Autowired private AreaInventoryReportDao areaInventoryReportDao;
	@Autowired private AreaInventoryReportItemDao areaInventoryReportItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private LoginService loginService;
	
	@Override
	public List<AreaInventoryReport> getAllAreaInventoryReports() {
		return areaInventoryReportDao.getAll();
	}

	@Override
	public void save(AreaInventoryReport areaInventoryReport) {
		if (areaInventoryReport.getId() == null) {
			areaInventoryReport.setCreatedBy(loginService.getLoggedInUser());
		}
		areaInventoryReportDao.save(areaInventoryReport);
	}

	@Override
	public AreaInventoryReport getAreaInventoryReport(long id) {
		AreaInventoryReport areaInventoryReport = areaInventoryReportDao.get(id);
		loadChildrenDetails(areaInventoryReport);
		return areaInventoryReport;
	}

	private void loadChildrenDetails(AreaInventoryReport areaInventoryReport) {
		List<AreaInventoryReportItem> items = areaInventoryReportItemDao.findAllByAreaInventoryReport(areaInventoryReport);
		for (AreaInventoryReportItem item : items) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
		areaInventoryReport.setItems(items);
	}

	@Transactional
	@Override
	public void delete(AreaInventoryReportItem item) {
		areaInventoryReportItemDao.delete(item);
	}

	@Override
	public void save(AreaInventoryReportItem item) {
		areaInventoryReportItemDao.save(item);
	}

	@Override
	public AreaInventoryReport findByInventoryCheckAndReportNumber(
			InventoryCheck inventoryCheck, int reportNumber) {
		return areaInventoryReportDao.findByInventoryCheckAndReportNumber(inventoryCheck, reportNumber);
	}

	@Override
	public List<AreaInventoryReport> findAllAreaInventoryReportsByInventoryCheck(
			InventoryCheck inventoryCheck) {
		return areaInventoryReportDao.findAllByInventoryCheck(inventoryCheck);
	}

	@Override
	public void markAsReviewed(AreaInventoryReport areaInventoryReport) {
		areaInventoryReport.setReviewed(true);
		areaInventoryReportDao.save(areaInventoryReport);
	}

	@Override
	public List<AreaInventoryReport> search(AreaInventoryReportSearchCriteria criteria) {
		return areaInventoryReportDao.search(criteria);
	}

}