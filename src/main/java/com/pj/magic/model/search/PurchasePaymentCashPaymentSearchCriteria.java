package com.pj.magic.model.search;

import java.util.Date;

public class PurchasePaymentCashPaymentSearchCriteria {

	private Boolean posted;
	private Date fromDate;
	private Date toDate;

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

}