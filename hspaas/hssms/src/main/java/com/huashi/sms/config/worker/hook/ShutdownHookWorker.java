package com.huashi.sms.config.worker.hook;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 钩子回调线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年11月29日 下午6:41:16
 */
public class ShutdownHookWorker implements Runnable {

    /**
     * 自定义线程关闭标记（用于钩子回调）
     */
    public static volatile boolean shutdownSignal         = false;

    private final Object           startupShutdownMonitor = new Object();

    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public ShutdownHookWorker(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        this.threadPoolTaskExecutor = threadPoolTaskExecutor;
    }

    @Override
    public void run() {
        synchronized (startupShutdownMonitor) {
            ShutdownHookWorker.shutdownSignal = true;

            // Map<String, SimpleMessageListenerContainer> containers =
            // applicationContext.getBeansOfType(SimpleMessageListenerContainer.class);
            // if(MapUtils.isNotEmpty(containers)) {
            // logger.info("共获取RABBIT监听{} 个， 名称为：{}", containers.size(), JSON.toJSONString(containers.keySet()));
            //
            // for(SimpleMessageListenerContainer container : containers.values()) {
            // container.shutdown();
            // }
            // }

            threadPoolTaskExecutor.shutdown();
        }

    }
}
