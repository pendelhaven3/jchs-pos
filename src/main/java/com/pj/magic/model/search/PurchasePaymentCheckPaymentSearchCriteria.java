package com.pj.magic.model.search;

import java.math.BigDecimal;
import java.util.Date;

import com.pj.magic.model.Supplier;

public class PurchasePaymentCheckPaymentSearchCriteria {

	private Boolean posted;
	private Date fromDate;
	private Date toDate;
	private Supplier supplier;
	private BigDecimal amount;

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

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
	
}