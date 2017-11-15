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
					<li class="active"><a href="javascript:void(0)">短信管理</a></li>
				</ul>
			</div>
            
            <div class="main_tab_tit">
				<ul>
					<li class="active" onclick="">短信模板</li>
					<li onclick="">短信测试</li>
				</ul>
			</div>

			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>1、涉及房产、贷款、移民、成人用品、政治、色情、暴力、赌博以及其他违法信息不能发送。</p>
				<p>2、含有病毒、恶意代码、色情、反动等不良信息或有害信息</p>
				<p>3、冒充任何人或机构，或以虚伪不实的的方式谎称或使人误认为与任何人或任何机构有关。</p>
				<p>4、侵犯他人著作权或其他知识产权、或违反保密、雇佣或不披露协议披露他人商业秘密或保密信息。</p>
                <p>5、粗话、脏话等不文明的内容; 让短信接收者难以理解的内容。</p>
                <p>6、主题不明确的模板，如：您好#content#,亲爱的用户#content#</p>
                <p>7、营销、广告类的短信不能发送-这类短信为：通过一些方式（打折，促销等）吸引客户过来参与一些活动，或购买一些产品或服务。</p>
                <p>8、验证码类的短信模板，请务必在获取短信验证之前加 图片验证码。</p>
			</div>
			<!--说明state_box eof-->

			<!--搜索-->
			<div class="search_box">
				<div class="search app_search">
					  <form id="appform" name="appform" action="" method="post">

						<input type="text" placeholder="关键词" name="" value="">
                        <select style="height:34px; vertical-align:middle">
                            <option>审批状态</option>
                        </select>
						<input type="button" class="bg-yellow" id="search" value="搜索">
            			<input type="button" class="bg-green" id="export" value="导出EXCEL">
					</form>




				</div>
				<div class="search_link"><a href="">添加模板</a></div>
			</div>
			<!--//搜索-->

			<!--表格列表-->
			<div class="table_box">
				<table cellpadding="0" cellspacing="0" border="0">
					<thead>
						<tr class="even">
							<th>序号</th>
							<th>签名</th>
							<th>模板内容</th>
                            <th>审核状态</th>
							<th>未通过原因</th>
							<th>匹配模板</th>
						</tr>
					</thead>
					<tbody>
					     
			          
		              	<tr>
		              		<td>1</td>
		              		<td>华时</td>
		              		<td width="240">您的业务已受理完毕，将于次月1号生效，感谢使用！</td>
                            <td>待审核</td>
		              		<td>信息不符</td>
                            <td>&nbsp;</td>
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