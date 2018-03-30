package com.huashi.sms.task.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.huashi.common.settings.context.SettingsContext.PushConfigStatus;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.user.model.P2pBalanceResponse;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserPassageService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.user.service.IUserSmsConfigService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.util.IdGenerator;
import com.huashi.common.util.MobileNumberCatagoryUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PassageCallType;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.SmsPushCode;
import com.huashi.constants.ResponseMessage;
import com.huashi.sms.config.cache.redis.constant.SmsRepeatSubmitConstant;
import com.huashi.sms.config.rabbit.constant.ActiveMqConstant;
import com.huashi.sms.config.zookeeper.ZookeeperLock;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.record.service.ISmsMtProcessFailedService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.settings.service.IForbiddenWordsService;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;
import com.huashi.sms.task.context.TaskContext.MessageSubmitStatus;
import com.huashi.sms.task.context.TaskContext.PacketsActionActor;
import com.huashi.sms.task.context.TaskContext.PacketsActionPosition;
import com.huashi.sms.task.context.TaskContext.PacketsApproveStatus;
import com.huashi.sms.task.context.TaskContext.TaskSubmitType;
import com.huashi.sms.task.dao.SmsMtTaskMapper;
import com.huashi.sms.task.dao.SmsMtTaskPacketsMapper;
import com.huashi.sms.task.domain.SmsMtTask;
import com.huashi.sms.task.domain.SmsMtTaskPackets;
import com.huashi.sms.template.context.TemplateContext;
import com.huashi.sms.template.domain.MessageTemplate;
import com.huashi.sms.template.service.ISmsTemplateService;

/**
 * TODO 短信下行任务服务实现
 *
 * @author zhengying
 * @version V1.0
 * @date 2016年10月27日 下午12:55:56
 */
@Service
public class SmsMtTaskService implements ISmsMtTaskService {

    @Autowired
    private IdGenerator                  idGenerator;
    @Reference
    private IUserService                 userService;
    @Reference
    private IUserBalanceService          userBalanceService;
    @Autowired
    private SmsMtTaskMapper              taskMapper;
    @Autowired
    private SmsMtTaskPacketsMapper       smsMtTaskPacketsMapper;
    @Reference
    private IUserPassageService          userPassageService;
    @Autowired
    private SmsMtTaskPacketsMapper       taskPacketsMapper;
    @Reference
    protected IUserSmsConfigService      userSmsConfigService;
    @Reference
    private IPushConfigService           pushConfigService;
    @Autowired
    protected IForbiddenWordsService     forbiddenWordsService;
    @Autowired
    protected ISmsMtProcessFailedService smsMtProcessFailedService;
    @Autowired
    protected ISmsMobileBlackListService mobileBlackListService;
    @Autowired
    private ISmsPassageService           smsPassageService;
    @Reference
    private IProvinceService             provinceService;
    @Autowired
    private ISmsMtSubmitService          smsMtSubmitService;
    @Autowired
    private ISmsTemplateService          smsTemplateService;

    @Autowired
    private JmsMessagingTemplate         jmsMessagingTemplate;
    @Resource
    private StringRedisTemplate          stringRedisTemplate;

    @Value("${zk.connect}")
    private String                       zkConnectUrl;

    @Value("${zk.locknode}")
    private String                       zkLockNode;

    /**
     * 当前业务锁节点名称
     */
    private static final String          CURRENT_BUSINESS_LOCK_NODE = "task_lock";

    // @PostConstruct
    // public void setConfirmCallback() {
    // // rabbitTemplate如果为单例的话，那回调就是最后设置的内容
    // rabbitTemplate.setConfirmCallback(this);
    // }

    protected Logger                     logger                     = LoggerFactory.getLogger(getClass());

    @Override
    public BossPaginationVo<SmsMtTask> findPage(Map<String, Object> condition) {
        changeTimestampeParamsIfExists(condition);

        BossPaginationVo<SmsMtTask> page = new BossPaginationVo<>();
        page.setCurrentPage(Integer.parseInt(condition.getOrDefault("currentPage", 1).toString()));
        int total = taskMapper.findCount(condition);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        condition.put("start", page.getStartPosition());
        condition.put("end", page.getPageSize());

        List<SmsMtTask> dataList = taskMapper.findList(condition);
        for (SmsMtTask record : dataList) {
            record.setUserModel(userService.getByUserId(record.getUserId()));

            if (StringUtils.isBlank(record.getForbiddenWords())) {
                continue;
            }

            record.setForbiddenWordLabels(forbiddenWordsService.getLabelByWords(record.getForbiddenWords()));
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
    public Integer getWaitSmsTaskCount() {
        return taskMapper.selectWaitDealTaskCount();
    }

    @Override
    public List<SmsMtTaskPackets> findChildTaskBySid(long sid) {
        List<SmsMtTaskPackets> childList = taskPacketsMapper.findChildBySid(sid);

        Province province;
        Map<Integer, String> provinceMap = new HashMap<>();
        for (SmsMtTaskPackets task : childList) {
            // if (task.getUserId() != null) {
            // UserModel userModel = userService.getByUserId(task.getUserId());
            // task.setUserModel(userModel);
            // }

            if (task.getProvinceCode() == null) {
                continue;
            }

            if (provinceMap.containsKey(task.getProvinceCode())) {
                task.setProvinceName(provinceMap.get(task.getProvinceCode()));
            } else {
                // 根据省份代码查询省份名称
                province = provinceService.get(task.getProvinceCode());
                task.setProvinceName(province == null ? "未知" : province.getName());
                provinceMap.put(task.getProvinceCode(), task.getProvinceName());
            }
        }
        // province = null;
        // provinceMap = null;

        return childList;
    }

    @Override
    public boolean updateSmsContent(long sid, String content) {
        try {
            taskMapper.updateContent(sid, content);
            taskPacketsMapper.updateContent(sid, content);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return false;
    }

    @Override
    public boolean batchUpdateSmsContent(String sidArrays, String content) {
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
    public boolean save(SmsMtTask task) {
        task.setCreateTime(new Date());

        return taskMapper.insertSelective(task) > 0;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean savePackets(SmsMtTaskPackets packetes) {
        try {
            packetes.setCreateTime(new Date());
            return taskPacketsMapper.insertSelective(packetes) > 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SmsMtTask getTaskBySid(Long sid) {
        return taskMapper.selectBySid(sid);
    }

    @Override
    public boolean update(SmsMtTask task) {
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
            SmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(taskPacketsId);
            if (packets == null) {
                logger.error("子任务信息未找到，请检查，子任务ID: {}", taskPacketsId);
                return false;
            }

            // 根据通道ID查询 通道参数（针对发送类型）
            SmsPassageParameter passageParameter = getPassageParameter(passageId);
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
                resendToQueue(packets);
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
    private SmsPassageParameter getPassageParameter(int passageId) {
        SmsPassage passage = smsPassageService.findById(passageId);
        if (passage == null) {
            return null;
        }

        for (SmsPassageParameter parameter : passage.getParameterList()) {
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
    private boolean isOnlyContainPassageError(SmsMtTaskPackets packets) {
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
        SmsMtTaskPackets packets = new SmsMtTaskPackets();
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

            SmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(taskPacketsId);

            // 查询是否所有的子任务状态均不为“待审批”，否则修改主任务状态为 审批通过
            int count = taskPacketsMapper.selectWaitingCount(packets.getSid());
            if (count == 0) {
                taskMapper.updateApproveStatusBySid(packets.getSid(), status);
            }

            // 如果状态为 手动审批通过 （页面操作人员点击），则重新发送到消息队列
            if (status == PacketsApproveStatus.HANDLING_COMPLETE.getCode()) {
                return resendToQueue(packets);
            }
        } catch (Exception e) {
            logger.error("更改子任务状态失败", e);
            throw new RuntimeException(e);
        }

        return false;
    }

    /**
     * TODO 重发发送到队列中
     *
     * @param source
     * @return
     */
    private boolean resendToQueue(SmsMtTaskPackets source) {
        return resendToQueue(source, null);
    }

    /**
     * TODO 重入待提交队列(每个用户ID单独)
     *
     * @param source
     * @param task
     * @return
     */
    private boolean resendToQueue(SmsMtTaskPackets source, SmsMtTask task) {
        task = task == null ? taskMapper.selectBySid(source.getSid()) : task;
        if (task == null) {
            logger.error("主任务未查到相关数据,SID：{}", source.getSid());
            return false;
        }
        source.setCallback(task.getCallback());
        source.setAttach(task.getAttach());
        source.setUserId(task.getUserId());
        source.setExtNumber(task.getExtNumber());
        source.setFee(task.getFee());

        List<SmsMtTaskPackets> list = new ArrayList<>();
        list.add(source);

        // 发送到待提交队列
        return smsMtSubmitService.sendToSubmitQueue(list);
    }

    @Override
    public SmsMtTaskPackets getTaskPacketsById(long id) {
        return taskPacketsMapper.selectByPrimaryKey(id);
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public void batchSave(List<SmsMtTask> tasks, List<SmsMtTaskPackets> taskPackets) throws RuntimeException {
        try {
            if (CollectionUtils.isEmpty(tasks)) {
                return;
            }

            int result = 0;
            if (CollectionUtils.isNotEmpty(tasks)) {
                result = taskMapper.batchInsert(tasks);
                if (CollectionUtils.isNotEmpty(taskPackets) && result > 0) {
                    result = taskPacketsMapper.batchInsert(taskPackets);
                }
            }

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
            smsMtSubmitService.sendToSubmitQueue(taskPacketsMapper.findChildBySid(sid));

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
            finalTaskIds = filterTaskExecuting(SmsRepeatSubmitConstant.DOING_TASK_REJECT, taskIdsArray);
            if (CollectionUtils.isEmpty(finalTaskIds)) {
                logger.info("过滤执行任务后无可用任务ID可用，均在执行中，忽略本次");
                return true;
            }

            SmsMtTask task;
            int result = 0;
            for (String taskId : finalTaskIds) {
                // 根据主任务ID查询任务信息
                task = taskMapper.selectByPrimaryKey(Long.parseLong(taskId));

                // 更新主任务状态为“驳回”
                result += taskMapper.updateApproveStatus(Long.parseLong(taskId), PacketsApproveStatus.REJECT.getCode());

                List<SmsMtTaskPackets> packets = taskPacketsMapper.findChildBySid(task.getSid());
                if (CollectionUtils.isEmpty(packets)) {
                    continue;
                }

                for (SmsMtTaskPackets pt : packets) {
                    if (pt.getStatus() == PacketsApproveStatus.REJECT.getCode()) {
                        continue;
                    }

                    pt.setUserId(task.getUserId());
                    doRejectToQueue(pt);
                }

                if (result > 0) {
                    result = taskPacketsMapper.updateStatusBySid(task.getSid(), PacketsApproveStatus.REJECT.getCode());
                }
            }

            return result > 0;
        } catch (Exception e) {
            logger.error("主任务驳回失败", e);
            throw new RuntimeException(e);
        } finally {
            flushTaskDoingFlag(SmsRepeatSubmitConstant.DOING_TASK_REJECT, finalTaskIds);
        }
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean doRejectInTaskPackets(long packetsId) {
        try {
            SmsMtTaskPackets packets = taskPacketsMapper.selectByPrimaryKey(packetsId);
            if (packets == null) {
                return false;
            }

            SmsMtTask task = taskMapper.selectBySid(packets.getSid());
            if (task == null) {
                return false;
            }

            packets.setUserId(task.getUserId());
            packets.setAttach(task.getAttach());
            packets.setFee(task.getFee());
            packets.setCallback(task.getCallback());

            doRejectToQueue(packets);

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
     * TODO 驳回入库队列
     *
     * @param packets
     */
    private void doRejectToQueue(SmsMtTaskPackets packets) {
        String[] mobiles = packets.getMobile().split(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR);
        if (mobiles.length == 0) {
            return;
        }

        for (String m : mobiles) {
            SmsMtMessageSubmit submit = new SmsMtMessageSubmit();
            submit.setUserId(packets.getUserId());
            submit.setSid(packets.getSid());
            submit.setMobile(m);
            submit.setCmcp(CMCP.local(m).getCode());
            submit.setContent(packets.getContent());
            submit.setFee(packets.getFee());
            submit.setAttach(packets.getAttach());
            submit.setPassageId(PassageContext.EXCEPTION_PASSAGE_ID);
            submit.setCreateTime(new Date());
            submit.setCreateUnixtime(submit.getCreateTime().getTime());
            submit.setStatus(MessageSubmitStatus.SUCCESS.getCode());
            submit.setRemark(SmsPushCode.SMS_TASK_REJECT.getCode());
            submit.setPushErrorCode(SmsPushCode.SMS_TASK_REJECT.getCode());
            submit.setMsgId(packets.getSid().toString());
            submit.setCallback(packets.getCallback());
            submit.setProvinceCode(Province.PROVINCE_CODE_ALLOVER_COUNTRY);

            PushConfig pushConfig = pushConfigService.getPushUrl(packets.getUserId(),
                                                                 PlatformType.SEND_MESSAGE_SERVICE.getCode(),
                                                                 packets.getCallback());

            // 推送信息为固定地址或者每次传递地址才需要推送
            if (pushConfig != null && PushConfigStatus.NO.getCode() != pushConfig.getStatus()) {
                submit.setPushUrl(pushConfig.getUrl());
                submit.setNeedPush(true);
            }

            jmsMessagingTemplate.convertAndSend(ActiveMqConstant.MQ_SMS_MT_PACKETS_EXCEPTION, submit);
        }
    }

    /**
     * TODO 根据提交类型获取队列名称（批量/点对点/模板点对点）
     *
     * @param task
     * @return
     */
    private String getQueueNameBySubmitType(SmsMtTask task) {
        if (TaskSubmitType.BATCH_MESSAGE.getCode() == task.getSubmitType()) {
            return ActiveMqConstant.MQ_SMS_MT_WAIT_PROCESS;
        }

        if (StringUtils.isEmpty(task.getContent())) {
            throw new RuntimeException("点对点短信原报文数据为空，无法执行");
        }

        // 点对点报文内容解析
        List<JSONObject> list = JSON.parseObject(task.getContent(), new TypeReference<List<JSONObject>>() {
        });
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("点对点解析计费及短信内容失败");
        }

        // 如果任务是点对点或者模板点对点处理短信内容
        P2pBalanceResponse p2pBalanceResponse = null;

        if (TaskSubmitType.POINT_TO_POINT.getCode() == task.getSubmitType()) {
            p2pBalanceResponse = userBalanceService.calculateP2pSmsAmount(task.getUserId(),
                                                                          JSON.parseObject(task.getContent(),
                                                                                           new TypeReference<List<JSONObject>>() {
                                                                                           }));
        } else if (TaskSubmitType.TEMPLATE_POINT_TO_POINT.getCode() == task.getSubmitType()) {
            p2pBalanceResponse = userBalanceService.calculateP2pSmsAmount(task.getUserId(),
                                                                          JSON.parseObject(task.getContent(),
                                                                                           new TypeReference<List<JSONObject>>() {
                                                                                           }));
        }

        if (p2pBalanceResponse == null || CollectionUtils.isEmpty(p2pBalanceResponse.getP2pBodies())) {
            throw new RuntimeException("点对点解析计费及短信内容失败");
        }

        task.setP2pBody(task.getContent());
        task.setP2pBodies(p2pBalanceResponse.getP2pBodies());

        return ActiveMqConstant.MQ_SMS_MT_P2P_WAIT_PROCESS;
    }

    @Override
    @Transactional(rollbackFor = RuntimeException.class)
    public boolean doRePackets(long id) {
        try {
            // 根据主任务ID查询任务信息
            SmsMtTask task = taskMapper.selectByPrimaryKey(id);
            // 根据ID删除主任务信息和 子任务信息
            int result = taskPacketsMapper.deleteBySid(task.getSid());
            if (result <= 0) {
                throw new RuntimeException("重新分包删除子任务失败");
            }
            result = taskMapper.deleteByPrimaryKey(task.getId());
            // 蒋主任务发送 分包队列，重新分包
            if (result > 0) {
                jmsMessagingTemplate.convertAndSend(getQueueNameBySubmitType(task), task);
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
    public SmsMtTask findById(long id) {
        return taskMapper.selectByPrimaryKey(id);
    }

    @Override
    public PaginationVo<SmsMtTask> findAll(int userId, String sid, String content, Long start, Long end,
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
        if (!StringUtils.isEmpty(content)) {
            params.put("content", content);
        }
        int totalRecord = taskMapper.findCount(params);
        if (totalRecord == 0) {
            return null;
        }

        params.put("start", PaginationVo.getStartPage(_currentPage));
        params.put("end", PaginationVo.DEFAULT_RECORD_PER_PAGE);
        List<SmsMtTask> list = taskMapper.findList(params);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return new PaginationVo<>(list, _currentPage, totalRecord);
    }

    @Override
    public boolean isTaskChildrenHasPassageError(Long sid) {
        return smsMtTaskPacketsMapper.selectPassageErrorCount(sid) > 0;
    }

    @Override
    @Transactional
    public ResponseMessage doTaskApproved(String taskIds) {
        List<String> finalTaskIds = null;

        try {
            if (StringUtils.isEmpty(taskIds)) {
                return new ResponseMessage(ResponseMessage.ERROR_CODE, "操作数据为空", false);
            }

            String[] idArray = taskIds.split(",");
            if (idArray.length == 0) {
                return new ResponseMessage(ResponseMessage.ERROR_CODE, "操作数据为空", false);
            }

            // 检验taskIds是否执行中，并且返回过滤执行中的数据
            finalTaskIds = filterTaskExecuting(SmsRepeatSubmitConstant.DOING_TASK_APPROVED, idArray);
            if (CollectionUtils.isEmpty(finalTaskIds)) {
                logger.info("过滤执行任务后无可用任务ID可用，均在执行中，忽略本次");
                return new ResponseMessage("任务ID均在其他线程执行中，本次忽略");
            }

            List<SmsMtTask> tasks = taskMapper.selectTaskByIds(finalTaskIds);
            if (CollectionUtils.isEmpty(tasks)) {
                return new ResponseMessage(ResponseMessage.ERROR_CODE, "任务数据为空", false);
            }

            return doTaskBatchApproved(tasks);
        } catch (Exception e) {
            logger.error("批量审核错误", e);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new RuntimeException(String.format("taskIds:%s审批处理失败", taskIds));
        } finally {
            flushTaskDoingFlag(SmsRepeatSubmitConstant.DOING_TASK_APPROVED, finalTaskIds);
        }
    }

    /**
     * TODO 任务批量通过审批
     *
     * @param tasks
     * @return
     */
    private ResponseMessage doTaskBatchApproved(List<SmsMtTask> tasks) {
        for (SmsMtTask task : tasks) {
            if (task == null) {
                continue;
            }

            boolean isHasError = isTaskChildrenHasPassageError(task.getSid());
            if (isHasError) {
                return new ResponseMessage(ResponseMessage.ERROR_CODE,
                                           String.format("SID:%d 包含通道不可用数据", task.getSid()), false);
            }

            List<SmsMtTaskPackets> packetsList = findChildTaskBySid(task.getSid());
            int result;
            boolean isOk;
            for (SmsMtTaskPackets packets : packetsList) {
                if (packets.getStatus() == PacketsApproveStatus.AUTO_COMPLETE.getCode()
                    || packets.getStatus() == PacketsApproveStatus.HANDLING_COMPLETE.getCode()) {
                    continue;
                }

                // 根据子任务ID更新状态为“手动完成”
                result = taskPacketsMapper.updateStatusById(packets.getId(),
                                                            PacketsApproveStatus.HANDLING_COMPLETE.getCode());
                if (result == 0) {
                    return new ResponseMessage(ResponseMessage.ERROR_CODE, String.format("SID:%d 更新子任务状态失败",
                                                                                         task.getSid()), false);
                }

                // 重新发送到待提交包中时需要更新状态
                packets.setStatus(PacketsApproveStatus.HANDLING_COMPLETE.getCode());

                // 根据通道ID查询通道信息
                SmsPassage passage = smsPassageService.findById(packets.getFinalPassageId());
                if (passage == null) {
                    return new ResponseMessage(ResponseMessage.ERROR_CODE,
                                               String.format("SID:%d 更新子任务状态失败, 通道: %d 数据为空", task.getSid(),
                                                             packets.getId()), false);
                }

                packets.setPassageCode(passage.getCode());
                packets.setPassageSpeed(passage.getPacketsSize());
                packets.setPassageSignMode(passage.getSignMode());

                if (packets.getMessageTemplateId() != null
                    && packets.getMessageTemplateId() != TemplateContext.SUPER_TEMPLATE_ID) {
                    // 根据模板ID查询模板
                    MessageTemplate template = smsTemplateService.get(packets.getMessageTemplateId());
                    if (template == null) {
                        return new ResponseMessage(ResponseMessage.ERROR_CODE,
                                                   String.format("SID:%d 更新子任务状态失败, 模板: %d 数据为空", task.getSid(),
                                                                 packets.getMessageTemplateId()), false);
                    }

                    packets.setTemplateExtNumber(template.getExtNumber());
                }

                isOk = resendToQueue(packets, task);
                if (!isOk) {
                    throw new RuntimeException(String.format("SID:%d 审批处理失败", task.getSid()));
                }
            }

            // 更新主任务状态“手动通过”
            updateStatus(task.getId(), PacketsApproveStatus.HANDLING_COMPLETE.getCode());
        }

        return new ResponseMessage("操作成功");
    }

    @Override
    public int approvedBySameContent(String content, boolean isLikePattern) {
        if (StringUtils.isEmpty(content)) {
            return 0;
        }

        List<SmsMtTask> list = taskMapper.selectWaitDealTaskList();
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }

        List<String> taskIds = new ArrayList<>(list.size());
        List<SmsMtTask> tasks = new ArrayList<>();
        for (SmsMtTask task : list) {
            if (!isContentMatched(task.getFinalContent(), content, isLikePattern)) {
                continue;
            }

            tasks.add(task);
            taskIds.add(task.getId() + "");
        }

        if (CollectionUtils.isEmpty(tasks)) {
            logger.info("未匹配到任何待处理任务");
            return 0;
        }

        String[] matchedTaskIds = taskIds.toArray(new String[] {});

        // 判断任务是否执行中（其他线程）
        List<String> finalTaskIds = filterTaskExecuting(SmsRepeatSubmitConstant.DOING_TASK_APPROVED, matchedTaskIds);
        if (CollectionUtils.isEmpty(finalTaskIds)) {
            logger.info("过滤执行任务后无可用任务ID可用，均在执行中，忽略本次");
            return matchedTaskIds.length;
        }

        try {
            // 此处应该考虑多线程异步处理Fork/Join，如按照任务平均划分
            ResponseMessage response = doTaskBatchApproved(tasks);
            if (!response.getOk()) {
                return 0;
            }

        } catch (Exception e) {
            logger.error("批量审核错误", e);
            return -1;
        } finally {
            flushTaskDoingFlag(SmsRepeatSubmitConstant.DOING_TASK_APPROVED, finalTaskIds);
        }

        return tasks.size();
    }

    @Override
    public int rejectBySameContent(String content, boolean isLikePattern) {
        if (StringUtils.isEmpty(content)) {
            return 0;
        }

        List<SmsMtTask> list = taskMapper.selectWaitDealTaskList();
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }

        StringBuilder taskIdsBuilder = new StringBuilder();
        for (SmsMtTask task : list) {
            if (!isContentMatched(task.getFinalContent(), content, isLikePattern)) {
                continue;
            }

            taskIdsBuilder.append(task.getId()).append(",");
        }

        if (StringUtils.isEmpty(taskIdsBuilder)) {
            return 0;
        }

        // 以逗号隔开的任务ID值
        String taskIds = taskIdsBuilder.substring(0, taskIdsBuilder.length() - 1);
        try {
            boolean isOk = doRejectInTask(taskIds);
            if (!isOk) {
                return 0;
            }

        } catch (Exception e) {
            logger.error("批量审核错误", e);
            return -1;
        }

        return taskIds.split(",").length;
    }

    /**
     * TODO 短信内容是否匹配
     *
     * @param sourceContent 原短信内容（数据库）
     * @param targetContent 页面传递的短信内容
     * @param isLikePattern 是否为模糊匹配模式
     * @return
     */
    private static boolean isContentMatched(String sourceContent, String targetContent, boolean isLikePattern) {
        return isLikePattern ? StringUtils.isNotBlank(sourceContent) && sourceContent.contains(targetContent) : StringUtils.isNotBlank(sourceContent)
                                                                                                                && sourceContent.equals(targetContent);

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
            SmsPassageParameter passageParameter = getPassageParameter(expectPassageId);
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

                SmsMtTask task = findById(Long.valueOf(taskId));
                if (task == null) {
                    continue;
                }

                List<SmsMtTaskPackets> packetsList = findChildTaskBySid(task.getSid());
                for (SmsMtTaskPackets packets : packetsList) {
                    if (!resetTaskPassage(task, packets, passageParameter)) {
                        logger.error("子任务：{} 切换通道：{} 失败", packets.getId(), expectPassageId);
                        throw new RuntimeException("切换通道失败");
                    }
                }

                // 切换通道后直接 重新放置队列中
                isOk = smsMtSubmitService.sendToSubmitQueue(packetsList);
            }

            return isOk;
        } catch (Exception e) {
            logger.error("任务切换通道失败", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * TODO 重新设置通道参数相关信息，状态等信息
     *
     * @param task
     * @param packets
     * @param passageParameter
     * @return
     */
    private boolean resetTaskPassage(SmsMtTask task, SmsMtTaskPackets packets, SmsPassageParameter passageParameter) {
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
            packets.setFee(task.getFee());

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
    // private String[] checkTaskIsExecuting(String repeatGroupKey, String... taskIds) {
    // try {
    // List<String> needExecuteTasks = new ArrayList<>();
    // for (String taskId : taskIds) {
    // if (!stringRedisTemplate.opsForValue().setIfAbsent(repeatGroupKey + taskId,
    // System.currentTimeMillis() + "")) {
    // logger.warn("[" + repeatGroupKey + taskId + "] 执行中，忽略");
    // continue;
    // }
    //
    // // 为了任务ID可能出现未及时清理，导致死锁，加入超时限制
    // stringRedisTemplate.expire(repeatGroupKey + taskId, 3, TimeUnit.MINUTES);
    // needExecuteTasks.add(taskId);
    // }
    //
    // return CollectionUtils.isEmpty(needExecuteTasks) ? null : needExecuteTasks.toArray(new String[] {});
    //
    // } catch (Exception e) {
    // logger.error("检查任务执行操作失败，失败原因：" + e.getMessage());
    // return null;
    // }
    // }

    /**
     * TODO 检查任务是否执行中
     *
     * @param repeatGroupKey 执行中重复KEY组名
     * @param taskIds 任务IDS（可能为多个）
     * @return
     */
    private List<String> filterTaskExecuting(String repeatGroupKey, String... taskIds) {
        Lock lock = new ZookeeperLock(zkConnectUrl, zkLockNode, CURRENT_BUSINESS_LOCK_NODE);
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
