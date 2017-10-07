package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.List;

public class SalesByManufacturerReport {

	private List<SalesByManufacturerReportItem> items = new ArrayList<>();

	public List<SalesByManufacturerReportItem> getItems() {
		return items;
	}

	public void setItems(List<SalesByManufacturerReportItem> items) {
		this.items = items;
	}

}