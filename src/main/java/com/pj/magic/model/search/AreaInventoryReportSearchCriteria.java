package com.pj.magic.model.search;

import com.pj.magic.model.InventoryCheck;

public class AreaInventoryReportSearchCriteria {

	private InventoryCheck inventoryCheck;
	private Boolean reviewed;

	public InventoryCheck getInventoryCheck() {
		return inventoryCheck;
	}

	public void setInventoryCheck(InventoryCheck inventoryCheck) {
		this.inventoryCheck = inventoryCheck;
	}

	public Boolean getReviewed() {
		return reviewed;
	}

	public void setReviewed(Boolean reviewed) {
		this.reviewed = reviewed;
	}

}