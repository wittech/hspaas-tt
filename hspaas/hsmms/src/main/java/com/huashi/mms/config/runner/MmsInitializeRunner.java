package com.huashi.mms.config.runner;

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

import com.huashi.mms.config.worker.hook.ShutdownHookWorker;
import com.huashi.mms.passage.service.IMmsPassageAccessService;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.mms.record.service.IMmsMtPushService;
import com.huashi.mms.record.service.IMmsMtSubmitService;

/**
 * TODO 初始化项目依赖的资源，如REDIS/自定义线程开启等
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年5月3日 上午11:18:55
 */
@Configuration
@Order(2)
public class MmsInitializeRunner implements CommandLineRunner {

    @Autowired
    private IMmsMtSubmitService      mmsMtSubmitService;
    @Autowired
    private IMmsPassageService       mmsPassageService;
    @Autowired
    private IMmsMtPushService        mmsMtPushService;
    @Autowired
    private IMmsPassageAccessService mmsPassageAccessService;
    @Resource
    private ThreadPoolTaskExecutor   threadPoolTaskExecutor;

    public static final Lock         LOCK                   = new ReentrantLock();
    public static final Condition    CONDITION              = LOCK.newCondition();

    private final Logger             logger                 = LoggerFactory.getLogger(getClass());

    /**
     * 自定义初始化资源是否完成（因有些服务强依赖某些资源初始化完成，如 rabbit listener 消费）
     */
    public static volatile boolean   isResourceInitFinished = false;

    @Override
    public void run(String... arg0) throws Exception {
        logger.info("=======================数据初始化MQ=======================");
        initMessageQueues();
        initPassage();
        initAccessPassage();
        initSignal();
        initDeliverFailoverPushThreads();
        registShutdownHook();
        logger.info("=======================数据初始化MQ完成=======================");
    }

    /**
     * TODO 初始化待提交消息队列信息
     */
    private boolean initMessageQueues() {
        return mmsMtSubmitService.declareWaitSubmitMessageQueues();
    }

    private void initPassage() {
        mmsPassageService.reloadToRedis();
        logger.info("彩信通道初始化完成");
    }

    private void initAccessPassage() {
        mmsPassageAccessService.reload();
        logger.info("彩信可用通道初始化完成");
    }

    private void initDeliverFailoverPushThreads() {
        mmsMtPushService.startFailoverListener();
    }

    /**
     * TODO 初始化信号源控制
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
