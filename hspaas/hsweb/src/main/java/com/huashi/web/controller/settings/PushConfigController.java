
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

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.settings.domain.PushConfig;
import com.huashi.common.settings.service.IPushConfigService;
import com.huashi.web.controller.BaseController;

/**
 * TODO 推送设置
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年1月24日 下午7:28:39
 */
@Controller
@RequestMapping("/settings/push")
public class PushConfigController extends BaseController {

	@Reference
	private IPushConfigService pushConfigService;

	/**
	 * 
	 * TODO 首页
	 * 
	 * @param model
	 * @param type 1、短信、3流量 4、语音
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model,String type) {
		model.addAttribute("type", type==null?"1":type);
		return "/settings/push/index";
	}

	/**
	 * 查询短信、流量、语音记录
	 * @return
	 */
	@RequestMapping(value = "/find_list", method = RequestMethod.POST)
	public @ResponseBody List<PushConfig> findList(){
		return pushConfigService.findByUserId(getCurrentUserId());
	}
	
	/**
	 * 
	 * TODO 修改推送设置
	 * 
	 * @param config
	 * @return
	 */
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public @ResponseBody boolean update(PushConfig config) {
		config.setUserId(getCurrentUserId());
		if(config.getType()==2){
			if(StringUtils.isNotEmpty(config.getUrl())){
				config.setStatus(1);
			}else{
				config.setStatus(0);
			}
		}
		//根据用户id 类型查询是否存在
		PushConfig cf = pushConfigService.getByUserId(getCurrentUserId(),config.getType());
		if(cf != null){
			config.setId(cf.getId());
			return pushConfigService.update(config);
		}
//		if(config.getId()!=null && config.getId()>0){
//			return pushConfigService.update(config);
//		}
		return pushConfigService.save(config);
	}

}
