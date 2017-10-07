package com.pj.magic.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Entity
@Table(name="PRODUCT_CATEGORY")
public class ProductCategory implements Serializable {

    private static final long serialVersionUID = -7478611206960298080L;
    
    @Id
	@GeneratedValue
	private Long id;
	private String name;
	
	@OneToMany(mappedBy="parent", fetch=FetchType.EAGER)
	@OrderBy("NAME asc")
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
