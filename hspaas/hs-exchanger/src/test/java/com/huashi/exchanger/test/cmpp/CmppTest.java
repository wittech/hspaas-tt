package com.huashi.exchanger.test.cmpp;

public class CmppTest {
	
	//短信收发类
	static SMProxySend myproxy=null;
	public static void main(String[] args) {
		
		if(myproxy==null){
			myproxy=new SMProxySend();
		}
		myproxy.ProBaseConf();
		System.out.println("启动短信服务");
		myproxy.SendMessage("18368031231", "【华时科技】您的验证码是040938。非本人发送请忽略该信息。退订回T");
		
		
		myproxy.Close();
		System.out.println("关闭短信服务");
	}
	
	
	
}
