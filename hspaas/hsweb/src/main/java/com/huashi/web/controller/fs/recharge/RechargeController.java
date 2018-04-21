/**
 * 
 */
package com.huashi.web.controller.fs.recharge;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.bill.bill.model.FluxDiscountModel;
import com.huashi.bill.bill.service.IBillService;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.fs.product.service.IFluxProductService;
import com.huashi.web.controller.BaseController;


/**
 * 流量充值
 * 
 * @author Administrator
 *
 */
@Controller
@RequestMapping("/fs/recharge")
public class RechargeController extends BaseController{
	
	@Reference
	private IFluxProductService fluxProductService;
	@Reference
	private IBillService billService;
	@Reference
	private IUserBalanceService userBalanceService;
	
	/**
	 * 单号充值
	 * 
	 * @return
	 */
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public String create(Model model) {
		model.addAttribute("user_flow_type", "");
		//三种产品余额
		model.addAttribute("userbalance", userBalanceService.findByUserId(getCurrentUserId()));
		return "/fs/recharge/create";
	}
	
	/**
	 * 批量充值
	 * 
	 * @return
	 */
	@RequestMapping(value = "/batch_recharge", method = RequestMethod.GET)
	public String batchRecharge(Model model) {
		//三种产品余额
		model.addAttribute("userbalance", userBalanceService.findByUserId(getCurrentUserId()));
		return "/fs/recharge/batch_recharge";
	}
	
	/**
	 * 接口充值
	 * 
	 * @return
	 */
	@RequestMapping(value = "/inteface_recharge", method = RequestMethod.GET)
	public String intefaceRecharge() {
		return "/fs/recharge/inteface_recharge";
	}
	
	/**
	 * 根据号码获取套餐列表
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/product", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> product(String mobile) {
		return fluxProductService.findListAll(mobile);
	}
	
	/**
	 * 优惠价获取	
	 * @param operatorCode
	 * @param speed
	 * @return
	 */
	@RequestMapping(value = "/getDiscountPrice", method = RequestMethod.POST)
	public @ResponseBody FluxDiscountModel getDiscountPrice(String speed,String mobile) {
		try {
			return billService.getFluxDiscountPrice(getCurrentUserId(), speed, mobile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 充值
	 * @param mobile 手机号码
	 * @param prodScope 类型
	 * @param packages 套餐
	 * @return
	 */
	@RequestMapping(value = "/recharge_submit", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> rechargeSubmit(String mobile,String productId,String productName,String spend) {
		Map<String,Object> resultMap =new HashMap<String,Object>();
		resultMap.put("resultCode","10000");
		resultMap.put("resultMsg", "充值成功");
		return resultMap;
	}
	
	/**
	 * 充值
	 * @param mobile 手机号码
	 * @param prodScope 类型
	 * @param packages 套餐
	 * @return
	 */
	@RequestMapping(value = "/batch_submit", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> batchSubmit(String mobile,String productId,String productName,String spend) {
		Map<String,Object> resultMap =new HashMap<String,Object>();
		resultMap.put("resultCode","10000");
		resultMap.put("resultMsg", "充值成功");
		return resultMap;
	}

	/**
	 * 读取文件 解析号码 
	 * @param filePath
	 * @return 三个数组进行区分 移动、电信、联通
	 */
	@RequestMapping(value = "/get_product", method = RequestMethod.POST)
	public @ResponseBody Map<String,Object> getProduct(String filename){
//		String mobleNumbers = ExcelUtil.readExcelFirstColumn(tmpStoreDirectory + filename);
//		if(StringUtils.isEmpty(mobleNumbers)) {
//            return null;
//        }
//		
//		return fluxProductService.findListByMobile(mobleNumbers);
	    return null;
	}
	
	
}
