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
								<li class="active"> 公告管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<#--
						<div class="panel">
							<div class="panel-body">
							    <div class="row" style="margin-top:5px">
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">查询条件</span>
							    			<input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">查询条件</span>
							    			<input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<button class="btn btn-primary">查&nbsp;&nbsp;&nbsp;询</button>
							    	</div>
							    </div>
							</div>
						</div>
						-->

						<div class="panel">
                        <div class="panel-heading">
                        	<#if macro.doOper("1002001001")>
	                            <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
	                                <a class="btn btn-success" href="${BASE_PATH}/base/notification/add">发布公告</a>
	                            </div>
	                        </#if>    
                            <h3 class="panel-title">
                            <span>公告列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>编号</th>
                                        <th>公告名称</th>
                                        <th>状态</th>
                                        <th>类型</th>
                                        <th>浏览量</th>
                                        <th>发布时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#assign deleteCheck = macro.doOper("1002001004") />
									<#assign editCheck = macro.doOper("1002001002") />
									<#assign disabledCheck = macro.doOper("1002001003") />
                                	<#list page.list as pl>
                                    <tr>
                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>${pl.title!''}</td>
                                        <td>
										<#if pl.status == 0>
											有效
										<#else>
											无效
										</#if>
										</td>
										<td>
											<#if pl.type == 1>
												系统公告
											<#elseif pl.type == 2>
												活动公告
											<#else>
												未知
											</#if>
										</td>
                                        <td>${pl.hits!0}</td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td>
                                        	<#if editCheck>
                                        		<a class="btn btn-primary btn-xs" href="${BASE_PATH}/base/notification/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	</#if>
                                        	<#if pl.status == 1>
                                        		<#if deleteCheck>
                                        			<a class="btn btn-danger btn-xs" href="javascript:deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除 </a>
                                        		</#if>
                                        		<#if disabledCheck>
                                        			<a class="btn btn-default btn-xs" href="javascript:disabled(${pl.id},0);"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
                                        		</#if>
                                        	<#else>
                                        		<#if disabledCheck>
                                        			<a class="btn btn-default btn-xs" href="javascript:disabled(${pl.id},1);"><i class="fa fa-lock"></i>&nbsp;禁用 </a>
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
	</body>
	<script type="text/javascript">
		function jumpPage(p){
			location.href = '${BASE_PATH}/base/notification/index?pn='+p;
		}
		
		function disabled(id,flag){
			var msg = flag == 0 ? '启用' : '禁用';
			Boss.confirm("确定要"+msg+"该公告吗？",function(){
                $.ajax({
                    url:'${BASE_PATH}/base/notification/disabled',
                    data:{'id':id,'flag':flag},
                    dataType:'json',
                    type:'post',
                    success:function(data){
                        Boss.alertToCallback(data.message,function(){
                            if(data.result){
                                location.reload();
                            }
                        });
                    },error:function(data){
                        Boss.alert(msg+'公告异常！');
                    }
                });
			});
		}
		
		function deleteById(id){
			Boss.confirm("确定要删除该公告吗？",function(){
				$.ajax({
					url:'${BASE_PATH}/base/notification/delete',
					data:{'id':id},
					dataType:'json',
					type:'post',
					success:function(data){
						Boss.alertToCallback(data.message,function(){
                            if(data.result){
                                location.reload();
                            }
						});

					},error:function(data){
						Boss.alert('删除公告异常！');
					}
				});
			});
		}
	</script>
</html>