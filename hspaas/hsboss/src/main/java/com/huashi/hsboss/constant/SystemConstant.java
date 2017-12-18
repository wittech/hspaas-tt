
package com.huashi.hsboss.constant;

import com.jfinal.kit.PropKit;

/**
 * 系统常量
 * @author ym
 * @created_at 2016年6月22日下午2:09:36
 */
public class SystemConstant {

	
	public static final String SESSION_VALIDATE_CODE = "session_validate_code";
	
	public static final String USER_SESSION = "user_session";
	
	public static final String DEFAULT_PASSWORD = "huashi2016";
	
	/**
	 * 静态访问地址（针对上传的图片资源）
	 */
	public static final String STATIC_VISIT_ADDR = PropKit.use("config.properties").get("file.static.addr");
}
