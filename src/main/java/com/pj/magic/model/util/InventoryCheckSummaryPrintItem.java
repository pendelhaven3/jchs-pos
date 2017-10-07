package com.pj.magic.model.util;

import java.math.BigDecimal;

import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Product;

public class InventoryCheckSummaryPrintItem {

	private Product product;
	private String unit;
	private int quantity;
	private BigDecimal cost;
	private BigDecimal quantityValue;
	private InventoryCheckSummaryItem item;

	public InventoryCheckSummaryPrintItem(InventoryCheckSummaryItem item, InventoryCheckReportType reportType) {
		product = item.getProduct();
		unit = item.getUnit();
		cost = item.getCost();
		
		switch (reportType) {
		case BEGINNING_INVENTORY:
			quantity = item.getBeginningInventory();
			quantityValue = item.getBeginningValue();
			break;
		case ACTUAL_COUNT:
			quantity = item.getQuantity();
			quantityValue = item.getActualValue();
			break;
		case COMPLETE:
			this.item = item;
			quantityValue = item.getActualValue();
			break;
		}
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

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getQuantityValue() {
		return quantityValue;
	}

	public void setQuantityValue(BigDecimal quantityValue) {
		this.quantityValue = quantityValue;
	}

	public int getBeginningInventory() {
		return item.getBeginningInventory();
	}
	
	public int getActualCount() {
		return item.getQuantity();
	}
	
	public int getQuantityDifference() {
		return item.getQuantityDifference();
	}
	
}