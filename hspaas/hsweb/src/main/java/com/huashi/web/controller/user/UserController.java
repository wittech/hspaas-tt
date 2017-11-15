package com.huashi.web.controller.user;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.user.domain.User;
import com.huashi.common.user.domain.UserProfile;
import com.huashi.common.user.service.IUserService;
import com.huashi.web.controller.BaseController;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

	@Reference
	private IUserService userService;

	@RequestMapping("/index")
	public String index(Model m) {
		m.addAttribute("userList", userService.findAll());
		return "index";
	}

	/**
	 * 
	 * TODO 账号信息
	 * 
	 * @param m
	 * @param session
	 * @return
	 */
	@RequestMapping("/basis")
	public String basis(Model m) {
		m.addAttribute("user", userService.getById(getCurrentUserId()));
		m.addAttribute("userBase", userService.getProfileByUserId(getCurrentUserId()));
		return "/user/basis";
	}

	/**
	 * 
	 * TODO 修改
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/update_submit", method = RequestMethod.POST)
	public @ResponseBody boolean update(UserProfile record, HttpSession session) {
		record.setUserId(getCurrentUserId());
		return userService.updateByUserId(record);
	}
	

	/**
	 * 跳转修改密码
	 * @return
	 */
	@RequestMapping(value = "/password", method = RequestMethod.GET)
	public String password(Model m,String id) {
		m.addAttribute("id", id);
		return "/user/password";
	}
	
	/**
	 * 更换手机号码
	 * @return
	 */
	@RequestMapping(value = "/replace_number", method = RequestMethod.GET)
	public String replaceNumber(Model m,String id) {
		m.addAttribute("id", getCurrentUserId());
		return "/user/replace_number";
	}
	
	/**
	 * 更换注册手机号码
	 * @return
	 */
	@RequestMapping(value = "/replace_number_submit", method = RequestMethod.POST)
	@ResponseBody
	public boolean replaceNumberSubmit(User u) {
		try {
			u.setId(getCurrentUserId());
			return userService.updateByPrimaryKeySelective(u);
		} catch (Exception e) {
			return false;
		}
	}
	
	
	/**
	 * 修改密码
	 * @return
	 */
	@RequestMapping(value = "/update_password", method = RequestMethod.POST)
	@ResponseBody
	public boolean updatePassword(String plainPassword, String newPassword) {
		try {
			return userService.updatePasword(getCurrentUserId(), plainPassword, newPassword);
		} catch (Exception e) {
			// 可以通过e.getMessage() 获取错误信息
			return false;
		}
	}

	/**
	 * 原密码校验
	 * @return
	 */
	@RequestMapping(value = "/password_exists", method = RequestMethod.POST)
	@ResponseBody
	public boolean passwordExists(String plainPassword) {
		try {
			return userService.passwordExists(getCurrentUserId(), plainPassword);
		} catch (Exception e) {
			// 可以通过e.getMessage() 获取错误信息
			return false;
		}
	}
}
