package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Customer;

public class SalesByManufacturerReportCriteria {

	private Date fromDate;
	private Date toDate;
	private Customer customer;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

}