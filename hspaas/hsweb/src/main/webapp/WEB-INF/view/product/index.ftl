<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心 控制台 -产品报价</title>
<#include "/common/common.ftl"/>
<script type="text/javascript">
$(function(){
	Pagination.go();
	$("#search").click(function(){
		Pagination.go();
	});
	loadMenu('li_finance','financial','recharge');
});
</script>
</head>
<body>
<#include "/common/navigation.ftl"/>
<div class="content">
	<#include "/common/sidebar.ftl"/>
	<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="javascript:;;">财务管理</a></li>
					<li class="active"><a href="${rc.contextPath}/product/index">产品报价</a></li>
				</ul>
			</div>
            <div class="main_tab_tit">
				<ul>
				  <li><a href="${rc.contextPath}/account/consumption">充值账单</a></li>
				  <li><a href="${rc.contextPath}/account/recharge">在线充值</a></li>
				  <li class="active"><a href="${rc.contextPath}/product/index">产品报价</a></li>
				</ul>
			</div>
		<form id="list_form" action="${rc.contextPath}/product/list">
			<!--表格列表-->
			<div id="list_container"></div>
		</form>
		
	</div>
	<!--右侧main-->
</div>
</body>
</html>