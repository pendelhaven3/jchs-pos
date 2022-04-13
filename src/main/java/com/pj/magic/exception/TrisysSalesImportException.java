package com.pj.magic.exception;

public class TrisysSalesImportException extends RuntimeException {

	private final String line;
	private Throwable exception;
	
	public TrisysSalesImportException(String line, Throwable exception) {
		this.line = line;
		this.exception = exception;
	}
	
	public String getLine() {
		return line;
	}
	
	@Override
	public String getMessage() {
		return exception.getMessage();
	}
	
}
