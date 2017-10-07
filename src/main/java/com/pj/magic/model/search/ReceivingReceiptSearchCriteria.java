package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Supplier;

public class ReceivingReceiptSearchCriteria {

	private Long receivingReceiptNumber;
	private Supplier supplier;
	private Boolean posted;
	private Boolean cancelled;
	private Date receivedDate;
	private Boolean paid;
	private String orderBy;
	private Date receivedDateFrom;
	private Date receivedDateTo;
	
	public Long getReceivingReceiptNumber() {
		return receivingReceiptNumber;
	}

	public void setReceivingReceiptNumber(Long receivingReceiptNumber) {
		this.receivingReceiptNumber = receivingReceiptNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public Date getReceivedDateFrom() {
		return receivedDateFrom;
	}

	public void setReceivedDateFrom(Date receivedDateFrom) {
		this.receivedDateFrom = receivedDateFrom;
	}

	public Date getReceivedDateTo() {
		return receivedDateTo;
	}

	public void setReceivedDateTo(Date receivedDateTo) {
		this.receivedDateTo = receivedDateTo;
	}

}