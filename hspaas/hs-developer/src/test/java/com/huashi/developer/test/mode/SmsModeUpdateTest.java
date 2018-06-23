package com.huashi.developer.test.mode;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.huashi.common.util.SecurityUtil;
import com.huashi.util.HttpClientUtil;

public class SmsModeUpdateTest {
    
    private String appId;
    private String modeId;
    private String title;
    private String modeSign;
    private String context;
    private String location;
    private String timestamp;
    private String sign;
    
    private String url;
    
    private String appKey;
    
    private Map<String ,Object> params;
    
    @Before
    public void init() {
        appId = "90";
        modeId = "30434";
        title = "测试模板2111111";
        modeSign = "【大数据23】";
        context = "您的短信验证码为${var1}，有效时间${var2}";
        location = "2";
        timestamp = System.currentTimeMillis() + "";
        
        appKey = "d523e832d112202b966beb909df270b5";
        
        sign = appKey+appId+modeId+title+modeSign+context+location+timestamp;
        
        url = "http://localhost:8080/api/mode/updMode";
        params = new HashMap<>();
        params.put("appId", appId);
        params.put("modeId", modeId);
        params.put("title", title);
        params.put("modeSign", modeSign);
        params.put("context", context);
        params.put("location", location);
        params.put("timestamp", timestamp);
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
