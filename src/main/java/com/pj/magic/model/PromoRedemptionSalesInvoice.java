package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;



public class PromoRedemptionSalesInvoice {

	private Long id;
	private PromoRedemption parent;
	private SalesInvoice salesInvoice;

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

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof PromoRedemptionSalesInvoice)) {
            return false;
        }
        PromoRedemptionSalesInvoice other = (PromoRedemptionSalesInvoice)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
}