package com.pj.magic.exception;

import com.pj.magic.model.SalesInvoice;

public class SalesInvoiceIneligibleForPromoRedemptionException extends RuntimeException {

	private SalesInvoice salesInvoice;
	
	public SalesInvoiceIneligibleForPromoRedemptionException(SalesInvoice salesInvoice) {
		this.salesInvoice = salesInvoice;
	}
	
	public SalesInvoice getSalesInvoice() {
		return salesInvoice;
	}
	
}