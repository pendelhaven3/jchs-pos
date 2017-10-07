package com.pj.magic.util.report;

import java.math.BigDecimal;

import com.pj.magic.Constants;

public class PostedSalesAndProfitReportReportUtil extends ReportUtil {

	public static String discount(BigDecimal discount, int length) {
		if (discount != null && !discount.equals(Constants.ZERO)) {
			return field(discount, length, ALIGN_RIGHT);
		} else {
			return field((String)null, length);
		}
	}
	
}
