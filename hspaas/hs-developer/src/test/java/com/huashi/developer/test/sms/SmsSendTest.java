package com.huashi.developer.test.sms;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.huashi.common.util.SecurityUtil;
import com.huashi.util.HttpClientUtil;

public class SmsSendTest {
    
    private String appId;
    private String modeId;
    private String vars;
    private String mobile;
    private String sendTime;
    private String notifyUrl;
    private String fromNo;
    private String userParams;
    private String sign;
    
    private String url;
    
    private String appKey;
    
    private Map<String ,Object> params;
    
    @Before
    public void init() {
        appId = "90";
        modeId = "30436";
        vars = "123888|5分钟";
        mobile = "15868193450";
        sendTime = "";
        notifyUrl = "http://192.168.1.104:31113/test";
        fromNo = "";
        userParams = "哈哈";
        
        appKey = "d523e832d112202b966beb909df270b5";
        
        sign = appKey+appId+ mobile;
        
        url = "http://localhost:8080/api/smsSend/smsSend";
        params = new HashMap<>();
        params.put("appId", appId);
        params.put("modeId", modeId);
        params.put("vars", vars);
        params.put("mobile", mobile);
        params.put("sendTime", sendTime);
        params.put("notifyUrl", notifyUrl);
        params.put("fromNo", fromNo);
        params.put("userParams", userParams);
        params.put("sign", sign(sign));
    }
    
    @Test
    public void addMode() {
        
        String result = HttpClientUtil.post(url, null, params, 100);
        
        System.out.println(result);
        
//        Assert.assertTrue(OpenApiCode.ApiReponseCode.SUCCESS.getCode().equals(result.));
    }
    
    private String sign(String originText){
        try {
            originText = URLEncoder.encode(originText, "UTF-8");
            return SecurityUtil.md5Hex(originText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
