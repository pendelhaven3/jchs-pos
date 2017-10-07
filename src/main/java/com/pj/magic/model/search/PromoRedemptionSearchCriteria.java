package com.pj.magic.model.search;

import java.util.Date;
import java.util.List;

import com.pj.magic.model.Customer;
import com.pj.magic.model.Promo;
import com.pj.magic.model.PromoType;
import com.pj.magic.model.SalesInvoice;

public class PromoRedemptionSearchCriteria {

	private SalesInvoice salesInvoice;
	private Boolean posted;
	private PromoType promoType;
	private Customer customer;
	private Date postDateFrom;
	private Date postDateTo;
	private Boolean cancelled;
	private List<PromoType> promoTypes;
	private Promo promo;

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

	public Boolean getPosted() {
		return posted;
	}

	public void setPosted(Boolean posted) {
		this.posted = posted;
	}

	public PromoType getPromoType() {
		return promoType;
	}

	public void setPromoType(PromoType promoType) {
		this.promoType = promoType;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public Date getPostDateFrom() {
		return postDateFrom;
	}

	public void setPostDateFrom(Date postDateFrom) {
		this.postDateFrom = postDateFrom;
	}

	public Date getPostDateTo() {
		return postDateTo;
	}

	public void setPostDateTo(Date postDateTo) {
		this.postDateTo = postDateTo;
	}

	public Boolean getCancelled() {
		return cancelled;
	}

	public void setCancelled(Boolean cancelled) {
		this.cancelled = cancelled;
	}

	public List<PromoType> getPromoTypes() {
		return promoTypes;
	}

	public void setPromoTypes(List<PromoType> promoTypes) {
		this.promoTypes = promoTypes;
	}

	public Promo getPromo() {
		return promo;
	}

	public void setPromo(Promo promo) {
		this.promo = promo;
	}

}