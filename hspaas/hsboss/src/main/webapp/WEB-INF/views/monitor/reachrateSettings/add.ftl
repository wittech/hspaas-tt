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
                        <li><a href="#"> 监控中心 </a></li>
                        <li class="active">添加短信通道监控设置</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">新增短信通道监控设置</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-2 control-label">监控通道</label>
                                <div class="col-xs-4">
                                    <select id="passageId" name="model.passageId" class="form-control">
                                        <#list passageList as p>
                                            <option value="${p.id!''}">${p.name}</option>
                                        </#list>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">轮询间隔</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[10],custom[number],min[0]]"
                                           name="model.interval" id="interval" value=""
                                           placeholder="请输入监控轮询间隔时间">
                                </div>
                                <label class="col-xs-1 control-label">
                                    单位：分钟
                                </label>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">数据源时间</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[10],custom[number],min[0]]"
                                           name="model.startTime" id="startTime" value=""
                                           placeholder="请输入数据源时间">
                                </div>
                                <label class="col-xs-1 control-label">
                                    单位：分钟
                                </label>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">数据源时长</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[10],custom[number],min[0]]"
                                           name="model.timeLength" id="timeLength" value=""
                                           placeholder="请输入数据源时长">
                                </div>
                                <label class="col-xs-1 control-label">
                                    单位：分钟
                                </label>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">计数阀值</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[10],custom[number],min[1]]"
                                           name="model.countPoint" id="countPoint"
                                           placeholder="请输入计数阀值"/>
                                </div>
                            </div>
                            <div class="form-group batchContent">
                                <label class="col-xs-2 control-label">成功率</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[10],custom[number],min[1]]"
                                           name="model.rateThreshold" id="rateThreshold" value="90"
                                           placeholder="请输入到达率">
                                </div>
                                <label class="col-xs-2 control-label" style="text-align: left">
                                    百分比，最大100%
                                </label>
                            </div>
                            <div class="form-group batchContent">
                                <label class="col-xs-2 control-label">告警手机号码</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required]"
                                           name="model.mobile" id="mobile" value=""
                                           placeholder="请输入告警手机号码">
                                </div>
                                <label class="col-xs-3 control-label" style="text-align: left">
                                    支持多号码，多个号码用英文逗号","分隔
                                </label>
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
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script>
<script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script>
<script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
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
            url: '${BASE_PATH}/monitor/reachrateSettings/create',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
                if (data.result) {
                    Boss.alertToCallback('提交成功！',function(){
                        var url = "${BASE_PATH}/monitor/reachrateSettings";
                        location.href = url;
                    });
                } else {
                    Boss.alert('提交失败！');
                }
            }, error: function (data) {
                Boss.alert('系统异常!请稍后重试！');
            }
        });
    }

</script>
</html>