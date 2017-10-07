package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PaymentCheckPayment;

public class CustomerCheckPaymentsReport {

	private List<PaymentCheckPayment> checkPayments = new ArrayList<>();

	public List<PaymentCheckPayment> getCheckPayments() {
		return checkPayments;
	}

	public void setCheckPayments(List<PaymentCheckPayment> checkPayments) {
		this.checkPayments = checkPayments;
	}
	
	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PaymentCheckPayment checkPayment : checkPayments) {
			total = total.add(checkPayment.getAmount());
		}
		return total;
	}
	
}