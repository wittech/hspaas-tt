<!DOCTYPE html>
<html lang="zh-cn">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>华时融合平台管理中心</title>
	<link href="${rc.contextPath}/assets/css/css.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${rc.contextPath}/assets/js/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="${rc.contextPath}/assets/js/common.js"></script>
</head>
<body>
<#include "/common/header.ftl"/>
<div class="content">
	<#include "/common/sidebar.ftl"/>	    
      
   <!--右侧main-->
		<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="">产品管理</a></li>
					<li class="active"><a href="javascript:void(0)">应用管理</a></li>
				</ul>
			</div>

			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>1、未上线应用只可用测试号码白名单列表的号码测试</p>
				<p>2、上线应用前请先完成身份认证，可选个人开发者、企业开发者认证，个人开发者认证可升级为企业开发者。</p>
				<p>3、企业开发者可通过控制台［财务管理］申请企业增值税发票</p>
				<p>4、可为每个应用配置［可用余额］</p>
			</div>
			<!--说明state_box eof-->

			<!--搜索-->
			<div class="search_box">
				<div class="search app_search">
					  <form id="appform" name="appform" action="" method="post">

						<input type="text" placeholder="应用名称" name="" value="">
						<input type="button" class="bg-main" id="search" value="搜索">
					</form>




				</div>
				<div class="search_link"><a href="">创建应用</a></div>
			</div>
			<!--//搜索-->

			<!--表格列表-->
			<div class="table_box">
				<table cellpadding="0" cellspacing="0" border="0">
					<thead>
						<tr class="even">
							<th>应用名称</th>
							<th>应用类型</th>
							<th>可用余额</th>
							<th>创建时间</th>
							<th>状态</th>
							<th>操作</th>
						</tr>
					</thead>
					<tbody>
						 
						
				      	<tr>
				      	<td>杭州华时科技</td>
				      	<td>网站</td>
				      	<td>￥800</td>
				      	<td>2015-11-26</td>
				      	<td>已上线</td>
                        <td><a href="">充值</a> <a href="">管理</a> <a href="">删除</a></td>
				      	</tr>
				      
					</tbody>
				</table>
			</div>
			 <div class="pagenum">
	<span>共<i>0</i>条记录 每页显示15条 当前1/0页</span>
</div>

		<!--弹层(充值) bof-->
		<div class="background_box">&nbsp;</div>
       	 <div class="float_box app_recharge_box" id="recharge_box" style="left: 424.5px; top: 46.5px;">
          <div class="float_tit">
            <h3>&nbsp;&nbsp;应用充值</h3>
            <span class="close"></span>
          </div>
            <div class="float_ctn">
              <form method="post" name="msgForm" id="msgForm">
              <div class="float_field">
	              <dl>
	              	<dt><input type="radio" id="yzx" name="app_recharge" checked="checked" value="yzx"></dt>
	              	<dd>
						与主账户共享（默认）
	              	</dd>
	              </dl>
              </div>
              <div class="float_field">
	              <dl>
	              	<dt><input type="radio" id="app" name="app_recharge" value="app" class="setting_radio"></dt>
	              	<dd>
						独立设置
	              	</dd>
	              	<dd class="setting">
	              		<dl>
			              <dt>分配额度</dt><dd><input type="text" name="appBalance.balance" id="charge">
			              <input type="hidden" name="appBalance.appSid" id="chargeAppSid">
			              <em>元</em>
			              <span id="charge_error" class="error" style="display:none;"></span></dd>
			              <dd><span class="tips">设定的余额不可超过主账户余额，当应用可用余额为0时自动停止服务。当前剩余可用余额：10.0</span></dd>
			              </dl>
	              	</dd>
	              </dl>
              </div>
              <div class="float_btn">
                <input type="button" value="取消" class="cancel_btn">
                <input type="button" value="确 定" class="confirm_btn" id="rechargeSubt">
              </div>
              </form>
            </div>
          </div>

			
			<!--//表格列表-->
		</div>
		<!--右侧main-->
        
        

	 
</div>
</body>
</html>