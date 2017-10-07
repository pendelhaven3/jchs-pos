package com.pj.magic.util;

import org.apache.commons.lang.StringUtils;

public class HtmlUtil {

	public static final String html(String text) {
		if (StringUtils.isEmpty(text)) {
			return StringUtils.EMPTY;
		}
		String body = text.replaceAll("\n", "<br>");
		return new StringBuilder().append("<html>").append(body).append("</html>").toString();
	}
	
	public static final String blueUnderline(String text) {
		StringBuilder sb = new StringBuilder();
		sb.append("<html><u><font color=\"blue\">").append(text).append("</font></u><html>");
		return sb.toString();
	}
	
}