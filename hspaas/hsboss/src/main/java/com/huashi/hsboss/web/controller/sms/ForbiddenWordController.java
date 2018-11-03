package com.huashi.hsboss.web.controller.sms;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ActionMode;
import com.huashi.hsboss.annotation.AuthCode;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.EnumConstant;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.constant.OperCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.huashi.sms.settings.domain.ForbiddenWords;
import com.huashi.sms.settings.service.IForbiddenWordsService;
import com.jfinal.ext.route.ControllerBind;

/**
 * TODO Author youngmeng Created 2016-10-14 15:30
 */
@ControllerBind(controllerKey = "/sms/forbidden_word")
@ViewMenu(code = MenuCode.MENU_CODE_2004003)
public class ForbiddenWordController extends BaseController {

	@Inject.BY_NAME
	private IForbiddenWordsService iForbiddenWordsService;

	@AuthCode(code= {OperCode.OPER_CODE_2004003001, OperCode.OPER_CODE_2004003002,OperCode.OPER_CODE_2004003003
			,OperCode.OPER_CODE_2004003004})
	@ActionMode
	public void index() {
		String keyword = getPara("keyword");
		BossPaginationVo<ForbiddenWords> page = iForbiddenWordsService
				.findPage(getPN(), keyword);
		setAttr("page", page);
		setAttr("keyword", keyword);
	}

	/**
	 * 
	 * TODO 跳转添加页面
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004003002)
	@ActionMode
	public void add() {
		setAttr("wordLables", iForbiddenWordsService.findWordsLabelLibrary());
	}

	/**
	 * 
	 * TODO 保存
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004003002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create() {
		ForbiddenWords forbiddenWords = getModel(ForbiddenWords.class, "forbiddenWords");
		renderResultJson(iForbiddenWordsService.saveForbiddenWords(forbiddenWords));

	}

	/**
	 * 
	 * TODO 删除
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004003004)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete() {
		boolean result = iForbiddenWordsService.deleteWord(getParaToInt("id"));
		renderResultJson(result);
	}
	
	/**
	 * 
	 * TODO 跳转编辑页面
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004003003)
	@ActionMode
	public void edit() {
		setAttr("wordLables", iForbiddenWordsService.findWordsLabelLibrary());
		setAttr("forbiddenWords", iForbiddenWordsService.get(getParaToInt("id")));
	}

	/**
	 * 
	 * TODO 修改
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004003003)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update() {
		renderResultJson(iForbiddenWordsService.update(getModel(ForbiddenWords.class, "forbiddenWords")));
	}

	/**
	 * 
	 * TODO 加载REDIS
	 */
	@AuthCode(code= OperCode.OPER_CODE_2004003001)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void loadingRedis() {
		renderResultJson(iForbiddenWordsService.reloadRedisForbiddenWords());
	}

}
