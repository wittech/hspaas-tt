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
        <script src="${BASE_PATH}/resources/js/common.js"></script>
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
								<li class="active">公告管理</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
							    <div class="row" style="margin-top:5px">
							    	<form id="myform">
							    		<input type="hidden" name="pn" id="pn" value="1" />
								    	<div class="col-md-6">
								    		<div class="input-group">
								    			<span class="input-group-addon">关键字</span>
								    			<input type="text" class="form-control" id="inputGroupSuccess4" name="keyword" value="${keyword!''}" placeholder="标题/内容/姓名/手机号">
								    		</div>
								    	</div>
								    	<div class="col-md-4">
								    		<button class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</button>
                                            <a class="btn btn-default" onclick="commonClearForm();">重&nbsp;&nbsp;&nbsp;置</a>
								    	</div>
							    	</form>
							    </div>
							</div>
						</div>

						<div class="panel">
                        <div class="panel-heading">
                            <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
                                <a class="btn btn-success" href="${BASE_PATH}/base/notification_message/add">发送站内消息</a>
                            </div>
                            <h3 class="panel-title">
                            <span>消息列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>编号</th>
                                        <th>公告名称</th>
                                        <th>接收用户</th>
                                        <th>状态</th>
                                        <th>类型</th>
                                        <th>发布时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list page.list as pl>
                                    <tr>
                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>${pl.title!''}</td>
                                        <td>${(pl.user.name)!''}</td>
                                        <td>
										<#if pl.status == 0>
											未读
										<#else>
											已读
										</#if>
										</td>
										<td>
											<#if pl.type == 1>
												开户通知
											<#elseif pl.type == 2>
												充值通知
											<#elseif pl.type == 3>
												系统通知
											<#else>
												未知
											</#if>
										</td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td>
                                        	<a class="btn btn-primary btn-xs" href="${BASE_PATH}/base/notification_message/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	<#if pl.status == 1>
                                        		<a class="btn btn-danger btn-xs" href="javascript:deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除 </a>
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
	</body>
	<script type="text/javascript">
		function jumpPage(p){
			$('#pn').val(p);
			$('#myform').attr('action','${BASE_PATH}/base/notification_message/index');
			$('#myform').submit();
		}
		
		function deleteById(id){
            Boss.confirm("确定要删除该站内消息吗？",function(){
				$.ajax({
					url:'${BASE_PATH}/base/notification_message/delete',
					data:{'id':id},
					dataType:'json',
					type:'post',
					success:function(data){
						alert(data.message);
						if(data.result){
							location.reload();
						}
					},error:function(data){
						alert('删除站内消息异常！');
					}
				});
			});
		}
	</script>
</html>