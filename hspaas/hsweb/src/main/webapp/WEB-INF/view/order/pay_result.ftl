<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心 控制台 -充值完成</title>
<#include "/common/common.ftl"/>
</head>
<body>
<#include "/common/navigation.ftl"/>
<div class="content">
	<#include "/common/sidebar.ftl"/>
	<div class="main">
		<div class="breadcrumbs">
				<ul>
					<li><a href="">财务管理</a></li>
					<li class="active"><a href="javascript:void(0)">在线充值</a></li>
				</ul>
			</div>
            <div class="main_tab_tit">
				<ul>
				  <li><a href="${rc.contextPath}/account/consumption">充值账单</a></li>
				  <li><a href="${rc.contextPath}/account/recharge">在线充值</a></li>
				  <li class="active"><a href="${rc.contextPath}/product/index">产品报价</a></li>
				</ul>
			</div>
            
            <div class="recharge_step">
              <ul>
                <li class="current"><i class="num1">&nbsp;</i>选择套餐包</li>
                <li class="current"><i class="num2">&nbsp;</i>选择支付方式</li>
                <li class="current"><i class="num3">&nbsp;</i>进行付款</li>
                <li class="current"><i class="num4">&nbsp;</i>充值完成</li>
              </ul>
            </div>

			<div class="recharge_form">
				<#if payResult?? && payResult>
					订单号：${(orderNo)!} 支付成功。
					<a href="${rc.contextPath}/order/consumption">查看订单详情</a>&nbsp;&nbsp;
					<a href="${rc.contextPath}/account/information">查看当前余额</a>
				<#else>
					订单号：${(orderNo)!} 支付失败。
				</#if>
            </div>
		</div>
	<!--右侧main-->
</div>
<div id="alipay_content"></div>
<script type="text/javascript">

$(function(){

});
</script>
</body>
</html>