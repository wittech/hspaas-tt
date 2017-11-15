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
                            <span>短信上行接收记录</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th>序</th>
                                <th>所属通道</th>
                                <th>客户帐号</th>
                                <th>下发号码</th>
                                <th>客户名</th>
                                <th>手机号</th>
                                <th>接收时间</th>
                                <th>创建时间</th>
                                <th>推送</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list page.list as pl>
                            <tr>
                                <td rowspan="2" style="background: #fff;text-align: center;">${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>${(pl.passageName)!''}</td>
                                <td>${(pl.userModel.appkey)!''}</td>
                                <td>${pl.destnationNo!''}</td>
                                <td>
                                ${(pl.userModel.name)!''}
                                </td>
                                <td>${pl.mobile!''}</td>
                                <td>${(pl.receiveTime)!}</td>
                                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                 <td>
                                 <#if pl.needPush>
	                                    <#if pl.messagePush??>
	                                        <a href="javascript:void(0);" data-placement="left" data-html="true" class="btn btn-<#if pl.messagePush.status == 0>success<#else>danger</#if> btn-xs" data-toggle="popover" onclick="showMessagePush('${pl.messagePush.createTime?string('yyyy-MM-dd HH:mm:ss')}',this,"${pl.messagePush.content!''}","${pl.messagePush.response_content!''}");" data='${pl.messagePush.content!''}' responseContent='${pl.messagePush.responseContent!''}' title="推送信息"
	                                           data-content="
	                                           推送状态：<#if pl.messagePush.status == 0>成功<#else>失败</#if><br>
	                                           推送次数：${pl.messagePush.retryTimes!1}<br>
	                                           推送时间：${pl.messagePush.createTime?string('yyyy-MM-dd HH:mm:ss')}<br>
	                                           响应时间：${pl.messagePush.responseMilliseconds?if_exists}ms
	                                        "><#if pl.messagePush.status == 0>成功<#else>失败</#if></a>
	                                    <#else>
	                                        待推送
	                                    </#if>
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="9" align="right" style="word-break:break-all;">
                                    ${pl.content!''}
                                    <span style="color:red">
                                    [字数：${pl.content?length}]
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
    </div>
    <!--推送-->
    <div class="modal fade" id="messagePushModal">
        <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" onclick="closeModal();"><span
                            aria-hidden="true">&times;</span></button>
                    <h4 class="modal-title">推送信息</h4>
                </div>
                <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                     id="myModelBody">
                    <b>推送时间：</b><input type="text" id="pushShowTime" class="form-control" value="" />
                    <b>推送内容：</b><input type="text" id="pushShowContent" class="form-control" value="" />
                    <b>回执内容：</b><textarea id="pushRemark" class="form-control" rows="6" cols="20"></textarea>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" onclick="closePushModal();">关闭</button>
                </div>
            </div><!-- /.modal-content -->
        </div><!-- /.modal-dialog -->
    </div>
    <script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>  <script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
</body>
<script type="text/javascript">
	$(function () {
        $('a[data-toggle=popover]').mouseover(function () {
            var $this = $(this);
            $this.popover('show')
        });

        $('a[data-toggle=popover]').mouseout(function () {
            var $this = $(this);
            $this.popover('hide')
        });

        $('span[data-toggle=popover]').mouseover(function () {
            var $this = $(this);
            $this.popover('show')
        });

        $('span[data-toggle=popover]').mouseout(function () {
            var $this = $(this);
            $this.popover('hide')
        });
    })
    
    //推送
    function showMessagePush(time,obj){
        $('#pushShowTime').val(time);
        $('#pushShowContent').val($(obj).attr('responseContent'));
        $('#pushRemark').val($(obj).attr('data'));
        $('#messagePushModal').modal('show');
    }
    
    function closePushModal() {
        $('#messagePushModal').modal('hide');
        $('#pushRemark').modal('hide');
        $('#pushShowContent').modal('hide');
        $('#pushShowTime').modal('hide');
    }
    
    function jumpPage(p){
        $('#pn').val(p);
        $('#myform').attr('action','${BASE_PATH}/sms/record/up_revice_list');
        $('#myform').submit();
    }
</script>
</html>