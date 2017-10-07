package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.ReceivingReceipt;
import com.pj.magic.model.Supplier;

public class PurchaseReturnSearchCriteria {

	private Long purchaseReturnNumber;
	private Boolean posted;
	private Supplier supplier;
	private Date postDate;
	private Date postDateFrom;
	private Date postDateTo;
	private ReceivingReceipt receivingReceipt;
	private Boolean paid;

	public Long getPurchaseReturnNumber() {
		return purchaseReturnNumber;
	}

	public void setPurchaseReturnNumber(Long purchaseReturnNumber) {
		this.purchaseReturnNumber = purchaseReturnNumber;
	}

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Date getPostDate() {
		return postDate;
	}

	public void setPostDate(Date postDate) {
		this.postDate = postDate;
	}

	public Date getPostDateFrom() {
		return postDateFrom;
	}

	public void setPostDateFrom(Date postDateFrom) {
		this.postDateFrom = postDateFrom;
	}

	public Date getPostDateTo() {
		return postDateTo;
	}

	public void setPostDateTo(Date postDateTo) {
		this.postDateTo = postDateTo;
	}

	public ReceivingReceipt getReceivingReceipt() {
		return receivingReceipt;
	}

	public void setReceivingReceipt(ReceivingReceipt receivingReceipt) {
		this.receivingReceipt = receivingReceipt;
	}

	public Boolean getPaid() {
		return paid;
	}

	public void setPaid(Boolean paid) {
		this.paid = paid;
	}

}