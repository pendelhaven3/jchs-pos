package com.pj.magic.model.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Percentage {

	private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");
	
	private BigDecimal number;

	public Percentage(BigDecimal number) {
		this.number = number;
	}
		
	public Percentage(String number) {
		this.number = new BigDecimal(number);
	}
	
	public BigDecimal toDecimal() {
		return number.divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP);
	}
	
}
