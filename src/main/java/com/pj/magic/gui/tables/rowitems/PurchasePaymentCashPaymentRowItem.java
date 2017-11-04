package com.pj.magic.gui.tables.rowitems;

import java.math.BigDecimal;
import java.util.Date;

import com.pj.magic.model.PurchasePaymentCashPayment;

public class PurchasePaymentCashPaymentRowItem {

	private PurchasePaymentCashPayment cashPayment;
	private BigDecimal amount;
	private Date paidDate;

	public PurchasePaymentCashPaymentRowItem(PurchasePaymentCashPayment cashPayment) {
		this.cashPayment = cashPayment;
		amount = cashPayment.getAmount();
		paidDate = cashPayment.getPaidDate();
	}
	
	
	public PurchasePaymentCashPayment getCashPayment() {
		return cashPayment;
	}


	public void setCheck(PurchasePaymentCashPayment cashPayment) {
		this.cashPayment = cashPayment;
	}


	public BigDecimal getAmount() {
		return amount;
	}


	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}


	public Date getPaidDate() {
		return paidDate;
	}


	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}


	public void reset() {
		amount = cashPayment.getAmount();
		paidDate = cashPayment.getPaidDate();
	}

	public boolean isValid() {
		return amount != null && paidDate != null;
	}

	public boolean isUpdating() {
		return cashPayment.getId() != null;
	}

}