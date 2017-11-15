<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-代码示例</title>
<#include "/common/assets.ftl"/>
<link href="${rc.contextPath}/assets/css/api.css" rel="stylesheet" type="text/css" />
<link href="${rc.contextPath}/assets/css/api_content.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<#include "/common/header.ftl"/>
	<div class="syzn_box">
		<div class="inner clearfix">
			<div id="user" class="main">
				<nav class="apileft">
				<#include "/common/api_menu.ftl"/>    
				</nav>
				<div class="content">
			        <div class="doc">
			            <p>支持开源精神，欢迎贡献代码示例，审核通过可获短信优惠或奖励！
			                <a href="/api2.0/contributecode.html" class="api20link" style="cursor: pointer;">我要贡献</a></p>
			            <p>
			                已提交的第三方源码（注：所有第三方源码均为开发者贡献，仅供参考使用）：
			                <a href="https://github.com/yunpian/sms/tree/master/thirdparty" target="_blank">Github</a>
			            </p>
			        </div>
			        <div class="tab-content doc">
			            <div class="divTab tab-content1" style="display: block;">
			                <h2 class="page-title">代码示例&nbsp;<span>Java</span></h2>
			                <p>本样例基于 <span class="black">Apache HttpClient v4.3</span> ，
			                    <a href="/download/api2.0_demo-java-httpclient4.zip" target="_top">可下载完整样例项目</a>。
			                    同时，也可 <a href="/download/api2.0_demo-java-httpclient3.zip" target="_top" "="">下载基于3.1版的完整样例项目</a>。
			                    样例项目包含代码、依赖 <span class="black">jar包和pom.xml配置</span>。 </p>
			                <p>
			                    Windows用户请注意：下载的代码及说明文件都是UTF-8格式编码，且换行风格为Linux的。
			                    用记事本或写字板直接打开显示会不正常。建议将其导入（或复制）到Eclipse等IDE，
			                    并将项目编码格式设置为UTF-8后再打开这些文件查看。若直接查看请用Notepad++、EditPlus等编辑器。
			                </p>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_java" data-clipboard-target="java_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="java hljs" id="java_text">
			<span class="hljs-comment">/**
			* Created by bingone on 15/12/16.
			*/</span>
			
			<span class="hljs-keyword">import</span> org.apache.http.HttpEntity;
			<span class="hljs-keyword">import</span> org.apache.http.NameValuePair;
			<span class="hljs-keyword">import</span> org.apache.http.client.entity.UrlEncodedFormEntity;
			<span class="hljs-keyword">import</span> org.apache.http.client.methods.CloseableHttpResponse;
			<span class="hljs-keyword">import</span> org.apache.http.client.methods.HttpPost;
			<span class="hljs-keyword">import</span> org.apache.http.impl.client.CloseableHttpClient;
			<span class="hljs-keyword">import</span> org.apache.http.impl.client.HttpClients;
			<span class="hljs-keyword">import</span> org.apache.http.message.BasicNameValuePair;
			<span class="hljs-keyword">import</span> org.apache.http.util.EntityUtils;
			<span class="hljs-keyword">import</span> java.io.IOException;
			<span class="hljs-keyword">import</span> java.net.URISyntaxException;
			<span class="hljs-keyword">import</span> java.net.URLEncoder;
			<span class="hljs-keyword">import</span> java.util.ArrayList;
			<span class="hljs-keyword">import</span> java.util.HashMap;
			<span class="hljs-keyword">import</span> java.util.List;
			<span class="hljs-keyword">import</span> java.util.Map;
			
			<span class="hljs-comment">/**
			* 短信http接口的java代码调用示例
			* 基于Apache HttpClient 4.3
			*
			* <span class="hljs-doctag">@author</span> songchao
			* <span class="hljs-doctag">@since</span> 2015-04-03
			*/</span>
			
			<span class="hljs-keyword">public</span> <span class="hljs-class"><span class="hljs-keyword">class</span> <span class="hljs-title">JavaSmsApi</span> </span>{
			
			    <span class="hljs-comment">//查账户信息的http地址</span>
			    <span class="hljs-keyword">private</span> <span class="hljs-keyword">static</span> String URI_GET_USER_INFO = <span class="hljs-string">"https://sms.yunpian.com/v2/user/get.json"</span>;
			
			    <span class="hljs-comment">//智能匹配模板发送接口的http地址</span>
			    <span class="hljs-keyword">private</span> <span class="hljs-keyword">static</span> String URI_SEND_SMS = <span class="hljs-string">"https://sms.yunpian.com/v2/sms/single_send.json"</span>;
			
			    <span class="hljs-comment">//模板发送接口的http地址</span>
			    <span class="hljs-keyword">private</span> <span class="hljs-keyword">static</span> String URI_TPL_SEND_SMS = <span class="hljs-string">"https://sms.yunpian.com/v2/sms/tpl_single_send.json"</span>;
			
			    <span class="hljs-comment">//发送语音验证码接口的http地址</span>
			    <span class="hljs-keyword">private</span> <span class="hljs-keyword">static</span> String URI_SEND_VOICE = <span class="hljs-string">"https://voice.yunpian.com/v2/voice/send.json"</span>;
			
			    <span class="hljs-comment">//绑定主叫、被叫关系的接口http地址</span>
			    <span class="hljs-keyword">private</span> <span class="hljs-keyword">static</span> String URI_SEND_BIND = <span class="hljs-string">"https://call.yunpian.com/v2/call/bind.json"</span>;
			
			    <span class="hljs-comment">//解绑主叫、被叫关系的接口http地址</span>
			    <span class="hljs-keyword">private</span> <span class="hljs-keyword">static</span> String URI_SEND_UNBIND = <span class="hljs-string">"https://call.yunpian.com/v2/call/unbind.json"</span>;
			
			    <span class="hljs-comment">//编码格式。发送编码格式统一用UTF-8</span>
			    <span class="hljs-keyword">private</span> <span class="hljs-keyword">static</span> String ENCODING = <span class="hljs-string">"UTF-8"</span>;
			
			    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-keyword">void</span> <span class="hljs-title">main</span><span class="hljs-params">(String[] args)</span> <span class="hljs-keyword">throws</span> IOException, URISyntaxException </span>{
			
			        <span class="hljs-comment">//修改为您的apikey.apikey可在官网（http://www.yuanpian.com)登录后获取</span>
			        String apikey = <span class="hljs-string">"xxxxxxxxxxxxxxxxxxxxx"</span>;
			
			        <span class="hljs-comment">//修改为您要发送的手机号</span>
			        String mobile = <span class="hljs-string">"130xxxxxxxx"</span>;
			
			        <span class="hljs-comment">/**************** 查账户信息调用示例 *****************/</span>
			        System.out.println(JavaSmsApi.getUserInfo(apikey));
			
			        <span class="hljs-comment">/**************** 使用智能匹配模板接口发短信(推荐) *****************/</span>
			        <span class="hljs-comment">//设置您要发送的内容(内容必须和某个模板匹配。以下例子匹配的是系统提供的1号模板）</span>
			        String text = <span class="hljs-string">"【云片网】您的验证码是1234"</span>;
			        <span class="hljs-comment">//发短信调用示例</span>
			        <span class="hljs-comment">// System.out.println(JavaSmsApi.sendSms(apikey, text, mobile));</span>
			
			        <span class="hljs-comment">/**************** 使用指定模板接口发短信(不推荐，建议使用智能匹配模板接口) *****************/</span>
			        <span class="hljs-comment">//设置模板ID，如使用1号模板:【#company#】您的验证码是#code#</span>
			        <span class="hljs-keyword">long</span> tpl_id = <span class="hljs-number">1</span>;
			        <span class="hljs-comment">//设置对应的模板变量值</span>
			
			        String tpl_value = URLEncoder.encode(<span class="hljs-string">"#code#"</span>,ENCODING) +<span class="hljs-string">"="</span>
			        + URLEncoder.encode(<span class="hljs-string">"1234"</span>, ENCODING) + <span class="hljs-string">"&amp;"</span>
			        + URLEncoder.encode(<span class="hljs-string">"#company#"</span>,ENCODING) + <span class="hljs-string">"="</span>
			        + URLEncoder.encode(<span class="hljs-string">"云片网"</span>,ENCODING);
			        <span class="hljs-comment">//模板发送的调用示例</span>
			        System.out.println(tpl_value);
			        System.out.println(JavaSmsApi.tplSendSms(apikey, tpl_id, tpl_value, mobile));
			
			        <span class="hljs-comment">/**************** 使用接口发语音验证码 *****************/</span>
			        String code = <span class="hljs-string">"1234"</span>;
			        <span class="hljs-comment">//System.out.println(JavaSmsApi.sendVoice(apikey, mobile ,code));</span>
			
			        <span class="hljs-comment">/**************** 使用接口绑定主被叫号码 *****************/</span>
			        String from = <span class="hljs-string">"+86130xxxxxxxx"</span>;
			        String to = <span class="hljs-string">"+86131xxxxxxxx"</span>;
			        Integer duration = <span class="hljs-number">30</span>*<span class="hljs-number">60</span>;<span class="hljs-comment">// 绑定30分钟</span>
			        <span class="hljs-comment">//        System.out.println(JavaSmsApi.bindCall(apikey, from ,to , duration));</span>
			
			        <span class="hljs-comment">/**************** 使用接口解绑主被叫号码 *****************/</span>
			        <span class="hljs-comment">//        System.out.println(JavaSmsApi.unbindCall(apikey, from, to));</span>
			    }
			
			    <span class="hljs-comment">/**
			    * 取账户信息
			    *
			    * <span class="hljs-doctag">@return</span> json格式字符串
			    * <span class="hljs-doctag">@throws</span> java.io.IOException
			    */</span>
			
			    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> String <span class="hljs-title">getUserInfo</span><span class="hljs-params">(String apikey)</span> <span class="hljs-keyword">throws</span> IOException, URISyntaxException </span>{
			        Map&lt;String, String&gt; params = <span class="hljs-keyword">new</span> HashMap&lt;String, String&gt;();
			        params.put(<span class="hljs-string">"apikey"</span>, apikey);
			        <span class="hljs-keyword">return</span> post(URI_GET_USER_INFO, params);
			    }
			
			    <span class="hljs-comment">/**
			    * 智能匹配模板接口发短信
			    *
			    * <span class="hljs-doctag">@param</span> apikey apikey
			    * <span class="hljs-doctag">@param</span> text   　短信内容
			    * <span class="hljs-doctag">@param</span> mobile 　接受的手机号
			    * <span class="hljs-doctag">@return</span> json格式字符串
			    * <span class="hljs-doctag">@throws</span> IOException
			    */</span>
			
			    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> String <span class="hljs-title">sendSms</span><span class="hljs-params">(String apikey, String text, String mobile)</span> <span class="hljs-keyword">throws</span> IOException </span>{
			        Map&lt;String, String&gt; params = <span class="hljs-keyword">new</span> HashMap&lt;String, String&gt;();
			        params.put(<span class="hljs-string">"apikey"</span>, apikey);
			        params.put(<span class="hljs-string">"text"</span>, text);
			        params.put(<span class="hljs-string">"mobile"</span>, mobile);
			        <span class="hljs-keyword">return</span> post(URI_SEND_SMS, params);
			    }
			
			    <span class="hljs-comment">/**
			    * 通过模板发送短信(不推荐)
			    *
			    * <span class="hljs-doctag">@param</span> apikey    apikey
			    * <span class="hljs-doctag">@param</span> tpl_id    　模板id
			    * <span class="hljs-doctag">@param</span> tpl_value 　模板变量值
			    * <span class="hljs-doctag">@param</span> mobile    　接受的手机号
			    * <span class="hljs-doctag">@return</span> json格式字符串
			    * <span class="hljs-doctag">@throws</span> IOException
			    */</span>
			
			    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> String <span class="hljs-title">tplSendSms</span><span class="hljs-params">(String apikey, <span class="hljs-keyword">long</span> tpl_id, String tpl_value, String mobile)</span> <span class="hljs-keyword">throws</span> IOException </span>{
			        Map&lt;String, String&gt; params = <span class="hljs-keyword">new</span> HashMap&lt;String, String&gt;();
			        params.put(<span class="hljs-string">"apikey"</span>, apikey);
			        params.put(<span class="hljs-string">"tpl_id"</span>, String.valueOf(tpl_id));
			        params.put(<span class="hljs-string">"tpl_value"</span>, tpl_value);
			        params.put(<span class="hljs-string">"mobile"</span>, mobile);
			        <span class="hljs-keyword">return</span> post(URI_TPL_SEND_SMS, params);
			    }
			
			    <span class="hljs-comment">/**
			    * 通过接口发送语音验证码
			    * <span class="hljs-doctag">@param</span> apikey apikey
			    * <span class="hljs-doctag">@param</span> mobile 接收的手机号
			    * <span class="hljs-doctag">@param</span> code   验证码
			    * <span class="hljs-doctag">@return</span>
			    */</span>
			
			    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> String <span class="hljs-title">sendVoice</span><span class="hljs-params">(String apikey, String mobile, String code)</span> </span>{
			        Map&lt;String, String&gt; params = <span class="hljs-keyword">new</span> HashMap&lt;String, String&gt;();
			        params.put(<span class="hljs-string">"apikey"</span>, apikey);
			        params.put(<span class="hljs-string">"mobile"</span>, mobile);
			        params.put(<span class="hljs-string">"code"</span>, code);
			        <span class="hljs-keyword">return</span> post(URI_SEND_VOICE, params);
			    }
			
			    <span class="hljs-comment">/**
			    * 通过接口绑定主被叫号码
			    * <span class="hljs-doctag">@param</span> apikey apikey
			    * <span class="hljs-doctag">@param</span> from 主叫
			    * <span class="hljs-doctag">@param</span> to   被叫
			    * <span class="hljs-doctag">@param</span> duration 有效时长，单位：秒
			    * <span class="hljs-doctag">@return</span>
			    */</span>
			
			    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> String <span class="hljs-title">bindCall</span><span class="hljs-params">(String apikey, String from, String to , Integer duration )</span> </span>{
			        Map&lt;String, String&gt; params = <span class="hljs-keyword">new</span> HashMap&lt;String, String&gt;();
			        params.put(<span class="hljs-string">"apikey"</span>, apikey);
			        params.put(<span class="hljs-string">"from"</span>, from);
			        params.put(<span class="hljs-string">"to"</span>, to);
			        params.put(<span class="hljs-string">"duration"</span>, String.valueOf(duration));
			        <span class="hljs-keyword">return</span> post(URI_SEND_BIND, params);
			    }
			
			    <span class="hljs-comment">/**
			    * 通过接口解绑绑定主被叫号码
			    * <span class="hljs-doctag">@param</span> apikey apikey
			    * <span class="hljs-doctag">@param</span> from 主叫
			    * <span class="hljs-doctag">@param</span> to   被叫
			    * <span class="hljs-doctag">@return</span>
			    */</span>
			    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> String <span class="hljs-title">unbindCall</span><span class="hljs-params">(String apikey, String from, String to)</span> </span>{
			        Map&lt;String, String&gt; params = <span class="hljs-keyword">new</span> HashMap&lt;String, String&gt;();
			        params.put(<span class="hljs-string">"apikey"</span>, apikey);
			        params.put(<span class="hljs-string">"from"</span>, from);
			        params.put(<span class="hljs-string">"to"</span>, to);
			        <span class="hljs-keyword">return</span> post(URI_SEND_UNBIND, params);
			    }
			
			    <span class="hljs-comment">/**
			    * 基于HttpClient 4.3的通用POST方法
			    *
			    * <span class="hljs-doctag">@param</span> url       提交的URL
			    * <span class="hljs-doctag">@param</span> paramsMap 提交&lt;参数，值&gt;Map
			    * <span class="hljs-doctag">@return</span> 提交响应
			    */</span>
			
			    <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> String <span class="hljs-title">post</span><span class="hljs-params">(String url, Map&lt;String, String&gt; paramsMap)</span> </span>{
			        CloseableHttpClient client = HttpClients.createDefault();
			        String responseText = <span class="hljs-string">""</span>;
			        CloseableHttpResponse response = <span class="hljs-keyword">null</span>;
			            <span class="hljs-keyword">try</span> {
			                HttpPost method = <span class="hljs-keyword">new</span> HttpPost(url);
			                <span class="hljs-keyword">if</span> (paramsMap != <span class="hljs-keyword">null</span>) {
			                    List&lt;NameValuePair&gt; paramList = <span class="hljs-keyword">new</span> ArrayList&lt;NameValuePair&gt;();
			                    <span class="hljs-keyword">for</span> (Map.Entry&lt;String, String&gt; param : paramsMap.entrySet()) {
			                        NameValuePair pair = <span class="hljs-keyword">new</span> BasicNameValuePair(param.getKey(), param.getValue());
			                        paramList.add(pair);
			                    }
			                    method.setEntity(<span class="hljs-keyword">new</span> UrlEncodedFormEntity(paramList, ENCODING));
			                }
			                response = client.execute(method);
			                HttpEntity entity = response.getEntity();
			                <span class="hljs-keyword">if</span> (entity != <span class="hljs-keyword">null</span>) {
			                    responseText = EntityUtils.toString(entity);
			                }
			            } <span class="hljs-keyword">catch</span> (Exception e) {
			                e.printStackTrace();
			            } <span class="hljs-keyword">finally</span> {
			                <span class="hljs-keyword">try</span> {
			                    response.close();
			                } <span class="hljs-keyword">catch</span> (Exception e) {
			                    e.printStackTrace();
			                }
			            }
			            <span class="hljs-keyword">return</span> responseText;
			        }
			}
			</code></pre>
			                <p>
			                    <button class="fuzhidaima fuzhi_java" data-clipboard-target="java_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content2" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>python</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_python" data-clipboard-target="python_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="python hljs" id="python_text"><span class="hljs-comment">#!/usr/local/bin/python</span>
			<span class="hljs-comment">#-*-coding:utf-8-*-</span>
			
			<span class="hljs-comment">#author: jacky</span>
			<span class="hljs-comment"># Time: 15-12-15</span>
			<span class="hljs-comment"># Desc: 短信http接口的python代码调用示例</span>
			<span class="hljs-comment"># https://www.yunpian.com/api/demo.html</span>
			<span class="hljs-comment"># https访问，需要安装  openssl-devel库。apt-get install openssl-devel</span>
			
			<span class="hljs-keyword">import</span> httplib
			<span class="hljs-keyword">import</span> urllib
			<span class="hljs-keyword">import</span> json
			<span class="hljs-comment">#服务地址</span>
			sms_host = <span class="hljs-string">"sms.yunpian.com"</span>
			voice_host = <span class="hljs-string">"voice.yunpian.com"</span>
			<span class="hljs-comment">#端口号</span>
			port = <span class="hljs-number">443</span>
			<span class="hljs-comment">#版本号</span>
			version = <span class="hljs-string">"v2"</span>
			<span class="hljs-comment">#查账户信息的URI</span>
			user_get_uri = <span class="hljs-string">"/"</span> + version + <span class="hljs-string">"/user/get.json"</span>
			<span class="hljs-comment">#智能匹配模板短信接口的URI</span>
			sms_send_uri = <span class="hljs-string">"/"</span> + version + <span class="hljs-string">"/sms/single_send.json"</span>
			<span class="hljs-comment">#模板短信接口的URI</span>
			sms_tpl_send_uri = <span class="hljs-string">"/"</span> + version + <span class="hljs-string">"/sms/tpl_single_send.json"</span>
			<span class="hljs-comment">#语音短信接口的URI</span>
			sms_voice_send_uri = <span class="hljs-string">"/"</span> + version + <span class="hljs-string">"/voice/send.json"</span>
			<span class="hljs-comment">#语音验证码</span>
			voiceCode = <span class="hljs-number">1234</span>
			<span class="hljs-function"><span class="hljs-keyword">def</span> <span class="hljs-title">get_user_info</span><span class="hljs-params">(apikey)</span>:</span>
			    <span class="hljs-string">"""
			    取账户信息
			    """</span>
			    conn = httplib.HTTPSConnection(sms_host , port=port)
			    headers = {<span class="hljs-string">"Content-type"</span>: <span class="hljs-string">"application/x-www-form-urlencoded"</span>, <span class="hljs-string">"Accept"</span>: <span class="hljs-string">"text/plain"</span>}
			    conn.request(<span class="hljs-string">'POST'</span>,user_get_uri,urllib.urlencode( {<span class="hljs-string">'apikey'</span> : apikey}))
			    response = conn.getresponse()
			    response_str = response.read()
			    conn.close()
			    <span class="hljs-keyword">return</span> response_str
			
			<span class="hljs-function"><span class="hljs-keyword">def</span> <span class="hljs-title">send_sms</span><span class="hljs-params">(apikey, text, mobile)</span>:</span>
			    <span class="hljs-string">"""
			    通用接口发短信
			    """</span>
			    params = urllib.urlencode({<span class="hljs-string">'apikey'</span>: apikey, <span class="hljs-string">'text'</span>: text, <span class="hljs-string">'mobile'</span>:mobile})
			    headers = {<span class="hljs-string">"Content-type"</span>: <span class="hljs-string">"application/x-www-form-urlencoded"</span>, <span class="hljs-string">"Accept"</span>: <span class="hljs-string">"text/plain"</span>}
			    conn = httplib.HTTPSConnection(sms_host, port=port, timeout=<span class="hljs-number">30</span>)
			    conn.request(<span class="hljs-string">"POST"</span>, sms_send_uri, params, headers)
			    response = conn.getresponse()
			    response_str = response.read()
			    conn.close()
			    <span class="hljs-keyword">return</span> response_str
			
			<span class="hljs-function"><span class="hljs-keyword">def</span> <span class="hljs-title">tpl_send_sms</span><span class="hljs-params">(apikey, tpl_id, tpl_value, mobile)</span>:</span>
			    <span class="hljs-string">"""
			    模板接口发短信
			    """</span>
			    params = urllib.urlencode({<span class="hljs-string">'apikey'</span>: apikey, <span class="hljs-string">'tpl_id'</span>:tpl_id, <span class="hljs-string">'tpl_value'</span>: urllib.urlencode(tpl_value), <span class="hljs-string">'mobile'</span>:mobile})
			    headers = {<span class="hljs-string">"Content-type"</span>: <span class="hljs-string">"application/x-www-form-urlencoded"</span>, <span class="hljs-string">"Accept"</span>: <span class="hljs-string">"text/plain"</span>}
			    conn = httplib.HTTPSConnection(sms_host, port=port, timeout=<span class="hljs-number">30</span>)
			    conn.request(<span class="hljs-string">"POST"</span>, sms_tpl_send_uri, params, headers)
			    response = conn.getresponse()
			    response_str = response.read()
			    conn.close()
			    <span class="hljs-keyword">return</span> response_str
			
			<span class="hljs-function"><span class="hljs-keyword">def</span> <span class="hljs-title">send_voice_sms</span><span class="hljs-params">(apikey, code, mobile)</span>:</span>
			    <span class="hljs-string">"""
			    通用接口发短信
			    """</span>
			    params = urllib.urlencode({<span class="hljs-string">'apikey'</span>: apikey, <span class="hljs-string">'code'</span>: code, <span class="hljs-string">'mobile'</span>:mobile})
			    headers = {<span class="hljs-string">"Content-type"</span>: <span class="hljs-string">"application/x-www-form-urlencoded"</span>, <span class="hljs-string">"Accept"</span>: <span class="hljs-string">"text/plain"</span>}
			    conn = httplib.HTTPSConnection(voice_host, port=port, timeout=<span class="hljs-number">30</span>)
			    conn.request(<span class="hljs-string">"POST"</span>, sms_voice_send_uri, params, headers)
			    response = conn.getresponse()
			    response_str = response.read()
			    conn.close()
			    <span class="hljs-keyword">return</span> response_str
			
			<span class="hljs-keyword">if</span> __name__ == <span class="hljs-string">'__main__'</span>:
			    <span class="hljs-comment">#修改为您的apikey.可在官网（http://www.yuanpian.com)登录后获取</span>
			    apikey = <span class="hljs-string">"xxxxxxxxxxxxxxxx"</span>
			    <span class="hljs-comment">#修改为您要发送的手机号码，多个号码用逗号隔开</span>
			    mobile = <span class="hljs-string">"xxxxxxxxxxxxxxxx"</span>
			    <span class="hljs-comment">#修改为您要发送的短信内容</span>
			    text = <span class="hljs-string">"【云片网】您的验证码是1234"</span>
			    <span class="hljs-comment">#查账户信息</span>
			    print(get_user_info(apikey))
			    <span class="hljs-comment">#调用智能匹配模板接口发短信</span>
			    <span class="hljs-keyword">print</span> send_sms(apikey,text,mobile)
			    <span class="hljs-comment">#调用模板接口发短信</span>
			    tpl_id = <span class="hljs-number">1</span> <span class="hljs-comment">#对应的模板内容为：您的验证码是#code#【#company#】</span>
			    tpl_value = {<span class="hljs-string">'#code#'</span>:<span class="hljs-string">'1234'</span>,<span class="hljs-string">'#company#'</span>:<span class="hljs-string">'云片网'</span>}
			    <span class="hljs-keyword">print</span> tpl_send_sms(apikey, tpl_id, tpl_value, mobile)
			    <span class="hljs-comment">#调用模板接口发语音短信</span>
			    <span class="hljs-keyword">print</span> send_voice_sms(apikey,voiceCode,mobile)</code></pre>
			                <p>
			                    <button class="fuzhidaima fuzhi_python" data-clipboard-target="python_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content3" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>C#</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_csharp" data-clipboard-target="csharp_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="C# hljs cs" id="csharp_text"><span class="hljs-comment">//项目需要添加System.web引用</span>
			<span class="hljs-keyword">using</span> System;
			<span class="hljs-keyword">using</span> System.Collections.Generic;
			<span class="hljs-keyword">using</span> System.IO;
			<span class="hljs-keyword">using</span> System.Linq;
			<span class="hljs-keyword">using</span> System.Net;
			<span class="hljs-keyword">using</span> System.Text;
			<span class="hljs-keyword">using</span> System.Threading.Tasks;
			<span class="hljs-keyword">using</span> System.Web;
			<span class="hljs-keyword">namespace</span> <span class="hljs-title">yunpianSmsClient</span>
			{
			    <span class="hljs-keyword">class</span> <span class="hljs-title">Program</span>
			    {
			        <span class="hljs-function"><span class="hljs-keyword">static</span> <span class="hljs-keyword">void</span> <span class="hljs-title">Main</span>(<span class="hljs-params"><span class="hljs-keyword">string</span>[] args</span>)
			        </span>{
			            <span class="hljs-comment">// 设置为您的apikey(https://www.yunpian.com)可查</span>
			            <span class="hljs-keyword">string</span> apikey = <span class="hljs-string">"xxxxxxxxxxxxxxxxxxxxxxx"</span>;
			            <span class="hljs-comment">// 发送的手机号</span>
			            <span class="hljs-keyword">string</span> mobile = <span class="hljs-string">"xxxxxxxxxxxxxxxxxxxxxxx"</span>;
			            <span class="hljs-comment">// 发送模板编号</span>
			            <span class="hljs-keyword">int</span> tpl_id = <span class="hljs-number">1</span>;
			            <span class="hljs-comment">// 发送模板内容</span>
			            mobile = HttpUtility.UrlEncode(mobile, Encoding.UTF8);
			            <span class="hljs-keyword">string</span> tpl_value = HttpUtility.UrlEncode(
			            HttpUtility.UrlEncode(<span class="hljs-string">"#code#"</span>, Encoding.UTF8)    + <span class="hljs-string">"="</span> +
			            HttpUtility.UrlEncode(<span class="hljs-string">"1234"</span>, Encoding.UTF8)      + <span class="hljs-string">"&amp;"</span> +
			            HttpUtility.UrlEncode(<span class="hljs-string">"#company#"</span>, Encoding.UTF8) + <span class="hljs-string">"="</span> +
			            HttpUtility.UrlEncode(<span class="hljs-string">"云片网"</span>, Encoding.UTF8), Encoding.UTF8);
			            <span class="hljs-comment">// 发送内容</span>
			            <span class="hljs-keyword">string</span> text = <span class="hljs-string">"【云片网】您的验证码是1234"</span>;
			            <span class="hljs-comment">// 获取user信息url</span>
			            <span class="hljs-keyword">string</span> url_get_user     = <span class="hljs-string">"https://sms.yunpian.com/v2/user/get.json"</span>;
			            <span class="hljs-comment">// 智能模板发送短信url</span>
			            <span class="hljs-keyword">string</span> url_send_sms     = <span class="hljs-string">"https://sms.yunpian.com/v2/sms/single_send.json"</span>;
			            <span class="hljs-comment">// 指定模板发送短信url</span>
			            <span class="hljs-keyword">string</span> url_tpl_sms      = <span class="hljs-string">"https://sms.yunpian.com/v2/sms/tpl_single_send.json"</span>;
			            <span class="hljs-comment">// 发送语音短信url</span>
			            <span class="hljs-keyword">string</span> url_send_voice   = <span class="hljs-string">"https://voice.yunpian.com/v2/voice/send.json"</span>;
			
			            <span class="hljs-keyword">string</span> data_get_user    = <span class="hljs-string">"apikey="</span> + apikey;
			            <span class="hljs-keyword">string</span> data_send_sms    = <span class="hljs-string">"apikey="</span> + apikey + <span class="hljs-string">"&amp;mobile="</span> + mobile + <span class="hljs-string">"&amp;text="</span> + text;
			            <span class="hljs-keyword">string</span> data_tpl_sms     = <span class="hljs-string">"apikey="</span> + apikey + <span class="hljs-string">"&amp;mobile="</span> + mobile + <span class="hljs-string">"&amp;tpl_id="</span> + tpl_id.ToString() + <span class="hljs-string">"&amp;tpl_value="</span> + tpl_value;
			            <span class="hljs-keyword">string</span> data_send_voice  = <span class="hljs-string">"apikey="</span> + apikey + <span class="hljs-string">"&amp;mobile="</span> + mobile + <span class="hljs-string">"&amp;code="</span> + <span class="hljs-string">"1234"</span>;
			
			           
			            HttpPost(url_get_user, data_get_user);
			            HttpPost(url_send_sms, data_send_sms);
			            HttpPost(url_tpl_sms, data_tpl_sms);
			            HttpPost(url_send_voice, data_send_voice);
			        }
			        <span class="hljs-function"><span class="hljs-keyword">public</span> <span class="hljs-keyword">static</span> <span class="hljs-keyword">void</span> <span class="hljs-title">HttpPost</span>(<span class="hljs-params"><span class="hljs-keyword">string</span> Url, <span class="hljs-keyword">string</span> postDataStr</span>)
			        </span>{
			            <span class="hljs-keyword">byte</span>[] dataArray = Encoding.UTF8.GetBytes(postDataStr);
			           <span class="hljs-comment">// Console.Write(Encoding.UTF8.GetString(dataArray));</span>
			            
			            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(Url);
			            request.Method = <span class="hljs-string">"POST"</span>;
			            request.ContentType = <span class="hljs-string">"application/x-www-form-urlencoded"</span>;
			            request.ContentLength = dataArray.Length;
			            <span class="hljs-comment">//request.CookieContainer = cookie;</span>
			            Stream dataStream = request.GetRequestStream();
			            dataStream.Write(dataArray, <span class="hljs-number">0</span>, dataArray.Length);
			            dataStream.Close();
			            <span class="hljs-keyword">try</span>
			            {
			                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
			                StreamReader reader = <span class="hljs-keyword">new</span> StreamReader(response.GetResponseStream(), Encoding.UTF8);
			                String res = reader.ReadToEnd();
			                reader.Close();
			                Console.Write(<span class="hljs-string">"\nResponse Content:\n"</span> + res + <span class="hljs-string">"\n"</span>);
			            }
			            <span class="hljs-keyword">catch</span>(Exception e)
			            {
			                Console.Write(e.Message + e.ToString());
			            }
			        }
			    }
			}
			
			</code></pre>
			                <p>
			                    <button class="fuzhidaima fuzhi_csharp" data-clipboard-target="csharp_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content4" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>C/C++</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_cplus" data-clipboard-target="cplus_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="c hljs cpp" id="cplus_text"><span class="hljs-preprocessor">#<span class="hljs-keyword">include</span> <span class="hljs-string">&lt;stdio.h&gt;</span></span>
			<span class="hljs-preprocessor">#<span class="hljs-keyword">include</span> <span class="hljs-string">&lt;curl/curl.h&gt;</span></span>
			<span class="hljs-preprocessor">#<span class="hljs-keyword">include</span> <span class="hljs-string">&lt;stdarg.h&gt;</span></span>
			<span class="hljs-preprocessor">#<span class="hljs-keyword">include</span> <span class="hljs-string">&lt;stdlib.h&gt;</span></span>
			<span class="hljs-preprocessor">#<span class="hljs-keyword">include</span> <span class="hljs-string">&lt;string.h&gt;</span></span>
			<span class="hljs-preprocessor">#<span class="hljs-keyword">define</span> MAXPARAM <span class="hljs-number">2048</span></span>
			
			CURL *curl;
			CURLcode res;
			<span class="hljs-comment">/**
			
			bingone
			
			本样例依赖libcurl库
			下载地址 https://curl.haxx.se/download.html
			
			*/</span>
			<span class="hljs-comment">// 修改为您的apikey(https://www.yunpian.com)登陆官网后获取</span>
			<span class="hljs-keyword">char</span> *apikey = <span class="hljs-string">"xxxxxxxxxxxxxxxx"</span>;
			<span class="hljs-comment">// 修改为您要发送的手机号</span>
			<span class="hljs-keyword">char</span> *mobile = <span class="hljs-string">"xxxxxxxxxxxxxxxx"</span>;
			<span class="hljs-comment">// 设置您要发送的内容</span>
			<span class="hljs-keyword">char</span> *text = <span class="hljs-string">"【云片网】您的验证码是1234"</span>;
			<span class="hljs-comment">// 指定发送的模板id</span>
			<span class="hljs-keyword">int</span> tpl_id = <span class="hljs-number">1</span>;
			<span class="hljs-comment">// 指定发送模板内容</span>
			
			
			<span class="hljs-keyword">char</span> *tpl_data[<span class="hljs-number">4</span>] = {<span class="hljs-string">"#code#"</span>,<span class="hljs-string">"1234"</span>,<span class="hljs-string">"#company#"</span>,<span class="hljs-string">"云片网"</span>};
			
			<span class="hljs-comment">// 发送语音验证码内容</span>
			<span class="hljs-keyword">int</span> code = <span class="hljs-number">1234</span>;
			<span class="hljs-comment">// 获取user信息url</span>
			<span class="hljs-keyword">char</span> *url_get_user     = <span class="hljs-string">"https://sms.yunpian.com/v2/user/get.json"</span>;
			<span class="hljs-comment">// 智能模板发送短信url</span>
			<span class="hljs-keyword">char</span> *url_send_sms     = <span class="hljs-string">"https://sms.yunpian.com/v2/sms/single_send.json"</span>;
			<span class="hljs-comment">// 指定模板发送短信url</span>
			<span class="hljs-keyword">char</span> *url_tpl_sms      = <span class="hljs-string">"https://sms.yunpian.com/v2/sms/tpl_single_send.json"</span>;
			<span class="hljs-comment">// 发送语音短信url</span>
			<span class="hljs-keyword">char</span> *url_send_voice   = <span class="hljs-string">"https://voice.yunpian.com/v2/voice/send.json"</span>;
			
			<span class="hljs-function"><span class="hljs-keyword">void</span> <span class="hljs-title">send_data</span><span class="hljs-params">(<span class="hljs-keyword">char</span> *url,<span class="hljs-keyword">char</span> *data)</span>
			</span>{
			    <span class="hljs-comment">// specify the url</span>
			    curl_easy_setopt(curl, CURLOPT_URL, url);
			    <span class="hljs-comment">// specify the POST data</span>
			    curl_easy_setopt(curl, CURLOPT_POSTFIELDS, data);
			    <span class="hljs-comment">// get response data</span>
			    curl_easy_perform(curl);
			    <span class="hljs-built_in">printf</span>(<span class="hljs-string">"\n\n"</span>);
			}
			<span class="hljs-comment">/**
			* 查账户信息
			*/</span>
			<span class="hljs-function"><span class="hljs-keyword">void</span> <span class="hljs-title">get_user</span><span class="hljs-params">(<span class="hljs-keyword">char</span> *apikey)</span>
			</span>{
			    <span class="hljs-keyword">char</span> params[MAXPARAM + <span class="hljs-number">1</span>];
			    <span class="hljs-keyword">char</span> *cp = params;
			    <span class="hljs-built_in">sprintf</span>(params,<span class="hljs-string">"apikey=%s"</span>, apikey);
			    send_data(url_get_user, params);
			}
			
			<span class="hljs-comment">/**
			* 使用智能匹配模板接口发短信
			*/</span>
			<span class="hljs-function"><span class="hljs-keyword">void</span> <span class="hljs-title">send_sms</span><span class="hljs-params">(<span class="hljs-keyword">char</span> *apikey, <span class="hljs-keyword">char</span> *mobile, <span class="hljs-keyword">char</span> *text)</span>
			</span>{
			    <span class="hljs-keyword">char</span> params[MAXPARAM + <span class="hljs-number">1</span>];
			    <span class="hljs-keyword">char</span> *cp = params;
			    <span class="hljs-built_in">sprintf</span>(params,<span class="hljs-string">"apikey=%s&amp;mobile=%s&amp;text=%s"</span>, apikey, mobile, text);
			    send_data(url_send_sms, params);
			}
			<span class="hljs-comment">/**
			* 使用智能匹配模板接口发短信
			*/</span>
			<span class="hljs-function"><span class="hljs-keyword">void</span> <span class="hljs-title">send_tpl_sms</span><span class="hljs-params">(<span class="hljs-keyword">char</span> *apikey, <span class="hljs-keyword">char</span> *mobile, <span class="hljs-keyword">int</span> tpl_id, <span class="hljs-keyword">char</span> *tpl_value)</span>
			</span>{
			    <span class="hljs-keyword">char</span> params[MAXPARAM + <span class="hljs-number">1</span>];
			    <span class="hljs-keyword">char</span> *cp = params;
			    <span class="hljs-built_in">sprintf</span>(params,<span class="hljs-string">"apikey=%s&amp;mobile=%s&amp;tpl_id=%d&amp;tpl_value=%s"</span>, apikey, mobile, tpl_id, tpl_value);
			    send_data(url_tpl_sms, params);
			}
			<span class="hljs-comment">/**
			* 使用智能匹配模板接口发短信
			*/</span>
			<span class="hljs-function"><span class="hljs-keyword">void</span> <span class="hljs-title">send_voice</span><span class="hljs-params">(<span class="hljs-keyword">char</span> *apikey, <span class="hljs-keyword">char</span> *mobile, <span class="hljs-keyword">int</span> code)</span>
			</span>{
			    <span class="hljs-keyword">char</span> params[MAXPARAM + <span class="hljs-number">1</span>];
			    <span class="hljs-keyword">char</span> *cp = params;
			    <span class="hljs-built_in">sprintf</span>(params,<span class="hljs-string">"apikey=%s&amp;mobile=%s&amp;code=%d"</span>, apikey, mobile, code);
			    send_data(url_send_voice, params);
			}
			<span class="hljs-function"><span class="hljs-keyword">int</span> <span class="hljs-title">main</span><span class="hljs-params">(<span class="hljs-keyword">void</span>)</span>
			</span>{
			
			    curl = curl_easy_init();
			    mobile = curl_easy_escape(curl,mobile,<span class="hljs-built_in">strlen</span>(mobile));
			    <span class="hljs-keyword">if</span>(<span class="hljs-literal">NULL</span> == curl) {
			        perror(<span class="hljs-string">"curl open fail\n"</span>);
			    }
			    <span class="hljs-comment">// 获取用户信息</span>
			    get_user(apikey);
			    <span class="hljs-comment">// 发送短信</span>
			    send_sms(apikey,mobile,text);
			    <span class="hljs-comment">// 发送语音验证码</span>
			    send_voice(apikey,mobile,code);
			
			    <span class="hljs-keyword">char</span> *tmp;
			    <span class="hljs-keyword">char</span> *tpl_value = (<span class="hljs-keyword">char</span> *)<span class="hljs-built_in">malloc</span>(<span class="hljs-keyword">sizeof</span>(<span class="hljs-keyword">char</span>) * <span class="hljs-number">500</span>);
			    bzero(tpl_value, <span class="hljs-keyword">sizeof</span>(<span class="hljs-keyword">char</span>)*<span class="hljs-number">500</span>);
			
			    <span class="hljs-comment">// 模板短信发送需要编码两次，第一次URL编码转换</span>
			    <span class="hljs-keyword">int</span> len = <span class="hljs-number">0</span>;
			    tmp = curl_easy_escape(curl,tpl_data[<span class="hljs-number">0</span>],<span class="hljs-built_in">strlen</span>(tpl_data[<span class="hljs-number">0</span>]));
			    <span class="hljs-built_in">memcpy</span>(tpl_value+len,tmp,<span class="hljs-built_in">strlen</span>(tmp));
			    len += <span class="hljs-built_in">strlen</span>(tmp);
			    tpl_value[len++] = <span class="hljs-string">'='</span>;
			
			    tmp = curl_easy_escape(curl,tpl_data[<span class="hljs-number">1</span>],<span class="hljs-built_in">strlen</span>(tpl_data[<span class="hljs-number">1</span>]));
			    <span class="hljs-built_in">memcpy</span>(tpl_value+len,tmp,<span class="hljs-built_in">strlen</span>(tmp));
			    len += <span class="hljs-built_in">strlen</span>(tmp);
			    tpl_value[len++] = <span class="hljs-string">'&amp;'</span>;
			
			    tmp = curl_easy_escape(curl,tpl_data[<span class="hljs-number">2</span>],<span class="hljs-built_in">strlen</span>(tpl_data[<span class="hljs-number">2</span>]));
			    <span class="hljs-built_in">memcpy</span>(tpl_value+len,tmp,<span class="hljs-built_in">strlen</span>(tmp));
			    len += <span class="hljs-built_in">strlen</span>(tmp);
			    tpl_value[len++] = <span class="hljs-string">'='</span>;
			
			    tmp = curl_easy_escape(curl,tpl_data[<span class="hljs-number">3</span>],<span class="hljs-built_in">strlen</span>(tpl_data[<span class="hljs-number">3</span>]));
			    <span class="hljs-built_in">memcpy</span>(tpl_value+len,tmp,<span class="hljs-built_in">strlen</span>(tmp));
			    len += <span class="hljs-built_in">strlen</span>(tmp);
			
			    tmp=tpl_value;
			    <span class="hljs-comment">// 第二次URL编码</span>
			    tpl_value = curl_easy_escape(curl,tpl_value,<span class="hljs-built_in">strlen</span>(tpl_value));
			    send_tpl_sms(apikey,mobile,tpl_id,tpl_value);
			    <span class="hljs-built_in">free</span>(tmp);
			
			    curl_easy_cleanup(curl);
			    
			    <span class="hljs-keyword">return</span> <span class="hljs-number">0</span>;
			}
			</code></pre>
			                <p>
			                    <button class="fuzhidaima fuzhi_cplus" data-clipboard-target="cplus_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content5" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>shell/bash</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_shell" data-clipboard-target="shell_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="shell hljs bash" id="shell_text"><span class="hljs-comment">#/bin/sh</span>
			<span class="hljs-comment">#author jacky</span>
			<span class="hljs-comment">#修改为您的apikey</span>
			apikey=<span class="hljs-string">"xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx"</span>
			<span class="hljs-comment">#修改为您要发送的手机号</span>
			mobile=<span class="hljs-string">"188xxxxxxxx"</span>
			<span class="hljs-comment">#设置您要发送的内容</span>
			text=<span class="hljs-string">"您的验证码是1234。如非本人操作，请忽略。【云片网】"</span>
			<span class="hljs-built_in">echo</span> <span class="hljs-string">"get user info:"</span>
			curl --data <span class="hljs-string">"apikey=<span class="hljs-variable">$apikey</span>"</span> <span class="hljs-string">"https://sms.yunpian.com/v2/user/get.json"</span>
			<span class="hljs-built_in">echo</span> <span class="hljs-string">"\nsend sms:"</span>
			curl --data <span class="hljs-string">"apikey=<span class="hljs-variable">$apikey</span>&amp;mobile=<span class="hljs-variable">$mobile</span>&amp;text=<span class="hljs-variable">$text</span>"</span> <span class="hljs-string">"https://sms.yunpian.com/v2/sms/single_send.json"</span></code></pre>
			                <p>
			                    <button style="margin-bottom: 200px;" class="fuzhidaima fuzhi_shell" data-clipboard-target="shell_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content6" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>php</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_php" data-clipboard-target="php_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code id="php_text" class="hljs xml"><span class="php"><span class="hljs-preprocessor">&lt;?php</span>
			header(<span class="hljs-string">"Content-Type:text/html;charset=utf-8"</span>);
			<span class="hljs-variable">$apikey</span> = <span class="hljs-string">"xxxxxxxxxxx"</span>; <span class="hljs-comment">//修改为您的apikey(https://www.yunpian.com)登陆官网后获取</span>
			<span class="hljs-variable">$mobile</span> = <span class="hljs-string">"xxxxxxxxxxx"</span>; <span class="hljs-comment">//请用自己的手机号代替</span>
			<span class="hljs-variable">$text</span>=<span class="hljs-string">"【云片网】您的验证码是1234"</span>;
			<span class="hljs-variable">$ch</span> = curl_init();
			
			<span class="hljs-comment">/* 设置验证方式 */</span>
			
			curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_HTTPHEADER, <span class="hljs-keyword">array</span>(<span class="hljs-string">'Accept:text/plain;charset=utf-8'</span>, <span class="hljs-string">'Content-Type:application/x-www-form-urlencoded'</span>,<span class="hljs-string">'charset=utf-8'</span>));
			
			<span class="hljs-comment">/* 设置返回结果为流 */</span>
			curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_RETURNTRANSFER, <span class="hljs-keyword">true</span>);
			
			<span class="hljs-comment">/* 设置超时时间*/</span>
			curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_TIMEOUT, <span class="hljs-number">10</span>);
			
			<span class="hljs-comment">/* 设置通信方式 */</span>
			curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_POST, <span class="hljs-number">1</span>);
			curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_SSL_VERIFYPEER, <span class="hljs-keyword">false</span>);
			<span class="hljs-comment">// 取得用户信息</span>
			<span class="hljs-variable">$json_data</span> = get_user(<span class="hljs-variable">$ch</span>,<span class="hljs-variable">$apikey</span>);
			<span class="hljs-variable">$array</span> = json_decode(<span class="hljs-variable">$json_data</span>,<span class="hljs-keyword">true</span>);
			<span class="hljs-keyword">echo</span> <span class="hljs-string">'&lt;pre&gt;'</span>;print_r(<span class="hljs-variable">$array</span>);
			<span class="hljs-comment">// 发送短信</span>
			<span class="hljs-variable">$data</span>=<span class="hljs-keyword">array</span>(<span class="hljs-string">'text'</span>=&gt;<span class="hljs-variable">$text</span>,<span class="hljs-string">'apikey'</span>=&gt;<span class="hljs-variable">$apikey</span>,<span class="hljs-string">'mobile'</span>=&gt;<span class="hljs-variable">$mobile</span>);
			<span class="hljs-variable">$json_data</span> = send(<span class="hljs-variable">$ch</span>,<span class="hljs-variable">$data</span>);
			<span class="hljs-variable">$array</span> = json_decode(<span class="hljs-variable">$json_data</span>,<span class="hljs-keyword">true</span>);
			<span class="hljs-keyword">echo</span> <span class="hljs-string">'&lt;pre&gt;'</span>;print_r(<span class="hljs-variable">$array</span>);
			<span class="hljs-comment">// 发送模板短信</span>
			<span class="hljs-comment">// 需要对value进行编码</span>
			<span class="hljs-variable">$data</span>=<span class="hljs-keyword">array</span>(<span class="hljs-string">'tpl_id'</span>=&gt;<span class="hljs-string">'1'</span>,<span class="hljs-string">'tpl_value'</span>=&gt;(<span class="hljs-string">'#code#'</span>).<span class="hljs-string">'='</span>.urlencode(<span class="hljs-string">'1234'</span>).<span class="hljs-string">'&amp;'</span>.urlencode(<span class="hljs-string">'#company#'</span>).<span class="hljs-string">'='</span>.urlencode(<span class="hljs-string">'欢乐行'</span>),<span class="hljs-string">'apikey'</span>=&gt;<span class="hljs-variable">$apikey</span>,<span class="hljs-string">'mobile'</span>=&gt;<span class="hljs-variable">$mobile</span>);
			print_r (<span class="hljs-variable">$data</span>);
			<span class="hljs-variable">$json_data</span> = tpl_send(<span class="hljs-variable">$ch</span>,<span class="hljs-variable">$data</span>);
			<span class="hljs-variable">$array</span> = json_decode(<span class="hljs-variable">$json_data</span>,<span class="hljs-keyword">true</span>);
			<span class="hljs-keyword">echo</span> <span class="hljs-string">'&lt;pre&gt;'</span>;print_r(<span class="hljs-variable">$array</span>);
			
			<span class="hljs-comment">// 发送语音验证码</span>
			<span class="hljs-variable">$data</span>=<span class="hljs-keyword">array</span>(<span class="hljs-string">'code'</span>=&gt;<span class="hljs-string">'9876'</span>,<span class="hljs-string">'apikey'</span>=&gt;<span class="hljs-variable">$apikey</span>,<span class="hljs-string">'mobile'</span>=&gt;<span class="hljs-variable">$mobile</span>);
			<span class="hljs-variable">$json_data</span> =voice_send(<span class="hljs-variable">$ch</span>,<span class="hljs-variable">$data</span>);
			<span class="hljs-variable">$array</span> = json_decode(<span class="hljs-variable">$json_data</span>,<span class="hljs-keyword">true</span>);
			<span class="hljs-keyword">echo</span> <span class="hljs-string">'&lt;pre&gt;'</span>;print_r(<span class="hljs-variable">$array</span>);
			
			curl_close(<span class="hljs-variable">$ch</span>);
			
			<span class="hljs-comment">/***************************************************************************************/</span>
			<span class="hljs-comment">//获得账户</span>
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">get_user</span><span class="hljs-params">(<span class="hljs-variable">$ch</span>,<span class="hljs-variable">$apikey</span>)</span></span>{
			    curl_setopt (<span class="hljs-variable">$ch</span>, CURLOPT_URL, <span class="hljs-string">'https://sms.yunpian.com/v2/user/get.json'</span>);
			    curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_POSTFIELDS, http_build_query(<span class="hljs-keyword">array</span>(<span class="hljs-string">'apikey'</span> =&gt; <span class="hljs-variable">$apikey</span>)));
			    <span class="hljs-keyword">return</span> curl_exec(<span class="hljs-variable">$ch</span>);
			}
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">send</span><span class="hljs-params">(<span class="hljs-variable">$ch</span>,<span class="hljs-variable">$data</span>)</span></span>{
			    curl_setopt (<span class="hljs-variable">$ch</span>, CURLOPT_URL, <span class="hljs-string">'https://sms.yunpian.com/v2/sms/single_send.json'</span>);
			    curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_POSTFIELDS, http_build_query(<span class="hljs-variable">$data</span>));
			    <span class="hljs-keyword">return</span> curl_exec(<span class="hljs-variable">$ch</span>);
			}
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">tpl_send</span><span class="hljs-params">(<span class="hljs-variable">$ch</span>,<span class="hljs-variable">$data</span>)</span></span>{
			    curl_setopt (<span class="hljs-variable">$ch</span>, CURLOPT_URL, <span class="hljs-string">'https://sms.yunpian.com/v2/sms/tpl_single_send.json'</span>);
			    curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_POSTFIELDS, http_build_query(<span class="hljs-variable">$data</span>));
			    <span class="hljs-keyword">return</span> curl_exec(<span class="hljs-variable">$ch</span>);
			}
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">voice_send</span><span class="hljs-params">(<span class="hljs-variable">$ch</span>,<span class="hljs-variable">$data</span>)</span></span>{
			    curl_setopt (<span class="hljs-variable">$ch</span>, CURLOPT_URL, <span class="hljs-string">'http://voice.yunpian.com/v2/voice/send.json'</span>);
			    curl_setopt(<span class="hljs-variable">$ch</span>, CURLOPT_POSTFIELDS, http_build_query(<span class="hljs-variable">$data</span>));
			    <span class="hljs-keyword">return</span> curl_exec(<span class="hljs-variable">$ch</span>);
			}
			
			<span class="hljs-preprocessor">?&gt;</span></span></code></pre>
			                <p>
			                    <button class="fuzhidaima fuzhi_php" data-clipboard-target="php_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content7" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>asp</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_asp" data-clipboard-target="asp_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="asp hljs xml" id="asp_text"><span class="hljs-tag">&lt;<span class="hljs-title">%</span>
			'安装<span class="hljs-attribute">IIS</span>并重启后可用
			@<span class="hljs-attribute">LANGUAGE</span>=<span class="hljs-value">"VBSCRIPT"</span> <span class="hljs-attribute">CODEPAGE</span>=<span class="hljs-value">"65001"</span>
			%&gt;</span>
			<span class="hljs-tag">&lt;<span class="hljs-title">%</span>
			<span class="hljs-attribute">response.contenttype</span> = "<span class="hljs-attribute">text</span>/<span class="hljs-attribute">html</span>;<span class="hljs-attribute">charset</span>=<span class="hljs-value">utf-8"</span>
			'提交方法
			<span class="hljs-attribute">method</span> = "<span class="hljs-attribute">POST</span>"
			'您要发送的手机号
			<span class="hljs-attribute">mobile</span> = <span class="hljs-attribute">Server.URLEncode</span>("<span class="hljs-attribute">xxxxxxxxxxx</span>")
			'修改为您的<span class="hljs-attribute">apikey</span>(<span class="hljs-attribute">https:</span>//<span class="hljs-attribute">www.yunpian.com</span>)登陆官网后获取
			<span class="hljs-attribute">apikey</span> = "<span class="hljs-attribute">xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx</span>"
			'发送内容
			<span class="hljs-attribute">text</span> =<span class="hljs-value">"【云片网】您的验证码是1234"</span>
			'使用模板号
			<span class="hljs-attribute">tpl_id</span> = <span class="hljs-attribute">1</span>
			'使用模板内容
			<span class="hljs-attribute">tpl_value</span> = <span class="hljs-attribute">Server.URLEncode</span>("#<span class="hljs-attribute">code</span>#=<span class="hljs-value">1234&amp;#company#=云片网")</span>
			'查询账户信息<span class="hljs-attribute">https</span>地址
			<span class="hljs-attribute">url_get_user</span>   = "<span class="hljs-attribute">https:</span>//<span class="hljs-attribute">sms.yunpian.com</span>/<span class="hljs-attribute">v2</span>/<span class="hljs-attribute">user</span>/<span class="hljs-attribute">get.json</span>"
			'智能匹配模板发送<span class="hljs-attribute">https</span>地址
			<span class="hljs-attribute">url_send_sms</span>   = "<span class="hljs-attribute">https:</span>//<span class="hljs-attribute">sms.yunpian.com</span>/<span class="hljs-attribute">v2</span>/<span class="hljs-attribute">sms</span>/<span class="hljs-attribute">single_send.json</span>"
			'指定模板发送接口<span class="hljs-attribute">https</span>地址
			<span class="hljs-attribute">url_tpl_sms</span>   = "<span class="hljs-attribute">https:</span>//<span class="hljs-attribute">sms.yunpian.com</span>/<span class="hljs-attribute">v2</span>/<span class="hljs-attribute">sms</span>/<span class="hljs-attribute">tpl_single_send.json</span>"
			'发送语音验证码接口<span class="hljs-attribute">https</span>地址
			<span class="hljs-attribute">url_send_voice</span> = "<span class="hljs-attribute">https:</span>//<span class="hljs-attribute">voice.yunpian.com</span>/<span class="hljs-attribute">v2</span>/<span class="hljs-attribute">voice</span>/<span class="hljs-attribute">send.json</span>"
			<span class="hljs-attribute">data_get_user</span>  =  "<span class="hljs-attribute">apikey</span>=<span class="hljs-value">" &amp; apikey
			data_send_sms  =  "</span><span class="hljs-value">apikey="</span> &amp; <span class="hljs-attribute">apikey</span> &amp; "&amp;<span class="hljs-attribute">mobile</span>=<span class="hljs-value">" &amp; mobile &amp; "</span><span class="hljs-value">&amp;text="</span> &amp; <span class="hljs-attribute">text</span>
			<span class="hljs-attribute">data_tpl_sms</span>    = "<span class="hljs-attribute">apikey</span>=<span class="hljs-value">" &amp; apikey &amp; "</span><span class="hljs-value">&amp;mobile="</span> &amp; <span class="hljs-attribute">mobile</span> &amp; "&amp;<span class="hljs-attribute">tpl_id</span>=<span class="hljs-value">" &amp; tpl_id &amp; "</span><span class="hljs-value">&amp;tpl_value="</span> &amp; <span class="hljs-attribute">tpl_value</span>
			<span class="hljs-attribute">data_send_voice</span> = "<span class="hljs-attribute">apikey</span>=<span class="hljs-value">" &amp; apikey &amp; "</span><span class="hljs-value">&amp;mobile="</span> &amp; <span class="hljs-attribute">mobile</span> &amp; "&amp;<span class="hljs-attribute">code</span>=<span class="hljs-value">" &amp; "</span><span class="hljs-value">1234"</span>
			<span class="hljs-attribute">response.write</span> <span class="hljs-attribute">GetBody</span>(<span class="hljs-attribute">url_get_user</span>,<span class="hljs-attribute">data_get_user</span>)
			<span class="hljs-attribute">response.write</span> <span class="hljs-attribute">GetBody</span>(<span class="hljs-attribute">url_send_sms</span>,<span class="hljs-attribute">data_send_sms</span>)
			<span class="hljs-attribute">response.write</span> <span class="hljs-attribute">GetBody</span>(<span class="hljs-attribute">url_tpl_sms</span>,<span class="hljs-attribute">data_tpl_sms</span>)
			<span class="hljs-attribute">response.write</span> <span class="hljs-attribute">GetBody</span>(<span class="hljs-attribute">url_send_voice</span>,<span class="hljs-attribute">data_send_voice</span>)
			<span class="hljs-attribute">Function</span> <span class="hljs-attribute">GetBody</span>(<span class="hljs-attribute">url</span>,<span class="hljs-attribute">data</span>) 
			    <span class="hljs-attribute">Set</span> <span class="hljs-attribute">https</span> = <span class="hljs-attribute">Server.CreateObject</span>("<span class="hljs-attribute">MSXML2.ServerXMLHTTP</span>") 
			    <span class="hljs-attribute">With</span> <span class="hljs-attribute">https</span>
			    <span class="hljs-attribute">.Open</span> <span class="hljs-attribute">method</span>, <span class="hljs-attribute">url</span>, <span class="hljs-attribute">False</span>
			    <span class="hljs-attribute">.setRequestHeader</span> "<span class="hljs-attribute">Content-Type</span>","<span class="hljs-attribute">application</span>/<span class="hljs-attribute">x-www-form-urlencoded</span>"
			    <span class="hljs-attribute">.Send</span> <span class="hljs-attribute">data</span>
			    
			    <span class="hljs-attribute">GetBody</span>= <span class="hljs-attribute">.ResponseBody</span>
			    <span class="hljs-attribute">End</span> <span class="hljs-attribute">With</span>
			    <span class="hljs-attribute">GetBody</span> = <span class="hljs-attribute">bytetostr</span>(<span class="hljs-attribute">httpss.ResponseBody</span>,"<span class="hljs-attribute">utf-8</span>")
			    <span class="hljs-attribute">Set</span> <span class="hljs-attribute">httpss</span> = <span class="hljs-attribute">Nothing</span> 
			    
			<span class="hljs-attribute">End</span> <span class="hljs-attribute">Function</span>
			<span class="hljs-attribute">function</span> <span class="hljs-attribute">bytetostr</span>(<span class="hljs-attribute">vin</span>,<span class="hljs-attribute">cset</span>)
			<span class="hljs-attribute">dim</span> <span class="hljs-attribute">bs</span>,<span class="hljs-attribute">sr</span>
			<span class="hljs-attribute">set</span> <span class="hljs-attribute">bs</span> = <span class="hljs-attribute">server.createObject</span>("<span class="hljs-attribute">adodb.stream</span>")
			    <span class="hljs-attribute">bs.type</span> = <span class="hljs-attribute">2</span>
			    <span class="hljs-attribute">bs.open</span>
			    <span class="hljs-attribute">bs.writetext</span> <span class="hljs-attribute">vin</span>
			    <span class="hljs-attribute">bs.position</span> = <span class="hljs-attribute">0</span>
			    <span class="hljs-attribute">bs.charset</span> = <span class="hljs-attribute">cset</span>
			    <span class="hljs-attribute">bs.position</span> = <span class="hljs-attribute">2</span>
			    <span class="hljs-attribute">sr</span> = <span class="hljs-attribute">bs.readtext</span>
			    <span class="hljs-attribute">bs.close</span>
			<span class="hljs-attribute">set</span> <span class="hljs-attribute">bs</span> = <span class="hljs-attribute">nothing</span>
			<span class="hljs-attribute">bytetostr</span> = <span class="hljs-attribute">sr</span>
			<span class="hljs-attribute">end</span> <span class="hljs-attribute">function</span>
			%&gt;</span></code></pre>
			                <p>
			                    <button style="margin-bottom: 50px;" class="fuzhidaima asp_shell" data-clipboard-target="asp_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content8" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>nodejs</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_nodejs" data-clipboard-target="nodejs_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="nodejs hljs javascript" id="nodejs_text"><span class="hljs-comment">// 修改为您的apikey.可在官网（https://www.yuanpian.com)登录后获取</span>
			<span class="hljs-keyword">var</span> https = <span class="hljs-built_in">require</span>(<span class="hljs-string">'https'</span>);
			<span class="hljs-keyword">var</span> qs = <span class="hljs-built_in">require</span>(<span class="hljs-string">'querystring'</span>);
			
			<span class="hljs-keyword">var</span> apikey = <span class="hljs-string">'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'</span>;
			<span class="hljs-comment">// 修改为您要发送的手机号码，多个号码用逗号隔开</span>
			<span class="hljs-keyword">var</span> mobile = <span class="hljs-string">'xxxxxxxxxxx'</span>;
			<span class="hljs-comment">// 修改为您要发送的短信内容</span>
			<span class="hljs-keyword">var</span> text = <span class="hljs-string">'【云片网】您的验证码是1234'</span>;
			<span class="hljs-comment">// 指定发送的模板编号</span>
			<span class="hljs-keyword">var</span> tpl_id = <span class="hljs-number">1</span>;
			<span class="hljs-comment">// 指定发送模板的内容</span>
			<span class="hljs-keyword">var</span> tpl_value =  {<span class="hljs-string">'#code#'</span>:<span class="hljs-string">'1234'</span>,<span class="hljs-string">'#company#'</span>:<span class="hljs-string">'yunpian'</span>};
			<span class="hljs-comment">// 语音短信的内容</span>
			<span class="hljs-keyword">var</span> code = <span class="hljs-string">'1234'</span>;
			<span class="hljs-comment">// 查询账户信息https地址</span>
			<span class="hljs-keyword">var</span> get_user_info_uri = <span class="hljs-string">'/v2/user/get.json'</span>;
			<span class="hljs-comment">// 智能匹配模板发送https地址</span>
			<span class="hljs-keyword">var</span> sms_host = <span class="hljs-string">'sms.yunpian.com'</span>;
			<span class="hljs-keyword">var</span> voice_host = <span class="hljs-string">'voice.yunpian.com'</span>;
			
			send_sms_uri = <span class="hljs-string">'/v2/sms/single_send.json'</span>;
			<span class="hljs-comment">// 指定模板发送接口https地址</span>
			send_tpl_sms_uri = <span class="hljs-string">'/v2/sms/tpl_single_send.json'</span>;
			<span class="hljs-comment">// 发送语音验证码接口https地址</span>
			send_voice_uri = <span class="hljs-string">'/v2/voice/send.json'</span>;
			
			
			
			
			
			query_user_info(get_user_info_uri,apikey);
			
			send_sms(send_sms_uri,apikey,mobile,text);
			
			send_tpl_sms(send_tpl_sms_uri,apikey,mobile,tpl_id,tpl_value);
			
			send_voice_sms(send_voice_uri,apikey,mobile,code);
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">query_user_info</span>(<span class="hljs-params">uri,apikey</span>)</span>{
			    <span class="hljs-keyword">var</span> post_data = {  
			    <span class="hljs-string">'apikey'</span>: apikey,  
			    };<span class="hljs-comment">//这是需要提交的数据</span>
			    <span class="hljs-keyword">var</span> content = qs.stringify(post_data);  
			    post(uri,content,sms_host);
			}
			
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">send_sms</span>(<span class="hljs-params">uri,apikey,mobile,text</span>)</span>{
			    <span class="hljs-keyword">var</span> post_data = {  
			    <span class="hljs-string">'apikey'</span>: apikey,  
			    <span class="hljs-string">'mobile'</span>:mobile,
			    <span class="hljs-string">'text'</span>:text,
			    };<span class="hljs-comment">//这是需要提交的数据  </span>
			    <span class="hljs-keyword">var</span> content = qs.stringify(post_data);  
			    post(uri,content,sms_host);
			}
			
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">send_tpl_sms</span>(<span class="hljs-params">uri,apikey,mobile,tpl_id,tpl_value</span>)</span>{
			    <span class="hljs-keyword">var</span> post_data = {  
			    <span class="hljs-string">'apikey'</span>: apikey,
			    <span class="hljs-string">'mobile'</span>:mobile,
			    <span class="hljs-string">'tpl_id'</span>:tpl_id,
			    <span class="hljs-string">'tpl_value'</span>:qs.stringify(tpl_value),  
			    };<span class="hljs-comment">//这是需要提交的数据  </span>
			    <span class="hljs-keyword">var</span> content = qs.stringify(post_data);  
			    post(uri,content,sms_host); 
			}
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">send_voice_sms</span>(<span class="hljs-params">uri,apikey,mobile,code</span>)</span>{
			    <span class="hljs-keyword">var</span> post_data = {  
			    <span class="hljs-string">'apikey'</span>: apikey,
			    <span class="hljs-string">'mobile'</span>:mobile,
			    <span class="hljs-string">'code'</span>:code,
			    };<span class="hljs-comment">//这是需要提交的数据  </span>
			    <span class="hljs-keyword">var</span> content = qs.stringify(post_data);  
			    <span class="hljs-built_in">console</span>.log(content);
			    post(uri,content,voice_host); 
			}
			<span class="hljs-function"><span class="hljs-keyword">function</span> <span class="hljs-title">post</span>(<span class="hljs-params">uri,content,host</span>)</span>{
			    <span class="hljs-keyword">var</span> options = {  
			        hostname: host,
			        port: <span class="hljs-number">443</span>,  
			        path: uri,  
			        method: <span class="hljs-string">'POST'</span>,  
			        headers: {  
			            <span class="hljs-string">'Content-Type'</span>: <span class="hljs-string">'application/x-www-form-urlencoded; charset=UTF-8'</span>  
			        }  
			    };
			    <span class="hljs-keyword">var</span> req = https.request(options, <span class="hljs-function"><span class="hljs-keyword">function</span> (<span class="hljs-params">res</span>) </span>{  
			        <span class="hljs-comment">// console.log('STATUS: ' + res.statusCode);  </span>
			        <span class="hljs-comment">// console.log('HEADERS: ' + JSON.stringify(res.headers));  </span>
			        res.setEncoding(<span class="hljs-string">'utf8'</span>);  
			        res.on(<span class="hljs-string">'data'</span>, <span class="hljs-function"><span class="hljs-keyword">function</span> (<span class="hljs-params">chunk</span>) </span>{  
			            <span class="hljs-built_in">console</span>.log(<span class="hljs-string">'BODY: '</span> + chunk);  
			        });  
			    }); 
			    <span class="hljs-comment">//console.log(content);</span>
			    req.write(content);  
			  
			    req.end();   
			}</code></pre>
			                <p>
			                    <button style="margin-bottom: 50px;" class="fuzhidaima fuzhi_nodejs" data-clipboard-target="nodejs_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content9" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>go</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_go" data-clipboard-target="go_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="go hljs cpp" id="go_text"><span class="hljs-function">package main
			<span class="hljs-title">import</span> <span class="hljs-params">(
			    <span class="hljs-string">"net/http"</span>
			    <span class="hljs-string">"io/ioutil"</span>
			    <span class="hljs-string">"net/url"</span>
			    <span class="hljs-string">"fmt"</span>
			 )</span>
			<span class="hljs-comment">// bingone</span>
			func <span class="hljs-title">main</span><span class="hljs-params">()</span></span>{
			
			    <span class="hljs-comment">// 修改为您的apikey(https://www.yunpian.com)登陆官网后获取</span>
			    apikey      := <span class="hljs-string">"xxxxxxxxxxxxxxxxxx"</span>
			     <span class="hljs-comment">// 修改为您要发送的手机号码，多个号码用逗号隔开</span>
			    mobile      := <span class="hljs-string">"xxxxxxxxxxxxxxxxxx"</span>
			    <span class="hljs-comment">// 发送内容</span>
			    text        := <span class="hljs-string">"【云片网】您的验证码是1234"</span>
			    <span class="hljs-comment">// 发送模板编号</span>
			    tpl_id      := <span class="hljs-number">1</span>
			    <span class="hljs-comment">// 语音验证码</span>
			    code        := <span class="hljs-string">"1234"</span>
			    company     := <span class="hljs-string">"云片网"</span>
			    <span class="hljs-comment">// 发送模板内容</span>
			    tpl_value   := url.Values{<span class="hljs-string">"#code#"</span>:{code},<span class="hljs-string">"#company#"</span>:{company}}.Encode()
			    
			    <span class="hljs-comment">// 获取user信息url</span>
			    url_get_user    := <span class="hljs-string">"https://sms.yunpian.com/v2/user/get.json"</span>;
			    <span class="hljs-comment">// 智能模板发送短信url</span>
			    url_send_sms    := <span class="hljs-string">"https://sms.yunpian.com/v2/sms/single_send.json"</span>;
			    <span class="hljs-comment">// 指定模板发送短信url</span>
			    url_tpl_sms     := <span class="hljs-string">"https://sms.yunpian.com/v2/sms/tpl_single_send.json"</span>;
			    <span class="hljs-comment">// 发送语音短信url</span>
			    url_send_voice  := <span class="hljs-string">"https://voice.yunpian.com/v2/voice/send.json"</span>;
			
			    data_get_user   := url.Values{<span class="hljs-string">"apikey"</span>: {apikey}}
			    data_send_sms   := url.Values{<span class="hljs-string">"apikey"</span>: {apikey}, <span class="hljs-string">"mobile"</span>: {mobile},<span class="hljs-string">"text"</span>:{text}}
			    data_tpl_sms    := url.Values{<span class="hljs-string">"apikey"</span>: {apikey}, <span class="hljs-string">"mobile"</span>: {mobile},<span class="hljs-string">"tpl_id"</span>:{fmt.Sprintf(<span class="hljs-string">"%d"</span>, tpl_id)},<span class="hljs-string">"tpl_value"</span>:{tpl_value}}
			    data_send_voice := url.Values{<span class="hljs-string">"apikey"</span>: {apikey}, <span class="hljs-string">"mobile"</span>: {mobile},<span class="hljs-string">"code"</span>:{code}}
			
			
			    httpsPostForm(url_get_user,data_get_user)
			    httpsPostForm(url_send_sms,data_send_sms)
			    httpsPostForm(url_tpl_sms,data_tpl_sms)
			    httpsPostForm(url_send_voice,data_send_voice)
			}
			
			<span class="hljs-function">func <span class="hljs-title">httpsPostForm</span><span class="hljs-params">(url <span class="hljs-built_in">string</span>,data url.Values)</span> </span>{
			    resp, err := http.PostForm(url,data)
			
			    <span class="hljs-keyword">if</span> err != nil {
			        <span class="hljs-comment">// handle error</span>
			    }
			
			    defer resp.Body.Close()
			    body, err := ioutil.ReadAll(resp.Body)
			    <span class="hljs-keyword">if</span> err != nil {
			        <span class="hljs-comment">// handle error</span>
			    }
			
			    fmt.Println(<span class="hljs-built_in">string</span>(body))
			
			}</code></pre>
			                <p>
			                    <button style="margin-bottom: 50px;" class="fuzhidaima fuzhi_go" data-clipboard-target="go_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			            <div class="divTab tab-content10" style="display: none;">
			                <h2 class="page-title">代码示例&nbsp;<span>ruby</span></h2>
			    <p>功能说明：<span class="black">该接口要求提前在云片后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    <button class="fuzhidaima fuzhi_ruby" data-clipboard-target="shell_ruby">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			                <pre><code class="ruby hljs" id="ruby_text"><span class="hljs-comment">=begin
			
			Desc:短信http接口的ruby代码调用示例
			author shaoyan
			date 2015-10.28
			
			=end</span>
			
			<span class="hljs-keyword">require</span> <span class="hljs-string">'net/http'</span>
			<span class="hljs-keyword">require</span> <span class="hljs-string">'uri'</span>
			params = {}
			
			
			<span class="hljs-comment">#修改为您的apikey.可在官网（http://www.yuanpian.com)登录后用户中心首页看到</span>
			apikey = <span class="hljs-string">'xxxxxxxxxxxxxxxxxxxxxxxxxxxx'</span>
			<span class="hljs-comment">#修改为您要发送的手机号码，多个号码用逗号隔开</span>
			mobile = <span class="hljs-string">'xxxxxxxxxxxxxxxxxxxxxxxxxxxx'</span>
			<span class="hljs-comment">#修改为您要发送的短信内容</span>
			text = <span class="hljs-string">'【云片网】您的验证码是1234'</span>
			<span class="hljs-comment">#查询账户信息HTTP地址</span>
			get_user_info_uri = <span class="hljs-constant">URI</span>.parse(<span class="hljs-string">'https://sms.yunpian.com/v2/user/get.json'</span>)
			<span class="hljs-comment">#智能匹配模板发送HTTP地址</span>
			send_sms_uri = <span class="hljs-constant">URI</span>.parse(<span class="hljs-string">'https://sms.yunpian.com/v2/sms/single_send.json'</span>)
			<span class="hljs-comment">#指定模板发送接口HTTP地址</span>
			send_tpl_sms_uri = <span class="hljs-constant">URI</span>.parse(<span class="hljs-string">'https://sms.yunpian.com/v2/sms/tpl_single_send.json'</span>)
			<span class="hljs-comment">#发送语音验证码接口HTTP地址</span>
			send_voice_uri = <span class="hljs-constant">URI</span>.parse(<span class="hljs-string">'https://voice.yunpian.com/v2/voice/send.json'</span>)
			
			params[<span class="hljs-string">'apikey'</span>] = apikey
			<span class="hljs-comment">#打印用户信息</span>
			response =  <span class="hljs-constant">Net::HTTP</span>.post_form(get_user_info_uri,params)
			print response.body + <span class="hljs-string">"\n"</span>
			
			params[<span class="hljs-string">'mobile'</span>] = mobile
			params[<span class="hljs-string">'text'</span>] = text
			<span class="hljs-comment">#智能匹配模板发送</span>
			response = <span class="hljs-constant">Net::HTTP</span>.post_form(send_sms_uri,params)
			print response.body + <span class="hljs-string">"\n"</span>
			<span class="hljs-comment">#指定模板发送</span>
			<span class="hljs-comment">#设置模板ID，如使用1号模板:【#company#】您的验证码是#code#</span>
			<span class="hljs-comment">#设置对应的模板变量值</span>
			
			params[<span class="hljs-string">'tpl_id'</span>] = <span class="hljs-number">1</span>
			params[<span class="hljs-string">'tpl_value'</span>] = <span class="hljs-constant">URI::</span>escape(<span class="hljs-string">'#code#'</span>)+<span class="hljs-string">'='</span>+<span class="hljs-constant">URI::</span>escape(<span class="hljs-string">'1234'</span>)+<span class="hljs-string">'&amp;'</span>+<span class="hljs-constant">URI::</span>escape(<span class="hljs-string">'#company#'</span>)+<span class="hljs-string">'='</span>+<span class="hljs-constant">URI::</span>escape(<span class="hljs-string">'yunpian'</span>)
			response = <span class="hljs-constant">Net::HTTP</span>.post_form(send_tpl_sms_uri,params)
			print response.body + <span class="hljs-string">"\n"</span>
			<span class="hljs-comment">#发送语音验证码</span>
			params[<span class="hljs-string">'code'</span>] = <span class="hljs-number">1234</span>
			response = <span class="hljs-constant">Net::HTTP</span>.post_form(send_voice_uri,params)
			print response.body + <span class="hljs-string">"\n"</span>
			
			</code></pre>
			                <p>
			                    <button style="margin-bottom: 50px;" class="fuzhidaima fuzhi_ruby" data-clipboard-target="ruby_text">复制代码</button>
			                    <span class="fuzhitip"></span>
			                </p>
			            </div>
			        </div>
			    </div>
			</div>
		</div>
	</div>
	<#include "/common/footer.ftl"/>
</body>
<script>
	//头部菜单选择
	service("api");
	api_service("demo");
</script>
</html>