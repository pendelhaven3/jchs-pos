package com.pj.magic.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;

import org.springframework.util.StringUtils;

import com.pj.magic.Constants;

public class NumberUtil {

	public static boolean isAmount(String value) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		
		ParsePosition pos = new ParsePosition(0);
		new DecimalFormat(Constants.AMOUNT_FORMAT).parse(value, pos);
		return pos.getIndex() == value.length();
	}
	
	public static BigDecimal toBigDecimal(String value) {
		try {
			return new BigDecimal(new DecimalFormat(Constants.AMOUNT_FORMAT).parse(value).doubleValue())
				.setScale(2, RoundingMode.HALF_UP);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BigDecimal roundUpToNearestFiveCents(BigDecimal amount) {
		BigDecimal remainder = amount.divideAndRemainder(Constants.FIVE_CENTS)[1];
		if (remainder.equals(BigDecimal.ZERO.setScale(2))) {
			return amount;
		} else {
			return amount.add(Constants.FIVE_CENTS.subtract(amount.divideAndRemainder(Constants.FIVE_CENTS)[1]));
		}
	}
	
}
