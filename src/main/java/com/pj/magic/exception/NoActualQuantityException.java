package com.pj.magic.exception;

import com.pj.magic.model.PurchaseOrderItem;

public class NoActualQuantityException extends RuntimeException {

	private PurchaseOrderItem item;

	public NoActualQuantityException(PurchaseOrderItem item) {
		this.item = item;
	}
	
	public PurchaseOrderItem getItem() {
		return item;
	}
	
}
