package com.pj.magic.exception;

import com.pj.magic.model.NoMoreStockAdjustmentItem;

public class NoMoreStockAdjustmentItemQuantityExceededException extends RuntimeException {

	private NoMoreStockAdjustmentItem item;
	
	public NoMoreStockAdjustmentItemQuantityExceededException(NoMoreStockAdjustmentItem item) {
		this.item = item;
	}
	
	public String getMessage() {
		StringBuilder sb = new StringBuilder("Total quantity from all NMS adjustments has exceeded Sales Invoice quantity:\n")
			.append(item.getQuantity()).append(" ")
			.append(item.getSalesInvoiceItem().getUnit()).append(" ")
			.append(item.getSalesInvoiceItem().getProduct().getDescription());
		return sb.toString();
	}
	
}