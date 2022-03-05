package com.pj.magic.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AreaInventoryReportItemDao;
import com.pj.magic.dao.InventoryCheckDao;
import com.pj.magic.dao.InventoryCheckSummaryItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.AreaInventoryReportItem;
import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.search.AreaInventoryReportItemSearchCriteria;
import com.pj.magic.repository.Product2Repository;
import com.pj.magic.service.InventoryCheckService;

@Service
public class InventoryCheckServiceImpl implements InventoryCheckService {

	@Autowired private InventoryCheckDao inventoryCheckDao;
	@Autowired private InventoryCheckSummaryItemDao inventoryCheckSummaryItemDao;
	@Autowired private AreaInventoryReportItemDao areaInventoryReportItemDao;
	@Autowired private SystemDao systemDao;
	@Autowired private Product2Repository product2Repository;
	
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
			List<InventoryCheckSummaryItem> items = inventoryCheckSummaryItemDao.findAllByPostedInventoryCheck(inventoryCheck);
			items = items.stream()
					.filter(item -> item.getQuantity() > 0)
					.collect(Collectors.toList());
			inventoryCheck.setSummaryItems(items);
		} else {
			List<InventoryCheckSummaryItem> items = inventoryCheckSummaryItemDao.findAllByInventoryCheck(inventoryCheck);
			items = items.stream()
					.filter(item -> item.getQuantity() > 0)
					.collect(Collectors.toList());
			inventoryCheck.setSummaryItems(items);
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
			product2Repository.addAvailableQuantity(item.getProduct().getId(), item.getUnit(), item.getQuantity());
			item.setParent(inventoryCheck);
			inventoryCheckSummaryItemDao.save(item);
		}
		
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
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