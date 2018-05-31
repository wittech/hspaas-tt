<!DOCTYPE html>

<html>
<head>
	<meta charset="utf-8">
	<title>控制台 - 华时融合平台</title>
	<meta name="renderer" content="webkit">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta name="apple-mobile-web-app-status-bar-style" content="black">
	<meta name="apple-mobile-web-app-capable" content="yes">
	<meta name="format-detection" content="telephone=no">

	<link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
	<link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
	<link rel="stylesheet" href="${rc.contextPath}/static/build/css/app.css" media="all">
	<link rel="stylesheet" href="${rc.contextPath}/static/css/global2.css" media="all">
	<link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body class="kit-theme">
<div class="layui-layout layui-layout-admin kit-layout-admin">
	<#include "/common/header.ftl"/>

	<#-- 菜单栏 -->
	<#include "/common/sidebar.ftl"/>
	
	<div class="layui-body" id="container">
		<!-- 内容主体区域 -->
		<div style="padding: 15px;">数据加载中,请稍等...</div>
	</div>

	<#include "/common/footer.ftl"/>

	<!--锁屏模板 start-->
	<script type="text/template" id="lock-temp">
		<div class="admin-header-lock" id="lock-box">
			<div class="admin-header-lock-img">
				<img id="headPortrait2" src="${rc.contextPath}/static/images/0.jpg"/>
			</div>
			<div class="admin-header-lock-name" id="lockUserName"></div>
			<input type="text" class="admin-header-lock-input" value="输入密码解锁.." name="lockPwd" id="lockPwd" />
			<button class="layui-btn layui-btn-sm" id="unlock">解锁</button>
		</div>
	</script>
	<!--锁屏模板 end -->
</div>
<script src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script src="${rc.contextPath}/static/plugins/layui2/layui.js?v=2.2.2"></script>
<script src="${rc.contextPath}/static/js/index/index2.js?v=20171204"></script>
</body>

</html>