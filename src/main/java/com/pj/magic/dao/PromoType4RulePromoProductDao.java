package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PromoType4Rule;
import com.pj.magic.model.PromoType4RulePromoProduct;

public interface PromoType4RulePromoProductDao {

	void save(PromoType4RulePromoProduct promoProduct);
	
	List<PromoType4RulePromoProduct> findAllByRule(PromoType4Rule rule);

	void delete(PromoType4RulePromoProduct promoProduct);

	void deleteAllByRule(PromoType4Rule rule);
	
}