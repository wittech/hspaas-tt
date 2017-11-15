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
								<li> <a href="#">  短信管理 </a> </li>
								<li class="active"> 通道轮训控制管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
								<form id="myform">
									<input type="hidden" name="pn" id="pn" value="1"/>
								    <div class="row" style="margin-top:5px">
								    	<div class="col-md-4">
								    		<div class="input-group">
								    			<span class="input-group-addon">通道名称</span>
								    			<select id="keyword" name="keyword" class="form-control">
								    				<option value="">全部</option>
								    				<#if passageList??>
								    					<#list passageList as p>
								    						<option value="${p.id!''}" 
								    							<#if keyword??><#if p.id == keyword>selected</#if></#if>
								    						>${p.name!''}</option>
								    					</#list>
								    				</#if>
								    			</select>
								    		</div>
							    		</div>
								    	<div class="col-md-4">
								    		<div class="input-group">
								    			<span class="input-group-addon">状态</span>
								    			<select id="status" name="status" class="form-control">
								    				<option value="">全部</option> 
								    				<#if passageStatus??>
								    					<#list passageStatus as a>
								    						<option value="${a.value!''}" 
								    							<#if status??><#if a.value == status>selected</#if></#if>
								    						>${a.title!''}</option>
								    					</#list>
								    				</#if>
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
                            <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
                                <a class="btn btn-success" href="${BASE_PATH}/sms/passage_control/create">添加轮训控制</a>
                            </div>
                            <h3 class="panel-title">
                            <span>通道轮训控制列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>编号</th>
                                        <th>通道名称</th>
                                        <th>状&nbsp;&nbsp;&nbsp;&nbsp;态</th>
                                        <th>轮训表达式</th>
                                        <th>创建时间</th>
                                        <th>操&nbsp;&nbsp;&nbsp;&nbsp;作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list page.list as pl>
                                    <tr>
                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>${pl.passageIdText!''}</td>
                                        <td>
										<#if pl.status==0>启用<#elseif pl.status==1>停用<#elseif pl.status==2><#else>未知</#if>
										</td>
                                        <td>${pl.cron!''}</td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td>
                                        	<a class="btn btn-primary btn-xs" href="${BASE_PATH}/sms/passage_control/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp编辑</a>
                                        	<#if pl.status==0>
                                        		<a class="btn btn-default btn-xs" href="javascript:updateStatus(${pl.id},1);"><i class="fa fa-lock"></i>&nbsp;停用 </a>
                                        	<#else>
	                                        	<a class="btn btn-default btn-xs" href="javascript:updateStatus(${pl.id},0);"><i class="fa fa-lock"></i>&nbsp;启用</a>
                                        	</#if>
                                        	<a href="javascript:remove(${pl.id});" class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除 </a>
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
		<form id="updateForm" class="form-horizontal">
	          <input type="hidden" name="smsPassageControl.id" id="controlById"/>
	          <input type="hidden" name="smsPassageControl.status" id="statusById"/>
        </form>
		<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
		<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
	</body>
	<script type="text/javascript">
		function jumpPage(p){
			$('#pn').val(p);
			$('#myform').attr('action','${BASE_PATH}/sms/passage_control/index');
			$('#myform').submit();
		}
		
		function updateStatus(id,status){
			$("#controlById").val(id);
			$("#statusById").val(status);
			$.ajax({
	  			url:'${BASE_PATH}/sms/passage_control/updateStatus',
	  			dataType:'json',
	  			data:$('#updateForm').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data==1){
	  					location.href = "${BASE_PATH}/sms/passage_control"
	  				}else{
                        Boss.alert('提交失败！');
	  				}
	  			},error:function(data){
                    Boss.alert('系统异常!请稍后重试！');
	  			}
	  		});
		}
		
		function remove(id){
			$.ajax({
	  			url:'${BASE_PATH}/sms/passage_control/delete',
	  			dataType:'json',
	  			data:{
	  				id :id
	  			},
	  			type:'post',
	  			success:function(data){
	  				if(data){
                        Boss.alertToCallback('删除成功！',function(){
                            location.href = "${BASE_PATH}/sms/passage_control"
						});
	  				}else{
                        Boss.alert('删除失败！');
	  				}
	  			},error:function(data){
                    Boss.alert('系统异常!请稍后重试！');
	  			}
	  		});
		}
	</script>
</html>