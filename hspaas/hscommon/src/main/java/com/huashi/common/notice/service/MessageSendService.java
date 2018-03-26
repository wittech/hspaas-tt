package com.huashi.common.notice.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.alibaba.dubbo.config.annotation.Service;
import com.huashi.common.notice.dao.SmsMtRecordMapper;
import com.huashi.common.notice.domain.SmsMtRecord;
import com.huashi.common.notice.util.MessageSendUtil;
import com.huashi.common.notice.vo.SmsResponse;

@Service
public class MessageSendService implements IMessageSendService {

    private Logger                  logger = LoggerFactory.getLogger(MessageSendService.class);

    @Autowired
    private IMessageTemplateService messageTemplateService;
    @Autowired
    private SmsMtRecordMapper       smsMtRecordMapper;

    @Value("${api.sms.url}")
    private String                  url;
    @Value("${api.sms.appkey}")
    private String                  appkey;
    @Value("${api.sms.appsecret}")
    private String                  appsecret;

    @Override
    public boolean sendVerifyCode(String code, String mobile) {
        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(code)) {
            return false;
        }
        String content = messageTemplateService.getVerifyContent(code);
        logger.info("手机号码：{}， 对应短信动态码为 ：{}", mobile, code);

        try {
            boolean isSuccess = MessageSendUtil.smsWithStatus(url, appkey, appsecret, mobile.trim(), content);

            if (isSuccess) {
                smsMtRecordMapper.insertSelective(new SmsMtRecord(null, mobile, content, new Date()));
            }

            // 解析发送短信结果
            return isSuccess;
        } catch (Exception e) {
            logger.error("发送短信错误", e);
            return false;
        }

    }

    @Override
    public SmsResponse sendSystemMessage(String mobile, String content) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(mobile)) {
            return null;
        }

        try {
            SmsResponse resp = MessageSendUtil.sms(url, appkey, appsecret, mobile, content);

            if (resp != null) {
                smsMtRecordMapper.insertSelective(new SmsMtRecord(null, mobile, content, new Date()));
            }

            return resp;
        } catch (Exception e) {
            logger.error("发送短信错误", e);
            return null;
        }
    }

    @Override
    public SmsResponse sendCustomMessage(String appkey, String appsecret, String mobile, String content) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(mobile)) {
            return null;
        }

        try {
            return MessageSendUtil.sms(url, appkey, appsecret, mobile, content);

        } catch (Exception e) {
            logger.error("发送短信错误", e);
            return null;
        }
    }

}
