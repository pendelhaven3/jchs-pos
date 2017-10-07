package com.pj.magic.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pj.magic.dao.PurchasePaymentAdjustmentDao;
import com.pj.magic.dao.SystemDao;
import com.pj.magic.model.PurchasePaymentAdjustment;
import com.pj.magic.model.search.PurchasePaymentAdjustmentSearchCriteria;
import com.pj.magic.service.LoginService;
import com.pj.magic.service.PurchasePaymentAdjustmentService;

@Service
public class PurchasePaymentAdjustmentServiceImpl implements PurchasePaymentAdjustmentService {

	@Autowired private PurchasePaymentAdjustmentDao purchasePaymentAdjustmentDao;
	@Autowired private SystemDao systemDao;
	@Autowired private LoginService loginService;
	
	@Transactional
	@Override
	public void save(PurchasePaymentAdjustment paymentAdjustment) {
		purchasePaymentAdjustmentDao.save(paymentAdjustment);
	}

	@Override
	public PurchasePaymentAdjustment getPurchasePaymentAdjustment(long id) {
		return purchasePaymentAdjustmentDao.get(id);
	}

	@Transactional
	@Override
	public void post(PurchasePaymentAdjustment paymentAdjustment) {
		PurchasePaymentAdjustment updated = purchasePaymentAdjustmentDao.get(paymentAdjustment.getId());
		updated.setPosted(true);
		updated.setPostDate(systemDao.getCurrentDateTime());
		updated.setPostedBy(loginService.getLoggedInUser());
		purchasePaymentAdjustmentDao.save(updated);
	}

	@Override
	public PurchasePaymentAdjustment findPurchasePaymentAdjustmentByPurchasePaymentAdjustmentNumber(
			long purchasePaymentAdjustmentNumber) {
		return purchasePaymentAdjustmentDao.findByPurchasePaymentAdjustmentNumber(purchasePaymentAdjustmentNumber);
	}

	@Override
	public List<PurchasePaymentAdjustment> search(PurchasePaymentAdjustmentSearchCriteria criteria) {
		return purchasePaymentAdjustmentDao.search(criteria);
	}

	@Override
	public List<PurchasePaymentAdjustment> getAllNewPaymentAdjustments() {
		PurchasePaymentAdjustmentSearchCriteria criteria = new PurchasePaymentAdjustmentSearchCriteria();
		criteria.setPosted(false);
		return search(criteria);
	}

}