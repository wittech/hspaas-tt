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
                        <li class="active"> 黑白名单管理 </li>
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
	                                <span class="input-group-addon">手机号码</span>
	                                <input type="text" class="form-control" name="keyword" id="keyword" value="${(keyword)!}" placeholder="手机号码">
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
                        <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
                        	<#if macro.doOper("2004001001")>
                           	 	<button class="btn btn-primary" onclick="loadingRedis();">重载redis</button>
                            </#if>
                            <#if macro.doOper("2004001002")>
                            	<a class="btn btn-success" href="${BASE_PATH}/sms/black_list/add">新增黑名单</a>
                            </#if>
                        </div>
                        <h3 class="panel-title">
                            <span>黑名单列表</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>编号</th>
                                <#--<th>客户名称</th>-->
                                <th>手机号码</th>
                                <th>类型</th>
                                <th>备注</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#assign deleteCheck = macro.doOper("2004001003") />
                            <#list page.list as pl>
                            <tr>
                                <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>
                                   ${pl.mobile!''}
                                </td>
                                <td>${pl.typeText!''}</td>
                                <td>${pl.remark!''}</td>
                                <td>
                                <#if pl.createTime??>
                                    ${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}
                                <#else>
                                    --
                                </#if>
                                </td>
                                <td>
                                	<#if deleteCheck>
                                    	<a class="btn btn-danger btn-xs" href="javascript:void(0);" onclick="deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除 </a>
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
        location.href = '${BASE_PATH}/sms/black_list/index?pn='+p;
    }


    function deleteById(id){
        Boss.confirm("确定要删除该黑名单吗？",function(){
            $.ajax({
                url:'${BASE_PATH}/sms/black_list/delete',
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
                    Boss.alert('删除黑名单异常！');
                }
            });
        });
    }
    
    function loadingRedis(){
        Boss.confirm("确定要重载redis黑名单吗？",function(){
            $.ajax({
                url:'${BASE_PATH}/sms/black_list/loadingRedis',
                dataType:'json',
                type:'post',
                success:function(data){
                    if(data){
                        Boss.alert('重载redis成功！');
                    }else{
                        Boss.alert('重载redis失败！');
                    }
                },error:function(data){
                    Boss.alert('重载redis黑名单异常！');
                }
            });
        });
    }
</script>
</html>