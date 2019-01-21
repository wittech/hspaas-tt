package com.huashi.mms.record.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.mms.record.domain.MmsMtMessageSubmit;
import com.huashi.mms.task.domain.MmsMtTaskPackets;

/**
 * TODO 下行彩信提交服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月15日 上午11:14:38
 */
public interface IMmsMtSubmitService {

    /**
     * TODO 查询彩信发送记录(BOSS)
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
    BossPaginationVo<MmsMtMessageSubmit> findPage(Map<String, Object> queryParams);

    /**
     * 发送彩信记录(WEB)
     * 
     * @param userId
     * @param mobile
     * @param startDate
     * @param endDate
     * @param currentPage
     * @param sid
     * @return
     */
    PaginationVo<MmsMtMessageSubmit> findPage(int userId, String mobile, String startDate, String endDate,
                                              String currentPage, String sid);

    /**
     * TODO 根据SID查询提交列表信息
     * 
     * @param sid
     * @return
     */
    List<MmsMtMessageSubmit> findBySid(long sid);

    /**
     * TODO 保存
     * 
     * @param submit
     * @return
     */
    boolean save(MmsMtMessageSubmit submit);

    /**
     * TODO 批量插入提交信息
     * 
     * @param list
     * @return
     */
    void batchInsertSubmit(List<MmsMtMessageSubmit> list);

    /**
     * TODO 根据消息ID或手机号码获取待回执信息（已提交）
     * 
     * @param msgId 消息ID
     * @param mobile 手机号码
     * @return
     */
    MmsMtMessageSubmit getSubmitWaitReceipt(String msgId, String mobile);

    /**
     * TODO 根据MO参数分析具体的userId和SID
     * 
     * @param passageId 通道ID
     * @param msgId 回执消息ID
     * @param mobile 手机号码
     * @param spcode 码号
     * @return
     */
    MmsMtMessageSubmit getByMoMapping(Integer passageId, String msgId, String mobile, String spcode);

    /**
     * TODO 根据消息ID和手机号码获取提交信息
     * 
     * @param msgId 消息ID
     * @param mobile 手机号码
     * @return
     */
    MmsMtMessageSubmit getByMsgidAndMobile(String msgId, String mobile);

    /**
     * TODO 根据消息ID获取提交信息
     * 
     * @param msgId 消息ID
     * @return
     */
    MmsMtMessageSubmit getByMsgid(String msgId);

    /**
     * TODO 异常彩信处理，如黑名单/驳回/超时，超速
     * 
     * @param submit
     * @return
     */
    boolean doSmsException(List<MmsMtMessageSubmit> submits);

    /**
     * TODO 声明新的待提交队列
     * 
     * @param protocol 协议类型
     * @param passageCode
     * @return
     */
    boolean declareNewSubmitMessageQueue(String protocol, String passageCode);

    /**
     * TODO 移除通道提交消息队列
     * 
     * @param passageCode
     * @return
     */
    boolean removeSubmitMessageQueue(String passageCode);

    /**
     * TODO 声明所有使用中通道队列(彩信提交队列)
     * 
     * @return
     */
    boolean declareWaitSubmitMessageQueues();

    /**
     * TODO 获取提交队列名称
     * 
     * @param passageCode
     * @return
     */
    String getSubmitMessageQueueName(String passageCode);

    /**
     * TODO 发送数据到提交队列
     * 
     * @param packets
     * @return
     */
    boolean sendToSubmitQueue(List<MmsMtTaskPackets> packets);

    /**
     * TODO 获取告警彩信记录信息
     * 
     * @param passageId
     * @param startTime
     * @param endTime
     * @return
     */
    List<MmsMtMessageSubmit> getRecordListToMonitor(Long passageId, Long startTime, Long endTime);

    /**
     * TODO 获取提交统计报告数据
     * 
     * @param startTime 开始时间（毫秒值）
     * @param endTime 截止时间（毫秒值）
     * @return
     */
    List<Map<String, Object>> getSubmitStatReport(Long startTime, Long endTime);

    /**
     * TODO 获取上一小时提交报告
     * 
     * @return
     */
    List<Map<String, Object>> getLastHourSubmitReport();

    /**
     * TODO 获取提交分省运营商统计信息
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    List<Map<String, Object>> getSubmitCmcpReport(Long startTime, Long endTime);

    /**
     * TODO 判断彩信是否需要推送，需要则设置推送信息
     * 
     * @param submits
     */
    void setPushConfigurationIfNecessary(List<MmsMtMessageSubmit> submits);
}
