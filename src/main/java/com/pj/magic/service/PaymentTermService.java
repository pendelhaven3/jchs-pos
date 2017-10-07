package com.pj.magic.service;

import java.util.List;

import com.pj.magic.model.PaymentTerm;

public interface PaymentTermService {

	void save(PaymentTerm paymentTerm);
	
	PaymentTerm getPaymentTerm(long id);
	
	List<PaymentTerm> getAllPaymentTerms();
	
}
