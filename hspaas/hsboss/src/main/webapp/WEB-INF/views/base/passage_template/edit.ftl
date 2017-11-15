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
                        <li class="active">通道模板管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">编辑通道模板</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <input type="hidden" name="template.id" value="${template.id}">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-2 control-label">模板名称</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[100]]"
                                           name="template.name" value="${template.name!''}" id="name"
                                           placeholder="请输入模板名称">
                                </div>
                                <label class="col-xs-1 control-label">模板协议</label>
                                <div class="col-xs-4">
                                    <select class="form-control" id="protocol" name="template.protocol">
                                    <#list protocolTypes as pl>
                                        <option value="${pl.name()}" <#if template.protocol?? && pl.name() == template.protocol>selected</#if>>${pl.name()}</option>
                                    </#list>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">模板类型</label>
                                <div class="col-xs-4">
                                    <label class="form-radio form-icon"><input type="radio" class="passageType"
                                                                               id="type_1" name="passageType"
                                                                               <#if template.passageType == 1>checked</#if>
                                                                               value="1">短信模板</label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon"><input type="radio" class="passageType"
                                                                               id="type_2" name="passageType"
                                                                               <#if template.passageType == 2>checked</#if>
                                                                               value="2">流量模板</label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon"><input type="radio" class="passageType"
                                                                               id="type_3" name="passageType"
                                                                               <#if template.passageType == 3>checked</#if>
                                                                               value="3">语音模板</label>&nbsp;&nbsp;
                                </div>
                                <input type="hidden" name="template.passageType" id="passageType"
                                       value="${template.passageType}">
                                <label class="col-xs-1 control-label">模板规则</label>
                                <div class="col-xs-4">
                                    <div class="input-group">
                                        <select class="form-control" id="ruleType">
                                            <option value="1">发送模板</option>
                                            <option value="2">状态回执推送</option>
                                            <option value="3">状态回执自取</option>
                                            <option value="4">上行推送</option>
                                            <option value="5">上行自取</option>
                                        </select>
                                        <span class="input-group-btn">
										        <button class="btn btn-default" type="button"
                                                        onclick="setRuleDetail();">设置</button>
										      </span>
                                    </div>
                                </div>
                            </div>

                            <div id="setedRuleDiv" class="form-group"
                                 <#if !(template.detailList??) || template.detailList?size lte 0>style="display:none" </#if>>
                                <label class="col-xs-2 control-label">已设置模板规则</label>
                                <div class="col-xs-10">
                                <#list template.detailList as pd>
                                    <div class="btn-group showRuleTag_${pd.callType}">
                                        <#assign showCn = "发送模板">
                                        <#if pd.callType == 2>
                                            <#assign showCn = "状态回执推送">
                                        <#elseif pd.callType == 3>
                                            <#assign showCn = "状态回执自取">
                                        <#elseif pd.callType == 4>
                                            <#assign showCn = "上行推送">
                                        <#elseif pd.callType == 5>
                                            <#assign showCn = "上行自取">
                                        </#if>

                                        <a class="btn btn-info btn-xs"
                                           onclick="showSelectedRule(${pd.callType});">${showCn}</a>
                                        <a class="btn btn-default btn-xs" onclick="removeSelectedRule(${pd.callType});">删除</a>
                                    </div>
                                </#list>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-5">
                                    <a onclick="formSubmit();" class="btn btn-primary" name="buttonSubmit">提交</a>
                                </div>
                            </div>
                        </div>
                        <input type="hidden" name="templateDetails" id="templateDetails">
                        <div id="hiddenInputForm">
                        </div>
                    </form>
                </div>
            </div>
        </div>
    <#include "/WEB-INF/views/main/left.ftl">
    </div>
</div>

<div class="modal fade" id="myModal">
    <div class="modal-dialog" style="width:auto;height:auto;min-width:890px">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" onclick="closeModal();"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">设置模板详情</h4>
            </div>
            <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000" id="myModelBody">

            <#list template.detailList as pd>
                <form id="detail_form_${pd.callType}" style="display:none" class="form-horizontal ruleForms">
                    <input type="hidden" name="detail_callType_${pd.callType}.callType" class="detailRuleType"
                           id="detail_callType_${pd.callType}" value="${pd.callType}">
                    <div class="form-group">
                        <label class="col-xs-2 control-label">请求URL</label>
                        <div class="col-xs-8">
                            <input type="text" class="form-control validate[required,maxSize[200]]"
                                   name="detail_url_${pd.callType}.url" value="${pd.url}" id="detail_url_${pd.callType}"
                                   placeholder="请输入请求URL">
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-xs-2 control-label">设置请求参数</label>
                        <div class="col-xs-5">
                            <a class="btn btn-info btn-sm" onclick="addRow(1);">加参数</a>
                        </div>
                    </div>
                    <div id="requestParam${pd.callType}">
                        <#assign requestList = pd.requestParams>
                        <#if requestList?? && requestList?size gt 0>
                            <#list requestList as rl>
                                <div class="form-group">
                                    <label class="col-xs-2 control-label">显示名</label>
                                    <div class="col-xs-2">
                                        <input type="text" class="form-control validate[required,maxSize[100]]"
                                               value="${rl.showName!''}" name="showName" placeholder="请输入显示名">
                                    </div>
                                    <label class="col-xs-1 control-label">请求名</label>
                                    <div class="col-xs-2">
                                        <input type="text" class="form-control validate[required,maxSize[100]]"
                                               value="${rl.requestName!''}" name="requestName" placeholder="请输入请求名">
                                    </div>
                                    <label class="col-xs-1 control-label">默认值</label>
                                    <div class="col-xs-2">
                                        <input type="text" class="form-control validate[optional,maxSize[100]]"
                                               value="${rl.defaultValue!''}" name="defaultValue" placeholder="请输入默认值">
                                    </div>
                                    <div class="col-xs-1">
                                        <a class="btn btn-danger btn-sm" onclick="removeRow(this);">移除</a>
                                    </div>
                                </div>
                            </#list>
                        </#if>
                    </div>

                    <div class="form-group" style="display:none">
                        <label class="col-xs-2 control-label">响应参数解析</label>
                        <div class="col-xs-5">
                            <a class="btn btn-info btn-sm" onclick="addRow(2);">加定位</a>
                        </div>
                    </div>
                    <div id="parseRule${pd.callType}" style="display:none">
                        <#assign parseList = pd.parseParams />
                        <#if parseList?? && parseList?size gt 0>
                            <#list parseList as pl>
                                <div class="form-group">
                                    <label class="col-xs-2 control-label">显示名</label>
                                    <div class="col-xs-2">
                                        <input type="text" class="form-control validate[required,maxSize[100]]"
                                               value="${pl.showName!''}" name="showName" placeholder="请输入显示名">
                                    </div>
                                    <label class="col-xs-1 control-label">解析名</label>
                                    <div class="col-xs-2">
                                        <input type="text" class="form-control validate[required,maxSize[100]]"
                                               value="${pl.parseName!''}" name="parseName" placeholder="请输入解析名">
                                    </div>
                                    <label class="col-xs-1 control-label">位置</label>
                                    <div class="col-xs-2">
                                        <input type="text" class="form-control validate[required,maxSize[100]]"
                                               value="${pl.position!''}" name="position" placeholder="请输入解析位置">
                                    </div>
                                    <div class="col-xs-1">
                                        <a class="btn btn-danger btn-sm" onclick="removeRow(this);">移除</a>
                                    </div>
                                </div>
                            </#list>
                        </#if>
                    </div>

                    <div class="form-group" style="display:none">
                        <label class="col-xs-2 control-label">结果格式</label>
                        <div class="col-xs-8">
                            <input type="text" class="form-control validate[optional,maxSize[200]]"
                                   value="${pd.showResultFormat!''}" name="detail_result_format_${pd.callType}"
                                   id="detail_result_format_${pd.callType}" placeholder="请输入结果格式">
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="col-xs-2 control-label">成功码标识</label>
                        <div class="col-xs-8">
                            <input type="text" class="form-control validate[optional,maxSize[200]]"
                                   value="${pd.successCode!''}" name="detail_success_${pd.callType}"
                                   id="detail_success_${pd.callType}" placeholder="请输入成功码标记">
                        </div>
                    </div>
                </form>
            </#list>


            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <button type="button" class="btn btn-success" onclick="addRule();">确定</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

</body>
<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>

<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
<script type="text/javascript">
    $(function () {
        $('#myform').validationEngine('attach', {promptPosition: "topRight"});

        $('.templateType').click(function () {
            $('#templateType').val($this.val());
        });

        resetRuleTypeSelectOption();
    });

    var ruleTypeTag = -1;

    function closeModal(){
        $('.ruleForms').hide();
        $('#myModal').modal('hide');
    }

    function checkRuleFormExists(){
        var rule_forms = $('.ruleForms');
        var ruleType = $('#ruleType').val();
        var form_id = 'detail_form_'+ruleType;
        for(var i = 0;i<rule_forms.length;i++){
            var form = $(rule_forms[i]);
            if(form.attr('id') == form_id){
                return true;
            }
        }
        return false;
    }

    function setRuleDetail() {
        var ruleType = $('#ruleType').val();
        if(checkRuleFormExists()){
            $('#detail_form_'+ruleType).show();
            $('#detail_form_'+ruleType).validationEngine('attach',{promptPosition : "topRight"});
            $('#myModal').modal('show');
            return;
        }
        ruleTypeTag = -1;
        $.ajax({
            url: '${BASE_PATH}/base/passage_template/ruleView',
            data: {ruleType: ruleType},
            dataType: 'html',
            success: function (data) {
                $('#myModelBody').append(data);
                $('#detail_form_' + ruleType).validationEngine('attach', {promptPosition: "topRight"});
                $('#myModal').modal('show');
            }, error: function (data) {
                Boss.alert('获取模板规则详情失败！');
            }
        });
    }

    function changeRuleInputName() {
        var hasRules = $('.detailRuleType');
        var tagName = "rule_detail_";
        var templateDetails = "";
        var hiddenInputHtml = "";
        for(var i = 0;i<hasRules.length;i++){
            var callType = $(hasRules[i]).val();
            var ruleName = tagName+callType;
            templateDetails += ruleName + ',';
            hiddenInputHtml += getHiddenInput(ruleName+'.callType',callType);
            var url = $('#detail_url_'+callType).val();
            hiddenInputHtml += getHiddenInput(ruleName+'.url',url);
            var requestParams = $('#requestParam'+callType+' .form-group');
            var requestParamCount = requestParams.length;
            hiddenInputHtml += getHiddenInput('reqParam_'+ruleName,requestParamCount);
            for(var j = 0;j<requestParams.length;j++){
                var showName = $(requestParams[j]).find('input[name=showName]').eq(0).val();
                hiddenInputHtml += getHiddenInput('reqParam_'+ruleName+'_'+j+'.showName',showName);
                var requestName = $(requestParams[j]).find('input[name=requestName]').eq(0).val();
                hiddenInputHtml += getHiddenInput('reqParam_'+ruleName+'_'+j+'.requestName',requestName);
                var defaultValue = $(requestParams[j]).find('input[name=defaultValue]').eq(0).val();
                hiddenInputHtml += getHiddenInput('reqParam_'+ruleName+'_'+j+'.defaultValue',defaultValue);
            }

            var parseRule = $('#parseRule'+callType+' .form-group');
            var parseRuleCount = parseRule.length;
            hiddenInputHtml += getHiddenInput('parseRule_'+ruleName,parseRuleCount);
            for(var k = 0;k<parseRule.length;k++){
                var showName = $(parseRule[k]).find('input[name=showName]').eq(0).val();
                hiddenInputHtml += getHiddenInput('parseRule_'+ruleName+'_'+k+'.showName',showName);
                var parseName = $(parseRule[k]).find('input[name=parseName]').eq(0).val();
                hiddenInputHtml += getHiddenInput('parseRule_'+ruleName+'_'+k+'.parseName',parseName);
                var position = $(parseRule[k]).find('input[name=position]').eq(0).val();
                hiddenInputHtml += getHiddenInput('parseRule_'+ruleName+'_'+k+'.position',position);
            }

            var format_result = $('#detail_result_format_'+callType).val();
            format_result = format_result.replace(/"/g, "&quot;");
            hiddenInputHtml += getHiddenInput(ruleName+'.resultFormat',format_result);

            var detail_success = $('#detail_success_'+callType).val();
            hiddenInputHtml += getHiddenInput(ruleName+'.successCode',detail_success);
        }
        if (templateDetails == "") {
            return;
        }
        templateDetails = templateDetails.substring(0, templateDetails.length - 1);
        $('#templateDetails').val(templateDetails);
        $('#hiddenInputForm').html(hiddenInputHtml);
    }

    function getHiddenInput(name, value) {
        var html = '<input type="hidden" style="display:none" name="' + name + '" value="' + value + '" />';
        return html;
    }

    function addRow(rowType) {
        var ruleType = $('#ruleType').val();
        if (ruleTypeTag != -1) {
            ruleType = ruleTypeTag;
        }
        var targetDiv = "requestParam" + ruleType;
        if (rowType == 2) {
            targetDiv = "parseRule" + ruleType;
        }
        $.ajax({
            url: '${BASE_PATH}/base/passage_template/addRow',
            data: {rowType: rowType},
            dataType: 'html',
            success: function (data) {
                $('#' + targetDiv).append(data);
            }, error: function (data) {
                Boss.alert('加行失败！');
            }
        })
    }

    function resetRuleTypeSelectOption() {
        var ruleCn = ["发送模板", "状态回执推送", "状态回执自取", "上行推送", "上行自取"];
        var ruleVal = [1, 2, 3, 4, 5];

        var hasRules = $('.detailRuleType');
        var hasRuleTypes = "";
        for (var i = 0; i < hasRules.length; i++) {
            var currentRule = $(hasRules[i]);
            var cr = parseInt(currentRule.val());
            ruleVal[cr - 1] = 0;
        }

        var newSeletOption = "";
        for (var i = 0; i < ruleVal.length; i++) {
            if (ruleVal[i] <= 0) {
                continue;
            }
            newSeletOption += '<option value="' + ruleVal[i] + '">' + ruleCn[i] + '</option>';
        }
        $('#ruleType').html(newSeletOption);

    }

    function removeRow(obj) {
        $(obj).parent().parent().remove();
    }

    function addRule() {
        var ruleType = $('#ruleType').val();

        var allCheck = $('#detail_form_' + ruleType).validationEngine('validate');
        if (!allCheck) {
            return;
        }

        if (ruleTypeTag != -1) {
            ruleType = ruleTypeTag;
            $('#detail_form_' + ruleType).hide();
            $('#myModal').modal('hide');
            return;
        }
        var ruleHtml = $('#myModelBody').html();
        $('#ruleDetailDiv').append(ruleHtml);
        $('#detail_form_' + ruleType).hide();
        var ruleCn = ["发送模板", "状态回执推送", "状态回执自取", "上行推送", "上行自取"];
        var showCn = ruleCn[parseInt(ruleType) - 1];
        var showTagHtml = '<div class="btn-group showRuleTag_' + ruleType + '">' +
                '<a class="btn btn-info btn-xs" onclick="showSelectedRule(' + ruleType + ');">' + showCn + '</a>' +
                '<a class="btn btn-default btn-xs" onclick="removeSelectedRule(' + ruleType + ');">删除</a>' +
                '</div>';
        $('#setedRuleDiv').show();
        $('#setedRuleDiv .col-xs-10').append(showTagHtml);
        resetRuleTypeSelectOption();
        $('#myModal').modal('hide');
    }

    function showSelectedRule(ruleType) {
        ruleTypeTag = ruleType;
        $('#detail_form_' + ruleType).show();
        $('#detail_form_' + ruleType).validationEngine('attach', {promptPosition: "topRight"});
        $('#myModal').modal('show');
    }

    function removeSelectedRule(ruleType) {
        $('.showRuleTag_' + ruleType).remove();
        $('#detail_form_' + ruleType).remove();
        resetRuleTypeSelectOption();
        var hasRules = $('.detailRuleType');
        if (hasRules.length <= 0) {
            $('#setedRuleDiv').hide();
        }
    }

    function formSubmit() {
        var allCheck = $('#myform').validationEngine('validate');
        if (!allCheck) {
            return;
        }
        changeRuleInputName();
        $.ajax({
            url: '${BASE_PATH}/base/passage_template/update',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
                if (data.result) {
                    Boss.alertToCallback('修改通道模板成功！',function(){
                        location.href = "${BASE_PATH}/base/passage_template"
                    });
                } else {
                    Boss.alert(data.message);
                }
            }, error: function (data) {
                Boss.alert('修改通道模板失败!');
            }
        });
    }
</script>
</html>