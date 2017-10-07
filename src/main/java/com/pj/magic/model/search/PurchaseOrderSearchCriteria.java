package com.pj.magic.model.search;

import com.pj.magic.model.Supplier;

public class PurchaseOrderSearchCriteria {

	private Long purchaseOrderNumber;
	private Supplier supplier;
	private Boolean posted;

	public Long getPurchaseOrderNumber() {
		return purchaseOrderNumber;
	}

	public void setPurchaseOrderNumber(Long purchaseOrderNumber) {
		this.purchaseOrderNumber = purchaseOrderNumber;
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

}
