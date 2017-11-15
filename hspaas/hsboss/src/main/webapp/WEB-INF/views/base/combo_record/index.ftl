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
		<link href="${BASE_PATH}/resources/css/bootstrap/pace.min.css" rel="stylesheet">
		<script src="${BASE_PATH}/resources/js/bootstrap/pace.min.js"></script>
        <script src="${BASE_PATH}/resources/js/My97DatePicker/WdatePicker.js"></script>
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
								<li> <a href="#"> 基础信息 </a> </li>
								<li class="active"> 套餐管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
								<form id="myform" method="post">
									<input type="hidden" name="pn" value="1" id="pn">
								<div class="row">
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">关键字</span>
							    			<input type="text" class="form-control" name="keyword" value="${keyword!''}" placeholder="客户名/订单号">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">开始时间</span>
                                            <input type="text" class="form-control" style="background: #fff" name="start" readonly onClick="WdatePicker()" id="start" value="${start!''}" placeholder="选择开始时间">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">截至时间</span>
                                            <input type="text" class="form-control" style="background: #fff" name="end" readonly onClick="WdatePicker()" id="end" value="${end!''}" placeholder="选择截至时间">
							    		</div>
							    	</div>
							    </div>
							    
							    <div class="row" style="margin-top:5px">
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">订单状态</span>
							    			<select name="status" class="form-control">
												<option value="-1">==订单状态==</option>
												<#list traderOrderStatus as ts>
												    <option value="${ts.getValue()}" <#if ts.getValue() == status>selected</#if>>${ts.getTitle()}</option>
												</#list>
											</select>

										</div>
							    	</div>
							    	<div class="col-md-4">
							    		<a class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</a>
                                        <a class="btn btn-default" onclick="commonClearForm();">重&nbsp;&nbsp;&nbsp;置</a>
							    	</div>
							    </div>
                                </form>
							</div>
						</div>


						<div class="panel">
                        <div class="panel-heading">
                            <h3 class="panel-title">
                            <span>套餐购买记录</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>编号</th>
                                        <th>订单号</th>
                                        <th>客户名</th>
                                        <th>订单类型</th>
                                        <th>产品名</th>
                                        <th>付款方式</th>
                                        <th>状态</th>
                                        <th>创建时间</th>
                                    </tr>
                                </thead>
                                <tbody>
								<#list page.list as pl>
                                    <tr>
                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>${pl.orderNo}</td>
                                        <td>${(pl.userFullName)!'--'}</td>
                                        <td>
											<#if pl.tradeType == 0>
                                                产品购买
											<#elseif pl.tradeType == 1>
                                                站内账户金额充值
											<#else>
												未知
											</#if>
										</td>
                                        <td>${pl.productName!''}</td>
                                        <td>
											<#if pl.payType == 1>
                                                账户转账
											<#elseif pl.payType == 2>
                                                线下转账
											<#elseif pl.payType == 3>
                                                支付宝
											<#elseif pl.payType == 4>
                                                微信支付
											<#else>
                                                未知
											</#if>
										</td>
                                        <td>
										<#if pl.status == 0>
											待支付
										<#elseif pl.status == 1>
                                            支付完成，待处理
										<#elseif pl.status == 2>
                                            支付失败
										<#elseif pl.status == 3>
                                            已竣工
										<#elseif pl.status == 4>
                                            处理失败
										<#else>
                                            未知
										</#if>
										</td>
                                        <td>
											${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}
                                        </td>
                                    </tr>
								</#list>
                                </tbody>
                            </table>
                            <nav style="margin-top:-15px">
							${(page.showPageHtml)!}
                            </nav>
                        </div>
                    </div>
				</div>
				<#include "/WEB-INF/views/main/left.ftl">
			</div>

		</div>
		<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
		<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
	</body>
<script type="text/javascript">

	function jumpPage(p){
		$('#pn').val(p);
		$('#myform').attr('action','${BASE_PATH}/base/combo_record');
		$('#myform').submit();
	}

</script>

</html>