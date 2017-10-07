package com.pj.magic.exception;

import com.pj.magic.model.SalesReturnItem;

public class SalesReturnItemQuantityExceededException extends RuntimeException {

	private SalesReturnItem item;
	
	public SalesReturnItemQuantityExceededException(SalesReturnItem item) {
		this.item = item;
	}
	
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Total quantity from all Sales Returns has exceeded Sales Invoice quantity:\n")
			.append(item.getQuantity()).append(" ")
			.append(item.getSalesInvoiceItem().getUnit()).append(" ")
			.append(item.getSalesInvoiceItem().getProduct().getDescription());
		return sb.toString();
	}
	
}