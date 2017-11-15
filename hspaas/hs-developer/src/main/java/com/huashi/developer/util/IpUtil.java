package com.huashi.developer.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtil {

	protected static Logger logger = LoggerFactory.getLogger(IpUtil.class);

	/**
	 * 
	 * TODO 获取客户端请求IP
	 * 
	 * @return
	 */
	public static String getClientIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (logger.isDebugEnabled()) {
			logger.debug("x-forwarded-for = {}", ip);
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
			if (logger.isDebugEnabled()) {
				logger.debug("Proxy-Client-IP = {}", ip);
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
			if (logger.isDebugEnabled()) {
				logger.debug("WL-Proxy-Client-IP = {}", ip);
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("X-Real-IP");
			if (logger.isDebugEnabled()) {
				logger.debug("X-Real-IP = {}", ip);
			}
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
			if (logger.isDebugEnabled()) {
				logger.debug("RemoteAddr-IP = {}", ip);
			}
		}
		if (StringUtils.isNotEmpty(ip)) {
			ip = ip.split(",")[0];
		}
		return ip;
	}
}
