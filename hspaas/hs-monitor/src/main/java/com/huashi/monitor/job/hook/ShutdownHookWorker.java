package com.huashi.monitor.job.hook;


/**
 * TODO 钩子回调线程
 * 
 * @author zhengying
 * @version V1.0
 * @date 2017年11月29日 下午6:41:16
 */
public class ShutdownHookWorker implements Runnable {

    private final Object startupShutdownMonitor = new Object();

    @Override
    public void run() {
        synchronized (startupShutdownMonitor) {
//            BaseThread.shutdownSignal = true;
        }

    }
}
