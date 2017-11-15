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
								<li class="active">模板管理</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
								<form id="myform" method="post">
									<input type="hidden" name="pn" id="pn" value="1" />
								    <div class="row" style="margin-top:5px">
								    	<div class="col-md-3">
								    		<div class="input-group">
								    			<span class="input-group-addon">模板内容</span>
								    			<input type="text" class="form-control" id="keyword" name="keyword" value="${keyword!''}" placeholder="模板内容">
								    		</div>
								    	</div>
								    	<div class="col-md-4">
		                                    <div class="input-group">
		                                        <span class="input-group-addon">所属用户</span>
		                                        <input type="text" class="form-control" id="username" name="username"
		                                               value="${username!''}" readonly style="background: #fff"
		                                               placeholder="选择用户">
		                                        <input type="hidden" name="userId" id="userId" value="${userId!''}"/>
		                                        <span class="input-group-btn">
		                                            <button class="btn btn-info" type="button"
		                                                    onclick="openUserList();">选择</button>
		                                        </span>
		                                    </div>
		                                </div>
								    	<div class="col-md-3">
								    		<div class="input-group">
								    			<span class="input-group-addon">审批状态</span>
								    			<select id="status" name="status" class="form-control">
								    				<option value="">==全部==</option>
								    				<#if approveStatus??>
								    					<#list approveStatus as a>
								    						<option value="${a.value!''}" 
								    							<#if status??><#if a.value == status>selected</#if></#if>
								    						>${a.title!''}</option>
								    					</#list>
								    				</#if>
								    			</select>
								    		</div>
							    		</div>
								    	<div class="col-md-2">
								    		<a class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</a>
                                            <a class="btn btn-default" onclick="commonClearForm();">重&nbsp;&nbsp;&nbsp;置</a>
								    	</div>
								    </div>
							    </form>
							</div>
						</div>

						<div class="panel">
                        <div class="panel-heading">
                            <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
                                <button class="btn btn-primary" onclick="loadingRedis();">重载redis</button>
                                <a class="btn btn-success" href="${BASE_PATH}/sms/message_template/create">添加模板</a>
                            </div>
                            <h3 class="panel-title">
                            <span>模板列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>编号</th>
                                        <th>用户信息</th>
                                        <th>优先级</th>
                                        <th>操作类型</th>
                                        <th>路由类型</th>
                                        <th>状态</th>
                                        <th>备注</th>
                                        <th>提交时间</th>
                                        <th>通过时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#if page?? && page.list??>
                                	<#list page.list as pl>
                                    <tr>
                                        <td rowspan="2">${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>${(pl.userModel.name)!}-${(pl.userModel.username)!}</td>
                                        <td>${(pl.priority)!}</td>
                                        <td>${(pl.apptypeText)!}</td>
                                        <td>${(pl.routeTypeText)!}</td>
                                        <td><#if pl.status==0>待审核<#elseif pl.status==1>审核成功<#elseif pl.status==2>审核失败</#if></td>
                                        <td>${(pl.remark)!}</td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td><#if pl.approveTime??>${pl.approveTime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
                                        <td>
                                        	<#if pl.status==1>
                                        	<#else>
	                                        	<a class="btn btn-default btn-xs" href="${BASE_PATH}/sms/message_template/audit?id=${pl.id}"><i class="fa fa-lock"></i>&nbsp;审批 </a>
                                        	</#if>
                                        	<a class="btn btn-primary btn-xs" href="${BASE_PATH}/sms/message_template/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp编辑</a>
                                        	<a href="javascript:remove(${pl.id});" class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除</a>
                                        	<a href="${BASE_PATH}/sms/message_template/matching?id=${pl.id}" class="btn btn-success btn-xs"><i class="fa fa-tags"></i>&nbsp;测试</a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="10" align="right"><b>模板内容：</b>${(pl.content)!}</td>
                                    </tr>
                                    </#list>
                                    </#if>
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
			
			<div class="modal fade" id="userModal">
	            <div class="modal-dialog" style="width:850px">
	                <div class="modal-content" style="width:850px">
	                    <div class="modal-header">
	                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
	                                aria-hidden="true">&times;</span></button>
	                        <h4 class="modal-title">选择用户</h4>
	                    </div>
	                    <div class="modal-body" id="userModelBody">
	
	                    </div>
	                    <div class="modal-footer">
	                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	                        <button type="button" class="btn btn-success" onclick="clearUser();">清空</button>
	                    </div>
	                </div><!-- /.modal-content -->
	            </div><!-- /.modal-dialog -->
	        </div><!-- /.modal -->

		</div>
		<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
				<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
	</body>
	<script type="text/javascript">
		function jumpPage(p){
			$('#pn').val(p);
			$('#myform').attr('action','${BASE_PATH}/sms/message_template/index');
			$('#myform').submit();
		}
		
		function remove(id){
            Boss.confirm('确定要删除该短信模板吗？',function(){
                $.ajax({
                    url:'${BASE_PATH}/sms/message_template/delete',
                    dataType:'json',
                    data:{
                        id :id
                    },
                    type:'post',
                    success:function(data){
                        if(data){
                            Boss.alertToCallback('删除成功！',function(){
                                location.href = "${BASE_PATH}/sms/message_template"
							});
                        }else{
                            Boss.alert('删除失败！');
                        }
                    },error:function(data){
                        Boss.alert('系统异常!请稍后重试！');
                    }
                });
			});
		}
		
		function openUserList() {
	        var userId = $('#userId').val();
	        $.ajax({
	            url: '${BASE_PATH}/base/customer/commonUserList',
	            dataType: 'html',
	            type: 'POST',
	            data: {userId: userId},
	            success: function (data) {
	                $('#userModelBody').html(data);
	                $('#userModal').modal('show');
	            }, error: function (data) {
	                Boss.alert('请求用户列表异常！');
	            }
	        });
	    }
	    
	    function selectUser(userId, fullName, mobile) {
	        $('#userId').val(userId);
	        $('#username').val(fullName);
	        $('#userModal').modal('hide');
	    }
	    
	    function userJumpPage(p){
	        $('#userpn').val(p);
	        var userId = $('#userId').val();
	        $.ajax({
	            url:'${BASE_PATH}/base/customer/commonUserList?userId='+userId,
	            dataType:'html',
	            data:$('#userform').serialize(),
	            type:'POST',
	            success:function(data){
	                $('#userModelBody').html(data);
	            },error:function(data){
	                Boss.alert('请求用户列表异常！');
	            }
	        });
	    }
		
		function loadingRedis(){
            Boss.confirm("确定要重载redis模板吗？",function(){
	            $.ajax({
	                url:'${BASE_PATH}/sms/message_template/loadingRedis',
	                dataType:'json',
	                type:'post',
	                success:function(data){
	                    if(data){
                            Boss.alert('重载redis成功！');
	                    }else{
                            Boss.alert('重载redis失败！');
	                    }
	                },error:function(data){
                        Boss.alert('重载redis模板异常！');
	                }
	            });
	        });
    	}
	</script>
</html>