package com.huashi.hsboss.web.controller.sms;

import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.MenuCode;
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
	public void add() {
		setAttr("wordLables", iForbiddenWordsService.findWordsLabelLibrary());
	}

	/**
	 * 
	 * TODO 保存
	 */
	public void create() {
		ForbiddenWords forbiddenWords = getModel(ForbiddenWords.class, "forbiddenWords");
		renderResultJson(iForbiddenWordsService.saveForbiddenWords(forbiddenWords));

	}

	/**
	 * 
	 * TODO 删除
	 */
	public void delete() {
		boolean result = iForbiddenWordsService.deleteWord(getParaToInt("id"));
		renderResultJson(result);
	}
	
	/**
	 * 
	 * TODO 跳转编辑页面
	 */
	public void edit() {
		setAttr("wordLables", iForbiddenWordsService.findWordsLabelLibrary());
		setAttr("forbiddenWords", iForbiddenWordsService.get(getParaToInt("id")));
	}

	/**
	 * 
	 * TODO 修改
	 */
	public void update() {
		renderResultJson(iForbiddenWordsService.update(getModel(ForbiddenWords.class, "forbiddenWords")));
	}

	/**
	 * 
	 * TODO 加载REDIS
	 */
	public void loadingRedis() {
		renderResultJson(iForbiddenWordsService.reloadRedisForbiddenWords());
	}

}
