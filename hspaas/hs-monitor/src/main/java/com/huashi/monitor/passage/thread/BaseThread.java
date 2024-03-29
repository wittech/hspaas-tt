package com.huashi.monitor.passage.thread;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO 线程抽象基础类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年4月27日 上午10:59:10
 */
public abstract class BaseThread {

    /**
     * 自定义线程关闭标记（用于钩子回调）
     */
    public static volatile boolean              shutdownSignal             = false;

    /**
     * 默认线程休眠20秒
     */
    public static final int                     SLEEP_TIME                 = 5 * 1000;

    /**
     * 自定义间隔时间
     */
    public static final String                  INTERVAL_KEY               = "interval";

    protected final Logger                      logger                     = LoggerFactory.getLogger(getClass());

    /**
     * 自定义线程拉取通道下行/上行数据前缀标识
     */
    public static final String                  PASSAGE_PULL_THREAD_PREFIX = "pid";

    // private static final int SERVICE_INJECT_FAILED_ALARM_COUNT = 50;

    /**
     * 运行中的全部通道
     */
    public static volatile Map<String, Boolean> PASSAGES_IN_RUNNING        = new ConcurrentHashMap<String, Boolean>();

    /**
     * DUBBO服务注入失败次数（主要针对其他依赖服务可能重启或者断开了）
     */
    protected AtomicInteger                     serviceInjectFailedCount   = new AtomicInteger(0);

    /**
     * TODO 远程服务是否丢失
     * 
     * @return
     */
    protected abstract boolean isRemoteServiceMissed();

    /**
     * TODO 服务是否可用(主要针对其他依赖服务可能重启或者断开了)
     * 
     * @return
     */
    protected boolean isServiceAvaiable() {
        // if(!isRemoteServiceMissed())
        // return true;
        //
        // // 服务出错首次告警
        // if(serviceInjectFailedCount.getAndIncrement() == 0) {
        // logger.error("DUBBO服务注入失败，请及时检修, {}", serviceInjectFailedCount.get());
        // return false;
        // }
        //
        // // 服务超出预设上限次数告警
        // if(serviceInjectFailedCount.incrementAndGet() >= SERVICE_INJECT_FAILED_ALARM_COUNT) {
        // logger.error("DUBBO服务注入失败，请及时检修, {}", serviceInjectFailedCount.get());
        // serviceInjectFailedCount.set(1);
        // }
        //
        // return false;

        return true;
    }

    /**
     * TODO 是否终止执行(JVM 应用程序钩子HOOK设置)
     *
     * @return
     */
    protected boolean isApplicationStop() {
        return shutdownSignal;
    }
}
