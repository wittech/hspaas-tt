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
 * 下行发送短信服务
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年12月25日 下午4:03:11
 */
public interface ISmsMtSubmitService {

    /**
     * 查询短信发送记录（后台）
     * 
     * @param queryParams 查询条件
     * @return 分页数据
     */
    BossPaginationVo<SmsMtMessageSubmit> findPage(Map<String, Object> queryParams);

    /**
     * 根据条件查询发送列表集合（含回执信息）
     * 
     * @param queryParams 查询条件
     * @return 列表
     */
    List<SmsMtMessageSubmit> findList(Map<String, Object> queryParams);

    /**
     * 查询短信发送记录（前端）
     * 
     * @param userId 用户ID
     * @param mobile 手机号码
     * @param startDate 开始日期
     * @param endDate 截止日期
     * @param currentPage 当前页码
     * @param sid 消息ID
     * @return 分页数据
     */
    PaginationVo<SmsMtMessageSubmit> findPage(int userId, String mobile, String startDate, String endDate,
                                              String currentPage, String sid);

    /**
     * 根据SID查询提交列表信息
     * 
     * @param sid 消息ID
     * @return 发送数据列表
     */
    List<SmsMtMessageSubmit> findBySid(long sid);

    /**
     * 保存发送记录
     * 
     * @param submit 发送记录
     * @return 成功标识
     */
    boolean save(SmsMtMessageSubmit submit);

    /**
     * 获取昨日消费短信条数
     * 
     * @return 消费发送信息
     */
    List<ConsumptionReport> getConsumeMessageInYestday();

    /**
     * 查询用户最后一条发送记录，可能是错误信息，也可能存在与回执信息
     * 
     * @param userId 用户编号
     * @param mobile 手机吗
     * @return 最后数据
     */
    SmsLastestRecordVo findLastestRecord(int userId, String mobile);

    /**
     * 批量插入提交信息
     * 
     * @param list 发送数据集合
     */
    void batchInsertSubmit(List<SmsMtMessageSubmit> list);

    /**
     * 根据消息ID或手机号码获取待回执信息（已提交）
     * 
     * @param msgId 消息ID
     * @param mobile 手机号码
     * @return 发送记录
     */
    SmsMtMessageSubmit getSubmitWaitReceipt(String msgId, String mobile);

    /**
     * 根据MO参数分析具体的userId和SID
     * 
     * @param passageId 通道ID
     * @param msgId 回执消息ID
     * @param mobile 手机号码
     * @param spcode 码号
     * @return 发送记录
     */
    SmsMtMessageSubmit getByMoMapping(Integer passageId, String msgId, String mobile, String spcode);

    /**
     * 根据消息ID和手机号码获取提交信息
     * 
     * @param msgId 消息ID
     * @param mobile 手机号码
     * @return 发送记录
     */
    SmsMtMessageSubmit getByMsgidAndMobile(String msgId, String mobile);

    /**
     * 根据消息ID获取提交信息
     * 
     * @param msgId 消息ID
     * @return 发送记录
     */
    SmsMtMessageSubmit getByMsgid(String msgId);

    /**
     * 异常短信处理，如黑名单/驳回/超时，超速
     * 
     * @param submits 发送记录集合
     * @return 成功标识
     */
    boolean doSmsException(List<SmsMtMessageSubmit> submits);

    /**
     * 声明新的待提交队列
     * 
     * @param protocol 协议类型
     * @param passageCode 通道编码
     * @return 处理结果
     */
    boolean declareNewSubmitMessageQueue(String protocol, String passageCode);

    /**
     * 移除通道提交消息队列
     * 
     * @param passageCode 通道编码
     * @return 处理结果
     */
    boolean removeSubmitMessageQueue(String passageCode);

    /**
     * 声明所有使用中通道队列(短信提交队列)
     * 
     * @return 处理结果
     */
    boolean declareWaitSubmitMessageQueues();

    /**
     * 获取提交队列名称
     * 
     * @param passageCode 通道编码
     * @return 队列名称
     */
    String getSubmitMessageQueueName(String passageCode);

    /**
     * 发送数据到提交队列
     * 
     * @param packets 子任务
     * @return 处理结果
     */
    boolean sendToSubmitQueue(List<SmsMtTaskPackets> packets);

    /**
     * 获取告警短信记录信息
     * 
     * @param passageId 通道id
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @return 发送记录集合
     */
    List<SmsMtMessageSubmit> getRecordListToMonitor(Long passageId, Long startTime, Long endTime);

    /**
     * 获取提交统计报告数据
     * 
     * @param startTime 开始时间（毫秒值）
     * @param endTime 截止时间（毫秒值）
     * @return 提交统计报告数据
     */
    List<Map<String, Object>> getSubmitStatReport(Long startTime, Long endTime);

    /**
     * 获取上一小时提交报告
     * 
     * @return 提交高爆
     */
    List<Map<String, Object>> getLastHourSubmitReport();

    /**
     * 获取提交分省运营商统计信息
     * 
     * @param startTime 开始时间
     * @param endTime 截止时间
     * @return 分省运营商统计信息
     */
    List<Map<String, Object>> getSubmitCmcpReport(Long startTime, Long endTime);

    /**
     * 判断短信是否需要推送，需要则设置推送信息
     * 
     * @param submits 提交记录集合
     */
    void setPushConfigurationIfNecessary(List<SmsMtMessageSubmit> submits);
}
