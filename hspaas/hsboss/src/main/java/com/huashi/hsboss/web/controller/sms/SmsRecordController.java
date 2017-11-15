package com.huashi.hsboss.web.controller.sms;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.huashi.common.util.DateUtil;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.ResponseMessage;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.huashi.sms.record.domain.SmsApiFailedRecord;
import com.huashi.sms.record.service.ISmsApiFaildRecordService;
import com.huashi.sms.record.service.ISmsMoMessageService;
import com.huashi.sms.record.service.ISmsMtProcessFailedService;
import com.huashi.sms.record.service.ISmsMtSubmitService;
import com.huashi.sms.task.domain.SmsMtTaskPackets;
import com.huashi.sms.task.service.ISmsMtTaskService;
import com.huashi.sms.template.domain.MessageTemplate;
import com.huashi.sms.template.service.ISmsTemplateService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 短信记录查询 Author youngmeng Created 2016-10-12 20:02
 */
@ControllerBind(controllerKey = "/sms/record")
@ViewMenu(code = { MenuCode.MENU_CODE_2002001, MenuCode.MENU_CODE_2002002, MenuCode.MENU_CODE_2002003,
		MenuCode.MENU_CODE_2002004, MenuCode.MENU_CODE_2002005, MenuCode.MENU_CODE_2002006,
		MenuCode.MENU_CODE_2002007 })
public class SmsRecordController extends BaseController {

	@Inject.BY_NAME
	private ISmsMoMessageService iSmsMoMessageService;
	@Inject.BY_NAME
	private ISmsMtProcessFailedService iSmsMtProcessFailedService;
	@Inject.BY_NAME
	private ISmsApiFaildRecordService iSmsApiFaildRecordService;
	@Inject.BY_NAME
	private ISmsMtTaskService iSmsMtTaskService;
	@Inject.BY_NAME
	private ISmsMtSubmitService iSmsMtSubmitService;
	@Inject.BY_NAME
	private ISmsPassageService iSmsPassageService;
	@Inject.BY_NAME
	private ISmsTemplateService iSmsTemplateService;

	private static final Integer UNWDER_WAY = 0;
	private static final Integer COMPLETED = 1;

	public void index() {
		redirect("/sms/record/under_way_list");
	}

	/**
	 * 进行中的短信任务
	 */
	public void under_way_list() {
		Map<String, Object> condition = appendTaskQueryParams(UNWDER_WAY);
		
		setAttr("page", iSmsMtTaskService.findPage(condition));
		setAttrs(condition);
	}

	/**
	 * 已完成的短信任务
	 */
	@ViewMenu(code = MenuCode.MENU_CODE_2002007)
	public void completed_list() {
		Map<String, Object> condition = appendTaskQueryParams(COMPLETED);
		
		setAttr("page", iSmsMtTaskService.findPage(condition));
		setAttrs(condition);
		
	}

	/**
	 * 子任务查询
	 */
	@ViewMenu(code = MenuCode.MENU_CODE_2002006)
	public void child_task() {
		List<SmsMtTaskPackets> taskList = iSmsMtTaskService.findChildTaskBySid(getParaToLong("sid"));
		setAttr("taskList", taskList);
	}

	/**
	 * 子任务查询
	 */
	@ViewMenu(code = MenuCode.MENU_CODE_2002007)
	public void complate_child_task() {
		List<SmsMtTaskPackets> taskList = iSmsMtTaskService.findChildTaskBySid(getParaToLong("sid"));
		setAttr("taskList", taskList);
	}

	/**
	 * 
	   * TODO 根据运营商查询通道列表信息
	 */
	public void passage_list() {
		renderJson(iSmsPassageService.findByCmcpOrAll(getParaToInt("cmcp")));
	}

	/**
	 * 
	   * TODO 切换通道
	 */
	public void update_passage() {
		try {
			renderResultJson(iSmsMtTaskService.changeTaskPacketsPassage(getParaToLong("childId"), getParaToInt("passageId")));
		} catch (Exception e) {
			renderResultJson(false);
		}
	}

	/**
	 * 
	   * TODO 查询短信模板
	 */
	public void sms_template() {
		setAttr("template", iSmsTemplateService.get(getParaToLong("templateId")));
	}

	/**
	 * 
	   * TODO 修改短信模板白名单词汇
	 */
	public void update_template() {
		MessageTemplate template = new MessageTemplate();
		template.setWhiteWord(getPara("words"));
		template.setId(getParaToLong("templateId"));
		renderResultJson(iSmsTemplateService.update(template));
	}

	public void add_template() {
	}

	/**
	 * 更新短信内容
	 */
	public void update_content() {
		renderResultJson(iSmsMtTaskService.updateSmsContent(getParaToLong("sid"), getPara("content")));
	}

	/**
	 * 子任务强制通过
	 */
	public void pass_task() {
		try {
			renderResultJson(iSmsMtTaskService.updateTaskPacketsStatus(getParaToLong("childId"), getParaToInt("status")));
		} catch (Exception e) {
			renderResultJson(false);
		}
	}

	/**
	 * 主任务强制通过
	 */
	public void main_task_force_pass() {
		boolean result = iSmsMtTaskService.updateMainTaskByForcePass(getParaToLong("sid"));
		renderResultJson(result);
	}

	/**
	 * 
	   * TODO 拼接查询条件
	   * @return
	 */
	private Map<String, Object> appendTaskQueryParams(int searchType) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("sid", getParaToLong("sid"));
		paramMap.put("mobile", getPara("mobile"));
		paramMap.put("content", getPara("content"));
		paramMap.put("username", getPara("username"));
		paramMap.put("userId", getParaToInt("userId", -1));
		
		String startDate = getPara("startDate");
		// 已完成需要设置当前时间值 待处理不需要
		if(COMPLETED == searchType && StringUtils.isEmpty(getPara("startDate"))) {
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
	 * 调用失败记录
	 */
	public void invoke_fail_list() {
		String keyword = getPara("keyword");
		BossPaginationVo<SmsApiFailedRecord> page = iSmsApiFaildRecordService.findPage(getPN(), keyword);
		setAttr("page", page);
		setAttr("keyword", keyword);
	}

	/**
	 * 处理失败记录
	 */
	public void disponse_fail_list() {
		String keyword = getPara("keyword");
		Long sid = getParaToLong("sid");
		setAttr("page", iSmsMtProcessFailedService.findPage(getPN(), keyword, sid));
		setAttr("keyword", keyword);
		setAttr("sid", sid);
	}

	/**
	 * 短信发送记录(短信下行记录查询 submit)
	 */
	public void send_record_list() {
		Map<String, Object> condition = appendQueryParams();

		setAttr("page", iSmsMtSubmitService.findPage(condition));
		setAttrs(condition);
		setAttr("passageList", iSmsPassageService.findAll());
	}
	
	/**
	 * 
	   * TODO 获取一小时前
	   * @return
	 */
	private static String getStartDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:00:00");
		return df.format(calendar.getTime());
	}
	
	/**
	 * 
	   * TODO 拼接查询条件
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
		if(StringUtils.isEmpty(getPara("startDate"))) {
//			startDate = DateUtil.getDayStr(new Date()) + " 00:00:00";
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
	public void up_revice_list() {
		setAttr("page", iSmsMoMessageService.findPage(getPN(), getPara("keyword")));
		setAttr("keyword", getPara("keyword"));
	}

	/**
	 * 
	 * TODO 子任务驳回
	 */
	public void refuse_task() {
		renderResultJson(iSmsMtTaskService.doRejectInTaskPackets(getParaToLong("childId")));
	}

	/**
	 * 
	 * TODO 批量切换通道
	 */
	public void batchSwitchPassage() {
		try {
			renderResultJson(iSmsMtTaskService.changeTaskPacketsPassage(getPara("ids"), getParaToInt("switchPassageId")));
		} catch (Exception e) {
			renderResultJson(false);
		}
	}

	/**
	 * 
	 * TODO 主任务驳回
	 */
	public void reject() {
		try {
			renderResultJson(iSmsMtTaskService.doRejectInTask(getPara("id")));
		} catch (Exception e) {
			renderResultJson(false);
		}
	}

	/**
	 * 
	 * TODO 批量驳回任务
	 */
	public void batchRefuse() {
		try {
			renderResultJson(iSmsMtTaskService.doRejectInTask(getPara("ids")));
		} catch (Exception e) {
			renderResultJson(false);
		}
	}

	/**
	 * 
	 * TODO 子任务驳回
	 */
	public void refuseTask() {
		try {
			renderResultJson(iSmsMtTaskService.doRejectInTaskPackets(getParaToLong("id")));
		} catch (Exception e) {
			renderResultJson(false);
		}
	}

	/**
	 * 
	 * TODO 批量审批通过
	 */
	public void batchPass() {
		try {
			renderJson(iSmsMtTaskService.doForcePass(getPara("ids")));
		} catch (Exception e) {
			renderJson(new ResponseMessage(e.getMessage()));
		}
	}

	/**
	 * 
	 * TODO 重新分包
	 */
	public void repeatTask() {
		renderResultJson(iSmsMtTaskService.doRePackets(getParaToLong("id")));
	}

	/**
	 * 
	 * TODO 批量重新分包
	 */
	public void batchRepeatTask() {
		renderResultJson(iSmsMtTaskService.batchDoRePackets(getPara("ids")));
	}

	/**
	 * 
	 * TODO 批量更新短信内容
	 */
	public void batchUpdateContent() {
		renderResultJson(iSmsMtTaskService.batchUpdateSmsContent(getPara("sidArray"), getPara("content")));
	}

	/**
	 * 
	 * TODO 批量审批同短信短信待处理任务
	 */
	public void sameContentPass() {
		try {
			int result = iSmsMtTaskService.doPassWithSameContent(getPara("content"),
					getParaToInt("like_pattern", 0) == 1);
			if (result >= 0) {
				renderResultJson(true, String.format("共处理：%d条", result));
				return;
			}

			renderResultJson(false);
		} catch (Exception e) {
			renderResultJson(false);
		}
	}

}
