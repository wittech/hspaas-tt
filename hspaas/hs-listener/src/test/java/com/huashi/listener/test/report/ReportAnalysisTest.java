package com.huashi.listener.test.report;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportAnalysisTest {

	public static void main(String[] args) {
		String str1 = "<?xml version='1.0' encoding='utf-8' ?><returnsms><statusbox><mobile>15821917717</mobile>"
				+ "<taskid>1212</taskid><status>10</status><receivetime>2011-12-02 22:12:11</receivetime>"
				+ "<errorcode>MK:0011</errorcode></statusbox><statusbox><mobile>15821917717</mobile>"
				+ "<taskid>1212</taskid><status>20</status><receivetime>2011-12-02 22:12:11</receivetime>"
				+ "<errorcode>DELIVRD</errorcode></statusbox></returnsms>";

		String regex1 = "(<mobile>)(.*?)(</mobile>)(<taskid>)(.*?)(</taskid>)(<status>)(.*?)(</status>)";
		Pattern pattern = Pattern.compile(regex1);
		Matcher matcher = pattern.matcher(str1);
		while (matcher.find()) {
			System.out.println("手机号：" + matcher.group(2));
			System.out.println("任务ID：" + matcher.group(5));
			System.out.println("响应状态：" + matcher.group(8));
			System.out.println("---------------------");
		}
	}
}
