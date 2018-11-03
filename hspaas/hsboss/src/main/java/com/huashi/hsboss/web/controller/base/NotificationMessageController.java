package com.huashi.hsboss.web.controller.base;

import java.util.Date;

import com.huashi.common.notice.domain.NotificationMessage;
import com.huashi.common.notice.service.INotificationMessageService;
import com.huashi.common.user.domain.UserProfile;
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
import com.jfinal.ext.route.ControllerBind;

/**
 * 站内消息
 * @author ym
 * @created_at 2016年6月28日下午4:24:17
 */
@ViewMenu(code=MenuCode.MENU_CODE_1002002)
@ControllerBind(controllerKey="/base/notification_message")
public class NotificationMessageController extends BaseController{

	
	@BY_NAME
	private INotificationMessageService iNotificationMessageService;
	@BY_NAME
	private IUserService iUserService;
	
	@AuthCode(code= {OperCode.OPER_CODE_1002002001, OperCode.OPER_CODE_1002002002,OperCode.OPER_CODE_1002002003})
	@ActionMode
	public void index(){
		BossPaginationVo<NotificationMessage> page = iNotificationMessageService.findPage(getPN(), getPara("keyword"));
		setAttr("page", page);
		setAttr("keyword", getPara("keyword"));
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002002001})
	@ActionMode
	public void add(){
		
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002002001})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create(){
		NotificationMessage message = getModel(NotificationMessage.class);
		message.setStatus(0);
		message.setType(getParaToInt("notificationType",3));
		message.setCreateTime(new Date());
		boolean result = iNotificationMessageService.create(message);
		renderResultJson(result);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002002002})
	@ActionMode
	public void edit(){
		NotificationMessage message = iNotificationMessageService.findById(getParaToInt("id"));
		setAttr("notificationMessage", message);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002002002})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update(){
		NotificationMessage message = getModel(NotificationMessage.class);
		message.setType(getParaToInt("notificationType",3));
		boolean result = iNotificationMessageService.update(message);
		renderResultJson(result);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002002003})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete(){
		boolean result = iNotificationMessageService.delete(getParaToInt("id"));
		renderResultJson(result);
	}
	
	public void userList(){
		String fullName = getPara("fullName");
		String mobile = getPara("mobile");
		String company = getPara("company");
		String cardNo = getPara("cardNo");
		String state = getPara("state");
		BossPaginationVo<UserProfile> page = iUserService.findPage(getPN(),
				fullName, mobile, company,cardNo,state,null);
		page.setJumpPageFunction("userJumpPage");
		setAttr("fullName", fullName);
		setAttr("mobile", mobile);
		setAttr("company", company);
		setAttr("cardNo", cardNo);
		setAttr("state", state);
		setAttr("page", page);
		setAttr("userId", getParaToInt("userId",-1));
			
	}
}
