package com.huashi.developer.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huashi.developer.response.sms.SmsApiResponse;

@RestController
@RequestMapping(value = "/api/smsSend")
public class SmsSendApi extends BasicApiSupport {

    @RequestMapping(value = "/smsSend")
    public SmsApiResponse smsSend() {

        
    }

    @RequestMapping(value = "/Callback")
    public SmsApiResponse callback() {

    }

}
