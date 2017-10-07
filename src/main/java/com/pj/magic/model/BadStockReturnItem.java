package com.pj.magic.model;

import java.math.BigDecimal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class BadStockReturnItem {

	private Long id;
	private BadStockReturn parent;
	private Product product;
	private String unit;
	private Integer quantity;
	private BigDecimal unitPrice;
	private BigDecimal cost;
	private Long salesInvoiceNumber;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public BadStockReturn getParent() {
		return parent;
	}

	public void setParent(BadStockReturn parent) {
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
        if (!(obj instanceof BadStockReturnItem)) {
            return false;
        }
        BadStockReturnItem other = (BadStockReturnItem)obj;		
		return new EqualsBuilder()
			.append(product, other.getProduct())
			.append(unit, other.getUnit())
			.isEquals();
	}

	public void setUnitPrice(BigDecimal unitPrice) {
		this.unitPrice = unitPrice;
	}
	
	public BigDecimal getUnitPrice() {
		return unitPrice;
	}

	public BigDecimal getAmount() {
		if (quantity != null && unitPrice != null) {
			return unitPrice.multiply(new BigDecimal(quantity));
		}
		return null;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getTotalLoss() {
		return getAmount().subtract(getTotalCost());
	}
	
	public BigDecimal getTotalCost() {
		return cost.multiply(new BigDecimal(quantity));
	}

	public Long getSalesInvoiceNumber() {
		return salesInvoiceNumber;
	}

	public void setSalesInvoiceNumber(Long salesInvoiceNumber) {
		this.salesInvoiceNumber = salesInvoiceNumber;
	}
	
}