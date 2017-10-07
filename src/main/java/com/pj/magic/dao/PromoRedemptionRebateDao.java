package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionRebate;

public interface PromoRedemptionRebateDao {

	void save(PromoRedemptionRebate rebate);

	List<PromoRedemptionRebate> findAllByPromoRedemption(PromoRedemption promoRedemption);
	
}