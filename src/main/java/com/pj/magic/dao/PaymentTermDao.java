package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.PaymentTerm;

public interface PaymentTermDao {

	void save(PaymentTerm paymentTerm);
	
	PaymentTerm get(long id);
	
	List<PaymentTerm> getAll();
	
}
