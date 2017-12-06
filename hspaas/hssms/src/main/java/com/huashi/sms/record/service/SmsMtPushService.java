package com.huashi.sms.record.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;
import com.huashi.common.user.service.IUserService;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.worker.fork.MtReportPushToDeveloperWorker;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.context.PassageContext.PushStatus;
import com.huashi.sms.record.dao.SmsMtMessagePushMapper;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.domain.SmsMtMessagePush;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
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

	@Value("${thread.poolsize.push:2}")
	private int pushThreadPoolSize;
	@Autowired
	private ApplicationContext applicationContext;
	@Resource
	private ThreadPoolTaskExecutor threadPoolTaskExecutor;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	// 推送分包分割主键（根据推送地址进行切割）
	private static final String PUSH_BODY_SUBPACKAGE_KEY = "puthUrl";

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
	
	/**
	 * 
	   * TODO 组装报文前半部分信息
	   * 
	   * @param body
	   * 		报文信息（上下文使用）
	   * @param deliver
	   * 		网关回执数据
	   * @param cachedPushArgs
	   * 		本地缓存推送参数信息（为了加快速度缓存变量，而不是每次都根据MSG_ID和MOBILE查询REDIS或者DB）
	   * @param failoverDeliversQueue
	   * 		本次处理失败（一般是查询REDIS或者DB都没有推送配置信息）重入待处理回执状态（后续线程重试）
	   * 		
	   * @return
	 */
	private boolean assembleBody(JSONObject body, SmsMtMessageDeliver deliver, Map<String, JSONObject> cachedPushArgs,
			List<SmsMtMessageDeliver> failoverDeliversQueue) {
		// 如果本地缓存中存在相关值，则直接获取，无需请求REDIS或DB
		if(MapUtils.isNotEmpty(cachedPushArgs) && cachedPushArgs.containsKey(deliver.getMsgId())) {
			body.putAll(cachedPushArgs.get(deliver.getMsgId()));
			return true;
		}
		
		else{
			try {
				body = getWaitPushBodyArgs(deliver.getMsgId(), deliver.getMobile());
				if(body != null) {
					cachedPushArgs.put(deliver.getMsgId(), body);
					return true;
				} 
				else {
					// 如果数据生成时间超过5分钟则舍弃，不再重新补偿
//					if(System.currentTimeMillis() - deliver.getCreateTime().getTime() >= 5 * 60 * 1000 )
//						continue;
					
					logger.info("deliver提交时间：{} 计算结果：{}", deliver.getCreateTime().getTime(), 
							System.currentTimeMillis() - deliver.getCreateTime().getTime());
					
					failoverDeliversQueue.add(deliver);
				}
			} catch (IllegalStateException e) {
				logger.warn(e.getMessage());
			}

			return false;
		} 
	}
	
	/**
	 * 
	   * TODO 回执信息按照用户分包，上家一次性回执多个回执报文，报文中很可能是我司多个用户数据回执信息，顾要分包
	   * @param body
	   * 		单个报文信息
	   * @param deliver
	   * 		上家回执报文信息
	   * @param userBodies
	   * 		本次需要处理的过程累计的报文集合信息
	   * @return
	 */
	private boolean subQueue(JSONObject body, SmsMtMessageDeliver deliver, Map<Integer, List<JSONObject>> userBodies) {
		// edit by zhengying 将SID转型为string（部分客户提到推送到客户侧均按照字符类型去解析，顾做了转义）
		body.put("sid", body.getLong("sid").toString());
		body.put("mobile", deliver.getMobile());
		body.put("status", deliver.getStatusCode());
		body.put("receiveTime", deliver.getDeliverTime());
		body.put("errorMsg", deliver.getStatus() == DeliverStatus.SUCCESS.getValue() ? "" : deliver.getStatusCode());
		
		try {
			// 如果本次处理的用户ID已经包含在山下文处理集合中，则直接追加即可
			if(MapUtils.isNotEmpty(userBodies) && userBodies.containsKey(body.getInteger("userId"))){
				userBodies.get(body.getInteger("userId")).add(body);
			} 
			// 如果未曾处理过，则重新初始化集合
			else {
//				List<JSONObject> ds = new ArrayList<>();
//				ds.add(body);
				userBodies.put(body.getInteger("userId"), Arrays.asList(body));
			}
			
			return true;
		} catch (Exception e) {
			logger.error("解析推送数据报告异常:{}", body.toJSONString(), e);
			return false;
		}
	}

	@Override
	public boolean compareAndPushBody(List<SmsMtMessageDeliver> delivers) {
		if(CollectionUtils.isEmpty(delivers))
			return false;
		
		JSONObject body = new JSONObject();
		Map<String, JSONObject> cachedPushArgs = new HashMap<>();
		// 用户ID对应的 推送报告集合数据
		Map<Integer, List<JSONObject>> userBodies = new HashMap<>();
		// 针对上家回执数据已回，但我方回执数据未入库以及REDIS没有推送配置信息，后续线程重试补完成
		List<SmsMtMessageDeliver> failoverDeliversQueue = new ArrayList<>();
		
		try {
			for(SmsMtMessageDeliver deliver : delivers) {
				if(deliver == null)
					continue;
				
				if(!assembleBody(body, deliver, cachedPushArgs, failoverDeliversQueue))
					continue;
				
				removeReadyMtPushConfig(deliver.getMsgId(), deliver.getMobile());
				
				// 如果用户推送地址为空则表明不需要推送
				if(StringUtils.isEmpty(body.getString(PUSH_BODY_SUBPACKAGE_KEY)))
					continue;
				
				if(!subQueue(body, deliver, userBodies))
					continue;
			}
			
			// 如果针对上家已回执我方未入库数据存在则保存至REDIS
			if(CollectionUtils.isNotEmpty(failoverDeliversQueue)) {
				sendToDeliverdFailoverQueue(failoverDeliversQueue);
			}
			
			// 根据用户ID分别组装数据
			for(Integer userId : userBodies.keySet()) {
				stringRedisTemplate.opsForList().rightPush(getUserPushQueueName(userId), JSON.toJSONString(userBodies.get(userId)));
			}
			return true;
		} catch (Exception e) {
			logger.error("将上家回执数据发送至待推送队列逻辑失败，回执数据为：{}", JSON.toJSONString(delivers), e);
		}
		
		// 处理本次资源，加速GC
		body = null;
		userBodies = null;
		cachedPushArgs = null;
		failoverDeliversQueue = null;
		
		return false;
	}
	
	/**
	 * 
	   * TODO 针对上家回执数据已回但我方回执数据未入库情况需要 推送集合数据
	   * 
	   * @param failoverDeliversQueue
	 */
	private void sendToDeliverdFailoverQueue(List<SmsMtMessageDeliver> failoverDeliversQueue) {
		try {
			stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_QUEUE_SMS_DELIVER_FAILOVER, 
					JSON.toJSONString(failoverDeliversQueue, new SimplePropertyPreFilter("msgId", "mobile", "statusCode", 
							"deliverTime", "remark", "status", "createTime")));
//			stringRedisTemplate.expire(SmsRedisConstant.RED_MESSAGE_DELIVED_WAIT_PUSH_LIST, 5, TimeUnit.MINUTES);
			logger.warn("deliver failover count : {}" , failoverDeliversQueue.size());
		} catch (Exception e) {
			logger.error("针对上家回执数据已回但我方回执数据未入库情况需要 推送集合数据失败", e);
		}
	}
	
	/**
	 * 
	   * TODO 获取待处理的推送报告定义参数信息
	   * 
	   * 	eg. （SID, pushUrl, pushTimes ...）
	   * 
	   * @param msgId
	   * @param mobile
	   * @return
	 */
	public JSONObject getWaitPushBodyArgs(String msgId, String mobile) {
		// 首先在REDIS查询是否存在数据
		try {
			HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
			if(hashOperations.hasKey(getMtPushConfigKey(msgId), mobile)) {
				Object o = hashOperations.get(getMtPushConfigKey(msgId), mobile);
				if(o != null)
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
			throw new IllegalStateException("msgId:" + msgId +", mobile:" + mobile + "推送记录已存在，忽略");
		
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
			for (int i = 0; i < pushThreadPoolSize; i++) {
				threadPoolTaskExecutor.execute(new MtReportPushToDeveloperWorker(applicationContext, getUserPushQueueName(userId)));
			}
			return true;
		} catch (Exception e) {
			logger.error("用户加入短信状态报告回执推送监听失败, 用户ID：{}", userId, e);
		}
		return false;
	}

	@Override
	public void pushMessageBodyToDeveloper(List<JSONObject> bodies) {
		// 资源URL对应的推送地址(资源地址对应的总量超过500应该分包发送)
		Map<String, List<JSONObject>> urlBodies = new HashMap<>();
		String urlKey = null;
		
		for(JSONObject body : bodies) {
			if(MapUtils.isEmpty(body))
				continue;
			
			if(StringUtils.isEmpty(body.getString(PUSH_BODY_SUBPACKAGE_KEY)))
				continue;
			
			urlKey = body.getString(PUSH_BODY_SUBPACKAGE_KEY);
			
			// 根据用户的推送'URL'进行拆分组装状态报告
			try {
				if(MapUtils.isNotEmpty(urlBodies) && urlBodies.containsKey(urlKey)){
					urlBodies.get(urlKey).add(body);
				} else {
					urlBodies.put(urlKey, Arrays.asList(body));
				}
			} catch (Exception e) {
				logger.error("解析推送数据报文异常:{}", body.toJSONString(), e);
				continue;
			}
		}
		
		sendBody(urlBodies);
	
		urlKey = null;
		urlBodies = null;
	}
	
	/**
	 * 
	   * TODO 发送短信状态报文至下家并完成异步持久化
	   * 
	   * @param urlBodies
	   * @return
	 */
	private boolean sendBody(Map<String, List<JSONObject>> urlBodies) {
		try {
			// 提交HTTP POST请求推送
			RetryResponse response = null;
			String content = null;
			Long startTime = null;
			for(String url : urlBodies.keySet()) {
				startTime = System.currentTimeMillis();
				content = JSON.toJSONString(urlBodies.get(url), new SimplePropertyPreFilter("sid", "mobile", "attach", "status", "receiveTime", "errorMsg"), 
						SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty);
				
				response = post(url, content, PUSH_RETRY_TIMES, 1);
				
				logger.info("推送URL:{} , 推送数量：{} ，耗时： {} MS", url, urlBodies.get(url).size(), System.currentTimeMillis() - startTime);
				doPushPersistence(urlBodies.get(url), response, System.currentTimeMillis() - startTime);
			}
			
			response = null;
			content = null;
			startTime = null;
			
			return true;
		} catch (Exception e) {
			logger.error("推送下家状态报文或持久化失败 ", e);
			return false;
		}
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
		Set<String> waitPushMsgIdRedisKeys = new HashSet<>();
		List<SmsMtMessagePush> persistPushesList = new ArrayList<>();
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
			
			waitPushMsgIdRedisKeys.add(getMtPushConfigKey(report.getString("msgId")));
			persistPushesList.add(push);
		}
		
		// 删除待推送消息信息
		stringRedisTemplate.delete(waitPushMsgIdRedisKeys);
		// 发送数据至带持久队列中
		stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_MT_PUSH_LIST, JSON.toJSONString(persistPushesList));
		
		push = null;
		persistPushesList = null;
		waitPushMsgIdRedisKeys = null;
	}

}
