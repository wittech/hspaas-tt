package com.huashi.common.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.huashi.common.user.domain.User;
import com.huashi.common.user.exception.LoginException;
import com.huashi.common.util.SecurityUtil;
import com.huashi.common.vo.SessionUser;

@com.alibaba.dubbo.config.annotation.Service
public class LoginService implements ILoginService {

    @Autowired
    private IUserService userService;
    private Logger       logger = LoggerFactory.getLogger(LoginService.class);

    @Override
    public SessionUser login(String username, String password) throws LoginException {
        User user = userService.getByUsername(username);
        if (user == null) {
            logger.info("login username : {} 数据为空", username);
            throw new LoginException("用户数据异常");
        }

        boolean isAvaiable = userService.verify(username, SecurityUtil.decode(password, user.getSalt()));
        if (isAvaiable) {
            return new SessionUser(user.getId(), user.getEmail(), user.getMobile());
        }

        throw new LoginException("用户名密码认证失败");
    }

}
