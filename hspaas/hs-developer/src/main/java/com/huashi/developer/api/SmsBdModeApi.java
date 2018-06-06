package com.huashi.developer.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/smsMode")
public class SmsBdModeApi extends BasicApiSupport {

    @RequestMapping(value = "/addMode", method = { RequestMethod.POST, RequestMethod.GET })
    public void addMode() {

    }

    public void updMode() {
    }

    public void qryMode() {

    }

    public void delMode() {

    }

    public void qryOrderDtl() {

    }

    public void smsSend() {

    }

    public void callback() {

    }

}
