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

            <#--
            <div class="panel">
                <div class="panel-body">
                    <div class="row" style="margin-top:5px">
                        <div class="col-md-4">
                            <div class="input-group">
                                <span class="input-group-addon">查询条件</span>
                                <input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="input-group">
                                <span class="input-group-addon">查询条件</span>
                                <input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
                            </div>
                        </div>
                        <div class="col-md-4">
                            <button class="btn btn-primary">查&nbsp;&nbsp;&nbsp;询</button>
                        </div>
                    </div>
                </div>
            </div>
            -->

                <div class="panel">
                    <div class="panel-heading">
                        <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
                            <a class="btn btn-success" href="${BASE_PATH}/sms/white_list/add">新增白名单</a>
                        </div>
                        <h3 class="panel-title">
                            <span>白名单列表</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>编号</th>
                                <th>客户名称</th>
                                <th>手机号码</th>
                                <#--<th>类型</th>-->
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list page.list as pl>
                            <tr>
                                <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>${(pl.userProfile.fullName)!''}</td>
                                <td>
                                   ${pl.mobile!''}
                                </td>
                                <#--<td>-->
                                    <#--<#if pl.type == 1>-->
                                        <#--短信-->
                                    <#--<#elseif pl.type == 2>-->
                                        <#--流量-->
                                    <#--<#elseif pl.type == 3>-->
                                        <#--语音-->
                                    <#--<#elseif pl.type == 0>-->
                                        <#--默认-->
                                    <#--</#if>-->
                                <#--</td>-->
                                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                <td>
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
    <script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
    <script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
</body>
<script type="text/javascript">
    function jumpPage(p){
        location.href = '${BASE_PATH}/sms/white_list/index?pn='+p;
    }


    function deleteById(id){
        Boss.confirm("确定要删除该白名单吗？",function(){
            $.ajax({
                url:'${BASE_PATH}/sms/white_list/delete',
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
                    Boss.alert('删除白名单异常！');
                }
            });
        });
    }
</script>
</html>