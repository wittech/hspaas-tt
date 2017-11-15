<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心 财务管理-发票管理</title>
<#include "/common/common.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	Pagination.go();
	$("#search").click(function(){
		Pagination.go();
	});
	loadMenu('li_finance','financial','financial_balance_list');
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
				<li><a href="javascript://">财务管理</a></li>
                <li class="active"><a href="${rc.contextPath}/finance/balance/index">发票管理</a></li>
			</ul>
		</div>
		 <div class="state_box">
		      <p>* 申请开票金额1000元以上可开具增值税专票，开票金额100元以上可开具增值税普票（国税发票）</p>
		      <p>* 官方活动赠送金额不计算在开票金额内</p>
		</div>
		<div class="table_box invoice_box">
			<form id="action_form">
				<div class="table_relate relate_invoice">
				   <ul>
				      <li class="li2">
				        <dl><dt>可开票金额：</dt>
				            <dd><span class="green1">￥
				            <#if invoiceBalance??>
				            	<#if invoiceBalance.money??>
				            		${(invoiceBalance.money)!}
				            	<#else>
				            		0
				            	</#if>
				            <#else>
				            	0
				            </#if>
				            </span>元<span class="tips">* 通过活动兑换码充值进账户及已充值的保证金无法开具发票</span></dd>
				        </dl>
				        <dl class="relate_link"><a href="javascript:invoice();">开具发票</a></dl>
				       </li>
				    </ul>
				</div>
			</form>
		</div>
		<form id="list_form" action="${rc.contextPath}/finance/balance/list">
				<!--表格列表-->
				<div id="list_container"></div>
		</form>
	</div>
	<!--右侧main-->
</div>
	<div class="showbox">
		<h2>邮寄详情<a class="close" href="#">关闭</a></h2>
		<div class="mainlist">
			<p>邮件类型：<span id="charge_type">暂无信息</span>&nbsp;&nbsp;</p>
			<p>邮件费用：<span id="charge_money">暂无信息</span>元&nbsp;&nbsp;</p>
			<p>快递单号：<span id="tracking_number">暂无信息</span></p>
			<p>快递公司：<span id="express">暂无信息</span></p>
			<p><span id="memo">经过多年多的经验累积和创新发展，网站还进一步明确站长素材网是一个致力于提供优质免费素材浏览与下载的交流和学习平台，网站专注于为各类用户提供PSD素材，矢量素材，图标素材，图片素材，字体素材等各类素材，旨在让每一位素材需求者都能够轻松找到自己想要的素材。</span></p>
		</div>
	</div>
	<div id="zhezhao"></div>
<script type="text/javascript">
	function invoice(){
		$.ajax({
	    	url : "/finance/balance/get",
	    	dataType:"json",
			data:$('#action_form').serialize(),
			type : "post",
	    	success : function(data){
	    		if(data){
	    			location.href ="${rc.contextPath}/finance/balance/create";
	    		}else{
	    			Hs.alert("没有可开取发票余额!");
	    		}
	    	}
	   	});
	}
	
	function alertDetail(trackingNumber,express,memo,chargeType,chargeMoney){
		var box =300;
		var th= $(window).scrollTop()+$(window).height()/1.6-box;
		var h =document.body.clientHeight;
		var rw =$(window).width()/2-box;
		$(".showbox").animate({top:th,opacity:'show',width:600,height:340,right:rw},500);
		$("#zhezhao").css({
			display:"block",height:$(document).height()
		});
		if(trackingNumber !="" && trackingNumber !=null && trackingNumber !=undefined){
			$("#tracking_number").html(trackingNumber);
		}
		if(express !="" && express !=null && express !=undefined){
			$("#express").html(express);
		}
		if(memo !="" && memo !=null && memo !=undefined){
			$("#memo").html(memo);
		}
		$("#charge_type").html(chargeType);
		$("#charge_money").html(chargeMoney);
	}
	
	$(".showbox .close").click(function(){
		$(this).parents(".showbox").animate({top:0,opacity: 'hide',width:0,height:0,right:0},500);
		$("#zhezhao").css("display","none");
	});
</script>
</body>
</html>