package com.pj.magic.dao;

import java.util.List;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentPaymentAdjustment;

public interface PaymentPaymentAdjustmentDao {

	void save(PaymentPaymentAdjustment adjustment);

	List<PaymentPaymentAdjustment> findAllByPayment(Payment payment);

	void deleteAllByPayment(Payment payment);

	void delete(PaymentPaymentAdjustment adjustment);
	
}
