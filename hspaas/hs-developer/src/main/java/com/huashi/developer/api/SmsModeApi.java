package com.huashi.developer.api;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.huashi.developer.prervice.SmsTemplatePrervice;
import com.huashi.developer.response.sms.SmsApiResponse;

@RestController
@RequestMapping(value = "/api/mode")
public class SmsModeApi extends BasicApiSupport {
    
    @Autowired
    private SmsTemplatePrervice smsTemplatePrervice;

    @RequestMapping(value = "/addMode")
    public JSONObject addMode() {
        SmsApiResponse response = smsTemplatePrervice.addTemplate(request.getParameterMap());
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", response.getCode());
        jsonObject.put("msg", response.getMsg());
        
        if(CollectionUtils.isNotEmpty(response.getRets())) {
            jsonObject.put("rets",response.getRets().get(0));
        }
        return jsonObject;
    }

    @RequestMapping(value = "/updMode")
    public SmsApiResponse updMode() {
        return smsTemplatePrervice.updateTemplate(request.getParameterMap());
    }

    @RequestMapping(value = "/qryMode")
    public SmsApiResponse qryMode() {
        return smsTemplatePrervice.queryTemplate(request.getParameterMap());
    }

    @RequestMapping(value = "/delMode")
    public SmsApiResponse delMode() {
        return smsTemplatePrervice.deleteTemplate(request.getParameterMap());
    }

}
