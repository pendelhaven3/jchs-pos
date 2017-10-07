package com.pj.magic.util;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.StringUtils;

import com.pj.magic.Constants;

public class DateUtil {

	public static boolean isDateString(String value) {
		if (StringUtils.isEmpty(value)) {
			return false;
		}
		
		ParsePosition pos = new ParsePosition(0);
		new SimpleDateFormat(Constants.DATE_FORMAT).parse(value, pos);
		return pos.getIndex() == value.length();
	}

	public static Date toDate(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return new SimpleDateFormat(Constants.DATE_FORMAT).parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
	
	public static Date toDateFromUrlValue(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		try {
			return new SimpleDateFormat(Constants.DATE_FORMAT_FOR_URL_VALUE).parse(value);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static boolean isMonday(Date date) {
		return DateUtils.toCalendar(date).get(Calendar.DAY_OF_WEEK) == Calendar.MONDAY;
	}
	
}