//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月25日       杨猛　     新建
//*********************************************************************
package com.huashi.hsboss.dto;

import java.util.ArrayList;
import java.util.List;

import com.huashi.sms.passage.context.PassageContext.RouteType;
import com.huashi.sms.passage.domain.SmsPassage;

/**
 * @author ym
 * @created_at 2016年8月25日下午7:20:46
 */
public class RoutePassageGroup {

	private RouteType type;
	
	private List<SmsPassage> passageList = new ArrayList<SmsPassage>();

	public RouteType getType() {
		return type;
	}

	public void setType(RouteType type) {
		this.type = type;
	}

	public List<SmsPassage> getPassageList() {
		return passageList;
	}

	public void setPassageList(List<SmsPassage> passageList) {
		this.passageList = passageList;
	}
	
	
}
