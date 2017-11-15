package com.huashi.sms.test.limit;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.huashi.sms.settings.service.ISmsMobileTablesService;

public class ApiSameMobileLimitTest {

	// 手机号码最后发送毫秒时间
	private static final String MOBILE_LAST_SEND_MILLIS = "last_send_millis";
	// 手机号码发送总数
	private static final String MOBILE_SEND_TOTAL_COUNT = "send_total_count";

	public static void main(String[] args) {

		int maxSpeed = 30;
		int maxLimit = 10;

		Integer _sendTotalCount = null;
		try {
			Map<Object, Object> map = new HashMap<>();
			map.put(MOBILE_LAST_SEND_MILLIS, 1501422535092l + "");
			map.put(MOBILE_SEND_TOTAL_COUNT, 0 + "");

			if (MapUtils.isEmpty(map)) {
				System.out.println(ISmsMobileTablesService.NICE_PASSED);
				return;
			}

			// 当前发送的总量
			Object sendTotalCount = map.get(MOBILE_SEND_TOTAL_COUNT);
			_sendTotalCount = Integer.parseInt(sendTotalCount.toString()) + 1;

			// 如果上限次数为0，则不允许提交任何
			if (maxLimit == 0) {
				System.out.println("MOBILE_BEYOND_TIMES" + ISmsMobileTablesService.MOBILE_BEYOND_TIMES);
				return;
			}

			// 判断手机号码一天内发送总量是否已超过预设值
			if (_sendTotalCount >= maxLimit) {
				System.out.println("MOBILE_BEYOND_TIMES" + ISmsMobileTablesService.MOBILE_BEYOND_TIMES);
				return;
			}

			// 如果提交频率为0，则不限制提交任何
			if (maxSpeed == 0) {
				System.out.println("NICE_PASSED" + ISmsMobileTablesService.NICE_PASSED);
				return;
			}

			// 判断 手机号码上次发送时间距离当前时间否已超过预设值
			Object lastSendMillis = map.get(MOBILE_LAST_SEND_MILLIS);
			if (System.currentTimeMillis() - Long.parseLong(lastSendMillis.toString()) < maxSpeed * 1000) {
				System.out.println("MOBILE_BEYOND_SPEED" + ISmsMobileTablesService.MOBILE_BEYOND_SPEED);
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("NICE_PASSED" + ISmsMobileTablesService.NICE_PASSED);
	}

}
