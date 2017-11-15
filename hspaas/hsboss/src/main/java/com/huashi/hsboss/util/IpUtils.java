package com.huashi.hsboss.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpUtils{
	
	protected final static Logger LOG = LoggerFactory.getLogger(IpUtils.class);
	
	public static String getClientIp(HttpServletRequest request) {
	       String ip = request.getHeader("x-forwarded-for");
	       if(LOG.isDebugEnabled()) {
	    	   LOG.debug("x-forwarded-for = {}", ip);
	       }
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	           ip = request.getHeader("Proxy-Client-IP");
	           if(LOG.isDebugEnabled()) {
		    	   LOG.debug("Proxy-Client-IP = {}", ip);
		       }
	       }
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	           ip = request.getHeader("WL-Proxy-Client-IP");
	           if(LOG.isDebugEnabled()) {
		    	   LOG.debug("WL-Proxy-Client-IP = {}", ip);
		       }
	       }
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	           ip = request.getHeader("X-Real-IP");
	           if(LOG.isDebugEnabled()) {
		    	   LOG.debug("X-Real-IP = {}", ip);
		       }
	       }
	       if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
	           ip = request.getRemoteAddr();
	           if(LOG.isDebugEnabled()) {
		    	   LOG.debug("RemoteAddr-IP = {}", ip);
		       }
	       }
	       if(StringUtils.isNotEmpty(ip)) {
	    	   ip = ip.split(",")[0];
	       }
	       return ip;
	   }
	
	public static boolean ipMatch(String ip, String ips[]) {
		if(ips == null || ips.length == 0) {
			throw new IllegalArgumentException("ips is null or emtpy");
		}
		boolean f = false;
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < ips.length; i++) {
			list.add( ips[i].replaceAll("\\*", "\\\\d+").replaceAll("\\.", "\\\\.") );
		}
		
		for(String e : list) {
			if(ip.matches(e)) {
				f = true;
				break;
			}
		}
		
		return f;
	}
}
