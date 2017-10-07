package com.pj.magic.exception;

import com.pj.magic.model.SalesRequisitionItem;

public class SalesRequisitionItemNotEnoughStocksException extends SalesRequisitionItemPostException {

	private static final String ERROR_MESSAGE = "Not enough stocks available";
	
	public SalesRequisitionItemNotEnoughStocksException(SalesRequisitionItem item) {
		super(item, ERROR_MESSAGE);
	}

}
