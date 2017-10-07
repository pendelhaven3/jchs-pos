package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionSalesInvoice;

public interface PromoRedemptionSalesInvoiceDao {

	void save(PromoRedemptionSalesInvoice salesInvoice);

	List<PromoRedemptionSalesInvoice> findAllByPromoRedemption(PromoRedemption promoRedemption);

	void delete(PromoRedemptionSalesInvoice salesInvoice);

	void deleteAllByPromoRedemption(PromoRedemption promoRedemption);

}