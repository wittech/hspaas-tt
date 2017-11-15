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
                        <li><a href="#"> 基础信息 </a></li>
                        <li class="active">发票管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">发票处理</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <input type="hidden" name="invoiceRecord.id" value="${record.id}">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-3 control-label">发票抬头</label>
                                <div class="col-xs-5 control-label" style="text-align:left">
                                    ${record.title}
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-3 control-label">发票类型</label>
                                <div class="col-xs-5 control-label" style="text-align:left">
                                <#if record.type == 0>
                                    普通发票
                                <#elseif record.type == 1>
                                    增值税专用发票
                                </#if>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">发票金额</label>
                                <div class="col-xs-5 control-label" style="text-align:left">
                                ${record.money!0}
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">邮件地址</label>
                                <div class="col-xs-5 control-label" style="text-align:left">
                                ${(record.address)!'--'}
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">邮政编码</label>
                                <div class="col-xs-5 control-label" style="text-align:left">
                                ${record.zipCode!'--'}
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">收件人</label>
                                <div class="col-xs-5 control-label" style="text-align:left">
                                ${record.mailMan!'--'}
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">手机号</label>
                                <div class="col-xs-5 control-label" style="text-align:left">
                                ${record.mobile!'--'}
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">状态处理</label>
                                <div class="col-xs-5">
                                    <label class="form-radio form-icon"><input type="radio" id="status_1"
                                                                               name="status" <#if !(record.status??) || record.status == 0>checked</#if>
                                                                               value="0">待处理</label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon"><input type="radio" id="status_2"
                                                                               name="status"  <#if record.status?? && record.status == 1>checked</#if>
                                                                               value="1">邮寄</label>&nbsp;&nbsp;
                                </div>
                                <input type="hidden" name="invoiceRecord.status" id="status" value="0" />
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">支付费用类型</label>
                                <div class="col-xs-5">
                                    <label class="form-radio form-icon"><input type="radio" id="chargeType_1"
                                                                               name="chargeType"  <#if !(record.chargeType??) || record.chargeType == 0>checked</#if>
                                                                               value="0">到付</label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon"><input type="radio" id="chargeType_2"
                                                                               name="chargeType" <#if record.chargeType?? && record.chargeType == 1>checked</#if>
                                                                               value="1">预付</label>&nbsp;&nbsp;
                                </div>
                                <input type="hidden" name="invoiceRecord.chargeType" id="chargeType" value="1" />
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">快递单号</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100]]"
                                           name="invoiceRecord.trackingNumber" value="${record.trackingNumber!''}" id="trackingNumber" placeholder="请输入快递单号">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">快递公司</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100]]"
                                           name="invoiceRecord.express" value="${record.express!''}" id="express" placeholder="请输入快递公司">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">用户备注</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control" readonly
                                              rows="5">${record.memo}</textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">回复备注</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control" name="invoiceRecord.remark" id="remark"
                                              rows="5">${record.remark!''}</textarea>
                                </div>
                            </div>
                            <#if record.status?? && (record.status == 0 || record.status == 3)>
                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-3">
                                    <button type="button" onclick="formSubmit();" class="btn btn-primary"
                                            name="buttonSubmit">提交
                                    </button>
                                </div>
                            </div>
                            </#if>
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
        $('#myform').validationEngine('attach', {promptPosition: "centerRight"});

        $('input[name=chargeType]').click(function(){
            $('#chargeType').val($(this).val());
        });

        $('input[name=status]').click(function(){
            $('#status').val($(this).val());
        });
    });

    function formSubmit() {
        var allCheck = $('#myform').validationEngine('validate');
        if (!allCheck) {
            return;
        }
        $.ajax({
            url: '${BASE_PATH}/base/invoice/update',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
                if (data.result) {
                    Boss.alert('发票处理成功！',function(){
                        location.href = "${BASE_PATH}/base/invoice"
                    });
                } else {
                    Boss.alert(data.message);
                }
            }, error: function (data) {
                Boss.alert('发票处理失败!');
            }
        });
    }
</script>
</html>