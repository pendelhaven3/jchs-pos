package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.CreditCard;
import com.pj.magic.model.Supplier;

public class PurchasePaymentCreditCardPaymentSearchCriteria {

	private Boolean posted;
	private Date fromDate;
	private Date toDate;
	private Supplier supplier;
	private CreditCard creditCard;
	private Boolean notIncludedInStatement;
	private String customerNumber;
	private String orderBy;

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

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

	public Boolean getNotIncludedInStatement() {
		return notIncludedInStatement;
	}

	public void setNotIncludedInStatement(Boolean notIncludedInStatement) {
		this.notIncludedInStatement = notIncludedInStatement;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
}