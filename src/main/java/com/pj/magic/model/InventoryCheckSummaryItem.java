package com.pj.magic.model;

import java.math.BigDecimal;

public class InventoryCheckSummaryItem implements Comparable<InventoryCheckSummaryItem> {

	private InventoryCheck parent;
	private Product product;
	private String unit;
	private int quantity;

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

	public int getQuantityDifference() {
		return quantity - getBeginningInventory();
	}

	public BigDecimal getBeginningValue() {
		return product.getTotalValue(unit);
	}
	
	public BigDecimal getActualValue() {
		return product.getFinalCost(unit).multiply(new BigDecimal(quantity));
	}

	public InventoryCheck getParent() {
		return parent;
	}

	public void setParent(InventoryCheck parent) {
		this.parent = parent;
	}

	public BigDecimal getCost() {
		return product.getFinalCost(unit);
	}

	@Override
	public int compareTo(InventoryCheckSummaryItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return -1 * Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

	public int getBeginningInventory() {
		return product.getUnitQuantity(unit);
	}
	
}
