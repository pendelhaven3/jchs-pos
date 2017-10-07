package com.pj.magic.service;

import java.util.List;

import com.pj.magic.exception.PaymentAdjustmentAlreadyUsedException;
import com.pj.magic.model.PaymentAdjustment;
import com.pj.magic.model.search.PaymentAdjustmentSearchCriteria;

public interface PaymentAdjustmentService {

	void save(PaymentAdjustment paymentAdjustment);
	
	PaymentAdjustment getPaymentAdjustment(long id);
	
	List<PaymentAdjustment> getAllPaymentAdjustments();

	void post(PaymentAdjustment paymentAdjustment);

	void markAsPaid(PaymentAdjustment paymentAdjustment);

	PaymentAdjustment findPaymentAdjustmentByPaymentAdjustmentNumber(long paymentAdjustmentNumber);

	List<PaymentAdjustment> search(PaymentAdjustmentSearchCriteria criteria);

	List<PaymentAdjustment> getUnpaidPaymentAdjustments();

	void unpost(PaymentAdjustment paymentAdjustment) throws PaymentAdjustmentAlreadyUsedException;
	
}