package com.pj.magic.dao;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType1Rule;

public interface PromoType1RuleDao {

	PromoType1Rule findByPromo(Promo promo);
	
	void save(PromoType1Rule rule);
	
}