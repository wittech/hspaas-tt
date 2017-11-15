/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.sms.settings.service;

/**
 * 
 * TODO 手机号码拦截防火墙
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月15日 上午9:42:57
 */
public interface ISmsMobileTablesService {

	/**
	 * 
	 * TODO 设置同一个手机号码发送的记录（最后一次发送时间，发送总数量）
	 * 
	 * @param userId
	 * @param templateId
	 * @param mobile
	 * @param sendCount
	 */
	void setMobileSendRecord(int userId, Long templateId, String mobile, int sendCount);

	/**
	 * 
	 * TODO 同一个手机号码同一模板下号码是否已超速/是否超限（一天内）
	 * 
	 * @param userId
	 * @param templateId
	 * @param mobile
	 * @param maxSpeed
	 * @param maxLimit
	 * @return
	 */
	int checkMobileIsBeyondExpected(int userId, Long templateId, String mobile, int maxSpeed, int maxLimit);

	public static final int NICE_PASSED = 0;
	public static final int MOBILE_BEYOND_SPEED = 1;
	public static final int MOBILE_BEYOND_TIMES = 2;
}
