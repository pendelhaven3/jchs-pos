package com.pj.magic.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.pj.magic.dao.AdjustmentTypeDao;
import com.pj.magic.model.AdjustmentType;
import com.pj.magic.service.AdjustmentTypeService;

@Service
public class AdjustmentTypeServiceImpl implements AdjustmentTypeService {

	@Autowired private AdjustmentTypeDao adjustmentTypeDao;
	
	@Override
	public List<AdjustmentType> getAllAdjustmentTypes() {
		return adjustmentTypeDao.getAll();
	}

	@Override
	public AdjustmentType getType(long id) {
		return adjustmentTypeDao.get(id);
	}

	@Transactional
	@Override
	public void save(AdjustmentType type) {
		adjustmentTypeDao.save(type);
	}

	@Override
	public AdjustmentType findAdjustmentTypeByCode(String code) {
		return adjustmentTypeDao.findByCode(code);
	}

	@Override
	public List<AdjustmentType> getRegularAdjustmentTypes() {
		List<AdjustmentType> adjustmentTypes = getAllAdjustmentTypes();
		return new ArrayList<AdjustmentType>(Collections2.filter(adjustmentTypes, new Predicate<AdjustmentType>() {

			@Override
			public boolean apply(AdjustmentType adjustmentType) {
				String code = adjustmentType.getCode();
				return !Arrays.asList(AdjustmentType.SALES_RETURN_CODE,
						AdjustmentType.BAD_STOCK_RETURN_CODE,
						AdjustmentType.NO_MORE_STOCK_ADJUSTMENT_CODE).contains(code);
			}
		}));
	}

}