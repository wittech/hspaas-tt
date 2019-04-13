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
        <#include "/WEB-INF/views/common/select_search.ftl">
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
								    			<span class="input-group-addon">模板名称</span>
								    			<input type="text" class="form-control" id="keyword" name="keyword" value="${keyword!''}" placeholder="模板名称">
								    		</div>
								    	</div>
								    	<div class="col-md-4">
								    		<div class="input-group">
		                                        <span class="input-group-addon">所属用户</span>
		                                        <select class="form-control selectpicker show-tick" id="userId" name="userId" data-live-search="true">
						                        	<option value="-1">--选择用户--</option>
						                        	<#if userList??>
							    					<#list userList as p>
							    						<option value="${p.userId!''}"  <#if userId?? && userId==p.userId>selected</#if>>${p.name!''}</option>
							    					</#list>
										    		</#if>
						                        </select>
		                                    </div>
		                                </div>
								    	<div class="col-md-3">
								    		<div class="input-group">
								    			<span class="input-group-addon">审批状态</span>
								    			<select id="status" name="status" class="form-control">
								    				<option value="">==全部==</option>
								    				<#if approveStatus??>
								    					<#list approveStatus as a>
								    						<option value="${a.value!''}" <#if status??><#if a.value == status>selected</#if></#if>>${a.title!''}</option>
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
                            	<#--
                            	<#if macro.doOper("10002001")>
                                	<button class="btn btn-primary" onclick="loadingRedis();">重载redis</button>
                                </#if>
                                <#if macro.doOper("10002002")>
                                	<a class="btn btn-success" href="${BASE_PATH}/mms/message_template/create">添加模板</a>
                            	</#if>
                            	 -->
                            </div>
                            <h3 class="panel-title">
                            <span>模板列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>序</th>
                                        <th>用户</th>
										<th>名称</th>
                                        <th>优先级</th>
                                        <th>来源</th>
                                        <th>路由</th>
                                        <th>状态</th>
                                        <th>提交时间</th>
                                        <th>通过时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
									<#assign editCheck = macro.doOper("10002001") />
                                	<#assign auditCheck = macro.doOper("10002002") />
									<#assign previewCheck = macro.doOper("10002003") />
									<#assign applyModelCheck = macro.doOper("10002004") />
									<#-- <#assign deleteCheck = macro.doOper("10002005") /> -->
                                	<#if page?? && page.list??>
                                	<#list page.list as pl>
                                    <tr>
                                        <td rowspan="2">${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>${(pl.userModel.name)!}-${(pl.userModel.username)!}</td>
										<td>${(pl.name)!}</td>
                                        <td>${(pl.priority)!}</td>
                                        <td>${(pl.apptypeText)!}</td>
                                        <td>${(pl.routeTypeText)!}</td>
                                        <td>
                                        	<#if pl.status==0>
                                        		<span class="label label-default">待审核</span>
                                        	<#elseif pl.status==1>
                                        		<span class="label label-mint">平台处理中</span>
                                        	<#elseif pl.status==2>
                                        		<span class="label label-success">审核成功</span>
                                        	<#elseif pl.status==3>
                                        		<span class="label label-danger">审核失败</span>
                                        	</#if>
                                        </td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td><#if pl.approveTime??>${pl.approveTime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
                                        <td>
                                        	<#if previewCheck>
                                        	    <a href="javascript:preview('${(pl.id)!}', '${(pl.title)!}');" class="btn btn-pink btn-xs"><i class="fa fa-video-camera"></i>&nbsp;预览</a>
                                        	</#if>
                                        	<#if pl.status==0>
                                        		<#if auditCheck>
	                                        		<a class="btn btn-mint btn-xs" href="${BASE_PATH}/mms/message_template/audit?id=${pl.id}"><i class="fa fa-lock"></i>&nbsp;审批 </a>
                                        		</#if>
                                        	</#if>
                                        	<#if editCheck>
												<a class="btn btn-info btn-xs" href="${BASE_PATH}/mms/message_template/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp编辑</a>
											</#if>
											<#if applyModelCheck>
												<a href="${BASE_PATH}/mms/message_template/apply?id=${pl.id}" class="btn btn-warning btn-xs"><i class="fa fa-star"></i>&nbsp;报备</a>
                                        	</#if>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td colspan="10" align="right"><b>标题：</b>${(pl.title)!}</td>
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
		</div>
		
		
		<div class="modal fade" id="previewModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">彩信内容</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="700" data-scrollcolor="#000" id="myPreviewModelBody">
                        
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div>
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
			$('#myform').attr('action','${BASE_PATH}/mms/message_template/index');
			$('#myform').submit();
		}
		
		function remove(id){
            Boss.confirm('确定要删除该彩信模板吗？',function(){
                $.ajax({
                    url:'${BASE_PATH}/mms/message_template/delete',
                    dataType:'json',
                    data:{
                        id :id
                    },
                    type:'post',
                    success:function(data){
                        if(data){
                            Boss.alertToCallback('删除成功！',function(){
                                location.href = "${BASE_PATH}/mms/message_template"
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
		
		function loadingRedis(){
            Boss.confirm("确定要重载redis模板吗？",function(){
	            $.ajax({
	                url:'${BASE_PATH}/mms/message_template/loadingRedis',
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
    	
    	function preview(templateId, title) {
	    	$.ajax({
	            url:'${BASE_PATH}/mms/message_template/preview',
	            data:{templateId : templateId, title: title},
	            dataType:'html',
	            type:'post',
	            success:function(data){
	                $("#myPreviewModelBody").html(data);
	            },error:function(data){
	                Boss.alert('请求失败！');
	            }
	        });
	    
	        $('#previewModal').modal('show');
	    }
	    
	    
	    function closeModal() {
	        $('#previewModal').modal('hide');
	    }
	</script>
</html>