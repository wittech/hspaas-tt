package com.huashi.sms.record.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.huashi.bill.bill.domain.ConsumptionReport;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.user.model.UserModel;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.rabbit.RabbitMessageQueueManager;
import com.huashi.sms.config.rabbit.listener.SmsWaitSubmitListener;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.dao.SmsMtMessagePushMapper;
import com.huashi.sms.record.dao.SmsMtMessageSubmitMapper;
import com.huashi.sms.record.domain.SmsMtMessageDeliver;
import com.huashi.sms.record.domain.SmsMtMessagePush;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.vo.SmsLastestRecordVo;
import com.huashi.sms.record.vo.SmsLastestRecordVo.MessageNode;
import com.huashi.sms.task.context.MQConstant;
import com.huashi.sms.task.context.MQConstant.WordsPriority;
import com.huashi.sms.task.context.TaskContext.MessageSubmitStatus;
import com.huashi.sms.task.domain.SmsMtTaskPackets;

/**
 * 
 * TODO 下行短信服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年2月27日 下午5:48:03
 */
@Service
public class SmsMtSubmitService implements ISmsMtSubmitService, RabbitTemplate.ConfirmCallback {

	@Reference
	private IUserService userService;
	@Autowired
	private SmsMtMessageSubmitMapper smsMtMessageSubmitMapper;
	@Autowired
	private SmsMtMessagePushMapper pushMapper;	
	@Reference
	private IPushConfigService pushConfigService;
	@Reference
	private ISmsMtDeliverService smsMtDeliverService;
	@Reference
	private IUserDeveloperService userDeveloperService;
	@Autowired
	private ISmsPassageService smsPassageService;
//	@Autowired
//	private SqlSessionTemplate sqlSessionTemplate;
	@Autowired
	private SmsWaitSubmitListener smsWaitSubmitListener;
	@Autowired
	private ISmsMtPushService smsMtPushService;
	
	@Resource
	private RabbitTemplate rabbitTemplate;
	@Resource
	private StringRedisTemplate stringRedisTemplate;

	@Autowired
	private RabbitMessageQueueManager rabbitMessageQueueManager;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public List<SmsMtMessageSubmit> findBySid(long sid) {
		return smsMtMessageSubmitMapper.findBySid(sid);
	}

	@Override
	public boolean save(SmsMtMessageSubmit submit) {

		submit.setCreateTime(new Date());
		return smsMtMessageSubmitMapper.insertSelective(submit) > 0;
	}

	@Override
	public BossPaginationVo<SmsMtMessageSubmit> findPage(Map<String, Object> queryParams) {
		
		changeTimestampeParamsIfExists(queryParams);
		
		BossPaginationVo<SmsMtMessageSubmit> page = new BossPaginationVo<SmsMtMessageSubmit>();
		page.setCurrentPage(Integer.parseInt(queryParams.getOrDefault("currentPage", 1).toString()));
		int total = smsMtMessageSubmitMapper.findCount(queryParams);
		if (total <= 0)
			return page;
		
		page.setTotalCount(total);
		queryParams.put("start", page.getStartPosition());
		queryParams.put("end", page.getPageSize());
		List<SmsMtMessageSubmit> dataList = smsMtMessageSubmitMapper.findList(queryParams);
		if (CollectionUtils.isEmpty(dataList))
			return page;

		joinRecordFascade(dataList);

		page.getList().addAll(dataList);

		return page;
	}
	
	/**
	 * 
	   * TODO 转换时间戳信息
	   * 
	   * @param queryParams
	 */
	private void changeTimestampeParamsIfExists(Map<String, Object> queryParams) {
		String startDate = queryParams.get("startDate") == null ? "" : queryParams.get("startDate").toString();
		String endDate = queryParams.get("endDate") == null ? "" : queryParams.get("endDate").toString();
		
		if (StringUtils.isNotBlank(startDate))
			queryParams.put("startDate",  DateUtil.getSecondDate(startDate).getTime());
		
		
		if (StringUtils.isNotBlank(endDate))
			queryParams.put("endDate",  DateUtil.getSecondDate(endDate).getTime());
		
	}

	/**
	 * 
	 * TODO 加入记录关联列数据（主要针对回执数据和推送数据）
	 * 
	 * @param dataList
	 */
	private void joinRecordFascade(List<SmsMtMessageSubmit> submits) {
		Map<String, SmsMtMessagePush> pushMap = new HashMap<String, SmsMtMessagePush>();

		// 加入内存对象，减少DB的查询次数
		Map<Integer, UserModel> userModelMap = new HashMap<>();
		Map<Integer, String> passageMap = new HashMap<>();

		String key = null;
		for (SmsMtMessageSubmit record : submits) {
			key = String.format("%s_%s", record.getMsgId(), record.getMobile());
			if (record.getUserId() != null) {
				if (userModelMap.containsKey(record.getUserId())) {
					record.setUserModel(userModelMap.get(record.getUserId()));
				} else {
					record.setUserModel(userService.getByUserId(record.getUserId()));
					userModelMap.put(record.getUserId(), record.getUserModel());
				}
			}

			if (record.getNeedPush()) {
				if (pushMap.containsKey(key)) {
					record.setMessagePush(pushMap.get(key));
				} else {
					record.setMessagePush(pushMapper.findByMobileAndMsgid(record.getMobile(), record.getMsgId()));
					pushMap.put(key, record.getMessagePush());
				}
			}
			// if(record.getStatus() == 0){
			// if(deliverMap.containsKey(key)) {
			// record.setMessageDeliver(deliverMap.get(key));
			// } else {
			// record.setMessageDeliver(smsMtDeliverService.findByMobileAndMsgid(record.getMobile(),record.getMsgId()));
			// deliverMap.put(key, record.getMessageDeliver());
			// }
			// }
			if (record.getPassageId() != null) {
				if (record.getPassageId() == PassageContext.EXCEPTION_PASSAGE_ID)
					record.setPassageName(PassageContext.EXCEPTION_PASSAGE_NAME);

				else {
					if (passageMap.containsKey(record.getUserId())) {
						record.setPassageName(passageMap.get(record.getPassageId()));
					} else {
						SmsPassage passage = smsPassageService.findById(record.getPassageId());
						if (passage != null) {
							record.setPassageName(passage.getName());
							passageMap.put(record.getPassageId(), passage.getName());
						}
					}
				}
			}
		}

		userModelMap = null;
		passageMap = null;
	}

	@Override
	public PaginationVo<SmsMtMessageSubmit> findPage(int userId, String mobile, String startDate, String endDate,
			String currentPage, String sid) {
		if (userId <= 0)
			return null;

		int _currentPage = PaginationVo.parse(currentPage);

		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("userId", userId);
		if (StringUtils.isNotBlank(sid)) {
			paramMap.put("sid", sid);
		} else {
			paramMap.put("sid", "-1");
		}
		if (StringUtils.isNotBlank(mobile)) {
			paramMap.put("mobile", mobile);
		} else {
			paramMap.put("mobile", "");
		}
		paramMap.put("content", "");
		paramMap.put("startDate", DateUtil.getSecondDate(startDate + " 00:00:01").getTime());
		paramMap.put("endDate", DateUtil.getSecondDate(endDate + " 23:59:59").getTime());
		paramMap.put("cmcp", -1);
		paramMap.put("status", -1);
		paramMap.put("passageName", "");
		int totalRecord = smsMtMessageSubmitMapper.findCount(paramMap);
		if (totalRecord == 0)
			return null;

		paramMap.put("start", PaginationVo.getStartPage(_currentPage));
		paramMap.put("end", PaginationVo.DEFAULT_RECORD_PER_PAGE);

		List<SmsMtMessageSubmit> dataList = smsMtMessageSubmitMapper.findList(paramMap);
		for (SmsMtMessageSubmit record : dataList) {
			if (record.getUserId() != null) {
				record.setUserModel(userService.getByUserId(record.getUserId()));
			}
			if (record.getNeedPush()) {
				record.setMessagePush(pushMapper.findByMobileAndMsgid(record.getMobile(), record.getMsgId()));
			}
			if (record.getStatus() == 0) {
				record.setMessageDeliver(
						smsMtDeliverService.findByMobileAndMsgid(record.getMobile(), record.getMsgId()));
			}
		}
		return new PaginationVo<SmsMtMessageSubmit>(dataList, _currentPage, totalRecord);
	}

	@Override
	public List<ConsumptionReport> getConsumeMessageInYestday() {
		Set<Integer> userList = userService.findAvaiableUserIds();
		if (CollectionUtils.isEmpty(userList))
			throw new RuntimeException("用户数据异常，请检修");

		List<ConsumptionReport> list = new ArrayList<>();

		// 昨日日期
		String yestday = DateUtil.getDayGoXday(-1);
		List<SmsMtMessageSubmit> submits = smsMtMessageSubmitMapper.findByDate(yestday);

		// 已存在的用户ID
		Set<Integer> existsUsers = new HashSet<>();
		ConsumptionReport report = null;
		for (SmsMtMessageSubmit submit : submits) {
			report = new ConsumptionReport();
			report.setAmount(submit.getFee());
			report.setType(PlatformType.SEND_MESSAGE_SERVICE.getCode());
			report.setRecordDate(DateUtil.getDayDate(yestday));
			report.setUserId(submit.getUserId());

			existsUsers.add(submit.getUserId());
			list.add(report);
		}

		userList.removeAll(existsUsers);

		if (CollectionUtils.isNotEmpty(userList)) {
			for (Integer userId : userList) {
				report = new ConsumptionReport();
				report.setAmount(0);
				report.setType(PlatformType.SEND_MESSAGE_SERVICE.getCode());
				report.setRecordDate(DateUtil.getDayDate(yestday));
				report.setUserId(userId);

				list.add(report);
			}

		}
		report = null;
		return list;
	}

	@Override
	public SmsLastestRecordVo findLastestRecord(int userId, String mobile) {
		SmsLastestRecordVo vo = new SmsLastestRecordVo();
		vo.setMobile(mobile);
		vo.setUserId(userId);
		
		Map<String, Object> map = smsMtMessageSubmitMapper.selectByUserIdAndMobile(userId, mobile);
		if(MapUtils.isEmpty(map)) {
			vo.setDescrption("未找到相关记录");
			return vo;
		}
		
		vo.setContent(map.get("content") == null ? "" : map.get("content").toString());
		vo.setCreateTime(map.get("create_time").toString());
		vo.setMessageNode(MessageNode.SMS_COMPLETE);
		vo.setNodeTime(map.get("deliver_time") == null ? "" : map.get("create_time").toString());
		Object sendStatus = map.get("send_status");
		Object deliverStatus = map.get("deliver_status");
		
		if(sendStatus == null) {
			vo.setDescrption("短信发送未知");
			
			return vo;
		} else if(Integer.parseInt(sendStatus.toString()) == MessageSubmitStatus.FAILED.getCode()) {
			vo.setDescrption("短信发送失败");
			
			return vo;
		} else if(deliverStatus == null) {
			vo.setDescrption("待网关发送");
			
			return vo;
		} else if(Integer.parseInt(deliverStatus.toString()) == DeliverStatus.FAILED.getValue()) {
			vo.setDescrption("短信发送失败，错误码：" + map.get("status_code"));
			
			return vo;
		} 
		
		vo.setDescrption("发送成功");
		return vo;
	}

	@Override
	public int batchInsertSubmit(List<SmsMtMessageSubmit> list) {
		if (CollectionUtils.isEmpty(list))
			return 0;

		return smsMtMessageSubmitMapper.batchInsert(list);
//		SqlSession session = sqlSessionTemplate.getSqlSessionFactory().openSession(ExecutorType.BATCH, false);
//		SmsMtMessagesmsMtMessageSubmitMapper smsMtMessageSubmitMapper = session.getMapper(SmsMtMessagesmsMtMessageSubmitMapper.class);
//		int size = 0;
//		try {
//			size = smsMtMessageSubmitMapper.batchInsert(list);
//			session.commit();
//			// 清理缓存，防止溢出
//			session.clearCache();
//		} catch (Exception e) {
//			// 没有提交的数据可以回滚
//			// session.rollback();
//			logger.error("短信提交数据入库失败", e);
//		} finally {
//			session.close();
//		}
//		return size;
	}

	@Override
	public SmsMtMessageSubmit getSubmitWaitReceipt(String msgId, String mobile) {
		return smsMtMessageSubmitMapper.selectByMsgIdAndMobile(msgId, mobile);
	}

	@Override
	public SmsMtMessageSubmit getByMoMapping(Integer passageId, String msgId, String mobile, String spcode) {

		SmsMtMessageSubmit smsMtMessageSubmit = null;
		if (passageId != null && StringUtils.isNotEmpty(msgId)) {
			smsMtMessageSubmit = smsMtMessageSubmitMapper.selectByPsm(passageId, msgId, mobile);
		}
		if (smsMtMessageSubmit == null && StringUtils.isNotEmpty(msgId)) {
			smsMtMessageSubmit = smsMtMessageSubmitMapper.selectByMsgIdAndMobile(msgId, mobile);
		}
		if (smsMtMessageSubmit == null) {
			return smsMtMessageSubmitMapper.selectByMobile(mobile);
		}
		return smsMtMessageSubmit;
	}

	@Override
	public SmsMtMessageSubmit getByMsgidAndMobile(String msgId, String mobile) {
		return smsMtMessageSubmitMapper.selectByMsgIdAndMobile(msgId, mobile);
	}
	
	@Override
	public SmsMtMessageSubmit getByMsgid(String msgId) {
		return smsMtMessageSubmitMapper.selectByMsgId(msgId);
	}

	@Override
	public boolean doSmsException(List<SmsMtMessageSubmit> submits) {
		List<SmsMtMessageDeliver> delivers = new ArrayList<>();
		SmsMtMessageDeliver deliver = null;
		for (SmsMtMessageSubmit submit : submits) {
			deliver = new SmsMtMessageDeliver();
			deliver.setCmcp(submit.getCmcp());
			deliver.setMobile(submit.getMobile());
			deliver.setMsgId(submit.getMsgId());
			deliver.setStatusCode(StringUtils.isNotEmpty(submit.getPushErrorCode()) ? submit.getPushErrorCode() : submit.getRemark());
			deliver.setStatus(DeliverStatus.FAILED.getValue());
			deliver.setDeliverTime(DateUtil.getNow());
			deliver.setCreateTime(new Date());
			deliver.setRemark(submit.getRemark());
			
			// 设置推送开关
			if(submit.getNeedPush() != null && submit.getNeedPush() && StringUtils.isNotEmpty(submit.getPushUrl())) {
				smsMtPushService.setReadyMtPushConfig(submit);
			}
			
			delivers.add(deliver);
		}

		stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_SUBMIT_LIST, JSON
				.toJSONString(submits, SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));

		try {
			smsMtDeliverService.doFinishDeliver(delivers);
			return true;
		} catch (Exception e) {
			logger.warn("人工制造短信回执信息错误，错误信息：{}", e.getMessage());
			return false;
		}
	}

	@Override
	public boolean declareWaitSubmitMessageQueues() {
		List<String> passageCodes = smsPassageService.findPassageCodes();
		if (CollectionUtils.isEmpty(passageCodes)) {
			logger.error("无可用通道需要声明队列");
			return false;
		}

		try {
			for (String passageCode : passageCodes) {
				rabbitMessageQueueManager.createQueue(getSubmitMessageQueueName(passageCode), smsPassageService.isPassageBelongtoDirect(null, passageCode), 
						smsWaitSubmitListener);
			}

			return true;
		} catch (Exception e) {
			logger.error("初始化消息队列异常");
			return false;
		}
	}

	@Override
	public String getSubmitMessageQueueName(String passageCode) {
		return String.format("%s.%s", MQConstant.MQ_SMS_MT_WAIT_SUBMIT, passageCode);
	}

	@Override
	public List<SmsMtMessageSubmit> getRecordListToMonitor(Long passageId, Long startTime, Long endTime) {
		return smsMtMessageSubmitMapper.getRecordListToMonitor(passageId, startTime, endTime);
	}

	@Override
	public boolean declareNewSubmitMessageQueue(String protocol, String passageCode) {
		String mqName = getSubmitMessageQueueName(passageCode);
		try {
			rabbitMessageQueueManager.createQueue(mqName, smsPassageService.isPassageBelongtoDirect(protocol, passageCode), 
					smsWaitSubmitListener);
			logger.info("RabbitMQ添加新队列：{} 成功", mqName);
			return true;
		} catch (Exception e) {
			logger.error("声明新队列：{}失败", passageCode);
		}
		return false;
	}

	@Override
	public boolean removeSubmitMessageQueue(String passageCode) {
		String mqName = getSubmitMessageQueueName(passageCode);
		try {
			rabbitMessageQueueManager.removeQueue(mqName);
			
			logger.info("RabbitMQ移除队列：{} 成功", mqName);
			
			return true;
		} catch (Exception e) {
			logger.error("移除MQ队列：{}失败", passageCode);
		}
		return false;
	}

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if (correlationData == null)
			return;

		// if (ack) {
		// logger.error("=================待提交消息队列处理成功：{}",
		// correlationData.getId());
		// } else {
		// logger.error("=================待提交消息队列处理失败：{}，信息：{}",
		// correlationData.getId(), cause);
		// }
	}

	@Override
	public boolean sendToSubmitQueue(List<SmsMtTaskPackets> packets) {
		if (CollectionUtils.isEmpty(packets)) {
			logger.warn("子任务数据为空，无需发送队列");
			return false;
		}

		// 发送至待提交信息队列处理
		Map<Integer, String> passageCodesMap = new HashMap<>();
		String passageCode = null;
		for (SmsMtTaskPackets packet : packets) {
			try {
				passageCode = getPassageCode(passageCodesMap, packet);
				if (StringUtils.isEmpty(passageCode)) {
					logger.error("子任务通道数据为空，无法进行通道代码分队列处理，通道ID：{}", packet.getFinalPassageId());
					continue;
				}

				rabbitTemplate.convertAndSend(MQConstant.EXCHANGE_SMS, getSubmitMessageQueueName(passageCode), packet,
						new MessagePostProcessor() {

							@Override
							public Message postProcessMessage(Message message) throws AmqpException {
								message.getMessageProperties().setPriority(WordsPriority.getLevel(packet.getContent()));

								return message;
							}
						}, new CorrelationData(packet.getSid() + ""));
			} catch (Exception e) {
				logger.error("子任务发送至待提交任务失败，信息为：{}", JSON.toJSONString(packet), e);
			}
		}
		return true;
	}

	/**
	 * 
	 * TODO 根据子任务中的通道获取通道代码信息
	 * 
	 * @param passageCodesMap
	 * @param packet
	 * @return
	 */
	private String getPassageCode(Map<Integer, String> passageCodesMap, SmsMtTaskPackets packet) {
		if (StringUtils.isEmpty(packet.getPassageCode())) {
			if (passageCodesMap.containsKey(packet.getPassageId())) {
				return passageCodesMap.get(packet.getPassageId());
			}

			SmsPassage passage = smsPassageService.findById(packet.getFinalPassageId());
			if (passage == null) {
				return null;
			}

			passageCodesMap.put(passage.getId(), passage.getCode());
			return passage.getCode();
		}

		return packet.getPassageCode();
	}

	@PostConstruct
	public void setConfirmCallback() {
		// rabbitTemplate如果为单例的话，那回调就是最后设置的内容
		rabbitTemplate.setConfirmCallback(this);
	}

	@Override
	public List<Map<String, Object>> getSubmitStatReport(Long startTime,
			Long endTime) {
		
		if(startTime == null || endTime == null)
			return null;
		
		List<Map<String, Object>> list = smsMtMessageSubmitMapper.selectSubmitReport(startTime, endTime);
		if(CollectionUtils.isEmpty(list))
			return null;
		
		return list;
	}

	@Override
	public List<Map<String, Object>> getLastHourSubmitReport() {
		// 截止时间为前一个小时0分0秒
		Long endTime = DateUtil.getXHourWithMzSzMillis(-1);
		// 开始时间为前2个小时0分0秒
		Long startTime = DateUtil.getXHourWithMzSzMillis(-2);
		
		return getSubmitStatReport(startTime, endTime);
	}

	@Override
	public List<Map<String, Object>> getSubmitCmcpReport(Long startTime,
			Long endTime) {
		if(startTime == null || endTime == null)
			return null;
		
		return smsMtMessageSubmitMapper.selectCmcpReport(startTime, endTime);
	}
	
}
