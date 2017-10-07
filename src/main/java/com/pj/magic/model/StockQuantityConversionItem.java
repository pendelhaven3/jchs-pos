package com.pj.magic.model;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class StockQuantityConversionItem implements Comparable<StockQuantityConversionItem>, Serializable {

    private static final long serialVersionUID = -995920799377050401L;
    
    private Long id;
	private StockQuantityConversion parent;
	private Product product;
	private String fromUnit;
	private String toUnit;
	private Integer quantity;
	private Integer convertedQuantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public StockQuantityConversion getParent() {
		return parent;
	}

	public void setParent(StockQuantityConversion parent) {
		this.parent = parent;
	}

	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(product)
			.append(fromUnit)
			.append(toUnit)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof StockQuantityConversionItem)) {
            return false;
        }
        StockQuantityConversionItem other = (StockQuantityConversionItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(fromUnit, other.getFromUnit())
			.append(toUnit, other.getToUnit())
			.isEquals();
	}
	
	public String getFromUnit() {
		return fromUnit;
	}

	public void setFromUnit(String fromUnit) {
		this.fromUnit = fromUnit;
	}

	public String getToUnit() {
		return toUnit;
	}

	public void setToUnit(String toUnit) {
		this.toUnit = toUnit;
	}

	public int getConvertedQuantity() {
		if (convertedQuantity != null) {
			return convertedQuantity;
		} else {
			return product.getUnitConversion(fromUnit) / product.getUnitConversion(toUnit) * quantity;
		}
	}

	@Override
	public int compareTo(StockQuantityConversionItem o) {
		return product.compareTo(o.getProduct());
	}
	
	public void calculateConvertedQuantity() {
		convertedQuantity = product.getUnitConversion(fromUnit) / product.getUnitConversion(toUnit) * quantity;
	}
	
	public void setConvertedQuantity(Integer convertedQuantity) {
		this.convertedQuantity = convertedQuantity;
	}
	
}