package com.huashi.hsboss.web.controller.mms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.dto.MmsPassageCmcpDto;
import com.huashi.hsboss.dto.MmsPassageGroupProvinceDto;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.mms.passage.domain.MmsPassage;
import com.huashi.mms.passage.domain.MmsPassageGroup;
import com.huashi.mms.passage.domain.MmsPassageGroupDetail;
import com.huashi.mms.passage.service.IMmsPassageAccessService;
import com.huashi.mms.passage.service.IMmsPassageGroupService;
import com.huashi.mms.passage.service.IMmsPassageService;
import com.huashi.sms.passage.context.PassageContext.RouteType;
import com.jfinal.ext.route.ControllerBind;

/**
 * 通道组
 * 
 * @author ym
 * @created_at 2016年8月25日下午4:17:02
 */
@ControllerBind(controllerKey = "/mms/passage_group")
@ViewMenu(code = MenuCode.MENU_CODE_10003002)
public class MmsPassageGroupController extends BaseController {

    @BY_NAME
    private IProvinceService         iProvinceService;
    @BY_NAME
    private IMmsPassageService       iMmsPassageService;
    @BY_NAME
    private IMmsPassageGroupService  iMmsPassageGroupService;
    @BY_NAME
    private IMmsPassageAccessService iMmsPassageAccessService;

    @AuthCode(code = { OperCode.OPER_CODE_10003002001, OperCode.OPER_CODE_10003002002 })
    @ActionMode
    public void index() {
        String keyword = getPara("keyword");
        BossPaginationVo<MmsPassageGroup> page = iMmsPassageGroupService.findPage(getPN(), keyword);
        setAttr("page", page);
        setAttr("keyword", keyword);
    }

    @AuthCode(code = OperCode.OPER_CODE_10003002001)
    @ActionMode
    public void add() {
        List<Province> provinceList = iProvinceService.findAvaiable();
        CommonContext.CMCP[] cmcps = CommonContext.CMCP.values();
        setAttr("cmcps", cmcps);
        setAttr("provinceList", provinceList);
        setAttr("routeTypes", RouteType.values());
    }

    public void old_add() {
        List<MmsPassageCmcpDto> cmcpList = new ArrayList<>();
        for (CommonContext.CMCP cmcp : CommonContext.CMCP.values()) {
            if (cmcp.getCode() == 0 || cmcp.getCode() == 4) {
                continue;
            }
            MmsPassageCmcpDto dto = new MmsPassageCmcpDto();
            dto.setCmcp(cmcp);
            List<MmsPassage> passageList = iMmsPassageService.getByCmcp(cmcp.getCode());
            dto.getPassageList().addAll(passageList);
            cmcpList.add(dto);
        }
        setAttr("routeTypes", RouteType.values());
        setAttr("cmcpList", cmcpList);
        setAttr("routeTypes", RouteType.values());
    }

    public void passageList() {
        int provinceCode = getParaToInt("province_code");
        int cmcp = getParaToInt("cmcp");
        List<MmsPassage> passageList = iMmsPassageService.getByProvinceAndCmcp(provinceCode, cmcp);
        setAttr("passageList", passageList);
    }

    @AuthCode(code = OperCode.OPER_CODE_10003002001)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void create() {
        MmsPassageGroup group = getModel(MmsPassageGroup.class, "group");
        for (RouteType rt : RouteType.values()) {
            String name = "route_type_" + rt.getValue();
            String[] passageInfos = getParaValues(name);
            if (passageInfos == null) {
                continue;
            }
            int priority = 1;
            for (String passageInfo : passageInfos) {
                MmsPassageGroupDetail detail = new MmsPassageGroupDetail(passageInfo);
                detail.setPriority(priority);
                detail.setRouteType(rt.getValue());
                group.getDetailList().add(detail);
                priority++;
            }
        }
        boolean result = iMmsPassageGroupService.create(group);
        renderResultJson(result);
    }

    public void old_create() {
        MmsPassageGroup group = getModel(MmsPassageGroup.class, "group");
        for (RouteType rt : RouteType.values()) {
            String name = "route_type_" + rt.getValue();
            int count = getParaToInt(name + "_count");
            for (int i = 0; i <= count; i++) {
                MmsPassageGroupDetail detail = getModel(MmsPassageGroupDetail.class, name + "_" + i);
                if (detail == null || detail.getPassageId() == null) {
                    continue;
                }

                detail.setRouteType(rt.getValue());
                group.getDetailList().add(detail);
            }
        }
        boolean result = iMmsPassageGroupService.create(group);
        renderResultJson(result);
    }

    public void old_edit() {
        MmsPassageGroup group = iMmsPassageGroupService.findById(getParaToInt("id"));
        List<MmsPassageCmcpDto> cmcpList = new ArrayList<>();
        for (CommonContext.CMCP cmcp : CommonContext.CMCP.values()) {
            if (cmcp.getCode() == 0 || cmcp.getCode() == 4) {
                continue;
            }
            MmsPassageCmcpDto dto = new MmsPassageCmcpDto();
            dto.setCmcp(cmcp);
            List<MmsPassage> passageList = iMmsPassageService.getByCmcp(cmcp.getCode());
            dto.getPassageList().addAll(passageList);
            cmcpList.add(dto);
        }
        setAttr("routeTypes", RouteType.values());
        setAttr("cmcpList", cmcpList);
        setAttr("routeTypes", RouteType.values());
        setAttr("group", group);
    }

    @AuthCode(code = OperCode.OPER_CODE_10003002002)
    @ActionMode
    public void edit() {
        MmsPassageGroup group = iMmsPassageGroupService.findById(getParaToInt("id"));
        List<Province> provinceList = new LinkedList<Province>();
        List<Province> dataProvinceList = iProvinceService.findAvaiable();
        Province initProvince = new Province();
        initProvince.setCode(0);
        initProvince.setName("其他");
        initProvince.setId(0);
        provinceList.add(initProvince);
        provinceList.addAll(dataProvinceList);

        List<MmsPassageGroupDetail> detailList = iMmsPassageGroupService.findPassageByGroupId(group.getId());
        List<MmsPassageGroupDetail> removeDetailList = new LinkedList<>();

        Map<String, Object> dtoMap = new HashMap<String, Object>();
        RouteType[] types = RouteType.values();
        CommonContext.CMCP[] cmcps = CommonContext.CMCP.values();
        for (RouteType routeType : types) {
            List<MmsPassageGroupProvinceDto> dtoList = new LinkedList<>();
            for (Province province : provinceList) {
                MmsPassageGroupProvinceDto dto = new MmsPassageGroupProvinceDto();
                dto.setProvince(province);
                List<MmsPassageCmcpDto> cmcpDtos = new LinkedList<>();
                for (CommonContext.CMCP cmcp : cmcps) {
                    if (cmcp.getCode() == 0 || cmcp.getCode() == 4) {
                        continue;
                    }

                    MmsPassageCmcpDto cmcpDto = new MmsPassageCmcpDto();
                    cmcpDto.setCmcp(cmcp);

                    List<String> passageInfos = new LinkedList<String>();
                    for (MmsPassageGroupDetail detail : detailList) {
                        if (detail.getRouteType().intValue() == routeType.getValue()
                            && detail.getProvinceCode().equals(province.getCode())
                            && detail.getCmcp().intValue() == cmcp.getCode()) {
                            String passageInfo = detail.disponsePassageToSplitStr() + routeType.getValue();
                            passageInfos.add(passageInfo);
                            removeDetailList.add(detail);
                        }
                    }
                    detailList.removeAll(removeDetailList);
                    cmcpDto.getPassageInfos().addAll(passageInfos);
                    cmcpDtos.add(cmcpDto);
                }
                dto.getCmcpList().addAll(cmcpDtos);
                dtoList.add(dto);
            }
            dtoMap.put("route_type_" + routeType.getValue(), dtoList);
        }
        setAttr("dataMap", dtoMap);
        setAttr("cmcps", cmcps);
        setAttr("routeTypes", RouteType.values());
        setAttr("group", group);
    }

    /**
     * TODO 修改通道组
     */
    @AuthCode(code = OperCode.OPER_CODE_10003002002)
    @ActionMode(type = EnumConstant.ActionType.JSON)
    public void update() {
        MmsPassageGroup group = getModel(MmsPassageGroup.class, "group");
        for (RouteType rt : RouteType.values()) {
            String name = "route_type_" + rt.getValue();
            String[] passageInfos = getParaValues(name);
            if (passageInfos == null) {
                continue;
            }
            int priority = 1;
            for (String passageInfo : passageInfos) {
                MmsPassageGroupDetail detail = new MmsPassageGroupDetail(passageInfo);
                detail.setPriority(priority);
                detail.setRouteType(rt.getValue());
                group.getDetailList().add(detail);
                priority++;
            }
        }
        boolean result = iMmsPassageGroupService.update(group);
        if (result) {
            iMmsPassageAccessService.updateByModifyPassageGroup(group.getId());
        }
        renderResultJson(result);
    }

    public void old_update() {
        MmsPassageGroup group = getModel(MmsPassageGroup.class, "group");
        for (RouteType rt : RouteType.values()) {
            String name = "route_type_" + rt.getValue();
            int count = getParaToInt(name + "_count");
            for (int i = 0; i <= count; i++) {
                MmsPassageGroupDetail detail = getModel(MmsPassageGroupDetail.class, name + "_" + i);
                if (detail == null || detail.getPassageId() == null) {
                    continue;
                }
                detail.setRouteType(rt.getValue());
                group.getDetailList().add(detail);
            }
        }
        boolean result = iMmsPassageGroupService.update(group);
        if (result) {
            iMmsPassageAccessService.updateByModifyPassageGroup(group.getId());
        }
        renderResultJson(result);
    }

    public void delete() {
        boolean result = iMmsPassageGroupService.deleteById(getParaToInt("id"));
        renderResultJson(result);
    }
}
