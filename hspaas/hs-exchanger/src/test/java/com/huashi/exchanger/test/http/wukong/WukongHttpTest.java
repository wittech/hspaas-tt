package com.huashi.exchanger.test.http.wukong;

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
import com.huashi.exchanger.resolver.sms.http.wukong.WukongPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class WukongHttpTest {

    private Logger        logger    = LoggerFactory.getLogger(getClass());

    WukongPassageResolver resolver  = null;
    SmsPassageParameter   parameter = null;
    String                mobile    = null;
    String                content   = null;
    String                extNumber = null;

    @Before
    public void init() {
        resolver = new WukongPassageResolver();
        parameter = new SmsPassageParameter();

        JSONObject pam = new JSONObject();
        pam.put("account", "b9bd6517e1a05eab218e25645a7024be");
        pam.put("password", "a8e13c5ebdc49a9e2d00572e2c6b8be0");
        pam.put("custom", "wukong");

        parameter.setUrl("http://www.52sms.com/api/batch_send/notice");
        parameter.setParams(pam.toJSONString());

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
