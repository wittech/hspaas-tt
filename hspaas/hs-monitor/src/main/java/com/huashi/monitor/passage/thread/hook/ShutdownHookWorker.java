package com.huashi.monitor.passage.thread.hook;

import com.huashi.monitor.passage.thread.BaseThread;

/**
 * 
  * TODO 钩子回调线程
  * 
  * @author zhengying
  * @version V1.0   
  * @date 2017年11月29日 下午6:41:16
 */
public class ShutdownHookWorker implements Runnable {
	
	private final Object startupShutdownMonitor = new Object();
	
//	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void run() {
		synchronized (startupShutdownMonitor) {
		    BaseThread.shutdownSignal = true;
		}
		
	}
}
