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
								<li class="active">用户管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
							    
							    <form id="myform" method="post">
							    	<input type="hidden" id="pn" name="pn" value="1">
								    <div class="row" style="margin-top:5px">
								    	<div class="col-md-4">
								    		<div class="input-group">
								    			<span class="input-group-addon">登录名</span>
								    			<input type="text" class="form-control" id="keyword" name="keyword" value="${keyword!''}" placeholder="登录名查询">
								    		</div>
								    	</div>
								    	<div class="col-md-4">
								    		<button class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</button>
								    	</div>
								    </div>
							    </form>
							</div>
						</div>


						<div class="panel">
                        <div class="panel-heading">
							<#if macro.doOper("6001001")>
								<div class="pull-right"  style="margin-top: 10px;margin-right: 20px;">
									<button class="btn btn-success" onclick="location.href='${BASE_PATH}/boss/user/add'">新增用户</button>
								</div>
							</#if>
                            <h3 class="panel-title">
                            <span>用户列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                   <tr>
										<th>#</th>
										<th>登录名</th>
										<th>动态口令</th>
										<th>真实姓名</th>
										<th>手机号</th>
										<th>创建时间</th>
										<th>创建人</th>
										<th>操作</th>
						 			</tr>
                                </thead>
                                <tbody>

								<#assign deleteCheck = macro.doOper("6001003") />
								<#assign editCheck = macro.doOper("6001002") />
                                	<#list page.list as pl>
									<tr>
										<td>${(page.pageNumber - 1) * page.pageSize + (pl_index+1)}</td>	
										<td>${pl.login_name}</td>	
										<td>
										<#if pl.mfaQrcode??>
											<a href="javascript:void(0);" data-placement="bottom" data-html="true" class="btn btn-info btn-xs" data-toggle="popover" title="绑定MFA二维码"
		                                       data-content="<img src='data:image/png;base64,${(pl.mfaQrcode)!}' width='98px' height='98px'>">MFA二维码</a>
										</#if>
										</td>
										<td>${(pl.real_name)!'--'}</td>	
										<td>${(pl.mobile)!'--'}</td>	
										<td>${pl.created_at?string('yyyy-MM-dd HH:mm:ss')}</td>	
										<td>${(pl.created)!''}</td>	
										<td>
											<#if pl.super_flag == 0>
												<#if editCheck>
													<a class="btn btn-primary btn-xs" href="${BASE_PATH}/boss/user/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
													<#if pl.disabled_flag == 0>
														<a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="disabled(${pl.id},${pl.disabled_flag});"><i class="fa fa-lock"></i>&nbsp;
															禁用
														</a>
													<#else>
														<a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="disabled(${pl.id},${pl.disabled_flag});"><i class="fa fa-unlock-alt"></i>&nbsp;
															启用
														</a>
													</#if>
												</#if>
												<#if deleteCheck>
													<a class="btn btn-danger btn-xs" href="javascript:void(0);" onclick="deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除 </a>
												</#if>
											</#if>
											<a class="btn btn-success btn-xs" href="javascript:void(0);" onclick="updateMfa(${pl.id});"><i class="fa fa-refresh"></i>&nbsp;MFA重置 </a>
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
	
		$(function(){

	        $('a[data-toggle=popover]').mouseover(function () {
	            var $this = $(this);
	            if($this.attr('data-content') != ''){
	                $this.popover('show');
	            }
	        });
	
	        $('a[data-toggle=popover]').mouseout(function () {
	            var $this = $(this);
	            $this.popover('hide')
	        });
	        
	    });
	
		function jumpPage(pn){
			$('#pn').val(pn);
			$('#myform').attr('action','${BASE_PATH}/boss/user/index');
			$('#myform').submit();
		}
		
		function deleteById(id){
            Boss.confirm('确定要删除该用户吗？',function(){
				$.ajax({
					url:'${BASE_PATH}/boss/user/delete',
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
                        Boss.alert('删除用户请求失败！');
					}
				})
			});
		}
		
		function disabled(id,flag){
			var msg = "启用";
			if(flag == 0){
				flag = 1;
				msg = "禁用";
			}else{
				flag = 0;
				msg = '启用';
			}
            Boss.confirm('确定要'+msg+'该用户吗？',function(){
				$.ajax({
					url:'${BASE_PATH}/boss/user/disabled',
					data:{id:id,flag:flag},
					dataType:'json',
					type:'post',
					success:function(data){
                        Boss.alertToCallback(data.message,function(){
                            if(data.result){
                                location.reload();
                            }
						});

					},error:function(data){
                        Boss.alert('禁用用户请求失败！');
					}
				})
			});
		}
		
		function updateMfa(id){
            Boss.confirm('确定重置MFA动态口令吗？',function(){
				$.ajax({
					url:'${BASE_PATH}/boss/user/updateMfa',
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
                        Boss.alert('重置MFA请求失败！');
					}
				})
			});
		}
		
	</script>

</html>