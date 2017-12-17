package com.huashi.monitor.constant;

public class MonitorPassageContext {

	/**
	 * 
	  * TODO 通道轮训运行中状态
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2017年2月24日 下午6:23:16
	 */
	public enum PassagePullRunnintStatus {
		YES(0, "运行中"), NO(1, "停止");
		
		private int code;
		private String title;

		private PassagePullRunnintStatus(int code, String title) {
			this.code = code;
			this.title = title;
		}

		public int getCode() {
			return code;
		}
		
		
		public String getTitle() {
			return title;
		}

		public static String parse(int code) {
			for(PassagePullRunnintStatus ma : PassagePullRunnintStatus.values()) {
				if(code == ma.getCode()) {
                    {
                        return ma.getTitle();
                    }
                }
			}
			
			return PassagePullRunnintStatus.NO.getTitle();
		}
		
		
	}
}
