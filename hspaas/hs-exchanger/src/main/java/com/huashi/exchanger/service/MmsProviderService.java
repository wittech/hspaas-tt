package com.huashi.exchanger.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.huashi.common.util.MobileNumberCatagoryUtil;
import com.huashi.constants.CommonContext.ProtocolType;
import com.huashi.exchanger.domain.ProviderModelResponse;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.exception.ExchangeProcessException;
import com.huashi.exchanger.resolver.mms.http.MmsHttpSender;
import com.huashi.exchanger.service.template.SmsProxyManagerTemplate;
import com.huashi.mms.passage.domain.MmsPassageAccess;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.record.domain.MmsMoMessageReceive;
import com.huashi.mms.record.domain.MmsMtMessageDeliver;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;

/**
 * TODO 彩信接口提供服务
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月6日 下午11:07:44
 */
@Service
public class MmsProviderService implements IMmsProviderService {

    @Autowired
    private MmsHttpSender        mmsHttpSender;

    private final Logger         logger                     = LoggerFactory.getLogger(getClass());

    /**
     * 默认限流计流数
     */
    private static final Integer DEFAULT_RATE_LIMITER_TIMES = 1;

    @Override
    public ProviderModelResponse applyModel(MmsPassageParameter parameter, MmsMessageTemplate mmsMessageTemplate) {
        validate(parameter);

        return mmsHttpSender.applyModel(parameter, mmsMessageTemplate);
    }

    @Override
    public List<ProviderSendResponse> sendMms(MmsPassageParameter parameter, String mobile, String extNumber,
                                              String modelId) throws ExchangeProcessException {

        validate(parameter);

        // 判断是否需要限流
        if (!isNeedLimitSpeed(parameter.getProtocol())) {
            return mmsHttpSender.send(parameter, mobile, extNumber, modelId);
        }

        // 获取通道对应的流速设置
        RateLimiter rateLimiter = getRateLimiter(parameter.getPassageId(), parameter.getPacketsSize());
        String[] mobiles = mobile.split(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR);

        // 目前HTTP用途并不是很大（因为取决于HTTP自身的瓶颈）
        List<String[]> packets = recombineMobilesByLimitSpeedInSecond(mobiles,
                                                                      getRateLimiterAmount(parameter.getProtocol()),
                                                                      parameter.getPacketsSize());

        // 如果手机号码同时传递多个，需要对流速进行控制，多余流速的需要分批提交
        List<ProviderSendResponse> responsesInMultiGroup = new ArrayList<ProviderSendResponse>(packets.size());
        for (String[] m : packets) {
            // 设置acquire 当前分包后的数量
            rateLimiter.acquire(Integer.parseInt(m[0]));
            List<ProviderSendResponse> reponsePerGroup = mmsHttpSender.send(parameter, m[1], extNumber, modelId);
            if (CollectionUtils.isEmpty(reponsePerGroup)) {
                continue;
            }

            responsesInMultiGroup.addAll(reponsePerGroup);
        }

        return responsesInMultiGroup;
    }

    @Override
    public List<ProviderSendResponse> sendMms(MmsPassageParameter parameter, String mobile, String extNumber,
                                              String title, List<MmsMessageTemplateBody> bobies)
                                                                                                throws ExchangeProcessException {

        validate(parameter);

        // 判断是否需要限流
        if (!isNeedLimitSpeed(parameter.getProtocol())) {
            return mmsHttpSender.send(parameter, mobile, extNumber, title, bobies);
        }

        // 获取通道对应的流速设置
        RateLimiter rateLimiter = getRateLimiter(parameter.getPassageId(), parameter.getPacketsSize());
        String[] mobiles = mobile.split(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR);

        // 目前HTTP用途并不是很大（因为取决于HTTP自身的瓶颈）
        List<String[]> packets = recombineMobilesByLimitSpeedInSecond(mobiles,
                                                                      getRateLimiterAmount(parameter.getProtocol()),
                                                                      parameter.getPacketsSize());

        // 如果手机号码同时传递多个，需要对流速进行控制，多余流速的需要分批提交
        List<ProviderSendResponse> responsesInMultiGroup = new ArrayList<ProviderSendResponse>(packets.size());
        for (String[] m : packets) {
            // 设置acquire 当前分包后的数量
            rateLimiter.acquire(Integer.parseInt(m[0]));
            List<ProviderSendResponse> reponsePerGroup = mmsHttpSender.send(parameter, m[1], extNumber, title, bobies);
            if (CollectionUtils.isEmpty(reponsePerGroup)) {
                continue;
            }

            responsesInMultiGroup.addAll(reponsePerGroup);
        }

        return responsesInMultiGroup;
    }

    /**
     * TODO 计算限流总次数 根据协议类型判断多条短信是否以多次限流计数
     * 
     * @param protocol
     * @return
     */
    private static Integer getRateLimiterAmount(String protocol) {
        // 如果协议类型无法识别或者为空则按照 1次计数
        if (StringUtils.isEmpty(protocol)) {
            return DEFAULT_RATE_LIMITER_TIMES;
        }

        // ProtocolType protocolType = ProtocolType.parse(protocol);
        // if (protocolType != null
        // && (protocolType == ProtocolType.CMPP2 || protocolType == ProtocolType.SGIP || protocolType ==
        // ProtocolType.SMGP)) {
        // return fee;
        // }

        return DEFAULT_RATE_LIMITER_TIMES;
    }

    /**
     * TODO 是否需要限流
     * 
     * @param protocol
     * @return
     */
    private boolean isNeedLimitSpeed(String protocol) {
        if (StringUtils.isEmpty(protocol)) {
            return false;
        }

        ProtocolType protocolType = ProtocolType.parse(protocol);

        return !(protocolType == null || protocolType == ProtocolType.HTTP || protocolType == ProtocolType.WEBSERVICE);
    }

    /**
     * TODO 对提交网关的数据进行1秒内限流
     * 
     * @param mobiles 手机号码
     * @param amount 单个手机号码分流计数器数量
     * @param packetsSize 限流次数
     * @return 数组：[0]手机号码个数，[1]逗号拼接后的手机号码
     */
    public static List<String[]> recombineMobilesByLimitSpeedInSecond(String[] mobiles, Integer amount,
                                                                      Integer packetsSize) {
        // 每组手机号码个数 ，分包数/短信条数，如 50/3,则每个包手机号码数
        int groupMobileSize = packetsSize / amount;
        // 总手机号码数
        int totalMobileSize = mobiles.length;
        // 分组数
        int groupSize = (totalMobileSize % groupMobileSize == 0) ? (totalMobileSize / groupMobileSize) : (totalMobileSize
                                                                                                          / groupMobileSize + 1);

        List<String[]> mobileData = new ArrayList<>(groupSize);
        StringBuilder builder;
        String[] report;
        int index;
        for (int i = 0; i < groupSize; i++) {
            int roundSize = 0;
            report = new String[2];
            builder = new StringBuilder();

            index = i * groupMobileSize;
            for (int j = 0; j < groupMobileSize && index < totalMobileSize; j++) {
                builder.append(mobiles[index++]).append(MobileNumberCatagoryUtil.DATA_SPLIT_CHARCATOR);
                roundSize++;
            }

            report[0] = (roundSize * amount) + "";
            report[1] = builder.substring(0, builder.length() - 1);

            // 第0位为本次处理手机号码总个数，第1位为手机号码信息
            mobileData.add(report);
        }

        return mobileData;
    }

    /**
     * TODO 传递参数前校验
     * 
     * @param parameter
     */
    private void validate(MmsPassageParameter parameter) {
        if (StringUtils.isEmpty(parameter.getUrl())) {
            throw new IllegalArgumentException("MmsPassageParameter's url is empty");
        }

        if (StringUtils.isEmpty(parameter.getParams())) {
            throw new IllegalArgumentException("MmsPassageParameter's params are empty");
        }

        ProtocolType pt = ProtocolType.parse(parameter.getProtocol());
        if (pt == null) {
            throw new IllegalArgumentException("MmsPassageParameter's protocol is undefined");
        }

    }

    @Override
    public List<MmsMtMessageDeliver> receiveMtReport(MmsPassageAccess access, JSONObject report) {
        try {
            return mmsHttpSender.deliver(access, report);
        } catch (Exception e) {
            logger.error("Failed by args - SmsPassageAccess[" + JSON.toJSONString(access) + "], JSONObject["
                                 + report.toJSONString() + "] ", e);
            throw new ExchangeProcessException(e);
        }
    }

    @Override
    public List<MmsMtMessageDeliver> pullMtReport(MmsPassageAccess access) {
        try {
            return mmsHttpSender.deliver(access);
        } catch (Exception e) {
            logger.error("Failed by args - SmsPassageAccess[" + JSON.toJSONString(access) + "]", e);
            throw new ExchangeProcessException(e);
        }
    }

    @Override
    public List<MmsMoMessageReceive> receiveMoReport(MmsPassageAccess access, JSONObject report) {
        try {
            return mmsHttpSender.mo(access, report);
        } catch (Exception e) {
            logger.error("Failed by args - MmsPassageAccess[" + JSON.toJSONString(access) + "], JSONObject["
                                 + report.toJSONString() + "] ", e);
            throw new ExchangeProcessException(e);
        }
    }

    @Override
    public List<MmsMoMessageReceive> pullMoReport(MmsPassageAccess access) {
        try {
            return mmsHttpSender.mo(access);
        } catch (Exception e) {
            logger.error("Failed by args - MmsPassageAccess[" + JSON.toJSONString(access) + "]", e);
            throw new ExchangeProcessException(e);
        }
    }

    /**
     * TODO 获取限速信息，基于令牌分桶算法
     * 
     * @param passageId
     * @param speed
     * @return
     */
    private static RateLimiter getRateLimiter(Integer passageId, Integer speed) {
        RateLimiter limiter = SmsProxyManagerTemplate.GLOBAL_RATE_LIMITERS.get(passageId);
        if (limiter == null) {
            ReentrantLock reentrantLock = new ReentrantLock();
            reentrantLock.tryLock();
            try {
                limiter = RateLimiter.create((speed == null || speed == 0) ? SmsProxyManagerTemplate.DEFAULT_LIMIT_SPEED : speed);
                SmsProxyManagerTemplate.GLOBAL_RATE_LIMITERS.put(passageId, limiter);
            } finally {
                reentrantLock.unlock();
            }
        }

        return limiter;
    }

}
