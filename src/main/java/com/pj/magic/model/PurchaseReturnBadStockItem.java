package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PurchaseReturnBadStockItem {

	private Long id;
	private PurchaseReturnBadStock parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal unitCost;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public PurchaseReturnBadStock getParent() {
		return parent;
	}

	public void setParent(PurchaseReturnBadStock parent) {
		this.parent = parent;
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
        if (!(obj instanceof PurchaseReturnBadStockItem)) {
            return false;
        }
        PurchaseReturnBadStockItem other = (PurchaseReturnBadStockItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public BigDecimal getAmount() {
		if (quantity != null && unitCost != null) {
			return unitCost.multiply(new BigDecimal(quantity));
		}
		return null;
	}

	public BigDecimal getUnitCost() {
		return unitCost;
	}

	public void setUnitCost(BigDecimal unitCost) {
		this.unitCost = unitCost;
	}

}