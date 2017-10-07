package com.pj.magic.service;

import java.util.List;

import com.pj.magic.exception.NotEnoughPromoPointsException;
import com.pj.magic.model.AvailedPromoPointsItem;
import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionSalesInvoice;
import com.pj.magic.model.PromoPointsClaim;
import com.pj.magic.model.SalesInvoice;
import com.pj.magic.model.search.PromoRedemptionSearchCriteria;

public interface PromoRedemptionService {

	void save(PromoRedemption promoRedemption);

	List<SalesInvoice> findAllUnreedemedSalesInvoices(Promo promo, Customer customer);
	
	void save(PromoRedemptionSalesInvoice salesInvoice);

	PromoRedemption getPromoRedemption(long id);

	void delete(PromoRedemptionSalesInvoice salesInvoice);

	void post(PromoRedemption promoRedemption);

	List<PromoRedemption> getPromoRedemptionsByPromo(Promo promo);

	List<PromoRedemption> findAllBySalesInvoice(SalesInvoice salesInvoice);
	
	List<PromoRedemption> findAllAvailedPromoRedemptions(SalesInvoice salesInvoice);

	List<AvailedPromoPointsItem> findAllAvailedPromoPoints(SalesInvoice salesInvoice);
	
	List<PromoPointsClaim> findAllPromoPointsClaimByPromoAndCustomer(Promo promo, Customer customer);

	PromoPointsClaim getPromoPointsClaim(long id);

	void save(PromoPointsClaim claim) throws NotEnoughPromoPointsException;

	void delete(PromoPointsClaim claim);

	void cancel(PromoRedemption promoRedemption);

	List<PromoRedemption> search(PromoRedemptionSearchCriteria promoRedemptionCriteria);
	
	int getNumberOfRedemptionsToday(Promo promo, Customer customer);
	
}