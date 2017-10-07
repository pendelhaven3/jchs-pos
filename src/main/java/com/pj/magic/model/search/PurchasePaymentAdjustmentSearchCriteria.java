package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.PurchasePaymentAdjustmentType;
import com.pj.magic.model.Supplier;

public class PurchasePaymentAdjustmentSearchCriteria {

	private Long purchasePaymentAdjustmentNumber;
	private Supplier supplier;
	private PurchasePaymentAdjustmentType adjustmentType;
	private Boolean posted;
	private Date postDate;
	private Date postDateFrom;
	private Date postDateTo;

	public Long getPaymentAdjustmentNumber() {
		return purchasePaymentAdjustmentNumber;
	}

	public void setPaymentAdjustmentNumber(Long paymentAdjustmentNumber) {
		this.purchasePaymentAdjustmentNumber = paymentAdjustmentNumber;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public Long getPurchasePaymentAdjustmentNumber() {
		return purchasePaymentAdjustmentNumber;
	}

	public void setPurchasePaymentAdjustmentNumber(Long purchasePaymentAdjustmentNumber) {
		this.purchasePaymentAdjustmentNumber = purchasePaymentAdjustmentNumber;
	}

	public PurchasePaymentAdjustmentType getAdjustmentType() {
		return adjustmentType;
	}

	public void setAdjustmentType(PurchasePaymentAdjustmentType adjustmentType) {
		this.adjustmentType = adjustmentType;
	}

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
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

}