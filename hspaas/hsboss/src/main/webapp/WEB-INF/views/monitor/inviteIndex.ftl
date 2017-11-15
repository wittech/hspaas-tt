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
                        <li> <a href="#"> 监控中心 </a> </li>
                        <li> <a href="#"> 通道控制中心  </a></li>
                        <li class="active"> 通道自取报告 </li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            <span>通道自取报告列表</span>
                        </h3>
                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>编号</th>
                                <th>通道ID</th>
                                <th>通道代码</th>
                                <th>通道类型</th>
                                <th>用户名</th>
                                <th>轮训间隔时间</th>
                                <th>最近执行时间</th>
                                <th>最近获取总量</th>
                                <th>执行耗时</th>
                                <th>抓取有效次数</th>
                                <th>状态</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#if list??>
	                            <#list list as pl>
	                            <tr>
	                                <td>${(pl_index+1)}</td>
	                                <td>${(pl.passageId)!''}</td>
	                               	<td>${pl.passageCode!''}</td>
	                               	<td>${pl.callTypeText!''}</td>
	                               	<td>${pl.account!''}</td>
	                               	<td>${pl.intevel!''}</td>
	                               	<td>${pl.lastTime!''}</td>
	                               	<td>${pl.lastAmount!''}</td>
	                               	<td>${pl.costTime!''}</td>
	                               	<td>${pl.pullAvaiableTimes!''}</td>
	                               	<td>
	                               		<#if pl.status == 0>
	                                       <a href="javascript:void(0);" class="btn btn-success btn-xs">${pl.statusText!''}</a>
	                                    <#else>
	                                       <a href="javascript:void(0);" class="btn btn-danger btn-xs">${pl.statusText!''}</a>
	                                    </#if>
	                               	</td>
	                                <td>--</td>
	                              </tr>
	                            </#list>
	                           </#if>
                            </tbody>
                        </table>
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
</html>