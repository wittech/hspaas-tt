package com.huashi.exchanger.test.http.lanjinglz;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.sms.http.lanjinglz.LanjinglianzhongPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class LanjinglzHttpTest {

    private Logger                  logger    = LoggerFactory.getLogger(getClass());

    LanjinglianzhongPassageResolver resolver  = null;
    SmsPassageParameter             parameter = null;
    String                          mobile    = null;
    String                          content   = null;
    String                          extNumber = null;

    @Before
    public void init() {
        resolver = new LanjinglianzhongPassageResolver();
        parameter = new SmsPassageParameter();

        JSONObject pam = new JSONObject();
        pam.put("account", "zs_hzhs");
        pam.put("password", "hzhshzhs");
        pam.put("custom", "lanjinglz");

        parameter.setUrl("http://58.83.147.92:8080/qxt/smssenderv2");
        parameter.setParams(pam.toJSONString());
        parameter.setSuccessCode("ok");

        mobile = "15868193450";
        content = "【华时科技】您的短息验证码为1234452";
    }

    @Test
    public void test() {
        List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);

        logger.info(JSON.toJSONString(list));

        Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));

    }
}
