package com.huashi.exchanger.test.http.lvcheng;

import java.util.List;

import com.huashi.exchanger.resolver.sms.http.lvcheng.LvchengPassageResolver;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.resolver.sms.http.wukong.WukongPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class LvchengHttpTest {

    private Logger        logger    = LoggerFactory.getLogger(getClass());

    LvchengPassageResolver resolver  = null;
    SmsPassageParameter   parameter = null;
    String                mobile    = null;
    String                content   = null;
    String                extNumber = null;

    @Before
    public void init() {
        resolver = new LvchengPassageResolver();
        parameter = new SmsPassageParameter();

        JSONObject pam = new JSONObject();
        pam.put("account", "huashi");
        pam.put("password", "huashi");
        pam.put("custom", "lvcheng");

        parameter.setUrl("http://api.china95059.net:8080/sms/send");
        parameter.setParams(pam.toJSONString());

        parameter.setSuccessCode("0");
        mobile = "13385819856";
        content = "【华时科技】您的验证码为334243";
    }

    @Test
    public void test() {
        List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);

        logger.info(JSON.toJSONString(list));

        Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));

    }
}
