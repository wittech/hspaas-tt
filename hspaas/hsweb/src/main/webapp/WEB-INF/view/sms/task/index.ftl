<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心</title>
<#include "/common/common.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script src="${rc.contextPath}/assets/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<script type="text/javascript">
$(function(){
	Pagination.go();
	$("#search").click(function(){
		Pagination.go();
	});
	loadMenu('li_dx','dx_send','dx_task');
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
                <li class="active"><a href="${rc.contextPath}/sms/task/index">短信任务</a></li>
			</ul>
		</div>
		<form id="list_form" action="${rc.contextPath}/sms/task/list">
			<div class="search_box">
				<div class="search app_search">
				  	<input type="text" class="input input-auto center_l" name="sid" size="20" placeholder="请输入sid" />
				  	<input type="text" class="input input-auto center_l" name="content" size="20" placeholder="请输入短信内容" />
		        	<input id="starDate" name="starDate" class="input input-auto center_l" placeholder="开始时间" value="${(start_date)!}" onclick="WdatePicker(
		                               {errDealMode : 1,
		                               minDate:'${(min_date)!}',
		                               maxDate:'',
		                               isShowClear:false,
		                               readOnly:true});" value="${(start_date)!}" readonly="readonly" size="15" />
		            <input id="endDate" name="endDate" class="input input-auto center_l" placeholder="截止时间" value="" onclick="WdatePicker(
		                               {errDealMode : 1,
		                               minDate:'${(min_date)!}',
		                               maxDate:'',
		                               isShowClear:false,
		                               readOnly:true});" value="${(stop_date)!}" readonly="readonly" size="15" />
		            <span class="text-main" style="margin-left:10px;">温馨提示：系统目前只支持近3个月内的记录查询!</span>
					<input type="button" class="bg-gray" id="search" value="搜索">
					<#--
        			<input type="button" class="bg-green" id="export" value="导出EXCEL">
        			-->
				</div>
			</div>
			<!--表格列表-->
			<div id="list_container"></div>
		</form>
	</div>
	<!--右侧main-->
</div>
</body>
</html>