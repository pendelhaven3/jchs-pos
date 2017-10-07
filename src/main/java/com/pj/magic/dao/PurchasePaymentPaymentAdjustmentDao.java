package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentPaymentAdjustment;

public interface PurchasePaymentPaymentAdjustmentDao {

	void save(PurchasePaymentPaymentAdjustment paymentAdjustment);

	List<PurchasePaymentPaymentAdjustment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void deleteAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentPaymentAdjustment paymentAdjustment);
	
}