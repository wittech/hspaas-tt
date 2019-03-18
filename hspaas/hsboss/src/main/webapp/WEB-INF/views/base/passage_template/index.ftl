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
								<li> <a href="#">  基础信息 </a> </li>
								<li class="active"> 通道模板管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
								<form id="myform" method="post">
									<input type="hidden" name="pn" id="pn" value="1"/>
								    <div class="row" style="margin-top:5px">
								    	<div class="col-md-4">
								    		<div class="input-group">
								    			<span class="input-group-addon">模板名称</span>
								    			<input type="text" class="form-control" id="keyword" name="keyword" value="${keyword!''}" placeholder="输入模板名称...">
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
                        	<#if macro.doOper("1004001001")>
	                            <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
	                                <a class="btn btn-success" href="${BASE_PATH}/base/passage_template/add">添加通道模板</a>
	                            </div>
                            </#if>
                            <h3 class="panel-title">
                            <span>通道模板列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>编号</th>
                                        <th>模板名称</th>
                                        <th>协议类型</th>
                                        <th>通道模板类型</th>
                                        <th>创建时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#assign deleteCheck = macro.doOper("1004001003") />
									<#assign editCheck = macro.doOper("1004001002") />
                                	<#list page.list as pl>
                                    <tr>
                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>
										${(pl.name)!''}
										</td>
										<td>${pl.protocol}</td>
										<td>
											<#if pl.passageType == 1>
												短信通道模板
											<#elseif pl.passageType == 2>
												流量通道模板
											<#elseif pl.passageType == 3>
												语音通道模板
											<#elseif pl.passageType == 4>
												彩信通道模板
											</#if>
										</td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td>
                                        	<#if editCheck>
                                        		<a class="btn btn-primary btn-xs" href="${BASE_PATH}/base/passage_template/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	</#if>
                                        	<#if editCheck && deleteCheck>
                                        	|
                                        	</#if>
                                        	<#if deleteCheck>
                                        		<a class="btn btn-danger btn-xs" href="javascript:void(0);" onclick="deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除</a>
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
			$('#myform').attr('action','${BASE_PATH}/base/passage_template/index');
			$('#myform').submit();
		}
		
		function deleteById(id){
            Boss.confirm("确定要删除该通道模板吗？",function(){
				$.ajax({
					url:'${BASE_PATH}/base/passage_template/delete',
					type:'post',
					dataType:'json',
					data:{id:id},
					success:function(data){
                        Boss.alertToCallback(data.message,function(){
                            if(data.result){
                                location.reload();
                            }
						});
					},error:function(data){
                        Boss.alert("删除通道模板失败！");
					}
				});
			});
		}
	</script>
</html>