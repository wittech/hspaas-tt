/**
 * 
 */
package com.huashi.hsboss.web.controller.sms;

import java.util.Map;

import com.huashi.common.settings.domain.HostWhiteList;
import com.huashi.common.settings.service.IHostWhiteListService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.passage.context.PassageContext;
import com.jfinal.ext.route.ControllerBind;

/**
 * ip白名单管理
 * @author Administrator
 *
 */
@ControllerBind(controllerKey = "/sms/host_white")
@ViewMenu(code= MenuCode.MENU_CODE_2004004)
public class HostWhiteListController extends BaseController {
	 @Inject.BY_NAME
	 private IHostWhiteListService iHostWhiteListService;
	 @BY_NAME
	 private IUserService iUserService;
	 
	 @AuthCode(code= {OperCode.OPER_CODE_2004004001, OperCode.OPER_CODE_2004004002,OperCode.OPER_CODE_2004004003})
	 @ActionMode
	 public void index(){
        String ip = getPara("ip");
        String userId = getPara("userId");
        String status = getPara("status");
        BossPaginationVo<HostWhiteList> page = iHostWhiteListService.findPageBoss(getPN(),ip,status,userId);
        setAttr("page",page);
        setAttr("ip",ip);
        setAttr("status",status);
	 }
	 
	 @AuthCode(code= OperCode.OPER_CODE_2004004002)
	 @ActionMode
	 public void create(){
		 setAttr("userList", iUserService.findUserModels());
		 setAttr("status",PassageContext.PassageStatus.values());
	 }
	 
	 @AuthCode(code= OperCode.OPER_CODE_2004004002)
	 @ActionMode(type = EnumConstant.ActionType.JSON)
	 public void add(){
		 HostWhiteList whiteList = getModel(HostWhiteList.class,"hostWhiteList");
		 Map<String,Object> resultMap = iHostWhiteListService.batchInsert(whiteList);
		 String resultCode= (String)resultMap.get("result_code");
		 boolean flag = false;
		 if("success".equals(resultCode)){
			 flag=true;
		 }
		 renderResultJson(flag,(String)resultMap.get("result_msg"));
	 }
	 
	 @AuthCode(code= OperCode.OPER_CODE_2004004003)
	 @ActionMode(type = EnumConstant.ActionType.JSON)
	 public void delete(){
	    int result = iHostWhiteListService.deleteByPrimaryKey(getParaToInt("id"));
	    renderResultJson(result > 0);
	 }
	 
	 @AuthCode(code= OperCode.OPER_CODE_2004004001)
	 @ActionMode(type = EnumConstant.ActionType.JSON)
	 public void loadingRedis(){
	    renderResultJson(iHostWhiteListService.reloadToRedis());
	 }
	 
}
