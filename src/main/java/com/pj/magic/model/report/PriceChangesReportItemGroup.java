package com.pj.magic.model.report;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.pj.magic.model.ProductPriceHistory;

public class PriceChangesReportItemGroup implements Comparable<PriceChangesReportItemGroup> {

	private Date date;
	private List<ProductPriceHistory> items = new ArrayList<>();

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public List<ProductPriceHistory> getItems() {
		return items;
	}

	public void setItems(List<ProductPriceHistory> items) {
		this.items = items;
	}

	@Override
	public int compareTo(PriceChangesReportItemGroup o) {
		return date.compareTo(o.getDate());
	}

}