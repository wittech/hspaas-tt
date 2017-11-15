package com.huashi.common.notice.context;

public class MessageContext {

		/**
	 * 
	  * TODO 消息读取状态
	  *
	  * @author zhengying
	  * @version V1.0.0   
	  * @date 2016年9月7日 下午11:24:29
	 */
	public enum ReadStatus {
		UNREAD(0, "未读"), READ(1, "已读");

		private int code;
		private String title;

		public int getCode() {
			return code;
		}

		public String getTitle() {
			return title;
		}

		private ReadStatus(int code, String title) {
			this.code = code;
			this.title = title;
		}

	}
}
