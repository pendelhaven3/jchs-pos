package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PricingScheme;

public interface PricingSchemeDao {

	void save(PricingScheme pricingScheme);
	
	List<PricingScheme> getAll();
	
	PricingScheme get(long id);

	void createProductPrices(PricingScheme pricingScheme);
	
}
