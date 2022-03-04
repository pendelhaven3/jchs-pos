package com.pj.magic.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryCheckSummaryItem implements Comparable<InventoryCheckSummaryItem> {

	private InventoryCheck parent;
	private Product2 product;
	private String unit;
	private int quantity;
	
	private String code; // derived field

	public int getQuantityDifference() {
		return quantity - getBeginningInventory();
	}

	public BigDecimal getBeginningValue() {
		return product.getTotalValue(unit);
	}
	
	public BigDecimal getActualValue() {
		return product.getFinalCost(unit).multiply(new BigDecimal(quantity));
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
