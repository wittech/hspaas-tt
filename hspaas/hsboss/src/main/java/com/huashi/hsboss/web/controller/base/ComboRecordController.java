package com.huashi.hsboss.web.controller.base;

import com.huashi.bill.order.constant.TradeOrderContext;
import com.huashi.bill.order.domain.TradeOrder;
import com.huashi.bill.order.service.ITradeOrderService;
import com.huashi.common.vo.BossPaginationVo;
import com.huashi.hsboss.annotation.ViewMenu;
import com.huashi.hsboss.config.plugin.spring.Inject;
import com.huashi.hsboss.constant.MenuCode;
import com.huashi.hsboss.web.controller.common.BaseController;
import com.jfinal.ext.route.ControllerBind;

/**
 * 套餐购买记录查询
 * @author ym
 * @created_at 2016年6月28日下午4:03:18
 */
@ViewMenu(code=MenuCode.MENU_CODE_1003002)
@ControllerBind(controllerKey="/base/combo_record")
public class ComboRecordController extends BaseController {

	@Inject.BY_NAME
	private ITradeOrderService iTradeOrderService;

	public void index(){
		String keyword = getPara("keyword");
		String start = getPara("start");
		String end = getPara("end");
		int status = getParaToInt("status",-1);
		BossPaginationVo<TradeOrder> page = iTradeOrderService.findPage(getPN(),keyword,start,end,status);
		setAttr("page",page);
		setAttr("keyword",keyword);
		setAttr("start",start);
		setAttr("end",end);
		setAttr("status",status);
		setAttr("traderOrderStatus", TradeOrderContext.TradeOrderStatus.values());
	}
	
	
	
}
