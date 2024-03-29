package com.pj.magic.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pj.magic.dao.AdjustmentOutDao;
import com.pj.magic.dao.AdjustmentOutItemDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.exception.NotEnoughStocksException;
import com.pj.magic.model.AdjustmentOut;
import com.pj.magic.model.AdjustmentOutItem;
import com.pj.magic.model.Product2;
import com.pj.magic.model.search.AdjustmentOutSearchCriteria;
import com.pj.magic.repository.Product2Repository;
import com.pj.magic.service.AdjustmentOutService;
import com.pj.magic.service.LoginService;

@Service
public class AdjustmentOutServiceImpl implements AdjustmentOutService {

	@Autowired private AdjustmentOutDao adjustmentOutDao;
	@Autowired private AdjustmentOutItemDao adjustmentOutItemDao;
	@Autowired private SystemDao systemDao;
	@Autowired private LoginService loginService;
	@Autowired private Product2Repository product2Repository;
	
	@Transactional
	@Override
	public void save(AdjustmentOut adjustmentOut) {
		adjustmentOutDao.save(adjustmentOut);
	}

	@Override
	public AdjustmentOut getAdjustmentOut(long id) {
		AdjustmentOut adjustmentOut = adjustmentOutDao.get(id);
		loadAdjustmentOutDetails(adjustmentOut);
		return adjustmentOut;
	}
	
	private void loadAdjustmentOutDetails(AdjustmentOut adjustmentOut) {
		adjustmentOut.setItems(adjustmentOutItemDao.findAllByAdjustmentOut(adjustmentOut));
		for (AdjustmentOutItem item : adjustmentOut.getItems()) {
			item.setProduct(product2Repository.get(item.getProduct().getId()));
		}
	}

	@Transactional
	@Override
	public void save(AdjustmentOutItem item) {
		adjustmentOutItemDao.save(item);
	}

	@Transactional
	@Override
	public void delete(AdjustmentOutItem item) {
		adjustmentOutItemDao.delete(item);
	}

	@Transactional
	@Override
	public void delete(AdjustmentOut adjustmentOut) {
		adjustmentOutItemDao.deleteAllByAdjustmentOut(adjustmentOut);
		adjustmentOutDao.delete(adjustmentOut);
	}

	@Transactional
	@Override
	public void post(AdjustmentOut adjustmentOut) throws NotEnoughStocksException {
		AdjustmentOut updated = getAdjustmentOut(adjustmentOut.getId());
		for (AdjustmentOutItem item : updated.getItems()) {
			Product2 product = product2Repository.get(item.getProduct().getId());
			if (!product.hasAvailableUnitQuantity(item.getUnit(), item.getQuantity())) {
				throw new NotEnoughStocksException(item);
			} else {
				product.subtractUnitQuantity(item.getUnit(), item.getQuantity());
				product2Repository.subtractAvailableQuantity(item.getProduct().getId(), item.getUnit(), item.getQuantity());
				
				item.setCost(product.getFinalCost(item.getUnit()));
				adjustmentOutItemDao.save(item);
			}
		}
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		adjustmentOutDao.save(updated);
	}

	@Override
	public List<AdjustmentOut> getAllNonPostedAdjustmentOuts() {
		AdjustmentOutSearchCriteria criteria = new AdjustmentOutSearchCriteria();
		criteria.setPosted(false);
		return search(criteria);
	}

	@Override
	public List<AdjustmentOut> search(AdjustmentOutSearchCriteria criteria) {
		return adjustmentOutDao.search(criteria);
	}
	
}
