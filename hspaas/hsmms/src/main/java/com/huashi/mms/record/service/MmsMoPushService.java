package com.huashi.mms.record.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.mms.record.dao.MmsMoMessagePushMapper;
import com.huashi.mms.record.domain.MmsMoMessagePush;

@Service
public class MmsMoPushService implements IMmsMoPushService {
	
	@Autowired
	private MmsMoMessagePushMapper mmsMoMessagePushMapper;

	@Override
	@Transactional
	public int savePushMessage(List<MmsMoMessagePush> pushes) {
		// 保存推送信息
		return mmsMoMessagePushMapper.batchInsert(pushes);
	}
	

}
