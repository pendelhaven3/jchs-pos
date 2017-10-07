package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import com.pj.magic.model.PaymentCashPayment;
import com.pj.magic.model.User;

public class PaymentCashPaymentRowItem {

	private PaymentCashPayment cashPayment;
	private BigDecimal amount;
	private Date receivedDate;
	private User receivedBy;

	public PaymentCashPaymentRowItem(PaymentCashPayment cashPayment) {
		this.cashPayment = cashPayment;
		amount = cashPayment.getAmount();
		receivedDate = cashPayment.getReceivedDate();
		receivedBy = cashPayment.getReceivedBy();
	}
	
	
	public PaymentCashPayment getCashPayment() {
		return cashPayment;
	}


	public void setCheck(PaymentCashPayment cashPayment) {
		this.cashPayment = cashPayment;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public Date getReceivedDate() {
		return receivedDate;
	}


	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}


	public User getReceivedBy() {
		return receivedBy;
	}


	public void setReceivedBy(User receivedBy) {
		this.receivedBy = receivedBy;
	}


	public void reset() {
		amount = cashPayment.getAmount();
		receivedDate = cashPayment.getReceivedDate();
		receivedBy = cashPayment.getReceivedBy();
	}

	public boolean isValid() {
		return amount != null && receivedDate != null && receivedBy != null;
	}

	public boolean isUpdating() {
		return cashPayment.getId() != null;
	}

}