package com.pj.magic.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PromoRedemptionReward {

	private Long id;
	private PromoRedemption parent;
	private Product product;
	private String unit;
	private Integer quantity;

	public BigDecimal getTotalCost() {
		return product.getFinalCost(unit).multiply(new BigDecimal(quantity)).setScale(2, RoundingMode.HALF_UP);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PromoRedemption getParent() {
		return parent;
	}

	public void setParent(PromoRedemption parent) {
		this.parent = parent;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

}