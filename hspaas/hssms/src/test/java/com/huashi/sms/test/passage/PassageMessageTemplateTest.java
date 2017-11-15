package com.huashi.sms.test.passage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassageMessageTemplateTest {

	public static void main(String[] args) {
//		String str3 = "短信验证码#code#,有效时间为#second#分钟，请尽快完成操作";
//		System.out.println(str3);
//		String regex3 = "#(.*)#";
//		Pattern pattern3 = Pattern.compile(regex3);
//		Matcher matcher3 = pattern3.matcher(str3);
//		System.out.println(matcher3.groupCount());
//		while (matcher3.find()) {
//			System.out.println(matcher3.group(1));
////			System.out.println("任务ID：" + matcher3.group(5));
////			System.out.println("响应状态：" + matcher3.group(8));
//			System.out.println("---------------------");
//		}
		
		String str = "验证码:#code#，有效时间：#second#，验证码1：#mycode1#,验证码2：#mycode2#,验证码3：#mycode3#";
        String regex = "#([a-zA-Z0-9]+)#";
        System.out.println(str.matches(regex));
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(str);
        while(m.find()) {
            System.out.println(m.group().replaceAll("#", ""));
        }

//        System.out.println(PatternUtil.isRight("^验证码:(.*)，有效时间：(.*)，验证码1：(.*),验证码2：(.*),验证码3：(.*)$", 
//        		"验证码:112122，有效时间：3，验证码1：222,验证码2：2111,验证码3：222"));
//        
//        System.out.println(SmsPassageMessageTemplateService.pickupValuesByRegex(
//        		"验证码:112122，有效时间：3，验证码1：222,验证码2：2111,验证码3：222",
//        		"^验证码:(.*)，有效时间：(.*)，验证码1：(.*),验证码2：(.*),验证码3：(.*)$",
//        		5));
	}
}
