package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductQuantityDiscrepancyReport {

	private Date date;
	private List<ProductQuantityDiscrepancyReportItem> items = new ArrayList<>();

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<ProductQuantityDiscrepancyReportItem> getItems() {
		return items;
	}

}
