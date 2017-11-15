package com.huashi.bill.order.util;

import java.util.Date;

import org.apache.commons.lang3.RandomUtils;

import com.huashi.common.util.DateUtil;
import com.huashi.common.util.RandomUtil;

public class TradeOrderNoBuilder {

	private static Integer seq = 1;

	public static String build() {
		return new StringBuilder().append(System.currentTimeMillis()).append(RandomUtil.getRandomNum()).toString();
	}

	public static String build2() {
		synchronized (seq) {
			String traderNumber = DateUtil.getYYDayStr(new Date());
			int random = RandomUtils.nextInt(1000, 1000);
			traderNumber += getOperSeq(random, 3) + getOperSeq(seq, 2);
			if (seq < 99) {
				seq++;
			} else {
				seq = 1;
			}
			return traderNumber;
		}
	}

	private static String getOperSeq(int oper, int count) {
		String o = String.valueOf(oper);
		int len = o.length();
		String rtn = "";
		for (int i = 0; i < count - len; i++) {
			rtn += "0";
		}
		return rtn + o;
	}

	public static void main(String[] args) {
		System.out.println(TradeOrderNoBuilder.build());
	}
}
