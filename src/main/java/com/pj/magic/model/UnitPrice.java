package com.pj.magic.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class UnitPrice implements Serializable {

    private static final long serialVersionUID = -9128080556646584915L;
    
    private String unit;
	private BigDecimal price;

	public UnitPrice(String unit, BigDecimal price) {
		this.unit = unit;
		this.price = price;
	}

	public UnitPrice(UnitPrice unitPrice) {
		unit = unitPrice.getUnit();
		price = unitPrice.getPrice();
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
