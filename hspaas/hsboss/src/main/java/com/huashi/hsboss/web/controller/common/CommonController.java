package com.huashi.hsboss.web.controller.common;

import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.sms.task.service.ISmsMtTaskService;
import com.jfinal.ext.route.ControllerBind;
import net.sf.json.JSONObject;

/**
 * Created by youngmeng on 2016/11/14.
 */
@ControllerBind(controllerKey = "/common")
public class CommonController extends BaseController {


    @Inject.BY_NAME
    private ISmsMtTaskService iSmsMtTaskService;

    public void not_found(){

    }

    public void error(){

    }

    public void message_info(){
        int waitTaskCount = iSmsMtTaskService.getWaitSmsTaskCount();
        JSONObject json = new JSONObject();
        json.put("wait_task_count",waitTaskCount);
        renderJson(json);
    }
}
