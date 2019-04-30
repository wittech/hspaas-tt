package com.huashi.sms.config.runner;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.huashi.sms.config.worker.hook.ShutdownHookWorker;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.service.ISmsMtPushService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.settings.service.IForbiddenWordsService;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.huashi.sms.settings.service.ISmsMobileWhiteListService;
import com.huashi.sms.template.service.ISmsTemplateService;

/**
 * 初始化项目依赖的资源，如REDIS/自定义线程开启等
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年5月3日 上午11:18:55
 */
@Configuration
@Order(2)
public class SmsInitializeRunner implements CommandLineRunner {

    @Autowired
    private ISmsMtSubmitService        smsMtSubmitService;
    @Autowired
    private ISmsPassageService         smsPassageService;
    @Autowired
    private ISmsTemplateService        smsTemplateService;
    @Autowired
    private ISmsPassageAccessService   smsPassageAccessService;
    @Autowired
    private ISmsMobileBlackListService smsMobileBlackListService;
    @Autowired
    private IForbiddenWordsService     forbiddenWordsService;
    @Autowired
    private ISmsMobileWhiteListService mobileWhiteListService;
    @Autowired
    private ISmsMtPushService          smsMtPushService;
    @Resource
    private ThreadPoolTaskExecutor     threadPoolTaskExecutor;

    public static final Lock           LOCK                   = new ReentrantLock();
    public static final Condition      CONDITION              = LOCK.newCondition();

    private final Logger               logger                 = LoggerFactory.getLogger(getClass());

    /**
     * 自定义初始化资源是否完成（因有些服务强依赖某些资源初始化完成，如 rabbit listener 消费）
     */
    public static volatile boolean     isResourceInitFinished = false;

    @Override
    public void run(String... arg0) throws Exception {
        logger.info("=======================数据初始化REDIS=======================");
        try {
            initPassage();
            initAccessPassage();
            initMessageTemplate();
            initForbiddenWordsList();
            initMobileBlacklist();
            initMobileWhiteList();
            initDeliverFailoverPushThreads();
            registShutdownHook();
            logger.info("=======================初始化REDIS完成=======================");
        } catch (Exception e) {
            logger.info("=======================初始化REDIS失败=======================", e);
            throw e;
        }

        logger.info("=======================数据初始化MQ=======================");
        try {
            boolean isSuccess = initMessageQueues();
            if (!isSuccess) {
                logger.info("=======================初始化MQ失败=======================");
                throw new RuntimeException("初始化MQ失败");
            }

            logger.info("=======================初始化MQ完成=======================");
        } catch (Exception e) {
            logger.info("=======================初始化MQ失败=======================", e);
            throw e;
        }

        initSignal();

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
     * 初始化短信敏感词信息
     */
    private void initForbiddenWordsList() {
        forbiddenWordsService.reloadRedisForbiddenWords();
        logger.info("短信敏感词信息初始化完成");
    }

    /**
     * 初始化短信手机号码白名单数据
     */
    private void initMobileWhiteList() {
        mobileWhiteListService.reloadToRedis();
        logger.info("短信手机号码白名单数据");
    }

    /**
     * 初始化待提交消息队列信息
     */
    private boolean initMessageQueues() {
        return smsMtSubmitService.declareWaitSubmitMessageQueues();
    }

    private void initDeliverFailoverPushThreads() {
        smsMtPushService.startFailoverListener();
    }

    /**
     * 初始化信号源控制
     */
    private void initSignal() {
        LOCK.lock();
        try {
            isResourceInitFinished = true;
            CONDITION.signalAll();
            logger.info("初始化资源信号源标记完成，开始消费..");
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * TODO 注册JVM关闭钩子函数
     */
    private void registShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHookWorker(threadPoolTaskExecutor)));
        logger.info("Jvm hook thread has registed");
    }

}
