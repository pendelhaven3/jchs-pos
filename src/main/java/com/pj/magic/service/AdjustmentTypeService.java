package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.AdjustmentType;

public interface AdjustmentTypeService {

	List<AdjustmentType> getAllAdjustmentTypes();
	
	AdjustmentType getType(long id);

	void save(AdjustmentType type);

	AdjustmentType findAdjustmentTypeByCode(String code);

	List<AdjustmentType> getRegularAdjustmentTypes();
	
}