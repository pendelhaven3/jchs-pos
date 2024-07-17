package com.pj.magic.exception;

public class PostReceivingReceiptException extends RuntimeException {
	
	private String productCode;
	
	public PostReceivingReceiptException(String productCode, Throwable cause) {
		super(cause.getMessage(), cause);
		this.productCode = productCode;
	}

	public String getProductCode() {
		return productCode;
	}
	
}
