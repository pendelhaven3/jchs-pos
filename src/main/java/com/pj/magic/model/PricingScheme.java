package com.pj.magic.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public class PricingScheme implements Serializable {

    private static final long serialVersionUID = 8768555128430112277L;
    
    private Long id;
	private String name;
	private List<Product> products = new ArrayList<>();

	public PricingScheme() {
		// default constructor
	}
	
	public PricingScheme(long id) {
		this.id = id;
	}
	
	public PricingScheme(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(id)
			.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof PricingScheme)) {
            return false;
        }
        PricingScheme other = (PricingScheme)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}

	@Override
	public String toString() {
		return name;
	}
	
}
