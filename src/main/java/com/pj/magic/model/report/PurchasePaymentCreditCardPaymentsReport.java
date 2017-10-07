package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.CreditCard;
import com.pj.magic.model.PurchasePaymentCreditCardPayment;
import com.pj.magic.model.Supplier;

public class PurchasePaymentCreditCardPaymentsReport {

	private Date fromDate;
	private Date toDate;
	private Supplier supplier;
	private CreditCard creditCard;
	private List<PurchasePaymentCreditCardPayment> creditCardPayments = new ArrayList<>();

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<PurchasePaymentCreditCardPayment> getCreditCardPayments() {
		return creditCardPayments;
	}

	public void setCreditCardPayments(List<PurchasePaymentCreditCardPayment> creditCardPayments) {
		this.creditCardPayments = creditCardPayments;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PurchasePaymentCreditCardPayment creditCardPayment : creditCardPayments) {
			total = total.add(creditCardPayment.getAmount());
		}
		return total;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}
	
}