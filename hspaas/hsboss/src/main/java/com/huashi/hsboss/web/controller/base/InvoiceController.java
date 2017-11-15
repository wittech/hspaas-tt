package com.huashi.hsboss.web.controller.base;

import com.huashi.common.finance.domain.InvoiceBalance;
import com.huashi.common.finance.domain.InvoiceRecord;
import com.huashi.common.finance.service.IInvoiceBalanceService;
import com.huashi.common.finance.service.IInvoiceRecordService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject.BY_NAME;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

import java.util.HashMap;
import java.util.Map;

/**
 * 发票管理
 * @author ym
 * @created_at 2016年6月28日下午4:24:09
 */
@ViewMenu(code=MenuCode.MENU_CODE_1004)
@ControllerBind(controllerKey="/base/invoice")
public class InvoiceController extends BaseController {

	@BY_NAME
	private IInvoiceRecordService iInvoiceRecordService;
	@BY_NAME
	private IInvoiceBalanceService iInvoiceBalanceService;
	
	public void index(){
		String invoiceKeyword = getPara("invoiceKeyword");
		String userKeyword = getPara("userKeyword");
		BossPaginationVo<InvoiceRecord> page = iInvoiceRecordService.findPage(getPN(), invoiceKeyword, userKeyword);
		setAttr("page", page);
		setAttr("invoiceKeyword", invoiceKeyword);
		setAttr("userKeyword", userKeyword);
	}
	
	public void edit(){
		InvoiceRecord record = iInvoiceRecordService.findById(getParaToInt("id"));
		setAttr("record", record);
	}
	
	public void update(){
		InvoiceRecord record = getModel(InvoiceRecord.class);
		boolean result = iInvoiceRecordService.update(record);
		renderResultJson(result);
	}

	public void add(){

	}

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
