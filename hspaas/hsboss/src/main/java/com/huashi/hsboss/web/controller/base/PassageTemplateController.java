//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月25日       杨猛　     新建
//*********************************************************************
package com.huashi.hsboss.web.controller.base;

import java.util.ArrayList;
import java.util.List;

import com.huashi.common.passage.context.TemplateEnum.PassageTemplateDetailType;
import com.huashi.common.passage.domain.PassageTemplate;
import com.huashi.common.passage.domain.PassageTemplateDetail;
import com.huashi.common.passage.dto.ParseParamDto;
import com.huashi.common.passage.dto.RequestParamDto;
import com.huashi.common.passage.service.IPassageTemplateService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

import net.sf.json.JSONArray;

/**
 * @author ym
 * @created_at 2016年8月25日下午4:13:37
 */
@ControllerBind(controllerKey="/base/passage_template")
@ViewMenu(code=MenuCode.MENU_CODE_1001002)
public class PassageTemplateController extends BaseController{

	
	@BY_NAME
	private IPassageTemplateService iPassageTemplateService;
	
	public void index(){
		String keyword = getPara("keyword");
		int type = getParaToInt("type",-1);
		BossPaginationVo<PassageTemplate> page = iPassageTemplateService.findPage(getPN(), keyword, type);
		setAttr("page", page);
		setAttr("keyword", keyword);
		setAttr("type", type);
	}
	
	public void add(){
		setAttr("protocolTypes", CommonContext.ProtocolType.values());
	}
	
	public void create(){
		PassageTemplate template = getModel(PassageTemplate.class, "template");
		String templateDetails = getPara("templateDetails");
		String[] tdArray = templateDetails.split(",");
		for(String templateDetail : tdArray){
			PassageTemplateDetail detail = getModel(PassageTemplateDetail.class, templateDetail);
			
			//请求参数设置
			int currentDetailReqParamNum = getParaToInt("reqParam_"+templateDetail, 0);
			List<RequestParamDto> reqParams = new ArrayList<RequestParamDto>();
			for(int i = 0;i< currentDetailReqParamNum;i++){
				RequestParamDto reqParam = getModel(RequestParamDto.class, "reqParam_"+templateDetail+"_"+i);
				reqParams.add(reqParam);
			}
			
			//解析参数设置
			int currentDetailParseParamNum = getParaToInt("parseRule_"+templateDetail, 0);
			List<ParseParamDto> parseParams = new ArrayList<ParseParamDto>();
			for(int i = 0;i< currentDetailParseParamNum;i++){
				ParseParamDto parseParam = getModel(ParseParamDto.class, "parseRule_"+templateDetail+"_"+i);
				parseParams.add(parseParam);
			}
			detail.setParams(JSONArray.fromObject(reqParams).toString());
			detail.setPosition(JSONArray.fromObject(parseParams).toString());
			template.getDetailList().add(detail);
		}
		boolean result = iPassageTemplateService.create(template);
		renderResultJson(result);
	}
	
	public void edit(){
		PassageTemplate template = iPassageTemplateService.findById(getParaToInt("id"));
		setAttr("template", template);
		setAttr("protocolTypes", CommonContext.ProtocolType.values());
	}
	
	public void update(){
		PassageTemplate template = getModel(PassageTemplate.class, "template");
		String templateDetails = getPara("templateDetails");
		String[] tdArray = templateDetails.split(",");
		for(String templateDetail : tdArray){
			PassageTemplateDetail detail = getModel(PassageTemplateDetail.class, templateDetail);
			
			//请求参数设置
			int currentDetailReqParamNum = getParaToInt("reqParam_"+templateDetail, 0);
			List<RequestParamDto> reqParams = new ArrayList<RequestParamDto>();
			for(int i = 0;i< currentDetailReqParamNum;i++){
				RequestParamDto reqParam = getModel(RequestParamDto.class, "reqParam_"+templateDetail+"_"+i);
				reqParams.add(reqParam);
			}
			
			//解析参数设置
			int currentDetailParseParamNum = getParaToInt("parseRule_"+templateDetail, 0);
			List<ParseParamDto> parseParams = new ArrayList<ParseParamDto>();
			for(int i = 0;i< currentDetailParseParamNum;i++){
				ParseParamDto parseParam = getModel(ParseParamDto.class, "parseRule_"+templateDetail+"_"+i);
				parseParams.add(parseParam);
			}
			detail.setParams(JSONArray.fromObject(reqParams).toString());
			detail.setPosition(JSONArray.fromObject(parseParams).toString());
			template.getDetailList().add(detail);
		}
		boolean result = iPassageTemplateService.update(template);
		renderResultJson(result);
	}
	
	public void ruleView(){
		int ruleType = getParaToInt("ruleType");
		setAttr("ruleType", PassageTemplateDetailType.getByValue(ruleType));
	}
	
	public void addRow(){
		setAttr("rowType", getParaToInt("rowType", 1));
		setAttr("ruleType", getParaToInt("ruleType"));
	}
	
	public void delete(){
		boolean result = iPassageTemplateService.deleteById(getParaToInt("id"));
		renderResultJson(result);
	}
}
