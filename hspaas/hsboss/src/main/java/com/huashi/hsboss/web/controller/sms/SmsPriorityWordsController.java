/**
 * 
 */
package com.huashi.hsboss.web.controller.sms;

import org.apache.commons.lang.StringUtils;

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
import com.huashi.sms.settings.constant.SmsSettingsContext.ForbiddenWordsSwitch;
import com.huashi.sms.settings.domain.SmsPriorityWords;
import com.huashi.sms.settings.service.ISmsPriorityWordsService;
import com.jfinal.ext.route.ControllerBind;

/**
 * 优先级词库配置管理
 * 
 * @author Administrator
 *
 */
@ControllerBind(controllerKey = "/sms/priority_words")
@ViewMenu(code = MenuCode.MENU_CODE_2004005)
public class SmsPriorityWordsController extends BaseController {
	@BY_NAME
	private ISmsPriorityWordsService iSmsPriorityWordsService;
	@BY_NAME
	private IUserService iUserService;

	/**
	 * 查询列表页面
	 */
	@AuthCode(code= {OperCode.OPER_CODE_2004005001, OperCode.OPER_CODE_2004005002,OperCode.OPER_CODE_2004005003
			,OperCode.OPER_CODE_2004005004})
	@ActionMode
	public void index() {
		String content = getPara("content");
		String userId = getPara("userId");
		BossPaginationVo<SmsPriorityWords> page = iSmsPriorityWordsService.findPage(getPN(), userId, content);
		if (StringUtils.isNotEmpty(content)) {
			setAttr("content", content);
		}
		if (StringUtils.isNotEmpty(userId)) {
			setAttr("userId", Integer.parseInt(userId));
		}
		setAttr("userList", iUserService.findAll());
		setAttr("page", page);
	}

	/**
	 * 添加页面
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004005001)
	@ActionMode
	public void create() {
		setAttr("forbiddenWordsSwitch", ForbiddenWordsSwitch.values());
		setAttr("userList", iUserService.findAll());
	}

	/**
	 * 保存
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004005001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void save() {
		SmsPriorityWords words = getModel(SmsPriorityWords.class, "smsPriorityWords");
		renderResultJson(iSmsPriorityWordsService.save(words));
	}

	/**
	 * 编辑页面
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004005002)
	@ActionMode
	public void edit() {
		long id = getParaToLong("id");
		SmsPriorityWords words = iSmsPriorityWordsService.get((int) id);
		setAttr("userList", iUserService.findAll());
		setAttr("forbiddenWordsSwitch", ForbiddenWordsSwitch.values());
		setAttr("smsPriorityWords", words);
	}

	/**
	 * 启用/禁用/修改
	 */
	@AuthCode(code= {OperCode.OPER_CODE_2004005002,OperCode.OPER_CODE_2004005003})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void enableAndisable() {
		SmsPriorityWords words = getModel(SmsPriorityWords.class, "smsPriorityWords");
		renderResultJson(iSmsPriorityWordsService.enableAndisable(words));
	}
	
	/**
	 * 删除
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004005004)
	@ActionMode(type = EnumConstant.ActionType.JSON)
    public void delete(){
        renderResultJson(iSmsPriorityWordsService.delete(getParaToInt("id")));
    }

}
