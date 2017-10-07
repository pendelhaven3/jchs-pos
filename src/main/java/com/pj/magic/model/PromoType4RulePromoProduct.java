package com.pj.magic.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PromoType4RulePromoProduct {

	private Long id;
	private PromoType4Rule parent;
	private Product product;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PromoType4Rule getParent() {
		return parent;
	}

	public void setParent(PromoType4Rule parent) {
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
        if (!(obj instanceof PromoType4RulePromoProduct)) {
            return false;
        }
        PromoType4RulePromoProduct other = (PromoType4RulePromoProduct)obj;		
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