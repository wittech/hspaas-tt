package com.huashi.sms.record.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.DateUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.common.vo.PaginationVo;
import com.huashi.constants.CommonContext.CallbackUrlType;
import com.huashi.sms.config.cache.redis.constant.SmsRedisConstant;
import com.huashi.sms.config.rabbit.constant.RabbitConstant;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.dao.SmsMoMessagePushMapper;
import com.huashi.sms.record.dao.SmsMoMessageReceiveMapper;
import com.huashi.sms.record.domain.SmsMoMessageReceive;
import com.huashi.sms.record.domain.SmsMtMessageSubmit;
import com.huashi.sms.settings.constant.MobileBlacklistType;
import com.huashi.sms.settings.domain.SmsMobileBlackList;
import com.huashi.sms.settings.service.ISmsMobileBlackListService;

/**
 * TODO 短信接收（上行）记录服务接口类
 * 
 * @author zhengying
 * @version V1.0
 * @date 2016年4月25日 上午9:54:32
 */
@Service
public class SmsMoMessageService implements ISmsMoMessageService {

    @Resource
    private RabbitTemplate             rabbitTemplate;
    @Resource
    private StringRedisTemplate        stringRedisTemplate;

    @Reference
    private IUserService               userService;
    @Reference
    private IPushConfigService         pushConfigService;
    @Reference
    private ISmsPassageService         smsPassageService;
    @Reference
    private ISystemConfigService       systemConfigService;

    @Autowired
    private ISmsMtSubmitService        smsMtSubmitService;
    @Autowired
    private SmsMoMessagePushMapper     smsMoMessagePushMapper;
    @Autowired
    private SmsMoMessageReceiveMapper  moMessageReceiveMapper;
    @Autowired
    private ISmsMobileBlackListService smsMobileBlackListService;

    /**
     * 恒生电子定制服务状态是否开启 0：未开启，1:已开启
     */
    @Value("${custom.mo.hundsun.run:0}")
    private int                        hundsunRunStatus;

    /**
     * 恒生电子用户Id
     */
    @Value("${custom.mo.hundsun.userId}")
    private int                        hundsunUserId;

    /**
     * 恒生电子定制短信上行码号
     */
    @Value("${custom.mo.hundsun.destnationNo}")
    private String                     hundsunDestnationNo;

    /**
     * 运行状态：运行中...
     */
    private static final int           RUNNING_STATUS = 1;

    private final Logger               logger         = LoggerFactory.getLogger(getClass());

    @Override
    public PaginationVo<SmsMoMessageReceive> findPage(int userId, String phoneNumber, String startDate, String endDate,
                                                      String currentPage) {
        if (userId <= 0) {
            return null;
        }

        int _currentPage = PaginationVo.parse(currentPage);

        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        if (StringUtils.isNotEmpty(phoneNumber)) {
            params.put("phoneNumber", phoneNumber);
        }
        params.put("startDate", DateUtil.getStringTurnDate(startDate));
        params.put("endDate", DateUtil.getStringTurnDate(endDate));

        int totalRecord = moMessageReceiveMapper.getCountByUserId(params);
        if (totalRecord == 0) {
            return null;
        }

        params.put("startPage", PaginationVo.getStartPage(_currentPage));
        params.put("pageRecord", PaginationVo.DEFAULT_RECORD_PER_PAGE);

        List<SmsMoMessageReceive> list = moMessageReceiveMapper.findPageListByUserId(params);
        if (list == null || list.isEmpty()) {
            return null;
        }

        return new PaginationVo<>(list, _currentPage, totalRecord);
    }

    @Override
    public BossPaginationVo<SmsMoMessageReceive> findPage(int pageNum, String keyword) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("keyword", keyword);
        BossPaginationVo<SmsMoMessageReceive> page = new BossPaginationVo<>();
        page.setCurrentPage(pageNum);
        int total = moMessageReceiveMapper.findCount(paramMap);
        if (total <= 0) {
            return page;
        }
        page.setTotalCount(total);
        paramMap.put("start", page.getStartPosition());
        paramMap.put("end", page.getPageSize());
        List<SmsMoMessageReceive> dataList = moMessageReceiveMapper.findList(paramMap);
        for (SmsMoMessageReceive record : dataList) {
            if (record.getUserId() != null) {
                record.setUserModel(userService.getByUserId(record.getUserId()));
            }
            if (record.getPassageId() != null && record.getPassageId() > 0) {
                SmsPassage passage = smsPassageService.findById(record.getPassageId());
                record.setPassageName(passage == null ? PassageContext.EXCEPTION_PASSAGE_NAME : passage.getName());
            }
            if (StringUtils.isNotEmpty(record.getMsgId()) && StringUtils.isNotEmpty(record.getMobile())) {
                record.setMessagePush(smsMoMessagePushMapper.findByMobileAndMsgid(record.getMobile(), record.getMsgId()));
            }
        }
        page.getList().addAll(dataList);
        return page;
    }

    @Override
    public int doFinishReceive(List<SmsMoMessageReceive> list) {
        int count = 0;
        try {
            for (SmsMoMessageReceive receive : list) {

                // // 31省测试上行自动触发下行功能（测试后上线去掉此逻辑） add by 20170709
                // if(StringUtils.isNotEmpty(receive.getDestnationNo()) &&
                // receive.getDestnationNo().startsWith("10691348")) {
                // logger.info("数据已发送短信 {}", JSON.toJSONString(receive));
                // MessageSendUtil.sms("http://api.hspaas.cn:8080/sms/send", "hsjXxJ2gO75iOK",
                // "f7c3ddd27a61fbe9612b2b9f1e6c8287",
                // receive.getMobile(), "【惠尔睿智】31省根据上行自动下行");
                // }

                // add by 20180730 如果属于恒生指定的码号上行，则按照固定恒生处理
                if(processIfHundsunMatched(receive)) {
                    continue;
                }
                
                // 根据通道ID和消息ID
                SmsMtMessageSubmit submit = smsMtSubmitService.getByMoMapping(receive.getPassageId(),
                                                                              receive.getMsgId(), receive.getMobile(),
                                                                              receive.getDestnationNo());
                if (submit != null) {
                    receive.setMsgId(submit.getMsgId());
                    receive.setUserId(submit.getUserId());
                    receive.setSid(submit.getSid() + "");

                    // 如果上行回执内容为空，则置一个空格字符
                    if (StringUtils.isEmpty(receive.getContent())) {
                        receive.setContent(" ");
                    }

                    // 针对直连协议PassageId反补
                    receive.setPassageId(submit.getPassageId());

                    count++;

                    // 判断是否包含退订关键词，如果包含直接加入黑名单
                    joinBlacklistIfMatched(receive.getMobile(), receive.getContent(),
                                           String.format("SID:%d,MSG_ID:%s", submit.getSid(), submit.getMsgId()));

                    PushConfig pushConfig = pushConfigService.getByUserId(submit.getUserId(),
                                                                          CallbackUrlType.SMS_MO.getCode());
                    if (pushConfig == null || StringUtils.isEmpty(pushConfig.getUrl())) {
                        receive.setNeedPush(false);
                        continue;
                    }

                    receive.setNeedPush(true);
                    receive.setPushUrl(pushConfig.getUrl());
                    receive.setRetryTimes(pushConfig.getRetryTimes());

                    sendToPushQueue(receive);
                }
            }

            // 插入DB
            moMessageReceiveMapper.batchInsert(list);

            return count;

        } catch (Exception e) {
            logger.error("处理待回执信息失败，失败信息：{}", JSON.toJSONString(list), e);
            return 0;
        }
    }

    /**
     * 
       * TODO 如果属于恒生上行码号，则按照恒生指定的方式执行
       * 
       * @param receive
       * @return
     */
    private boolean processIfHundsunMatched(SmsMoMessageReceive receive) {
        try {
            if(RUNNING_STATUS != hundsunRunStatus || receive == null || 
                    StringUtils.isEmpty(hundsunDestnationNo) || 
                    !receive.getDestnationNo().startsWith(hundsunDestnationNo)) {
                return false;
            }
            
            receive.setUserId(hundsunUserId);
            PushConfig pushConfig = pushConfigService.getByUserId(hundsunUserId, 
                                                                  CallbackUrlType.SMS_MO.getCode());
            if (pushConfig == null || StringUtils.isEmpty(pushConfig.getUrl())) {
                receive.setNeedPush(false);
                return true;
            }

            receive.setNeedPush(true);
            receive.setPushUrl(pushConfig.getUrl());
            receive.setRetryTimes(pushConfig.getRetryTimes());

            sendToPushQueue(receive);
            
            return true;
        } catch (Exception e) {
            logger.error("【恒生电子】处理上行信息失败，失败信息：{}", JSON.toJSONString(receive), e);
            return false;
        }
    }

    /**
     * TODO 根据上行回执短信内容判断是否需要加入黑名单
     *
     * @param mobile
     * @param content
     */
    private void joinBlacklistIfMatched(String mobile, String content, String remark) {
        // 判断回复内容是否包含 黑名单词库内容，如果包括则直接加入黑名单
        String[] words = systemConfigService.getBlacklistWords();
        if (words == null || words.length == 0) {
            words = BLACKLIST_WORDS;
        }

        boolean isContains = false;
        for (String wd : words) {
            if (StringUtils.isEmpty(wd)) {
                continue;
            }

            if (content.toUpperCase().contains(wd.toUpperCase())) {
                isContains = true;
                break;
            }
        }

        if (!isContains) {
            return;
        }

        SmsMobileBlackList blacklist = new SmsMobileBlackList();
        blacklist.setMobile(mobile);
        blacklist.setType(MobileBlacklistType.UNSUBSCRIBE.getCode());
        blacklist.setRemark(remark);

        smsMobileBlackListService.batchInsert(blacklist);
    }

    /**
     * 默认黑名单词库（当用户上行回复下列词汇，会自动将回复的手机号码加入黑名单手机号码中）
     */
    private static final String[] BLACKLIST_WORDS = { "TD", "T", "N" };

    @Override
    public int batchInsert(List<SmsMoMessageReceive> list) {
        if (CollectionUtils.isEmpty(list)) {
            return 0;
        }

        return moMessageReceiveMapper.batchInsert(list);
    }

    @Override
    public boolean doReceiveToException(Object obj) {
        try {
            stringRedisTemplate.opsForList().rightPush(SmsRedisConstant.RED_MESSAGE_MO_RECEIPT_EXCEPTION_LIST,
                                                       JSON.toJSONString(obj));
            return true;
        } catch (Exception e) {
            logger.error("上行回执错误信息失败 {}", JSON.toJSON(obj), e);
            return false;
        }
    }

    /**
     * 
       * TODO 发送上行推送数据至消息队列中
       * 
       * @param receive
     */
    private void sendToPushQueue(SmsMoMessageReceive receive) {
        try {
            // 发送至待推送信息队列
            rabbitTemplate.convertAndSend(RabbitConstant.EXCHANGE_SMS, RabbitConstant.MQ_SMS_MO_WAIT_PUSH, receive);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
