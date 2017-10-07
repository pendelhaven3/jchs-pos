package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.pj.magic.dao.PurchasePaymentAdjustmentTypeDao;
import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.service.PurchasePaymentAdjustmentTypeService;

@Service
public class PurchasePaymentAdjustmentTypeServiceImpl implements PurchasePaymentAdjustmentTypeService {

	@Autowired private PurchasePaymentAdjustmentTypeDao purchasePaymentAdjustmentTypeDao;
	
	@Override
	public List<PurchasePaymentAdjustmentType> getAllAdjustmentTypes() {
		return purchasePaymentAdjustmentTypeDao.getAll();
	}

	@Override
	public PurchasePaymentAdjustmentType getAdjustmentType(long id) {
		return purchasePaymentAdjustmentTypeDao.get(id);
	}

	@Transactional
	@Override
	public void save(PurchasePaymentAdjustmentType type) {
		purchasePaymentAdjustmentTypeDao.save(type);
	}

	@Override
	public PurchasePaymentAdjustmentType findAdjustmentTypeByCode(String code) {
		return purchasePaymentAdjustmentTypeDao.findByCode(code);
	}

	@Override
	public List<PurchasePaymentAdjustmentType> getRegularAdjustmentTypes() {
		List<PurchasePaymentAdjustmentType> adjustmentTypes = getAllAdjustmentTypes();
		return new ArrayList<PurchasePaymentAdjustmentType>(
				Collections2.filter(adjustmentTypes, new Predicate<PurchasePaymentAdjustmentType>() {

			@Override
			public boolean apply(PurchasePaymentAdjustmentType adjustmentType) {
				String code = adjustmentType.getCode();
				return !Arrays.asList(
						PurchasePaymentAdjustmentType.PURCHASE_RETURN_GOOD_STOCK_CODE,
						PurchasePaymentAdjustmentType.PURCHASE_RETURN_BAD_STOCK_CODE).contains(code);
			}
		}));
	}

}