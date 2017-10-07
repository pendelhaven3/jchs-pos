package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchasePaymentAdjustment;
import com.pj.magic.model.search.PurchasePaymentAdjustmentSearchCriteria;

public interface PurchasePaymentAdjustmentService {

	void save(PurchasePaymentAdjustment paymentAdjustment);
	
	PurchasePaymentAdjustment getPurchasePaymentAdjustment(long id);
	
	void post(PurchasePaymentAdjustment paymentAdjustment);

	PurchasePaymentAdjustment findPurchasePaymentAdjustmentByPurchasePaymentAdjustmentNumber(
			long purchasePaymentAdjustmentNumber);

	List<PurchasePaymentAdjustment> search(PurchasePaymentAdjustmentSearchCriteria criteria);

	List<PurchasePaymentAdjustment> getAllNewPaymentAdjustments();
	
}