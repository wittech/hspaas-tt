package com.huashi.hsboss.web.controller.mms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.huashi.common.passage.context.TemplateEnum.PassageTemplateDetailType;
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
import com.huashi.exchanger.service.IMmsProviderService;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.mms.passage.domain.MmsPassageParameter;
import com.huashi.mms.passage.domain.MmsPassageProvince;
import com.huashi.mms.passage.service.IMmsPassageAccessService;
import com.huashi.mms.passage.service.IMmsPassageParameterService;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.jfinal.ext.route.ControllerBind;

@ControllerBind(controllerKey = "/mms/passage")
@ViewMenu(code = MenuCode.MENU_CODE_10003001)
public class MmsPassageController extends BaseController {

    @BY_NAME
    private IUserService                iUserService;
    @BY_NAME
    private IMmsPassageService          iMmsPassageService;
    @BY_NAME
    private IMmsPassageAccessService    iMmsPassageAccessService;
    @BY_NAME
    private IMmsPassageParameterService iMmsPassageParameterService;
    @BY_NAME
    private IMmsProviderService         iMmsProviderService;
    @BY_NAME
    private IProvinceService            iProvinceService;
    @BY_NAME
    private IPassageTemplateService     iPassageTemplateService;

    /**
     * TODO 列表页面
     */
    @AuthCode(code = { OperCode.OPER_CODE_10003001001, OperCode.OPER_CODE_10003001002, OperCode.OPER_CODE_10003001003,
            OperCode.OPER_CODE_10003001004 })
    @ActionMode
    public void index() {
        String keyword = getPara("keyword");
        BossPaginationVo<MmsPassage> page = iMmsPassageService.findPage(getPN(), keyword);
        setAttr("page", page);
        setAttr("keyword", keyword);
    }

    /**
     * TODO 跳转添加页面
     */
    @AuthCode(code = OperCode.OPER_CODE_10003001001)
    @ActionMode
    public void add() {
        setAttr("templateList", iPassageTemplateService.findListByType(PassageTemplateType.MMS.getValue()));
        setAttr("cmcp", CommonContext.CMCP.values());
        setAttr("provinceList", iProvinceService.findAvaiable());
        setAttr("passageTemplateDetailTypes", PassageTemplateDetailType.values());
    }

    /**
     * TODO 添加
     */
    @AuthCode(code = OperCode.OPER_CODE_10003001002)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void create() {
        MmsPassage passage = getModel(MmsPassage.class, "passage");
        String templateDetails = getPara("templateDetails");
        String provinceCodes = getPara("provinceCodes");
        String[] tdArray = templateDetails.split(",");
        PassageTemplate template = iPassageTemplateService.findById(passage.getHspaasTemplateId());
        for (String templateDetail : tdArray) {
            MmsPassageParameter parameter = getModel(MmsPassageParameter.class, templateDetail);
            parameter.setProtocol(template.getProtocol());
            // 请求参数设置
            int currentDetailReqParamNum = getParaToInt("reqParam_" + templateDetail, 0);
            Map<String, String> requestMap = new HashMap<String, String>();
            List<RequestParamDto> reqDtoList = new ArrayList<RequestParamDto>();
            for (int i = 0; i < currentDetailReqParamNum; i++) {
                RequestParamDto reqParam = getModel(RequestParamDto.class, "reqParam_" + templateDetail + "_" + i);
                requestMap.put(reqParam.getRequestName(), reqParam.getDefaultValue());
                reqDtoList.add(reqParam);
            }

            // 解析解析设置
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
        Map<String, Object> map = iMmsPassageService.add(passage, provinceCodes);
        renderJson(map);
    }

    public void templateView() {
        int templateId = getParaToInt("templateId");
        PassageTemplate template = iPassageTemplateService.findById(templateId);
        setAttr("template", template);
    }

    /**
     * TODO 跳转修改页面
     */
    @AuthCode(code = OperCode.OPER_CODE_10003001002)
    @ActionMode
    public void edit() {
        MmsPassage passage = iMmsPassageService.findById(getParaToInt("id"));
        setAttr("passage", passage);
        setAttr("templateList", iPassageTemplateService.findListByType(PassageTemplateType.MMS.getValue()));
        List<Province> provinceList = iProvinceService.findAvaiable();
        Province qg = new Province();
        qg.setCode(0);
        qg.setName("全国");
        qg.setId(0);
        provinceList.add(qg);
        List<MmsPassageProvince> passageProvinceList = iMmsPassageService.getPassageProvinceById(passage.getId());
        out: for (Province province : provinceList) {
            for (MmsPassageProvince passageProvince : passageProvinceList) {
                if (province.getCode().equals(passageProvince.getProvinceCode())) {
                    province.setSelect(1);
                    continue out;
                }
            }
        }
        setAttr("provinceList", provinceList);
        setAttr("cmcp", CommonContext.CMCP.values());
        setAttr("passageTemplateDetailTypes", PassageTemplateDetailType.values());

    }

    @AuthCode(code = OperCode.OPER_CODE_10003001002)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void update() {
        MmsPassage passage = getModel(MmsPassage.class, "passage");
        String templateDetails = getPara("templateDetails");
        String[] tdArray = templateDetails.split(",");
        PassageTemplate template = iPassageTemplateService.findById(passage.getHspaasTemplateId());
        for (String templateDetail : tdArray) {
            MmsPassageParameter parameter = getModel(MmsPassageParameter.class, templateDetail);
            parameter.setProtocol(template.getProtocol());
            // 请求参数设置
            int currentDetailReqParamNum = getParaToInt("reqParam_" + templateDetail, 0);
            Map<String, String> requestMap = new HashMap<String, String>();
            List<RequestParamDto> reqDtoList = new ArrayList<RequestParamDto>();
            for (int i = 0; i < currentDetailReqParamNum; i++) {
                RequestParamDto reqParam = getModel(RequestParamDto.class, "reqParam_" + templateDetail + "_" + i);
                requestMap.put(reqParam.getRequestName(), reqParam.getDefaultValue());
                reqDtoList.add(reqParam);
            }

            // 解析解析设置
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

        renderJson(iMmsPassageService.update(passage, getPara("provinceCodes")));
    }

    @AuthCode(code = OperCode.OPER_CODE_10003001003)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void delete() {
        renderResultJson(iMmsPassageService.deleteById(getParaToInt("id")));
    }

    /**
     * TODO 禁用激活
     */
    @AuthCode(code = OperCode.OPER_CODE_10003001004)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void disabled() {
        try {
            renderResultJson(iMmsPassageService.disabledOrActive(getParaToInt("id"), getParaToInt("flag")));
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
        BossPaginationVo<UserProfile> page = iUserService.findPage(getPN(), fullName, mobile, company, cardNo, state,
                                                                   null);
        page.setJumpPageFunction("userJumpPage");
        setAttr("fullName", fullName);
        setAttr("mobile", mobile);
        setAttr("company", company);
        setAttr("cardNo", cardNo);
        setAttr("state", state);
        setAttr("page", page);
        setAttr("userId", getParaToInt("userId", -1));
    }

    public void passage_json() {
        List<MmsPassage> list = iMmsPassageService.findAll();
        List<Object> jsonList = new ArrayList<Object>();
        for (MmsPassage passage : list) {
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
