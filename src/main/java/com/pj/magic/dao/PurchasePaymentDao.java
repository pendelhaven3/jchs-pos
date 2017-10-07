package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePayment;
import com.pj.magic.model.search.PurchasePaymentSearchCriteria;

public interface PurchasePaymentDao {

	void save(PurchasePayment purchasePayment);
	
	PurchasePayment get(long id);
	
	List<PurchasePayment> search(PurchasePaymentSearchCriteria criteria);
	
}