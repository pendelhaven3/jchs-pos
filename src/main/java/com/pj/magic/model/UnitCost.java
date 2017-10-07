package com.pj.magic.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class UnitCost implements Serializable {

    private static final long serialVersionUID = 6995330371612371709L;
    
    private String unit;
	private BigDecimal grossCost = BigDecimal.ZERO;
	private BigDecimal finalCost = BigDecimal.ZERO;

	public UnitCost(String unit) {
		this.unit = unit;
	}
	
	public UnitCost(String unit, BigDecimal grossCost, BigDecimal finalCost) {
		this.unit = unit;
		this.grossCost = grossCost;
		this.finalCost = finalCost;
	}
	
	public UnitCost(UnitCost unitCost) {
		unit = unitCost.getUnit();
		grossCost = unitCost.getGrossCost();
		finalCost = unitCost.getFinalCost();
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public BigDecimal getGrossCost() {
		return grossCost;
	}

	public void setGrossCost(BigDecimal grossCost) {
		this.grossCost = grossCost;
	}

	public BigDecimal getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(BigDecimal finalCost) {
		this.finalCost = finalCost;
	}

}
