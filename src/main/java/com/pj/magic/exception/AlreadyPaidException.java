package com.pj.magic.exception;

public class AlreadyPaidException extends RuntimeException {

	public AlreadyPaidException(String message) {
		super(message);
	}

}