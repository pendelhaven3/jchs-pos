package com.pj.magic.model;

import java.math.BigDecimal;


public class SalesReturnItem {

	private Long id;
	private SalesReturn parent;
	private SalesInvoiceItem salesInvoiceItem;
	private Integer quantity;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public SalesReturn getParent() {
		return parent;
	}

	public void setParent(SalesReturn parent) {
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
			return salesInvoiceItem.getDiscountedUnitPrice().multiply(new BigDecimal(quantity));
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
	
	public BigDecimal getNetCost() {
		return getCost().multiply(new BigDecimal(quantity));
	}
	
}