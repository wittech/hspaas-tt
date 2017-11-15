package com.huashi.sms.config.worker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.service.ISmsMtDeliverService;

/**
 * 
 * TODO 短信主任务待持久线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年12月10日 下午6:03:39
 */
public class SmsWaitReceiptPersistenceWorker implements Runnable {

	private StringRedisTemplate stringRedisTemplate;
	private ISmsMtDeliverService smsMtDeliverService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	public SmsWaitReceiptPersistenceWorker(StringRedisTemplate stringRedisTemplate, ISmsMtDeliverService smsMtDeliverService) {
		this.stringRedisTemplate = stringRedisTemplate;
		this.smsMtDeliverService = smsMtDeliverService;
	}

	// 批量扫描大小
	private static final int SCAN_BATCH_SIZE = 1000;

	@Override
	public void run() {
		while (true) {
			try {
				stringRedisTemplate.watch(SmsRedisConstant.RED_DB_MESSAGE_STATUS_RECEIVE_LIST);
				// SCAN_BATCH_SIZE - 1， 是因为REDIS LRANGE 默认从0索引算起，0-999 则为1000个
				List<String> list = stringRedisTemplate.opsForList().range(SmsRedisConstant.RED_DB_MESSAGE_STATUS_RECEIVE_LIST, 0,
						SCAN_BATCH_SIZE - 1);
				
				if(CollectionUtils.isEmpty(list))
					continue;
				
				List<SmsMtMessageDeliver> delivers = new ArrayList<>();
				for (String t : list) {
					delivers.addAll(JSON.parseObject(t, new TypeReference<List<SmsMtMessageDeliver>>(){}));
				}
				
				// 如果解析数据为空，则直接执行下次逻辑
				if(CollectionUtils.isEmpty(delivers)) {
					continue;
				}
				
//				if(CollectionUtils.isNotEmpty(removeDelivers)) {
//					logger.warn("本次回执状态持久数据去重总数：{}，信息为 ：{}", removeDelivers.size(), JSON.toJSONString(removeDelivers));
//					delivers.removeAll(removeDelivers);
//				}

				doPersistence(list, delivers);
				
				// 保留自本次的SCAN_BATCH_SIZE至最后数据，清除本次处理的1000条
				stringRedisTemplate.opsForList().trim(SmsRedisConstant.RED_DB_MESSAGE_STATUS_RECEIVE_LIST, list.size(), -1);
				stringRedisTemplate.unwatch();
				
				// 移除 处理中回执状态
//				doRemoveReceiptDoing(delivers);
				
				delivers = null;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 
	 * TODO 持久化主任务
	 * 
	 * @param list
	 * @param tasks
	 * @param taskPackets
	 */
	private void doPersistence(List<String> list, List<SmsMtMessageDeliver> delivers) {
		try {
			smsMtDeliverService.batchInsert(delivers);
			logger.info("短信回执信息持久同步完成，共处理  {} 条", delivers.size());
		} catch (Exception e) {
			logger.error("短信回执信息异步持久化失败，数据为：{}", JSON.toJSONString(delivers), e);
			// 处理失败放置处理失败队列中
			stringRedisTemplate.opsForList().rightPushAll(SmsRedisConstant.RED_DB_MESSAGE_STATUS_RECEIVE_FAILED_LIST, list);
		}
	}
	
	/**
	 * 
	   * TODO 移除正在处理中的回执状态标识
	 */
	public void doRemoveReceiptDoing(List<SmsMtMessageDeliver> delivers) {
		try {
			HashOperations<String, String, String> hashOpertions = stringRedisTemplate.opsForHash();
			for(SmsMtMessageDeliver deliver : delivers) {
				hashOpertions.delete(getReceiptKey(deliver.getMsgId()), deliver.getMobile());
			}
		} catch (Exception e) {
			logger.error("删除正在处理回执状态标识失败，失败数据为{}", JSON.toJSONString(delivers), e);
		}
		
	}
	
	private String getReceiptKey(String msgId) {
		return String.format("%s:%s", SmsRedisConstant.RED_READY_MESSAGE_WAIT_RECEIPT, msgId);
	}
	
	/**
	 * 
	   * TODO 此条回执是否已经已处理（针对下家推送多次或者轮训取数据报告取多次情况，需要去除我方已经处理完成的数据，防止数据插入多次）
	   * 
	   * @param deliver
	 */
	boolean isDelivedHasDone(String msgId, String mobile) {
		try {
			SmsMtMessageDeliver deliver = smsMtDeliverService.findByMobileAndMsgid(msgId, mobile);
			if(deliver != null) {
				logger.warn("回执数据已处理完成 ，本条忽略，消息ID：{}，手机号码：{}", msgId, mobile);
				return true;
			}
			
			return false;
		} catch (Exception e) {
			logger.error("判断回执数据是否已处理失败，msg_id:{}, mobile:{}", msgId, mobile, e);
			return false;
		}
	}

}
