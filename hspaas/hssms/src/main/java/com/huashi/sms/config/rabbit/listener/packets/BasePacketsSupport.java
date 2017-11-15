package com.huashi.sms.config.rabbit.listener.packets;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.huashi.common.third.model.MobileCatagory;

public abstract class BasePacketsSupport {
	
	// 错误信息分隔符
	protected static final String ERROR_MESSAGE_SEPERATOR = ";";
	// 错误消息组装
	protected ThreadLocal<StringBuilder> errorReport = new ThreadLocal<StringBuilder>();
	
	protected void appendErrorReport(String message) {
		if(errorReport.get() == null)
			errorReport.set(new StringBuilder());
		
		errorReport.get().append(String.format("●%s;", message));
	}
	
	protected String getErrorReport() {
		return errorReport.get() == null ? "" :errorReport.get().toString();
	}
	
	/**
	 * 
	   * TODO 手机号码是否单个
	   * @param mobile
	   * @return
	 */
	protected static boolean isSingleMobileSize(String mobile) {
		if(StringUtils.isEmpty(mobile))
			return true;
		
		if(mobile.trim().length() == 11 || mobile.split(MobileCatagory.MOBILE_SPLIT_CHARCATOR).length == 1)
			return true;
		
		return false;
	}
	
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
//		if (StringUtils.isEmpty(model.getMobile()))
//			throw new QueueProcessException("手机号码为空");
//
//		logger.info("userSmsConfigService：" + userSmsConfigService);
//		
//		UserSmsConfig userSmsConfig = userSmsConfigService.getByUserId(model.getUserId());
//		if (userSmsConfig == null) {
//			userSmsConfigService.save(model.getUserId(), SmsBillConstant.WORDS_SIZE_PER_NUM, model.getExtNumber());
//
//			appendErrorMessage("用户短信配置为空，需要更新");
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
//		smsMtTaskPackets
//				.setFee(task.getFee() == null ? SmsBillConstant.CONTENT_WORDS_EXCEPTION_COUNT_FEE : task.getFee());
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
//		return String.format("●%s;", message);
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
//				appendErrorMessage("短信内容强制签名但用户签名内容未设置");
//			} else {
//				model.setContent(String.format("【%s】%s", smsConfig.getSignatureContent(), model.getContent()));
//			}
//
//		} else {
//
//			// 如果签名为客户自维护，则需要判断签名相关信息
//			if (!PatternUtil.isContainsSignature(model.getContent())) {
//				appendErrorMessage("用户短信内容不包含签名");
//			}
//
//			// 判断短信内容是否包含多个签名
//			if (PatternUtil.isMultiSignatures(model.getContent())) {
//				appendErrorMessage("用户短信内容包含多个签名");
//			}
//
//		}
//	}

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
//			// 遍历敏感词信息，并组装
//			// StringBuilder finalWords = new StringBuilder();
//			// filterWords.forEach(item -> {
//			// finalWords.append(item).append("\\|");
//			// });
//			//
//			// finalWords.substring(0, finalWords.length() - 1);
//
//			model.getErrorMessageReport()
//					.append(appendErrorMessage(String.format("用户短信内容存在敏感词，敏感词为：%s", filterWords.toString())));
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
//				passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
//						CMCP.CHINA_MOBILE.getCode(), provinceCode);
//				
//				logger.error("当前通道：{}", JSON.toJSONString(passageAccess));
//
//				isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
//				if (!isAvaiable) {
//					// 如果通道不可用，判断当前运营商是否包含 全国通道
//					passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
//							CMCP.CHINA_MOBILE.getCode(), Province.PROVINCE_CODE_ALLOVER_COUNTRY);
//					
//					logger.error("当前通道2：{}", JSON.toJSONString(passageAccess));
//
//					isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
//					
//					logger.error("可用状态：{}", isAvaiable);
//				}
//
//				if (isAvaiable)
//					cmPassage.put(provinceCode, passageAccess);
//				else
//					routePassage.setCmErrorMessage(getPassageErrorMessage(routePassage.getCmErrorMessage(), provinceCode, CMCP.CHINA_MOBILE));
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
//				passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
//						CMCP.CHINA_TELECOM.getCode(), provinceCode);
//
//				isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
//				if (!isAvaiable) {
//					// 如果通道不可用，判断当前运营商是否包含 全国通道
//					passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
//							CMCP.CHINA_TELECOM.getCode(), Province.PROVINCE_CODE_ALLOVER_COUNTRY);
//
//					isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
//				}
//
//				if (isAvaiable)
//					ctPassage.put(provinceCode, passageAccess);
//				else
//					routePassage.setCtErrorMessage(
//							getPassageErrorMessage(routePassage.getCtErrorMessage(), provinceCode, CMCP.CHINA_TELECOM));
//
//			}
//		}
//
//		// 联通通道逻辑
//		if (MapUtils.isNotEmpty(mobileCatagory.getCuNumbers())) {
//			Set<Integer> provinceCodes = mobileCatagory.getCuNumbers().keySet();
//			for (Integer provinceCode : provinceCodes) {
//
//				passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
//						CMCP.CHINA_UNICOM.getCode(), provinceCode);
//
//				isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
//				if (!isAvaiable) {
//					// 如果通道不可用，判断当前运营商是否包含 全国通道
//					passageAccess = smsPassageAccessService.getByUserId(routePassage.getUserId(), routeType,
//							CMCP.CHINA_UNICOM.getCode(), Province.PROVINCE_CODE_ALLOVER_COUNTRY);
//
//					isAvaiable = isSmsPassageAccessAvaiable(passageAccess);
//				}
//
//				if (isAvaiable)
//					cuPassage.put(provinceCode, passageAccess);
//				else
//					routePassage.setCuErrorMessage(
//							getPassageErrorMessage(routePassage.getCuErrorMessage(), provinceCode, CMCP.CHINA_UNICOM));
//
//			}
//		}
//
//		// 判断三网通道是否为空，如果最终解析的不为空，则设置相关通道信息
//		if (MapUtils.isNotEmpty(cmPassage))
//			routePassage.setCmPassage(cmPassage);
//
//		if (MapUtils.isNotEmpty(ctPassage))
//			routePassage.setCtPassage(ctPassage);
//
//		if (MapUtils.isNotEmpty(cuPassage))
//			routePassage.setCuPassage(cuPassage);
//
//		return routePassage;
//	}
//
//	/**
//	 * 
//	 * TODO 组装可用通道无法找到错误信息
//	 * 
//	 * @param plainMessage
//	 *            原有错误信息
//	 * @param provinceCode
//	 *            省份代码
//	 * @param cmcp
//	 *            运营商
//	 * @return
//	 */
//	protected String getPassageErrorMessage(String plainMessage, Integer provinceCode, CMCP cmcp) {
//		if (StringUtils.isEmpty(plainMessage))
//			return String.format("省份代码：%d，运营商：%s 通道不可用", provinceCode, cmcp.getTitle());
//
//		return String.format("%s; 省份代码：%d，运营商：%s 通道不可用", plainMessage, provinceCode, cmcp.getTitle());
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
//			logger.info("当前短信模板免审核，任务SID：{}", model.getSid());
//		} else {
//			// 根据短信内容匹配模板，短信模板需要报备而查出的短信模板为空则提至人工处理信息中
//			template = smsTemplateService.getByContent(model.getUserId(), model.getContent());
//			if (template == null) {
//				model.getErrorMessageReport().append(appendErrorMessage("用户短信模板未报备"));
//				appendActionMessage(PacketsActionPosition.SMS_TEMPLATE_MISSED.getPosition(),
//						model.getForceActionsReport());
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
//	protected void doMobileBelongtoBlacklist(SmsMtTask task, List<String> mobiles, String errorStatusCode) {
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
//		submit.setRemark(errorStatusCode);
//		submit.setMsgId(task.getSid().toString());
//		submit.setCallback(task.getCallback());
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
//				logger.error("手机号码为空 {}", mobile.toString());
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
//			List<String> fmobiles = doSplitMobileByPacketsSize(mobiles, perMobileSize);
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
//	 * TODO 拆分数组，按照分包数量进行数据拆分
//	 * 
//	 * 分包数据
//	 * 
//	 * @param splitSize
//	 *            每个包上限数
//	 * @return
//	 */
//	private List<String> doSplitMobileByPacketsSize(String[] mobile, int splitSize) {
//		int totalSize = mobile.length;
//		// 获取要拆分子数组个数
//		int count = (totalSize % splitSize == 0) ? (totalSize / splitSize) : (totalSize / splitSize + 1);
//
//		List<String> rows = new ArrayList<>();
//		StringBuilder builder = null;
//		for (int i = 0; i < count; i++) {
//
//			int index = i * splitSize;
//			builder = new StringBuilder();
//
//			for (int j = 0; j < splitSize && index < totalSize; j++) {
//				builder.append(mobile[index++]).append(",");
//			}
//
//			rows.add(builder.substring(0, builder.length() - 1));
//		}
//		return rows;
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
//		boolean isPassageAvaiable = routePassage != null;
//		// 移动分包逻辑
//		if (MapUtils.isNotEmpty(mobileCatagory.getCmNumbers())) {
//			doTaskPackets(task, messageTemplateId, mobileCatagory.getCmNumbers(), isPassageAvaiable,
//					routePassage.getCmErrorMessage(), routePassage.getCmPassage(), CMCP.CHINA_MOBILE, packets);
//		}
//		// 联通分包逻辑
//		if (MapUtils.isNotEmpty(mobileCatagory.getCuNumbers())) {
//			doTaskPackets(task, messageTemplateId, mobileCatagory.getCuNumbers(), isPassageAvaiable,
//					routePassage.getCuErrorMessage(), routePassage.getCuPassage(), CMCP.CHINA_UNICOM, packets);
//		}
//		// 电信分包逻辑
//		if (MapUtils.isNotEmpty(mobileCatagory.getCtNumbers())) {
//			doTaskPackets(task, messageTemplateId, mobileCatagory.getCtNumbers(), isPassageAvaiable,
//					routePassage.getCtErrorMessage(), routePassage.getCtPassage(), CMCP.CHINA_TELECOM, packets);
//		}
//
//		return packets;
//	}
	
	/**
	 * 
	 * TODO 拆分数组，按照分包数量进行数据拆分
	 * 
	 * 分包数据
	 * 
	 * @param splitSize
	 *            每个包上限数
	 * @return
	 */
	static List<String> doSplitMobileByPacketsSize(String[] mobile, int splitSize) {
		int totalSize = mobile.length;
		// 获取要拆分子数组个数
		int count = (totalSize % splitSize == 0) ? (totalSize / splitSize) : (totalSize / splitSize + 1);

		List<String> rows = new ArrayList<>();
		StringBuilder builder = null;
		for (int i = 0; i < count; i++) {

			int index = i * splitSize;
			builder = new StringBuilder();

			for (int j = 0; j < splitSize && index < totalSize; j++) {
				builder.append(mobile[index++]).append(",");
			}

			rows.add(builder.substring(0, builder.length() - 1));
		}
		return rows;
	}
}
