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
								<li class="active">签名扩展号码管理</li>
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
								    			<span class="input-group-addon">签名</span>
								    			<input type="text" class="form-control" id="signature" name="signature" value="${signature!''}" placeholder="签名">
								    		</div>
								    	</div>
								    	<div class="col-md-4">
								    		<div class="input-group">
		                                        <span class="input-group-addon">所属用户</span>
		                                        <select class="form-control selectpicker show-tick" id="userId" name="userId" data-live-search="true">
						                        	<option value="-1">--选择用户--</option>
						                        	<#if userList??>
							    					<#list userList as p>
							    						<option value="${p.id!''}" <#if userId?? && userId==p.id>selected</#if>>${p.name!''}</option>
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
                            	<#if macro.doOper("2005001")>
                                	<button class="btn btn-primary" onclick="loadingRedis();">重载redis</button>
                                </#if>
                                <#if macro.doOper("2005002")>
                                	<a class="btn btn-success" href="${BASE_PATH}/sms/signature_extno/create">添加签名</a>
                            	</#if>
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
                                        <th>签名</th>
                                        <th>扩展号码</th>
                                        <th>备注</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#assign editCheck = macro.doOper("2005003") />
									<#assign deleteCheck = macro.doOper("2005004") />
                                	<#if page?? && page.list??>
                                	<#list page.list as pl>
                                    <tr>
                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>${(pl.userModel.name)!}-${(pl.userModel.username)!}</td>
                                        <td>${(pl.signature)!}</td>
                                        <td>${(pl.extNumber)!}</td>
                                        <td>${(pl.remark)!}</td>
                                        <td>
                                        	<#if editCheck>
                                        		<a class="btn btn-primary btn-xs" href="${BASE_PATH}/sms/signature_extno/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp编辑</a>
                                        	</#if>
                                        	<#if deleteCheck>
                                        		<a href="javascript:remove(${pl.id});" class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除&nbsp;&nbsp; </a>
                                        	</#if>
                                        </td>
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
		<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
		<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> 
		<script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> 
		<script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
	</body>
	<script type="text/javascript">
		function jumpPage(p){
			$('#pn').val(p);
			$('#myform').attr('action','${BASE_PATH}/sms/signature_extno/index');
			$('#myform').submit();
		}
		
		function remove(id){
            Boss.confirm('确定要删除该签名信息吗？',function(){
                $.ajax({
                    url:'${BASE_PATH}/sms/signature_extno/delete',
                    dataType:'json',
                    data:{
                        id :id
                    },
                    type:'post',
                    success:function(data){
                        if(data){
                            Boss.alertToCallback('删除成功！',function(){
                                location.href = "${BASE_PATH}/sms/signature_extno"
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
            Boss.confirm("确定要重载redis签名吗？",function(){
	            $.ajax({
	                url:'${BASE_PATH}/sms/signature_extno/loadingRedis',
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