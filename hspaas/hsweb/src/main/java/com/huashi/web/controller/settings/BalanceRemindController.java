
/**************************************************************************
* Copyright (c) 2015-2016 HangZhou Huashi, Inc.
* All rights reserved.
* 
* 项目名称：华时短信平台
* 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
*           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
*           识产权保护的内容。                            
***************************************************************************/
package com.huashi.web.controller.settings;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.settings.domain.BalanceRemind;
import com.huashi.common.settings.service.IBalanceRemindService;
import com.huashi.web.controller.BaseController;


/**
 * TODO 余额提醒
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年1月27日 下午10:21:20
 */
@Controller
@RequestMapping("/settings/balance_remind")
public class BalanceRemindController extends BaseController {
	
	@Reference
	private IBalanceRemindService balanceRemindService;

	/**
	 * 
	 * TODO 首页
	 * 
	 * @param model
	 * @param cl 样式 1短信、2流量 3语音
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model,String type) {
		model.addAttribute("balanceList", balanceRemindService.selectByUserId(getCurrentUserId()));
		model.addAttribute("type", type==null?"1":type);
		return "/settings/balance_remind/index";
	}

	/**
	 * 
	 * TODO 修改
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody boolean update(BalanceRemind balanceRemind) {
		balanceRemind.setUserId(getCurrentUserId());
		if(balanceRemind.getId() !=null && balanceRemind.getId()>0){
			return balanceRemindService.update(balanceRemind);
		}else{
			return balanceRemindService.insert(balanceRemind)>0;
		}
	}
	
	/**
	 * 
	 * TODO 保存
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping(value = "/insert", method = RequestMethod.POST)
	public @ResponseBody Integer insert(BalanceRemind balanceRemind) {
		balanceRemind.setUserId(getCurrentUserId());
		return balanceRemindService.insert(balanceRemind);
	}

	/**
	 * 查询短信、流量、语音记录
	 * @return
	 */
	@RequestMapping(value = "/find_list", method = RequestMethod.POST)
	public @ResponseBody List<BalanceRemind> findList(){
		return balanceRemindService.selectByUserId(getCurrentUserId());
	}
	
}
