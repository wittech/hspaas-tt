package com.huashi.exchanger.test.http.mms.trioly;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huashi.exchanger.domain.ProviderModelResponse;
import com.huashi.exchanger.resolver.mms.http.trioly.TriolyPassageResolver;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.template.constant.MmsTemplateContext.MediaType;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsMessageTemplateBody;

public class TriolyModelHttpTest {

    private Logger        logger    = LoggerFactory.getLogger(getClass());

    TriolyPassageResolver resolver  = null;
    MmsPassageParameter   parameter = null;
    String                mobile    = null;
    String                modelId   = null;
    String                extNumber = null;

    MmsMessageTemplate    mmsMessageTemplate   = null;

    @Before
    public void init() throws IOException {
        resolver = new TriolyPassageResolver();
        parameter = new MmsPassageParameter();

        mmsMessageTemplate = new MmsMessageTemplate();
        mmsMessageTemplate.setName("11");
        mmsMessageTemplate.setTitle("222");
        
        mmsMessageTemplate.setBodies(setBody());

        JSONObject pam = new JSONObject();
        pam.put("appKey", "4c34ef5f3c");
        pam.put("appId", "10606");
        pam.put("custom", "trioly");

        parameter.setUrl("http://op.supermms.cn/api/vsmsMode/addMode");
        parameter.setParams(pam.toJSONString());

        mobile = "15868193450";
        // content = "【华时科技】您的短息验证码为1234452";
    }
    
    private List<MmsMessageTemplateBody> setBody() throws IOException {
        List<MmsMessageTemplateBody> bodies = new ArrayList<>();
        MmsMessageTemplateBody body = new MmsMessageTemplateBody();
        body.setMediaName("txt");
        body.setMediaType(MediaType.TEXT.getCode());
        body.setContent("222222222222222222222");
        
        bodies.add(body);
        
        MmsMessageTemplateBody body2 = new MmsMessageTemplateBody();
        body2.setMediaName("jpg");
        body2.setMediaType(MediaType.IMAGE.getCode());
        body2.setContent("http://huashi-mms.oss-cn-shanghai.aliyuncs.com/custom_body_file/image/aqwe.jpg");
        
        bodies.add(body2);
        
        MmsMessageTemplateBody body3 = new MmsMessageTemplateBody();
        body3.setMediaName("mp4");
        body3.setMediaType(MediaType.VIDEO.getCode());
        body3.setContent("http://huashi-mms.oss-cn-shanghai.aliyuncs.com/custom_body_file/image/5bfce94e9588d.mp4");
        
        bodies.add(body3);
        
        return bodies;
    }
    
    @Test
    public void test() {
        ProviderModelResponse response = resolver.applyModel(parameter, mmsMessageTemplate);
        
        logger.info(JSON.toJSONString(response));


    }
}
