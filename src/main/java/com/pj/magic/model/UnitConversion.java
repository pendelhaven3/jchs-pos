package com.pj.magic.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class UnitConversion implements Serializable {

    private static final long serialVersionUID = 4920172342587480215L;
    
    private String unit;
	private int quantity;

	public UnitConversion() {
		// default constructor
	}
	
	public UnitConversion(String unit, int quantity) {
		this.unit = unit;
		this.quantity = quantity;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(unit)
			.append(quantity)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof UnitConversion)) {
            return false;
        }
        UnitConversion other = (UnitConversion)obj;		
		return new EqualsBuilder()
			.append(unit, other.getUnit())
			.append(quantity, other.getQuantity())
			.isEquals();
	}
	
}
