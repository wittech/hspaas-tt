package com.huashi.hsboss.web.controller;

import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * Created by youngmeng on 2016/12/20.
 */
@ControllerBind(controllerKey = "/")
public class IndexController extends BaseController {

    @AuthCode(code = OperCode.OPER_CODE_COMMON)
    public void index(){
        redirect("/account");
    }
}
