package com.pj.magic.util.report;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.pj.magic.util.FormatterUtil;

public class ReportUtil {

	protected static final String ALIGN_LEFT = "left";
	protected static final String ALIGN_RIGHT = "right";
	private static final int CHARACTERS_PER_LINE = 73;
	
	public static String field(String text) {
		return StringUtils.defaultString(text);
	}
	
	public static String field(final String text, int length) {
		return field(text, length, ALIGN_LEFT);
	}
	
	public static String field(final String text, int length, String align) {
		String value = text;
		
		if (value == null) {
			value = "";
		}
		
		if (value.length() < length) {
			if (ALIGN_LEFT.equals(align)) {
				value = StringUtils.rightPad(value, length);
			} else if (ALIGN_RIGHT.equals(align)) {
				value = StringUtils.leftPad(value, length);
			}
		} else if (value.length() > length) {
			value = value.substring(0, length);
		}
		return value;
	}
	
	public static String field(long number, int length) {
		return field(String.valueOf(number), length, ALIGN_LEFT);
	}
	
	public static String field(long number, int length, String align) {
		return field(String.valueOf(number), length, align);
	}
	
	public static String field(int number, int length) {
		return field(number, length, ALIGN_LEFT);
	}
	
	public static String field(int number, int length, String align) {
		return field(String.valueOf(number), length, align);
	}

	public static String field(BigDecimal number, int length, String align) {
		return field(FormatterUtil.formatAmount(number), length, align);
	}
	
	public static String multiline(final String text, int lineLength, int lineNumber) {
		String line = "";
		if (!StringUtils.isEmpty(text)) {
			int lastPosition = 0;
			for (int i = 0; i < lineNumber; i++) {
				if (lastPosition == text.length()) {
					line = "";
				} else if (text.length() - lastPosition > lineLength) {
					line = text.substring(lastPosition, lastPosition + lineLength);
					int lastSpacePosition = line.lastIndexOf(' ');
					line = line.substring(0, lastSpacePosition);
					lastPosition += lastSpacePosition + 1;
				} else {
					line = text.substring(lastPosition);
					lastPosition = text.length();
				}
			}
		}
		return StringUtils.rightPad(line, lineLength);
	}
	
	public static String center(String text) {
		return center(text, CHARACTERS_PER_LINE);
	}
	
	public static String center(String text, int charactersPerLine) {
		return StringUtils.repeat(" ", (charactersPerLine - text.length()) / 2) + text;
	}
	
	public static String field(Date date) {
		return FormatterUtil.formatDate(date);
	}
	
	public static String time(Date date) {
		return StringUtils.leftPad(FormatterUtil.formatTime(date), 8);
	}

	public static String dateTime(Date date) {
		return StringUtils.rightPad(FormatterUtil.formatDateTime(date), 19);
	}

}