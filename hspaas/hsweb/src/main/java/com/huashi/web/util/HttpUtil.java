package com.huashi.web.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

/**
 * HTTP相关的工具类
 *
 * @author Shen, Shibang
 */
public final class HttpUtil {

	private HttpUtil() {
	}

	/**
	 * 
	 * 通过USER-AGENT编码文件名
	 * 
	 * @param request
	 * @return String
	 * @throws UnsupportedEncodingException
	 * @since p2p_cloud_v1.0
	 */
	public static String getEncodedFileName(HttpServletRequest request,
			String fileName) throws UnsupportedEncodingException {
		String userAgent = request.getHeader("USER-AGENT");
		String finalFileName = null;
		if (StringUtils.contains(userAgent, "MSIE")) { // IE浏览器
			finalFileName = URLEncoder.encode(fileName, "UTF-8");
		} else if (StringUtils.contains(userAgent, "Mozilla")) { // google,火狐浏览器
			finalFileName = new String(fileName.getBytes(), "ISO8859-1");
		} else {
			finalFileName = URLEncoder.encode(fileName, "UTF-8");// 其他浏览器
		}
		return finalFileName;
	}

	/**
	 * 
	   * TODO 判断请求是否为AJAX请求
	   * 
	   * @param request
	   * @return
	 */
	public static boolean isAjaxRequest(HttpServletRequest request) {
		String requestedWith = request.getHeader("X-Requested-With");
		// ajax请求
		return requestedWith != null && "XMLHttpRequest".equalsIgnoreCase(requestedWith);
	}
}
