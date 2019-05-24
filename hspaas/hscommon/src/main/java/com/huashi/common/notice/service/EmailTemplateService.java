package com.huashi.common.notice.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import org.apache.dubbo.config.annotation.Service;
import com.huashi.common.notice.context.TemplateContext.EmailCode;
import com.huashi.common.notice.dao.EmailTemplateMapper;
import com.huashi.common.notice.domain.EmailTemplate;
import com.huashi.common.util.FreemarkerUtil;

@Service
public class EmailTemplateService implements IEmailTemplateService {

    @Autowired
    private EmailTemplateMapper emailTemplateMapper;

    @Override
    public EmailTemplate getRegisterVerifyContent(String url) {
        if (StringUtils.isEmpty(url)) {
            throw new RuntimeException("URL参数不正确");
        }
        EmailTemplate template = emailTemplateMapper.selectByCode(EmailCode.REGISTER_VERIFY.getCode());
        if (template == null) {
            return null;
        }
        if (StringUtils.isEmpty(template.getContent())) {
            return null;
        }

        Map<String, String> values = new HashMap<String, String>();
        values.put("url", url);

        template.setContent(FreemarkerUtil.parse(template.getContent(), values));
        return template;
    }

    @Override
    public EmailTemplate getRegisterSuceessContent(String username, String appkey, String appsecret) {
        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(appkey) || StringUtils.isEmpty(appsecret)) {
            throw new RuntimeException("参数不正确");
        }

        EmailTemplate template = emailTemplateMapper.selectByCode(EmailCode.REGISTER_SUCCESS.getCode());
        if (template == null) {
            return null;
        }
        if (StringUtils.isEmpty(template.getContent())) {
            return null;
        }

        Map<String, String> values = new HashMap<String, String>();
        values.put("username", username);
        values.put("appkey", appkey);
        values.put("appsecret", appsecret);

        template.setContent(FreemarkerUtil.parse(template.getContent(), values));

        return template;
    }

}
