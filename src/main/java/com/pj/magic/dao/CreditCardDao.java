package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.CreditCard;

public interface CreditCardDao {

	void save(CreditCard creditCard);
	
	CreditCard get(long id);
	
	List<CreditCard> getAll();
	
}