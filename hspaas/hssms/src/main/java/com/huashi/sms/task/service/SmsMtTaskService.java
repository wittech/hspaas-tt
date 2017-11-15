package com.huashi.sms.task.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.bill.bill.model.SmsP2PBillModel;
import com.huashi.bill.bill.service.IBillService;
import com.huashi.common.settings.context.SettingsContext.PushConfigStatus;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.user.service.IUserSmsConfigService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.IdGenerator;
import com.huashi.common.util.MobileNumberCatagoryUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.SmsPushCode;
import com.huashi.constants.ResponseMessage;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageAccess;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtProcessFailedService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.settings.service.IForbiddenWordsService;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.huashi.sms.task.context.MQConstant;
import com.huashi.sms.task.context.MQConstant.WordsPriority;
import com.huashi.sms.task.context.TaskContext.MessageSubmitStatus;
import com.huashi.sms.task.context.TaskContext.PacketsActionActor;
import com.huashi.sms.task.context.TaskContext.PacketsActionPosition;
import com.huashi.sms.task.context.TaskContext.PacketsApproveStatus;
import com.huashi.sms.task.context.TaskContext.TaskSubmitType;
import com.huashi.sms.task.dao.SmsMtTaskMapper;
import com.huashi.sms.task.dao.SmsMtTaskPacketsMapper;
import com.huashi.sms.task.domain.SmsMtTask;
import com.huashi.sms.task.domain.SmsMtTaskPackets;
import com.huashi.sms.task.exception.QueueProcessException;
import com.huashi.sms.template.context.TemplateContext;
import com.huashi.sms.template.domain.MessageTemplate;
import com.huashi.sms.template.service.ISmsTemplateService;

/**
 * TODO 短信下行任务服务实现
 *
 * @author zhengying
 * @version V1.0
 * @date 2016年10月27日 下午12:55:56
 */
@Service
public class SmsMtTaskService implements ISmsMtTaskService, RabbitTemplate.ConfirmCallback  {

	@Reference
	private IBillService billService;
	@Autowired
	private IdGenerator idGenerator;
	@Reference
	private IUserService userService;
	@Autowired
	private SmsMtTaskMapper taskMapper;
	@Autowired
	private SmsMtTaskPacketsMapper smsMtTaskPacketsMapper;
	@Reference
	private IUserPassageService userPassageService;
	@Autowired
	private SmsMtTaskPacketsMapper taskPacketsMapper;
	@Reference
	protected IUserSmsConfigService userSmsConfigService;
	@Reference
	private IPushConfigService pushConfigService;
	@Autowired
	protected IForbiddenWordsService forbiddenWordsService;
	@Autowired
	protected ISmsMtProcessFailedService smsMtProcessFailedService;
	@Autowired
	protected ISmsMobileBlackListService mobileBlackListService;
	@Reference
	private IUserBalanceService userBalanceService;
	@Resource
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private ISmsPassageService smsPassageService;
	@Reference
	private IProvinceService provinceService;
	@Autowired
	private ISmsMtSubmitService smsMtSubmitService;
	@Autowired
	private ISmsTemplateService smsTemplateService;

	@Resource
	private RabbitTemplate rabbitTemplate;

	@PostConstruct
	public void setConfirmCallback() {
		// rabbitTemplate如果为单例的话，那回调就是最后设置的内容
		rabbitTemplate.setConfirmCallback(this);
	}

	protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public BossPaginationVo<SmsMtTask> findPage(Map<String, Object> condition) {
		changeTimestampeParamsIfExists(condition);
		
		BossPaginationVo<SmsMtTask> page = new BossPaginationVo<SmsMtTask>();
		page.setCurrentPage(Integer.parseInt(condition.getOrDefault("currentPage", 1).toString()));
		int total = taskMapper.findCount(condition);
		if (total <= 0) {
			return page;
		}
		page.setTotalCount(total);
		condition.put("start", page.getStartPosition());
		condition.put("end", page.getPageSize());
		
		List<SmsMtTask> dataList = taskMapper.findList(condition);
		for (SmsMtTask record : dataList) {
			record.setUserModel(userService.getByUserId(record.getUserId()));
			
			if(StringUtils.isBlank(record.getForbiddenWords()))
				continue;
			
			record.setForbiddenWordLabels(forbiddenWordsService.getLabelByWords(record.getForbiddenWords()));
		}
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
     * 获取待处理的短信任务条数
     * @return
     */
	@Override
	public Integer getWaitSmsTaskCount() {
		return taskMapper.selectWaitDealTaskCount();
	}

	@Override
	public List<SmsMtTaskPackets> findChildTaskBySid(long sid) {
		List<SmsMtTaskPackets> childList = taskPacketsMapper
				.findChildBySid(sid);
		
		Province province = null;
		Map<Integer, String> provinceMap = new HashMap<>();
		for (SmsMtTaskPackets task : childList) {
			// if (task.getUserId() != null) {
			// UserModel userModel = userService.getByUserId(task.getUserId());
			// task.setUserModel(userModel);
			// }

			if(provinceMap.containsKey(task.getProvinceCode())) {
				task.setProvinceName(provinceMap.get(task.getProvinceCode()));
			} else {
				// 根据省份代码查询省份名称
				province = provinceService.get(task.getProvinceCode());
				task.setProvinceName(province == null ? "未知" : province.getName());
				provinceMap.put(task.getProvinceCode(), task.getProvinceName());
			}
		}
		province = null;
		provinceMap = null;

		return childList;
	}

	@Override
	public boolean updateSmsContent(long sid, String content) {
		try {
			taskMapper.updateContent(sid, content);
			taskPacketsMapper.updateContent(sid, content);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			TransactionAspectSupport.currentTransactionStatus()
					.setRollbackOnly();
		}
		return false;
	}

	@Override
	public boolean batchUpdateSmsContent(String sidArrays, String content) {
		try {
			String[] array = sidArrays.split(",");
			for (String sid : array) {
				taskMapper.updateContent(Long.valueOf(sid), content);
				taskPacketsMapper.updateContent(Long.valueOf(sid), content);
			}
			return true;
		} catch (Exception e) {
			logger.error("批量修改内容失败", e);
			TransactionAspectSupport.currentTransactionStatus()
					.setRollbackOnly();
		}
		return false;
	}

	@Override
	public boolean save(SmsMtTask task) {
		task.setCreateTime(new Date());

		return taskMapper.insertSelective(task) > 0;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = RuntimeException.class)
	public boolean savePackets(SmsMtTaskPackets packetes) {
		try {

			packetes.setCreateTime(new Date());
			return taskPacketsMapper.insertSelective(packetes) > 0;

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * TODO 验证通道是否可用，如不可用做报警异常
	 *
	 * @param access
	 * @return
	 */
	public boolean isSmsPassageAccessAvaiable(SmsPassageAccess access) {
		if (access == null) {
			// 丢到错误队列还是直接抛异常？？

		}
		return true;
	}

	@Override
	public SmsMtTask getTaskBySid(Long sid) {
		return taskMapper.selectBySid(sid);
	}

	@Override
	public boolean update(SmsMtTask task) {
		return taskMapper.updateByPrimaryKey(task) > 0;
	}

	@Override
	public boolean updateStatus(long id, int status) {
		try {
			return taskMapper.updateApproveStatus(id, status) > 0;
		} catch (Exception e) {
			logger.error("根据任务ID更新审批状态失败", e);
		}
		return false;
	}

	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public boolean changeTaskPacketsPassage(long taskPacketsId, int passageId) {
		try {
			SmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(taskPacketsId);
			if (packets == null) {
				logger.error("子任务信息未找到，请检查，子任务ID: {}", taskPacketsId);
				return false;
			}

			// 根据通道ID查询 通道参数（针对发送类型）
			SmsPassageParameter passageParameter = getPassageParameter(passageId);
			if(passageParameter == null) {
				logger.error("通道ID：{} 未找到相关参数信息", passageId);
				return false;
			}

			packets.setFinalPassageId(passageId);
			packets.setPassageCode(passageParameter.getPassageCode());
			packets.setPassageProtocol(passageParameter.getProtocol());
			packets.setPassageParameter(passageParameter.getParams());
			packets.setPosition(passageParameter.getPosition());
			packets.setSuccessCode(passageParameter.getSuccessCode());
			packets.setPassageUrl(passageParameter.getUrl());
			packets.setResultFormat(passageParameter.getResultFormat());

			// 如果不仅仅包含 通道问题，切换通道后需要人工审核后才重入队列
			if (!isOnlyContainPassageError(packets)) {
				packets.setStatus(PacketsApproveStatus.WAITING.getCode());
				return taskPacketsMapper.updateByPrimaryKeySelective(packets) > 0;
			}

			packets.setStatus(PacketsApproveStatus.HANDLING_COMPLETE.getCode());
			boolean isOk = taskPacketsMapper.updateByPrimaryKeySelective(packets) > 0;
			if (isOk) {
				
				
				// 切换通道后直接 重新放置队列中
				repeatSendToQueue(packets, null);
			}
			return isOk;
			
		} catch (Exception e) {
			logger.error("任务切换通道失败", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	   * TODO 根据通道ID获取通道相关参数信息
	   * @param passageId
	   * @return
	 */
	private SmsPassageParameter getPassageParameter(int passageId) {
		SmsPassage passage = smsPassageService.findById(passageId);
		if(passage == null)
			return null;
			
		for (SmsPassageParameter parameter : passage.getParameterList()) {
			if (parameter.getCallType().intValue() == PassageCallType.DATA_SEND.getCode()) {
				parameter.setPassageCode(passage.getCode());
				return parameter;
			}
		}
		return null;
	}

	/**
	 * 
	 * TODO 任务是否仅仅包含 '通道错误'
	 * 
	 * @param packets
	 * @return
	 */
	private boolean isOnlyContainPassageError(SmsMtTaskPackets packets) {
		if (StringUtils.isEmpty(packets.getForceActions()))
			return false;

		if (!packets.getForceActions().contains(
				String.valueOf(PacketsActionActor.BROKEN.getActor())))
			return false;

		char[] actions = packets.getActions();
		// 将通道不可用操作符 暂时赋值为 通过
		actions[PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition()] = PacketsActionActor.AVAIABLE
				.getActor();

		return !String.valueOf(actions).contains(String.valueOf(PacketsActionActor.BROKEN.getActor()));

	}

	@Override
	public boolean updateForceActions(long taskPacketsId, String actions) {
		SmsMtTaskPackets packets = new SmsMtTaskPackets();
		packets.setId(taskPacketsId);
		packets.setForceActions(actions);
		try {
			return taskPacketsMapper.updateByPrimaryKeySelective(packets) > 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public boolean updateTaskPacketsStatus(long taskPacketsId, int status) {
		try {
			int result = taskPacketsMapper.updateStatusById(taskPacketsId, status);
			if(result <= 0)
				return false;
			
			SmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(taskPacketsId);
			
			// 查询是否所有的子任务状态均不为“待审批”，否则修改主任务状态为 审批通过
			int count = taskPacketsMapper.selectWaitingCount(packets.getSid());
			if(count == 0) 
				taskMapper.updateApproveStatusBySid(packets.getSid(), status);
			
			if (status == PacketsApproveStatus.HANDLING_COMPLETE.getCode()) {
				return repeatSendToQueue(packets, null);
			}
		} catch (Exception e) {
			logger.error("更改子任务状态失败", e);
			throw new RuntimeException(e);
		}
		
		return false;
	}

	/**
	 * 
	   * TODO 重入待提交队列(每个用户ID单独)
	   * 
	   * @param source
	   * @param task
	   * @return
	 */
	private boolean repeatSendToQueue(SmsMtTaskPackets source, SmsMtTask task) {
		task = task == null ? taskMapper.selectBySid(source.getSid()) : task;
		if(task == null) {
			logger.error("主任务未查到相关数据,SID：{}", source.getSid());
			return false;
		}
		source.setCallback(task.getCallback());
		source.setAttach(task.getAttach());
		source.setUserId(task.getUserId());
		source.setExtNumber(task.getExtNumber());
		source.setFee(task.getFee());
		
		List<SmsMtTaskPackets> list = new ArrayList<>();
		list.add(source);
		
		// 发送到待提交队列
		return smsMtSubmitService.sendToSubmitQueue(list);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = QueueProcessException.class)
	public long doSubmitTask(SmsMtTask task) throws QueueProcessException {
		try {
			// 更新用户余额
			boolean isSuccess = userBalanceService.deductBalance(task.getUserId(), -task.getTotalFee(),
					PlatformType.SEND_MESSAGE_SERVICE.getCode(), null);

			if (!isSuccess) {
				logger.error("用户ID:{} 扣除短信余额：{} 失败", task.getUserId(), task.getTotalFee());
				throw new QueueProcessException("发送短信扣除短信余额失败");
			}

			task.setSid(idGenerator.generate());
			task.setCreateTime(new Date());
			task.setCreateUnixtime(task.getCreateTime().getTime());

			// 插入TASK任务（异步）

			// 判断队列的优先级别
			int priority = WordsPriority.getLevel(task.getContent());
			// if(WordsPriority.L10.getLevel() == priority) {
			// stringRedisTemplate.opsForList().rightPush(SmsConstant.RED_TASK_PERSISTENCE_HIGH,
			// JSON.toJSONString(task));
			// } else {
			// if(WordsPriority.L1.getLevel() == priority)
			// stringRedisTemplate.opsForList().rightPush(SmsConstant.RED_TASK_PERSISTENCE_LOW,
			// JSON.toJSONString(task));
			// if(WordsPriority.L1.getLevel() == priority)
			// stringRedisTemplate.opsForList().leftPush(SmsConstant.RED_TASK_PERSISTENCE_LOW,
			// JSON.toJSONString(task));
			// }

			String queueName = MQConstant.MQ_SMS_MT_WAIT_PROCESS;
			if(TaskSubmitType.POINT_TO_POINT.getCode() == task.getSubmitType() 
					|| TaskSubmitType.TEMPLATE_POINT_TO_POINT.getCode() == task.getSubmitType()) {
				queueName = MQConstant.MQ_SMS_MT_P2P_WAIT_PROCESS;
			}
			
			rabbitTemplate.convertAndSend(queueName, task, new MessagePostProcessor() {

						@Override
						public Message postProcessMessage(Message message)
								throws AmqpException {
							message.getMessageProperties().setPriority(priority);
							return message;
						}
					}, new CorrelationData(task.getSid().toString()));

			return task.getSid();
		} catch (Exception e) {
			logger.error("发送短信队列失败", e);
			throw new QueueProcessException("发送短信队列失败");
		}
	}

	@Override
	public SmsMtTaskPackets getTaskPacketsById(long id) {
		return taskPacketsMapper.selectByPrimaryKey(id);
	}

	@Override
	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
		if(correlationData == null)
			return;
		
		// add by zhengying 20170313 理论上此处应该做基础数据校准，为了校验是否用户所有的请求都已经发送到队列，防止用户发送请求数据丢失
//		if (ack) {
//			logger.info("待处理任务消息队列处理成功，SID：{}", correlationData.getId());
//		} else {
//			logger.error("待处理任务消息队列处理失败，SID：{}，信息：{}", correlationData.getId(), cause);
//		}
	}

	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public void batchSave(List<SmsMtTask> tasks, List<SmsMtTaskPackets> taskPackets) throws RuntimeException {
		try {
			if (CollectionUtils.isEmpty(tasks))
				return;

			int result = 0;
			if (CollectionUtils.isNotEmpty(tasks)) {
				result = taskMapper.batchInsert(tasks);
				if (CollectionUtils.isNotEmpty(taskPackets) && result > 0)
					result = taskPacketsMapper.batchInsert(taskPackets);
			}

			if (result == 0)
				throw new RuntimeException("数据执行失败");

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean updateMainTaskByForcePass(long sid) {
		try {
			taskPacketsMapper.updateStatusBySid(sid, PacketsApproveStatus.HANDLING_COMPLETE.getCode());
			
			// 发送到待提交队列
			smsMtSubmitService.sendToSubmitQueue(taskPacketsMapper.findChildBySid(sid));
			
			return true;
		} catch (Exception e) {
			logger.error("主任务强制通过异常！", e);
		}
		return false;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = RuntimeException.class)
	public boolean doRejectInTask(String taskIds) {
		if(StringUtils.isEmpty(taskIds)) {
			logger.warn("主任务ID集合数据为空 ： {}", taskIds);
			return false;
		}
		
		String[] taskIdsArray = taskIds.split(",");
		if(taskIdsArray == null || taskIdsArray.length == 0) {
			logger.warn("主任务ID集合数据为空 ： {}", taskIds);
			return false;
		}
		
		try {
			SmsMtTask task = null;
			int result = 0;
			for(String taskId : taskIdsArray) {
				// 根据主任务ID查询任务信息
				task = taskMapper.selectByPrimaryKey(Long.parseLong(taskId));
	
				// 更新主任务状态为“驳回”
				result = taskMapper.updateApproveStatus(Long.parseLong(taskId), PacketsApproveStatus.REJECT.getCode());
	
				List<SmsMtTaskPackets> packets = taskPacketsMapper.findChildBySid(task.getSid());
				if (CollectionUtils.isEmpty(packets))
					continue;
	
				for (SmsMtTaskPackets pt : packets) {
					if (pt.getStatus() == PacketsApproveStatus.REJECT.getCode())
						continue;
	
					pt.setUserId(task.getUserId());
					doRejectToQueue(pt);
				}
	
				if (result > 0) {
					result = taskPacketsMapper.updateStatusBySid(task.getSid(), PacketsApproveStatus.REJECT.getCode());
				}
			}

			return result > 0;
		} catch (Exception e) {
			logger.error("主任务驳回失败", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = RuntimeException.class)
	public boolean doRejectInTaskPackets(long packetsId) {
		try {
			SmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(packetsId);
			if (packets == null)
				return false;

			SmsMtTask task = taskMapper.selectBySid(packets.getSid());
			if (task == null)
				return false;

			packets.setUserId(task.getUserId());
			packets.setAttach(task.getAttach());
			packets.setFee(task.getFee());
			packets.setCallback(task.getCallback());

			doRejectToQueue(packets);

			int result = taskPacketsMapper.updateStatusById(packets.getId(), PacketsApproveStatus.REJECT.getCode());
			
			// 查询是否所有的子任务状态均不为“待审批”，否则修改主任务状态为 审批通过
			int count = taskPacketsMapper.selectWaitingCount(packets.getSid());
			if(count == 0) 
				updateStatus(packets.getSid(), PacketsApproveStatus.REJECT.getCode());
				
			return result > 0;
		} catch (Exception e) {
			logger.error("子任务驳回失败", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * TODO 驳回入库队列
	 * 
	 * @param packets
	 */
	private void doRejectToQueue(SmsMtTaskPackets packets) {
		String[] mobiles = packets.getMobile().split(
				MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR);
		if (mobiles == null || mobiles.length == 0)
			return;

		for (String m : mobiles) {
			SmsMtMessageSubmit submit = new SmsMtMessageSubmit();
			submit.setUserId(packets.getUserId());
			submit.setSid(packets.getSid());
			submit.setMobile(m);
			submit.setCmcp(CMCP.local(m).getCode());
			submit.setContent(packets.getContent());
			submit.setFee(packets.getFee());
			submit.setAttach(packets.getAttach());
			submit.setPassageId(PassageContext.EXCEPTION_PASSAGE_ID);
			submit.setCreateTime(new Date());
			submit.setCreateUnixtime(submit.getCreateTime().getTime());
			submit.setStatus(MessageSubmitStatus.SUCCESS.getCode());
			submit.setRemark(SmsPushCode.SMS_TASK_REJECT.getCode());
			submit.setPushErrorCode(SmsPushCode.SMS_TASK_REJECT.getCode());
			submit.setMsgId(packets.getSid().toString());
			submit.setCallback(packets.getCallback());
			submit.setProvinceCode(Province.PROVINCE_CODE_ALLOVER_COUNTRY);
			
			PushConfig pushConfig = pushConfigService.getPushUrl(packets.getUserId(),
					PlatformType.SEND_MESSAGE_SERVICE.getCode(), packets.getCallback());
			
			// 推送信息为固定地址或者每次传递地址才需要推送
			if (pushConfig != null && PushConfigStatus.NO.getCode() != pushConfig.getStatus()) {
				submit.setPushUrl(pushConfig.getUrl());
				submit.setNeedPush(true);
			}

			rabbitTemplate.convertAndSend(MQConstant.EXCHANGE_SMS, MQConstant.MQ_SMS_MT_PACKETS_EXCEPTION, submit);
		}
	}
	
	/**
	 * 
	   * TODO 根据提交类型获取队列名称（批量/点对点/模板点对点）
	   * @param task
	   * @return
	 */
	private String getQueueNameBySubmitType(SmsMtTask task) {
		if(TaskSubmitType.BATCH_MESSAGE.getCode() == task.getSubmitType())
			return MQConstant.MQ_SMS_MT_WAIT_PROCESS;
		
		// 如果任务是点对点或者模板点对点处理短信内容
		SmsP2PBillModel model = null;
		if(StringUtils.isEmpty(task.getContent())) {
			throw new RuntimeException("点对点短信原报文数据为空，无法执行");
		}
		
		// 点对点报文内容解析
		List<JSONObject> list = JSON.parseObject(task.getContent(), new TypeReference<List<JSONObject>>(){});
		if(CollectionUtils.isEmpty(list)) {
			throw new RuntimeException("点对点解析计费及短信内容失败");
		}
		
		if(TaskSubmitType.POINT_TO_POINT.getCode() == task.getSubmitType()) {
			model = billService.getSmsFeeInP2P(task.getUserId(), JSON.parseObject(task.getContent(), new TypeReference<List<JSONObject>>(){}));
		}else if(TaskSubmitType.TEMPLATE_POINT_TO_POINT.getCode() == task.getSubmitType()) {
			model = billService.getSmsFeeInP2P(task.getUserId(), JSON.parseObject(task.getContent(), new TypeReference<List<JSONObject>>(){}));
		}
		
		if(model == null || CollectionUtils.isEmpty(model.getP2pBodies())) {
			throw new RuntimeException("点对点解析计费及短信内容失败");
		}
		
		task.setP2pBody(task.getContent());
		task.setP2pBodies(model.getP2pBodies());
		
		return MQConstant.MQ_SMS_MT_P2P_WAIT_PROCESS;
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = RuntimeException.class)
	public boolean doRePackets(long id) {
		try {
			// 根据主任务ID查询任务信息
			SmsMtTask task = taskMapper.selectByPrimaryKey(id);
			// 根据ID删除主任务信息和 子任务信息
			int result = taskPacketsMapper.deleteBySid(task.getSid());
			if (result <= 0) {
				throw new RuntimeException("重新分包删除子任务失败");
			}
			result = taskMapper.deleteByPrimaryKey(task.getId());
			// 蒋主任务发送 分包队列，重新分包
			if (result > 0) {
				rabbitTemplate.convertAndSend(getQueueNameBySubmitType(task), task, new MessagePostProcessor() {
						@Override
						public Message postProcessMessage(Message message)
								throws AmqpException {
							message.getMessageProperties().setPriority(WordsPriority.getLevel(task.getContent()));
							return message;
						}
					}, new CorrelationData(task.getSid().toString()));
			}

			return result > 0;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean batchDoRePackets(String mainTaskIds) {
		try {
			String[] idArray = mainTaskIds.split(",");
			for (String id : idArray) {
				doRePackets(Long.valueOf(id));
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public SmsMtTask findById(long id) {
		return taskMapper.selectByPrimaryKey(id);
	}

	@Override
	public PaginationVo<SmsMtTask> findAll(int userId, String sid, String content, Long start, Long end,
			String currentPage) {
		int _currentPage = PaginationVo.parse(currentPage);
		Map<String, Object> params = new HashMap<>();
		params.put("userId", userId);
		if(!StringUtils.isEmpty(sid)){
			params.put("sid", sid);
		}
		if(start !=null && start>0){
			params.put("startDate", start);
		}
		if(end !=null && end>0){
			params.put("endDate", end);
		}
		if(!StringUtils.isEmpty(content)){
			params.put("content", content);
		}
		int totalRecord = taskMapper.findCount(params);
		if (totalRecord == 0)
			return null;

		params.put("start", PaginationVo.getStartPage(_currentPage));
		params.put("end", PaginationVo.DEFAULT_RECORD_PER_PAGE);
		List<SmsMtTask> list = taskMapper.findList(params);
		if (list == null || list.isEmpty())
			return null;

		return new PaginationVo<SmsMtTask>(list, _currentPage, totalRecord);
	}

	@Override
	public boolean isTaskChildrenHasPassageError(Long sid) {
		return smsMtTaskPacketsMapper.selectPassageErrorCount(sid) > 0;
	}

	@Override
	@Transactional
	public ResponseMessage doForcePass(String taskIds) {
		Long sid = null;
		try {
			if(StringUtils.isEmpty(taskIds))
				return new ResponseMessage(ResponseMessage.ERROR_CODE, "操作数据为空", false);
			
            String[] idArray = taskIds.split(",");
            if(idArray == null || idArray.length == 0)
            	return new ResponseMessage(ResponseMessage.ERROR_CODE, "操作数据为空", false);
            
            List<SmsMtTask> tasks = taskMapper.selectTaskByIds(Arrays.asList(idArray));
            if(CollectionUtils.isEmpty(tasks))
            	return new ResponseMessage(ResponseMessage.ERROR_CODE, "任务数据为空", false);
            
            return doTaskBatchApproved(tasks);
        }catch (Exception e) {
        	logger.error("批量审核错误", e);
        	TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        	throw new RuntimeException(String.format("SID:%d 审批处理失败", sid));
        }
	}
	
	/**
	 * 
	   * TODO 任务批量通过审批
	   * 
	   * @param tasks
	   * @return
	 */
	private ResponseMessage doTaskBatchApproved(List<SmsMtTask> tasks) {
		for(SmsMtTask task : tasks){
            if(task == null)
            	continue;
            
            boolean isHasError = isTaskChildrenHasPassageError(task.getSid());
            if(isHasError)
            	return new ResponseMessage(ResponseMessage.ERROR_CODE, String.format("SID:%d 包含通道不可用数据", task.getSid()), false);
            
            List<SmsMtTaskPackets> packetsList = findChildTaskBySid(task.getSid());
            int result = 0;
            boolean isOk = false;
            for(SmsMtTaskPackets packets : packetsList) {
            	if(packets.getStatus() == PacketsApproveStatus.AUTO_COMPLETE.getCode()
            			|| packets.getStatus() == PacketsApproveStatus.HANDLING_COMPLETE.getCode())
            		continue;
            	
            	// 根据子任务ID更新状态为“手动完成”
            	result = taskPacketsMapper.updateStatusById(packets.getId(), PacketsApproveStatus.HANDLING_COMPLETE.getCode());
            	if(result == 0) {
            		return new ResponseMessage(ResponseMessage.ERROR_CODE, String.format("SID:%d 更新子任务状态失败", task.getSid()), false);
            	}
            		
            	// 重新发送到待提交包中时需要更新状态
            	packets.setStatus(PacketsApproveStatus.HANDLING_COMPLETE.getCode());
            	
            	// 根据通道ID查询通道信息
            	SmsPassage passage = smsPassageService.findById(packets.getFinalPassageId());
            	if(passage == null)
            		return new ResponseMessage(ResponseMessage.ERROR_CODE, String.format("SID:%d 更新子任务状态失败, 通道: %d 数据为空", 
            				task.getSid(), packets.getId()), false);
            	
            	packets.setPassageCode(passage.getCode());
            	packets.setPassageSpeed(passage.getPacketsSize());
            	packets.setPassageSignMode(passage.getSignMode());
            	
            	if(packets.getMessageTemplateId() != null && packets.getMessageTemplateId() != TemplateContext.SUPER_TEMPLATE_ID) {
            		// 根据模板ID查询模板
            		MessageTemplate template = smsTemplateService.get(packets.getMessageTemplateId().longValue());
            		if(template == null) {
            			return new ResponseMessage(ResponseMessage.ERROR_CODE, String.format("SID:%d 更新子任务状态失败, 模板: %d 数据为空", 
                				task.getSid(), packets.getMessageTemplateId()), false);
            		}
            		
            		packets.setTemplateExtNumber(template.getExtNumber());
            	}
            	
            	isOk = repeatSendToQueue(packets, task);
            	if(!isOk) {
            		throw new RuntimeException(String.format("SID:%d 审批处理失败", task.getSid()));
            	}
            }
            
            // 更新主任务状态“手动通过”
            updateStatus(task.getId(), PacketsApproveStatus.HANDLING_COMPLETE.getCode());
        }
		
		return new ResponseMessage("操作成功");
	}

	@Override
	public int doPassWithSameContent(String content, boolean isLikePattern) {
		if(StringUtils.isEmpty(content))
			return 0;
		
		List<SmsMtTask> list = taskMapper.selectWaitDealTaskList();
		if(CollectionUtils.isEmpty(list))
			return 0;
		
		List<SmsMtTask> tasks = new ArrayList<>();
		for(SmsMtTask task : list) {
			if(!isContentMatched(task.getFinalContent(), content, isLikePattern))
				continue;
			
			tasks.add(task);
		}
		
		if(CollectionUtils.isEmpty(tasks))
			return 0;
		
		try {
			ResponseMessage response = doTaskBatchApproved(tasks);
			if(!response.getOk())
				return 0;
			
		} catch (Exception e) {
			logger.error("批量审核错误", e);
			return -1;
		}
		
		return tasks.size();
	}
	
	/**
	 * 
	   * TODO 短信内容是否匹配
	   * 
	   * @param sourceContent
	   * 		原短信内容（数据库）
	   * @param targetContent
	   * 		页面传递的短信内容
	   * @param isLikePattern
	   * 		是否为模糊匹配模式
	   * @return
	 */
	private static boolean isContentMatched(String sourceContent, 
			String targetContent, boolean isLikePattern) {
		
		if(StringUtils.isEmpty(sourceContent))
			return false;
		
		return isLikePattern ? sourceContent.contains(targetContent) : sourceContent.equals(targetContent);
		
		
	}

	@Override
	@Transactional(rollbackFor = RuntimeException.class)
	public boolean changeTaskPacketsPassage(String taskIds, int expectPassageId) {
		if(StringUtils.isEmpty(taskIds) || expectPassageId == 0) {
			logger.warn("主任务ID集合数据为空 ： {}，或切换通道ID未0 ： {}", taskIds, expectPassageId);
			return false;
		}
		
		String[] taskIdsArray = taskIds.split(",");
		if(taskIdsArray == null || taskIdsArray.length == 0) {
			logger.warn("主任务ID集合数据为空 ： {}", taskIds);
			return false;
		}
		
		try {
			SmsPassageParameter passageParameter = getPassageParameter(expectPassageId);
			if(passageParameter == null) {
				logger.error("切换通道ID：{} 未找到相关参数信息", expectPassageId);
				return false;
			}
			
			boolean isOk = false;
			// 迭代主任务数据
			for (String taskId : taskIdsArray) {
				if(StringUtils.isEmpty(taskId))
					continue;
				
				SmsMtTask task = findById(Long.valueOf(taskId));
				if(task == null)
					continue;
				
				List<SmsMtTaskPackets> packetsList = findChildTaskBySid(task.getSid());
				for (SmsMtTaskPackets packets : packetsList) {
					if (!resetTaskPassage(task, packets, passageParameter)) {
						logger.error("子任务：{} 切换通道：{} 失败", packets.getId(), expectPassageId);
						throw new RuntimeException("切换通道失败");
					}
				}
				
				// 切换通道后直接 重新放置队列中
				isOk = smsMtSubmitService.sendToSubmitQueue(packetsList);
			}
			
			return isOk;
		} catch (Exception e) {
			logger.error("任务切换通道失败", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	   * TODO 重新设置通道参数相关信息，状态等信息
	   * 		
	   * @param task
	   * @param packets
	   * @param passageParameter
	   * @return
	 */
	private boolean resetTaskPassage(SmsMtTask task, SmsMtTaskPackets packets, SmsPassageParameter passageParameter) {
		try {
			packets.setFinalPassageId(passageParameter.getPassageId());
			packets.setPassageCode(passageParameter.getPassageCode());
			packets.setPassageProtocol(passageParameter.getProtocol());
			packets.setPassageParameter(passageParameter.getParams());
			packets.setPosition(passageParameter.getPosition());
			packets.setSuccessCode(passageParameter.getSuccessCode());
			packets.setPassageUrl(passageParameter.getUrl());
			packets.setResultFormat(passageParameter.getResultFormat());
			
			// 设置用户短信传递的参数信息（临时信息，非持久化信息，用于队列数据传递）
			packets.setCallback(task.getCallback());
			packets.setAttach(task.getAttach());
			packets.setUserId(task.getUserId());
			packets.setExtNumber(task.getExtNumber());
			packets.setFee(task.getFee());
			

			// 如果不仅仅包含 通道问题，切换通道后需要人工审核后才重入队列
			if (!isOnlyContainPassageError(packets)) {
				packets.setStatus(PacketsApproveStatus.WAITING.getCode());
			} else {
				packets.setStatus(PacketsApproveStatus.HANDLING_COMPLETE.getCode());
			}
			
			return taskPacketsMapper.updateByPrimaryKeySelective(packets) > 0;
		} catch (Exception e) {
			logger.error("任务切换通道失败", e);
			return false;
		}
	}
	
}
