package com.pj.magic.model.report;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pj.magic.Constants;
import com.pj.magic.model.Customer;
import com.pj.magic.util.FormatterUtil;

public class PostedSalesAndProfitReport {

	private Customer customer;
	private Date transactionDateFrom;
	private Date transactionDateTo;
	private List<PostedSalesAndProfitReportItem> items;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getTransactionDateFrom() {
		return transactionDateFrom;
	}

	public void setTransactionDateFrom(Date transactionDateFrom) {
		this.transactionDateFrom = transactionDateFrom;
	}

	public Date getTransactionDateTo() {
		return transactionDateTo;
	}

	public void setTransactionDateTo(Date transactionDateTo) {
		this.transactionDateTo = transactionDateTo;
	}

	public List<PostedSalesAndProfitReportItem> getItems() {
		return items;
	}

	public void setItems(List<PostedSalesAndProfitReportItem> items) {
		this.items = items;
	}

	public String getCustomerName() {
		return (customer != null) ? customer.getCode() + " - " + customer.getName() : "ALL";
	}

	public String getTransactionDate() {
		if (transactionDateFrom.equals(transactionDateTo)) {
			return FormatterUtil.formatDate(transactionDateFrom);
		} else {
			return FormatterUtil.formatDate(transactionDateFrom) + " - " + 
						FormatterUtil.formatDate(transactionDateTo);
		}
	}

	public BigDecimal getTotalAmount() {
		BigDecimal total = Constants.ZERO;
		for (PostedSalesAndProfitReportItem item : items) {
			total = total.add(item.getTotalAmount());
		}
		return total;
	}
	
	public BigDecimal getTotalDiscounts() {
		BigDecimal total = Constants.ZERO;
		for (PostedSalesAndProfitReportItem item : items) {
			total = total.add(item.getTotalDiscounts());
		}
		return total;
	}
	
	public BigDecimal getTotalNetAmount() {
		BigDecimal total = Constants.ZERO;
		for (PostedSalesAndProfitReportItem item : items) {
			total = total.add(item.getNetAmount());
		}
		return total;
	}

	public BigDecimal getTotalNetCost() {
		BigDecimal total = Constants.ZERO;
		for (PostedSalesAndProfitReportItem item : items) {
			total = total.add(item.getNetCost());
		}
		return total;
	}
	
	public BigDecimal getTotalNetProfit() {
		BigDecimal total = Constants.ZERO;
		for (PostedSalesAndProfitReportItem item : items) {
			total = total.add(item.getNetProfit());
		}
		return total;
	}
	
}