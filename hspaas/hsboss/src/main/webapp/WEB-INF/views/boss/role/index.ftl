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
								<li class="active">角色管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                        <div class="panel-heading">
                            <div class="pull-right"  style="margin-top: 10px;margin-right: 20px;">
                                <button class="btn btn-success" onclick="location.href='${BASE_PATH}/boss/role/add'">新增角色</button>
                            </div>
                            <h3 class="panel-title">
                            <span>角色列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                   <tr>
										<th>#</th>
										<th>角色名称</th>
										<th>创建时间</th>
										<th>创建用户</th>
										<th>操作</th>
						 			</tr>
                                </thead>
                                <tbody>
                                
                                	<#list page.list as pl>
									<tr>
										<td>${(page.pageNumber - 1) * page.pageSize + (pl_index+1)}</td>	
										<td>${pl.role_name}</td>	
										<td>${pl.created_at?string('yyyy-MM-dd HH:mm:ss')}</td>	
										<td>${pl.created}</td>	
										<td>
											<a class="btn btn-primary btn-xs" href="${BASE_PATH}/boss/role/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
											<a class="btn btn-danger btn-xs" href="javascript:void(0);" onclick="deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除 </a>
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
	</body>
	<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
	<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
	<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
	<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
	<script type="text/javascript">
	
		function jumpPage(pn){
			location.href = "${BASE_PATH}/boss/role/index?pn="+pn;
		}
		
		function deleteById(id){
            Boss.confirm('确定要删除该角色吗？',function(){
				$.ajax({
					url:'${BASE_PATH}/boss/role/delete',
					data:{id:id},
					dataType:'json',
					type:'post',
					success:function(data){
                        Boss.alertToCallback(data.message,function(){
                            if(data.result){
                                location.reload();
                            }
						});
					},error:function(data){
                        Boss.alert('删除角色请求失败！');
					}
				})
			});
		}
		
	</script>

</html>