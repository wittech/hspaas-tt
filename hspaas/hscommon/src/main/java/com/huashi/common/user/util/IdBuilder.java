package com.huashi.common.user.util;

import java.util.HashSet;
import java.util.Set;

import com.huashi.common.util.RandomUtil;

/**
 * 
 * TODO ID生成器
 *
 * @author zhengying
 * @version V1.0.0
 * @date 2016年3月9日 下午11:20:05
 */
public class IdBuilder {
	
	private static final String HS_DEVELOPER_PREFIX = "hs";

	/**
	 * 
	 * TODO 用户ID生成器
	 * 
	 * @return
	 */
	public static String userIdBuider() {
		return  System.currentTimeMillis() + RandomUtil.getRandomNum(6);
	}

	/**
	 * TODO 用户接口ID生成器
	 * 
	 * @return
	 */
	public static String developerKeyBuider() {
		return HS_DEVELOPER_PREFIX + RandomUtil.rks(12);
	}
	
	public static void main(String[] args) {
		Set<String> set = new HashSet<>();
		for(int i=0; i< 10;i++) {
			String k = developerKeyBuider();
			set.add(k);
			System.out.println(k);
		}
		
		System.out.println(set.size());
	}
}
