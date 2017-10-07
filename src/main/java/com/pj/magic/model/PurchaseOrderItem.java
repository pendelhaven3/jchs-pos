package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class PurchaseOrderItem implements Comparable<PurchaseOrderItem> {

	private Long id;
	private PurchaseOrder parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal cost;
	private BigDecimal vatInclusiveCost;
	private Integer actualQuantity;
	private boolean ordered;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PurchaseOrder getParent() {
		return parent;
	}

	public void setParent(PurchaseOrder parent) {
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

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public Integer getActualQuantity() {
		return actualQuantity;
	}

	public void setActualQuantity(Integer actualQuantity) {
		this.actualQuantity = actualQuantity;
	}

	public BigDecimal getAmount() {
		if (actualQuantity != null) {
			return cost.multiply(new BigDecimal(actualQuantity.intValue()));
		} else {
			return cost.multiply(new BigDecimal(quantity.intValue()));
		}
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
        if (!(obj instanceof PurchaseOrderItem)) {
            return false;
        }
        PurchaseOrderItem other = (PurchaseOrderItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public boolean isOrdered() {
		return ordered;
	}

	public void setOrdered(boolean ordered) {
		this.ordered = ordered;
	}

	@Override
	public int compareTo(PurchaseOrderItem o) {
		int result = product.compareTo(o.getProduct());
		if (result == 0) {
			return Unit.compare(unit, o.getUnit());
		} else {
			return result;
		}
	}

	public BigDecimal getVatInclusiveCost() {
		return vatInclusiveCost;
	}

	public void setVatInclusiveCost(BigDecimal vatInclusiveCost) {
		this.vatInclusiveCost = vatInclusiveCost;
	}
	
}
