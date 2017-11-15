<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-调用说明</title>
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
			        <h2 class="page-title">调用说明</h2>
			        <article class="doc">
			            <section class="one">
			                <h3>1、调用方式</h3>
			                <p>云片短信API是使用HTTP并遵循REST原则设计的Web服务接口，您可以使用几乎任何客户端和任何编程语言与REST API进行交互。通过发送简单的HTTP POST请求就可以轻松接入使用。</p>
			                <p>注：云片网为确保您的账户和信息安全，请在调用前进入后台添加您要使用的IP地址到IP白名单列表中</p>
			                <h3>2、统一请求格式</h3>
			                <p>BASE URL：</p>
			                <code>
			                    https://{type}.yunpian.com/{version}
			                </code>
			                <p>其中，{type}为业务类型，如短信为sms，语音为voice，流量为flow；{version}为版本号，如当前的版本号为v1</p>
			                <p>URL格式：</p>
			                <code>/{resource}/{function}.{format}</code>
			                <p class="info">说明：<br>
			                    {resource}为资源名，通常对应一类API<br>
			                    {function}为该资源提供的操作方法<br>
			                    {format}表示请求响应的结果格式<br>
			                    比如查账户信息的url为：<span>http://yunpian.com/v1/user/get.json</span>
			                    表示调用user的get方法，并且返回json格式的字符串。
			                    我们目前已经提供的接口，请参考API。
			                </p>
			                <p>HTTP头信息:</p>
			                <code>
			                    Accept:application/json;charset=utf-8;<br>
			                    Content-Type:application/x-www-form-urlencoded;charset=utf-8;
			                </code>
			                <p class="info">
			                    说明：<br>
			                    请求方式(Method)：统一用POST方式<br>
			                    编码：UTF-8<br>
			                    最佳实践：<br>
			                    &nbsp;•请使用发短信接口<br>
			                    &nbsp;&nbsp;&nbsp;&nbsp;可以自动匹配模板，开发更简单<br>
			                    &nbsp;•请设置好IP白名单<br>
			                    &nbsp;&nbsp;&nbsp;&nbsp;IP白名单可以有效提高账户安全性<br>
			                    &nbsp;•请补充完整您的账户资料信息<br>
			                    &nbsp;&nbsp;&nbsp;&nbsp;包括设置余额提醒阈值、联系人和紧急联系人的联系方式等账户信息，方便我们提供更好的服务<br>
			                </p>
			
			                <h3>5、提高发送速度建议</h3>
			                <p>
			                    1）使用长连接代替短连接，减少TCP连接建立过程中不必要的消耗。<br>
			                    2）推荐您使用批量发送或者multi_send接口，进一步降低网络消耗。<br>
			                    3）配置合适的线程数，避免网络拥塞导致您延迟增加。不同带宽下，推荐使用的连接类型和线程数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>客户端带宽</th>
			                            <th>连接类型</th>
			                            <th>客户端最优线程</th>
			                        </tr>
			                        <tr>
			                            <td>1M</td>
			                            <td>长连接</td>
			                            <td>10</td>
			                        </tr>
			                        <tr>
			                            <td>5M</td>
			                            <td>长连接</td>
			                            <td>100</td>
			                        </tr>
			                        <tr>
			                            <td>15M</td>
			                            <td>长连接</td>
			                            <td>150</td>
			                        </tr>
			                        <tr>
			                            <td>50M</td>
			                            <td>长连接</td>
			                            <td>200</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>注：如果您的发送速度达不到预期，可联系我们技术支持协助。</p>
			            </section>
			        </article>
			    </div>
			</div>
		</div>
	</div>
	<#include "/common/footer.ftl"/>
</body>
<script>
	//头部菜单选择
	service("api");
	api_service("explain");
</script>
</html>