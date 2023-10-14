package com.pj.magic.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnpostedReceivingReceiptException extends RuntimeException {

	private long receivingReceiptNumber;

	public UnpostedReceivingReceiptException(long receivingReceiptNumber) {
		this.receivingReceiptNumber = receivingReceiptNumber;
	}
	
}
