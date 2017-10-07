package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductPriceHistory {

	private Long id;
	private PricingScheme pricingScheme;
	private Product product;
	private List<UnitPrice> unitPrices = new ArrayList<>();
	private List<UnitPrice> previousUnitPrices = new ArrayList<>();
	private List<String> activeUnits = new ArrayList<>();
	private Date updateDate;
	private User updatedBy;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public List<UnitPrice> getUnitPrices() {
		return unitPrices;
	}

	public void setUnitPrices(List<UnitPrice> unitPrices) {
		this.unitPrices = unitPrices;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}

	public BigDecimal getUnitPrice(String unit) {
		for (UnitPrice unitPrice : unitPrices) {
			if (unitPrice.getUnit().equals(unit)) {
				return unitPrice.getPrice();
			}
		}
		return null;
	}

	public List<String> getActiveUnits() {
		return activeUnits;
	}

	public void setActiveUnits(List<String> activeUnits) {
		this.activeUnits = activeUnits;
	}
	
	public BigDecimal getActiveUnitPrice(String unit) {
		if (activeUnits.contains(unit)) {
			return getUnitPrice(unit);
		}
		return null;
	}
	
	public BigDecimal getPreviousActiveUnitPrice(String unit) {
		if (activeUnits.contains(unit)) {
			return getPreviousUnitPrice(unit);
		}
		return null;
	}
	
	public BigDecimal getPreviousUnitPrice(String unit) {
		return previousUnitPrices.stream()
				.filter(unitPrice -> unitPrice.getUnit().equals(unit))
				.map(unitPrice -> unitPrice.getPrice())
				.findAny()
				.orElse(null);
	}
	
	public BigDecimal getPercentIncrease() {
		String maxUnit = getMaxUnit();
		BigDecimal unitPrice = getUnitPrice(maxUnit);
		BigDecimal previousUnitPrice = getPreviousUnitPrice(maxUnit);
		
		if (BigDecimal.ZERO.setScale(2).equals(previousUnitPrice.setScale(2))) {
			return null;
		}
		
		return unitPrice.subtract(previousUnitPrice).divide(previousUnitPrice, 4, RoundingMode.HALF_UP)
				.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
	}
	
	private String getMaxUnit() {
		if (getUnitPrice(Unit.CASE) != null) {
			return Unit.CASE;
		}
		if (getActiveUnitPrice(Unit.TIE) != null) {
			return Unit.TIE;
		}
		if (getActiveUnitPrice(Unit.CARTON) != null) {
			return Unit.CARTON;
		}
		if (getActiveUnitPrice(Unit.DOZEN) != null) {
			return Unit.DOZEN;
		}
		return Unit.PIECES;
	}
	
	public void setPreviousUnitPrices(List<UnitPrice> previousUnitPrices) {
		this.previousUnitPrices = previousUnitPrices;
	}
	
	public List<UnitPrice> getPreviousUnitPrices() {
		return previousUnitPrices;
	}

}