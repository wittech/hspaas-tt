package com.huashi.monitor.constant;

/**
 * 
  * TODO 监管中心常量
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年2月22日 下午8:36:32
 */
public class MonitorConstant {

	// 通道任务调度主题（广播方式, 订阅-发布）
	public static final String BROADCAST_PASSAGE_TASK_TOPIC = "broadcast_passage_task_topic";
	// 自取报告通道 县城组信息
	public static final String RD_PASSAGE_PULL_THREAD_GROUP = "rd_passage_pull_thread_group";
	
	/**
	 * 
	  * TODO 消息操作动作
	  * 
	  * @author zhengying
	  * @version V1.0   
	  * @date 2016年12月27日 下午5:50:14
	 */
	public enum MessageAction {
		ADD(1), REMOVE(2), MODIFY(3); 
		
		private int code;

		private MessageAction(int code) {
			this.code = code;
		}

		public int getCode() {
			return code;
		}
		
		public static MessageAction parse(int code) {
			for(MessageAction ma : MessageAction.values()) {
				if(code == ma.getCode()) {
                    {
                        return ma;
                    }
                }
			}
			
			return MessageAction.ADD;
		}
		
		
	}
}
