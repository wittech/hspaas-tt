package com.huashi.web.filter;

/**
 * 防止xss攻击
 *
 */
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.web.util.HtmlUtils;

public class XssHttpServletRequestWraper extends HttpServletRequestWrapper {

	public XssHttpServletRequestWraper(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		return clearXss(super.getParameter(name));
	}

	@Override
	public String getHeader(String name) {
		return clearXss(super.getHeader(name));
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		if (values != null) {
			String[] newValues = new String[values.length];

			for (int i = 0; i < values.length; i++) {
				newValues[i] = clearXss(values[i]);
			}
			return newValues;
		}
		return super.getParameterValues(name);
	}

	/**
	 * 处理字符转义
	 * 
	 * @param value
	 * @return
	 */
	private String clearXss(String value) {
        if (value == null) {
            return null;
        }
        return HtmlUtils.htmlEscape(value);
    }

}