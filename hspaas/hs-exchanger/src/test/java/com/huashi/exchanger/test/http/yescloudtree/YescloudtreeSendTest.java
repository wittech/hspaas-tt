package com.huashi.exchanger.test.http.yescloudtree;

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
import com.huashi.exchanger.resolver.sms.http.yescloudtree.YescloudtreePassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class YescloudtreeSendTest {

    private Logger              logger    = LoggerFactory.getLogger(getClass());

    YescloudtreePassageResolver resolver  = null;
    SmsPassageParameter         parameter = null;
    String                      mobile    = null;
    String                      content   = null;
    String                      extNumber = null;

    @Before
    public void init() {
        resolver = new YescloudtreePassageResolver();
        parameter = new SmsPassageParameter();

        JSONObject pam = new JSONObject();

        pam.put("account", "hzhs");
        pam.put("password", "hzhs0814");
        pam.put("is_p2p", "");
        pam.put("custom", "yescloudtree");

        parameter.setUrl("http://www.yescloudtree.cn:28001/");
        parameter.setParams(pam.toJSONString());
        parameter.setSuccessCode("0");

        extNumber = "12";

        mobile = "15868193450";
        content = "【云树科技】尊敬的客户，本次验证码为：125889，5分钟有效，请及时操作。";
    }

    @Test
    public void test() {
        List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);

        logger.info(JSON.toJSONString(list));

        Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));

    }
}
