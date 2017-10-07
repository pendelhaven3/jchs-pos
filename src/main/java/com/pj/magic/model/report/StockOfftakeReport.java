package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.List;

public class StockOfftakeReport {
	
	private List<StockOfftakeReportItem> items = new ArrayList<>();

	public List<StockOfftakeReportItem> getItems() {
		return items;
	}

	public void setItems(List<StockOfftakeReportItem> items) {
		this.items = items;
	}
	
}
