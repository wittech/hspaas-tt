package com.huashi.mms.task.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.settings.context.SettingsContext.PushConfigStatus;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.util.MobileNumberCatagoryUtil;
import com.huashi.constants.CommonContext.CMCP;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.constants.OpenApiCode.SmsPushCode;
import com.huashi.constants.ResponseMessage;
import com.huashi.mms.config.rabbit.constant.RabbitConstant;
import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.mms.record.domain.MmsMtMessageSubmit;
import com.huashi.mms.record.service.IMmsMtSubmitService;
import com.huashi.mms.task.dao.MmsMtTaskMapper;
import com.huashi.mms.task.dao.MmsMtTaskPacketsMapper;
import com.huashi.mms.task.domain.MmsMtTask;
import com.huashi.mms.task.domain.MmsMtTaskPackets;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.task.context.TaskContext.MessageSubmitStatus;
import com.huashi.sms.task.context.TaskContext.PacketsApproveStatus;
import com.huashi.sms.template.context.TemplateContext;

/**
 * TODO 任务并行分发服务（主要针对任务批量审批和批量驳回）
 * 
 * @author zhengying
 * @version V1.0
 * @date 2018年5月1日 下午11:57:08
 */
@Service
public class MmsMtTaskForkService {

    @Autowired
    private MmsMtTaskMapper        taskMapper;
    @Autowired
    private MmsMtTaskPacketsMapper taskPacketsMapper;
    @Autowired
    private IMmsMtSubmitService    mmsMtSubmitService;
    @Autowired
    private IMmsTemplateService    mmsTemplateService;
    @Autowired
    private IMmsPassageService     mmsPassageService;
    @Reference
    private IPushConfigService     pushConfigService;
    @Resource
    private RabbitTemplate         rabbitTemplate;

    private final Logger           logger = LoggerFactory.getLogger(getClass());

    @Value("${distribute.task.threshold.approve:50}")
    private int                    approveDistributeThreshold;
    @Value("${distribute.task.threshold.reject:100}")
    private int                    rejectDistributeThreshold;

    /**
     * TODO 将大任务按照指定阈值进行重新分组，从而进行任务分发/并发执行
     * 
     * @param tasks 总任务（一般比较大）
     * @param status 状态：HANDLING_COMPLETE/REJECT
     * @return
     */
    public List<List<MmsMtTask>> distributeTasks(List<MmsMtTask> tasks, PacketsApproveStatus status) {
        return distributeTasks(tasks,
                               PacketsApproveStatus.HANDLING_COMPLETE == status ? approveDistributeThreshold : rejectDistributeThreshold);
    }

    /**
     * TODO 根据阈值分发任务
     * 
     * @param tasks
     * @param threshold
     * @return
     */
    public List<List<MmsMtTask>> distributeTasks(List<MmsMtTask> tasks, int threshold) {
        List<List<MmsMtTask>> distributeTasks = new ArrayList<>();
        if (CollectionUtils.isEmpty(tasks)) {
            return distributeTasks;
        }

        if (tasks.size() < threshold) {
            distributeTasks.add(tasks);
            return distributeTasks;
        }

        return subGroup(tasks, threshold);
    }

    /**
     * TODO 数据重新分组
     * 
     * @param list
     * @param groupSize
     * @return
     */
    private static <T> List<List<T>> subGroup(List<T> list, int groupSize) {
        int totalCount = list.size();
        int pageCount;
        int m = totalCount % groupSize;

        if (m > 0) {
            pageCount = totalCount / groupSize + 1;
        } else {
            pageCount = totalCount / groupSize;
        }

        List<List<T>> totalList = new ArrayList<>();
        for (int i = 1; i <= pageCount; i++) {
            if (m == 0) {
                List<T> subList = list.subList((i - 1) * groupSize, groupSize * (i));
                totalList.add(subList);
            } else {
                if (i == pageCount) {
                    List<T> subList = list.subList((i - 1) * groupSize, totalCount);
                    totalList.add(subList);
                } else {
                    List<T> subList = list.subList((i - 1) * groupSize, groupSize * i);
                    totalList.add(subList);
                }
            }
        }

        return totalList;
    }

    /**
     * TODO 并发执行主任务集合审批逻辑
     * 
     * @param tasks
     * @return
     */
    @Async("asyncTaskExecutor")
    public Future<ResponseMessage> parallelTaskApproved(List<MmsMtTask> tasks) {
        try {
            return new AsyncResult<>(mulitiTaskApprove(tasks));
        } catch (Exception e) {
            logger.error("并行任务审批失败", e);
            return new AsyncResult<>(new ResponseMessage(ResponseMessage.ERROR_CODE, "并行任务审批失败", false));
        }
    }

    /**
     * TODO 根据SID查询是否包含子任务错误信息
     * 
     * @param sid
     * @return
     */
    private boolean isTaskChildrenHasPassageError(Long sid) {
        return taskPacketsMapper.selectPassageErrorCount(sid) > 0;
    }

    /**
     * TODO 任务批量通过审批
     *
     * @param tasks
     * @return
     */
    private ResponseMessage mulitiTaskApprove(List<MmsMtTask> tasks) {
        // 通道ID下的通道数据
        Map<Integer, MmsPassage> passageContainer = new HashMap<>();
        // 短信模板ID和短信模板扩展号码对应关系
        Map<Long, String> tempalteExtNumberRef = new HashMap<>();

        // 待更新状态的SID集合，即本次处理成功的SID信息
        // List<Long> avaiableSids = new ArrayList<>();
        // 错误信息
        StringBuilder errorReport = new StringBuilder();

        int successCounter = 0;
        M: for (MmsMtTask task : tasks) {
            if (task == null) {
                continue M;
            }

            // 校验子任务中是否包含通道有问题数据
            if (isTaskChildrenHasPassageError(task.getSid())) {
                errorReport.append("sid : [" + task.getSid() + "] 包含通道不可用数据;");
                continue M;
            }

            // 根据sid获取所有子任务数据
            List<MmsMtTaskPackets> packetsList = taskPacketsMapper.selectBySid(task.getSid());
            // 最终需要处理的子任务数据
            List<MmsMtTaskPackets> finalPacketsList = new ArrayList<>(packetsList.size());
            C: for (MmsMtTaskPackets packets : packetsList) {

                // 已完成的数据无需再发送队列，在子任务可能单独放出去了（目前一般不会子任务进行操作）
                if (packets.getStatus() == PacketsApproveStatus.AUTO_COMPLETE.getCode()
                    || packets.getStatus() == PacketsApproveStatus.HANDLING_COMPLETE.getCode()) {
                    continue C;
                }

                // 重新发送到待提交包中时需要更新状态
                packets.setStatus(PacketsApproveStatus.HANDLING_COMPLETE.getCode());

                // 设置最终通道属性[编码，限速大小，签名模式]
                if (!setTaskPacketsPassageAttribute(packets, passageContainer)) {
                    errorReport.append("sid : [" + task.getSid() + "] 包含通道不可用数据;");
                    continue M;
                }

                // 设置短信模板扩展号码
                if (!setTaskPacketsMessageTemplateExtNumber(packets, tempalteExtNumberRef)) {
                    errorReport.append("sid : [" + task.getSid() + "] 模板: [" + packets.getMessageTemplateId()
                                       + "]数据为空;");
                    continue M;
                }

                copyUserCustomProperties(task, packets);

                finalPacketsList.add(packets);
            }

            if (CollectionUtils.isEmpty(finalPacketsList)) {
                continue M;
            }

            // 批量发送数据至网关队列
            if (!sendPacketsListToQueue(finalPacketsList)) {
                errorReport.append("sid : [" + task.getSid() + "] 入队失败;");
                continue M;
            }

            // 更新任务状态
            if (!updateTaskAndPacketsCompleted(task.getSid())) {
                errorReport.append("sid : [" + task.getSid() + "] 任务更新失败;");
            }

            // 累加SID集合信息
            // avaiableSids.add(task.getSid());
            successCounter++;
        }

        if (successCounter > 0) {
            // 无错误，一般是指全部处理成功
            if (StringUtils.isEmpty(errorReport)) {
                return new ResponseMessage(successCounter, "操作成功", true);
            }

            return new ResponseMessage(successCounter,
                                       "主任务共 : [" + tasks.size() + "], 本次处理 : [" + successCounter + "]", true);
        }

        return new ResponseMessage(successCounter, StringUtils.isEmpty(errorReport) ? "处理失败" : errorReport.toString(),
                                   false);
    }

    /**
     * TODO 设置通道信息
     * 
     * @param packets
     * @param passageContainer
     */
    private boolean setTaskPacketsPassageAttribute(MmsMtTaskPackets packets, Map<Integer, MmsPassage> passageContainer) {
        MmsPassage passage = null;
        if (passageContainer.containsKey(packets.getFinalPassageId())) {
            passage = passageContainer.get(packets.getFinalPassageId());
        } else {
            passage = mmsPassageService.findById(packets.getFinalPassageId());
            if (passage == null) {
                return false;
            }
            passageContainer.put(packets.getFinalPassageId(), passage);
        }

        packets.setPassageCode(passage.getCode());
        packets.setPassageSpeed(passage.getPacketsSize());

        return true;
    }

    /**
     * TODO 设置通道子任务短信模板扩展号码信息
     * 
     * @param packets
     * @param tempalteExtNumberRef
     * @return
     */
    private boolean setTaskPacketsMessageTemplateExtNumber(MmsMtTaskPackets packets,
                                                           Map<Long, String> tempalteExtNumberRef) {
        // 如果短信模板ID没有，审核通过，则表明不用报备模板
        if (packets.getMessageTemplateId() == null
            || packets.getMessageTemplateId() == TemplateContext.SUPER_TEMPLATE_ID) {
            return true;
        }

        if (!tempalteExtNumberRef.containsKey(packets.getMessageTemplateId())) {
            packets.setTemplateExtNumber(tempalteExtNumberRef.get(packets.getMessageTemplateId()));
        } else {
            MmsMessageTemplate template = mmsTemplateService.get(packets.getMessageTemplateId());
            if (template == null) {
                return false;
            }
            tempalteExtNumberRef.put(packets.getMessageTemplateId(), template.getExtNumber());
            packets.setTemplateExtNumber(template.getExtNumber());
        }

        return true;
    }

    /**
     * TODO 发送分包数据至网关队列
     * 
     * @param finalPacketsList
     * @return
     */
    private boolean sendPacketsListToQueue(List<MmsMtTaskPackets> finalPacketsList) {
        if (CollectionUtils.isEmpty(finalPacketsList)) {
            logger.warn("待发送网关队列数据为空");
            return false;
        }
        try {
            mmsMtSubmitService.sendToSubmitQueue(finalPacketsList);

            return true;
        } catch (Exception e) {
            logger.error("待发送网关队列数据入队列失败", e);
            return false;
        }
    }

    /**
     * TODO 子任务拷贝主任务属性信息（方便值数据传递，MQ进行后续解析）
     * 
     * @param source
     * @param target
     */
    public void copyUserCustomProperties(MmsMtTask source, MmsMtTaskPackets target) {
        target.setCallback(source.getCallback());
        target.setAttach(source.getAttach());
        target.setUserId(source.getUserId());
        target.setExtNumber(source.getExtNumber());
    }

    /**
     * TODO 根据SID同时更新主任务和子任务状态为“手动完成”
     * 
     * @param sid 任务编号
     * @return
     */
    private boolean updateTaskAndPacketsCompleted(Long sid) {
        // 根据子任务ID更新状态为“手动完成”
        int result = taskPacketsMapper.updateStatusBySid(sid, PacketsApproveStatus.HANDLING_COMPLETE.getCode());
        if (result == 0) {
            return false;
        }

        // 更新主任务状态“手动通过”
        return taskMapper.updateApproveStatusBySid(sid, PacketsApproveStatus.HANDLING_COMPLETE.getCode()) > 0;
    }

    /**
     * TODO 并发执行主任务集合驳回逻辑
     * 
     * @param tasks
     * @return
     */
    @Async("asyncTaskExecutor")
    public Future<ResponseMessage> parallelTaskRejected(List<MmsMtTask> tasks) {
        if (CollectionUtils.isEmpty(tasks)) {
            return new AsyncResult<>(new ResponseMessage(ResponseMessage.ERROR_CODE, "并行驳回任务数据为空", false));
        }

        try {
            int successCounter = 0;
            for (MmsMtTask task : tasks) {
                List<MmsMtTaskPackets> packets = taskPacketsMapper.selectBySid(task.getSid());
                if (CollectionUtils.isEmpty(packets)) {
                    continue;
                }

                for (MmsMtTaskPackets pt : packets) {
                    if (pt.getStatus() == PacketsApproveStatus.REJECT.getCode()) {
                        continue;
                    }

                    copyUserCustomProperties(task, pt);

                    // 制作网关伪造包并发送至相关队列
                    this.sendRejectPackageQueue(pt);
                }

                // 更新任务状态为“驳回”
                if (updateTaskAndPacketsRejected(task.getSid())) {
                    successCounter++;
                }
            }

            if (successCounter > 0) {
                return new AsyncResult<>(new ResponseMessage(successCounter, "并行任务驳回成功", true));
            }

            return new AsyncResult<>(new ResponseMessage(ResponseMessage.ERROR_CODE, "并行任务驳回失败", false));
        } catch (Exception e) {
            logger.error("并行任务驳回失败", e);
            return new AsyncResult<>(new ResponseMessage(ResponseMessage.ERROR_CODE, "并行任务驳回失败", false));
        }
    }

    /**
     * TODO 制作短信驳回伪造包并发送至相关队列（）
     *
     * @param packets
     */
    public void sendRejectPackageQueue(MmsMtTaskPackets packets) {
        String[] mobiles = packets.getMobile().split(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR);
        if (mobiles.length == 0) {
            return;
        }

        for (String m : mobiles) {
            MmsMtMessageSubmit submit = new MmsMtMessageSubmit();
            submit.setUserId(packets.getUserId());
            submit.setSid(packets.getSid());
            submit.setMobile(m);
            submit.setCmcp(CMCP.local(m).getCode());
            submit.setContent(packets.getContent());
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

            rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_MMS, RabbitConstant.MQ_MMS_MT_PACKETS_EXCEPTION,
                                          submit);
        }
    }

    /**
     * TODO 根据SID同时更新主任务和子任务状态为“驳回”
     * 
     * @param sid 任务编号
     * @return
     */
    private boolean updateTaskAndPacketsRejected(Long sid) {
        // 根据子任务ID更新状态为“手动完成”
        int result = taskPacketsMapper.updateStatusBySid(sid, PacketsApproveStatus.REJECT.getCode());
        if (result == 0) {
            return false;
        }

        // 更新主任务状态“手动通过”
        return taskMapper.updateApproveStatusBySid(sid, PacketsApproveStatus.REJECT.getCode()) > 0;
    }

}
