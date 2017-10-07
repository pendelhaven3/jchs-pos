package com.pj.magic.model.search;

import java.util.Date;

import com.pj.magic.model.PricingScheme;

public class ProductPriceHistorySearchCriteria {

	private Date fromDate;
	private Date toDate;
	private PricingScheme pricingScheme;

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public PricingScheme getPricingScheme() {
		return pricingScheme;
	}

	public void setPricingScheme(PricingScheme pricingScheme) {
		this.pricingScheme = pricingScheme;
	}
	
}