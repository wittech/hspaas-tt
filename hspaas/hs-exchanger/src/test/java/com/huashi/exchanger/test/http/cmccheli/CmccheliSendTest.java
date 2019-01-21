package com.huashi.exchanger.test.http.cmccheli;

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
import com.huashi.exchanger.resolver.sms.http.cmccheli.CmccheliPassageResolver;
import com.huashi.sms.passage.domain.SmsPassageParameter;

public class CmccheliSendTest {

    private Logger          logger    = LoggerFactory.getLogger(getClass());

    CmccheliPassageResolver resolver  = null;
    SmsPassageParameter     parameter = null;
    String                  mobile    = null;
    String                  content   = null;
    String                  extNumber = null;

    @Before
    public void init() {
        resolver = new CmccheliPassageResolver();
        parameter = new SmsPassageParameter();

        JSONObject pam = new JSONObject();

        pam.put("appkey", "225000172fa74d77a7d6e4e14cbca081");
        pam.put("password", "c8a2016333f948f2877d35c15b2f9e69");
        pam.put("terminal_no", "106575261108090");

        // 【可遇科技】您的验证码是{code}，{number}分钟有效。

        pam.put("custom", "cmccheli");

        parameter.setSmsTemplateId("1");
        parameter.setVariableParamNames(new String[] { "code", "number" });
        parameter.setVariableParamValues(new String[] { "666666", "5" });

        parameter.setUrl("http://www.cmccheli.com/api/v1/sms/send");
        parameter.setParams(pam.toJSONString());
        parameter.setSuccessCode("200");

        extNumber = "12";

        mobile = "15868193450";
        // content = "【云树科技】尊敬的客户，本次验证码为：125889，5分钟有效，请及时操作。";
    }

    @Test
    public void test() {
        List<ProviderSendResponse> list = resolver.send(parameter, mobile, content, extNumber);

        logger.info(JSON.toJSONString(list));

        Assert.assertTrue("回执数据失败", CollectionUtils.isNotEmpty(list));

    }

    // public static void main(String[] args) {
    // String result =
    // "{\"messageId\": \"896c48663ff545448f41f237c7ff9caa\",\"msgList\": [{\"mobile\": \"18867103702\",\"resultCode\": 200,\"resultMsg\": \"response success\"},{\"mobile\": \"18867103703\",\"resultCode\": 416,\"resultMsg\": \"mobile belong to black list\"}],\"resultCode\": 200,\"resultMsg\": \"response success\"}\n";
    //
    // List<ProviderSendResponse> list = CmccheliPassageResolver.sendResponse(result, "200");
    // for(ProviderSendResponse p : list) {
    // System.out.println("msgid:" + p.getSid() + "  mobile:"+p.getMobile() + " code:" + p.getStatusCode());
    // }
    // }
}
