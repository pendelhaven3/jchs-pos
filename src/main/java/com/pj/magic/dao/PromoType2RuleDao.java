package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType2Rule;

public interface PromoType2RuleDao {

	void save(PromoType2Rule rule);

	List<PromoType2Rule> findAllByPromo(Promo promo);

	void delete(PromoType2Rule rule);
	
}