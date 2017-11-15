package com.huashi.common.notice.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.notice.dao.EmailSendRecordMapper;
import com.huashi.common.notice.domain.EmailSendRecord;
import com.huashi.common.notice.domain.EmailTemplate;
import com.huashi.common.notice.exception.SendException;
import com.huashi.common.notice.util.EmailSendUtil;
import com.huashi.common.util.PatternUtil;

@Service
public class EmailSendService implements IEmailSendService {
	
	private Logger logger = LoggerFactory.getLogger(EmailSendService.class);

	@Autowired
	private IEmailTemplateService emailTemplateService;
	@Autowired
	private EmailSendUtil emailSendUtil;
	@Autowired
	private EmailSendRecordMapper emailSendRecordMapper;
	@Autowired
	private IEmailVerifyService emailVerifyService;

	@Value("${web.root.path}")
	private String webRootPath;

	@Override
	public boolean sendRegisterVerifyContent(String email) {
		if (!PatternUtil.isEmail(email)) {
			logger.error("{} 不符合电子邮箱格式", email);
			return false;
		}
			
		// 序列编号，用于标识
		String uid = java.util.UUID.randomUUID().toString().replaceAll("-", "");
		String url = buildUrlByEmail(uid); // 激活URL
		EmailTemplate template = emailTemplateService.getRegisterVerifyContent(url);
		if (template == null)
			return false;
		
		try {
			emailSendUtil.sendEmail(email, template.getSubject(), template.getContent());
			boolean isOK = emailVerifyService.saveEmailVerify(url, uid, email);
			if(isOK)
				this.saveSendRecord(new EmailSendRecord(email, template.getSubject(), template.getContent()));
		} catch (SendException e) {
			throw e;
		}
		
		return true;
	}

	/**
	 * 
	   * TODO 拼接邮箱认验证RL
	   * @return
	 */
	private String buildUrlByEmail(String uid) {
		return String.format("%s/register/verify_email/%s", webRootPath, uid);
	}

	/**
	 * 
	   * TODO 添加邮件发送记录
	   * 
	   * @param record
	 */
	private void saveSendRecord(EmailSendRecord record) {
		record.setCreateTime(new Date());
		emailSendRecordMapper.insert(record);
	}

}
