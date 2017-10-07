package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AdjustmentInDao;
import com.pj.magic.dao.AdjustmentInItemDao;
import com.pj.magic.dao.ProductDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.AdjustmentIn;
import com.pj.magic.model.AdjustmentInItem;
import com.pj.magic.model.Product;
import com.pj.magic.model.search.AdjustmentInSearchCriteria;
import com.pj.magic.service.AdjustmentInService;
import com.pj.magic.service.LoginService;

@Service
public class AdjustmentInServiceImpl implements AdjustmentInService {

	@Autowired private AdjustmentInDao adjustmentInDao;
	@Autowired private AdjustmentInItemDao adjustmentInItemDao;
	@Autowired private ProductDao productDao;
	@Autowired private SystemDao systemDao;
	@Autowired private LoginService loginService;
	
	@Transactional
	@Override
	public void save(AdjustmentIn adjustmentIn) {
		adjustmentInDao.save(adjustmentIn);
	}

	@Override
	public AdjustmentIn getAdjustmentIn(long id) {
		AdjustmentIn adjustmentIn = adjustmentInDao.get(id);
		loadAdjustmentInDetails(adjustmentIn);
		return adjustmentIn;
	}
	
	private void loadAdjustmentInDetails(AdjustmentIn adjustmentIn) {
		adjustmentIn.setItems(adjustmentInItemDao.findAllByAdjustmentIn(adjustmentIn));
		for (AdjustmentInItem item : adjustmentIn.getItems()) {
			item.setProduct(productDao.get(item.getProduct().getId()));
		}
	}

	@Transactional
	@Override
	public void save(AdjustmentInItem item) {
		adjustmentInItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(AdjustmentInItem item) {
		adjustmentInItemDao.delete(item);
	}

	@Transactional
	@Override
	public void delete(AdjustmentIn adjustmentIn) {
		adjustmentInItemDao.deleteAllByAdjustmentIn(adjustmentIn);
		adjustmentInDao.delete(adjustmentIn);
	}

	@Transactional(propagation=Propagation.REQUIRES_NEW)
	@Override
	public void post(AdjustmentIn adjustmentIn) {
		AdjustmentIn updated = getAdjustmentIn(adjustmentIn.getId());
		for (AdjustmentInItem item : updated.getItems()) {
			Product product = productDao.get(item.getProduct().getId());
			product.addUnitQuantity(item.getUnit(), item.getQuantity());
			productDao.updateAvailableQuantities(product);
			
			item.setCost(product.getFinalCost(item.getUnit()));
			adjustmentInItemDao.save(item);
		}
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		adjustmentInDao.save(updated);
	}

	@Override
	public List<AdjustmentIn> getAllNonPostedAdjustmentIns() {
		AdjustmentInSearchCriteria criteria = new AdjustmentInSearchCriteria();
		criteria.setPosted(false);
		return search(criteria);
	}

	@Override
	public List<AdjustmentIn> search(AdjustmentInSearchCriteria criteria) {
		return adjustmentInDao.search(criteria);
	}
	
}
