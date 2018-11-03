//*********************************************************************
//系统名称：DBRDR
//Copyright(C)2000-2016 NARI Information and Communication Technology 
//Branch. All rights reserved.
//版本信息：DBRDR-V1.000
//#作者：杨猛 $权重：100%#
//版本                                 日期                             作者               变更记录
//DBRDR-V1.000         2016年8月5日       杨猛　     新建
//*********************************************************************
package com.huashi.hsboss.web.controller.base;

import java.util.Map;

import com.huashi.bill.product.domain.Product;
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
 * @author ym
 * @created_at 2016年8月5日下午7:57:26
 */
@ViewMenu(code=MenuCode.MENU_CODE_1003003)
@ControllerBind(controllerKey="/base/product")
public class ProductController extends BaseController {

	@BY_NAME
	private IProductService iProductService;
	
	@AuthCode(code= {OperCode.OPER_CODE_1003003001, OperCode.OPER_CODE_1003003002,OperCode.OPER_CODE_1003003003,
			OperCode.OPER_CODE_1003003004,OperCode.OPER_CODE_1003003005})
	@ActionMode
	public void index(){
		String name = getPara("name");
		BossPaginationVo<Product> page = iProductService.findPage(getPN(), name);
		setAttr("name", name);
		setAttr("page", page);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1003003002})
	@ActionMode
	public void add(){
		
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1003003002})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create(){
		Product product = getModel(Product.class);
		product.setOperationId(getUserId());
		String money = getPara("money");
		product.setMoney(Double.valueOf(money));
		boolean result = iProductService.create(product);
		renderResultJson(result);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1003003003})
	@ActionMode
	public void edit(){
		int id = getParaToInt("id");
		Product product = iProductService.findById(id);
		setAttr("product", product);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1003003003})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update(){
		Product product = getModel(Product.class);
		product.setModifyOperationId(getUserId());
		String money = getPara("money");
		product.setMoney(Double.valueOf(money));
		boolean result = iProductService.update(product);
		renderResultJson(result);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1003003004})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void delete(){
		int id = getParaToInt("id");
		Map<String, Object> map = iProductService.delete(id);
		renderJson(map);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_1003003005})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void disabled(){
		int id = getParaToInt("id");
		int flag = getParaToInt("flag");
		boolean result = iProductService.disabled(id, flag);
		renderResultJson(result);
	}
	
}
