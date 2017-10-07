package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PromoRedemption;
import com.pj.magic.model.PromoRedemptionReward;

public interface PromoRedemptionRewardDao {

	void save(PromoRedemptionReward reward);

	List<PromoRedemptionReward> findAllByPromoRedemption(PromoRedemption promoRedemption);
	
}