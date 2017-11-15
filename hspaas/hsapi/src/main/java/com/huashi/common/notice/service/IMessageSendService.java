package com.huashi.common.notice.service;

import com.huashi.common.notice.vo.SmsResponse;

/**
 * 
  * TODO 短信发送接口
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年1月10日 下午4:10:49
 */
public interface IMessageSendService {

	/**
	 * 
	   * TODO 发送短信校验码
	   * @param code
	   * 		校验码
	   * @param userId
	   * 		用户ID（注册时发送userID 为0）
	   * @return
	 */
	boolean sendVerifyCode(String code, String mobile);
	
	/**
	 * 
	   * TODO 发送系统短信
	   * @param content
	   * 		短信内容
	   * @param mobile
	   * 		手机号码（支持多个，多个手机号码用","分割）
	   * @return
	 */
	SmsResponse sendSystemMessage(String mobile, String content);
	
	
	/**
	 * 发送用户自定义短信
	 * @param appkey
	 * @param appsecret
	 * @param content
	 * @param mobile
	 * @return
	 */
	SmsResponse sendCustomMessage(String appkey, String appsecret, 
			String mobile, String content);
}
