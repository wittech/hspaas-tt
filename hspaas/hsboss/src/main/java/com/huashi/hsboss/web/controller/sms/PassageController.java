package com.huashi.hsboss.web.controller.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.huashi.common.passage.context.TemplateEnum.PassageTemplateType;
import com.huashi.common.passage.domain.PassageTemplate;
import com.huashi.common.passage.dto.ParseParamDto;
import com.huashi.common.passage.dto.RequestParamDto;
import com.huashi.common.passage.service.IPassageTemplateService;
import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext;
import com.huashi.exchanger.service.ISmsProviderService;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.context.PassageContext.PassageSignMode;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageParameter;
import com.huashi.sms.passage.domain.SmsPassageProvince;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.passage.service.ISmsPassageParameterService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.jfinal.ext.route.ControllerBind;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * @author ym
 * @created_at 2016年8月25日下午4:15:11
 */
@ControllerBind(controllerKey = "/sms/passage")
@ViewMenu(code = MenuCode.MENU_CODE_2001001)
public class PassageController extends BaseController {

    @BY_NAME
    private ISmsPassageService iSmsPassageService;
    @BY_NAME
    private IPassageTemplateService iPassageTemplateService;
    @BY_NAME
    private IUserService iUserService;
    @BY_NAME
    private ISmsPassageAccessService iSmsPassageAccessService;
    @BY_NAME
    private ISmsPassageParameterService iSmsPassageParameterService;
    @BY_NAME
    private ISmsProviderService iSmsProviderService;
    @BY_NAME
    private IProvinceService iProvinceService;


    /**
     * 
       * TODO 列表页面
     */
    public void index() {
        String keyword = getPara("keyword");
        BossPaginationVo<SmsPassage> page = iSmsPassageService.findPage(getPN(), keyword);
        setAttr("page", page);
        setAttr("keyword", keyword);
    }

    /**
     * 
       * TODO 跳转添加页面
     */
    public void add() {
        setAttr("templateList", iPassageTemplateService.findListByType(PassageTemplateType.SMS.getValue()));
        setAttr("cmcp", CommonContext.CMCP.values());
        setAttr("provinceList",iProvinceService.findAvaiable());
        setAttr("signModes", PassageSignMode.values());
    }

    /**
     * 
       * TODO 添加
     */
    public void create() {
        SmsPassage passage = getModel(SmsPassage.class, "passage");
        String templateDetails = getPara("templateDetails");
        String provinceCodes = getPara("provinceCodes");
        String[] tdArray = templateDetails.split(",");
        PassageTemplate template = iPassageTemplateService.findById(passage.getHspaasTemplateId());
        for (String templateDetail : tdArray) {
            SmsPassageParameter parameter = getModel(SmsPassageParameter.class, templateDetail);
            parameter.setProtocol(template.getProtocol());
            //请求参数设置
            int currentDetailReqParamNum = getParaToInt("reqParam_" + templateDetail, 0);
            Map<String, String> requestMap = new HashMap<String, String>();
            List<RequestParamDto> reqDtoList = new ArrayList<RequestParamDto>();
            for (int i = 0; i < currentDetailReqParamNum; i++) {
                RequestParamDto reqParam = getModel(RequestParamDto.class, "reqParam_" + templateDetail + "_" + i);
                requestMap.put(reqParam.getRequestName(), reqParam.getDefaultValue());
                reqDtoList.add(reqParam);
            }

            //解析解析设置
            int currentDetailParseParamNum = getParaToInt("parseRule_" + templateDetail, 0);
            Map<String, String> responseMap = new HashMap<String, String>();
            for (int i = 0; i < currentDetailParseParamNum; i++) {
                ParseParamDto parseParam = getModel(ParseParamDto.class, "parseRule_" + templateDetail + "_" + i);
                responseMap.put(parseParam.getParseName(), parseParam.getPosition());
            }
            parameter.setParams(JSONObject.fromObject(requestMap).toString());
            parameter.setParamsDefinition(JSONArray.fromObject(reqDtoList).toString());
            parameter.setPosition(JSONObject.fromObject(responseMap).toString());
            passage.getParameterList().add(parameter);

        }
        passage.setCreateTime(new Date());
        passage.setStatus(0);
        Map<String,Object> map = iSmsPassageService.create(passage,provinceCodes);
        renderJson(map);
    }

    public void templateView() {
        int templateId = getParaToInt("templateId");
        PassageTemplate template = iPassageTemplateService.findById(templateId);
        setAttr("template", template);
    }

    /**
     * 
       * TODO 跳转修改页面
     */
    public void edit() {
        SmsPassage passage = iSmsPassageService.findById(getParaToInt("id"));
        UserProfile user = iUserService.getProfileByUserId(passage.getExclusiveUserId());
        setAttr("passage", passage);
        setAttr("templateList", iPassageTemplateService.findListByType(PassageTemplateType.SMS.getValue()));
        setAttr("signModes", PassageSignMode.values());
        List<Province> provinceList = iProvinceService.findAvaiable();
        Province qg = new Province();
        qg.setCode(0);
        qg.setName("全国");
        qg.setId(0);
        provinceList.add(qg);
        List<SmsPassageProvince> passageProvinceList = iSmsPassageService
                .getPassageProvinceById(passage.getId());
        out:for(Province province : provinceList){
            for(SmsPassageProvince passageProvince : passageProvinceList){
                if(province.getCode().equals(passageProvince.getProvinceCode())){
                    province.setSelect(1);
                    continue out;
                }
            }
        }
        setAttr("provinceList",provinceList);
        setAttr("cmcp", CommonContext.CMCP.values());
        if (user != null) {
            setAttr("selectUserName", user.getFullName());
        }

    }

    public void update() {
        SmsPassage passage = getModel(SmsPassage.class, "passage");
        String templateDetails = getPara("templateDetails");
        String[] tdArray = templateDetails.split(",");
        PassageTemplate template = iPassageTemplateService.findById(passage.getHspaasTemplateId());
        for (String templateDetail : tdArray) {
            SmsPassageParameter parameter = getModel(SmsPassageParameter.class, templateDetail);
            parameter.setProtocol(template.getProtocol());
            //请求参数设置
            int currentDetailReqParamNum = getParaToInt("reqParam_" + templateDetail, 0);
            Map<String, String> requestMap = new HashMap<String, String>();
            List<RequestParamDto> reqDtoList = new ArrayList<RequestParamDto>();
            for (int i = 0; i < currentDetailReqParamNum; i++) {
                RequestParamDto reqParam = getModel(RequestParamDto.class, "reqParam_" + templateDetail + "_" + i);
                requestMap.put(reqParam.getRequestName(), reqParam.getDefaultValue());
                reqDtoList.add(reqParam);
            }

            //解析解析设置
            int currentDetailParseParamNum = getParaToInt("parseRule_" + templateDetail, 0);
            Map<String, String> responseMap = new HashMap<String, String>();
            for (int i = 0; i < currentDetailParseParamNum; i++) {
                ParseParamDto parseParam = getModel(ParseParamDto.class, "parseRule_" + templateDetail + "_" + i);
                responseMap.put(parseParam.getParseName(), parseParam.getPosition());
            }
            parameter.setParams(JSONObject.fromObject(requestMap).toString());
            parameter.setParamsDefinition(JSONArray.fromObject(reqDtoList).toString());
            parameter.setPosition(JSONObject.fromObject(responseMap).toString());
            passage.getParameterList().add(parameter);

        }
      
        renderJson(iSmsPassageService.update(passage, getPara("provinceCodes")));
    }

    public void delete() {
        renderResultJson(iSmsPassageService.deleteById(getParaToInt("id")));
    }

    /**
     * 
       * TODO 禁用激活
     */
    public void disabled() {
        try {
        	renderResultJson(iSmsPassageService.disabledOrActive(getParaToInt("id"), getParaToInt("flag")));
		} catch (Exception e) {
			 renderResultJson(false);
		}
    }
    
    /**
     * 
       * TODO 断开通道连接
     */
    public void kill() {
        try {
            renderResultJson(iSmsPassageService.kill(getParaToInt("id")));
        } catch (Exception e) {
             renderResultJson(false);
        }
    }

    public void userList() {
        String fullName = getPara("fullName");
        String mobile = getPara("mobile");
        String company = getPara("company");
        String cardNo = getPara("cardNo");
        String state = getPara("state");
        BossPaginationVo<UserProfile> page = iUserService.findPage(getPN(),
                fullName, mobile, company, cardNo, state, null);
        page.setJumpPageFunction("userJumpPage");
        setAttr("fullName", fullName);
        setAttr("mobile", mobile);
        setAttr("company", company);
        setAttr("cardNo", cardNo);
        setAttr("state", state);
        setAttr("page", page);
        setAttr("userId", getParaToInt("userId", -1));
    }

    /**
     * 通道测试
     */
    public void test() {
        Integer passageId = getParaToInt("passageId");
        String mobile = getPara("mobile");
        String content = getPara("content");
        
        renderResultJson(iSmsPassageService.doTestPassage(passageId, mobile, content));
    }

    public void passage_json() {
        List<SmsPassage> list = iSmsPassageService.findAll();
        List<Object> jsonList = new ArrayList<Object>();
        for (SmsPassage passage : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", passage.getId());
            map.put("name", passage.getName());
            map.put("cmcp", passage.getCmcp());
            map.put("status", passage.getStatus());
            jsonList.add(map);
        }
        renderJson(jsonList);
    }

}
