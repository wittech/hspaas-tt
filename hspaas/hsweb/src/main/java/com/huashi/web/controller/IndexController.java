package com.huashi.web.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.huashi.common.notice.service.INotificationService;
import com.huashi.common.settings.service.IBalanceRemindService;
import com.huashi.common.user.domain.UserBalance;
import com.huashi.common.user.exception.LoginException;
import com.huashi.common.user.service.ILoginService;
import com.huashi.common.user.service.IUserAccountService;
import com.huashi.common.user.service.IUserBalanceService;
import com.huashi.common.user.service.IUserDeveloperService;
import com.huashi.common.user.service.IUserService;
import com.huashi.common.vo.SessionUser;
import com.huashi.constants.CommonContext.PlatformType;
import com.huashi.web.context.WebConstants;
import com.huashi.web.filter.PermissionClear;
import com.huashi.web.prervice.common.SearchPrervice;

@Controller
public class IndexController extends BaseController {

    @Reference
    private IUserService          userService;
    @Reference
    private IUserDeveloperService userDeveloperService;
    @Reference
    private IUserAccountService   userAccountService;
    @Reference
    private IUserBalanceService   userBalanceService;
    @Reference
    private ILoginService         loginService;
    @Reference
    private INotificationService  notificationService;
    @Reference
    private IBalanceRemindService balanceRemindService;
    @Autowired
    private SearchPrervice        searchPrervice;

    /**
     * TODO 网站首页
     * 
     * @return
     */
    @PermissionClear
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "index";
    }

    /**
     * TODO 登录页面
     * 
     * @return
     */
    @PermissionClear
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login() {
        return "/member/login/login";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        session.removeAttribute(WebConstants.LOGIN_USER_SESSION_KEY);
        return "redirect:/index";
    }

    /**
     * 流量
     * 
     * @return
     */
    @PermissionClear
    @RequestMapping(value = "/flow", method = RequestMethod.GET)
    public String flow() {
        return "flow";
    }

    /**
     * 短信
     * 
     * @return
     */
    @PermissionClear
    @RequestMapping(value = "/sms", method = RequestMethod.GET)
    public String sms() {
        return "sms";
    }

    /**
     * 语音
     * 
     * @return
     */
    @PermissionClear
    @RequestMapping(value = "/voice", method = RequestMethod.GET)
    public String voice() {
        return "voice";
    }

    /**
     * 微推
     * 
     * @return
     */
    @PermissionClear
    @RequestMapping(value = "/wecp", method = RequestMethod.GET)
    public String wecp() {
        return "wecp";
    }

    /**
     * TODO 登录校验
     * 
     * @return
     */
    @PermissionClear
    @RequestMapping(value = "/login/verify", method = RequestMethod.POST)
    @ResponseBody
    public boolean doLoginVerify() {
        try {
            SessionUser user = loginService.login(request.getParameter("account"), request.getParameter("password"));
            session.setAttribute(WebConstants.LOGIN_USER_SESSION_KEY, user);
            return true;
        } catch (LoginException e) {
            return false;
        }

    }

    /**
     * TODO 用户控制中心
     * 
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(value = "/console", method = RequestMethod.GET)
    public String console(Model model) {
//        if (!super.isLogin()) {
//            return login();
//        }
//
//        model.addAttribute("accountBalance", userAccountService.getAccountByUserId(getCurrentUserId()));
//        model.addAttribute("developer", userDeveloperService.getByUserId(getCurrentUserId()));
//        model.addAttribute("notifications", notificationService.findTopList());
//        model.addAttribute("balanceRemind", balanceRemindService.selectByUserId(getCurrentUserId()));
//
//        this.balance(model);
        return "/console/console";
    }
    
    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashborad(Model model) {
        return "/console/dashboard";
    }
    
    /**
     * TODO 设置平台业务量余额信息
     * 
     * @param model
     */
    private void balance(Model model) {
        List<UserBalance> list = userBalanceService.findByUserId(getCurrentUserId());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (UserBalance balance : list) {
            if (balance == null) {
                continue;
            }
            if (PlatformType.SEND_MESSAGE_SERVICE.getCode() == balance.getType()) {
                model.addAttribute("messageBalance", balance.getBalance());
            } else if (PlatformType.FLUX_SERVICE.getCode() == balance.getType()) {
                model.addAttribute("fluxBalance", balance.getBalance());
            } else if (PlatformType.VOICE_SERVICE.getCode() == balance.getType()) {
                model.addAttribute("voiceBalance", balance.getBalance());
            }
        }
    }

    /**
     * TODO 首页快捷查询
     * 
     * @param content 查询内容
     * @param platformType 平台类型
     * @param quickType 查询类型（手机号码或者状态码）
     * @return
     */
    @RequestMapping(value = "/quick_search", method = RequestMethod.POST)
    @ResponseBody
    public Object quickSearch(String content, int platformType, int quickType) {
        return searchPrervice.quickSearch(getCurrentUserId(), content, platformType, quickType);
    }

}
