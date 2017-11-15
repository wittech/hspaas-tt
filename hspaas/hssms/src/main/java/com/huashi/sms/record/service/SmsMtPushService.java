package com.huashi.sms.record.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huashi.common.user.service.IUserService;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.context.PassageContext.PushStatus;
import com.huashi.sms.record.dao.SmsMtMessagePushMapper;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.domain.SmsMtMessagePush;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.task.SmsMtPushTask;
import com.huashi.util.HttpClientUtil;

/**
 * 
 * TODO 下行短信推送服务实现
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2017年3月20日 下午9:52:18
 */
@Service
public class SmsMtPushService implements ISmsMtPushService {

	@Reference
	private IUserService userService;
	@Autowired
	private SmsMtMessagePushMapper smsMtMessagePushMapper;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private ISmsMtSubmitService smsMtSubmitService;
	
	// 批量扫描大小
	private static final int SCAN_PACKETS_SIZE = 1000;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@Transactional
	public int savePushMessage(List<SmsMtMessagePush> pushes) {
		// 保存推送信息
		return smsMtMessagePushMapper.batchInsert(pushes);
	}

	@Override
	public boolean doListenerAllUser() {
		Set<Integer> userIds = userService.findAvaiableUserIds();
		if (CollectionUtils.isEmpty(userIds)) {
			logger.error("待推送可用用户数据为空，无法监听");
			return false;
		}
		
		try {
			for(Integer userId : userIds) {
				addUserMtPushListener(userId);
			}
			
			return true;
		} catch (Exception e) {
			logger.info("用户初始化下行推送队列失败", e);
		}
		
		return false;
	}

	@Override
	public String getUserPushQueueName(Integer userId) {
		return String.format("%s:%d", SmsRedisConstant.RED_QUEUE_SMS_MT_WAIT_PUSH, userId);
	}

	@Override
	public boolean sendToWaitPushQueue(List<SmsMtMessageDeliver> delivers) {
		try {
			Map<String, JSONObject> pushMap = new HashMap<>();
			JSONObject jsonObject = null;
			
			Map<Integer, List<JSONObject>> report = new HashMap<>();
			
			// 针对上家回执数据已回但我方回执数据未入库情况需要 推送集合数据
			List<SmsMtMessageDeliver> waitPushDelivers = new ArrayList<>();
			long start = System.currentTimeMillis();
			for(SmsMtMessageDeliver deliver : delivers) {
				if(deliver == null)
					continue;
				
				// 根据消息ID获取推送相关数据
				if(MapUtils.isNotEmpty(pushMap) && pushMap.containsKey(deliver.getMsgId())) {
					jsonObject = new JSONObject();
					jsonObject.putAll(pushMap.get(deliver.getMsgId()));
				} else {
					try {
						jsonObject = getWaitPushReport(deliver.getMsgId(), deliver.getMobile());
						if(jsonObject != null)
							pushMap.put(deliver.getMsgId(), jsonObject);
					} catch (IllegalStateException e) {
						// 忽略已经推送的信息
						continue;
					}
				}
				
				if(jsonObject == null) {
					waitPushDelivers.add(deliver);
					logger.warn("当前msg_id : {}, mobile :{}未找到相关回执数据，待后续轮训补偿", deliver.getMsgId(), deliver.getMobile());
					continue;
				}
				
				// 移除待推送配置信息（如果存在移除REDIS缓存信息）
				removeReadyMtPushConfig(deliver.getMsgId(), deliver.getMobile());
				
				// 如果用户推送地址为空则表明不需要推送
				if(StringUtils.isEmpty(jsonObject.getString("pushUrl"))) {
					continue;
				}
				
				// edit by zhengying 将SID转型为string
				jsonObject.put("sid", jsonObject.getLong("sid").toString());
				jsonObject.put("mobile", deliver.getMobile());
				jsonObject.put("status", deliver.getStatusCode());
				jsonObject.put("receiveTime", deliver.getDeliverTime());
				jsonObject.put("errorMsg", deliver.getStatus() == DeliverStatus.SUCCESS.getValue() ? "" : deliver.getStatusCode());
				
				// 根据用户ID 进行分包
				try {
					if(MapUtils.isNotEmpty(report) && report.containsKey(jsonObject.getInteger("userId"))){
						report.get(jsonObject.getInteger("userId")).add(jsonObject);
					} else {
						List<JSONObject> ds = new ArrayList<>();
						ds.add(jsonObject);
						report.put(jsonObject.getInteger("userId"), ds);
					}
				} catch (Exception e) {
					logger.error("解析推送数据报告异常:{}", jsonObject.toJSONString(), e);
					continue;
				}
			}
			
			logger.info("推送用户组装报文耗时：{}", System.currentTimeMillis() - start);
			
			// 如果针对上家已回执我方未入库数据存在则保存至REDIS
			if(CollectionUtils.isNotEmpty(waitPushDelivers)) {
				sendToPushBeforeDeliverdFinished(waitPushDelivers);
			}
			
			// 根据用户ID分别组装数据
			for(Integer userId : report.keySet()) {
				stringRedisTemplate.opsForList().rightPush(getUserPushQueueName(userId), JSON.toJSONString(report.get(userId)));
			}
			
			jsonObject = null;
			report = null;
			
			return true;
		} catch (Exception e) {
			logger.error("将上家回执数据发送至待推送队列逻辑失败，回执数据为：{}", JSON.toJSONString(delivers), e);
		}
		return false;
	}
	
	/**
	 * 
	   * TODO 针对上家回执数据已回但我方回执数据未入库情况需要 推送集合数据
	   * @param waitPushDelivers
	 */
	private void sendToPushBeforeDeliverdFinished(List<SmsMtMessageDeliver> waitPushDelivers) {
		try {
			// 如果解析推送数据为空,则将数据扔至REDIS队列,待轮训任务扫描做补偿对账
			stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_MESSAGE_DELIVED_WAIT_PUSH_LIST, 
					JSON.toJSONString(waitPushDelivers, new SimplePropertyPreFilter("msgId", "mobile", "statusCode", "deliverTime", "remark", "status", "createTime")));
//			stringRedisTemplate.expire(SmsRedisConstant.RED_MESSAGE_DELIVED_WAIT_PUSH_LIST, 5, TimeUnit.MINUTES);
		} catch (Exception e) {
			logger.error("针对上家回执数据已回但我方回执数据未入库情况需要 推送集合数据失败", e);
		}
	}
	
	/**
	 * 
	   * TODO 获取待处理的推送报告数据
	   * @param msgId
	   * @param mobile
	   * @return
	 */
	public JSONObject getWaitPushReport(String msgId, String mobile) {
		// 首先在REDIS查询是否存在数据
		try {
			HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
			if(hashOperations.hasKey(getMtPushConfigKey(msgId), mobile)) {
				Object o = hashOperations.get(getMtPushConfigKey(msgId), mobile);
				if(o == null)
					return null;
				
				return JSON.parseObject(o.toString());
			}
		} catch (Exception e) {
			logger.warn("回执完成逻辑中获取待推送设置数据REDIS异常，DB补偿, {}", e.getMessage());
		}
		
		// 如果REDIS没有或者REDIS 异常，需要查询DB是否有数据（REDIS过期后自动释放，顾要做兼容判断）
		return getUserPushConfigFromDB(msgId, mobile);
	}
	
	/**
	 * 
	   * TODO REDIS 查询不到反查数据库是否需要推送
	   * 
	   * @param msgId
	   * @param mobile
	   * @return
	 */
	private JSONObject getUserPushConfigFromDB(String msgId, String mobile) {
		if(StringUtils.isEmpty(msgId) || StringUtils.isEmpty(mobile))
			return null;
		
		SmsMtMessagePush push = smsMtMessagePushMapper.findByMobileAndMsgid(mobile, msgId);
		if(push != null)
			throw new IllegalStateException("推送记录已存在，忽略");
		
		// 此处需要查询数据库是否需要有推送设置，无则不推送
		SmsMtMessageSubmit submit = smsMtSubmitService.getByMsgidAndMobile(msgId, mobile);
		if(submit == null) {
			logger.warn("msg_id : {}, mobile: {} 未找到短信相关提交数据", msgId, mobile);
			return null;
		}
		
		JSONObject pushSettings = new JSONObject();
		pushSettings.put("sid", submit.getSid());
		pushSettings.put("userId", submit.getUserId());
		pushSettings.put("msgId", msgId);
		pushSettings.put("attach", submit.getAttach());
		pushSettings.put("pushUrl", submit.getPushUrl());
		pushSettings.put("retryTimes", PUSH_RETRY_TIMES);
		
		return pushSettings;
	}
	
	/**
	 * 
	   * TODO 
	   * @param msgId
	   * @return
	 */
	public String getMtPushConfigKey(String msgId) {
		return String.format("%s:%s", SmsRedisConstant.RED_READY_MT_PUSH_CONFIG, msgId);
	}

	/**
	 * 
	   * TODO 移除待推送信息配置信息
	   * 
	   * @param msgId
	   * @param mobile
	 */
	private void removeReadyMtPushConfig(String msgId, String mobile) {
		try {
			stringRedisTemplate.opsForHash().delete(getMtPushConfigKey(msgId), mobile);
		} catch (Exception e) {
			logger.error("移除待推送消息参数设置失败, msg_id : {}", msgId, e);
		}
	}

	@Override
	public void setReadyMtPushConfig(SmsMtMessageSubmit submit) {
		try {
			stringRedisTemplate.opsForHash().put(getMtPushConfigKey(submit.getMsgId()), submit.getMobile(), JSON.toJSONString(submit, 
					new SimplePropertyPreFilter("sid", "userId", "msgId", "attach", "pushUrl", "retryTimes")));
			stringRedisTemplate.expire(getMtPushConfigKey(submit.getMsgId()), 3, TimeUnit.HOURS);
		} catch (Exception e) {
			logger.error("设置待推送消息失败", e);
		}
	}

	@Override
	public boolean addUserMtPushListener(Integer userId) {
		try {
			Thread thread = new Thread(new SmsMtPushTask(getUserPushQueueName(userId), this));
			thread.start();
			return true;
		} catch (Exception e) {
			logger.error("用户加入短信状态报告回执推送监听失败, 用户ID：{}", userId, e);
		}
		return false;
	}

	@Override
	public void pushReportToUser(String queueName) {
		try {
			stringRedisTemplate.watch(queueName);
			// SCAN_BATCH_SIZE - 1， 是因为REDIS LRANGE 默认从0索引算起，0-999 则为1000个
			List<String> list = stringRedisTemplate.opsForList().range(queueName, 0, SCAN_PACKETS_SIZE - 1);
			if(CollectionUtils.isEmpty(list))
				return;
			
			// 后续加入计数器，大于500个号码要进行拆分分包
			Map<String, List<JSONObject>> report = new HashMap<>();
			for (String t : list) {
				List<JSONObject> data = JSON.parseObject(t, new TypeReference<List<JSONObject>>(){});
				for(JSONObject d : data) {
					if(d == null)
						continue;
					
					// 根据用户的推送'URL'进行拆分组装状态报告
					try {
						if(MapUtils.isNotEmpty(report) && report.containsKey(d.getString("pushUrl"))){
							report.get(d.getString("pushUrl")).add(d);
						} else {
							List<JSONObject> ds = new ArrayList<>();
							ds.add(d);
							report.put(d.getString("pushUrl"), ds);
						}
					} catch (Exception e) {
						logger.error("解析推送数据报告异常:{}", d.toJSONString(), e);
						continue;
					}
				}
			}
			
			// 保留自本次的SCAN_BATCH_SIZE至最后数据，清除本次处理的500条
			stringRedisTemplate.opsForList().trim(queueName, list.size(), -1);
			stringRedisTemplate.unwatch();
		
			// 提交HTTP POST请求推送
			RetryResponse response = null;
			String content = null;
			Long startTime = null;
			for(String url : report.keySet()) {
				startTime = System.currentTimeMillis();
				content = JSON.toJSONString(report.get(url), new SimplePropertyPreFilter("sid", "mobile", "attach", "status", "receiveTime", "errorMsg"), SerializerFeature.WriteMapNullValue,
						SerializerFeature.WriteNullStringAsEmpty);
				
				response = post(url, content, PUSH_RETRY_TIMES, 1);
				
				logger.info("推送URL:{} , 推送数量：{} ，耗时： {} MS", url, report.get(url).size(), System.currentTimeMillis() - startTime);
				doPushPersistence(report.get(url), response, System.currentTimeMillis() - startTime);
			}
			
			report = null;
			response = null;
			content = null;
			startTime = null;

		} catch (Exception e) {
			logger.error("推送下行状态至用户失败", e);
		}
		return;
	}
	
	// 推送回执信息，如果用户回执success才算正常接收，否则重试，达到重试上限次数，抛弃
	public static final String PUSH_REPONSE_SUCCESS_CODE = "success";
	public static final int PUSH_RETRY_TIMES = 3;

	/**
	 * 
	 * TODO 调用用户回调地址
	 * 
	 * @param url
	 *            推送回调地址（HTTP）
	 * @param content
	 *            推送报文内容
	 * @param retryTimes
	 *            重试次数（默认3次）
	 * @return
	 */
	protected RetryResponse post(String url, String content, int retryTimes, int curretCount) {
		RetryResponse retryResponse = new RetryResponse();
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(content)) {
        	retryResponse.setResult("URL或内容为空");
        	return retryResponse;
        }
        
        try {
        	String result = HttpClientUtil.postReport(url, content);
        	retryResponse.setResult(StringUtils.isEmpty(result) ? PUSH_REPONSE_SUCCESS_CODE : result);
            retryResponse.setSuccess(true);
            
        } catch (Exception e) {
            logger.error("调用用户推送地址解析失败：{}， 错误信息：{}", url, e.getMessage());
			retryResponse.setResult("调用异常失败，"+ e.getMessage());
        }
        
    	if(!retryResponse.isSuccess() && curretCount <= retryTimes)  {
    		curretCount = curretCount + 1;
    		retryResponse = post(url, content, retryTimes, curretCount);
    	}
    		
    	retryResponse.setAttemptTimes(curretCount > retryTimes ? retryTimes : curretCount);
        return retryResponse;
	}
	
	public class RetryResponse {
		// 尝试次数
		private int attemptTimes = 0;
		// 返回结果
		private String result;
		// 最后一次异常信息
		private Throwable lastThrowable;
		
		private boolean isSuccess = false;

		public int getAttemptTimes() {
			return attemptTimes;
		}

		public void setAttemptTimes(int attemptTimes) {
			this.attemptTimes = attemptTimes;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public Throwable getLastThrowable() {
			return lastThrowable;
		}

		public void setLastThrowable(Throwable lastThrowable) {
			this.lastThrowable = lastThrowable;
		}

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}
	}

	/**
	 * 
	 * TODO 推送持久化
	 * 
	 * @param report
	 * @param responseContent
	 * @param timeCost
	 */
	private void doPushPersistence(List<JSONObject> data, RetryResponse retryResponse, long timeCost) {
		SmsMtMessagePush push = null;
		Set<String> msgIds = new HashSet<>();
		List<SmsMtMessagePush> pushes = new ArrayList<>();
		for(JSONObject report : data) {
			push =  new SmsMtMessagePush();
			push.setMsgId(report.getString("msgId"));
			push.setMobile(report.getString("mobile"));
			if(retryResponse == null) {
				push.setStatus(PushStatus.FAILED.getValue());
				push.setRetryTimes(0);
			} else {
				push.setStatus(retryResponse.isSuccess() ? PushStatus.SUCCESS.getValue() : PushStatus.FAILED.getValue());
				push.setRetryTimes(retryResponse.getAttemptTimes());
			}
			
			// 暂时先用作批量处理ID
			push.setResponseContent(System.nanoTime() + "");
			push.setResponseMilliseconds(timeCost);
			push.setContent(JSON.toJSONString(report, new SimplePropertyPreFilter("sid", "mobile", "attach", "status", "receiveTime", "errorMsg"), SerializerFeature.WriteMapNullValue,
					SerializerFeature.WriteNullStringAsEmpty));
			push.setCreateTime(new Date());
			
			msgIds.add(getMtPushConfigKey(report.getString("msgId")));
			pushes.add(push);
		}
		
		// 删除待推送消息信息
		stringRedisTemplate.delete(msgIds);
		// 发送数据至带持久队列中
		stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_MT_PUSH_LIST, JSON.toJSONString(pushes));
		
		push = null;
		pushes = null;
		msgIds = null;
	}
}
