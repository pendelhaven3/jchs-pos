package com.pj.magic.exception;

public class NoItemException extends RuntimeException {

	public NoItemException() {
		super("No items to post");
	}
	
}
