package com.huashi.web.controller;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.apache.dubbo.config.annotation.Reference;
import com.huashi.common.notice.service.IEmailVerifyService;
import com.huashi.common.user.context.UserContext.Source;
import com.huashi.common.user.domain.User;
import com.huashi.common.user.model.RegisterModel;
import com.huashi.common.user.service.IRegisterService;
import com.huashi.common.user.service.IUserService;
import com.huashi.web.filter.PermissionClear;

@Controller
@PermissionClear
@RequestMapping("/register")
public class RegisterController extends BaseController {

	@Reference
	private IUserService userService;
	@Reference
	private IRegisterService registerService;
	@Reference
	private IEmailVerifyService emailVerifyService;
	private static final String EMAIL_VERIFY_UID = "EmailVerifyUID"; // 验证UID(唯一)
	private static final String EMAIL_VERIFY_PASSED = "EmailVerifyPassed"; // 邮箱是否验证通过
	
	/**
	 * 
	 * TODO 跳转至注册填写邮箱页面
	 * 
	 * @return
	 */
	@RequestMapping(value = "/fill_email", method = RequestMethod.GET)
	public String fillEmail(Model model) {
		return "/register/fill_email";
	}

	/**
	 * 
	 * TODO 发送邮件
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/send_email", method = RequestMethod.POST)
	public @ResponseBody boolean sendEmail(String email) {
		return registerService.sendEmailBeforeVerify(email);
	}

	/**
	 * 
	 * TODO 验证链接是否有效
	 * 
	 * @param uid
	 *            有效码
	 * @return
	 */
	@RequestMapping(value = "/verify_email/{uid}", method = RequestMethod.GET)
	public String verifyEmail(@PathVariable String uid, Model model) {
		// 判断链接是否有效，有效则跳转至填写详情页面，并置 邮箱链接 为已失效
		try {
			String email = emailVerifyService.isAvaiable(uid);
			if (StringUtils.isNotEmpty(email)) {				
				session.setAttribute(EMAIL_VERIFY_UID, uid);
				session.setAttribute(EMAIL_VERIFY_PASSED, email);
				return fillMessage(model);
			}
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		return fillEmail(model);
	}

	/**
	 * 
	 * TODO 跳转至注册用户信息表单
	 * 
	 * @return
	 */
	@RequestMapping(value = "/fill_message", method = RequestMethod.GET)
	public String fillMessage(Model model) {
		Object email = session.getAttribute(EMAIL_VERIFY_PASSED);
		if (email == null) {
            {
                return fillEmail(model);
            }
        }
		return "/register/fill_message";
	}

	/**
	 * 
	 * TODO 判断邮箱用户是否已被注册
	 * 
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "/email_exists", method = RequestMethod.POST)
	public @ResponseBody boolean isEmailExists(String email) {
		return userService.isUserExistsByEmail(email);
	}

	/**
	 * 
	 * TODO 判断手机用户是否已被注册
	 * 
	 * @param mobile
	 * @return
	 */
	@RequestMapping(value = "/mobile_exists", method = RequestMethod.POST)
	public @ResponseBody boolean isMobileExists(String mobile) {
		return userService.isUserExistsByMobile(mobile);
	}

	/**
	 * 
	 * TODO 提交表单，注册
	 * 
	 * @param user
	 * @return
	 */
	@RequestMapping(value = "/commit", method = RequestMethod.POST)
	@ResponseBody
	public boolean register(User user) {
//		if (result.hasErrors())
//			return false;
		if (StringUtils.isEmpty(user.getEmail())) {
            {
                return false;
            }
        }
		if(session.getAttribute(EMAIL_VERIFY_UID) == null) {
            {
                return false;
            }
        }
		
		user.setRegistIp(getClientIp());
		
		try {
			boolean isSuccess = registerService.register(new RegisterModel(Source.WEB_REGISTER, user));
			if(isSuccess) {
				emailVerifyService.activeEmail(session.getAttribute(EMAIL_VERIFY_UID).toString());
				session.removeAttribute(EMAIL_VERIFY_UID);
				session.removeAttribute(EMAIL_VERIFY_PASSED);
			}
			return isSuccess;
		} catch (Exception e) {
			logger.error("注册失败", e);
			return false;
		}
	}

	/**
	 * 
	 * TODO 注册完成
	 * 
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public String complete(Model model, HttpSession session) {
		Object email = session.getAttribute(EMAIL_VERIFY_PASSED);
		if (email == null) {
            {
                return fillEmail(model);
            }
        }
		
		model.addAttribute("email", email);
		session.removeAttribute(EMAIL_VERIFY_PASSED);
		session.removeAttribute(DEFAULT_CAPTCHA_SMS_CODE_KEY);
		return "/register/complete";
	}

}
