package com.huashi.developer.prervice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FluxPrervice {

	Logger logger = LoggerFactory.getLogger(getClass());

	public String sendMessage() {

		
		
		
		
		// 3.1.2、调用失败
		// a.同步插入调用错误日志至库表
		// b.返回错误码信息
		// 3.1.3、调用成功
		// 以下逻辑需要做到原子性，均为同步执行；
		// a.插入数据库用户发送记录信息（用户ID,手机号码，短信内容，提交时间，计费条数等信息）；
		// b.修改用户的短信余额表信息[这里注意需要根据客户资料中的计费字数来算]
		// c.生成待处理队列信息（详见4.2.1）
		// d.返回调用成功码信息给调用者

		return null;
	}

	public void saveErrorLog(String errorCode, String url, String ip, String message) {
		logger.warn("错误码：{}, 调用URL：{}, 调用IP：{}, 消息为：{}", errorCode, url, ip, message);

	}

}
