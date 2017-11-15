
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.huashi.web.controller.BaseController;

/**
 * TODO 协议合同管理
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月5日 下午5:59:39
 */
@Controller
@RequestMapping("/service/agreement_contract")
public class AgreementContractController extends BaseController {

	/**
	 * 
	 * TODO 首页
	 * 
	 * @return
	 */
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index() {
		return "/service/agreement_contract/index";
	}
}
