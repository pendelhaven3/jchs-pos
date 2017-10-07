package com.pj.magic.exception;

import com.pj.magic.model.Payment;
import com.pj.magic.model.PaymentAdjustment;

public class PaymentAdjustmentAlreadyUsedException extends Exception {

	private PaymentAdjustment paymentAdjustment;
	private Payment payment;
	
	public PaymentAdjustmentAlreadyUsedException(PaymentAdjustment paymentAdjustment, Payment payment) {
		this.paymentAdjustment = paymentAdjustment;
		this.payment = payment;
	}
	
	public PaymentAdjustment getPaymentAdjustment() {
		return paymentAdjustment;
	}
	
	public Payment getPayment() {
		return payment;
	}
	
}
