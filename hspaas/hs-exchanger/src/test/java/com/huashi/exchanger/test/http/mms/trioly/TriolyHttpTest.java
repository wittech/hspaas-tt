package com.huashi.exchanger.test.http.mms.trioly;

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
import com.huashi.exchanger.resolver.mms.http.trioly.TriolyPassageResolver;
import com.huashi.mms.passage.domain.MmsPassageParameter;

public class TriolyHttpTest {

    private Logger        logger    = LoggerFactory.getLogger(getClass());

    TriolyPassageResolver resolver  = null;
    MmsPassageParameter   parameter = null;
    String                mobile    = null;
    String                modelId   = null;
    String                extNumber = null;

    @Before
    public void init() {
        resolver = new TriolyPassageResolver();
        parameter = new MmsPassageParameter();

        JSONObject pam = new JSONObject();
        pam.put("appKey", "4c34ef5f3c");
        pam.put("appId", "10606");
        pam.put("custom", "trioly");

        modelId = "1253480";

        parameter.setUrl("http://send.supermms.cn/mt.php");
        parameter.setParams(pam.toJSONString());

        mobile = "15868193450";
        // content = "【华时科技】您的短息验证码为1234452";
    }

    @Test
    public void test() {
        List<ProviderSendResponse> list = resolver.send(parameter, mobile, extNumber, modelId);

        logger.info(JSON.toJSONString(list));

        Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));

    }
}
