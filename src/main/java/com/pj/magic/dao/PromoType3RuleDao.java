package com.pj.magic.dao;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType3Rule;

public interface PromoType3RuleDao {

	void save(PromoType3Rule rule);

	PromoType3Rule findByPromo(Promo promo);

	void addAllPromoProducts(PromoType3Rule rule);
	
}