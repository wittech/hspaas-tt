<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-贡献代码</title>
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
				<div class="content doc">
			        <div class="divTab tab-content7" style="display: block;">
			            <h2 class="page-title">我要贡献代码</h2>
			            <p>
			                为方便广大开发者更快、更好的使用云片的短信服务，云片推出开发者激励计划，鼓励开发者贡献云片API的接入代码，同时自己也能获得优惠。
			            </p>
			
			            <h3>一、代码贡献原则：</h3>
			            <p>1、正确运行</p>
			            <p>2、包含测试CASE</p>
			            <p>3、不与官方和其他开发者提交的代码雷同</p>
			
			            <h3>二、github代码贡献的流程：</h3>
			            <p>1、提交pull request贡献代码。<br>
			                代码库地址：
			                <a href="https://github.com/yunpian/sms/tree/master/thirdparty" target="_blank">https://github.com/yunpian/sms/tree/master/thirdparty</a><br>
			                提交注意事项：
			                <a href="https://github.com/yunpian/sms/blob/master/README.md" target="_blank">https://github.com/yunpian/sms/blob/master/README.md</a>
			            </p>
			            <p>2、等待云片技术人员审核和合并。云片技术人员收到你的pull request后，会进行代码审核，审核通过的代码进行merge request合并</p>
			
			            <h3>三、激励计划：（根据您的贡献大小我们可提供以下三种不同等级的奖励）</h3>
			            <p>1、一次获得云片赠送的短信1000条</p>
			            <p>2、价格优惠，按你的发送量申请定制价格</p>
			            <p>3、收入分成，如果你的代码被多次使用，可以申请收入分成</p>
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
</script>
</html>