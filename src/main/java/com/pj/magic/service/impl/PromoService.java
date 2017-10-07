package com.pj.magic.service.impl;

import java.util.List;

import com.pj.magic.model.Manufacturer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;
import com.pj.magic.model.PromoType3Rule;
import com.pj.magic.model.PromoType3RulePromoProduct;
import com.pj.magic.model.PromoType4Rule;
import com.pj.magic.model.PromoType4RulePromoProduct;
import com.pj.magic.model.PromoType5Rule;
import com.pj.magic.model.PromoType5RulePromoProduct;
import com.pj.magic.model.search.PromoSearchCriteria;

public interface PromoService {

	List<Promo> getAllPromos();

	void save(Promo promo);

	Promo getPromo(long id);

	void save(PromoType2Rule rule);

	void delete(PromoType2Rule rule);

	List<Promo> getAllActivePromos();

	void save(PromoType3RulePromoProduct promoProduct);

	void delete(PromoType3RulePromoProduct promoProduct);

	void addAllPromoProducts(PromoType3Rule rule);

	void removeAllPromoProducts(PromoType3Rule rule);

	void save(PromoType4RulePromoProduct promoProduct);

	void delete(PromoType4RulePromoProduct promoProduct);

	void addAllPromoProducts(PromoType4Rule rule);

	void removeAllPromoProducts(PromoType4Rule rule);

	List<Promo> search(PromoSearchCriteria criteria);

	void save(PromoType5RulePromoProduct promoProduct);

	void removeAllPromoProducts(PromoType5Rule promoType5Rule);

	void addAllPromoProducts(PromoType5Rule promoType5Rule);

	void delete(PromoType5RulePromoProduct promoProduct);

	void addAllPromoProductsByManufacturer(PromoType4Rule rule, Manufacturer manufacturer);
	
}