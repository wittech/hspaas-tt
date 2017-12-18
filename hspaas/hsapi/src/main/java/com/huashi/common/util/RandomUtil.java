package com.huashi.common.util;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author tenx
 */
public class RandomUtil {

	private static final int DEFAULT_NUM = 6;

	/**
	 * 
	 * TODO 生成纯数字的随机数
	 * 
	 * @param num
	 * @return
	 */
	public static String getRandomNum(int num) {
		String[] DIGITS = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "0" };
		for (int i = 0; i < DIGITS.length; i++) {
			int index = Math.abs(ThreadLocalRandom.current().nextInt(10000)) % 10;
			String tmpDigit = DIGITS[index];
			DIGITS[index] = DIGITS[i];
			DIGITS[i] = tmpDigit;
		}

		String returnStr = DIGITS[0];
		for (int i = 1; i < num; i++) {
			returnStr = DIGITS[i] + returnStr;
		}
		return returnStr;
	}

	public static String getRandomNum() {
		return getRandomNum(DEFAULT_NUM);
	}

	private static final String CONTENT = "23456789AaBbCcDdEeFfGgHhJjKkLMmNnPpQqRrSsTtUuVvWwXxYyZz";

	/**
	 * 
	 * TODO 生成数字和字符混合的随机数
	 * 
	 * @param size
	 * @param sources
	 * @return
	 */
	public static String randomMix(int size, String sources) {
		if (sources == null || sources.length() == 0) {
			sources = CONTENT;
		}
		int codesLen = sources.length();
		Random rand = new Random(System.currentTimeMillis());
		StringBuilder verifyCode = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			verifyCode.append(sources.charAt(rand.nextInt(codesLen - 1)));
		}
		return verifyCode.toString();
	}

	public static String randomMix(String sources) {
		return randomMix(DEFAULT_NUM, sources);
	}

	public static String randomMix() {
		return randomMix(DEFAULT_NUM, CONTENT);
	}

	public static String randomMix(int size) {
		return randomMix(size, CONTENT);
	}

	final static char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
			'9', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
			'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y',
			'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L',
			'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y',
			'Z' };

	/**
	 * 随机ID生成器，由数字、小写字母和大写字母组成
	 * 
	 * @param size
	 * @return
	 */
	public static String rks(int size) {
		Random random = new Random();
		char[] cs = new char[size];
		for (int i = 0; i < cs.length; i++) {
			cs[i] = DIGITS[random.nextInt(DIGITS.length)];
		}
		return new String(cs);
	}

	public static void main(String[] args) {
		int index = 100;
		for (int i = 0; i < index; i++) {
			System.out.println(RandomUtil.rks(12));
		}

	}
}
