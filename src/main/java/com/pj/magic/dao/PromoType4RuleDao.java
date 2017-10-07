package com.pj.magic.dao;

import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType4Rule;

public interface PromoType4RuleDao {

	void save(PromoType4Rule rule);

	PromoType4Rule findByPromo(Promo promo);

	void addAllPromoProducts(PromoType4Rule rule);

	void addAllPromoProductsByManufacturer(PromoType4Rule rule, Manufacturer manufacturer);
	
}