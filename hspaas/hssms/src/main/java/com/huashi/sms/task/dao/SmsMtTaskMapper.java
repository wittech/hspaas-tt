package com.huashi.sms.task.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.huashi.sms.task.domain.SmsMtTask;

public interface SmsMtTaskMapper {
    int deleteByPrimaryKey(Long id);

    int insert(SmsMtTask record);

    int insertSelective(SmsMtTask record);

    SmsMtTask selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SmsMtTask record);

    int updateByPrimaryKey(SmsMtTask record);

    List<SmsMtTask> findList(Map<String, Object> params);

    int findCount(Map<String, Object> params);

    /**
     * TODO 根据SID更新短信内容
     *
     * @param sid
     * @param content
     * @return
     */
    int updateContent(@Param("sid") long sid, @Param("content") String content);

    /**
     * TODO 根据主任务ID获取任务详细信息
     *
     * @param sid
     * @return
     */
    SmsMtTask selectBySid(Long sid);

    /**
     * TODO 批量插入信息
     *
     * @param list
     * @return
     */
    int batchInsert(List<SmsMtTask> list);

    /**
     * TODO 更新审核状态
     *
     * @param id
     * @param approveStatus
     * @return
     */
    int updateApproveStatus(@Param("id") long id, @Param("approveStatus") int approveStatus);

    /**
     * TODO 根据SID更新审核状态
     *
     * @param sid
     * @param approveStatus
     * @return
     */
    int updateApproveStatusBySid(@Param("sid") long sid, @Param("approveStatus") int approveStatus);

    /**
     * TODO 获取待处理任务总数
     *
     * @return
     */
    int selectWaitDealTaskCount();

    /**
     * TODO 根据审核状态查询主任务信息
     *
     * @return
     */
    List<SmsMtTask> selectWaitDealTaskList();

    /**
     * TODO 根据ID数组查询相关任务信息
     *
     * @param list
     * @return
     */
    List<SmsMtTask> selectTaskByIds(@Param("list") List<String> list);
    
    /**
     * 
       * TODO 根据短信内容（相等）查询任务信息
       * @param content
       * @return
     */
    List<SmsMtTask> selectEqualContent(@Param("content") String content);
    
    /**
     * 
       * TODO 根据短信内容（相等）查询任务信息
       * @param content
       * @return
     */
    List<SmsMtTask> selectLikeContent(@Param("content") String content);

}