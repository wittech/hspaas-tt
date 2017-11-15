package com.huashi.sms.record.service;

import java.util.List;

import com.huashi.sms.record.domain.SmsMoMessagePush;


/**
 * 
  * TODO 短信上行推送服务
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月1日 下午6:48:47
 */
public interface ISmsMoPushService {

	/**
	 * 
	   * TODO 保存推送记录
	   * 
	   * @param list
	 */
	int savePushMessage(List<SmsMoMessagePush> pushes);
	
}
