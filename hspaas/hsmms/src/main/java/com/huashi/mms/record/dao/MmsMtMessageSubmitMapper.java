package com.huashi.mms.record.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.mms.record.domain.MmsMtMessageSubmit;

public interface MmsMtMessageSubmitMapper {
    int deleteByPrimaryKey(Long id);

    int insert(MmsMtMessageSubmit record);

    int insertSelective(MmsMtMessageSubmit record);

    MmsMtMessageSubmit selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(MmsMtMessageSubmit record);

    int updateByPrimaryKey(MmsMtMessageSubmit record);
    
    List<MmsMtMessageSubmit> findList(Map<String,Object> params);

    int findCount(Map<String,Object> params);
    
    /**
     * 
       * TODO 针对指定用户查询列表数据（一般指WEB分页查询）
       * @param params
       * @return
     */
    List<MmsMtMessageSubmit> findListByUser(Map<String,Object> params);

    /**
     * 
       * TODO 针对指定用户查询总量数据（一般指WEB统计查询）
       * @param params
       * @return
     */
    int findCountByUser(Map<String,Object> params);

    /**
     * 
       * TODO 根据SID查询所有的提交信息
       * @param sid
       * @return
     */
    List<MmsMtMessageSubmit> findBySid(@Param("sid") long sid);
    
    /**
     * 
       * TODO 根据上家通道回执消息ID查询提交数据
       * 
       * @param mobile
       * @return
     */
    MmsMtMessageSubmit selectByMobile(@Param("mobile") String mobile);
    
    /**
     * 
       * TODO 根据上家通道回执消息ID查询提交数据
       * 
       * @param msgId
       * @return
     */
    MmsMtMessageSubmit selectByMsgId(@Param("msgId") String msgId);
    
    /**
     * 
       * TODO 根据上家通道回执消息ID查询提交数据
       * 
       * @param msgId
       * @param mobile
       * @return
     */
    MmsMtMessageSubmit selectByMsgIdAndMobile(@Param("msgId") String msgId, 
            @Param("mobile") String mobile);
    
  /**
   * 
     * TODO 根据上家通道回执消息ID查询提交数据
     * @param passageId
     *      通道ID
     * @param msgId
     *      消息ID
     * @param mobile
     *      手机号码
     * @return
   */
    MmsMtMessageSubmit selectByPsm(@Param("passageId") Integer passageId ,@Param("msgId") String msgId, 
            @Param("mobile") String mobile);
    
    /**
     * 
       * TODO 批量插入信息
       * 
       * @param list
       * @return
     */
    int batchInsert(List<MmsMtMessageSubmit> list);
    
    /**
    * 
      * TODO 根据日期查询发送记录
      * @param date
      * @return
    */
    List<MmsMtMessageSubmit> findByDate(@Param("date") String date);

    /**
     * 
       * TODO 统计监听时间内的数据量
       * 
       * @param passageId
       * @param startTime
       * @param endTime
       * @return
     */
    List<MmsMtMessageSubmit> getRecordListToMonitor(@Param("passageId") Long passageId, 
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