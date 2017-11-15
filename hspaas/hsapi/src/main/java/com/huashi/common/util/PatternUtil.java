package com.huashi.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class PatternUtil {
	
	// 短信签名内容表达式，通过扫描方式检索
	public static final String MESSAGE_CONTENT_SIGNATURE_REG = "(【)(.*?)(】)";
	
	/**
	 * 验证字符串是否是email
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmail(String str) {
		Pattern pattern = Pattern.compile("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否为固定电话号码
	 *
	 * @param str	固定电话号码
	 * @return
	 */
	public static boolean isFixedPhone(String str) {
		Pattern pattern = Pattern.compile("[0]{1}[0-9]{2,3}-[0-9]{7,8}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证护照
	 * @param str	护照号
	 * @return
	 */
	public static boolean isPassport(String str) {
		Pattern pattern = Pattern.compile("^1[45][0-9]{7}|G[0-9]{8}|P[0-9]{7}|S[0-9]{7,8}|D[0-9]+$");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证时间
	 * @param str
	 * @return
	 */
	public static boolean isDate(String str) {
		Pattern pattern = Pattern.compile("([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8])))");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证邮编
	 * @param str
	 * @return
	 */
	public static boolean isZipCode(String str) {
		Pattern pattern = Pattern.compile("[1-9]\\d{5}(?!\\d)");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 验证是否是QQ号码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isQQ(String str) {
		Pattern pattern = Pattern.compile("[1-9]{5,10}");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 验证是否是QQ号码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isUrl(String str) {
		Pattern pattern = Pattern.compile("^(http://|https://)?((?:[A-Za-z0-9]+-[A-Za-z0-9]+|[A-Za-z0-9]+)\\.)+([A-Za-z]+)[/\\?\\:]?.*$");
		Matcher matcher = pattern.matcher(str);
		if (matcher.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断字符串是否是数字
	 */
	public static boolean isNumber(String value) {
		return isInteger(value) || isDouble(value);
	}

	/**
	 * 判断字符串是否是整数
	 */
	public static boolean isInteger(String value) {
		try {
			Integer.parseInt(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * 判断字符串是否是浮点数
	 */
	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
			if (value.contains("."))
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	/**
	 * 
	   * TODO 判断数据是否符合表达式规则
	   * 
	   * @param reg
	   * @param value
	   * @return
	 */
	public static boolean isRight(String reg, String value) {
		try {
			if(StringUtils.isEmpty(value) || StringUtils.isEmpty(reg))
				return false;
			
			Pattern pattern = Pattern.compile(reg);
			Matcher matcher = pattern.matcher(value);
			if (matcher.matches()) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * 
	   * TODO 短信内容是否包含签名
	   * @param content
	   * @return
	 */
	public static boolean isContainsSignature(String content) {
		Pattern pattern = Pattern.compile(MESSAGE_CONTENT_SIGNATURE_REG);
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			if(StringUtils.isNotEmpty(matcher.group(2)))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	   * TODO 短信签名是否包含多个签名
	   * @param content
	   * @return
	 */
	public static boolean isMultiSignatures(String content) {
		Pattern pattern = Pattern.compile(MESSAGE_CONTENT_SIGNATURE_REG);
		Matcher matcher = pattern.matcher(content);
		
		int count = 0;
		if (matcher.find()) {
			count++;
		}
		return count > 1;
	}
	
	/**
	 * 
	   * TODO 报备签名和本次签名内容是否符合
	   * @param content
	   * @param signature
	   * @return
	 */
	public static boolean isSignatureMatched(String content, String signature) {
		Pattern pattern = Pattern.compile(MESSAGE_CONTENT_SIGNATURE_REG);
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			if(StringUtils.isNotEmpty(matcher.group(2)) && matcher.group(2).equals(signature))
				return true;
		}
		
		return false;
	}
	
	/**
	 * 
	   * TODO 签名是否有效
	   * @param content
	   * @param signature
	   * @return
	 */
	public static boolean isSignatureAvaiable(String content, String signature) {
		if(!isContainsSignature(content))
			throw new RuntimeException("短信内容不包含签名内容");
		
		if(isMultiSignatures(content))
			throw new RuntimeException("短信内容包含多个签名内容");
		
		if(!isSignatureMatched(content, signature))
			throw new RuntimeException("短信报备签名和本次签名内容不一致");
		
		return true;
	}
	
	
	public static void main(String[] args) {
//		String str1 = "我们】你好";
//		String regex1 = "(【)(.*?)(】)";
//		Pattern pattern = Pattern.compile(regex1);
//		Matcher matcher = pattern.matcher(str1);
//		
//		
//		
//		System.out.println(matcher.groupCount());
//		
//		while (matcher.find()) {
//			System.out.println("手机号：" + matcher.group(2));
//			System.out.println("---------------------");
//		}
		System.out.println(isUrl("http://s"));
	}
	
}