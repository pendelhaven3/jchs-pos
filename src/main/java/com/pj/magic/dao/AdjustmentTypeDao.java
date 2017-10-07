package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.AdjustmentType;

public interface AdjustmentTypeDao {

	void save(AdjustmentType type);
	
	List<AdjustmentType> getAll();
	
	AdjustmentType get(long id);

	AdjustmentType findByCode(String code);
	
}