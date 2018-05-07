package com.huashi.sms.config.rabbit.listener;

import org.springframework.amqp.core.Message;
import org.springframework.stereotype.Component;

import com.huashi.sms.config.rabbit.AbstartRabbitListener;
import com.rabbitmq.client.Channel;

/**
 * 
  * TODO 点对点短信待消息分包处理
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年4月4日 下午2:38:55
 */
@Component
public class SmsP2PWaitPacketsListener extends AbstartRabbitListener{

	@Override
	public void onMessage(Message message, Channel channel) throws Exception {
	}

	//	, RabbitTemplate.ConfirmCallback

//	// 点对点短信固定内容
//	private static final String P2P_SMS_CONTENT = "点对点短信内容";
//
//	/**
//	 *
//	 * TODO 发送至待提交队列
//	 *
//	 * @param mobileCatagory
//	 */
//	private void doPassagePackets(SmsMtTask task, MobileCatagory mobileCatagory, Long messageTemplateId,
//			SmsRoutePassage routePassage) {
//
//		List<SmsMtTaskPackets> packets = doGeneratePassagePackets(task, mobileCatagory, messageTemplateId,
//				routePassage);
//
//		// 异步发送主任务
//		try {
//			if(CollectionUtils.isEmpty(task.getPackets())) {
//				task.setPackets(packets);
//			} else {
//				task.getPackets().addAll(packets);
//			}
//
//		} catch (Exception e) {
//			logger.warn("发送数据至任务持久队列失败", e);
//		}
//	}
//
//	/**
//	 *
//	 * TODO 正常任务执行
//	 *
//	 * @param task
//	 * @param mobileCatagory
//	 * @param messageTemplateId
//	 * @param errorMessage
//	 * @return
//	 */
//	private void doPrepareTask(SmsMtTask task, List<String> errorMobiles, Integer returnFee,
//			Long messageTemplateId) {
//
//		task.setMobile(getTotalMobiles(task.getP2pBodies()));
//
//		// 点对点主任务操作符默认插入初始状态(主任务报备短信不能在主记录操作，需要在子任务进行)
//		task.setForceActions("000");
//
//		// 如果错误消息为空，则认为处理状态为 正常
//		task.setProcessStatus(StringUtils.isEmpty(task.getRemark()) ? PacketsProcessStatus.PROCESS_COMPLETE.getCode()
//				: PacketsProcessStatus.PROCESS_EXCEPTION.getCode());
//
//		task.setMessageTemplateId(messageTemplateId);
//		task.setForceActions(task.getForceActionsReport().toString());
//
//		// 如果正在分包或者分包异常，则审核状态为待审核
//		task.setApproveStatus(PacketsProcessStatus.DOING.getCode() == task.getProcessStatus() || PacketsProcessStatus.PROCESS_EXCEPTION.getCode() == task.getProcessStatus()
//				? PacketsApproveStatus.WAITING.getCode() : PacketsApproveStatus.AUTO_COMPLETE.getCode());
//
//		if(CollectionUtils.isNotEmpty(errorMobiles))
//			task.setErrorMobiles(StringUtils.join(errorMobiles, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
//
//		task.setProcessTime(new Date());
//
//		// 如果返回条数不为空，则需要设置返还的条数
//		if(returnFee != null && returnFee != 0) {
//			task.setReturnFee(returnFee);
//		}
//
//		// 发送异步队列 暂时改成本地数据
//		stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_DB_MESSAGE_TASK_LIST,
//				JSON.toJSONString(task));
//
//		if (PacketsProcessStatus.PROCESS_COMPLETE.getCode() == task.getProcessStatus()
//				&& PacketsApproveStatus.WAITING.getCode() != task.getApproveStatus()) {
//
//			// 发送至待提交信息队列
//			smtMtSubmitService.sendToSubmitQueue(task.getPackets());
//
//		}
//
//		// 如果存在错号或者重复号码需要将 之前的计费返还到客户余额
//		if(returnFee != null && returnFee != 0) {
//			doReturnFeeWhenHasErrorMobile(task);
//		}
//
//	}
//
//	/**
//	 *
//	   * TODO 当任务中包含错号/重号 返还相应余额给用户
//	   * @param task
//	   * @param mobileCatagory
//	 */
//	private void doReturnFeeWhenHasErrorMobile(SmsMtTask task) {
//		if(task.getReturnFee() != null && task.getReturnFee() != 0) {
//			logger.info("用户ID：{} 发送点对点短信 ，返还条数{}", task.getUserId(), task.getReturnFee());
//			try {
//				userBalanceService.updateBalance(task.getUserId(), task.getReturnFee(), PlatformType.SEND_MESSAGE_SERVICE.getCode(),
//						PaySource.USER_ACCOUNT_EXCHANGE, PayType.SYSTEM, 0d, 0d, "错号或者重号返还", false);
//			} catch (Exception e) {
//				logger.error("返还用户ID：{}，总短信条数：{} 失败", task.getUserId(), task.getReturnFee());
//			}
//		}
//	}
//
//	/**
//	 *
//	   * TODO 手机号码是否是黑名单
//	   * @param task
//	   * @return
//	 */
//	private boolean isBlacklistMobile(SmsMtTask task) {
//		if(StringUtils.isNotEmpty(task.getBlackMobiles()) && task.getBlackMobiles().contains(task.getMobile())) {
//			logger.info("手机号码：{}为上次已处理黑名单手机号码，此次忽略跳过", task.getMobile());
//			return true;
//		}
//
//		// 黑名单手机号码
//		boolean isBlacklist = mobileBlackListService.isMobileBelongtoBlacklist(task.getMobile());
//
//		if (isBlacklist) {
//			// 移除需要执行的手机号码
//			doMobileBelongtoBlacklist(task, Arrays.asList(task.getMobile().split(MobileCatagory.MOBILE_SPLIT_CHARCATOR)));
//			logger.info("手机黑名单{}", task.getMobile());
//
//			task.setBlackMobiles(StringUtils.isEmpty(task.getBlackMobiles()) ? task.getMobile() :
//				task.getBlackMobiles() + MobileCatagory.MOBILE_SPLIT_CHARCATOR + task.getMobile());
//			return true;
//		}
//
//		return false;
//	}
//
//	/**
//	 *
//	 * TODO 手机号码处理逻辑，黑名单判断/无效手机号码过滤/运营商分流
//	 *
//	 * @return
//	 */
//	private MobileCatagory doMobileNumberProcess(SmsMtTask task) {
//		// 号码分流
//		MobileCatagory mobileNumberResponse = mobileLocalService.doCatagory(task.getMobile());
//		if (mobileNumberResponse == null || !mobileNumberResponse.isSuccess()) {
//			logger.error("手机号码分流失败：{}", JSON.toJSONString(task));
//			return null;
//		}
//
//		return mobileNumberResponse;
//	}
//
//	/**
//	 *
//	   * TODO 拼接本次所有点对点的短信手机号码
//	   *
//	   * @param p2pBodies
//	   * @return
//	 */
//	private String getTotalMobiles(List<JSONObject> p2pBodies) {
//		if(CollectionUtils.isEmpty(p2pBodies))
//			return "N/A";
//
//		StringBuilder mobile  = new StringBuilder();
//		for(JSONObject body : p2pBodies) {
//			mobile.append(body.getString("mobile")).append(",");
//		}
//		return mobile.substring(0, mobile.length() - 1);
//	}
//
//	@Override
//	@RabbitListener(queues = MQConstant.MQ_SMS_MT_P2P_WAIT_PROCESS)
//	public void onMessage(Message message, Channel channel) throws Exception {
//		try {
//			SmsMtTask model = (SmsMtTask) messageConverter.fromMessage(message);
//			model.setOriginMobile(model.getMobile());
//
//			// 点对点原内容
//			String p2pContent = model.getContent();
//
//			// 用户短信配置中心数据
//			UserSmsConfig smsConfig = validateAndParseConfig(model);
//
//			Set<String> errorMessages = new HashSet<>();
//			MessageTemplate messageTemplate = null;
//			List<String> errorMobiles = new ArrayList<>();
//			int returnFee = 0;
//			int totalFee = 0;
//			for(JSONObject jsonObject : model.getP2pBodies()) {
//				model.getErrorMessageReport().setLength(0);
//
//				model.setMobile(jsonObject.getString("mobile"));
//				model.setContent(jsonObject.getString("content"));
//				model.setFee(jsonObject.getInteger("fee"));
//
//				// 短信内容处理逻辑
//				messageTemplate = doMessageContentProcess(model, smsConfig);
//
//				// 如果手机号码是黑名单则跳过本次逻辑
//				if(isBlacklistMobile(model)) {
//					continue;
//				}
//
//				// 短信手机号码处理逻辑
//				MobileCatagory mobileCatagory = doMobileNumberProcess(model);
//				if(mobileCatagory == null) {
//					continue;
//				}
//
//				if(StringUtils.isNotEmpty(mobileCatagory.getFilterNumbers())) {
//					errorMobiles.add(mobileCatagory.getFilterNumbers());
//					returnFee += model.getFee();
//				}
//
//				// 用户通道处理
//				SmsRoutePassage passage = doUserPassageProcess(model, mobileCatagory,
//						messageTemplate == null ? null : messageTemplate.getRouteType());
//
//				// 通道分包逻辑
//				doPassagePackets(model, mobileCatagory, messageTemplate == null ? null : messageTemplate.getId(), passage);
//
//				totalFee += model.getFee();
//
//				// 如果子任务的每条错误信息不为空，则插入错误信息
//				if(StringUtils.isNotEmpty(model.getErrorMessageReport()))
//					errorMessages.addAll(Arrays.asList(model.getErrorMessageReport().toString().split(ERROR_MESSAGE_SEPERATOR)));
//			}
//
//			// 如果短信内容为点对点，则主任务插入固定内容（子任务为具体内容）
//			model.setContent(model.getP2pBody());
//			if(TaskSubmitType.POINT_TO_POINT.getCode() == model.getSubmitType()) {
//				model.setFinalContent(P2P_SMS_CONTENT);
//			} else if (TaskSubmitType.TEMPLATE_POINT_TO_POINT.getCode() == model.getSubmitType()){
//				model.setFinalContent(p2pContent);
//			}
//
//			// 点对点设置费用为总费用
//			model.setFee(totalFee);
//			if(CollectionUtils.isNotEmpty(errorMessages)) {
//				model.setRemark(StringUtils.join(errorMessages, ERROR_MESSAGE_SEPERATOR));
//			}
//
//			// 处理主任务
//			doPrepareTask(model, errorMobiles, returnFee, messageTemplate == null ? null : messageTemplate.getId());
//
//		} catch (Exception e) {
//			logger.error("未知异常捕获", e);
////			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
//		}  finally {
//			// 确认消息成功消费
//			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
//		}
//	}
//
////	@Override
////	public void confirm(CorrelationData correlationData, boolean ack, String cause) {
////		if(correlationData == null)
////			return;
////
////		if (ack) {
////			logger.error("=================消息队列处理成功：{}", correlationData.getId());
////		} else {
////			logger.error("=================消息队列处理失败：{}，信息：{}", correlationData.getId(), cause);
////		}
////	}
//
////	@PostConstruct
////	public void setConfirmCallback() {
////		// rabbitTemplate如果为单例的话，那回调就是最后设置的内容
////		rabbitTemplate.setConfirmCallback(this);
////	}
//
//
//
//
//
//
//	@Reference
//	protected IUserPassageService userPassageService;
//	@Reference
//	protected IUserSmsConfigService userSmsConfigService;
//	@Autowired
//	protected ISmsTemplateService smsTemplateService;
//	@Autowired
//	protected IForbiddenWordsService forbiddenWordsService;
//	@Autowired
//	protected ISmsPassageAccessService smsPassageAccessService;
//	@Autowired
//	protected RabbitTemplate rabbitTemplate;
//	@Autowired
//	protected Jackson2JsonMessageConverter messageConverter;
//	@Resource
//	protected StringRedisTemplate stringRedisTemplate;
//	@Reference
//	protected ISmsMobileTablesService smsMobileTablesService;
//	@Autowired
//	protected ISmsMobileBlackListService mobileBlackListService;
//	@Reference
//	protected IMobileLocalService mobileLocalService;
//	@Reference
//	protected IPushConfigService pushConfigService;
//	@Reference
//	protected IUserBalanceService userBalanceService;
//	@Autowired
//	protected ISmsMtSubmitService smtMtSubmitService;
//	@Reference
//	private IBillService billService;
//
//	protected Logger logger = LoggerFactory.getLogger(getClass());
//
//	// 默认每个包手机号码上限数
//	private static final int DEFAULT_MOBILE_SIZE_PER_PACKTES = 500;
//
//	/**
//	 *
//	 * TODO 验证数据有效性并返回用户短信配置信息
//	 *
//	 * @param model
//	 */
//	protected UserSmsConfig validateAndParseConfig(SmsMtTask model) {
//		if (model == null)
//			throw new QueueProcessException("待处理数据为空");
//
//		UserSmsConfig userSmsConfig = userSmsConfigService.getByUserId(model.getUserId());
//		if (userSmsConfig == null) {
//			userSmsConfigService.save(model.getUserId(), SmsBillConstant.WORDS_SIZE_PER_NUM, model.getExtNumber());
//
//			model.getErrorMessageReport().append(appendErrorMessage("用户短信配置为空，需要更新"));
//		}
//		return userSmsConfig;
//	}
//
//	/**
//	 *
//	 * TODO 保存子任务
//	 *
//	 * 短信提交数据
//	 *
//	 * @param mobile
//	 *            手机号码
//	 * @param cmcp
//	 *            运营商
//	 * @param provinceCode
//	 *            省份代码
//	 * @param messageTemplateId
//	 *            短信模板ID
//	 * @param passageAccess
//	 *            通道信息
//	 * @param remark
//	 *            备注信息
//	 */
//	protected SmsMtTaskPackets joinTaskPackets(SmsMtTask task, String mobile, Integer cmcp, Integer provinceCode,
//			Long messageTemplateId, SmsPassageAccess passageAccess, String remark, String forceAction) {
//		SmsMtTaskPackets smsMtTaskPackets = new SmsMtTaskPackets();
//		smsMtTaskPackets.setSid(task.getSid());
//		smsMtTaskPackets.setMobile(mobile);
//		smsMtTaskPackets.setCmcp(cmcp);
//		smsMtTaskPackets.setProvinceCode(provinceCode);
//		smsMtTaskPackets.setMobileSize(mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length);
//		smsMtTaskPackets.setContent(task.getContent());
//		smsMtTaskPackets.setMessageTemplateId(messageTemplateId);
//
//		if (passageAccess != null) {
//			smsMtTaskPackets.setPassageId(passageAccess.getPassageId());
//			smsMtTaskPackets.setPassageCode(passageAccess.getPassageCode());
//			smsMtTaskPackets.setFinalPassageId(passageAccess.getPassageId());
//			smsMtTaskPackets.setPassageProtocol(passageAccess.getProtocol());
//			smsMtTaskPackets.setPassageUrl(passageAccess.getUrl());
//			smsMtTaskPackets.setPassageParameter(passageAccess.getParams());
//			smsMtTaskPackets.setResultFormat(passageAccess.getResultFormat());
//			smsMtTaskPackets.setPosition(passageAccess.getPosition());
//			smsMtTaskPackets.setSuccessCode(passageAccess.getSuccessCode());
//			smsMtTaskPackets.setPassageSpeed(passageAccess.getPacketsSize());
//
//			// add by zhengying 20170610 加入签名模式
//			smsMtTaskPackets.setPassageSignMode(passageAccess.getSignMode());
//		}
//
//		smsMtTaskPackets.setRemark(remark);
//		smsMtTaskPackets.setForceActions(forceAction);
//		smsMtTaskPackets.setRetryTimes(0);
//		smsMtTaskPackets.setCreateTime(new Date());
//
//		// 如果账号是华时系统通知账号则直接通过
//		// boolean isAvaiable = isHsAdmin(task.getAppKey());
//
//		// 短信模板ID为空，短信包含敏感词及其他错误信息，短信通道为空 均至状态为 待人工处理
//		if (passageAccess == null || StringUtils.isNotEmpty(remark) || messageTemplateId == null)
//			smsMtTaskPackets.setStatus(PacketsApproveStatus.WAITING.getCode());
//		else {
//			smsMtTaskPackets.setStatus(PacketsApproveStatus.AUTO_COMPLETE.getCode());
//		}
//
//		// 用户自定义内容，一般为他方子平台的开发者ID（渠道），用于标识
//		smsMtTaskPackets.setAttach(task.getAttach());
//		// 设置用户自设置的扩展号码
//		smsMtTaskPackets.setExtNumber(task.getExtNumber());
//		smsMtTaskPackets.setCallback(task.getCallback());
//		smsMtTaskPackets.setUserId(task.getUserId());
//		smsMtTaskPackets.setFee(task.getFee() == null ? SmsBillConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE : task.getFee());
//		smsMtTaskPackets.setSingleFee(billService.getSmsFeeByWords(task.getUserId(), task.getContent()));
//
//		return smsMtTaskPackets;
//	}
//
//	/**
//	 *
//	 * TODO 追加错误信息
//	 *
//	 */
//	protected String appendErrorMessage(String message) {
//		return String.format("●%s%s", message, ERROR_MESSAGE_SEPERATOR);
//	}
//
//	/**
//	 *
//	 * TODO 拼接可操作动作代码
//	 *
//	 * @param index
//	 */
//	protected void appendActionMessage(int index, StringBuilder forceActions) {
//		// 异常分包情况下允许的操作，如000,010，第一位:未报备模板，第二位：敏感词，第三位：通道不可用
//		char[] actions = forceActions.toString().toCharArray();
//
//		actions[index] = PacketsActionActor.BROKEN.getActor();
//
//		forceActions.setLength(0);
//		forceActions.append(String.valueOf(actions));
//	}
//
//	/**
//	 *
//	 * TODO 校验短信签名相关内容
//	 *
//	 * @param model
//	 * @param smsConfig
//	 */
//	protected void doMessageContentCheckSignature(SmsMtTask model, UserSmsConfig smsConfig) {
//		// 如果用户不携带签名模式（一客一签模式），模板匹配需要考虑时候将原短信内容基础上加入签名进行匹配
//		if (smsConfig.getSignatureSource() != null
//				&& smsConfig.getSignatureSource() == SmsSignatureSource.HSPAAS_AUTO_APPEND.getValue()) {
//
//			if (StringUtils.isEmpty(smsConfig.getSignatureContent())) {
//				model.getErrorMessageReport().append(appendErrorMessage("短信内容强制签名但用户签名内容未设置"));
//			} else {
//				model.setContent(String.format("【%s】%s", smsConfig.getSignatureContent(), model.getContent()));
//			}
//
//		} else {
//
//			// 如果签名为客户自维护，则需要判断签名相关信息
//			if (!PatternUtil.isContainsSignature(model.getContent())) {
//				model.getErrorMessageReport().append(appendErrorMessage("用户短信内容不包含签名"));
//			}
//
//			// 判断短信内容是否包含多个签名
////			if (PatternUtil.isMultiSignatures(model.getContent())) {
////				appendErrorMessage("用户短信内容包含多个签名");
////				model.getErrorMessageReport().append(appendErrorMessage("用户短信内容包含多个签名"));
////			}
//
//		}
//	}
//
//	/**
//	 *
//	 * TODO 判断短信是否有敏感词
//	 *
//	 * @param model
//	 * @param whiteWordsRecord
//	 */
//	protected void doMessageContentCheckSensitiveWords(SmsMtTask model, String whiteWordsRecord) {
//		// 判断敏感词开关是否开启
//		if (forbiddenWordsService.isForbiddenWordsAllowPassed()) {
//			logger.info("当前敏感词无需过滤，任务SID：{}", model.getSid());
//			return;
//		}
//
//		// 判断短信内容是否包含敏感词
//		Set<String> filterWords = null;
//		if (StringUtils.isEmpty(whiteWordsRecord)) {
//			filterWords = forbiddenWordsService.filterForbiddenWords(model.getContent());
//
//		} else {
//			// 报备的免审敏感词（报备后对敏感词有效）
//			Set<String> whiteWordsSet = new HashSet<>(Arrays.asList(whiteWordsRecord.split("\\|")));
//			filterWords = forbiddenWordsService.filterForbiddenWords(model.getContent(), whiteWordsSet);
//		}
//
//		if (CollectionUtils.isNotEmpty(filterWords)) {
//			model.setForbiddenWords(StringUtils.join(filterWords, MobileCatagory.MOBILE_SPLIT_CHARCATOR));
//			model.getErrorMessageReport().append(appendErrorMessage(String.format("用户短信内容存在敏感词，敏感词为：%s", filterWords.toString())));
//			appendActionMessage(PacketsActionPosition.FOBIDDEN_WORDS.getPosition(), model.getForceActionsReport());
//		}
//	}
//
//	/**
//	 *
//	 * TODO 用户通道处理逻辑
//	 *
//	 * @param model
//	 */
//	protected SmsRoutePassage doUserPassageProcess(SmsMtTask model, MobileCatagory mobileCatagory, Integer routeType) {
//		UserPassage userPassage = userPassageService.getByUserIdAndType(model.getUserId(),
//				PlatformType.SEND_MESSAGE_SERVICE.getCode());
//		if (userPassage == null) {
//			model.getErrorMessageReport().append("用户通道组未找到");
//			appendActionMessage(PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition(),
//					model.getForceActionsReport());
//			return null;
//		}
//
//		return doRoutePassageByCmcp(mobileCatagory, model.getUserId(), routeType);
//	}
//
//	/**
//	 *
//	 * TODO 根据运营商和路由通道寻找具体的通道信息
//	 *
//	 * 运营商
//	 */
//	protected SmsRoutePassage doRoutePassageByCmcp(MobileCatagory mobileCatagory, int userId, Integer routeType) {
//		SmsPassageAccess passageAccess = null;
//		boolean isAvaiable = false;
//
//		SmsRoutePassage routePassage = new SmsRoutePassage();
//		routePassage.setUserId(userId);
//
//		// 如果路由类型未确定，则按默认路由由走
//		routeType = routeType == null ? RouteType.DEFAULT.getValue() : routeType;
//
//		// 移动通道信息
//		Map<Integer, SmsPassageAccess> cmPassage = new HashMap<Integer, SmsPassageAccess>();
//		// 联通通道信息
//		Map<Integer, SmsPassageAccess> cuPassage = new HashMap<Integer, SmsPassageAccess>();
//		// 电信通道信息
//		Map<Integer, SmsPassageAccess> ctPassage = new HashMap<Integer, SmsPassageAccess>();
//
//		// 移动通道逻辑
//		if (MapUtils.isNotEmpty(mobileCatagory.getCmNumbers())) {
//			Set<Integer> provinceCodes = mobileCatagory.getCmNumbers().keySet();
//			for (Integer provinceCode : provinceCodes) {
//
////				passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
////						CMCP.CHINA_MOBILE.getCode(), provinceCode);
////
////				isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
////				if (!isAvaiable) {
////					// 如果通道不可用，判断当前运营商是否包含 全国通道
////					passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
////							CMCP.CHINA_MOBILE.getCode(), Province.PROVINCE_CODE_ALLOVER_COUNTRY);
////
////					isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
////				}
////
////				if (isAvaiable)
////					cmPassage.put(provinceCode, passageAccess);
////				else
////					routePassage.setCmErrorMessage("任务中包含通道不可用数据");
//
//			}
//
//		}
//
//		// 电信通道逻辑
//		if (MapUtils.isNotEmpty(mobileCatagory.getCtNumbers())) {
//			Set<Integer> provinceCodes = mobileCatagory.getCtNumbers().keySet();
//			for (Integer provinceCode : provinceCodes) {
//
////				passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
////						CMCP.CHINA_TELECOM.getCode(), provinceCode);
////
////				isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
////				if (!isAvaiable) {
////					// 如果通道不可用，判断当前运营商是否包含 全国通道
////					passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
////							CMCP.CHINA_TELECOM.getCode(), Province.PROVINCE_CODE_ALLOVER_COUNTRY);
////
////					isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
////				}
////
////				if (isAvaiable)
////					ctPassage.put(provinceCode, passageAccess);
////				else
////					routePassage.setCtErrorMessage("任务中包含通道不可用数据");
//
//			}
//		}
//
//		// 联通通道逻辑
//		if (MapUtils.isNotEmpty(mobileCatagory.getCuNumbers())) {
//			Set<Integer> provinceCodes = mobileCatagory.getCuNumbers().keySet();
//			for (Integer provinceCode : provinceCodes) {
//
////				passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
////						CMCP.CHINA_UNICOM.getCode(), provinceCode);
////
////				isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
////				if (!isAvaiable) {
////					// 如果通道不可用，判断当前运营商是否包含 全国通道
////					passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
////							CMCP.CHINA_UNICOM.getCode(), Province.PROVINCE_CODE_ALLOVER_COUNTRY);
////
////					isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
////				}
////
////				if (isAvaiable)
////					cuPassage.put(provinceCode, passageAccess);
////				else
////					routePassage.setCuErrorMessage("任务中包含通道不可用数据");
//
//			}
//		}
//
//		// 判断三网通道是否为空，如果最终解析的不为空，则设置相关通道信息
////		if (MapUtils.isNotEmpty(cmPassage))
////			routePassage.setCmPassage(cmPassage);
////
////		if (MapUtils.isNotEmpty(ctPassage))
////			routePassage.setCtPassage(ctPassage);
////
////		if (MapUtils.isNotEmpty(cuPassage))
////			routePassage.setCuPassage(cuPassage);
//
//		return routePassage;
//	}
//
//	/**
//	 *
//	 * TODO 验证通道是否可用，如不可用做报警异常
//	 *
//	 * @param access
//	 * @return
//	 */
//	protected boolean isSmsPassageAccessAvaiable(SmsPassageAccess access) {
//		if (access == null) {
//			// 丢到错误队列还是直接抛异常？？
//
//			return false;
//		}
//		return true;
//	}
//
//	/**
//	 *
//	 * TODO 短信内容逻辑
//	 *
//	 * @param smsConfig
//	 *            短信配置数据
//	 */
//	protected MessageTemplate doMessageContentProcess(SmsMtTask model, UserSmsConfig smsConfig) {
//		// 短信签名判断
//		doMessageContentCheckSignature(model, smsConfig);
//
//		MessageTemplate template = null;
//		// 短信是否免审
//		if (!smsConfig.getMessagePass()) {
//			template = new MessageTemplate();
//			template.setId(TemplateContext.SUPER_TEMPLATE_ID);
//			template.setRouteType(RouteType.DEFAULT.getValue());
//		} else {
//			// 根据短信内容匹配模板，短信模板需要报备而查出的短信模板为空则提至人工处理信息中
//			template = smsTemplateService.getByContent(model.getUserId(), model.getContent());
//			if (template == null) {
//				model.getErrorMessageReport().append(appendErrorMessage("用户短信模板未报备"));
//				appendActionMessage(PacketsActionPosition.SMS_TEMPLATE_MISSED.getPosition(), model.getForceActionsReport());
//			}
//		}
//
//		if (template != null)
//			doSameMobileBeyond(template, model.getUserId(), model.getMobile());
//
//		// 短信内容敏感词逻辑
//		doMessageContentCheckSensitiveWords(model, template == null ? null : template.getWhiteWord());
//
//		return template;
//	}
//
//	/**
//	 *
//	 * TODO 判断用户手机号码是超限/超速
//	 *
//	 * @param template
//	 * @param userId
//	 * @param mobile
//	 */
//	private void doSameMobileBeyond(MessageTemplate template, int userId, String mobile) {
//
//		if (template != null) {
//			// 判断短信发送是否超速
//			// smsMobileTablesService.isMobileBeyondMaxSpeed(userId,
//			// template.getId(), mobile, template.getSubmitInterval());
//			// 判断短信发送是否超限
//			// smsMobileTablesService.isMobileBeyondMaxLimit(userId,
//			// template.getId(), mobile, template.getLimitTimes());
//		}
//	}
//
//	/**
//	 *
//	 * TODO 更新主任务状态
//	 *
//	 */
//	protected void doMobileBelongtoBlacklist(SmsMtTask task, List<String> mobiles) {
//		SmsMtMessageSubmit submit = new SmsMtMessageSubmit();
//		submit.setUserId(task.getUserId());
//		submit.setSid(task.getSid());
//		submit.setContent(task.getContent());
//		submit.setFee(task.getFee());
//		submit.setAttach(task.getAttach());
//		submit.setPassageId(PassageContext.EXCEPTION_PASSAGE_ID);
//		submit.setCreateTime(new Date());
//		submit.setCreateUnixtime(submit.getCreateTime().getTime());
//		submit.setStatus(MessageSubmitStatus.SUCCESS.getCode());
//		submit.setRemark(SmsPushCode.SMS_MOBILE_BLACKLIST.getCode());
//		submit.setPushErrorCode(SmsPushCode.SMS_TASK_REJECT.getCode());
//		submit.setMsgId(task.getSid().toString());
//		submit.setCallback(task.getCallback());
//		// 省份代码默认 为 0（全国）
//		submit.setProvinceCode(Province.PROVINCE_CODE_ALLOVER_COUNTRY);
//
//		// add by zhengying 2017-03-28 针对用户WEB平台发送的数据，则不进行推送，直接在平台看推送记录
//		if (task.getAppType() != null && AppType.DEVELOPER.getCode() == AppType.WEB.getCode()) {
//			submit.setNeedPush(false);
//
//		} else {
//			PushConfig pushConfig = pushConfigService.getPushUrl(task.getUserId(),
//					PlatformType.SEND_MESSAGE_SERVICE.getCode(), task.getCallback());
//
//			// 推送信息为固定地址或者每次传递地址才需要推送
//			if (pushConfig != null && PushConfigStatus.NO.getCode() != pushConfig.getStatus()) {
//				submit.setPushUrl(pushConfig.getUrl());
//				submit.setNeedPush(true);
//			}
//		}
//
//		// 如果黑名单手机号码为多个，则多次发送至队列
//		for (String mobile : mobiles) {
//			submit.setMobile(mobile);
//			submit.setCmcp(CMCP.local(mobile).getCode());
//
//			rabbitTemplate.convertAndSend(MQConstant.EXCHANGE_SMS, MQConstant.MQ_SMS_MT_PACKETS_EXCEPTION, submit);
//		}
//
//	}
//
//	/**
//	 *
//	 * TODO 通道分包逻辑
//	 *
//	 * @param messageTemplateId
//	 * @param mobile
//	 * @param isRoutePassageAvaiable
//	 * @param passageErrorMessage
//	 * @param access
//	 * @param cmcp
//	 */
//	protected void doTaskPackets(SmsMtTask task, Long messageTemplateId, Map<Integer, String> mobileMap,
//			boolean isRoutePassageAvaiable, String passageErrorMessage, Map<Integer, SmsPassageAccess> accessMap,
//			CMCP cmcp, List<SmsMtTaskPackets> packets) {
//		StringBuilder finalMessage = new StringBuilder(task.getErrorMessageReport());
//		String action = task.getForceActionsReport().toString();
//
//		// 设置可操作类型
//		if (!isRoutePassageAvaiable || StringUtils.isNotEmpty(passageErrorMessage) || MapUtils.isEmpty(accessMap)) {
//			finalMessage.append("●").append(passageErrorMessage).append(" ;");
//			// 更新通道错误码 操作位
//			char[] actions = task.getForceActionsReport().toString().toCharArray();
//			actions[PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition()] = PacketsActionActor.BROKEN.getActor();
//			action = new String(actions);
//
//			// 主要为了设置主任务错误信息和操作符 add by zhengying 2017-03-08
//			task.getErrorMessageReport().append(appendErrorMessage("任务中包含通道不可用数据"));
//			appendActionMessage(PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition(), task.getForceActionsReport());
//		}
//
//		String mobile = null;
//		SmsPassageAccess access = null;
//
//		// 遍历所有分省后的数据
//		for (Integer provinceCode : mobileMap.keySet()) {
//
//			mobile = mobileMap.get(provinceCode);
//
//			String[] mobiles = mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR);
//			if (mobiles.length == 0) {
//				logger.info("手机号码为空 {}", mobile.toString());
//				continue;
//			}
//
//			// 通道信息为空，则子任务插入空数据
//			if(accessMap == null || accessMap.get(provinceCode) == null) {
//				packets.add(joinTaskPackets(task, mobile, cmcp.getCode(), provinceCode, messageTemplateId, access,
//						finalMessage.toString(), action));
//				logger.info("通道信息为空,sid: {}, mobile:{}", task.getSid(), mobile.toString());
//				continue;
//			}
//
//			access = accessMap.get(provinceCode);
//
//			// 大于预设的手机号码分包数需要 拆包处理
//			int perMobileSize = (access == null || access.getMobileSize() == null || access.getMobileSize() == 0)
//					? DEFAULT_MOBILE_SIZE_PER_PACKTES : access.getMobileSize();
//
//			// 0表示号码无限制
//			if (mobiles.length == 1 || perMobileSize == 0) {
//				packets.add(joinTaskPackets(task, mobile, cmcp.getCode(), provinceCode, messageTemplateId, access,
//						finalMessage.toString(), action));
//				continue;
//			}
//
//			// 如果手机号码多于分包数量，需要分包保存子任务
//			List<String> fmobiles = regroupMobiles(mobiles, perMobileSize);
//			if (CollectionUtils.isNotEmpty(fmobiles)) {
//				for (String m : fmobiles)
//					packets.add(joinTaskPackets(task, m, cmcp.getCode(), provinceCode, messageTemplateId, access,
//							finalMessage.toString(), action));
//			}
//		}
//	}
//
//	/**
//	 *
//	 * TODO 组装生成最终的子任务数据
//	 *
//	 * @param task
//	 * @param mobileCatagory
//	 * @param messageTemplateId
//	 * @param routePassage
//	 * @return
//	 */
//	protected List<SmsMtTaskPackets> doGeneratePassagePackets(SmsMtTask task, MobileCatagory mobileCatagory,
//			Long messageTemplateId, SmsRoutePassage routePassage) {
//
//		// 分包子任务
//		List<SmsMtTaskPackets> packets = new ArrayList<>();
////		boolean isPassageAvaiable = routePassage != null;
//		// 移动分包逻辑
////		if (MapUtils.isNotEmpty(mobileCatagory.getCmNumbers())) {
////			doTaskPackets(task, messageTemplateId, mobileCatagory.getCmNumbers(), isPassageAvaiable,
////					routePassage.getCmErrorMessage(), routePassage.getCmPassage(), CMCP.CHINA_MOBILE, packets);
////		}
////		// 联通分包逻辑
////		if (MapUtils.isNotEmpty(mobileCatagory.getCuNumbers())) {
////			doTaskPackets(task, messageTemplateId, mobileCatagory.getCuNumbers(), isPassageAvaiable,
////					routePassage.getCuErrorMessage(), routePassage.getCuPassage(), CMCP.CHINA_UNICOM, packets);
////		}
////		// 电信分包逻辑
////		if (MapUtils.isNotEmpty(mobileCatagory.getCtNumbers())) {
////			doTaskPackets(task, messageTemplateId, mobileCatagory.getCtNumbers(), isPassageAvaiable,
////					routePassage.getCtErrorMessage(), routePassage.getCtPassage(), CMCP.CHINA_TELECOM, packets);
////		}
//
//		return packets;
//	}
//
//	/**
//	 *
//	 * TODO 追加错误信息
//	 *
//	 */
//	protected static String getErrorMessage(String message) {
//		return String.format("●%s;", message);
//	}
}
