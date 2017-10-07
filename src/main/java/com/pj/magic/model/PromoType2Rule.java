package com.pj.magic.model;

public class PromoType2Rule {

	private Long id;
	private Promo parent;
	private Product promoProduct;
	private String promoUnit;
	private Integer promoQuantity;
	private Product freeProduct;
	private String freeUnit;
	private Integer freeQuantity;

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

	public PromoRedemptionReward evaluate(SalesRequisitionItem item) {
		if (item.getProduct().getId().equals(promoProduct.getId())
				&& item.getUnit().equals(promoUnit)
				&& item.getQuantity().intValue() >= promoQuantity.intValue()) {
			PromoRedemptionReward reward = new PromoRedemptionReward();
			reward.setProduct(freeProduct);
			reward.setUnit(freeUnit);
			reward.setQuantity(item.getQuantity().intValue() / promoQuantity.intValue() 
					* freeQuantity.intValue());
			reward.setParent(new PromoRedemption());
			reward.getParent().setPromo(parent);
			return reward;
		} else {
			return null;
		}
	}

	public PromoRedemptionReward evaluate(SalesInvoiceItem item) {
		if (item.getProduct().getId().equals(promoProduct.getId())
				&& item.getUnit().equals(promoUnit)
				&& item.getQuantity().intValue() >= promoQuantity.intValue()) {
			PromoRedemptionReward reward = new PromoRedemptionReward();
			reward.setProduct(freeProduct);
			reward.setUnit(freeUnit);
			reward.setQuantity(item.getQuantity().intValue() / promoQuantity.intValue() 
					* freeQuantity.intValue());
			reward.setParent(new PromoRedemption());
			reward.getParent().setPromo(parent);
			return reward;
		} else {
			return null;
		}
	}
	
}