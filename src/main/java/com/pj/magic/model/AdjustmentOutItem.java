package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class AdjustmentOutItem implements Comparable<AdjustmentOutItem> {

	private Long id;
	private AdjustmentOut parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal unitPrice;

	public BigDecimal getUnitPrice() {
		return unitPrice;
	}
	
	public BigDecimal getAmount() {
		if (product == null || quantity == null) {
			return null;
		}
		
		return getEffectiveUnitPrice().multiply(new BigDecimal(quantity.intValue()));
	}
	
	public boolean isQuantityValid() {
		return product.hasAvailableUnitQuantity(unit, quantity);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
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
			.append(unit)
			.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
        if (!(obj instanceof AdjustmentOutItem)) {
            return false;
        }
        AdjustmentOutItem other = (AdjustmentOutItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public AdjustmentOut getParent() {
		return parent;
	}

	public void setParent(AdjustmentOut parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(AdjustmentOutItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public BigDecimal getEffectiveUnitPrice() {
		BigDecimal unitPrice = this.unitPrice;
		if (unitPrice == null) {
			unitPrice = product.getUnitPrice(unit);
		}
		return unitPrice;
	}
	
}
