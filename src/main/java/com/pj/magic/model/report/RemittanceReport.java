package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.PaymentCheckPayment;
import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.util.TimePeriod;

public class RemittanceReport {

	private Date reportDate;
	private TimePeriod timePeriod;
	private PaymentTerminal paymentTerminal;
	private List<PaymentCashPayment> cashPayments = new ArrayList<>();
	private List<PaymentCheckPayment> checkPayments = new ArrayList<>();

	public List<PaymentCashPayment> getCashPayments() {
		return cashPayments;
	}

	public void setCashPayments(List<PaymentCashPayment> cashPayments) {
		this.cashPayments = cashPayments;
	}

	public List<PaymentCheckPayment> getCheckPayments() {
		return checkPayments;
	}

	public void setCheckPayments(List<PaymentCheckPayment> checkPayments) {
		this.checkPayments = checkPayments;
	}

	public BigDecimal getTotalCashPayments() {
		BigDecimal total = Constants.ZERO;
		for (PaymentCashPayment cashPayment : cashPayments) {
			total = total.add(cashPayment.getAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalCheckPayments() {
		BigDecimal total = Constants.ZERO;
		for (PaymentCheckPayment checkPayment : checkPayments) {
			total = total.add(checkPayment.getAmount());
		}
		return total;
	}

	public Date getReportDate() {
		return reportDate;
	}

	public void setReportDate(Date reportDate) {
		this.reportDate = reportDate;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}
	
}