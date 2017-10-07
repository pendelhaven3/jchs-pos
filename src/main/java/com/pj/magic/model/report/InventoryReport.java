package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pj.magic.Constants;

public class InventoryReport {

	private List<InventoryReportItem> items = new ArrayList<>();

	public List<InventoryReportItem> getItems() {
		return items;
	}

	public void setItems(List<InventoryReportItem> items) {
		this.items = items;
	}
	
	public BigDecimal getTotalCost() {
		BigDecimal total = Constants.ZERO;
		for (InventoryReportItem item : items) {
			total = total.add(item.getTotalCost());
		}
		return total;
	}
	
}