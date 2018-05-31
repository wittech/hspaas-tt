/**
 * 
 */
package com.huashi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

// import com.huashi.web.filter.PermissionClear;

/**
 * api 文档
 * 
 * @author Administrator
 */
@Controller
// @PermissionClear
@RequestMapping("/api")
public class ApiDocumentController extends BaseController {

    /**
     * TODO 短信API
     * 
     * @return
     */
    @RequestMapping(value = "/sms", method = RequestMethod.GET)
    public String sms() {
        return "/api/sms";
    }

    /**
     * TODO 帮助支持
     * 
     * @return
     */
    @RequestMapping(value = "/help", method = RequestMethod.GET)
    public String help() {
        return "/api/help";
    }

    /**
     * TODO 关于我们
     * 
     * @return
     */
    @RequestMapping(value = "/about", method = RequestMethod.GET)
    public String about() {
        return "/api/about";
    }

    /**
     * 公司简介
     * 
     * @return
     */
    @RequestMapping(value = "/company", method = RequestMethod.GET)
    public String company() {
        return "/api/company";
    }

    /**
     * 联系我们
     * 
     * @return
     */
    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contact() {
        return "/api/contact";
    }

    /**
     * 招贤纳士
     * 
     * @return
     */
    @RequestMapping(value = "/job", method = RequestMethod.GET)
    public String job() {
        return "/api/job";
    }

}
