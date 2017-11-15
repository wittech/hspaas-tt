<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8"/>
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
                <div class="breadcrumb-wrapper"><span class="label">所在位置:</span>
                    <ol class="breadcrumb">
                        <li><a href="#"> 管理平台 </a></li>
                        <li><a href="#"> 模板管理 </a></li>
                        <li class="active">模板匹配</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">模板匹配</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <input type="hidden" name="id" value="${(messageTemplate.id)!}" />
                        <div class="panel-body">
                            <div class="form-group batchContent">
                                <label class="col-xs-2 control-label">模板内容</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control validate[required,maxSize[1000]]"
                                             id="content"  rows="8"><#if messageTemplate??>${messageTemplate.content!''}</#if></textarea>
                                </div>
                            </div>
                            <div class="form-group batchContent">
                                <label class="col-xs-2 control-label">模板测试</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control validate[required,maxSize[1000]]"
                                             name="content" id="testContent" rows="8"></textarea>
                                </div>
                            </div>
                            <div class="form-group" id="div_matching_result" style="display:none;">
                                <label class="col-xs-2 control-label">匹配结果</label>
                                <div class="col-xs-4">
                                	<label id="matching_result" style="color:red;width:80px" class="col-xs-2 control-label"></label>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-3">
                                    <button type="button" onclick="formSubmit();" class="btn btn-primary"
                                            name="buttonSubmit">提交
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    <#include "/WEB-INF/views/main/left.ftl">
    </div>
</div>
</body>
<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
<script type="text/javascript">
    $(function () {
        $('#myform').validationEngine('attach', {promptPosition: "topRight"});
    });

    function formSubmit() {
        var allCheck = $('#myform').validationEngine('validate');
        if (!allCheck) {
            return;
        }
        $.ajax({
            url: '${BASE_PATH}/sms/message_template/matchingSubmit',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
            	$("#div_matching_result").show();
                if (data.result) {
                	$("#matching_result").html("<span class='label label-success'>匹配成功</span>");
                } else {
                	$("#matching_result").html("<span class='label label-danger'>匹配失败</span>");
                }
            }, error: function (data) {
                Boss.alert('系统异常!请稍后重试！');
            }
        });
    }
</script>
</html>