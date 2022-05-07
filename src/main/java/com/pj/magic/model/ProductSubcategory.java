package com.pj.magic.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ProductSubcategory implements Serializable {

    private static final long serialVersionUID = 1609612926058033707L;
    
	private Long id;
	private String name;
	
	private ProductCategory parent;
	
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
        if (!(obj instanceof ProductSubcategory)) {
            return false;
        }
        ProductSubcategory other = (ProductSubcategory)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return name;
	}

	public ProductCategory getParent() {
		return parent;
	}

	public void setParent(ProductCategory parent) {
		this.parent = parent;
	}
	
}