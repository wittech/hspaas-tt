//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月25日       杨猛　     新建
//*********************************************************************
package com.huashi.hsboss.web.controller.sms;

import com.huashi.common.settings.domain.Province;
import com.huashi.common.settings.service.IProvinceService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.constants.CommonContext;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.dto.PassageCmcpDto;
import com.huashi.hsboss.dto.PassageGroupProvinceDto;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.context.PassageContext.RouteType;
import com.huashi.sms.passage.domain.SmsPassage;
import com.huashi.sms.passage.domain.SmsPassageGroup;
import com.huashi.sms.passage.domain.SmsPassageGroupDetail;
import com.huashi.sms.passage.service.ISmsPassageAccessService;
import com.huashi.sms.passage.service.ISmsPassageGroupService;
import com.huashi.sms.passage.service.ISmsPassageService;
import com.jfinal.ext.route.ControllerBind;

import java.util.*;

/**
 * 通道组
 * @author ym
 * @created_at 2016年8月25日下午4:17:02
 */
@ControllerBind(controllerKey="/sms/passage_group")
@ViewMenu(code= MenuCode.MENU_CODE_2003002)
public class PassageGroupController extends BaseController {

	
	@BY_NAME
	private ISmsPassageGroupService iSmsPassageGroupService;
	@BY_NAME
	private ISmsPassageService iSmsPassageService;
	@BY_NAME
	private ISmsPassageAccessService iSmsPassageAccessService;
    @BY_NAME
    private IProvinceService iProvinceService;

	
	public void index(){
		String keyword = getPara("keyword");
		BossPaginationVo<SmsPassageGroup> page = iSmsPassageGroupService.findPage(getPN(), keyword);
		setAttr("page", page);
		setAttr("keyword", keyword);
	}
	
	public void add(){
        List<Province> provinceList = iProvinceService.findAvaiable();
        CommonContext.CMCP[] cmcps = CommonContext.CMCP.values();
        setAttr("cmcps",cmcps);
        setAttr("provinceList", provinceList);
        setAttr("routeTypes",RouteType.values());
	}

	public void old_add(){
		List<PassageCmcpDto> cmcpList = new ArrayList<PassageCmcpDto>();
		for(CommonContext.CMCP cmcp : CommonContext.CMCP.values()){
			if(cmcp.getCode() == 0 || cmcp.getCode() == 4){
				continue;
			}
			PassageCmcpDto dto = new PassageCmcpDto();
			dto.setCmcp(cmcp);
			List<SmsPassage> passageList = iSmsPassageService.getByCmcp(cmcp.getCode());
			dto.getPassageList().addAll(passageList);
			cmcpList.add(dto);
		}
		setAttr("routeTypes",RouteType.values());
		setAttr("cmcpList", cmcpList);
		setAttr("routeTypes", RouteType.values());
	}

	public void passageList(){
        int provinceCode = getParaToInt("province_code");
        int cmcp = getParaToInt("cmcp");
        List<SmsPassage> passageList = iSmsPassageService.getByProvinceAndCmcp(provinceCode,cmcp);
        setAttr("passageList", passageList);
    }

    public void create(){
        SmsPassageGroup group = getModel(SmsPassageGroup.class, "group");
        //passageId + split_tag + passageName + split_tag + provinceCode + split_tag + cmcp + split_tag + routeType;
        for(RouteType rt : RouteType.values()){
            String name = "route_type_"+rt.getValue();
            String[] passageInfos = getParaValues(name);
            if(passageInfos == null){
                continue;
            }
            int priority = 1;
            for(String passageInfo : passageInfos){
                SmsPassageGroupDetail detail = new SmsPassageGroupDetail(passageInfo);
                detail.setPriority(priority);
                detail.setRouteType(rt.getValue());
                group.getDetailList().add(detail);
                priority ++;
            }
        }
        boolean result = iSmsPassageGroupService.create(group);
        renderResultJson(result);
    }
	
	public void old_create(){
		SmsPassageGroup group = getModel(SmsPassageGroup.class, "group");
		for(RouteType rt : RouteType.values()){
			String name = "route_type_"+rt.getValue();
			int count = getParaToInt(name+"_count");
			for(int i = 0; i<= count;i++){
				SmsPassageGroupDetail detail = getModel(SmsPassageGroupDetail.class,name+"_"+i);
				if(detail == null || detail.getPassageId() == null){
					continue;
				}
				detail.setRouteType(rt.getValue());
				group.getDetailList().add(detail);
			}
		}
		boolean result = iSmsPassageGroupService.create(group);
		renderResultJson(result);
	}
	
	public void old_edit(){
        SmsPassageGroup group = iSmsPassageGroupService.findById(getParaToInt("id"));
        List<PassageCmcpDto> cmcpList = new ArrayList<PassageCmcpDto>();
        for(CommonContext.CMCP cmcp : CommonContext.CMCP.values()){
            if(cmcp.getCode() == 0 || cmcp.getCode() == 4){
                continue;
            }
            PassageCmcpDto dto = new PassageCmcpDto();
            dto.setCmcp(cmcp);
            List<SmsPassage> passageList = iSmsPassageService.getByCmcp(cmcp.getCode());
            dto.getPassageList().addAll(passageList);
            cmcpList.add(dto);
        }
        setAttr("routeTypes",RouteType.values());
        setAttr("cmcpList", cmcpList);
        setAttr("routeTypes", RouteType.values());
        setAttr("group", group);
    }

    public void edit(){
        SmsPassageGroup group = iSmsPassageGroupService.findById(getParaToInt("id"));
        List<Province> provinceList = new LinkedList<Province>();
        List<Province> dataProvinceList = iProvinceService.findAvaiable();
        Province initProvince = new Province();
        initProvince.setCode(0);
        initProvince.setName("其他");
        initProvince.setId(0);
        provinceList.add(initProvince);
        provinceList.addAll(dataProvinceList);

        List<SmsPassageGroupDetail> detailList = iSmsPassageGroupService.findPassageByGroupId(group.getId());
        List<SmsPassageGroupDetail> removeDetailList = new LinkedList<SmsPassageGroupDetail>();

        Map<String,Object> dtoMap = new HashMap<String,Object>();
        RouteType[] types = RouteType.values();
        CommonContext.CMCP[] cmcps = CommonContext.CMCP.values();
        for(RouteType routeType : types){
            List<PassageGroupProvinceDto> dtoList = new LinkedList<PassageGroupProvinceDto>();
            for(Province province : provinceList){
                PassageGroupProvinceDto dto = new PassageGroupProvinceDto();
                dto.setProvince(province);
                List<PassageCmcpDto> cmcpDtos = new LinkedList<PassageCmcpDto>();
                for(CommonContext.CMCP cmcp : cmcps){
                    if(cmcp.getCode() == 0 || cmcp.getCode() == 4){
                        continue;
                    }
                    PassageCmcpDto cmcpDto = new PassageCmcpDto();
                    cmcpDto.setCmcp(cmcp);

                    List<String> passageInfos = new LinkedList<String>();
                    for(SmsPassageGroupDetail detail : detailList){
                        if(detail.getRouteType().intValue() == routeType.getValue() &&
                                detail.getProvinceCode().equals(province.getCode()) &&
                                detail.getCmcp().intValue() == cmcp.getCode()){
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
            dtoMap.put("route_type_"+routeType.getValue(),dtoList);
        }
        setAttr("dataMap",dtoMap);
        setAttr("cmcps",cmcps);
        setAttr("routeTypes", RouteType.values());
        setAttr("group", group);
    }
	
    /**
     * 
       * TODO 修改通道组
     */
	public void update(){
		SmsPassageGroup group = getModel(SmsPassageGroup.class, "group");
        for(RouteType rt : RouteType.values()){
            String name = "route_type_"+rt.getValue();
            String[] passageInfos = getParaValues(name);
            if(passageInfos == null){
                continue;
            }
            int priority = 1;
            for(String passageInfo : passageInfos){
                SmsPassageGroupDetail detail = new SmsPassageGroupDetail(passageInfo);
                detail.setPriority(priority);
                detail.setRouteType(rt.getValue());
                group.getDetailList().add(detail);
                priority ++;
            }
        }
		boolean result = iSmsPassageGroupService.update(group);
		if(result){
			iSmsPassageAccessService.updateByModifyPassageGroup(group.getId());
		}
		renderResultJson(result);
	}

	public void old_update(){
        SmsPassageGroup group = getModel(SmsPassageGroup.class, "group");
        for(RouteType rt : RouteType.values()){
            String name = "route_type_"+rt.getValue();
            int count = getParaToInt(name+"_count");
            for(int i = 0; i<= count;i++){
                SmsPassageGroupDetail detail = getModel(SmsPassageGroupDetail.class,name+"_"+i);
                if(detail == null || detail.getPassageId() == null){
                    continue;
                }
                detail.setRouteType(rt.getValue());
                group.getDetailList().add(detail);
            }
        }
        boolean result = iSmsPassageGroupService.update(group);
        if(result){
            iSmsPassageAccessService.updateByModifyPassageGroup(group.getId());
        }
        renderResultJson(result);
    }
	
	public void delete(){
		boolean result = iSmsPassageGroupService.deleteById(getParaToInt("id"));
		renderResultJson(result);
	}
}
