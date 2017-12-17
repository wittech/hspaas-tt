package com.huashi.sms.task.context;

public class TaskContext {

	/**
	 * 
	 * TODO 分包状态
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年10月28日 上午12:21:17
	 */
	public enum PacketsProcessStatus {
		DOING(0, "正在分包"), PROCESS_COMPLETE(1, "分包完成，待提交网关"), PROCESS_EXCEPTION(2, "分包异常，待处理");

		private int code;
		private String name;

		private PacketsProcessStatus(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static PacketsProcessStatus parse(int code) {
			for (PacketsProcessStatus pt : PacketsProcessStatus.values()) {
				if (pt.getCode() == code) {
                    {
                        return pt;
                    }
                }
			}
			return PROCESS_EXCEPTION;
		}
	}

	/**
	 * 
	 * TODO 审核状态
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年10月28日 上午12:22:54
	 */
	public enum PacketsApproveStatus {
		WAITING(0, "待审核"), AUTO_COMPLETE(1, "自动通过"), HANDLING_COMPLETE(2, "手动通过"), REJECT(3, "驳回");

		private int code;
		private String name;

		private PacketsApproveStatus(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static PacketsApproveStatus parse(int code) {
			for (PacketsApproveStatus pt : PacketsApproveStatus.values()) {
				if (pt.getCode() == code) {
                    {
                        return pt;
                    }
                }
			}
			return WAITING;
		}
	}

	/**
	 * 
	 * TODO 调用上家接口状态
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年11月13日 下午11:24:40
	 */
	public enum MessageSubmitStatus {
		SUCCESS(0, "处理成功"), FAILED(1, "处理失败");

		private int code;
		private String name;

		private MessageSubmitStatus(int code, String name) {
			this.code = code;
			this.name = name;
		}

		public int getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public static MessageSubmitStatus parse(int code) {
			for (MessageSubmitStatus pt : MessageSubmitStatus.values()) {
				if (pt.getCode() == code) {
                    {
                        return pt;
                    }
                }
			}
			return FAILED;
		}
	}

	/**
	 * 
	 * TODO 分包异常操作定位
	 *
	 * @author zhengying
	 * @version V1.0.0
	 * @date 2016年11月12日 下午3:39:24
	 */
	public enum PacketsActionPosition {
		SMS_TEMPLATE_MISSED(0, "短信模板未报备"), FOBIDDEN_WORDS(1, "短信存在敏感词"), PASSAGE_NOT_AVAIABLE(2, "通道不可用");

		private int position;
		private String title;

		private PacketsActionPosition(int position, String title) {
			this.position = position;
			this.title = title;
		}

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

	/**
	 * 
	 * TODO 分包子任务操作符
	 * 
	 * @author zhengying
	 * @version V1.0
	 * @date 2017年2月21日 上午11:58:23
	 */
	public enum PacketsActionActor {
		AVAIABLE('0', "可用"), BROKEN('1', "故障");
		private char actor;
		private String title;

		private PacketsActionActor(char actor, String title) {
			this.actor = actor;
			this.title = title;
		}

		public char getActor() {
			return actor;
		}

		public void setActor(char actor) {
			this.actor = actor;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

	/**
	 * 
	  * TODO 任务提交类型
	  *
	  * @author zhengying
	  * @version V1.0.0   
	  * @date 2017年3月29日 下午10:38:58
	 */
	public enum TaskSubmitType {

		BATCH_MESSAGE(0, "批量"), POINT_TO_POINT(1, "普通点对点"), TEMPLATE_POINT_TO_POINT(2, "模板点对点");
		private int code;
		private String title;

		private TaskSubmitType(int code, String title) {
			this.code = code;
			this.title = title;
		}

		public int getCode() {
			return code;
		}

		public void setCode(int code) {
			this.code = code;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

	}

}
