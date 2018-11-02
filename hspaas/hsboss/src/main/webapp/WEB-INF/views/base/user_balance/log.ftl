<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8" />
    <meta charset="utf-8">

    <title>融合平台</title>
    <link href="${BASE_PATH}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/style.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/js/confirm/jquery-confirm.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/font-awesome.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/jquery.tagit.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/pace.min.css" rel="stylesheet">
    <script src="${BASE_PATH}/resources/js/bootstrap/pace.min.js"></script>
    <script src="${BASE_PATH}/resources/js/common.js"></script>
</head>

<body>
<div id="container" class="effect mainnav-lg navbar-fixed mainnav-fixed">
<#include "/WEB-INF/views/main/top.ftl">
    <div class="boxed">
	    <div id="content-container">
	    <div class="pageheader">
	        <div class="breadcrumb-wrapper"> <span class="label">所在位置:</span>
	            <ol class="breadcrumb">
	                <li> <a href="#"> 管理平台 </a> </li>
	                <li> <a href="#"> 系统管理 </a> </li>
	                 <li> <a href="${BASE_PATH}/base/customer/index"> 客户基础信息</a> </li>
	                <li> <a href="${BASE_PATH}/base/user_balance/index"> 用户账户余额</a> </li>
	                <li class="active">冲扣值日志</li>
	            </ol>
	        </div>
	    </div>
	    <div id="page-content">
		<div class="panel">
			<div class="panel-heading">
                        <div class="pull-right"  style="margin-top: 10px;margin-right: 20px;">
                        </div>
                        <h3 class="panel-title">
                            <span>冲扣值日志</span>
                        </h3>

                    </div>
		    <div class="panel-body">
		    	<form id="myform">
					<input type="hidden" name="pn" id="pn" value="1" />
					<input type="hidden" name="userId" id="userId" value="${(userId)!''}" />
					<input type="hidden" name="platformType" id="platformType" value="${(platformType)!''}" />
			    </form>
		        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
		            <thead>
		            <tr>
		                <th>编号</th>
						<th>单价</th>
						<th>总价</th>
						<th>客户名称</th>
						<th>充值额度</th>
						<th>付费类型</th>
						<th>冲扣备注</th>
						<th>创建时间</th>
		            </tr>
		            </thead>
		            <tbody>
		            <#if page?? && page.list?size gt 0>
			            <#list page.list as pl>
						<tr>
							<td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
							<td>${(pl.price)!''}</td>
							<td>${(pl.totalPrice)!''}</td>
							<td>${(pl.userModel.name)!''}-${(pl.userModel.username)!''}</td>
							<td>${(pl.balance)!''}</td>
							<td>
								<#if pl.payType == 1>
									账户转账
								<#elseif pl.payType == 2>
									 线下转账
								<#elseif pl.payType == 3>
									 支付宝
								<#elseif pl.payType == 4>
									微信支付																				  微信支付
								<#else>
																													未知
								</#if>
							</td>
							<td>${pl.remark!''} </td>
							<td>
							<#if pl.createTime??>
								${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}
							<#else>
								--
							</#if>
							</td>
						</tr>
						</#list>
					<#else>
						<tr style="text-align:center;">
							<td colspan="8">
								暂无数据!
							</td>
						</tr>
					</#if>
					</tbody>
				</table>
				<nav style="margin-top:-15px">
				${(page.showPageHtml)!}
				</nav>
		    </div></div>
	</div>
	<#include "/WEB-INF/views/main/left.ftl">
	</div></div></div>
</body>
	<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
	<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
	<#include "/WEB-INF/views/common/form_validation.ftl">
	<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
	<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
		<script type="text/javascript">
			function jumpPage(p){
				$('#pn').val(p);
				$('#myform').attr('action','${BASE_PATH}/base/user_balance/log');
				$('#myform').submit();
			}
	</script>
</html>