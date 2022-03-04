package com.pj.magic.model.util;

import java.math.BigDecimal;

import com.pj.magic.model.InventoryCheckSummaryItem;
import com.pj.magic.model.Product2;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryCheckSummaryPrintItem {

	private Product2 product;
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