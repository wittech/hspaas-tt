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
								<li> <a href="#"> 管理平台 </a> </li>
								<li> <a href="#">  短信管理 </a> </li>
								<li class="active"> 用户运行中通道管理 </li>
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
								    			<select id="keyword" name="keyword" class="form-control selectpicker show-tick" data-live-search="true">
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
								    			<span class="input-group-addon">用户名称</span>
								    			<select id="userId" name="userId" class="form-control selectpicker show-tick" data-live-search="true">
								    				<option value="">全部</option> 
								    				<#if userList??>
										    			<#list userList as u>
										    				<option value="${u.id!''}" <#if userId??><#if u.id==userId>selected</#if></#if> >${u.name!''}-${u.userName!''}</option>
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
                                <button class="btn btn-primary" onclick="loadingRedis();">重载redis</button>
                            </div>
                            <h3 class="panel-title">
                            <span>用户运行中通道列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>编号</th>
                                        <th>用户名称</th>
                                        <th>通道名称</th>
                                        <th>调用类型</th>
                                        <th>路由类型</th>
                                        <th>省份</th>
                                        <th>运营商</th>
                                        <th>协议类型</th>
                                        <th>通道代码</th>
                                        <th>操作时间</th>
                                        <th>操&nbsp;&nbsp;&nbsp;&nbsp;作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list page.list as pl>
                                    <tr>
                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>${(pl.userModel.name)!}-${(pl.userModel.username)!}</td>
                                        <td>
                                        <#if pl.status == 0>
                                            <span class="label label-success">使用中</span>
                                        <#else>
                                        	<span class="label label-danger">停用</span>
                                        </#if>&nbsp;${pl.passageIdText!''}
                                        </td>
                                        <td>
											<#if pl.callType==1>数据发送<#elseif pl.callType==2>状态报告网关推送<#elseif pl.callType==3>状态回执自取
											<#elseif pl.callType==4>短信上行推送<#elseif pl.callType==5>短信上行自取
											<#elseif pl.callType==6>通道余额查询<#else>未知</#if>
										</td>
										<td>
											<#if pl.routeType==0>默认路由<#elseif pl.routeType==1>验证码路由<#elseif pl.routeType==2>即时通知路由
											<#elseif pl.routeType==3>批量通知路由<#elseif pl.routeType==4>高风险投诉路由<#else>未知</#if>
										</td>
										<td>${pl.provinceName!''}</td>
                                        <td>
											<#if pl.cmcp==1>移动<#elseif pl.cmcp==2>电信<#elseif pl.cmcp==3>联通<#elseif pl.cmcp==4>全网<#else>未知</#if>
										</td>
										<td>${pl.protocol!''}</td>
										<td>${pl.passageCode!''}</td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td>
                                        	<a class="btn btn-primary btn-xs" href="${BASE_PATH}/sms/passage_access/edit?id=${pl.id}&groupId=${pl.groupId}&cmcp=${pl.cmcp}&routeType=${pl.routeType}"><i class="fa fa-edit"></i>&nbsp编辑</a>
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
			$('#myform').attr('action','${BASE_PATH}/sms/passage_access/index');
			$('#myform').submit();
		}
		
		function loadingRedis(){
            Boss.confirm("确定要重载redis运行中通道吗？",function(){
	            $.ajax({
	                url:'${BASE_PATH}/sms/passage_access/loadingRedis',
	                dataType:'json',
	                type:'post',
	                success:function(data){
	                    if(data){
                            Boss.alert('重载redis成功！');
	                    }else{
                            Boss.alert('重载redis失败！');
	                    }
	                },error:function(data){
                        Boss.alert('重载redis运行中通道异常！');
	                }
	            });
	        });
    	}
	</script>
</html>