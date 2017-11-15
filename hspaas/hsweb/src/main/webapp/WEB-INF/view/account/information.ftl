<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心 -财务管理-账户信息</title>
<#include "/common/common.ftl"/>
<script type="text/javascript">
$(function(){
	Pagination.go();
	$("#search").click(function(){
		Pagination.go();
	});
	loadMenu('li_finance','financial','information');
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
					<li class="active"><a href="${rc.contextPath}/account/information">账户信息</a></li>
				</ul>
			</div>
            <div class="main_tab_tit">
				<ul>
				  <li onclick="" class="active">账户信息</li>
				</ul>
			</div>
			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>1、配置余额不足提醒可在余额降低到一定阀值进行短信提醒，请留意余额不足导致的应用停止服务</p>
			</div>
			<!--说明state_box eof-->
			<!--内容列表-->
			<div class="item_box account_info">
				<div class="item_left">
					<div class="box">
						<h1>
							我的余额					
						</h1>
						<ul>
							<li>
								<label>账户余额：</label><span>￥${(userAccount.money)!}元</span>
							</li>
							<li>
								<label>短信余额：</label><span>
								<#if userbalance??>
									<#list userbalance as b>
										<#if b.type=="1">
											${(b.balance)!}
										</#if>
									</#list>
								</#if>
								条
								</span>
							</li>
							<li>
								<label>语音余额：</label><span>
									<#if userbalance??>
										<#list userbalance as b>
											<#if b.type=="3">
												${(b.balance)!}
											</#if>
										</#list>
									</#if>
								分钟
								</span>
							</li>
							<li>
								<label>流量余额：</label><span>￥
									<#if userbalance??>
										<#list userbalance as b>
											<#if b.type=="2">
												${(b.balance)!}
											</#if>
										</#list>
									</#if>
								元
								</span>
							</li>
						</ul>
					</div>
				</div>
				<div class="item_right" style="width: 371px;">
					<div class="box">
						<h1>资费套餐</h1>
						<div>
                        	<table width="100%" border="0" align="center"  cellpadding="0" cellspacing="0">
                                <tbody>
                                 	<tr>
	                                  <th height="45">套餐</th>
	                                  <th>单价</th>
	                                  <th>操作</th>
                                	</tr>
	                                <#if comboList??>
		                                <#list comboList as c>
		                                	 <tr>
	                                          <td align="center">${(c.name)!}</td>
	                                          <td align="center">${(c.sellMoney)!}元</td>
	                                          <td align="center"><a href="${rc.contextPath}/order/index/${(c.id)!}">购买</a></td>
	                                        </tr>
		                                </#list>
		                            <#else>
		                            	<tr><td colspan = "2" style="text-align: center;">暂无记录！</td></tr>
		                            </#if>
                                  </tbody>
                              </table>
                        </div>
					</div>
				</div>
			</div>
			<!--//内容列表-->
		</div>
		<!--右侧main-->
	</div>
</body>
</html>