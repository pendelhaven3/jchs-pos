package com.pj.magic.exception;

public class AlreadyPostedException extends RuntimeException {

	public AlreadyPostedException() {
		// default constructor
	}
	
	public AlreadyPostedException(String message) {
		super(message);
	}

}