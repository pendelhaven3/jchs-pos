package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.Manufacturer;

public class StockOfftakeReportCriteria {

	private Manufacturer manufacturer;
	private Date fromDate;
	private Date toDate;

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

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

}
