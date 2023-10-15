package com.pj.magic.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InvalidProductCodeException extends RuntimeException {

	private String code;
	
}
