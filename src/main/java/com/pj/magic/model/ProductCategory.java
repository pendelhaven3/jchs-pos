package com.pj.magic.model;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ProductCategory implements Serializable {

    private static final long serialVersionUID = -7478611206960298080L;
    
	private Long id;
	private String name;
	
	private List<ProductSubcategory> subcategories;
	
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
        if (!(obj instanceof ProductCategory)) {
            return false;
        }
        ProductCategory other = (ProductCategory)obj;		
		return new EqualsBuilder()
			.append(id, other.getId())
			.isEquals();
	}
	
	@Override
	public String toString() {
		return name;
	}

	public List<ProductSubcategory> getSubcategories() {
		return subcategories;
	}

	public void setSubcategories(List<ProductSubcategory> subcategories) {
		this.subcategories = subcategories;
	}
	
}
