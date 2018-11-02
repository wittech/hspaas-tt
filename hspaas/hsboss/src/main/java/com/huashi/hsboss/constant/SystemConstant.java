package com.huashi.hsboss.constant;

import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

/**
 * 系统常量
 * 
 * @author ym
 * @created_at 2016年6月22日下午2:09:36
 */
public class SystemConstant {

    /**
     * SESSION中记录的验证码
     */
    public static final String  SESSION_VALIDATE_CODE = "session_validate_code";

    /**
     * 用户SESSION名称定义
     */
    public static final String  USER_SESSION          = "user_session";

    /**
     * 开户默认密码
     */
    public static final String  DEFAULT_PASSWORD      = "huashi2016";

    /**
     * GOOGLE MFA授权认证开关
     */
    public static boolean       USER_AUTH_MFA         = false;

    /**
     * mfa认证开关节点名称定义
     */
    private static final String NODE_USER_AUTH_MFA    = "user.auth.mfa";

    static {
        Prop prop = PropKit.use("config.properties");
        USER_AUTH_MFA = prop.getBoolean(NODE_USER_AUTH_MFA, false);
    }
}
