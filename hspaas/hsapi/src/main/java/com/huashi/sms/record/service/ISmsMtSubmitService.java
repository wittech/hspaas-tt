package com.huashi.sms.record.service;

import java.util.List;
import java.util.Map;

import com.huashi.bill.bill.domain.ConsumptionReport;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.vo.SmsLastestRecordVo;
import com.huashi.sms.task.domain.SmsMtTaskPackets;

/**
 * 
 * TODO 下行发送短信服务
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年12月25日 下午4:03:11
 */
public interface ISmsMtSubmitService {

	/**
	 * 
	 * TODO 查询短信发送记录
	 * 
	 * @param pageNum
	 * @param sid
	 * @param mobile
	 * @param content
	 * @param status
	 * @param appKey
	 * @param deliverStatus
	 * @param start
	 * @param end
	 * @return
	 */
	BossPaginationVo<SmsMtMessageSubmit> findPage(Map<String, Object> queryParams);

	/**
	 * 融合平台 发送短信记录
	 * 
	 * @param userId
	 * @param mobile
	 * @param startDate
	 * @param endDate
	 * @param currentPage
	 * @param sid
	 * @return
	 */
	PaginationVo<SmsMtMessageSubmit> findPage(int userId, String mobile, String startDate, String endDate,
			String currentPage, String sid);

	/**
	 * 
	 * TODO 根据SID查询提交列表信息
	 * 
	 * @param sid
	 * @return
	 */
	List<SmsMtMessageSubmit> findBySid(long sid);

	/**
	 * 
	 * TODO 保存
	 * 
	 * @param submit
	 * @return
	 */
	boolean save(SmsMtMessageSubmit submit);

	/**
	 * 
	 * TODO 获取昨日消费短信条数
	 * 
	 * @param userId
	 * @return
	 */
	List<ConsumptionReport> getConsumeMessageInYestday();

	/**
	 * 
	 * TODO 查询用户最后一条发送记录，可能是错误信息，也可能存在与回执信息
	 * 
	 * @param userId
	 * @param mobile
	 * @return
	 */
	SmsLastestRecordVo findLastestRecord(int userId, String mobile);

	/**
	 * 
	 * TODO 批量插入提交信息
	 * 
	 * @param list
	 * @return
	 */
	void batchInsertSubmit(List<SmsMtMessageSubmit> list);

	/**
	 * 
	 * TODO 根据消息ID或手机号码获取待回执信息（已提交）
	 * 
	 * @param msgId
	 *            消息ID
	 * @param mobile
	 *            手机号码
	 * @return
	 */
	SmsMtMessageSubmit getSubmitWaitReceipt(String msgId, String mobile);

	/**
	 * 
	 * TODO 根据MO参数分析具体的userId和SID
	 * 
	 * @param passageId
	 *            通道ID
	 * @param msgId
	 *            回执消息ID
	 * @param mobile
	 *            手机号码
	 * @param spcode
	 *            码号
	 * @return
	 */
	SmsMtMessageSubmit getByMoMapping(Integer passageId, String msgId, String mobile, String spcode);

	/**
	 * 
	 * TODO 根据消息ID和手机号码获取提交信息
	 * 
	 * @param msgId
	 *            消息ID
	 * @param mobile
	 *            手机号码
	 * @return
	 */
	SmsMtMessageSubmit getByMsgidAndMobile(String msgId, String mobile);
	
	/**
	 * 
	 * TODO 根据消息ID获取提交信息
	 * 
	 * @param msgId
	 *            消息ID
	 * @return
	 */
	SmsMtMessageSubmit getByMsgid(String msgId);

	/**
	 * 
	 * TODO 异常短信处理，如黑名单/驳回/超时，超速
	 * 
	 * @param submit
	 * @return
	 */
	boolean doSmsException(List<SmsMtMessageSubmit> submits);

	/**
	 * 
	 * TODO 声明新的待提交队列
	 * 
	 * @param protocol
	 * 		协议类型
	 * @param passageCode
	 * @return
	 */
	boolean declareNewSubmitMessageQueue(String protocol, String passageCode);
	/**
	 * 
	   * TODO 移除通道提交消息队列
	   * 
	   * @param passageCode
	   * @return
	 */
	boolean removeSubmitMessageQueue(String passageCode);

	/**
	 * 
	 * TODO 声明所有使用中通道队列(短信提交队列)
	 * 
	 * @return
	 */
	boolean declareWaitSubmitMessageQueues();

	/**
	 * 
	 * TODO 获取提交队列名称
	 * 
	 * @param passageCode
	 * @return
	 */
	String getSubmitMessageQueueName(String passageCode);

	/**
	 * 
	 * TODO 发送数据到提交队列
	 * 
	 * @param packets
	 * @return
	 */
	boolean sendToSubmitQueue(List<SmsMtTaskPackets> packets);

	/**
	 * 
	 * TODO 获取告警短信记录信息
	 * 
	 * @param passageId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	List<SmsMtMessageSubmit> getRecordListToMonitor(Long passageId, Long startTime, Long endTime);
	
	/**
	 * 
	   * TODO 获取提交统计报告数据
	   * 
	   * @param startTime
	   * 		开始时间（毫秒值）
	   * @param endTime
	   * 		截止时间（毫秒值）
	   * @return
	 */
	List<Map<String, Object>> getSubmitStatReport(Long startTime, Long endTime);
	
	/**
	 * 
	   * TODO 获取上一小时提交报告
	   * 
	   * @return
	 */
	List<Map<String, Object>> getLastHourSubmitReport();
	
	/**
	 * 
	   * TODO 获取提交分省运营商统计信息
	   * 
	   * @param startTime
	   * @param endTime
	   * @return
	 */
	List<Map<String, Object>> getSubmitCmcpReport(Long startTime, Long endTime);
	
	/**
	 * 
	   * TODO 判断短信是否需要推送，需要则设置推送信息
	   * 
	   * @param submits
	 */
	void setPushConfigurationIfNecessary(List<SmsMtMessageSubmit> submits);
}
