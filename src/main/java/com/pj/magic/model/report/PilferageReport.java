package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PilferageReport {

	private List<PilferageReportItem> items = new ArrayList<>();

	public List<PilferageReportItem> getItems() {
		return items;
	}

	public void setItems(List<PilferageReportItem> items) {
		this.items = items;
	}

	public BigDecimal getTotalAmount() {
		return items.stream()
				.map(item -> item.getAmount())
				.reduce(BigDecimal.ZERO, (x,y) -> x.add(y));
	}
	
}
