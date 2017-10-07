package com.pj.magic.exception;

import com.pj.magic.model.SalesRequisitionItem;

public class SalesRequisitionItemPostException extends RuntimeException {

	private SalesRequisitionItem item;
	private String errorMessage;

	public SalesRequisitionItemPostException(SalesRequisitionItem item, String errorMessage) {
		this.item = item;
		this.errorMessage = errorMessage;
	}
	
	public SalesRequisitionItem getItem() {
		return item;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
}