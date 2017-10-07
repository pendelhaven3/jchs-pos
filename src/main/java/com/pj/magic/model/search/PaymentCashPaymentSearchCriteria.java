package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.PaymentTerminal;
import com.pj.magic.model.util.TimePeriod;

public class PaymentCashPaymentSearchCriteria {

	private Date paymentDate;
	private Boolean paid;
	private PaymentTerminal paymentTerminal;
	private TimePeriod timePeriod;

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public PaymentTerminal getPaymentTerminal() {
		return paymentTerminal;
	}

	public void setPaymentTerminal(PaymentTerminal paymentTerminal) {
		this.paymentTerminal = paymentTerminal;
	}

	public TimePeriod getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(TimePeriod timePeriod) {
		this.timePeriod = timePeriod;
	}

}