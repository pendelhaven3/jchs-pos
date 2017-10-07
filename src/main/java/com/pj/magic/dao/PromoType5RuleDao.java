package com.pj.magic.dao;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType5Rule;

public interface PromoType5RuleDao {

	void save(PromoType5Rule rule);

	PromoType5Rule findByPromo(Promo promo);

	void addAllPromoProducts(PromoType5Rule rule);
	
}