package com.pj.magic.model.search;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.PricingScheme;
import com.pj.magic.model.PromoType;

public class PromoSearchCriteria {

	private PromoType promoType;
	private Date promoDate;
	private Boolean active;
	private PricingScheme acceptedPricingScheme;
	private List<PromoType> promoTypes;
	private Date startDate;
	private Date endDate;
	
	public PromoType getPromoType() {
		return promoType;
	}

	public void setPromoType(PromoType promoType) {
		this.promoType = promoType;
	}

	public Date getPromoDate() {
		return promoDate;
	}

	public void setPromoDate(Date promoDate) {
		this.promoDate = promoDate;
	}

	public PricingScheme getAcceptedPricingScheme() {
		return acceptedPricingScheme;
	}

	/**
	 * Search criteria for promos that will accept the specified pricing scheme
	 */
	public void setAcceptedPricingScheme(PricingScheme pricingScheme) {
		this.acceptedPricingScheme = pricingScheme;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public List<PromoType> getPromoTypes() {
		return promoTypes;
	}

	public void setPromoTypes(List<PromoType> promoTypes) {
		this.promoTypes = promoTypes;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
}