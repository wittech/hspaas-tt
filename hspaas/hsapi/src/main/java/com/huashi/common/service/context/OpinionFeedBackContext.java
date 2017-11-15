
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.common.service.context;


/**
 * TODO 反馈泛型
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月5日 下午12:37:11
 */

public class OpinionFeedBackContext {
	/**
	 * 
	 * TODO 反馈类型
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年3月5日 下午12:38:40
	 */
	public enum FeedbackType {
		FEEDBACKZERO(0, "网站建设"), FEEDBACKONE(1, "紧急处理"), FEEDBACKSECOND(2, "普通处理");
		private int value;
		private String title;

		private FeedbackType(int value, String title) {
			this.value = value;
			this.title = title;
		}

		public static FeedbackType parse(int value) {
			for (FeedbackType as : FeedbackType.values()) {
				if (as.getValue() == value)
					return as;
			}
			return null;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}
	}
}
