package com.huashi.mms.task.service;

import java.util.List;
import java.util.Map;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.ResponseMessage;
import com.huashi.mms.task.domain.MmsMtTask;
import com.huashi.mms.task.domain.MmsMtTaskPackets;

/**
 * TODO 彩信下行任务接口
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月15日 上午11:17:24
 */
public interface IMmsMtTaskService {

    /**
     * TODO 查询待处理/已完成任务列信息
     * 
     * @param condition
     * @return
     */
    BossPaginationVo<MmsMtTask> findPage(Map<String, Object> condition);

    /**
     * TODO 获取待处理的任务总数（目前主要用于5分钟弹窗提醒 短信处理）
     * 
     * @return
     */
    Integer getTaskCountInWaiting();

    /**
     * 查询子任务
     * 
     * @param sid
     * @return
     */
    List<MmsMtTaskPackets> findChildTaskBySid(long sid);

    /**
     * TODO 根据消息ID更改短信内容（一般是用户提交内容有误，手动修改）
     * 
     * @param sid 消息ID
     * @param content 短信内容
     * @return
     */
    boolean updateMmsContent(long sid, String content);

    /**
     * TODO 批量更新短信内容
     * 
     * @param sidArrays
     * @param content
     * @return
     */
    boolean batchUpdateMmsContent(String sidArrays, String content);

    /**
     * TODO 新增任务
     * 
     * @param task
     * @return
     */
    void save(MmsMtTask task);

    /**
     * TODO 更新主任务信息
     * 
     * @param task
     * @return
     */
    boolean update(MmsMtTask task);

    /**
     * TODO 添加子任务并且更新主任务
     * 
     * @param packetes
     * @return
     */
    boolean savePackets(MmsMtTaskPackets packetes);

    /**
     * TODO 根据SID获取主任务Id
     * 
     * @param sid
     * @return
     */
    MmsMtTask getTaskBySid(Long sid);

    /**
     * TODO 根据任务ID更新审批状态
     * 
     * @param id 主任务id
     * @param status 审批状态
     * @return
     */
    boolean updateStatus(long id, int status);

    /**
     * TODO 切换通道（子任务操作）
     * 
     * @param taskPacketsId 子任务ID
     * @param passageId 切换后的通道ID
     * @return
     */
    boolean changeTaskPacketsPassage(long taskPacketsId, int passageId);

    /**
     * TODO 批量切换任务通道信息
     * 
     * @param taskIds 主任务ID集合，以','(半角逗号)分割
     * @param expectPassageId 切换后的通道ID
     * @return
     */
    boolean changeTaskPacketsPassage(String taskIds, int expectPassageId);

    /**
     * TODO 更新子任务操作符（用于标识通道错误，如模板未报备，包含敏感词，无可用通道等）
     * 
     * @param taskPacketsId
     * @param actions
     * @return
     */
    boolean updateForceActions(long taskPacketsId, String actions);

    /**
     * TODO 更新任务状态（如果任务更新成完成，需要重入队列发送）
     * 
     * @param taskPacketsId
     * @param status
     * @return
     */
    boolean updateTaskPacketsStatus(long taskPacketsId, int status);

    /**
     * TODO 根据ID查询子任务信息
     * 
     * @param id
     * @return
     */
    MmsMtTaskPackets getTaskPacketsById(long id);

    /**
     * TODO 批量插入任务信息
     * 
     * @param tasks
     * @param taskPackets
     * @return
     */
    void batchSave(List<MmsMtTask> tasks, List<MmsMtTaskPackets> taskPackets);

    /**
     * TODO 主任务强制通过
     *
     * @param sid
     * @return
     */
    boolean updateMainTaskByForcePass(long sid);

    /**
     * TODO 主任务任务驳回 数据
     * 
     * @param taskIds
     * @return
     */
    boolean doRejectInTask(String taskIds);

    /**
     * TODO 子任务驳回数据
     * 
     * @param packtesId
     * @return
     */
    boolean doRejectInTaskPackets(long packtesId);

    /**
     * TODO 批量审核通过
     * 
     * @param taskIds
     * @return
     */
    ResponseMessage doTaskApproved(String taskIds);

    /**
     * TODO 批量审批同样内容短信任务
     * 
     * @param content 短信内容
     * @param isLikePattern 是否模糊匹配，如果模糊匹配则采用包含模式，默认为全匹配 false
     * @return
     */
    int approvedBySameTitle(String title, boolean isLikePattern);

    /**
     * TODO 批量驳回同样内容短信任务
     *
     * @param content 短信内容
     * @param isLikePattern 是否模糊匹配，如果模糊匹配则采用包含模式，默认为全匹配 false
     * @return
     */
    int rejectBySameTitle(String title, boolean isLikePattern);

    /**
     * TODO 重新分包
     * 
     * @param id
     * @return
     */
    boolean doRePackets(long id);

    /**
     * TODO 主任务批量重新分包
     * 
     * @param mainTaskIds
     * @return
     */
    boolean batchDoRePackets(String mainTaskIds);

    /**
     * TODO 根据任务ID获取短信任务信息
     * 
     * @param id
     * @return
     */
    MmsMtTask findById(long id);

    /**
     * 融合前台 查询全部任务
     * 
     * @param userId
     * @param sid
     * @param title
     * @param start
     * @param end
     * @param currentPage
     * @return
     */
    PaginationVo<MmsMtTask> findAll(int userId, String sid, String title, Long start, Long end, String currentPage);

    /**
     * TODO 子任务中是否存在通道错误
     * 
     * @param sid
     * @return
     */
    boolean isTaskChildrenHasPassageError(Long sid);
}
