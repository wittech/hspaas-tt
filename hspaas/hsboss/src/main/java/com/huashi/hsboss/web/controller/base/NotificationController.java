package com.huashi.hsboss.web.controller.base;

import java.util.Date;

import com.huashi.common.notice.domain.Notification;
import com.huashi.common.notice.service.INotificationService;
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
 * 通知、公告
 * @author ym
 * @created_at 2016年6月28日下午4:24:26
 */
@ViewMenu(code=MenuCode.MENU_CODE_1002001)
@ControllerBind(controllerKey="/base/notification")
public class NotificationController extends BaseController {

	
	@BY_NAME
	private INotificationService iNotificationService;
	
	@AuthCode(code= {OperCode.OPER_CODE_1002001001, OperCode.OPER_CODE_1002001002,OperCode.OPER_CODE_1002001003,
			OperCode.OPER_CODE_1002001004})
	@ActionMode
	public void index(){
		BossPaginationVo<Notification> page = iNotificationService.findPage(getPN());
		setAttr("page", page);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002001001})
	@ActionMode
	public void add(){
		
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002001001})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create(){
		Notification notification = getModel(Notification.class);
		int notificationType = getParaToInt("notificationType");
		notification.setType(notificationType);
		notification.setCreateTime(new Date());
		notification.setStatus(0);
		boolean result = iNotificationService.create(notification);
		renderResultJson(result);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002001002})
	@ActionMode
	public void edit(){
		Notification notification = iNotificationService.findById(getParaToInt("id"));
		setAttr("notification", notification);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002001002})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update(){
		Notification notification = getModel(Notification.class);
		int notificationType = getParaToInt("notificationType");
		notification.setType(notificationType);
		boolean result = iNotificationService.update(notification);
		renderResultJson(result);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002001004})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete(){
		boolean result = iNotificationService.delete(getParaToInt("id", -1));
		renderResultJson(result);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1002001003})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void disabled(){
		Notification notification = iNotificationService.findById(getParaToInt("id"));
		notification.setStatus(getParaToInt("flag"));
		boolean result = iNotificationService.update(notification);
		renderResultJson(result);
	}
}
