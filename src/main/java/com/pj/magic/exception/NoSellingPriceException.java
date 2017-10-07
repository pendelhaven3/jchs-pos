package com.pj.magic.exception;

import com.pj.magic.model.SalesRequisitionItem;

public class NoSellingPriceException extends RuntimeException {

	private SalesRequisitionItem item;

	public NoSellingPriceException(SalesRequisitionItem item) {
		this.item = item;
	}

	public SalesRequisitionItem getItem() {
		return item;
	}
	
}
