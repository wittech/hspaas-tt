package com.huashi.common.notice.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.notice.dao.EmailVerifyMapper;
import com.huashi.common.notice.domain.EmailVerify;
import com.huashi.common.util.DateUtil;

@Service
public class EmailVerifyService implements IEmailVerifyService {

	@Autowired
	private EmailVerifyMapper emailVerifyMapper;

	@Override
	public boolean saveEmailVerify(String url, String uid, String email) {
		EmailVerify ev = new EmailVerify();
		ev.setCurlInfo(url);
		ev.setUid(uid);
		ev.setEmail(email);
		ev.setSendTime(new Date());
		return emailVerifyMapper.insert(ev) > 0;
	}

	@Override
	public String isAvaiable(String uid) throws Exception {
		if (StringUtils.isEmpty(uid))
			throw new Exception("链接无效，请重新输入邮箱");
		
		EmailVerify ev = emailVerifyMapper.selectByUid(uid);
		
		if (ev == null || ev.getSendTime() == null)
			throw new Exception("链接无效，请重新输入邮箱");
		
		if (!DateUtil.isBeyond24Hour(ev.getSendTime(), new Date()))
			throw new Exception("链接已过期，请重新输入邮箱");
		
		if(ev.getValidTime() != null)
			throw new Exception("链接已激活，无法重复使用");
		
		return ev.getEmail();
	}

	@Override
	public void activeEmail(String uid) {
		if (StringUtils.isEmpty(uid))
			return;
		emailVerifyMapper.updateByUid(uid);
	}

}
