package com.pj.magic.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.pj.magic.Constants;

public class FormatterUtil {

	private static final String DATE_IN_FILENAME_FORMAT = "MMM-dd-yyyy";
	private static final String DATE_IN_CHEQUE_FORMAT = "MM-dd-yyyy";
	
	public static final String formatDate(Date date) {
		return new SimpleDateFormat(Constants.DATE_FORMAT).format(date);
	}
	
	public static final String formatDateTime(Date date) {
		return new SimpleDateFormat(Constants.DATETIME_FORMAT).format(date);
	}
	
	public static final String formatTime(Date date) {
		return new SimpleDateFormat(Constants.TIME_FORMAT).format(date);
	}
	
	public static final String formatAmount(BigDecimal amount) {
		return new DecimalFormat(Constants.AMOUNT_FORMAT).format(amount);
	}
	
	public static final String formatInteger(int number) {
		return new DecimalFormat(Constants.INTEGER_FORMAT).format(number);
	}
	
	public static final String formatDateInFilename(Date date) {
		return new SimpleDateFormat(DATE_IN_FILENAME_FORMAT).format(date);
	}

	public static final String formatChequeDate(Date date) {
		return new SimpleDateFormat(DATE_IN_CHEQUE_FORMAT).format(date).toUpperCase();
	}

}