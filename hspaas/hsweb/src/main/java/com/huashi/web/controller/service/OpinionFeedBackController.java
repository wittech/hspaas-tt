/**************************************************************************
 * Copyright (c) 2015-2016 HangZhou Huashi, Inc.
 * All rights reserved.
 * 
 * 项目名称：华时短信平台
 * 版权说明：本软件属杭州华时科技有限公司所有，在未获得杭州华时科技有限公司正式授权
 *           情况下，任何企业和个人，不能获取、阅读、安装、传播本软件涉及的任何受知
 *           识产权保护的内容。                            
 ***************************************************************************/
package com.huashi.web.controller.service;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.service.context.OpinionFeedBackContext;
import com.huashi.common.service.domain.OpinionFeedBack;
import com.huashi.common.service.service.IOpinionFeedBackService;
import com.huashi.web.controller.BaseController;

/**
 * TODO 意见反馈管理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月5日 下午12:30:40
 */
@Controller
@RequestMapping("/service/opinion")
public class OpinionFeedBackController extends BaseController {

	@Reference
	private IOpinionFeedBackService opinionFeedBackService;

	/**
	 * 
	 * TODO 首页
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("feeBackTypes",
				OpinionFeedBackContext.FeedbackType.values());
		return "/service/opinion/index";
	}

	/**
	 * 
	 * TODO 保存
	 * 
	 * @param title
	 * @param content
	 * @param type
	 * @return
	 */
	@RequestMapping(value = "/preservation", method = RequestMethod.POST)
	public @ResponseBody boolean preservation(OpinionFeedBack opinionFeedBack) {
		opinionFeedBack.setUserId(getCurrentUserId());
		return opinionFeedBackService.insert(opinionFeedBack);
	}

}
