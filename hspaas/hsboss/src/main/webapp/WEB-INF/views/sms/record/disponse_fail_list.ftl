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
                        <li class="active"> 短信记录查询 </li>
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
                                        <span class="input-group-addon">关键字</span>
                                        <input type="text" class="form-control" id="keyword" name="keyword" value="${keyword!''}" placeholder="输入内容/手机号">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-addon">SID</span>
                                        <input type="text" class="form-control" id="sid" name="sid" value="${sid?if_exists}" placeholder="输入SID">
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
                        <h3 class="panel-title">
                            <span>短信处理失败记录</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>序</th>
                                <th>发送IP</th>
                                <th>客户名</th>
                                <th>手机号</th>
                                <th>运营商</th>
                                <th>计费条数</th>
                                <th>状态</th>
                                <th>创建时间</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list page.list as pl>
                            <tr>
                                <td rowspan="2" style="background: #fff;text-align: center;">${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>${(pl.ip)!''}</td>
                                <td>${(pl.userModel.fullName)!''}</td>
                                <td>
                                    <#if pl.mobiles?? && pl.mobiles?size gt 1>
                                    ${pl.firstMobile!''}...
                                        <button type="button" onclick="showAllMobile('${pl.mobile!''}');" class="btn btn-primary btn-xs">${pl.mobiles?size}</button>
                                    <#else>
                                    ${(pl.mobile)!''}
                                    </#if>
                                </td>
                                <td>
                                    <#if pl.cmcp == 1>
                                        移动
                                    <#elseif pl.cmcp == 2>
                                        电信
                                    <#elseif  pl.cmcp == 3>
                                        联通
                                    <#elseif  pl.cmcp == 4>
                                        全网
                                    <#else>
                                        未知
                                    </#if>
                                </td>
                                <td>
                                    ${pl.fee!0}
                                </td>
                                <td>
                                    <#if pl.status == 0>
                                        待归正
                                    <#elseif pl.status == 1>
                                        归正完成，已提交
                                    <#elseif pl.status == 2>
                                        归正完成，待审核
                                    <#elseif  pl.status == 3>
                                        归正失败
                                    <#else>
                                        未知
                                    </#if>
                                </td>
                                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                            </tr>
                            <tr>
                                <td colspan="7" align="right" style="word-break:break-all;">
                                    ${pl.content!''}
                                    <span style="color:red">
                                        <#if pl.content?? && pl.content != ''>
                                            [字数：${pl.content?length}]
                                            <#else>
                                                [字数：0]
                                            </#if>

                                    </span>
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

        <div class="modal fade" id="myModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">手机号</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000" id="myModelBody">
                        <textarea id="all_mobile" class="form-control" rows="6"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>

    </div>
    <script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>  <script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
</body>
<script type="text/javascript">
    function jumpPage(p){
        $('#pn').val(p);
        $('#myform').attr('action','${BASE_PATH}/sms/record/disponse_fail_list');
        $('#myform').submit();
    }

    function showAllMobile(mobile){
        $('#all_mobile').val(mobile);
        $('#myModal').modal('show');
    }

    function closeModal(){
        $('#myModal').modal('hide');
    }
</script>
</html>