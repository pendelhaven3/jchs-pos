package com.pj.magic.model.report;

import java.math.BigDecimal;

import com.pj.magic.model.Product;

public class InventoryReportItem {

	private Product product;
	private String unit;
	private int quantity;
	private BigDecimal unitCost;

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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getCost() {
		return unitCost;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}

	public BigDecimal getTotalCost() {
		return unitCost.multiply(new BigDecimal(quantity));
	}

}