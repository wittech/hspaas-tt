<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-账户API</title>
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
			        <h2 class="page-title">账户API</h2>
			        <article class="doc">
			            <section class="one">
			                <h3 id="a1">1、查账户信息</h3>
			                <code>
			                    URL：https://sms.yunpian.com/v2/user/get.json
			                </code>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>参数名</th>
			                            <th>类型</th>
			                            <th>是否必须</th>
			                            <th>描述</th>
			                            <th>示例</th>
			                        </tr>
			                        <tr>
			                            <td>apikey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户唯一标识</td>
			                            <td>9b11127a9701975c734b8aee81ee3526</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>调用成功的返回值示例：</p>
			                <pre><code>{
							    "nick": "zhangshan",
							    "gmt_created": "2012-09-11 15:14:00",
							    "mobile": "13812341234",
							    "email": "zhangshan@company.com",
							    "ip_whitelist": null,                 //IP白名单，推荐使用
							    "api_version": "v1",                  //api版本号
							    "balance": 1000,                         //短信剩余条数
							    "alarm_balance": 100,                   //剩余条数低于该值时提醒
							    "emergency_contact": "张三",           //紧急联系人
							    "emergency_mobile": "13812341234"     //紧急联系人电话
							}</code></pre>
			                <h3 id="a2">2、修改账户信息</h3>
			                <code>
			                    URL：https://sms.yunpian.com/v2/user/set.json
			                </code>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>参数名</th>
			                            <th>类型</th>
			                            <th>是否必须</th>
			                            <th>描述</th>
			                            <th>示例</th>
			                        </tr>
			                        <tr>
			                            <td>apikey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户唯一标识</td>
			                            <td>9b11127a9701975c734b8aee81ee3526</td>
			                        </tr>
			                        <tr>
			                            <td>emergency_contact</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>紧急联系人</td>
			                            <td>zhangshan</td>
			                        </tr>
			                        <tr>
			                            <td>emergency_mobile</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>紧急联系人手机号</td>
			                            <td>13012345678</td>
			                        </tr>
			                        <tr>
			                            <td>alarm_balance</td>
			                            <td>Long</td>
			                            <td>否</td>
			                            <td>短信余额提醒阈值。<br>一天只提示一次</td>
			                            <td>100</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>修改时，可一次修改emergency_contact、emergency_mobile和alarm_balance中的一个或多个(必须传入一个),调用成功的返回值示例：</p>
			                <pre><code>{
							    "nick": "zhangsan",
							    "gmt_created": "2012-09-11 15:14:00",
							    "mobile": "13812341234",
							    "email": "zhangsan@company.com",
							    "ip_whitelist": null,                 //IP白名单，推荐使用
							    "api_version": "v1",                  //api版本号
							    "balance": 1000,                      //短信剩余条数
							    "alarm_balance": 100,                 //剩余条数低于该值时提醒
							    "emergency_contact": "张三",           //紧急联系人
							    "emergency_mobile": "13812341234"     //紧急联系人电话
							}</code></pre>
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
	api_service("account");
</script>
</html>