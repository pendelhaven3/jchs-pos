package com.pj.magic.gui.tables.rowitems;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.model.Product;
import com.pj.magic.model.PromoType2Rule;

public class PromoType2RuleRowItem {

	private PromoType2Rule rule;
	private Product promoProduct;
	private String promoUnit;
	private Integer promoQuantity;
	private Product freeProduct;
	private String freeUnit;
	private Integer freeQuantity;

	public PromoType2RuleRowItem(PromoType2Rule rule) {
		this.rule = rule;
		reset();
	}
	
	public PromoType2Rule getRule() {
		return rule;
	}
	
	public void reset() {
		promoProduct = rule.getPromoProduct();
		promoUnit = rule.getPromoUnit();
		promoQuantity = rule.getPromoQuantity();
		freeProduct = rule.getFreeProduct();
		freeUnit = rule.getFreeUnit();
		freeQuantity = rule.getFreeQuantity();
	}

	public boolean isValid() {
		return promoProduct != null && !StringUtils.isEmpty(promoUnit) && promoQuantity != null
				&& freeProduct != null && !StringUtils.isEmpty(freeUnit) && freeQuantity != null;
	}

	public boolean isUpdating() {
		return rule.getId() != null;
	}

	public Product getPromoProduct() {
		return promoProduct;
	}

	public void setPromoProduct(Product promoProduct) {
		this.promoProduct = promoProduct;
	}

	public String getPromoUnit() {
		return promoUnit;
	}

	public void setPromoUnit(String promoUnit) {
		this.promoUnit = promoUnit;
	}

	public Integer getPromoQuantity() {
		return promoQuantity;
	}

	public void setPromoQuantity(Integer promoQuantity) {
		this.promoQuantity = promoQuantity;
	}

	public Product getFreeProduct() {
		return freeProduct;
	}

	public void setFreeProduct(Product freeProduct) {
		this.freeProduct = freeProduct;
	}

	public String getFreeUnit() {
		return freeUnit;
	}

	public void setFreeUnit(String freeUnit) {
		this.freeUnit = freeUnit;
	}

	public Integer getFreeQuantity() {
		return freeQuantity;
	}

	public void setFreeQuantity(Integer freeQuantity) {
		this.freeQuantity = freeQuantity;
	}

	public String getPromoProductCode() {
		return promoProduct != null ? promoProduct.getCode() : null;
	}

	public String getPromoProductDescription() {
		return promoProduct != null ? promoProduct.getDescription() : null;
	}

	public String getFreeProductCode() {
		return freeProduct != null ? freeProduct.getCode() : null;
	}
	
	public String getFreeProductDescription() {
		return freeProduct != null ? freeProduct.getDescription() : null;
	}

}