package com.huashi.sms.record.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.record.dao.SmsMtMessageDeliverMapper;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;

/**
 * 
  * TODO 短信回执服务实现
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2016年12月14日 上午11:03:28
 */
@Service
public class SmsMtDeliverService implements ISmsMtDeliverService{

	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private SmsMtMessageDeliverMapper smsMtMessageDeliverMapper;
	@Reference
	private IPushConfigService pushConfigService;
	@Autowired
	private ISmsMtPushService smsMtPushService;
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public SmsMtMessageDeliver findByMobileAndMsgid(String mobile, String msgId) {
		if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(msgId))
			return null;
		
		return smsMtMessageDeliverMapper.selectByMobileAndMsgid(msgId, mobile);
	}

	@Override
	public int batchInsert(List<SmsMtMessageDeliver> list) {
		if(CollectionUtils.isEmpty(list))
			return 0;
		
		return smsMtMessageDeliverMapper.batchInsert(list);
	}
	
	@Override
	public int doFinishDeliver(List<SmsMtMessageDeliver> delivers) {
		
//		// ZK 分布式锁
//		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000,3);
//		CuratorFramework client = CuratorFrameworkFactory.newClient(zkConnect, retryPolicy);
//		
//		// 不可重入锁
//		InterProcessLock lock = new InterProcessSemaphoreMutex(client, zkLockNode);
//		
//        client.start();
		
		try  {
			
			if(CollectionUtils.isEmpty(delivers))
				return 0;
			
//			lock.acquire();
			
			// 将待推送消息发送至用户队列进行处理（2017-03-20 合包处理）
			smsMtPushService.compareAndPushBody(delivers);
			
			// 提交至待DB持久队列
			stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_STATUS_RECEIVE_LIST, JSON.toJSONString(delivers));
			
			return delivers.size();
		} catch (Exception e) {
			logger.error("处理待回执信息REDIS失败，失败信息：{}", JSON.toJSONString(delivers), e);
			throw new RuntimeException("状态报告回执处理失败");
		} 
//		finally {
//			//获取JVM锁(同一进程内有效)  
//			try {
//				lock.release();
//				client.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//		}
	}
	
	/**
	 * 
	   * TODO 此条回执是否已经已处理（针对下家推送多次或者轮训取数据报告取多次情况，需要去除我方已经处理完成的数据，防止数据插入多次）
	   * 
	   * @param deliver
	 */
	public boolean isDelivedHasDone(String msgId, String mobile) {
		try {
			boolean isDone = stringRedisTemplate.opsForHash().hasKey(getReceiptKey(msgId), mobile);
			if(isDone) {
				logger.warn("回执数据正在处理中 ，本条忽略，消息ID：{}，手机号码：{}", msgId, mobile);
				return true;
			}
			
			SmsMtMessageDeliver deliver = smsMtMessageDeliverMapper.selectByMobileAndMsgid(msgId, mobile);
			if(deliver != null) {
				logger.warn("回执数据已处理完成 ，本条忽略，消息ID：{}，手机号码：{}", msgId, mobile);
				return true;
			}
			
			// 设置 待回执 短信内容
			stringRedisTemplate.opsForHash().put(getReceiptKey(msgId), mobile, String.valueOf(System.currentTimeMillis()));
			stringRedisTemplate.expire(getReceiptKey(msgId), 3, TimeUnit.DAYS);
			
			return false;
		} catch (Exception e) {
			logger.error("判断回执数据是否已处理失败，msg_id:{}, mobile:{}", msgId, mobile, e);
			return false;
		}
	}
	
	private String getReceiptKey(String msgId) {
		return String.format("%s:%s", SmsRedisConstant.RED_READY_MESSAGE_WAIT_RECEIPT, msgId);
	}
	
	@Override
	public boolean doDeliverToException(JSONObject obj) {
		try {
			stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_MESSAGE_STATUS_RECEIPT_EXCEPTION_LIST,
					JSON.toJSONString(obj));
			return true;
		} catch (Exception e) {
			logger.error("发送回执错误信息失败 {}", JSON.toJSON(obj), e);
			return false;
		}
	}

	@Override
	public boolean saveDeliverLog(JSONObject report) {
		
		
		
		return false;
	}

}
