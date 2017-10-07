package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PurchasePaymentAdjustmentType;

public interface PurchasePaymentAdjustmentTypeService {

	List<PurchasePaymentAdjustmentType> getAllAdjustmentTypes();
	
	PurchasePaymentAdjustmentType getAdjustmentType(long id);

	void save(PurchasePaymentAdjustmentType type);

	PurchasePaymentAdjustmentType findAdjustmentTypeByCode(String code);

	List<PurchasePaymentAdjustmentType> getRegularAdjustmentTypes();
	
}