package com.huashi.developer.test.validator;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.huashi.developer.exception.ValidateException;


public class ApiParametersValidatorTest {
    
    private Map<String, String[]> paramMap;
    
    @Before
    public void init() {
        paramMap = new HashMap<>();
        paramMap.put("appkey", new String[]{"aa3k9ksi23kia"});
        paramMap.put("appsecret", new String[]{"eiaj8akaie92ks93ka9ddad92kai2s1"});
        paramMap.put("timestamp", new String[]{System.currentTimeMillis() + ""});
        paramMap.put("mobile", new String[]{"15868193450"});
        paramMap.put("content", new String[]{"【车点点】您的短信验证码为122312"});
        paramMap.put("extNumber", new String[]{"12"});
        paramMap.put("attach", new String[]{"9988821"});
        paramMap.put("callback", new String[]{"http://sms.hspaas.cn/status"});

        
    }

    @Test
    public void testByAnotation() throws ValidateException {
        
//        SmsValidator validator = new SmsValidator();

//        for(int i=0; i< 1000; i++) {
//            long start = System.currentTimeMillis();
//            SmsModel model = new SmsModel();
//            model = validator.validate(paramMap, "127.0.0.1");
//            System.out.println(System.currentTimeMillis() -  start);
//        }

        
//        Assert.assertNotNull("数据失败", model);
    }
}
