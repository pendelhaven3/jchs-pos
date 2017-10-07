package com.pj.magic.dao;

import com.pj.magic.model.Promo;
import com.pj.magic.model.search.PromoSearchCriteria;

import java.util.List;

public interface PromoDao {

	List<Promo> getAll();

	Promo get(long id);

	void save(Promo promo);

	List<Promo> findAllByActive(boolean active);

	List<Promo> search(PromoSearchCriteria criteria);
	
}