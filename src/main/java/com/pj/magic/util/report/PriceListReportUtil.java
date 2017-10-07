package com.pj.magic.util.report;

import java.math.BigDecimal;

import com.pj.magic.Constants;

public class PriceListReportUtil extends ReportUtil {

	public static String price(BigDecimal price, int length) {
		if (Constants.ZERO.equals(price)) {
			return field("-", length, ALIGN_RIGHT);
		} else {
			return field(price, length, ALIGN_RIGHT);
		}
	}
	
}
