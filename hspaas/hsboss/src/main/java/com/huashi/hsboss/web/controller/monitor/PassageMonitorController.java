/**
 * 
 */
package com.huashi.hsboss.web.controller.monitor;

import java.util.List;

import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.monitor.passage.model.PassagePullReport;
import com.huashi.monitor.passage.service.IPassageMonitorService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 监控中心管理
 * 
 * @author Administrator
 */
@ViewMenu(code = MenuCode.MENU_CODE_8001001)
@ControllerBind(controllerKey = "/monitor")
public class PassageMonitorController extends BaseController {

    @BY_NAME
    private IPassageMonitorService iPassageMonitorService;

    /**
     * 通道自取报告
     */
    @AuthCode(code = { OperCode.OPER_CODE_8001001001 })
    @ActionMode
    public void inviteIndex() {
        List<PassagePullReport> list = iPassageMonitorService.findPassagePullReport();
        setAttr("list", list);
    }

}
