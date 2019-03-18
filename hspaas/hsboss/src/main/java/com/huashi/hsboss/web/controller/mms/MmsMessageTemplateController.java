package com.huashi.hsboss.web.controller.mms;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.mms.task.service.IMmsMtTaskService;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.huashi.sms.passage.context.PassageContext;
import com.huashi.sms.template.context.SmsTemplateContext.ApproveStatus;
import com.huashi.sms.template.context.SmsTemplateContext.ModeOperation;
import com.jfinal.ext.route.ControllerBind;

/**
 * 彩信模板控制器
 * 
 * @author zhengying
 * @version V1.0
 * @date 2019年3月17日 下午3:29:29
 */
@ViewMenu(code = MenuCode.MENU_CODE_10002)
@ControllerBind(controllerKey = "/mms/message_template")
public class MmsMessageTemplateController extends BaseController {

    @BY_NAME
    private IUserService        iUserService;
    @BY_NAME
    private IMmsTemplateService iMmsTemplateService;
    @BY_NAME
    private IMmsMtTaskService   iMmsMtTaskService;

    @AuthCode(code = { OperCode.OPER_CODE_10002001, OperCode.OPER_CODE_10002002, OperCode.OPER_CODE_10002003,
            OperCode.OPER_CODE_10002004, OperCode.OPER_CODE_10002005, OperCode.OPER_CODE_10002006 })
    @ActionMode
    public void index() {
        String userId = getPara("userId");
        String keyword = getPara("keyword");
        String status = getPara("status");
        BossPaginationVo<MmsMessageTemplate> page = iMmsTemplateService.findPageBoos(getPN(), keyword, status, userId);
        setAttr("keyword", keyword);
        if (StringUtils.isNotEmpty(status)) {
            setAttr("status", Integer.parseInt(status));
        }
        setAttr("username", getPara("username"));
        setAttr("page", page);
        setAttr("userId", getParaToInt("userId", -1));
        setAttr("approveStatus", ApproveStatus.values());
        setAttr("userList", iUserService.findUserModels());
    }

    @AuthCode(code = OperCode.OPER_CODE_10002002)
    @ActionMode
    public void add() {
        MmsMessageTemplate messageTemplate = getModel(MmsMessageTemplate.class);
        messageTemplate.setApproveUser(getLoginName());
        messageTemplate.setAppType(ModeOperation.OPERATIONSUPPORT.getValue());
        messageTemplate.setApproveTime(new Date());
        messageTemplate.setStatus(ApproveStatus.SUCCESS.getValue());

        boolean result = iMmsTemplateService.save(messageTemplate);
        renderResultJson(result);
    }

    /**
     * TODO 编辑（跳转到修改页面）
     */
    @AuthCode(code = OperCode.OPER_CODE_10002003)
    @ActionMode
    public void edit() {
        long id = getParaToLong("id");
        MmsMessageTemplate messageTemplate = iMmsTemplateService.get(id);
        setAttr("routeTypes", PassageContext.RouteType.values());
        setAttr("messageTemplate", messageTemplate);
        setAttr("userList", iUserService.findUserModels());
    }

    @AuthCode(code = OperCode.OPER_CODE_10002003)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void update() {
        MmsMessageTemplate template = getModel(MmsMessageTemplate.class, "messageTemplate");
        template.setApproveUser(getLoginName());
        template.setAppType(ModeOperation.OPERATIONSUPPORT.getValue());
        template.setApproveTime(new Date());
        boolean result = iMmsTemplateService.update(template);
        renderResultJson(result);
    }

    @AuthCode(code = OperCode.OPER_CODE_10002006)
    @ActionMode
    public void audit() {
        setAttr("templateStatus", ApproveStatus.values());
        setAttr("messageTemplate", iMmsTemplateService.get(getParaToLong("id")));
    }

    @AuthCode(code = OperCode.OPER_CODE_10002006)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void approve() {
        boolean result = iMmsTemplateService.approve(getParaToLong("id"), getParaToInt("status"), getPara("remark"));
        logger.info("账号: [" + getLoginName() + "] 审批模板:[" + getParaToLong("id") + "], 状态: [" + getParaToInt("status")
                    + "], 备注:[" + getParaToInt("status") + "], 审批结果：[" + result + "]");
        renderResultJson(result);
    }

    @AuthCode(code = OperCode.OPER_CODE_10002004)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void delete() {
        long id = getParaToLong("id");
        boolean result = iMmsTemplateService.deleteById(id);
        renderResultJson(result);
    }

    @AuthCode(code = OperCode.OPER_CODE_10002001)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void loadingRedis() {
        renderResultJson(iMmsTemplateService.reloadToRedis());
    }
}
