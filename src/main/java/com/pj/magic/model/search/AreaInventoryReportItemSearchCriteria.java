package com.pj.magic.model.search;

import com.pj.magic.model.InventoryCheck;
import com.pj.magic.model.Product;

public class AreaInventoryReportItemSearchCriteria {

	private InventoryCheck inventoryCheck;
	private Product product;
	private String unit;

	public InventoryCheck getInventoryCheck() {
		return inventoryCheck;
	}

	public void setInventoryCheck(InventoryCheck inventoryCheck) {
		this.inventoryCheck = inventoryCheck;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

}
