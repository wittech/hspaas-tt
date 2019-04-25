package com.huashi.mms.record.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.user.model.UserModel;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.mms.config.rabbit.RabbitMessageQueueManager;
import com.huashi.mms.config.rabbit.constant.RabbitConstant;
import com.huashi.mms.config.rabbit.listener.MmsWaitSubmitListener;
import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.mms.record.dao.MmsMtMessagePushMapper;
import com.huashi.mms.record.dao.MmsMtMessageSubmitMapper;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.record.domain.MmsMtMessagePush;
import com.huashi.mms.record.domain.MmsMtMessageSubmit;
import com.huashi.mms.task.domain.MmsMtTaskPackets;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.service.IMmsTemplateBodyService;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.passage.context.PassageContext.DeliverStatus;

/**
 * TODO 下行彩信服务实现
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年1月16日 下午11:13:10
 */
@Service
public class MmsMtSubmitService implements IMmsMtSubmitService {

    @Reference
    private IUserService              userService;
    @Autowired
    private MmsMtMessagePushMapper    pushMapper;
    @Reference
    private IMmsMtDeliverService      mmsMtDeliverService;
    @Autowired
    private IMmsPassageService        mmsPassageService;

    @Autowired
    private IMmsMtPushService         mmsMtPushService;
    @Autowired
    private MmsMtMessageSubmitMapper  mmsMtMessageSubmitMapper;

    @Resource
    private RabbitTemplate            rabbitTemplate;
    @Resource
    private StringRedisTemplate       stringRedisTemplate;
    @Autowired
    private MmsWaitSubmitListener     mmsWaitSubmitListener;
    @Autowired
    private RabbitMessageQueueManager rabbitMessageQueueManager;
    @Reference
    private IMmsTemplateBodyService   mmsTemplateBodyService;
    private final Logger              logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<MmsMtMessageSubmit> findBySid(long sid) {
        return mmsMtMessageSubmitMapper.findBySid(sid);
    }

    @Override
    public boolean save(MmsMtMessageSubmit submit) {
        submit.setCreateTime(new Date());
        return mmsMtMessageSubmitMapper.insertSelective(submit) > 0;
    }

    @Override
    public BossPaginationVo<MmsMtMessageSubmit> findPage(Map<String, Object> queryParams) {

        changeTimestampeParamsIfExists(queryParams);

        BossPaginationVo<MmsMtMessageSubmit> page = new BossPaginationVo<>();
        page.setCurrentPage(Integer.parseInt(queryParams.getOrDefault("currentPage", 1).toString()));
        int total = mmsMtMessageSubmitMapper.findCount(queryParams);
        if (total <= 0) {
            return page;
        }

        page.setTotalCount(total);
        queryParams.put("start", page.getStartPosition());
        queryParams.put("end", page.getPageSize());
        List<MmsMtMessageSubmit> dataList = mmsMtMessageSubmitMapper.findList(queryParams);
        if (CollectionUtils.isEmpty(dataList)) {
            return page;
        }

        joinRecordFascade(dataList);

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
     * TODO 加入记录关联列数据（主要针对回执数据和推送数据）
     *
     * @param submits
     */
    private void joinRecordFascade(List<MmsMtMessageSubmit> submits) {
        Map<String, MmsMtMessagePush> pushMap = new HashMap<>();

        // 加入内存对象，减少DB的查询次数
        Map<Integer, UserModel> userModelMap = new HashMap<>();
        Map<Integer, String> passageMap = new HashMap<>();

        String key;
        for (MmsMtMessageSubmit record : submits) {
            key = String.format("%s_%s", record.getMsgId(), record.getMobile());
            if (record.getUserId() != null) {
                if (userModelMap.containsKey(record.getUserId())) {
                    record.setUserModel(userModelMap.get(record.getUserId()));
                } else {
                    record.setUserModel(userService.getByUserId(record.getUserId()));
                    userModelMap.put(record.getUserId(), record.getUserModel());
                }
            }

            if (record.getNeedPush()) {
                if (pushMap.containsKey(key)) {
                    record.setMessagePush(pushMap.get(key));
                } else {
                    record.setMessagePush(pushMapper.findByMobileAndMsgid(record.getMobile(), record.getMsgId()));
                    pushMap.put(key, record.getMessagePush());
                }
            }
            // if(record.getStatus() == 0){
            // if(deliverMap.containsKey(key)) {
            // record.setMessageDeliver(deliverMap.get(key));
            // } else {
            // record.setMessageDeliver(mmsMtDeliverService.findByMobileAndMsgid(record.getMobile(),record.getMsgId()));
            // deliverMap.put(key, record.getMessageDeliver());
            // }
            // }
            if (record.getPassageId() != null) {
                if (record.getPassageId() == PassageContext.EXCEPTION_PASSAGE_ID) {
                    record.setPassageName(PassageContext.EXCEPTION_PASSAGE_NAME);
                } else {
                    if (passageMap.containsKey(record.getUserId())) {
                        record.setPassageName(passageMap.get(record.getPassageId()));
                    } else {
                        MmsPassage passage = mmsPassageService.findById(record.getPassageId());
                        if (passage != null) {
                            record.setPassageName(passage.getName());
                            passageMap.put(record.getPassageId(), passage.getName());
                        }
                    }
                }
            }
        }

    }

    @Override
    public PaginationVo<MmsMtMessageSubmit> findPage(int userId, String mobile, String startDate, String endDate,
                                                     String currentPage, String sid) {
        if (userId <= 0) {
            return null;
        }

        int _currentPage = PaginationVo.parse(currentPage);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", userId);
        if (StringUtils.isNotBlank(sid)) {
            paramMap.put("sid", sid);
        }
        if (StringUtils.isNotBlank(mobile)) {
            paramMap.put("mobile", mobile);
        }
        paramMap.put("startDate", DateUtil.getSecondDate(startDate + " 00:00:01").getTime());
        paramMap.put("endDate", DateUtil.getSecondDate(endDate + " 23:59:59").getTime());
        int totalRecord = mmsMtMessageSubmitMapper.findCountByUser(paramMap);
        if (totalRecord == 0) {
            return null;
        }

        paramMap.put("start", PaginationVo.getStartPage(_currentPage));
        paramMap.put("end", PaginationVo.DEFAULT_RECORD_PER_PAGE);

        List<MmsMtMessageSubmit> dataList = mmsMtMessageSubmitMapper.findListByUser(paramMap);
        for (MmsMtMessageSubmit record : dataList) {
            // if (record.getNeedPush()) {
            // record.setMessagePush(pushMapper.findByMobileAndMsgid(record.getMobile(), record.getMsgId()));
            // }
            if (record.getStatus() == 0) {
                record.setMessageDeliver(mmsMtDeliverService.findByMobileAndMsgid(record.getMobile(), record.getMsgId()));
            }
        }
        return new PaginationVo<>(dataList, _currentPage, totalRecord);
    }

    @Override
    public void batchInsertSubmit(List<MmsMtMessageSubmit> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        mmsMtMessageSubmitMapper.batchInsert(list);
    }

    @Override
    public MmsMtMessageSubmit getSubmitWaitReceipt(String msgId, String mobile) {
        return mmsMtMessageSubmitMapper.selectByMsgIdAndMobile(msgId, mobile);
    }

    @Override
    public MmsMtMessageSubmit getByMoMapping(Integer passageId, String msgId, String mobile, String spcode) {

        MmsMtMessageSubmit smsMtMessageSubmit = null;
        if (passageId != null && StringUtils.isNotEmpty(msgId)) {
            smsMtMessageSubmit = mmsMtMessageSubmitMapper.selectByPsm(passageId, msgId, mobile);
        }
        if (smsMtMessageSubmit == null && StringUtils.isNotEmpty(msgId)) {
            smsMtMessageSubmit = mmsMtMessageSubmitMapper.selectByMsgIdAndMobile(msgId, mobile);
        }
        if (smsMtMessageSubmit == null) {
            return mmsMtMessageSubmitMapper.selectByMobile(mobile);
        }
        return smsMtMessageSubmit;
    }

    @Override
    public MmsMtMessageSubmit getByMsgidAndMobile(String msgId, String mobile) {
        return mmsMtMessageSubmitMapper.selectByMsgIdAndMobile(msgId, mobile);
    }

    @Override
    public MmsMtMessageSubmit getByMsgid(String msgId) {
        return mmsMtMessageSubmitMapper.selectByMsgId(msgId);
    }

    @Override
    public boolean doSmsException(List<MmsMtMessageSubmit> submits) {
        List<MmsMtMessageDeliver> delivers = new ArrayList<>();
        MmsMtMessageDeliver deliver;
        for (MmsMtMessageSubmit submit : submits) {
            deliver = new MmsMtMessageDeliver();
            deliver.setCmcp(submit.getCmcp());
            deliver.setMobile(submit.getMobile());
            deliver.setMsgId(submit.getMsgId());
            deliver.setStatusCode(StringUtils.isNotEmpty(submit.getPushErrorCode()) ? submit.getPushErrorCode() : submit.getRemark());
            deliver.setStatus(DeliverStatus.FAILED.getValue());
            deliver.setDeliverTime(DateUtil.getNow());
            deliver.setCreateTime(new Date());
            deliver.setRemark(submit.getRemark());
            delivers.add(deliver);
        }

        batchInsertSubmit(submits);

        // 判断短信是否需要推送，需要则设置推送设置信息
        setPushConfigurationIfNecessary(submits);

        try {
            mmsMtDeliverService.doFinishDeliver(delivers);
            return true;
        } catch (Exception e) {
            logger.warn("伪造短信回执包信息错误", e);
            return false;
        }
    }

    @Override
    public void setPushConfigurationIfNecessary(List<MmsMtMessageSubmit> submits) {
        // add by 2018-03-24 取出第一个值的信息（推送设置一批任务为一致信息）
        MmsMtMessageSubmit submit = submits.iterator().next();
        if (submit.getNeedPush() == null || !submit.getNeedPush() || StringUtils.isEmpty(submit.getPushUrl())) {
            return;
        }

        mmsMtPushService.setMessageReadyPushConfigurations(submits);
    }

    @Override
    public boolean declareWaitSubmitMessageQueues() {
        List<String> passageCodes = mmsPassageService.findPassageCodes();
        if (CollectionUtils.isEmpty(passageCodes)) {
            logger.error("无可用通道需要声明队列");
            return false;
        }

        try {
            for (String passageCode : passageCodes) {
                rabbitMessageQueueManager.createQueue(getSubmitMessageQueueName(passageCode),
                                                      mmsPassageService.isPassageBelongtoDirect(null, passageCode),
                                                      mmsWaitSubmitListener);
            }

            return true;
        } catch (Exception e) {
            logger.error("初始化消息队列异常");
            return false;
        }
    }

    @Override
    public String getSubmitMessageQueueName(String passageCode) {
        return String.format("%s.%s", RabbitConstant.MQ_MMS_MT_WAIT_SUBMIT, passageCode);
    }

    @Override
    public List<MmsMtMessageSubmit> getRecordListToMonitor(Long passageId, Long startTime, Long endTime) {
        return mmsMtMessageSubmitMapper.getRecordListToMonitor(passageId, startTime, endTime);
    }

    @Override
    public boolean declareNewSubmitMessageQueue(String passageCode) {
        String mqName = getSubmitMessageQueueName(passageCode);
        try {
            rabbitMessageQueueManager.createQueue(mqName, false, mmsWaitSubmitListener);
            logger.info("RabbitMQ添加新队列：{} 成功", mqName);
            return true;
        } catch (Exception e) {
            logger.error("声明新队列：{}失败", passageCode);
            return false;
        }
    }

    @Override
    public boolean removeSubmitMessageQueue(String passageCode) {
        String mqName = getSubmitMessageQueueName(passageCode);
        boolean isSuccess = rabbitMessageQueueManager.removeQueue(mqName);
        if (isSuccess) {
            logger.info("RabbitMQ移除队列：{} 成功", mqName);
        } else {
            logger.error("RabbitMQ移除队列：{} 失败", mqName);
        }

        return isSuccess;
    }

    @Override
    public boolean sendToSubmitQueue(List<MmsMtTaskPackets> packets) {
        if (CollectionUtils.isEmpty(packets)) {
            logger.warn("子任务数据为空，无需发送队列");
            return false;
        }

        // 发送至待提交信息队列处理
        Map<Integer, String> passageCodesMap = new HashMap<>();
        for (MmsMtTaskPackets packet : packets) {
            try {
                String passageCode = getPassageCode(passageCodesMap, packet);
                if (StringUtils.isEmpty(passageCode)) {
                    logger.error("子任务通道数据为空，无法进行通道代码分队列处理，通道ID：{}", packet.getFinalPassageId());
                    continue;
                }

                rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_MMS, getSubmitMessageQueueName(passageCode),
                                              packet, new CorrelationData(packet.getSid() + ""));
            } catch (Exception e) {
                logger.error("子任务发送至待提交任务失败，信息为：{}", JSON.toJSONString(packet), e);
            }
        }
        return true;
    }

    /**
     * TODO 根据子任务中的通道获取通道代码信息
     *
     * @param passageCodesMap
     * @param packet
     * @return
     */
    private String getPassageCode(Map<Integer, String> passageCodesMap, MmsMtTaskPackets packet) {
        if (StringUtils.isEmpty(packet.getPassageCode())) {
            if (passageCodesMap.containsKey(packet.getPassageId())) {
                return passageCodesMap.get(packet.getPassageId());
            }

            MmsPassage passage = mmsPassageService.findById(packet.getFinalPassageId());
            if (passage == null) {
                return null;
            }

            passageCodesMap.put(passage.getId(), passage.getCode());
            return passage.getCode();
        }

        return packet.getPassageCode();
    }

    @Override
    public List<Map<String, Object>> getSubmitStatReport(Long startTime, Long endTime) {

        if (startTime == null || endTime == null) {
            return null;
        }

        List<Map<String, Object>> list = mmsMtMessageSubmitMapper.selectSubmitReport(startTime, endTime);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> getLastHourSubmitReport() {
        // 截止时间为前一个小时0分0秒
        Long endTime = DateUtil.getXHourWithMzSzMillis(-1);
        // 开始时间为前2个小时0分0秒
        Long startTime = DateUtil.getXHourWithMzSzMillis(-2);

        return getSubmitStatReport(startTime, endTime);
    }

    @Override
    public List<Map<String, Object>> getSubmitCmcpReport(Long startTime, Long endTime) {
        if (startTime == null || endTime == null) {
            return null;
        }

        return mmsMtMessageSubmitMapper.selectCmcpReport(startTime, endTime);
    }

    @Override
    public MmsMessageTemplate getWithUserId(long sid, int userId) {
        List<MmsMtMessageSubmit> list = findBySid(sid);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        MmsMtMessageSubmit submit = list.iterator().next();
        if (submit.getUserId() != userId) {
            logger.error("下行数据用户ID[" + submit.getUserId() + "]与参数usreId:[" + userId + "]不匹配");
            return null;
        }

        MmsMessageTemplate template = new MmsMessageTemplate();
        template.setTitle(submit.getTitle());

        // 如果模板ID不为空，则按照模板ID填充
        if (StringUtils.isNotBlank(template.getModelId())) {
            template.setBodies(mmsTemplateBodyService.getBodiesByModelId(template.getModelId()));
        } else if (StringUtils.isNotEmpty(submit.getContent())) {
            template.setBodies(mmsTemplateBodyService.getBodies(submit.getContent()));
        }

        return template;
    }

}
