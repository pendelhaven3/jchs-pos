package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PurchasePaymentAdjustmentType;

public interface PurchasePaymentAdjustmentTypeDao {

	void save(PurchasePaymentAdjustmentType adjustmentType);
	
	List<PurchasePaymentAdjustmentType> getAll();
	
	PurchasePaymentAdjustmentType get(long id);

	PurchasePaymentAdjustmentType findByCode(String code);
	
}