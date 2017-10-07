package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PromoType3RulePromoProduct {

	private Long id;
	private PromoType3Rule parent;
	private Product product;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PromoType3Rule getParent() {
		return parent;
	}

	public void setParent(PromoType3Rule parent) {
		this.parent = parent;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public boolean isNew() {
		return id == null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof PromoType3RulePromoProduct)) {
            return false;
        }
        PromoType3RulePromoProduct other = (PromoType3RulePromoProduct)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.toHashCode();
	}
	
}