package com.pj.magic.exception;

import java.util.ArrayList;
import java.util.List;

public class SalesRequisitionPostException extends RuntimeException {

	private List<SalesRequisitionItemPostException> exceptions = new ArrayList<>();
	
	public void add(SalesRequisitionItemPostException e) {
		exceptions.add(e);
	}

	public boolean isEmpty() {
		return exceptions.isEmpty();
	}
	
	public List<SalesRequisitionItemPostException> getExceptions() {
		return exceptions;
	}

}