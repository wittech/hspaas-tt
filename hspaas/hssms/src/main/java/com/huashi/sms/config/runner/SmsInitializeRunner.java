package com.huashi.sms.config.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.service.ISmsMtPushService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.settings.service.IForbiddenWordsService;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.huashi.sms.settings.service.ISmsMobileWhiteListService;
import com.huashi.sms.template.service.ISmsTemplateService;

@Configuration
@Order(1)
public class SmsInitializeRunner implements CommandLineRunner {

	@Autowired
	private ISmsMtSubmitService smsMtSubmitService;
	@Autowired
	private ISmsMtPushService smsMtPushService;
	@Autowired
	private ISmsPassageService smsPassageService;
	@Autowired
	private ISmsTemplateService smsTemplateService;
	@Autowired
	private ISmsPassageAccessService smsPassageAccessService;
	@Autowired
	private ISmsMobileBlackListService smsMobileBlackListService;
	@Autowired
	private IForbiddenWordsService forbiddenWordsService;
	@Autowired
	private ISmsMobileWhiteListService mobileWhiteListService;

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void run(String... arg0) throws Exception {
		logger.info("=======================数据初始化REDIS=======================");
		initPassage();
		initAccessPassage();
		initMessageTemplate();
		initForbiddenWordsList();
		initMobileBlacklist();
		initMobileWhiteList();
		initUserMtReportPushConfigQueue();
		logger.info("=======================初始化REDIS完成=======================");

		logger.info("=======================数据初始化MQ=======================");
		boolean isSuccess = initMessageQueues();
		if (!isSuccess) {
			logger.info("=======================初始化MQ失败=======================");
			return;
		}
		logger.info("=======================初始化MQ完成=======================");
	}

	private void initPassage() {
		smsPassageService.reloadToRedis();
		logger.info("短信通道初始化完成");
	}

	private void initAccessPassage() {
		smsPassageAccessService.reload();
		logger.info("短信热点通道初始化完成");
	}

	private void initMessageTemplate() {
		smsTemplateService.reloadToRedis();
		logger.info("短信模板初始化完成");
	}

	private void initMobileBlacklist() {
		logger.info("手机号码黑名单初始化处理中...");
		boolean result = smsMobileBlackListService.reloadToRedis();
		logger.info("手机号码黑名单初始化{}", result ? "完成" : "失败");
	}

	/**
	 * 
	 * TODO 初始化短信敏感词信息
	 */
	private void initForbiddenWordsList() {
		forbiddenWordsService.reloadRedisForbiddenWords();
		logger.info("短信敏感词信息初始化完成");
	}

	/**
	 * 
	 * TODO 初始化短信手机号码白名单数据
	 */
	private void initMobileWhiteList() {
		mobileWhiteListService.reloadToRedis();
		logger.info("短信手机号码白名单数据");
	}

	/**
	 * 
	 * TODO 初始化待提交消息队列信息
	 */
	private boolean initMessageQueues() {
		return smsMtSubmitService.declareWaitSubmitMessageQueues();
	}

	/**
	 * 
	 * TODO 初始化所有用户下行状态推送队列数据
	 * 
	 * @return
	 */
	private boolean initUserMtReportPushConfigQueue() {
		boolean isSuccess = smsMtPushService.doListenerAllUser();
		if (isSuccess)
			logger.info("用户下行状态报告推送队列初始化完成");
		else
			logger.info("用户下行状态报告推送队列初始化失败");
		return isSuccess;
	}

}
