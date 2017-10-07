package com.pj.magic.model.report;

import java.math.BigDecimal;

import com.pj.magic.model.Manufacturer;

public class SalesByManufacturerReportItem {

	private Manufacturer manufacturer;
	private BigDecimal amount;

	public Manufacturer getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(Manufacturer manufacturer) {
		this.manufacturer = manufacturer;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

}