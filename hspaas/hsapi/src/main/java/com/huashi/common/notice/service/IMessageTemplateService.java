package com.huashi.common.notice.service;

/**
 * 
  * TODO 短信模板接口
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2016年1月10日 下午4:05:35
 */
public interface IMessageTemplateService {

	String getVerifyContent(String code);
}
