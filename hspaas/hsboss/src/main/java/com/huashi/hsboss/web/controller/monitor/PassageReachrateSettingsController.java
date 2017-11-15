package com.huashi.hsboss.web.controller.monitor;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.monitor.passage.service.IPassageMonitorService;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageReachrateSettings;
import com.huashi.sms.passage.service.ISmsPassageReachrateSettingsService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.jfinal.ext.route.ControllerBind;

import java.util.Date;
import java.util.List;

/**
 * Created by youngmeng on 2017/3/11.
 */
@ViewMenu(code = MenuCode.MENU_CODE_8003)
@ControllerBind(controllerKey = "/monitor/reachrateSettings")
public class PassageReachrateSettingsController extends BaseController {

    @Inject.BY_NAME
    private ISmsPassageReachrateSettingsService iSmsPassageReachrateSettingsService;

    @Inject.BY_NAME
    private ISmsPassageService iSmsPassageService;

    @Inject.BY_NAME
    private IPassageMonitorService iPassageMonitorService;

    public void index(){
        String keyword = getPara("keyword");
        BossPaginationVo<SmsPassageReachrateSettings> page = iSmsPassageReachrateSettingsService.findPage(getPN(),keyword);
        setAttr("page", page);
        setAttr("keyword", keyword);
    }

    public void add(){
        List<SmsPassage> passageList = iSmsPassageReachrateSettingsService.getPassageByUseable();
        setAttr("passageList", passageList);
    }

    public void create(){
        SmsPassageReachrateSettings data = getModel(SmsPassageReachrateSettings.class,"model");
        data.setStartTime(data.getStartTime() * 60);
        data.setTimeLength(data.getTimeLength() * 60);
        data.setInterval(data.getInterval() * 60);
        data.setStatus(1);
        data.setCreateTime(new Date());
        Integer id = iSmsPassageReachrateSettingsService.create(data);
        if(id > -1){
            data.setId(id);
            iPassageMonitorService.addSmsPassageMonitor(data);
        }
        renderResultJson(id > -1);
    }

    public void edit(){
        SmsPassageReachrateSettings source = iSmsPassageReachrateSettingsService.findById(getParaToLong("id"));
        SmsPassage passage = iSmsPassageService.findById(source.getPassageId().intValue());
        List<SmsPassage> passageList = iSmsPassageReachrateSettingsService.getPassageByUseable();
        setAttr("model",source);
        setAttr("passage", passage);
        setAttr("passageList", passageList);
    }

    public void update(){
        SmsPassageReachrateSettings data = getModel(SmsPassageReachrateSettings.class,"model");
        data.setStartTime(data.getStartTime() * 60);
        data.setTimeLength(data.getTimeLength() * 60);
        data.setInterval(data.getInterval() * 60);
        boolean result = iSmsPassageReachrateSettingsService.update(data);
        renderResultJson(result);
    }


    public void disabled(){
        Long id = getParaToLong("id");
        int flag = getParaToInt("flag");    //1-启用 2-禁用
        SmsPassageReachrateSettings source = iSmsPassageReachrateSettingsService.findById(id);
        source.setStatus(flag);
        boolean result = iSmsPassageReachrateSettingsService.update(source);
        if(result && flag == 1){
            iPassageMonitorService.addSmsPassageMonitor(source);
        }
        renderResultJson(result);
    }


}
