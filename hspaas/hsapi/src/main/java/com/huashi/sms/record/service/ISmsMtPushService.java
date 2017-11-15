package com.huashi.sms.record.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.domain.SmsMtMessagePush;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;


/**
 * 
  * TODO 短信推送服务
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月1日 下午6:48:47
 */
public interface ISmsMtPushService {

	/**
	 * 
	   * TODO 保存推送记录
	   * 
	   * @param list
	 */
	int savePushMessage(List<SmsMtMessagePush> pushes);
	
	/**
	 * 
	   * TODO 监听所有用户信息
	   * 
	   * @return
	 */
	boolean doListenerAllUser();
	
	/**
	 * 
	   * TODO 根据用户ID获取用户待推送队列名称
	   * 
	   * @param userId
	   * @return
	 */
	String getUserPushQueueName(Integer userId);
	
	/**
	 * 
	   * TODO 设置消息ID组装待推送数据配置信息
	   * @param submit
	   * @return
	 */
	void setReadyMtPushConfig(SmsMtMessageSubmit submit);
	
	/**
	 * 
	   * TODO 发送待推送消息至队列
	   * 
	   * @param delivers
	   * @return
	 */
	boolean sendToWaitPushQueue(List<SmsMtMessageDeliver> delivers);
	
	/**
	 * 
	   * TODO 推送下行短信回执报告至用户
	   * 
	   * @param queueName
	   * 		用户对应的队列名称
	   * @return
	 */
	void pushReportToUser(String queueName);
	
	 /**
     * 
       * TODO 获取待推送数据报告（如 SID,用户自定义内容）
       * 
       * @param msgId
       * 	消息ID
       * @param mobile
       * 	手机号码
       * @return
     */
    JSONObject getWaitPushReport(String msgId, String mobile);
    
    /**
     * 
       * TODO 添加用户推送队列监听（添加用户后需要添加监听）
       * @param userId
       * @return
     */
    boolean addUserMtPushListener(Integer userId);
    
    /**
     * 
       * TODO 获取下行短信待推送消息 KEY
       * @param msgId
       * @return
     */
    String getMtPushConfigKey(String msgId); 
	
}
