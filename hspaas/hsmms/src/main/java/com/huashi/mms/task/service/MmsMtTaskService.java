package com.huashi.mms.task.service;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.fastjson.JSON;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserMmsConfigService;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.IdGenerator;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.constants.ResponseMessage;
import com.huashi.mms.config.cache.redis.constant.MmsRepeatSubmitConstant;
import com.huashi.mms.config.rabbit.constant.RabbitConstant;
import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.mms.record.service.IMmsMtSubmitService;
import com.huashi.mms.task.dao.MmsMtTaskMapper;
import com.huashi.mms.task.dao.MmsMtTaskPacketsMapper;
import com.huashi.mms.task.domain.MmsMtTask;
import com.huashi.mms.task.domain.MmsMtTaskPackets;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.huashi.sms.task.context.TaskContext.PacketsActionActor;
import com.huashi.sms.task.context.TaskContext.PacketsActionPosition;
import com.huashi.sms.task.context.TaskContext.PacketsApproveStatus;

/**
 * TODO 短信下行任务服务实现
 *
 * @author zhengying
 * @version V1.0
 * @date 2016年10月27日 下午12:55:56
 */
@Service
public class MmsMtTaskService implements IMmsMtTaskService {

    @Autowired
    private IdGenerator                idGenerator;
    @Reference
    private IUserService               userService;
    @Reference
    private IUserBalanceService        userBalanceService;
    @Reference
    private IUserPassageService        userPassageService;
    @Reference
    private IUserMmsConfigService      userMmsConfigService;
    @Reference
    private IPushConfigService         pushConfigService;
    @Reference
    private ISmsMobileBlackListService mobileBlackListService;
    @Reference
    private IProvinceService           provinceService;

    @Autowired
    private MmsMtTaskMapper            taskMapper;
    @Autowired
    private MmsMtTaskPacketsMapper     taskPacketsMapper;
    @Autowired
    private IMmsPassageService         mmsPassageService;
    @Autowired
    private IMmsMtSubmitService        mmsMtSubmitService;
    @Autowired
    private IMmsTemplateService        mmsTemplateService;
    @Autowired
    private MmsMtTaskForkService       mmsMtTaskForkService;

    @Resource
    private RabbitTemplate             rabbitTemplate;
    @Resource
    private StringRedisTemplate        stringRedisTemplate;

    /**
     * 防止同时审批产生脏数据
     */
    private static final Lock          lock   = new ReentrantLock();
    private final Logger               logger = LoggerFactory.getLogger(getClass());

    @Override
    public BossPaginationVo<MmsMtTask> findPage(Map<String, Object> condition) {
        changeTimestampeParamsIfExists(condition);

        BossPaginationVo<MmsMtTask> page = new BossPaginationVo<>();
        page.setCurrentPage(Integer.parseInt(condition.getOrDefault("currentPage", 1).toString()));
        int total = taskMapper.findCount(condition);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        condition.put("start", page.getStartPosition());
        condition.put("end", page.getPageSize());

        List<MmsMtTask> dataList = taskMapper.findList(condition);
        for (MmsMtTask record : dataList) {
            record.setUserModel(userService.getByUserId(record.getUserId()));
        }
        page.getList().addAll(dataList);
        return page;
    }

    /**
     * TODO 转换时间戳信息
     *
     * @param queryParams
     */
    private void changeTimestampeParamsIfExists(Map<String, Object> queryParams) {
        String startDate = queryParams.get("startDate") == null ? "" : queryParams.get("startDate").toString();
        String endDate = queryParams.get("endDate") == null ? "" : queryParams.get("endDate").toString();

        if (StringUtils.isNotBlank(startDate)) {
            queryParams.put("startDate", DateUtil.getSecondDate(startDate).getTime());
        }

        if (StringUtils.isNotBlank(endDate)) {
            queryParams.put("endDate", DateUtil.getSecondDate(endDate).getTime());
        }

    }

    /**
     * 获取待处理的短信任务条数
     *
     * @return
     */
    @Override
    public Integer getTaskCountInWaiting() {
        return taskMapper.selectWaitDealTaskCount();
    }

    @Override
    public List<MmsMtTaskPackets> findChildTaskBySid(long sid) {
        List<MmsMtTaskPackets> childList = taskPacketsMapper.selectBySid(sid);

        Map<Integer, String> provinceMap = new HashMap<>();
        Map<Integer, String> passageMap = new HashMap<>();
        for (MmsMtTaskPackets taskPackets : childList) {

            // 组装省份信息
            if (taskPackets.getProvinceCode() != null && taskPackets.getProvinceCode() > 0) {
                if (provinceMap.containsKey(taskPackets.getProvinceCode())) {
                    taskPackets.setProvinceName(provinceMap.get(taskPackets.getProvinceCode()));
                } else {
                    // 根据省份代码查询省份名称
                    Province province = provinceService.get(taskPackets.getProvinceCode());
                    taskPackets.setProvinceName(province == null ? "未知" : province.getName());
                    provinceMap.put(taskPackets.getProvinceCode(), taskPackets.getProvinceName());
                }
            }

            // 组装通道信息
            if (taskPackets.getFinalPassageId() != null && taskPackets.getFinalPassageId() > 0) {
                if (passageMap.containsKey(taskPackets.getFinalPassageId())) {
                    taskPackets.setPassageName(passageMap.get(taskPackets.getFinalPassageId()));
                } else {
                    MmsPassage passage = mmsPassageService.findById(taskPackets.getFinalPassageId());
                    taskPackets.setPassageName(passage == null ? "未知" : passage.getName());
                    passageMap.put(taskPackets.getFinalPassageId(), taskPackets.getPassageName());
                }
            }

        }
        provinceMap = null;
        passageMap = null;

        return childList;
    }

    @Override
    public boolean updateMmsContent(long sid, String content) {
        try {
            taskMapper.updateContent(sid, content);
            taskPacketsMapper.updateContent(sid, content);
            return true;
        } catch (Exception e) {
            logger.error("更新彩信[" + sid + "]内容失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return false;
    }

    @Override
    public boolean batchUpdateMmsContent(String sidArrays, String content) {
        try {
            String[] array = sidArrays.split(",");
            for (String sid : array) {
                taskMapper.updateContent(Long.valueOf(sid), content);
                taskPacketsMapper.updateContent(Long.valueOf(sid), content);
            }
            return true;
        } catch (Exception e) {
            logger.error("批量修改内容失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return false;
    }

    @Override
    public void save(MmsMtTask task) {
        if (task == null) {
            logger.error("任务数据 [ " + JSON.toJSONString(task) + "] 为空，处理失败");
            return;
        }

        int effect = taskMapper.insertSelective(task);
        if (effect > 0) {
            if (CollectionUtils.isNotEmpty(task.getPackets())) {
                taskPacketsMapper.batchInsert(task.getPackets());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean savePackets(MmsMtTaskPackets packetes) {
        try {
            packetes.setCreateTime(new Date());
            return taskPacketsMapper.insertSelective(packetes) > 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public MmsMtTask getTaskBySid(Long sid) {
        return taskMapper.selectBySid(sid);
    }

    @Override
    public boolean update(MmsMtTask task) {
        return taskMapper.updateByPrimaryKey(task) > 0;
    }

    @Override
    public boolean updateStatus(long id, int status) {
        try {
            return taskMapper.updateApproveStatus(id, status) > 0;
        } catch (Exception e) {
            logger.error("根据任务ID更新审批状态失败", e);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean changeTaskPacketsPassage(long taskPacketsId, int passageId) {
        try {
            MmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(taskPacketsId);
            if (packets == null) {
                logger.error("子任务信息未找到，请检查，子任务ID: {}", taskPacketsId);
                return false;
            }

            // 根据通道ID查询 通道参数（针对发送类型）
            MmsPassageParameter passageParameter = getPassageParameter(passageId);
            if (passageParameter == null) {
                logger.error("通道ID：{} 未找到相关参数信息", passageId);
                return false;
            }

            packets.setFinalPassageId(passageId);
            packets.setPassageCode(passageParameter.getPassageCode());
            packets.setPassageProtocol(passageParameter.getProtocol());
            packets.setPassageParameter(passageParameter.getParams());
            packets.setPosition(passageParameter.getPosition());
            packets.setSuccessCode(passageParameter.getSuccessCode());
            packets.setPassageUrl(passageParameter.getUrl());
            packets.setResultFormat(passageParameter.getResultFormat());

            // 如果不仅仅包含 通道问题，切换通道后需要人工审核后才重入队列
            if (!isOnlyContainPassageError(packets)) {
                packets.setStatus(PacketsApproveStatus.WAITING.getCode());
                return taskPacketsMapper.updateByPrimaryKeySelective(packets) > 0;
            }

            packets.setStatus(PacketsApproveStatus.HANDLING_COMPLETE.getCode());
            boolean isOk = taskPacketsMapper.updateByPrimaryKeySelective(packets) > 0;
            if (isOk) {

                // 切换通道后直接 重新放置队列中
                sendPacketsToQueue(packets);
            }
            return isOk;

        } catch (Exception e) {
            logger.error("任务切换通道失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 根据通道ID获取通道相关参数信息
     *
     * @param passageId
     * @return
     */
    private MmsPassageParameter getPassageParameter(int passageId) {
        MmsPassage passage = mmsPassageService.findById(passageId);
        if (passage == null) {
            return null;
        }

        for (MmsPassageParameter parameter : passage.getParameterList()) {
            if (parameter.getCallType() == PassageCallType.DATA_SEND.getCode()) {
                parameter.setPassageCode(passage.getCode());
                return parameter;
            }
        }
        return null;
    }

    /**
     * TODO 任务是否仅仅包含 '通道错误'
     *
     * @param packets
     * @return
     */
    private boolean isOnlyContainPassageError(MmsMtTaskPackets packets) {
        if (StringUtils.isEmpty(packets.getForceActions())) {
            return false;
        }

        if (!packets.getForceActions().contains(String.valueOf(PacketsActionActor.BROKEN.getActor()))) {
            return false;
        }

        char[] actions = packets.getActions();
        // 将通道不可用操作符 暂时赋值为 通过
        actions[PacketsActionPosition.PASSAGE_NOT_AVAIABLE.getPosition()] = PacketsActionActor.AVAIABLE.getActor();

        return !String.valueOf(actions).contains(String.valueOf(PacketsActionActor.BROKEN.getActor()));

    }

    @Override
    public boolean updateForceActions(long taskPacketsId, String actions) {
        MmsMtTaskPackets packets = new MmsMtTaskPackets();
        packets.setId(taskPacketsId);
        packets.setForceActions(actions);
        try {
            return taskPacketsMapper.updateByPrimaryKeySelective(packets) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean updateTaskPacketsStatus(long taskPacketsId, int status) {
        try {
            int result = taskPacketsMapper.updateStatusById(taskPacketsId, status);
            if (result <= 0) {
                return false;
            }

            MmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(taskPacketsId);

            // 查询是否所有的子任务状态均不为“待审批”，否则修改主任务状态为 审批通过
            int count = taskPacketsMapper.selectWaitingCount(packets.getSid());
            if (count == 0) {
                taskMapper.updateApproveStatusBySid(packets.getSid(), status);
            }

            // 如果状态为 手动审批通过 （页面操作人员点击），则重新发送到消息队列
            if (status == PacketsApproveStatus.HANDLING_COMPLETE.getCode()) {
                return sendPacketsToQueue(packets);
            }
        } catch (Exception e) {
            logger.error("更改子任务状态失败", e);
            throw new RuntimeException(e);
        }

        return false;
    }

    @Override
    public MmsMtTaskPackets getTaskPacketsById(long id) {
        return taskPacketsMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void batchSave(List<MmsMtTask> tasks, List<MmsMtTaskPackets> taskPackets) throws RuntimeException {
        try {
            if (CollectionUtils.isEmpty(tasks)) {
                return;
            }

            long start = System.currentTimeMillis();
            int result = 0;
            if (CollectionUtils.isNotEmpty(tasks)) {
                result = taskMapper.batchInsert(tasks);
                if (CollectionUtils.isNotEmpty(taskPackets) && result > 0) {
                    result = taskPacketsMapper.batchInsert(taskPackets);
                }
            }

            logger.info("处理任务耗时：{} 共处理 ：{} 个， 子任务： {} 个", (System.currentTimeMillis() - start), tasks.size(),
                        taskPackets.size());

            if (result == 0) {
                throw new RuntimeException("数据执行失败");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateMainTaskByForcePass(long sid) {
        try {
            taskPacketsMapper.updateStatusBySid(sid, PacketsApproveStatus.HANDLING_COMPLETE.getCode());

            // 发送到待提交队列
            mmsMtSubmitService.sendToSubmitQueue(taskPacketsMapper.selectBySid(sid));

            return true;
        } catch (Exception e) {
            logger.error("主任务强制通过异常！", e);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean doRejectInTask(String taskIds) {
        if (StringUtils.isEmpty(taskIds)) {
            logger.warn("主任务ID集合数据为空 ： {}", taskIds);
            return false;
        }

        String[] taskIdsArray = taskIds.split(",");
        if (taskIdsArray.length == 0) {
            logger.warn("主任务ID集合数据为空 ： {}", taskIds);
            return false;
        }

        List<String> finalTaskIds = null;

        try {
            finalTaskIds = filterTaskExecuting(MmsRepeatSubmitConstant.DOING_TASK_REJECT, taskIdsArray);
            if (CollectionUtils.isEmpty(finalTaskIds)) {
                logger.info("过滤执行任务后无可用任务ID可用，均在执行中，忽略本次");
                return true;
            }

            List<MmsMtTask> finalTasks = taskMapper.selectTaskByIds(finalTaskIds);
            if (CollectionUtils.isEmpty(finalTasks)) {
                logger.warn("任务ID [" + finalTaskIds + "] 未找到相关任务数据");
                return false;
            }

            return asyncTask(finalTasks, PacketsApproveStatus.REJECT).getOk();

        } catch (Exception e) {
            logger.error("主任务驳回失败", e);
            throw new RuntimeException(e);
        } finally {
            flushTaskDoingFlag(MmsRepeatSubmitConstant.DOING_TASK_REJECT, finalTaskIds);
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean doRejectInTaskPackets(long packetsId) {
        try {
            MmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(packetsId);
            if (packets == null) {
                return false;
            }

            MmsMtTask task = taskMapper.selectBySid(packets.getSid());
            if (task == null) {
                return false;
            }

            mmsMtTaskForkService.copyUserCustomProperties(task, packets);
            mmsMtTaskForkService.sendRejectPackageQueue(packets);

            int result = taskPacketsMapper.updateStatusById(packets.getId(), PacketsApproveStatus.REJECT.getCode());

            // 查询是否所有的子任务状态均不为“待审批”，否则修改主任务状态为 审批通过
            int count = taskPacketsMapper.selectWaitingCount(packets.getSid());
            if (count == 0) {
                updateStatus(packets.getSid(), PacketsApproveStatus.REJECT.getCode());
            }

            return result > 0;
        } catch (Exception e) {
            logger.error("子任务驳回失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 根据提交类型获取队列名称（批量/点对点/模板点对点）
     *
     * @param task
     * @return
     */
    private String getQueueNameBySubmitType(MmsMtTask task) {
        return RabbitConstant.MQ_MMS_MT_WAIT_PROCESS;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean doRePackets(long id) {
        try {
            // 根据主任务ID查询任务信息
            MmsMtTask task = taskMapper.selectByPrimaryKey(id);
            // 根据ID删除主任务信息和 子任务信息
            int result = taskPacketsMapper.deleteBySid(task.getSid());
            if (result <= 0) {
                throw new RuntimeException("重新分包删除子任务失败");
            }
            result = taskMapper.deleteByPrimaryKey(task.getId());
            // 蒋主任务发送 分包队列，重新分包
            if (result > 0) {
                rabbitTemplate.convertAndSend(getQueueNameBySubmitType(task), task,
                                              new CorrelationData(task.getSid().toString()));
            }

            return result > 0;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public boolean batchDoRePackets(String mainTaskIds) {
        try {
            String[] idArray = mainTaskIds.split(",");
            for (String id : idArray) {
                doRePackets(Long.valueOf(id));
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public MmsMtTask findById(long id) {
        return taskMapper.selectByPrimaryKey(id);
    }

    @Override
    public PaginationVo<MmsMtTask> findAll(int userId, String sid, String title, Long start, Long end,
                                           String currentPage) {
        int _currentPage = PaginationVo.parse(currentPage);
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (!StringUtils.isEmpty(sid)) {
            params.put("sid", sid);
        }
        if (start != null && start > 0) {
            params.put("startDate", start);
        }
        if (end != null && end > 0) {
            params.put("endDate", end);
        }
        if (!StringUtils.isEmpty(title)) {
            params.put("title", title);
        }
        int totalRecord = taskMapper.findCount(params);
        if (totalRecord == 0) {
            return null;
        }

        params.put("start", PaginationVo.getStartPage(_currentPage));
        params.put("end", PaginationVo.DEFAULT_RECORD_PER_PAGE);
        List<MmsMtTask> list = taskMapper.findList(params);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return new PaginationVo<>(list, _currentPage, totalRecord);
    }

    @Override
    public boolean isTaskChildrenHasPassageError(Long sid) {
        return taskPacketsMapper.selectPassageErrorCount(sid) > 0;
    }

    @Override
    @Transactional
    public ResponseMessage doTaskApproved(String taskIds) {
        if (StringUtils.isEmpty(taskIds)) {
            return new ResponseMessage(ResponseMessage.ERROR_CODE, "操作数据为空", false);
        }

        String[] idArray = taskIds.split(",");
        if (idArray.length == 0) {
            return new ResponseMessage(ResponseMessage.ERROR_CODE, "操作数据为空", false);
        }

        List<String> finalTaskIds = null;
        try {
            // 检验taskIds是否执行中，并且返回过滤执行中的数据
            finalTaskIds = filterTaskExecuting(MmsRepeatSubmitConstant.DOING_TASK_APPROVED, idArray);
            if (CollectionUtils.isEmpty(finalTaskIds)) {
                return new ResponseMessage("任务ID均在其他线程执行中，本次忽略");
            }

            List<MmsMtTask> tasks = taskMapper.selectTaskByIds(finalTaskIds);
            if (CollectionUtils.isEmpty(tasks)) {
                return new ResponseMessage(ResponseMessage.ERROR_CODE, "任务数据为空", false);
            }

            return asyncTask(tasks, PacketsApproveStatus.HANDLING_COMPLETE);
        } catch (Exception e) {
            logger.error("任务ID [" + taskIds + "] 批量审批失败", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return new ResponseMessage(ResponseMessage.ERROR_CODE, "任务审批失败", false);
        } finally {
            flushTaskDoingFlag(MmsRepeatSubmitConstant.DOING_TASK_APPROVED, finalTaskIds);
        }
    }

    /**
     * TODO 根据任务执行异步并行任务
     * 
     * @param tasks
     * @param status HANDLING_COMPLETE/REJECT
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private ResponseMessage asyncTask(List<MmsMtTask> tasks, PacketsApproveStatus status) throws InterruptedException,
                                                                                         ExecutionException {
        // 根据并行阈值分发任务数据
        List<List<MmsMtTask>> distributeTasks = mmsMtTaskForkService.distributeTasks(tasks, status);
        if (CollectionUtils.isEmpty(distributeTasks)) {
            return new ResponseMessage(ResponseMessage.ERROR_CODE, "并行任务分发数据为空", false);
        }

        List<Future<ResponseMessage>> futureList = new ArrayList<>();
        for (List<MmsMtTask> executeTasks : distributeTasks) {
            if (PacketsApproveStatus.HANDLING_COMPLETE == status) {
                // 并行异步审批
                futureList.add(mmsMtTaskForkService.parallelTaskApproved(executeTasks));
            } else if (PacketsApproveStatus.REJECT == status) {
                // 并行异步驳回
                futureList.add(mmsMtTaskForkService.parallelTaskRejected(executeTasks));
            }
        }

        int successCounter = 0;
        // String message = "";
        for (Future<ResponseMessage> future : futureList) {
            successCounter += future.get().getCode();
        }

        return new ResponseMessage(successCounter, "总任务 [" + tasks.size() + "] 本次共处理[" + successCounter + "]",
                                   successCounter > 0);
    }

    /**
     * TODO 发送分包数据至网关队列
     * 
     * @param packets
     * @return
     */
    private boolean sendPacketsToQueue(MmsMtTaskPackets packets) {
        if (packets == null) {
            logger.warn("待发送网关队列数据为空");
            return false;
        }

        try {
            MmsMtTask task = taskMapper.selectBySid(packets.getSid());

            // 复制用户传递相关值
            mmsMtTaskForkService.copyUserCustomProperties(task, packets);

            mmsMtSubmitService.sendToSubmitQueue(Arrays.asList(packets));

            return true;
        } catch (Exception e) {
            logger.error("待发送网关队列数据入队列失败", e);
            return false;
        }
    }

    /**
     * TODO 根据短信内容和匹配模式查询可匹配的待处理任务信息
     * 
     * @param title
     * @param isLikePattern
     * @return
     */
    private Map<String[], List<MmsMtTask>> findTasksByTitle(String title, boolean isLikePattern) {
        if (StringUtils.isEmpty(title)) {
            return null;
        }

        // 查询所有待处理的任务（因为目前表中的任务 content 字段未创建索引，顾采用JAVA匹配）
        List<MmsMtTask> waittingTaskList = taskMapper.selectWaitDealTaskList();
        if (CollectionUtils.isEmpty(waittingTaskList)) {
            return null;
        }

        List<String> matchedTaskIds = new ArrayList<>(waittingTaskList.size());
        List<MmsMtTask> matchedTaskList = new ArrayList<>(waittingTaskList.size());
        for (MmsMtTask waitingTask : waittingTaskList) {
            if (!isTitleMatched(waitingTask.getTitle(), title, isLikePattern)) {
                continue;
            }

            matchedTaskList.add(waitingTask);
            matchedTaskIds.add(waitingTask.getId() + "");
        }

        if (CollectionUtils.isEmpty(matchedTaskIds)) {
            logger.info("彩信标题: [ " + title + "] 未匹配到任何待处理任务");
            return null;
        }

        Map<String[], List<MmsMtTask>> tasksMap = new HashMap<>();
        tasksMap.put(matchedTaskIds.toArray(new String[] {}), matchedTaskList);

        return tasksMap;
    }

    @Override
    public int approvedBySameTitle(String title, boolean isLikePattern) {
        Map<String[], List<MmsMtTask>> taskMap = findTasksByTitle(title, isLikePattern);
        if (MapUtils.isEmpty(taskMap)) {
            return 0;
        }

        String[] matchedTaskIds = taskMap.keySet().iterator().next();

        // 判断任务是否执行中（其他线程）
        List<String> finalTaskIds = filterTaskExecuting(MmsRepeatSubmitConstant.DOING_TASK_APPROVED, matchedTaskIds);
        if (CollectionUtils.isEmpty(finalTaskIds)) {
            logger.info("过滤执行任务后无可用任务ID可用，均在执行中，忽略本次");
            return matchedTaskIds.length;
        }

        try {
            // code 为处理最终成功数量
            return asyncTask(taskMap.get(matchedTaskIds), PacketsApproveStatus.HANDLING_COMPLETE).getCode();

        } catch (Exception e) {
            logger.error("批量审核错误", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        } finally {
            flushTaskDoingFlag(MmsRepeatSubmitConstant.DOING_TASK_APPROVED, finalTaskIds);
        }
    }

    @Override
    public int rejectBySameTitle(String title, boolean isLikePattern) {
        Map<String[], List<MmsMtTask>> taskMap = findTasksByTitle(title, isLikePattern);
        if (MapUtils.isEmpty(taskMap)) {
            return 0;
        }

        String[] matchedTaskIds = taskMap.keySet().iterator().next();

        // 判断任务是否执行中（其他线程）
        List<String> finalTaskIds = filterTaskExecuting(MmsRepeatSubmitConstant.DOING_TASK_REJECT, matchedTaskIds);
        if (CollectionUtils.isEmpty(finalTaskIds)) {
            logger.info("过滤执行任务后无可用任务ID可用，均在执行中，忽略本次");
            return matchedTaskIds.length;
        }

        try {
            // code 为处理最终成功数量
            return asyncTask(taskMap.get(matchedTaskIds), PacketsApproveStatus.REJECT).getCode();

        } catch (Exception e) {
            logger.error("批量驳回错误", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return -1;
        } finally {
            flushTaskDoingFlag(MmsRepeatSubmitConstant.DOING_TASK_REJECT, finalTaskIds);
        }
    }

    /**
     * TODO 彩信内容是否匹配
     *
     * @param sourceTitle 原短信内容（数据库）
     * @param targetTitle 页面传递的短信内容
     * @param isLikePattern 是否为模糊匹配模式
     * @return
     */
    private static boolean isTitleMatched(String sourceTitle, String targetTitle, boolean isLikePattern) {
        return isLikePattern ? StringUtils.isNotBlank(sourceTitle) && sourceTitle.contains(targetTitle) : StringUtils.isNotBlank(sourceTitle)
                                                                                                          && sourceTitle.equals(targetTitle);

    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean changeTaskPacketsPassage(String taskIds, int expectPassageId) {
        if (StringUtils.isEmpty(taskIds) || expectPassageId == 0) {
            logger.warn("主任务ID集合数据为空 ： {}，或切换通道ID未0 ： {}", taskIds, expectPassageId);
            return false;
        }

        String[] taskIdsArray = taskIds.split(",");
        if (taskIdsArray.length == 0) {
            logger.warn("主任务ID集合数据为空 ： {}", taskIds);
            return false;
        }

        try {
            MmsPassageParameter passageParameter = getPassageParameter(expectPassageId);
            if (passageParameter == null) {
                logger.error("切换通道ID：{} 未找到相关参数信息", expectPassageId);
                return false;
            }

            boolean isOk = false;
            // 迭代主任务数据
            for (String taskId : taskIdsArray) {
                if (StringUtils.isEmpty(taskId)) {
                    continue;
                }

                MmsMtTask task = findById(Long.valueOf(taskId));
                if (task == null) {
                    continue;
                }

                List<MmsMtTaskPackets> packetsList = taskPacketsMapper.selectBySid(task.getSid());
                for (MmsMtTaskPackets packets : packetsList) {
                    if (!resetFinalPassage(task, packets, passageParameter)) {
                        logger.error("子任务：{} 切换通道：{} 失败", packets.getId(), expectPassageId);
                        throw new RuntimeException("切换通道失败");
                    }
                }

                // 切换通道后直接 重新放置队列中
                isOk = mmsMtSubmitService.sendToSubmitQueue(packetsList);
            }

            return isOk;
        } catch (Exception e) {
            logger.error("任务切换通道失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 重新设置通道参数相关信息，状态等信息（切换新通道后重置最终任务发送的通道信息）
     *
     * @param task
     * @param packets
     * @param passageParameter
     * @return
     */
    private boolean resetFinalPassage(MmsMtTask task, MmsMtTaskPackets packets, MmsPassageParameter passageParameter) {
        try {
            packets.setFinalPassageId(passageParameter.getPassageId());
            packets.setPassageCode(passageParameter.getPassageCode());
            packets.setPassageProtocol(passageParameter.getProtocol());
            packets.setPassageParameter(passageParameter.getParams());
            packets.setPosition(passageParameter.getPosition());
            packets.setSuccessCode(passageParameter.getSuccessCode());
            packets.setPassageUrl(passageParameter.getUrl());
            packets.setResultFormat(passageParameter.getResultFormat());

            // 设置用户短信传递的参数信息（临时信息，非持久化信息，用于队列数据传递）
            packets.setCallback(task.getCallback());
            packets.setAttach(task.getAttach());
            packets.setUserId(task.getUserId());
            packets.setExtNumber(task.getExtNumber());

            // 如果不仅仅包含 通道问题，切换通道后需要人工审核后才重入队列
            if (!isOnlyContainPassageError(packets)) {
                packets.setStatus(PacketsApproveStatus.WAITING.getCode());
            } else {
                packets.setStatus(PacketsApproveStatus.HANDLING_COMPLETE.getCode());
            }

            return taskPacketsMapper.updateByPrimaryKeySelective(packets) > 0;
        } catch (Exception e) {
            logger.error("任务切换通道失败", e);
            return false;
        }
    }

    /**
     * TODO 检查任务是否执行中
     *
     * @param repeatGroupKey 执行中重复KEY组名
     * @param taskIds 任务IDS（可能为多个）
     * @return
     */
    private List<String> filterTaskExecuting(String repeatGroupKey, String... taskIds) {
        // edit by 20180416 介于当前分布式zookeeper 经常超时，待查，而分布式还未上暂时切换为JVM的可重入锁
        // Lock lock = new ZookeeperLock(zkConnectUrl, zkSessionTimeout, zkConnectTimeout, zkLockNode,
        // CURRENT_BUSINESS_LOCK_NODE);
        // 分布式锁开启
        lock.lock();
        try {
            List<String> finalTaskIds = new ArrayList<>();
            for (String taskId : taskIds) {
                if (stringRedisTemplate.hasKey(repeatGroupKey + taskId)) {
                    continue;
                }

                // 如果检查当前REDIS中不存在TASK_ID对应的标志则设置数据，有效期3分钟
                stringRedisTemplate.opsForValue().set(repeatGroupKey + taskId, System.currentTimeMillis() + "", 3,
                                                      TimeUnit.MINUTES);
                finalTaskIds.add(taskId);
            }

            return finalTaskIds;
        } catch (Exception e) {
            logger.error("检查任务执行操作失败，失败原因：" + e.getMessage());
            return null;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 清除执行中KEY标识
     *
     * @param repeatGroupKey
     * @param taskIds
     */
    private void flushTaskDoingFlag(String repeatGroupKey, List<String> taskIds) {
        if (CollectionUtils.isEmpty(taskIds)) {
            return;
        }

        try {
            List<String> keys = new ArrayList<>();
            for (String taskId : taskIds) {
                keys.add(repeatGroupKey + taskId);
            }

            stringRedisTemplate.delete(keys);

        } catch (Exception e) {
            logger.error("删除任务执行中标识操作失败，失败原因：" + e.getMessage());
        }
    }

}
