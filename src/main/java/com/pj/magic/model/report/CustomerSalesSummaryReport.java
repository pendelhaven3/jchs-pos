package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.pj.magic.Constants;

public class CustomerSalesSummaryReport {

	private List<CustomerSalesSummaryReportItem> items = new ArrayList<>();

	public List<CustomerSalesSummaryReportItem> getItems() {
		return items;
	}

	public void setItems(List<CustomerSalesSummaryReportItem> items) {
		this.items = items;
	}

	public BigDecimal getTotalNetAmount() {
		BigDecimal total = Constants.ZERO;
		for (CustomerSalesSummaryReportItem item : items) {
			total = total.add(item.getTotalAmount());
		}
		return total;
	}

	public BigDecimal getTotalCost() {
		BigDecimal total = Constants.ZERO;
		for (CustomerSalesSummaryReportItem item : items) {
			total = total.add(item.getTotalCost());
		}
		return total;
	}

	public BigDecimal getTotalProfit() {
		BigDecimal total = Constants.ZERO;
		for (CustomerSalesSummaryReportItem item : items) {
			total = total.add(item.getTotalProfit());
		}
		return total;
	}

}