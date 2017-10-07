package com.pj.magic.model;

import java.util.Arrays;
import java.util.List;

public enum PromoType {

	PROMO_TYPE_1(1L, "Buy X amount of Manufacturer products, get Y product free"),
	PROMO_TYPE_2(2L, "Buy X quantity of product, get Y product free"),
	PROMO_TYPE_3(3L, "Buy X amount of selected products, get Y product free"),
	PROMO_TYPE_4(4L, "Earn points by buying selected products"),
	PROMO_TYPE_5(5L, "Buy X amount of selected products, get Y rebate");
	
	private Long id;
	private String description;
	
	private PromoType(Long id, String description) {
		this.id = id;
		this.description = description;
	}
	
	public Long getId() {
		return id;
	}
	
	public static List<PromoType> getPromoTypes() {
		return Arrays.asList(PromoType.values());
	}
	
	public static PromoType getPromoType(long id) {
		for (PromoType promoType : PromoType.values()) {
			if (promoType.getId().equals(id)) {
				return promoType;
			}
		}
		return null;
	}
	
	public boolean isType1() {
		return this == PROMO_TYPE_1;
	}
	
	public boolean isType2() {
		return this == PROMO_TYPE_2;
	}
	
	public boolean isType3() {
		return this == PROMO_TYPE_3;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
	public String getDescription() {
		return description;
	}

	public boolean isType4() {
		return this == PROMO_TYPE_4;
	}

	public boolean isType5() {
		return this == PROMO_TYPE_5;
	}
	
}