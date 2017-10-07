package com.pj.magic.exception;

public class AlreadyCancelledException extends RuntimeException {

	public AlreadyCancelledException() {
	}

	public AlreadyCancelledException(String message) {
		super(message);
	}

}