package com.huashi.hsboss.web.controller.mms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huashi.common.user.service.IUserService;
import com.huashi.common.util.DateUtil;
import com.huashi.constants.ResponseMessage;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.mms.record.service.IMmsMoMessageService;
import com.huashi.mms.record.service.IMmsMtSubmitService;
import com.huashi.mms.task.domain.MmsMtTaskPackets;
import com.huashi.mms.task.service.IMmsMtTaskService;
import com.huashi.mms.template.service.IMmsTemplateService;
import com.jfinal.ext.route.ControllerBind;

@ControllerBind(controllerKey = "/mms/record")
@ViewMenu(code = { MenuCode.MENU_CODE_10001001, MenuCode.MENU_CODE_10001002, MenuCode.MENU_CODE_10001003,
                  MenuCode.MENU_CODE_10001004 })
public class MmsRecordController extends BaseController {

    @Inject.BY_NAME
    private IMmsMoMessageService iMmsMoMessageService;
    @Inject.BY_NAME
    private IMmsMtTaskService    iMmsMtTaskService;
    @Inject.BY_NAME
    private IMmsMtSubmitService  iMmsMtSubmitService;
    @Inject.BY_NAME
    private IMmsPassageService   iMmsPassageService;
    @Inject.BY_NAME
    private IMmsTemplateService  iMmsTemplateService;
    @Inject.BY_NAME
    private IUserService         iUserService;

    private static final Integer UNWDER_WAY = 0;
    private static final Integer COMPLETED  = 1;

    public void index() {
        redirect("/mms/record/under_way_list");
    }

    /**
     * 进行中的短信任务
     */
    @ViewMenu(code = MenuCode.MENU_CODE_10001001)
    @AuthCode(code = { OperCode.OPER_CODE_10001001001, OperCode.OPER_CODE_10001001002, OperCode.OPER_CODE_10001001003,
            OperCode.OPER_CODE_10001001004, OperCode.OPER_CODE_10001001005, OperCode.OPER_CODE_10001001006,
            OperCode.OPER_CODE_10001001007, OperCode.OPER_CODE_10001001008 })
    @ActionMode
    public void under_way_list() {
        Map<String, Object> condition = appendTaskQueryParams(UNWDER_WAY);

        setAttr("page", iMmsMtTaskService.findPage(condition));
        setAttrs(condition);

        setAttr("passageList", iMmsPassageService.findAll());
        setAttr("userList", iUserService.findAll());
    }

    /**
     * 已完成的彩信任务
     */
    @ViewMenu(code = MenuCode.MENU_CODE_10001003)
    @AuthCode(code = { OperCode.OPER_CODE_10001003001, OperCode.OPER_CODE_10001003002 })
    @ActionMode
    public void completed_list() {
        Map<String, Object> condition = appendTaskQueryParams(COMPLETED);

        setAttr("page", iMmsMtTaskService.findPage(condition));
        setAttrs(condition);
        setAttr("userList", iUserService.findAll());
    }

    /**
     * 子任务查询
     */
    @ViewMenu(code = MenuCode.MENU_CODE_10001001)
    @AuthCode(code = OperCode.OPER_CODE_10001001008)
    @ActionMode
    public void child_task() {
        List<MmsMtTaskPackets> taskList = iMmsMtTaskService.findChildTaskBySid(getParaToLong("sid"));
        setAttr("taskList", taskList);
    }

    /**
     * 子任务查询
     */
    @ViewMenu(code = MenuCode.MENU_CODE_10001003)
    @AuthCode(code = OperCode.OPER_CODE_10001003001)
    @ActionMode
    public void complate_child_task() {
        List<MmsMtTaskPackets> taskList = iMmsMtTaskService.findChildTaskBySid(getParaToLong("sid"));
        setAttr("taskList", taskList);
    }

    /**
     * TODO 根据运营商查询通道列表信息
     */
    public void passage_list() {
        renderJson(iMmsPassageService.findByCmcpOrAll(getParaToInt("cmcp")));
    }

    /**
     * TODO 拼接查询条件
     * 
     * @return
     */
    private Map<String, Object> appendTaskQueryParams(int searchType) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sid", getParaToLong("sid"));
        paramMap.put("mobile", getPara("mobile"));
        paramMap.put("content", getPara("content"));
        paramMap.put("userId", getParaToInt("userId", -1));

        String startDate = getPara("startDate");
        // 已完成需要设置当前时间值 待处理不需要
        if (COMPLETED == searchType && StringUtils.isEmpty(getPara("startDate"))) {
            startDate = DateUtil.getDayStr(new Date()) + " 00:00:00";
        }
        paramMap.put("templateId", getParaToLong("template_id"));
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", getPara("endDate"));
        paramMap.put("processStatus", getParaToInt("processStatus", -1));
        paramMap.put("approveStatus", getParaToInt("approveStatus", -1));
        paramMap.put("currentPage", getPN());
        paramMap.put("searchType", searchType);

        return paramMap;
    }

    /**
     * 短信发送记录(短信下行记录查询 submit)
     */
    @ViewMenu(code = MenuCode.MENU_CODE_10001002)
    @AuthCode(code = { OperCode.OPER_CODE_10001003002, OperCode.OPER_CODE_10001003001 })
    @ActionMode
    public void send_record_list() {
        Map<String, Object> condition = appendQueryParams();

        setAttr("page", iMmsMtSubmitService.findPage(condition));
        setAttrs(condition);
        setAttr("passageList", iMmsPassageService.findAll());
    }

    /**
     * TODO 获取一小时前
     * 
     * @return
     */
    private static String getStartDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
        return df.format(calendar.getTime());
    }

    /**
     * TODO 拼接查询条件
     * 
     * @return
     */
    private Map<String, Object> appendQueryParams() {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sid", getPara("sid"));
        paramMap.put("mobile", getPara("mobile"));
        paramMap.put("content", getPara("content"));
        paramMap.put("username", getPara("username"));
        paramMap.put("userId", getParaToInt("userId", -1));

        String startDate = getPara("startDate");
        if (StringUtils.isEmpty(getPara("startDate"))) {
            // startDate = DateUtil.getDayStr(new Date()) + " 00:00:00";
            startDate = getStartDate();
        }
        paramMap.put("startDate", startDate);
        paramMap.put("endDate", getPara("endDate"));
        paramMap.put("passageId", getPara("passageId"));
        paramMap.put("sendStatus", getParaToInt("sendStatus", -1));
        paramMap.put("deliverStatus", getParaToInt("deliverStatus", -1));
        paramMap.put("currentPage", getPN());
        return paramMap;
    }

    /**
     * 上行接收记录
     */
    @AuthCode(code = OperCode.OPER_CODE_10001004001)
    @ActionMode
    public void up_revice_list() {
        setAttr("page", iMmsMoMessageService.findPage(getPN(), getPara("keyword")));
        setAttr("keyword", getPara("keyword"));
    }

    /**
     * TODO 批量审批通过
     */
    @AuthCode(code = { OperCode.OPER_CODE_10001001001 })
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void batchPass() {
        try {
            renderJson(iMmsMtTaskService.doTaskApproved(getPara("ids")));
        } catch (Exception e) {
            renderJson(new ResponseMessage(e.getMessage()));
        }
    }

    /**
     * TODO 批量驳回任务
     */
    @AuthCode(code = { OperCode.OPER_CODE_10001001003 })
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void batchRefuse() {
        try {
            renderResultJson(iMmsMtTaskService.doRejectInTask(getPara("ids")));
        } catch (Exception e) {
            renderResultJson(false);
        }
    }

    /**
     * TODO 批量切换通道
     */
    @AuthCode(code = { OperCode.OPER_CODE_10001001005 })
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void batchSwitchPassage() {
        try {
            renderResultJson(iMmsMtTaskService.changeTaskPacketsPassage(getPara("ids"), getParaToInt("switchPassageId")));
        } catch (Exception e) {
            renderResultJson(false);
        }
    }

    /**
     * TODO 批量重新分包
     */
    @AuthCode(code = { OperCode.OPER_CODE_10001001006 })
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void repeatTask() {
        renderResultJson(iMmsMtTaskService.batchDoRePackets(getPara("ids")));
    }

    /**
     * TODO 驳回（子任务）
     */
    public void refuse_task() {
        renderResultJson(iMmsMtTaskService.doRejectInTaskPackets(getParaToLong("childId")));
    }

    /**
     * 强制通过（子任务）
     */
    public void pass_task() {
        try {
            renderResultJson(iMmsMtTaskService.updateTaskPacketsStatus(getParaToLong("childId"), getParaToInt("status")));
        } catch (Exception e) {
            renderResultJson(false);
        }
    }

}
