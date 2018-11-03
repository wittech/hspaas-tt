package com.huashi.hsboss.web.controller.base;

import com.huashi.common.settings.context.SettingsContext;
import com.huashi.common.settings.domain.SystemConfig;
import com.huashi.common.settings.service.ISystemConfigService;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.domain.SmsPassageGroup;
import com.huashi.sms.passage.service.ISmsPassageGroupService;
import com.jfinal.ext.route.ControllerBind;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by youngmeng on 2016/10/20.
 */
@ViewMenu(code= MenuCode.MENU_CODE_1001002)
@ControllerBind(controllerKey = "/base/system_config")
public class SystemConfigController extends BaseController {

    @Inject.BY_NAME
    private ISystemConfigService iSystemConfigService;
    @Inject.BY_NAME
    private ISmsPassageGroupService iSmsPassageGroupService;

    @AuthCode(code= {OperCode.OPER_CODE_1001002001})
	@ActionMode
    public void index() {
        String currentType = getPara("currentType");
        SettingsContext.SystemConfigType[] types = SettingsContext.SystemConfigType.values();
        if(StringUtils.isBlank(currentType)){
            currentType = types[0].name();
        }
        List<SystemConfig> configList = iSystemConfigService.findByType(currentType);
        setAttr("types",types);
        setAttr("configList",configList);
        setAttr("currentType",currentType);
    }

    @AuthCode(code= {OperCode.OPER_CODE_1001002001})
  	@ActionMode
    public void edit() {
        SystemConfig config = iSystemConfigService.findById(getParaToInt("id"));
        if(config.getType().equals(SettingsContext.SystemConfigType.USER_DEFAULT_PASSAGE_GROUP.name())){
            if(config.getAttrKey().toUpperCase().equals(SettingsContext.UserDefaultPassageGroupKey.SMS_DEFAULT_PASSAGE_GROUP.name())){
                List<SmsPassageGroup> groupList = iSmsPassageGroupService.findAll();
                setAttr("groupList",groupList);
            }else if(config.getAttrKey().toUpperCase().equals(SettingsContext.UserDefaultPassageGroupKey.FS_DEFAULT_PASSAGE_GROUP.name())){

            }else if(config.getAttrKey().toUpperCase().equals(SettingsContext.UserDefaultPassageGroupKey.VS_DEFAULT_PASSAGE_GROUP.name())){

            }
        }
        setAttr("config",config);
    }

    @AuthCode(code= {OperCode.OPER_CODE_1001002001})
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void update() {
        SystemConfig config = getModel(SystemConfig.class,"config");
        config.setModifyTime(new Date());
        Map<String,Object> resultMap = iSystemConfigService.update(config);
        renderJson(resultMap);
    }

    public void delete() {
        boolean result = iSystemConfigService.deleteById(getParaToInt("id"));
        renderResultJson(result);
    }
}
