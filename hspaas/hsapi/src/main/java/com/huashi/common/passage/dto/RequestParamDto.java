//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年9月20日       杨猛　     新建
//*********************************************************************
package com.huashi.common.passage.dto;

/**
 * @author ym
 * @created_at 2016年9月20日下午11:41:06
 */
public class RequestParamDto {

	private String showName;
	
	private String requestName;
	
	private String defaultValue;

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public String getRequestName() {
		return requestName;
	}

	public void setRequestName(String requestName) {
		this.requestName = requestName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	
	
}
