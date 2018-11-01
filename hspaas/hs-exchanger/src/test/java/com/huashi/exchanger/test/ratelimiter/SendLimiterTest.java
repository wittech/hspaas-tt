package com.huashi.exchanger.test.ratelimiter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.RateLimiter;
import com.huashi.exchanger.domain.ProviderSendResponse;
import com.huashi.exchanger.service.SmsProviderService;

public class SendLimiterTest {

	public static void main(String[] args) {
//		RateLimiter limiter = RateLimiter.create(2);
//		
//		for(int i=0; i < 10 ; i++) {
//			Thread t = new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					for(int j=0; j < 10;j++) {
//						limiter.acquire(3);
//						System.out.println(Thread.currentThread().getName() + "-j" + ": " + j + "date:" + new Date());
//					}
//					
//				}
//			}, "t"+i);
//			
//			t.start();
//		}
		

		int packetsSize = 60;
		RateLimiter rateLimiter = RateLimiter.create(packetsSize);
		
		int amount = 3;
		String[] mobiles = new String[125];
		for(int i=0; i< 125; i++) {
			mobiles[i] = "m" + i;
		}
		System.out.println(JSON.toJSONString(mobiles));
		System.out.println("-------------------");
		
		
		
		// 目前HTTP用途并不是很大（因为取决于HTTP自身的瓶颈）
		List<String[]> packets = SmsProviderService.recombineMobilesByLimitSpeedInSecond(mobiles, amount, packetsSize);
		for(String[] str : packets) {
			System.out.println("0:"+ str[0] + "mobiles:" + str[1]);
		}

		// 如果手机号码同时传递多个，需要对流速进行控制，多余流速的需要分批提交
		List<ProviderSendResponse> list = new ArrayList<ProviderSendResponse>(packets.size());
		for(String[] m : packets) {
			// 设置acquire 当前分包后的数量
			rateLimiter.acquire(Integer.parseInt(m[0]));
			List<ProviderSendResponse> tlist = submitData2Gateway(m[1]);
			if(CollectionUtils.isEmpty(tlist))
				continue;
			
			System.out.println(new Date() + "------------" +JSON.toJSONString(tlist));
			System.out.println("-------------------");
			list.addAll(tlist);
		}
		
		System.out.println("-------------------###############");
		System.out.println(JSON.toJSONString(list));
	}
	
	private static List<ProviderSendResponse> submitData2Gateway(String mobile) {
		List<ProviderSendResponse> list = new ArrayList<ProviderSendResponse>();
		
		ProviderSendResponse response = new ProviderSendResponse();
		response.setMobile(mobile);
		response.setStatusCode("ok");
		response.setSuccess(true);
		
		list.add(response);
		return list;
		
	}
}
