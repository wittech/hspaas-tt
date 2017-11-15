<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心</title>
<#include "/common/common.ftl"/>
<script type="text/javascript">
$(function(){
	Pagination.go();
	$("#search").click(function(){
		Pagination.go();
	});
	loadMenu('li_dx','dx_send','dx_template');
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
				<li><a href="javascript:void(0)">短信管理</a></li>
                <li class="active"><a href="${rc.contextPath}/sms/template/index">短信模板</a></li>
			</ul>
		</div>
        <div class="main_tab_tit">
				<ul>
					<li class="active"><a href="${rc.contextPath}/sms/template/index">短信模板</a></li>
					<li><a href="${rc.contextPath}/sms/record/send/create">短信发送</a></li>
				</ul>
		</div>
		<form id="list_form" action="${rc.contextPath}/sms/template/list" method="post">
			
			<div class="search_box">
				<div class="search app_search">
				    <label>模板内容：</label><input type="text" placeholder="关键词" name="content" />
                    <label class="l3"> 审批状态：</label> 
                    <select name="status" style="height:34px; vertical-align:middle">
                        <option value="">全部状态</option>
                        <#if approveStatus??>
                        <#list approveStatus as p>
                        <option value="${p.value}">${p.title}</option>
                        </#list>
                        </#if>
                    </select>
					<input type="button" class="bg-gray" id="search" value="搜索">
        			<input type="button" class="bg-green" id="export" value="导出EXCEL">
				</div>
				<div class="search_link"><a href="${rc.contextPath}/sms/template/create">添加模板</a></div>
			</div>
	
			<!--表格列表-->
			<div id="list_container"></div>
		</form>
		
	</div>
	<!--右侧main-->
</div>
</body>
</html>