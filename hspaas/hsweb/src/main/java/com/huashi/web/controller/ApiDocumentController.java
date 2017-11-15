/**
 * 
 */
package com.huashi.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//import com.huashi.web.filter.PermissionClear;

/**
 * api 文档
 * @author Administrator
 *
 */
@Controller
//@PermissionClear
@RequestMapping("/api")
public class ApiDocumentController extends BaseController {
	
	/**
	 * 
	 * TODO 开发流程
	 * 
	 * @return
	 */
	@RequestMapping(value = "/development", method = RequestMethod.GET)
	public String development() {
		return "/api/development";
	}
	
	/**
	 * 
	 * TODO 调用说明
	 * 
	 * @return
	 */
	@RequestMapping(value = "/explain", method = RequestMethod.GET)
	public String explain() {
		return "/api/explain";
	}
	
	/**
	 * 
	 * TODO 账户API
	 * 
	 * @return
	 */
	@RequestMapping(value = "/account", method = RequestMethod.GET)
	public String account() {
		return "/api/account";
	}
	
	/**
	 * 
	 * TODO 模板API
	 * 
	 * @return
	 */
	@RequestMapping(value = "/template", method = RequestMethod.GET)
	public String template() {
		return "/api/template";
	}
	
	/**
	 * 
	 * TODO 短信API
	 * 
	 * @return
	 */
	@RequestMapping(value = "/sms", method = RequestMethod.GET)
	public String sms() {
		return "/api/sms";
	}
	
	/**
	 * 
	 * TODO 语音API
	 * 
	 * @return
	 */
	@RequestMapping(value = "/voice", method = RequestMethod.GET)
	public String voice() {
		return "/api/voice";
	}
	
	/**
	 * 
	 * TODO 流量API
	 * 
	 * @return
	 */
	@RequestMapping(value = "/flow", method = RequestMethod.GET)
	public String flow() {
		return "/api/flow";
	}
	
	/**
	 * 
	 * TODO 隐私通话API
	 * 
	 * @return
	 */
	@RequestMapping(value = "/conversation", method = RequestMethod.GET)
	public String conversation() {
		return "/api/conversation";
	}
	
	/**
	 * 
	 * TODO 返回值说明
	 * 
	 * @return
	 */
	@RequestMapping(value = "/return_explain", method = RequestMethod.GET)
	public String return_explain() {
		return "/api/return_explain";
	}
	
	/**
	 * 
	 * TODO 代码示例
	 * 
	 * @return
	 */
	@RequestMapping(value = "/demo", method = RequestMethod.GET)
	public String demo() {
		return "/api/demo";
	}
	
	/**
	 * 
	 * TODO 贡献代码
	 * 
	 * @return
	 */
	@RequestMapping(value = "/contribution", method = RequestMethod.GET)
	public String contribution() {
		return "/api/contribution";
	}
	
	/**
	 * 
	 * TODO 常见问题
	 * 
	 * @return
	 */
	@RequestMapping(value = "/problem", method = RequestMethod.GET)
	public String problem() {
		return "/api/problem";
	}
	
	/**
	 * 
	 * TODO SDK下载
	 * 
	 * @return
	 */
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public String download() {
		return "/api/download";
	}
	
	/**
	 * 
	 * TODO 帮助支持
	 * 
	 * @return
	 */
	@RequestMapping(value = "/help", method = RequestMethod.GET)
	public String help() {
		return "/api/help";
	}
	
	/**
	 * 
	 * TODO 关于我们
	 * 
	 * @return
	 */
	@RequestMapping(value = "/about", method = RequestMethod.GET)
	public String about() {
		return "/api/about";
	}
	
	/**
	 * 公司简介
	 * @return
	 */
	@RequestMapping(value = "/company", method = RequestMethod.GET)
	public String company(){
		return "/api/company";
	}
	
	/**
	 * 联系我们
	 * @return
	 */
	@RequestMapping(value = "/contact", method = RequestMethod.GET)
	public String contact(){
		return "/api/contact";
	}
	
	/**
	 * 招贤纳士
	 * @return
	 */
	@RequestMapping(value = "/job", method = RequestMethod.GET)
	public String job(){
		return "/api/job";
	}
	
}
