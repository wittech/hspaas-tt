package com.huashi.hsboss.web.controller.sms;

import java.util.HashMap;
import java.util.Map;

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
import com.huashi.sms.signature.domain.SignatureExtNo;
import com.huashi.sms.signature.service.ISignatureExtNoService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 
  * TODO 用户签名扩展号码控制器
  *
  * @author zhengying
  * @version V1.0.0   
  * @date 2017年7月9日 下午9:42:31
 */
@ViewMenu(code = MenuCode.MENU_CODE_2005)
@ControllerBind(controllerKey = "/sms/signature_extno")
public class SignatureExtNoController extends BaseController {

	@BY_NAME
	private ISignatureExtNoService iSignatureExtNoService;
	@BY_NAME
	private IUserService iUserService;

	@AuthCode(code= {OperCode.OPER_CODE_2005001, OperCode.OPER_CODE_2005002,OperCode.OPER_CODE_2005003
			,OperCode.OPER_CODE_2005004})
	@ActionMode
	public void index() {
		Map<String, Object> condition = appendQueryParams();
		BossPaginationVo<SignatureExtNo> page = iSignatureExtNoService.findPage(condition);
		
		setAttr("page", page);
		setAttrs(condition);
		setAttr("userList", iUserService.findAll());
	}
	
	/**
	 * 
	   * TODO 拼接查询条件
	   * @return
	 */
	private Map<String, Object> appendQueryParams() {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("userId", getParaToInt("userId", -1));
		paramMap.put("currentPage", getPN());
		paramMap.put("signature", getPara("signature"));
		paramMap.put("username", getPara("username"));
		return paramMap;
	}

	@AuthCode(code= OperCode.OPER_CODE_2005002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void add() {
		renderResultJson(iSignatureExtNoService.save(getModel(SignatureExtNo.class)));
	}

	@AuthCode(code= OperCode.OPER_CODE_2005002)
	@ActionMode
	public void create() {
		//所有融合平台用户
		setAttr("userList", iUserService.findUserModels());
	}

	@AuthCode(code= OperCode.OPER_CODE_2005003)
	@ActionMode
	public void edit() {
		setAttr("signatureExtNo", iSignatureExtNoService.get(getParaToLong("id")));
		setAttr("userList", iUserService.findUserModels());
	}

	@AuthCode(code= OperCode.OPER_CODE_2005003)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update() {
		renderResultJson(iSignatureExtNoService.update(getModel(SignatureExtNo.class)));
	}
	
	@AuthCode(code= OperCode.OPER_CODE_2005004)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete() {
		renderResultJson(iSignatureExtNoService.delete(getParaToLong("id")));
	}
	
	@AuthCode(code= OperCode.OPER_CODE_2005001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void loadingRedis(){
	    renderResultJson(iSignatureExtNoService.reloadToRedis());
	}
}
