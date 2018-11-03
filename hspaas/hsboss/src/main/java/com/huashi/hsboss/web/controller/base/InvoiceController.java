package com.huashi.hsboss.web.controller.base;

import com.huashi.common.finance.domain.InvoiceBalance;
import com.huashi.common.finance.domain.InvoiceRecord;
import com.huashi.common.finance.service.IInvoiceBalanceService;
import com.huashi.common.finance.service.IInvoiceRecordService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 发票管理
 * @author ym
 * @created_at 2016年6月28日下午4:24:09
 */
@ViewMenu(code=MenuCode.MENU_CODE_7001)
@ControllerBind(controllerKey="/base/invoice")
public class InvoiceController extends BaseController {

	@BY_NAME
	private IInvoiceRecordService iInvoiceRecordService;
	@BY_NAME
	private IInvoiceBalanceService iInvoiceBalanceService;
	
	@AuthCode(code= {OperCode.OPER_CODE_7001001, OperCode.OPER_CODE_7001002,OperCode.OPER_CODE_7001003})
	@ActionMode
	public void index(){
		String invoiceKeyword = getPara("invoiceKeyword");
		String userKeyword = getPara("userKeyword");
		BossPaginationVo<InvoiceRecord> page = iInvoiceRecordService.findPage(getPN(), invoiceKeyword, userKeyword);
		setAttr("page", page);
		setAttr("invoiceKeyword", invoiceKeyword);
		setAttr("userKeyword", userKeyword);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_7001002,OperCode.OPER_CODE_7001003})
	@ActionMode
	public void edit(){
		InvoiceRecord record = iInvoiceRecordService.findById(getParaToInt("id"));
		setAttr("record", record);
	}
	
	@AuthCode(code= {OperCode.OPER_CODE_7001002,OperCode.OPER_CODE_7001003})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void update(){
		InvoiceRecord record = getModel(InvoiceRecord.class);
		boolean result = iInvoiceRecordService.update(record);
		renderResultJson(result);
	}

	@AuthCode(code= {OperCode.OPER_CODE_7001001})
	@ActionMode
	public void add(){

	}

	@AuthCode(code= {OperCode.OPER_CODE_7001001})
	@ActionMode(type = EnumConstant.ActionType.JSON)
	public void create(){
		InvoiceRecord record = getModel(InvoiceRecord.class,"record");
		boolean result = iInvoiceRecordService.save(record);
		renderResultJson(result);
	}

	public void userBalance(){
        int userId = getParaToInt("userId");
        InvoiceBalance balance = iInvoiceBalanceService.getByUserId(userId);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("money",0);
		if(balance != null){
			map.put("money",balance.getMoney());
		}
        renderJson(map);
    }
}
