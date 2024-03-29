package com.huashi.hsboss.web.controller.base;

import java.util.List;

import com.huashi.bill.product.domain.Combo;
import com.huashi.bill.product.domain.Product;
import com.huashi.bill.product.service.IComboService;
import com.huashi.bill.product.service.IProductService;
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
 * 套餐管理
 * 
 * @author ym
 * @created_at 2016年6月28日下午3:57:13
 */
@ViewMenu(code = MenuCode.MENU_CODE_1003001)
@ControllerBind(controllerKey = "/base/combo")
public class ComboController extends BaseController {

	@BY_NAME
	private IComboService iComboService;
	@BY_NAME
	private IProductService iProductService;

	@AuthCode(code= {OperCode.OPER_CODE_1003001001,OperCode.OPER_CODE_1003001002, OperCode.OPER_CODE_1003001003,OperCode.OPER_CODE_1003001004
			,OperCode.OPER_CODE_1003001005})
	@ActionMode
	public void index() {
		String name = getPara("name");
		String start = getPara("start");
		String end = getPara("end");
		BossPaginationVo<Combo> page = iComboService.findPage(getPN(), name, start, end);
		setAttr("name", name);
		setAttr("page", page);
		setAttr("start", start);
		setAttr("end", end);
	}

	@AuthCode(code= OperCode.OPER_CODE_1003001002)
	@ActionMode
	public void add() {

	}

	public void productList() {
		String name = getPara("name");
		String filterIds = getPara("filterIds", "");
		List<Product> productList = iProductService.getSelectProductList(name);
		setAttr("name", name);
		setAttr("filterIds", filterIds);
		setAttr("productList", productList);
	}

	@AuthCode(code= OperCode.OPER_CODE_1003001002)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create() {
		Combo combo = getModel(Combo.class);
		String originalMoney = getPara("originalMoney");
		String sellMoney = getPara("sellMoney");
		combo.setOriginalMoney(Double.valueOf(originalMoney));
		combo.setSellMoney(Double.valueOf(sellMoney));
		combo.setOperatorId(getUserId());
		String productIds = getPara("productIds");
		boolean result = iComboService.create(combo, productIds);
		renderResultJson(result);
	}

	@AuthCode(code= OperCode.OPER_CODE_1003001003)
	@ActionMode
	public void edit() {
		int id = getParaToInt("id");
		Combo combo = iComboService.findById(id);
		List<Product> productList = iProductService.getProductListByComboId(id);
		setAttr("productList", productList);
		setAttr("combo", combo);
	}

	@AuthCode(code= OperCode.OPER_CODE_1003001003)
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update() {
		Combo combo = getModel(Combo.class);
		String originalMoney = getPara("originalMoney");
		String sellMoney = getPara("sellMoney");
		combo.setOriginalMoney(Double.valueOf(originalMoney));
		combo.setSellMoney(Double.valueOf(sellMoney));
		combo.setOperatorId(getUserId());
		String productIds = getPara("productIds");
		boolean result = iComboService.update(combo, productIds);
		renderResultJson(result);
	}

	@AuthCode(code= {OperCode.OPER_CODE_1003001005})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void disabled() {
		int id = getParaToInt("id");
		int flag = getParaToInt("flag");
		boolean result = iComboService.updateStatus(id, flag);
		renderResultJson(result);
	}

	@AuthCode(code= {OperCode.OPER_CODE_1003001004})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete() {
		int id = getParaToInt("id");
		boolean result = iComboService.delete(id);
		renderResultJson(result);
	}
}
