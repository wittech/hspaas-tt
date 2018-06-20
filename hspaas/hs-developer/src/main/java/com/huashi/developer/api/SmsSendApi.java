package com.huashi.developer.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huashi.developer.prervice.SmsPrervice;
import com.huashi.developer.response.sms.SmsApiResponse;

@RestController
@RequestMapping(value = "/api/smsSend")
public class SmsSendApi extends BasicApiSupport {
    
    @Autowired
    private SmsPrervice smsPrervice;

    @RequestMapping(value = "/smsSend")
    public SmsApiResponse smsSend() {
        return smsPrervice.sendMessage(request.getParameterMap(), getClientIp());
    }

    @RequestMapping(value = "/Callback")
    public SmsApiResponse callback() {
        return null;
    }

}
