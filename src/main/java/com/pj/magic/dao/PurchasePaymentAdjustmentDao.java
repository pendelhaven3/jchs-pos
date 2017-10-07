package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePaymentAdjustment;
import com.pj.magic.model.search.PurchasePaymentAdjustmentSearchCriteria;

public interface PurchasePaymentAdjustmentDao {

	PurchasePaymentAdjustment get(long id);

	void save(PurchasePaymentAdjustment paymentAdjustment);

	PurchasePaymentAdjustment findByPurchasePaymentAdjustmentNumber(long purchasePaymentAdjustmentNumber);

	List<PurchasePaymentAdjustment> search(PurchasePaymentAdjustmentSearchCriteria criteria);
	
}