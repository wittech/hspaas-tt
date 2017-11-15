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
                        <li class="active">用户账户余额 </li>
                    </ol>
                </div>
            </div>
            <div id="page-content">

            <div class="panel">
                <div class="panel-body">
				    <div class="row" style="margin-top:5px">
				    	<form id="myform">
				    		<input type="hidden" name="pn" id="pn" value="1" />
				    		<input type="hidden" name="userId" id="userId" value="${userId!'0'}" />
				    	</form>
				    </div>
				</div>
            </div>

                <div class="panel">
                    <div class="panel-heading">
                        <div class="pull-right"  style="margin-top: 10px;margin-right: 20px;">
                        </div>
                        <h3 class="panel-title">
                            <span>账户余额列表</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>编号</th>
                                <th>客户名称</th>
                                <th>业务类型</th>
                                <th>金额/条数</th>
                                <th>付费类型</th>
                                <th>告警阀值</th>
                                <th>状态</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list page.list as pl>
                            <tr>
                                <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>
                                	${(pl.name)!''}
                                </td>
                                <td>
                                    <#if pl.type == 1>
                                       	短信
                                    <#elseif pl.type == 2>
                                        流量
                                    <#elseif pl.type == 3>
                                        语音
                                   	<#elseif pl.type == 0>
                                        默认
                                    </#if>
                                </td>
                                <td>
                                   ${pl.balance!''}
                                </td>
                                <td>
                                	<#if pl.payType == 1>
                                		预付
                                	<#else>
                                		后付
                                	</#if>
                                </td>
                                <td>
                                	${pl.threshold!''}
                                </td>
                                <td>
                                	<#if pl.status == 0>
                                		<span class="label label-success">正常</span>
                                	<#else>
                                		<span class="label label-danger">告警中</span>
                                	</#if>
                                </td>
                                <td>
                                <#if pl.createTime??>
                                    ${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}
                                <#else>
                                    --
                                </#if>
                                </td>
                                <td>
                                	<a class="btn btn-success btn-xs" href="${BASE_PATH}/base/user_balance/edit?id=${pl.id}">冲扣值 </a>&nbsp;
                                	<a class="btn btn-danger  btn-xs" href="${BASE_PATH}/base/user_balance/warning?id=${pl.id}">告警设置 </a>&nbsp;
                                    <#if pl.status == 0>
                                        <a class="btn btn-warning btn-xs" href="javascript:void(0);" onclick="updateStatus(${pl.id},'1');">禁止告警</a>&nbsp;
                                    <#else>
                                        <a class="btn btn-warning btn-xs" href="javascript:void(0);" onclick="updateStatus(${pl.id},'0');">恢复监管 </a>&nbsp;
                                    </#if>
                                    &nbsp;
                                    <a class="btn btn-default btn-xs" href="${BASE_PATH}/base/user_balance/log?userId=${pl.userId}&platformType=${(pl.type)!''}">冲扣值日志</a>
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
	<div class="modal fade" id="myModal">
	  <div class="modal-dialog" style="width:850px">
	    <div class="modal-content" style="width:850px">
	      <div class="modal-header">
	        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
	        <h4 class="modal-title">冲扣值日志</h4>
	      </div>
	      <div class="modal-body" id="myModelBody">
	        	
	      </div>
	      <div class="modal-footer">
	        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
	      </div>
	    </div><!-- /.modal-content -->
	  </div><!-- /.modal-dialog -->
	</div><!-- /.modal -->
</body>
	<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
	<#include "/WEB-INF/views/common/form_validation.ftl">
	<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
	<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
	<script type="text/javascript">
			function jumpPage(p){
				$('#pn').val(p);
				$('#myform').attr('action','${BASE_PATH}/base/user_balance/index');
				$('#myform').submit();
			}
			
			function openBalanceLog(userId,platformType){
				$.ajax({
					url:'${BASE_PATH}/base/user_balance/log',
					dataType:'html',
					type:'POST',
					data:{userId:userId,platformType:platformType},
					success:function(data){
						$('#myModelBody').html(data);
						$('#myModal').modal('show');
					},error:function(data){
                        Boss.alert('请求用户列表异常！');
					}
				});
			}
			
			function updateStatus(id,status){
	            var msg  = "确定要恢复该用户余额告警吗？";
	            if(status == 1){
	                msg = "确定要禁用该用户余额告警吗？";
	            }
	            Boss.confirm(msg ,function(){
	                $.ajax({
	                    url:'${BASE_PATH}/base/user_balance/update_status',
	                    type:'post',
	                    dataType:'json',
	                    data:{id:id,status:status},
	                    success:function(data){
	                        Boss.alertToCallback(data.message,function(){
	                            if(data.result){
	                                location.reload();
	                            }
	                        });
	                    },error:function(data){
	                        Boss.alert("操作失败！");
	                    }
	                });
	            });
	        }
	</script>
</html>