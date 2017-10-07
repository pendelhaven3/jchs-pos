package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.search.PurchasePaymentCreditCardPaymentSearchCriteria;

public interface PurchasePaymentCreditCardPaymentDao {

	void save(PurchasePaymentCreditCardPayment creditCardPayment);

	List<PurchasePaymentCreditCardPayment> findAllByPurchasePayment(PurchasePayment purchasePayment);

	void delete(PurchasePaymentCreditCardPayment creditCardPayment);

	List<PurchasePaymentCreditCardPayment> search(PurchasePaymentCreditCardPaymentSearchCriteria criteria);

}