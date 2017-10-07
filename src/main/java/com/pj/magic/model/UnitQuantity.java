package com.pj.magic.model;

import java.io.Serializable;

public class UnitQuantity implements Serializable {

    private static final long serialVersionUID = 2053446161848737786L;
    
    private String unit;
	private int quantity;
	
	public UnitQuantity(String unit, int quantity) {
		this.unit = unit;
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
}
