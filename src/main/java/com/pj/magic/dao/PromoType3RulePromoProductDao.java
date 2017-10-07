package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType3RulePromoProduct;

public interface PromoType3RulePromoProductDao {

	void save(PromoType3RulePromoProduct promoProduct);
	
	List<PromoType3RulePromoProduct> findAllByRule(PromoType3Rule rule);

	void delete(PromoType3RulePromoProduct promoProduct);

	void deleteAllByRule(PromoType3Rule rule);
	
}