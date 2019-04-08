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
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.mms.task.service.IMmsMtTaskService;
import com.huashi.mms.template.constant.MmsTemplateContext;
import com.huashi.mms.template.constant.MmsTemplateContext.ApproveStatus;
import com.huashi.mms.template.constant.MmsTemplateContext.PassageTemplateStatus;
import com.huashi.mms.template.domain.MmsMessageTemplate;
import com.huashi.mms.template.domain.MmsPassageMessageTemplate;
import com.huashi.mms.template.exception.ModelApplyException;
import com.huashi.mms.template.service.IMmsPassageTemplateService;
import com.huashi.mms.template.service.IMmsTemplateBodyService;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.huashi.sms.passage.context.PassageContext;
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
    private IUserService               iUserService;
    @BY_NAME
    private IMmsMtTaskService          iMmsMtTaskService;
    @BY_NAME
    private IMmsTemplateService        iMmsTemplateService;
    @BY_NAME
    private IMmsPassageService         iMmsPassageService;
    @BY_NAME
    private IMmsPassageTemplateService iMmsPassageTemplateService;
    @BY_NAME
    private IMmsTemplateBodyService    iMmsTemplateBodyService;

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
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void create() {
        setAttr("templateStatus", ApproveStatus.values());
        setAttr("routeTypes", PassageContext.RouteType.values());
        setAttr("userList", iUserService.findUserModels());
        setAttr("mediaTypes", MmsTemplateContext.MediaType.values());
    }

    @AuthCode(code = OperCode.OPER_CODE_10002002)
    @ActionMode
    public void add() {
        try {
            MmsMessageTemplate messageTemplate = getModel(MmsMessageTemplate.class);
            messageTemplate.setApproveUser(getLoginName());
            messageTemplate.setAppType(ModeOperation.OPERATIONSUPPORT.getValue());
            messageTemplate.setApproveTime(new Date());
            messageTemplate.setStatus(ApproveStatus.SUCCESS.getValue());

            String modelId = iMmsTemplateService.save(messageTemplate);
            renderResultJson(StringUtils.isNotEmpty(modelId));
        } catch (Exception e) {
        }

    }

    /**
     * 编辑（跳转到修改页面）
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
        try {
            renderResultJson(iMmsTemplateService.update(template));
        } catch (ModelApplyException e) {
            logger.error("修改彩信模板失败", e);
            renderResultJson(false);
        }

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

    /**
     * 跳转通道模板报备页面
     */
    public void apply() {
        setAttr("messageTemplate", iMmsTemplateService.get(getParaToInt("id")));
        setAttr("passageList", iMmsPassageService.findAll());
        setAttr("templates", iMmsPassageTemplateService.getByMmsTemplateId(getParaToInt("id")));
        setAttr("passageTemplateStatus", PassageTemplateStatus.values());
    }

    /**
     * 通道模板报备
     */
    public void passage_model() {
        renderResultJson(iMmsPassageTemplateService.save(getModel(MmsPassageMessageTemplate.class,
                                                                  "mmsPassageMessageTemplate")));
    }

    /**
     * 修改模板信息
     */
    public void update_passage_model() {
        renderResultJson(iMmsPassageTemplateService.update(getModel(MmsPassageMessageTemplate.class,
                                                                    "mmsPassageMessageTemplate")));
    }

    /**
     * 彩信预览
     */
    public void preview() {
        try {
            if (StringUtils.isNotEmpty(getPara("title"))) {
                setAttr("title", getPara("title"));
                setAttr("bodies", iMmsTemplateBodyService.getBodies(getPara("resource")));
            } else {
                MmsMessageTemplate template = null;
                if (StringUtils.isNotBlank(getPara("modelId"))) {
                    template = iMmsTemplateService.getByModelId(getPara("modelId"));
                } else if (StringUtils.isNotBlank(getPara("templateId"))) {
                    template = iMmsTemplateService.get(getParaToInt("templateId"));
                }

                if (template == null) {
                    return;
                }

                setAttr("title", template.getTitle());
                setAttr("bodies", iMmsTemplateBodyService.getBodiesByTemplateId(template.getId()));
            }
        } catch (Exception e) {
            logger.error("预览失败", e);
        }

    }
}
