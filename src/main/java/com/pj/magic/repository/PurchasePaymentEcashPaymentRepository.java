package com.pj.magic.repository;

import java.util.List;

import com.pj.magic.model.EcashReceiver;
import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentEcashPayment;
import com.pj.magic.model.search.PurchasePaymentEcashPaymentSearchCriteria;

public interface PurchasePaymentEcashPaymentRepository {

	void save(PurchasePaymentEcashPayment ecashPayment);
	
	List<PurchasePaymentEcashPayment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentEcashPayment ecashPayment);

	List<PurchasePaymentEcashPayment> search(PurchasePaymentEcashPaymentSearchCriteria criteria);

	PurchasePaymentEcashPayment findOneByEcashReceiver(EcashReceiver ecashReceiver);
	
}