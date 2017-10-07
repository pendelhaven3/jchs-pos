package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class AdjustmentInItem implements Comparable<AdjustmentInItem> {

	private Long id;
	private AdjustmentIn parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;

	public BigDecimal getCost() {
		return cost;
	}
	
	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}
	
	public BigDecimal getEffectiveCost() {
		BigDecimal cost = this.cost;
		if (cost == null) {
			cost = product.getFinalCost(unit);
		}
		return cost;
	}
	
	public BigDecimal getAmount() {
		if (product == null || quantity == null) {
			return null;
		}
		return getEffectiveCost().multiply(new BigDecimal(quantity.intValue()));
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
        if (!(obj instanceof AdjustmentInItem)) {
            return false;
        }
        AdjustmentInItem other = (AdjustmentInItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public AdjustmentIn getParent() {
		return parent;
	}

	public void setParent(AdjustmentIn parent) {
		this.parent = parent;
	}

	@Override
	public int compareTo(AdjustmentInItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}
	
}