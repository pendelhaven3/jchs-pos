package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentReceivingReceipt;

public interface PurchasePaymentReceivingReceiptDao {

	void insert(PurchasePaymentReceivingReceipt purchasePaymentReceivingReceipt);

	List<PurchasePaymentReceivingReceipt> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void deleteAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentReceivingReceipt purchasePaymentReceivingReceipt);

}