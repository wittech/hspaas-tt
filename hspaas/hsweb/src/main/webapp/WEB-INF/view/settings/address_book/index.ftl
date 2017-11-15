<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心 系统管理 -地址管理</title>
<#include "/common/common.ftl"/>
<script src="${rc.contextPath}/assets/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	Pagination.go();
	$("#search").click(function(){
		Pagination.go();
	});
	loadMenu('li_settings','settings','settings_address_book_list');
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
				<li><a href="javascript:void(0)">系统管理</a></li>
                <li class="active"><a href="${rc.contextPath}/settings/address_book/index">地址管理</a></li>
			</ul>
		</div>
		<form id="list_form" action="${rc.contextPath}/settings/address_book/list">
			<div class="search_box">
				<div class="search app_search">
				  	<input type="text" class="input input-auto center_l" name="mobile" size="20" placeholder="手机号码" />
		        	<input name="startDate" class="input input-auto center_l" placeholder="开始时间" value="${(start_date)!}" onclick="WdatePicker(
		                               {errDealMode : 1,
		                               minDate:'${(min_date)!}',
		                               maxDate:'${(stop_date)!}',
		                               isShowClear:false,
		                               readOnly:true});" value="${(start_date)!}" readonly="readonly" size="15" />
		            <input name="endDate" class="input input-auto center_l" placeholder="截止时间" value="${(stop_date)!}" onclick="WdatePicker(
		                               {errDealMode : 1,
		                               minDate:'${(min_date)!}',
		                               maxDate:'${(stop_date)!}',
		                               isShowClear:false,
		                               readOnly:true});" value="${(stop_date)!}" readonly="readonly" size="15" />
		            <span class="text-main" style="margin-left:10px;">温馨提示：系统目前只支持近3个月内的记录查询!</span>
					<input type="button" class="bg-gray" id="search" value="搜索">
        			<input type="button" class="bg-green" id="export" value="导出EXCEL">
        			<input type="button" class="bg-green" id="export" onclick="back_jump();" value="添加">
				</div>
			</div>
			<!--表格列表-->
			<div id="list_container"></div>
		</form>
		
	</div>
	<!--右侧main-->
</div>
<script type="text/javascript">
	function back_jump(){
		location.href = "${rc.contextPath}/settings/address_book/create";
	}
	
	function remove(id){
		if (confirm("确认要删除？")) {
			$.ajax({
				url : "${rc.contextPath}/settings/address_book/delete",
				dataType:"json",
				data:{
					id:id
				},
				type : "post",
				beforeSend : function(){
				},
				success:function(data){
					if(data>0){
						Hs.alert("删除成功！");
						Pagination.go();
					}else{
						Hs.alert("删除失败！");
					}
				}
			});
		}
	}
</script>
</body>
</html>