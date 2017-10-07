package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.search.PaymentSearchCriteria;

public interface PaymentDao {

	void save(Payment payment);

	Payment get(long id);

	List<Payment> search(PaymentSearchCriteria criteria);

}