package com.pj.magic.model;

import java.math.BigDecimal;


public class NoMoreStockAdjustmentItem {

	private Long id;
	private NoMoreStockAdjustment parent;
	private SalesInvoiceItem salesInvoiceItem;
	private Integer quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public NoMoreStockAdjustment getParent() {
		return parent;
	}

	public void setParent(NoMoreStockAdjustment parent) {
		this.parent = parent;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public SalesInvoiceItem getSalesInvoiceItem() {
		return salesInvoiceItem;
	}

	public void setSalesInvoiceItem(SalesInvoiceItem salesInvoiceItem) {
		this.salesInvoiceItem = salesInvoiceItem;
	}

	public BigDecimal getAmount() {
		if (salesInvoiceItem == null || quantity == null) {
			return null;
		} else {
			return getUnitPrice().multiply(new BigDecimal(quantity));
		}
	}

	public BigDecimal getUnitPrice() {
		if (salesInvoiceItem == null) {
			return null;
		} else {
			return salesInvoiceItem.getDiscountedUnitPrice();
		}
	}
	
	public BigDecimal getCost() {
		if (salesInvoiceItem == null) {
			return null;
		} else {
			return salesInvoiceItem.getCost();
		}
	}
	
	public BigDecimal getTotalCost() {
		return getCost().multiply(new BigDecimal(quantity));
	}
	
}