package com.pj.magic.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PromoType4Rule {

	private Long id;
	private Promo parent;
	private BigDecimal targetAmount;
	private List<PromoType4RulePromoProduct> promoProducts = new ArrayList<>();

	public PromoType4Rule() {
		// default constructor
	}
	
	public PromoType4Rule(long id) {
		this.id = id;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Promo getParent() {
		return parent;
	}

	public void setParent(Promo parent) {
		this.parent = parent;
	}

	public BigDecimal getTargetAmount() {
		return targetAmount;
	}

	public void setTargetAmount(BigDecimal targetAmount) {
		this.targetAmount = targetAmount;
	}

	public List<PromoType4RulePromoProduct> getPromoProducts() {
		return promoProducts;
	}

	public void setPromoProducts(List<PromoType4RulePromoProduct> promoProducts) {
		this.promoProducts = promoProducts;
	}

	public boolean isNew() {
		return id == null;
	}
	
	public boolean hasPromoProduct(Product product) {
		for (PromoType4RulePromoProduct promoProduct : promoProducts) {
			if (promoProduct.getProduct().equals(product)) {
				return true;
			}
		}
		return false;
	}
	
}