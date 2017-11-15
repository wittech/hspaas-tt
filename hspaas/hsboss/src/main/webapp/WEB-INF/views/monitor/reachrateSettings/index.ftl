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
                        <li> <a href="#">  监控中心 </a> </li>
                        <li class="active"> 短信通道监控 </li>
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
                                        <span class="input-group-addon">通道名称</span>
                                        <input type="text" class="form-control" id="keyword" name="keyword" value="${keyword!''}" placeholder="输入通道名称...">
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
                            <a class="btn btn-success" href="${BASE_PATH}/monitor/reachrateSettings/add">添加通道监控设置</a>
                        </div>
                        <h3 class="panel-title">
                            <span>通道监控设置列表</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>编号</th>
                                <th>通道名称</th>
                                <th>轮询间隔(分钟)</th>
                                <th>数据源时间(分钟)</th>
                                <th>数据源时长(分钟)</th>
                                <th>计数阀值</th>
                                <th>到达率</th>
                                <th>告警手机</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list page.list as pl>
                            <tr>
                                <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>${(pl.passageName)!''}</td>
                                <td>${pl.showMinuteInterval}</td>
                                <td>
                                    ${pl.showMinuteStartTime}
                                </td>
                                <td>
                                  ${pl.showMinuteTimeLength}
                                </td>
                                <td>${pl.countPoint}</td>
                                <td>${pl.rateThreshold}</td>
                                <td>${pl.mobile!''}</td>
                                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                <td>
                                    <a class="btn btn-primary btn-xs" href="${BASE_PATH}/monitor/reachrateSettings/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                    &nbsp;
                                    <#--<a class="btn btn-danger btn-xs" href="javascript:void(0);" onclick="deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除</a>-->
                                    <#--&nbsp;-->
                                    <#if pl.status == 1>
                                        <a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="disabled(${pl.id},'2');"><i class="fa fa-lock"></i>&nbsp;禁用 </a>
                                    <#else>
                                        <a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="disabled(${pl.id},'1');"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
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
    <script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script>
    <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script>
    <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
</body>
<script type="text/javascript">
    function jumpPage(p){
        $('#pn').val(p);
        $('#myform').attr('action','${BASE_PATH}/monitor/reachrateSettings/index');
        $('#myform').submit();
    }

    function deleteById(id){
        Boss.confirm("确定要删除该设置吗？",function(){
            $.ajax({
                url:'${BASE_PATH}/monitor/reachrateSettings/delete',
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
                    Boss.alert("删除设置失败！");
                }
            });
        });
    }

    function disabled(id,flag){
        var msg  = "禁用";
        if(flag == 1){
            msg = "启用";
        }
        Boss.confirm('确定要'+msg+'该设置吗？',function(){
            $.ajax({
                url:'${BASE_PATH}/monitor/reachrateSettings/disabled',
                type:'post',
                dataType:'json',
                data:{id:id,flag:flag},
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            location.reload();
                        }
                    });
                },error:function(data){
                    Boss.alert(msg+"设置失败！");
                }
            });
        });
    }

</script>
</html>