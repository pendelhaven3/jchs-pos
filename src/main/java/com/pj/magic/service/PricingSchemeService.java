package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PricingScheme;

public interface PricingSchemeService {

	List<PricingScheme> getAllPricingSchemes();
	
	void save(PricingScheme pricingScheme);
	
	PricingScheme get(long id);
	
}
