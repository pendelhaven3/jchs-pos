package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.search.PaymentCheckPaymentSearchCriteria;

public interface PaymentCheckPaymentDao {

	void save(PaymentCheckPayment check);

	List<PaymentCheckPayment> findAllByPayment(Payment payment);

	void deleteAllByPayment(Payment payment);

	void delete(PaymentCheckPayment checkPayment);

	List<PaymentCheckPayment> search(PaymentCheckPaymentSearchCriteria criteria);
	
}
