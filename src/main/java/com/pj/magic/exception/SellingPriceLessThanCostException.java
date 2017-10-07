package com.pj.magic.exception;

import com.pj.magic.model.SalesInvoiceItem;

public class SellingPriceLessThanCostException extends RuntimeException {

	private SalesInvoiceItem item;

	public SellingPriceLessThanCostException(SalesInvoiceItem item) {
		this.item = item;
	}
	
	public SalesInvoiceItem getItem() {
		return item;
	}
	
}
