package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCashPayment;
import com.pj.magic.model.search.PurchasePaymentCashPaymentSearchCriteria;

public interface PurchasePaymentCashPaymentDao {

	void save(PurchasePaymentCashPayment cashPayment);

	List<PurchasePaymentCashPayment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentCashPayment cashPayment);

	List<PurchasePaymentCashPayment> search(PurchasePaymentCashPaymentSearchCriteria criteria);

}