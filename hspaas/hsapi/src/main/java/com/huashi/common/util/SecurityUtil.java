package com.huashi.common.util;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class SecurityUtil {

	public static final int DEFAULT_SALT_SIZE = 16;
	public static final char[] N62_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
			'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z' };

	/**
	 * 生成含有随机盐的密码
	 */
	public static String encode(String password, String salt) {
		return md5Hex(md5Hex(password) + salt);
	}

	public static String random(int count) {
		return RandomStringUtils.random(count, N62_CHARS);
	}

	public static String salt() {
		return RandomStringUtils.random(DEFAULT_SALT_SIZE, N62_CHARS);
	}
	
	/**
	 * 
	   * TODO HASH拼接
	   * @param password
	   * 		密码（输入参数）
	   * @param salt
	   * @return
	 */
	public static String decode(String password, String salt){
		if(StringUtils.isEmpty(salt))
			throw new RuntimeException("解密失败");
		return md5Hex(md5Hex(password) + salt);
	}

	/**
	 * 校验密码是否正确
	 */
	public static boolean verify(String password, String dpass, String salt) {
		return md5Hex(md5Hex(dpass) + salt).equals(password);
	}

	/**
	 * 获取十六进制字符串形式的MD5摘要
	 */
	public static String md5Hex(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bs = md5.digest(src.getBytes());
			return new String(new Hex().encode(bs));
		} catch (Exception e) {
			return null;
		}
	}

	public static void main(String[] args) {
		
		md5Hex("123456");

//		String salt = salt();
//		System.out.println("The salt is " + salt);
//
//		String encodeText = encode("admin", salt);
//		//
//		//
//		System.out.println("The encode text is " + encodeText);
//
//		System.out.println(verify("698746d49eafaec026ac45a4d0602e56", "admin", "VTyOdoOheW44osXA"));
	}
}