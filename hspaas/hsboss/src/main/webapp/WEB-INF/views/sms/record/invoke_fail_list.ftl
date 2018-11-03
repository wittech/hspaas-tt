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
                                        <input type="text" class="form-control" id="keyword" name="keyword" value="${keyword!''}" placeholder="输入内容/手机号/接口帐号">
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
                            <span>短信调用失败记录</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>序</th>
                                <th>客户名</th>
                                <th>帐号</th>
                                <th>密码</th>
                                <th>手机号</th>
                                <th>IP</th>
                                <th>源</th>
                                <th>提交时间</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#assign openFailReasonCheck = macro.doOper("2001005001") />
                            <#list page.list as pl>
                            <tr>
                                <td rowspan="2" style="background: #fff;text-align: center;">${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>${(pl.userModel.name)!'--'}</td>
                                <td>${(pl.appKey)!''}</td>
                                <td>${(pl.appSecret)!''}</td>
                                <td>
                                <#if pl.mobiles?? && pl.mobiles?size gt 1>
                                    ${pl.firstMobile!''}...
                                    <button type="button" onclick="showAllMobile('${pl.mobile!''}');" class="btn btn-primary btn-xs">${pl.mobiles?size}</button>
                                <#else>
                                    ${(pl.mobile)!''}
                                </#if>
                                </td>
                                <td>${pl.ip!'--'}</td>
                                <td>
                                	<#if pl.appType == 1>
                                       	 W
                                    <#elseif pl.appType == 2>
                                        D
                                    <#elseif pl.appType == 3>
                                        B
                                    <#else>
                                                                                 未知
                                    </#if>
                                </td>
                                <td>
                                    <#if pl.submitTime??>
                                    ${pl.submitTime?string('yyyy-MM-dd HH:mm:ss.s')}
                                    <#else>
                                    --
                                    </#if>

                                </td>
                                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                <td>
                                	<#if openFailReasonCheck>
	                                    <#if pl.respCode?? && pl.respCode != ''>
	                                        <a href="javascript:void(0);" onclick="openFailReason('${pl.respCode?html}')" class="btn btn-info btn-xs">失败原因</a>
	                                    </#if>
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="10" align="right" style="word-break:break-all;">
                                	URL: ${(pl.submitUrl)!''}&nbsp;&nbsp;内容：
                                    ${pl.content!''}
                                    <span style="color:red">
                                    [字数：${(pl.content?length)!}]
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

        <div class="modal fade" id="reasonModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">失败原因</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000" id="myModelBody">
                        <textarea id="reasonMessage" class="form-control" rows="6"></textarea>
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
        $('#myform').attr('action','${BASE_PATH}/sms/record/invoke_fail_list');
        $('#myform').submit();
    }

    function showAllMobile(mobile){
        $('#all_mobile').val(mobile);
        $('#myModal').modal('show');
    }

    function closeModal(){
        $('#myModal').modal('hide');
        $('#reasonModal').modal('hide');
    }

    function openFailReason(reason){
        $('#reasonMessage').val(reason);
        $('#reasonModal').modal('show');
    }
</script>
</html>