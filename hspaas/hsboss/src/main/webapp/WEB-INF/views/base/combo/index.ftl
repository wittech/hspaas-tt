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
								<li> <a href="#"> 套餐管理 </a> </li>
								<li class="active"> 套餐管理</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<form id="myform" method="post">
							<input type="hidden" name="pn" id="pn" value="1">
								<div class="panel-body">
									<div class="row">
								    	<div class="col-md-3">
								    		<div class="input-group">
								    			<span class="input-group-addon">套餐名称</span>
								    			<input type="text" class="form-control" id="name" name="name" value="${name!''}" placeholder="输入套餐名称查询">
								    		</div>
								    	</div>
								    	<div class="col-md-3">
								    		<div class="input-group">
								    			<span class="input-group-addon">开始时间</span>
								    			<input type="text" class="form-control" id="start" name="start" onClick="WdatePicker()" value="${start!''}" placeholder="输入开始时间查询">
								    		</div>
								    	</div>
								    	<div class="col-md-3">
								    		<div class="input-group">
								    			<span class="input-group-addon">截至时间</span>
								    			<input type="text" class="form-control" id="end" name="end" onClick="WdatePicker()" value="${end!''}" placeholder="输入截至时间查询">
								    		</div>
								    	</div>
								    	<div class="col-md-2">
								    		<button class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</button>
                                            <a class="btn btn-default" onclick="commonClearForm();">重&nbsp;&nbsp;&nbsp;置</a>
								    	</div>
								    </div>
							</form>
							</div>
						</div>


						<div class="panel">
                        <div class="panel-heading">
                        	<#if macro.doOper("1003001002")>
	                            <div class="pull-right"  style="margin-top: 10px;margin-right: 20px;">
	                                <a class="btn btn-success" href="${BASE_PATH}/base/combo/add">添加套餐</a>
	                            </div>
                            </#if>
                            <h3 class="panel-title">
                            <span>套餐列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>套餐名称</th>
                                        <th>原价</th>
                                        <th>售价</th>
                                        <th>时间段</th>
                                        <th>创建时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
									<#assign editCheck = macro.doOper("1003001003") />
									<#assign disabledCheck = macro.doOper("1003001005") />
                                	<#list page.list as pl>
                                    <tr>
                                        <td>${pl.name}</td>
                                        <td>${pl.originalMoney}</td>
                                        <td>${pl.sellMoney}</td>
                                        <td>
                                        <#if pl.isInTime == 0>
                                        	长期有效
                                        <#else>
                                        	${pl.startTime?string('yyyy-MM-dd')}至${pl.endTime?string('yyyy-MM-dd')}
                                        </#if>
                                        </td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td>
                                        	<#if editCheck>
	                                        	<#if pl.status == 1>
	                                        		<a class="btn btn-primary btn-xs" href="${BASE_PATH}/base/combo/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
	                                        	</#if>
                                        	</#if>
                                        	<#--
                                        		<a class="btn btn-danger btn-xs" href="javascript:void(0);" onclick="deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除 </a>
                                        	-->
                                        	<#if disabledCheck>
	                                        	<#if pl.status == 0>
	                                        		<a class="btn btn-default btn-xs" href="javascript:disabled(${pl.id},1)"><i class="fa fa-lock"></i>&nbsp;下线 </a>
	                                        	<#else>
	                                        		<a class="btn btn-default btn-xs" href="javascript:disabled(${pl.id},0)"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
	                                        	</#if>
                                        	</#if>
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
		<script type="text/javascript">
		
			function jumpPage(p){
				$('#pn').val(p);
				$('#myform').attr('action','${BASE_PATH}/base/combo/index');
				$('#myform').submit();
			}
		
			function disabled(id,flag){
				var msg = flag == 1 ? "下线" : "启用";
				Boss.confirm("确定要"+msg+"该套餐吗？",function(){
					$.ajax({
						url:'${BASE_PATH}/base/combo/disabled',
						dataType:'json',
						type:'post',
						data:{id:id,flag:flag},
						success:function(data){
                            Boss.alertToCallback(data.message,function(){
                                if(data.result){
                                    location.reload();
                                }
							});

						},error:function(data){
                            Boss.alert('操作异常！');
						}
					});
				});
			}
		</script>
	</body>

</html>