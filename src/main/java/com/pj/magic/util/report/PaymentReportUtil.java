package com.pj.magic.util.report;

import java.math.BigDecimal;

import com.pj.magic.Constants;
import com.pj.magic.model.PaymentPaymentAdjustment;

public class PaymentReportUtil extends ReportUtil {

	public static String adjustmentAmount(BigDecimal adjustmentAmount, int length) {
		if (adjustmentAmount != null && !adjustmentAmount.equals(Constants.ZERO)) {
			return field(adjustmentAmount, length, ALIGN_RIGHT);
		} else {
			return field((String)null, length);
		}
	}
	
	public static String referenceNumber(PaymentPaymentAdjustment adjustment, int length, String align) {
		String referenceNumber = adjustment.getReferenceNumber();
		if (referenceNumber == null) {
			referenceNumber = "";
		}
		return field(referenceNumber, length, align);
	}
	
}
