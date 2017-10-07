package com.pj.magic.model.search;

import com.pj.magic.model.Product;
import com.pj.magic.model.SalesInvoice;

public class NoMoreStockAdjustmentItemSearchCriteria {

	private Product product;
	private String unit;
	private SalesInvoice salesInvoice;

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

	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}

	public void setSalesInvoice(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}

}