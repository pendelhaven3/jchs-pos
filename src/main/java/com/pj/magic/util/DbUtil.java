package com.pj.magic.util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class DbUtil {

	public static String toMySqlDateString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd").format(date);
	}

	public static String toMySqlDateTimeString(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
	}

	public static String escape(String value) {
		return new StringBuilder("'").append(value).append("'").toString();
	}

	public static String toSqlInValues(List<String> values) {
		Collection<String> escapedValues = Collections2.transform(values, new Function<String, String>() {

			@Override
			public String apply(String input) {
				return escape(input);
			}
		});
		return StringUtils.join(escapedValues, ",");
	}
	
	public static java.sql.Date toSqlDate(Date date) {
		return new java.sql.Date(date.getTime());
	}
	
}