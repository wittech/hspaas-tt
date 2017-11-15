package com.huashi.sms.record.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.record.domain.SmsMtMessageSubmit;

public interface SmsMtMessageSubmitMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsMtMessageSubmit record);

    int insertSelective(SmsMtMessageSubmit record);

    SmsMtMessageSubmit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsMtMessageSubmit record);

    int updateByPrimaryKey(SmsMtMessageSubmit record);
    
    List<SmsMtMessageSubmit> findList(Map<String,Object> params);

    int findCount(Map<String,Object> params);

    /**
     * 
       * TODO 根据SID查询所有的提交信息
       * @param sid
       * @return
     */
    List<SmsMtMessageSubmit> findBySid(@Param("sid") long sid);
    
    /**
     * 
       * TODO 根据上家通道回执消息ID查询提交数据
       * 
       * @param mobile
       * @return
     */
    SmsMtMessageSubmit selectByMobile(@Param("mobile") String mobile);
    
    /**
     * 
       * TODO 根据上家通道回执消息ID查询提交数据
       * 
       * @param msgId
       * @return
     */
    SmsMtMessageSubmit selectByMsgId(@Param("msgId") String msgId);
    
    /**
     * 
       * TODO 根据上家通道回执消息ID查询提交数据
       * 
       * @param msgId
       * @param mobile
       * @return
     */
    SmsMtMessageSubmit selectByMsgIdAndMobile(@Param("msgId") String msgId, 
    		@Param("mobile") String mobile);
    
  /**
   * 
     * TODO 根据上家通道回执消息ID查询提交数据
     * @param passageId
     * 		通道ID
     * @param msgId
     * 		消息ID
     * @param mobile
     * 		手机号码
     * @return
   */
    SmsMtMessageSubmit selectByPsm(@Param("passageId") Integer passageId ,@Param("msgId") String msgId, 
    		@Param("mobile") String mobile);
    
    /**
     * 
       * TODO 批量插入信息
       * 
       * @param list
       * @return
     */
    int batchInsert(List<SmsMtMessageSubmit> list);
    
    /**
    * 
      * TODO 根据日期查询发送记录
      * @param date
      * @return
    */
    List<SmsMtMessageSubmit> findByDate(@Param("date") String date);

    /**
     * 
       * TODO 统计监听时间内的数据量
       * 
       * @param passageId
       * @param startTime
       * @param endTime
       * @return
     */
    List<SmsMtMessageSubmit> getRecordListToMonitor(@Param("passageId") Long passageId, 
    		@Param("startTime") Long startTime, @Param("endTime") Long endTime);
    
    /**
     * 
       * TODO 查询时间段内的统计数据
       * 
       * @param startTime
       * @param endTime
       * @return
     */
    List<Map<String, Object>> selectSubmitReport(@Param("startTime") Long startTime, 
    		@Param("endTime") Long endTime);
    
    /**
     * 
       * TODO 根据用户ID和手机号码查询最后一条提交记录
       * 
       * @param userId
       * @param mobile
       * @return
     */
    Map<String, Object> selectByUserIdAndMobile(@Param("userId") Integer userId, 
    		@Param("mobile") String mobile);
    
    /**
     * 
       * TODO 获取分省运营商统计数据
       * 
       * @param startTime
       * @param endTime
       * @return
     */
    List<Map<String, Object>> selectCmcpReport(@Param("startTime") Long startTime, 
    		@Param("endTime") Long endTime);
    
    
}