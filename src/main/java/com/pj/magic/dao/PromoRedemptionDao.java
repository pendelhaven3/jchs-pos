package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.PromoRedemptionSearchCriteria;

public interface PromoRedemptionDao {

	void save(PromoRedemption promoRedemption);

	PromoRedemption get(long id);

	List<PromoRedemption> findAllByPromo(Promo promo);

	void insertNewPromoRedemptionSequence(Promo promo);

	List<PromoRedemption> findAllBySalesInvoice(SalesInvoice salesInvoice);

	List<PromoRedemption> search(PromoRedemptionSearchCriteria criteria);
	
}