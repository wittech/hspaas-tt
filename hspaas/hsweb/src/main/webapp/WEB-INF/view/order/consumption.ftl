<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心</title>
<#include "/common/common.ftl"/>
<script src="${rc.contextPath}/assets/js/My97DatePicker/WdatePicker.js" type="text/javascript"></script>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	Pagination.go();
	$("#search").click(function(){
		Pagination.go();
	});
	loadMenu('li_finance','financial','consumption_list');
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
					<li><a href="">财务管理</a></li>
					<li class="active"><a href="${base}/order/consumption">消费帐单</a></li>
				</ul>
			</div>
            
            <div class="main_tab_tit">
				<ul>
				<#--
				  <li onclick="" class="active">消费记录</li>
                  <li onclick="" class="">月结账单</li>
                  -->
				</ul>
			</div>
			<div class="bill_search">
				<form id="list_form" action="${rc.contextPath}/order/consumption_list">
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
				            <#if statusValues??>
								<select name="status" class="select">
									<option value="">订单状态</option>
									<#list statusValues as s>
										<option value="${(s.value)!}">${(s.title)!}</option>
									</#list>
								</select>
							</#if>
							 <#if payValues??>
								<select name="payType" class="select">
									<option value="">支付方式</option>
									<#list payValues as p>
										<option value="${(p.value)!}">${(p.title)!}</option>
									</#list>
								</select>
							</#if>
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
<script>
	function invoiceList(id){
      	  $.ajax({
    			url:"/order/invoice",
    			type:"post",
    			data:"orderId="+id,
    			success: function (data) {
    				if(data !=null){
    						var html = "发票抬头：&nbsp;&nbsp;"+data.title+"<br/>收&nbsp;&nbsp;件&nbsp;&nbsp;人：&nbsp;&nbsp;"+data.contactName+
    						"<br/>联系方式：&nbsp;&nbsp;"+data.contactPhone+"<br/>时&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;间：&nbsp;&nbsp;"+data.createTimeText+
    						"<br/>收货地址：&nbsp;"+data.address;
							Hs.alert(html);
    				}else{
    					Hs.alert("系统异常！请稍后重试！");
    				}
              }
    	 });
	}
</script>
</html>