package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PromoType5Rule;
import com.pj.magic.model.PromoType5RulePromoProduct;

public interface PromoType5RulePromoProductDao {

	void save(PromoType5RulePromoProduct promoProduct);
	
	List<PromoType5RulePromoProduct> findAllByRule(PromoType5Rule rule);

	void delete(PromoType5RulePromoProduct promoProduct);

	void deleteAllByRule(PromoType5Rule rule);

}