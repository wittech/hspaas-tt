package com.huashi.common.notice.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.notice.context.TemplateContext.SmsCode;
import com.huashi.common.notice.dao.SmsTemplateMapper;
import com.huashi.common.notice.domain.SmsTemplate;

@Service
public class MessageTemplateService implements IMessageTemplateService {
	
	@Autowired
	private SmsTemplateMapper smsTemplateMapper;
	
	@Override
	public String getVerifyContent(String code) {
		// 根据短信模板查询 短信动态码模板内容
		SmsTemplate template = smsTemplateMapper.selectByCode(SmsCode.VERIFY_CODE.getCode());
		if(template == null)
			return null;
		
		return String.format(template.getContent(), code);
	}

}
