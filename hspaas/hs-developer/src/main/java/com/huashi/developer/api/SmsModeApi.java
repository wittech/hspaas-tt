package com.huashi.developer.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huashi.developer.prervice.SmsTemplatePrervice;
import com.huashi.developer.response.sms.SmsApiResponse;

@RestController
@RequestMapping(value = "/api/mode")
public class SmsModeApi extends BasicApiSupport {
    
    @Autowired
    private SmsTemplatePrervice smsTemplatePrervice;

    @RequestMapping(value = "/addMode")
    public SmsApiResponse addMode() {
        return smsTemplatePrervice.addTemplate(request.getParameterMap());
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
