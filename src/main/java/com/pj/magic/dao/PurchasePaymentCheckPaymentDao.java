package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCheckPayment;
import com.pj.magic.model.search.PurchasePaymentCheckPaymentSearchCriteria;

public interface PurchasePaymentCheckPaymentDao {

	void save(PurchasePaymentCheckPayment checkPayment);

	List<PurchasePaymentCheckPayment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentCheckPayment checkPayment);

	List<PurchasePaymentCheckPayment> search(
			PurchasePaymentCheckPaymentSearchCriteria criteria);

}