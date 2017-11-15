package com.huashi.sms.record.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.sms.record.dao.SmsMoMessagePushMapper;
import com.huashi.sms.record.domain.SmsMoMessagePush;

@Service
public class SmsMoPushService implements ISmsMoPushService {
	
	@Autowired
	private SmsMoMessagePushMapper smsMoMessagePushMapper;

	@Override
	@Transactional
	public int savePushMessage(List<SmsMoMessagePush> pushes) {
		// 保存推送信息
		return smsMoMessagePushMapper.batchInsert(pushes);
	}
	

}
