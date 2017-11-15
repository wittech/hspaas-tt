<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心 -财务管理-充值账单</title>
<#include "/common/common.ftl"/>
<script src="${rc.contextPath}/assets/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<#include "/common/dialog.ftl"/>
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
<!--header-->	  
<#include "/common/navigation.ftl"/>
<!--//header-->	
<div class="content">
	<!--左侧side-->
	<#include "/common/sidebar.ftl"/>
		<!--//左侧side-->
		<!--右侧main-->
		<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="javascript:;;">财务管理</a></li>
					<li class="active"><a href="${rc.contextPath}/account/consumption">在线账单</a></li>
				</ul>
			</div>
            <div class="main_tab_tit">
				<ul>
				  <li class="active"><a href="${rc.contextPath}/account/consumption">充值账单</a></li>
				  <li><a href="${rc.contextPath}/account/recharge">在线充值</a></li>
				  <li><a href="${rc.contextPath}/product/index">产品报价</a></li>
				</ul>
			</div>

			<div class="bill_search">
				<form id="list_form" action="${rc.contextPath}/account/consumption_list">
					<div class="search_box">
						<div class="search app_search">
						  	<input type="text" class="input input-auto center_l" name="orderNo" size="35" placeholder="订单编号" />
				        	<input name="starDate" class="input input-auto center_l" placeholder="开始时间" value="${(start_date)!}" onclick="WdatePicker(
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
							<input type="button" class="bg-gray" id="search" value="搜索">
        					<input type="button" class="bg-green" id="export" value="导出EXCEL">
						</div>
					</div>
					<!--表格列表-->
					<div id="list_container"></div>
				</form>
			</div>
		</div>
		<!--右侧main-->
        

	</div>
</body>
</html>