package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AreaInventoryReportItemDao;
import com.pj.magic.dao.InventoryCheckDao;
import com.pj.magic.dao.InventoryCheckSummaryItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.AreaInventoryReportItemSearchCriteria;
import com.pj.magic.service.InventoryCheckService;
import com.pj.magic.service.LoginService;

@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {

	@Autowired private InventoryCheckDao inventoryCheckDao;
	@Autowired private InventoryCheckSummaryItemDao inventoryCheckSummaryItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private AreaInventoryReportItemDao areaInventoryReportItemDao;
	@Autowired private SystemDao systemDao;
	@Autowired private LoginService loginService;
	
	@Override
	public List<InventoryCheck> getAllInventoryChecks() {
		return inventoryCheckDao.getAll();
	}

	@Override
	public void save(InventoryCheck inventoryCheck) {
		inventoryCheckDao.save(inventoryCheck);
	}

	@Override
	public InventoryCheck getNonPostedInventoryCheck() {
		List<InventoryCheck> inventoryChecks = inventoryCheckDao.search(new InventoryCheck());
		switch (inventoryChecks.size()) {
		case 0:
			return null;
		case 1:
			return inventoryChecks.get(0);
		default:
			throw new RuntimeException("There should only be one non-posted inventory check at any one time");
		}
	}

	@Override
	public InventoryCheck getInventoryCheck(long id) {
		InventoryCheck inventoryCheck = inventoryCheckDao.get(id);
		if (inventoryCheck.isPosted()) {
			inventoryCheck.setSummaryItems(
					inventoryCheckSummaryItemDao.findAllByPostedInventoryCheck(inventoryCheck));
		} else {
			inventoryCheck.setSummaryItems(inventoryCheckSummaryItemDao.findAllByInventoryCheck(inventoryCheck));
		}
		
		for (InventoryCheckSummaryItem item : inventoryCheck.getSummaryItems()) {
			item.setParent(inventoryCheck);
		}
		
		return inventoryCheck;
	}

	@Transactional
	@Override
	public void post(InventoryCheck inventoryCheck) {
		InventoryCheck updated = getInventoryCheck(inventoryCheck.getId());
		for (InventoryCheckSummaryItem item : updated.getSummaryItems()) {
			Product product = productDao.get(item.getProduct().getId());
			product.addUnitQuantity(item.getUnit(), item.getQuantityDifference());
			productDao.updateAvailableQuantities(product);
			
			item.setParent(inventoryCheck);
			inventoryCheckSummaryItemDao.save(item);
		}
		
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		inventoryCheckDao.save(updated);
	}

	@Override
	public List<AreaInventoryReportItem> getItemActualCountDetails(InventoryCheckSummaryItem item) {
		AreaInventoryReportItemSearchCriteria criteria = new AreaInventoryReportItemSearchCriteria();
		criteria.setInventoryCheck(item.getParent());
		criteria.setProduct(item.getProduct());
		criteria.setUnit(item.getUnit());
		return areaInventoryReportItemDao.search(criteria);
	}
	
}