package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoPointsClaim;

public interface PromoPointsClaimDao {

	List<PromoPointsClaim> findAllByPromoAndCustomer(Promo promo, Customer customer);

	PromoPointsClaim get(long id);

	void save(PromoPointsClaim claim);

	void delete(PromoPointsClaim claim);
	
}