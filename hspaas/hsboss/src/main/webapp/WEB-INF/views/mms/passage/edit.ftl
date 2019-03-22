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
                        <li> <a href="#"> 彩信管理 </a> </li>
                        <li class="active">通道管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">编辑通道</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <input type="hidden" name="passage.id" value="${passage.id}">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-2 control-label">通道名称</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[100]]" name="passage.name" value="${passage.name!''}" id="name" placeholder="请输入通道名称">
                                </div>
                                <label class="col-xs-1 control-label">通道编码</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[100]]" name="passage.code" value="${passage.code!''}" readonly="readonly" id="code" placeholder="请输入通道编码">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-2 control-label">运营商</label>
                                <div class="col-xs-4">
                                <#list cmcp as c>
                                    <#if c.getCode() gt 0>
                                        <label class="form-radio form-icon"><input type="radio" class="passageType" id="type_${c.getCode()}" name="passageType" <#if c.getCode() == passage.cmcp>checked</#if> value="${c.getCode()}">${c.getTitle()}</label>&nbsp;&nbsp;
                                    </#if>
                                </#list>
                                </div>
                                <input type="hidden" name="passage.cmcp" id="passageType" value="${passage.cmcp}">
                                <label class="col-xs-1 control-label">付费方式</label>
                                <div class="col-xs-4">
                                     <label class="form-radio form-icon"><input type="radio" class="payType" id="payType_1" name="payType" <#if passage.payType == 1>checked</#if> value="1">预付</label>&nbsp;&nbsp;
                                     <label class="form-radio form-icon"><input type="radio" class="payType" id="payType_2" name="payType" <#if passage.payType == 2>checked</#if> value="2">后付</label>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-2 control-label">通道类型</label>
                                <div class="col-xs-4">
                                    <label class="form-radio form-icon"><input type="radio" class="passageRealType" id="passageRealType_1" name="passageRealType" <#if passage.type == 0>checked</#if> value="0">公共通道</label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon"><input type="radio" class="passageRealType" id="passageRealType_2" name="passageRealType" <#if passage.type == 1>checked</#if> value="1">独立通道</label>
                                    <input type="hidden" name="passage.exclusiveUserId" id="userId" value="${passage.exclusiveUserId!-1}">

                                    <a href="javascript:void(0);"  onclick="openUserList();" class="btn btn-primary btn-xs selectUserDiv" <#if passage.type == 0>style="display: none;"</#if>>${selectUserName!'选择用户'}</a>
                                </div>
                                <input type="hidden" name="passage.type" id="passageRealType" value="${passage.type}">

                                <label class="col-xs-1 control-label">省份设置</label>
                                <div class="col-xs-4">
                                    <a href="javascript:void(0);" onclick="openProvince();" class="btn btn-primary btn-xs">选择省份</a>
                                    已选<span id="provinceCount" style="color:red">0</span>个省份
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-2 control-label">接入号</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[100]]" name="passage.accessCode" id="accessCode" value="${passage.accessCode!''}" placeholder="请输入接入号">
                                </div>
                                <label class="col-xs-1 control-label">接入帐号</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[100]]" name="passage.account" value="${passage.account!''}" id="account" placeholder="请输入接入帐号">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-2 control-label">手机号码分包数</label>
                                <div class="col-xs-4">
                                    <input type="text"
                                           class="form-control validate[required,custom[integer],maxSize[100]]"
                                           name="passage.mobileSize" value="${passage.mobileSize?if_exists}" id="mobileSize" placeholder="请输入手机号码分包数">
                                </div>
                                <label class="col-xs-1 control-label">每秒提交流速</label>
                                <div class="col-xs-4">
                                    <input type="text"
                                           class="form-control validate[required,custom[integer],maxSize[100]]"
                                           name="passage.packetsSize" value="${passage.packetsSize?if_exists}" id="packetsSize" placeholder="请输入每秒提交流速">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-2 control-label">通道模板</label>
                                <div class="col-xs-4">
                                    <select name="passage.hspaasTemplateId" onchange="selectPassageTemplate();" class="form-control validate[funcCall[checkPassageTemplate]]" id="passageTemplateId">
                                        <option value="-1">选择通道模板</option>
                                    <#list templateList as tl>
                                        <option value="${tl.id}" <#if passage.hspaasTemplateId?? &&passage.hspaasTemplateId == tl.id>selected</#if>>${tl.name}</option>
                                    </#list>
                                    </select>

                                </div>
                                <label class="col-xs-1 control-label">参数设置</label>
                                <div class="col-xs-5" id="allDetailDiv">
                                    <h5><span class="label label-danger">请先选择通道模板</span></h5>
                                </div>
                            </div>

                            <div class="form-group">

                                <label class="col-xs-2 control-label">优先级</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,custom[integer],maxSize[100]]" name="passage.priority" id="priority" placeholder="请输入优先级" value="${passage.priority!5}">
                                </div>
                                
                                <label class="col-xs-1 control-label">扩展号长度</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,custom[integer],maxSize[20]]"
                                           name="passage.extNumber" id="extNumber" placeholder="-1为不限制长度" value="${passage.extNumber!''}">

                                </div>
                            </div>
                            
                            <div class="form-group">
                            	<label class="col-xs-2 control-label">最大连接数</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,custom[integer],maxSize[10]]" name="passage.connectionSize" id="connectionSize" placeholder="请输入最大连接数" value="${(passage.connectionSize)!}">
                                </div>
                                <label class="col-xs-1 control-label">超时时间(毫秒)</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,custom[integer],maxSize[10]]" name="passage.readTimeout" id="readTimeout" value="5000" placeholder="请输入请求超时时间（毫秒）">
                                </div>
                            </div>
                            
                             <div class="form-group">
                             	<label class="col-xs-2 control-label">通道强制参数</label>
                                <div class="col-xs-4">
                                	<select id="smsTemplateParam" name="passage.smsTemplateParam" class="form-control">
                                         <option value="0" selected="selected">否</option>
                                         <option value="1" <#if passage.smsTemplateParam?? && passage.smsTemplateParam == 1>selected="selected"</#if>>是</option>
					    			</select>
                                </div>
                            	<label class="col-xs-1 control-label">备注</label>
                                <div class="col-xs-4">
                                    <textarea name="passage.remark" id="remark" rows="5" class="form-control">${passage.remark!''}</textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-5">
                                    <a onclick="formSubmit();" class="btn btn-primary" name="buttonSubmit">提交</a>
                                </div>
                            </div>
                        </div>
                        <input type="hidden" name="provinceCodes" id="provinceCodes" value="" />
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
                <div id="passageTemplateShowDiv">
                <#if passage?? && passage.parameterList?? && passage.parameterList?size gt 0>
                    <#list passage.parameterList as pd>
                        <form id="detail_form_${pd.callType}" style="display:none" class="form-horizontal">
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
                                <label class="col-xs-2 control-label">请求参数</label>
                            </div>
                            <div id="requestParam${pd.callType}">
                                <#assign requestList = pd.requestParams>
                                <#if requestList?? && requestList?size gt 0>
                                    <#list requestList as rl>
                                        <div class="form-group">
                                            <label class="col-xs-2 control-label">${rl.showName!''}[${rl.requestName!''}]</label>
                                            <div class="col-xs-8">
                                                <input type="hidden" name="requestName" value="${rl.requestName!''}">
                                                <input type="hidden" name="showName" value="${rl.showName!''}">
                                                <input type="text" class="form-control validate[required,maxSize[100]]"
                                                       value="${rl.defaultValue!''}" name="defaultValue" placeholder="请输入请求值">
                                            </div>
                                        </div>
                                    </#list>
                                </#if>
                            </div>

                            <div class="form-group" style="display: none">
                                <label class="col-xs-2 control-label">响应参数解析</label>
                            </div>
                            <div id="parseRule${pd.callType}" style="display: none">
                                <#assign parseList = pd.parseParams />
                                <#if parseList?? && parseList?size gt 0>
                                    <#list parseList as pl>
                                        <div class="form-group">
                                            <label class="col-xs-2 control-label">${pl.showName!''}[${pl.parseName!''}]</label>
                                            <div class="col-xs-8">
                                                <input type="hidden" name="parseName" value="${pl.parseName!''}">
                                                <input type="hidden" name="showName" value="${pl.showName!''}">
                                                <input type="text" class="form-control validate[required,maxSize[100]]"
                                                       value="${pl.position!''}" name="position" placeholder="请输入解析位置">
                                            </div>
                                        </div>
                                    </#list>
                                </#if>
                            </div>

                            <div class="form-group">
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
                <#else>
                    <div class="alert alert-warning" role="alert">无法获取参数规则</div>
                </#if>

                </div>

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                &nbsp;&nbsp;&nbsp;&nbsp;
                <button type="button" class="btn btn-success" onclick="closeModal();">确定</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<div class="modal fade" id="userModal">
    <div class="modal-dialog" style="width:850px">
        <div class="modal-content" style="width:850px">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">选择用户</h4>
            </div>
            <div class="modal-body" id="userModelBody">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


<div class="modal fade" id="provinceModal">
    <div class="modal-dialog" style="width:550px">
        <div class="modal-content" style="width:550px">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">选择省份</h4>
            </div>
            <div class="modal-body" id="provinceModelBody">
                <div class="row" style="width:650px">
                    <div class="col-lg-10" style="margin-left:15px">
                    <#list provinceList as pl>
                        <label style="width:70px">
                            <input type="checkbox" name="provinceItem" <#if pl.select?? && pl.select == 1>checked</#if> value="${pl.code}"/>${pl.name!''}
                        </label>
                    </#list>
                    </div>
                </div>

                <div class="row" style="margin-top:10px;margin-left:5px">
                    <div class="col-lg-2"><a href="javascript:void(0);" onclick="selectAll(1);" class="btn btn-primary btn-xs">全部选择</a></div>
                    <div class="col-lg-2"><a href="javascript:void(0);" onclick="selectAll(2);" class="btn btn-default btn-xs">全部取消</a></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="selectProvince();">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

</body>
<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"  type="text/javascript"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"  type="text/javascript"></script>
<script type="text/javascript">
    $(function(){
        $('#myform').validationEngine('attach',{promptPosition : "topRight"});

        $('.payType').click(function(){
            $('#payType').val($(this).val());
        });

        $('.passageType').click(function(){
            $('#passageType').val($(this).val());
        });

        $('.passageRealType').click(function(){
            var value = $(this).val();
            if(value == 1){
                $('.selectUserDiv').show();
            }else{
                $('.selectUserDiv').hide();
            }
            $('#passageRealType').val(value);
        });

        initRuleDetailName();

        $('#provinceCount').html($('input[name=provinceItem]:checked').length);
    });

    function selectAll(flag){
        if(flag == 1){
            $('input[name=provinceItem]').prop('checked',true);
        }else{
            $('input[name=provinceItem]').prop('checked',false);
        }
    }

    function selectProvince(){
        var selects = $('input[name=provinceItem]:checked');
        $('#provinceCount').html(selects.length);
        if(selects.length <= 0){
            $('#provinceModal').modal('hide');
            $('#provinceCodes').val('');
            return;
        }
        var codes = '';
        for(var i = 0;i<selects.length;i++){
            var province = $(selects[i]);
            codes += province.val() + ',';
        }
        codes = codes.substring(0,codes.length -1);
        $('#provinceCodes').val(codes);
        $('#provinceModal').modal('hide');
    }

    function openProvince(){
        $('#provinceModal').modal('show');
    }


    function checkPassageTemplate(field, rules, i, options){
        if (field.val() == -1) {
            //return options.allrules.validate2fields.alertText;
            return '* 请选择通道模板';
        };
    };

    function initRuleDetailName(){
        var detailRuleType = $('.detailRuleType');
        var callTypeCn = ['','发送模板','状态回执推送','状态回执自取','上行推送','上行自取'];
        var html = '';
        for(var i = 0;i<detailRuleType.length;i++){
            var detailType = $(detailRuleType[i]).val();
            html += '<a class="btn btn-info btn-xs" onclick="showSelectedRule('+detailType+');">'+callTypeCn[(parseInt(detailType))]+'</a>&nbsp;';
        }
        $('#allDetailDiv').html(html);
    }

    function selectPassageTemplate(){
        var templateId = $('#passageTemplateId').val();
        if(templateId == '-1'){
            $('#allDetailDiv').html('<h5><span class="label label-danger">请先选择通道模板</span></h5>');
            return;
        }
        $.ajax({
            url:'${BASE_PATH}/mms/passage/templateView',
            data:{'templateId':templateId},
            dataType:'html',
            success:function(data){
                $('#passageTemplateShowDiv').html(data);
                var detailRuleType = $('.detailRuleType');
                var callTypeCn = ['','发送模板','状态回执推送','状态回执自取','上行推送','上行自取'];
                var html = '';
                for(var i = 0;i<detailRuleType.length;i++){
                    var detailType = $(detailRuleType[i]).val();
                    html += '<a class="btn btn-info btn-xs" onclick="showSelectedRule('+detailType+');">'+callTypeCn[(parseInt(detailType))]+'</a>&nbsp;';
                }
                $('#allDetailDiv').html(html);
            },error:function(data){
                Boss.alert('请求失败！');
            }
        })
    }

    var ruleTypeTag = -1;

    function closeModal(){
        $('#detail_form_'+ruleTypeTag).hide();
        $('#myModal').modal('hide');
    }

    function changeRuleInputName(){
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
        if(templateDetails == ""){
            return;
        }
        templateDetails = templateDetails.substring(0,templateDetails.length - 1);
        $('#templateDetails').val(templateDetails);
        $('#hiddenInputForm').html(hiddenInputHtml);
    }

    function getHiddenInput(name,value){
        var html = '<input type="hidden" name="'+name+'" value="'+value+'" />';
        return html;
    }

    function showSelectedRule(ruleType){
        ruleTypeTag = ruleType;
        $('#detail_form_'+ruleType).show();
        $('#detail_form_'+ruleType).validationEngine('attach',{promptPosition : "topRight"});
        $('#myModal').modal('show');
    }

    function formSubmit(){
        var allCheck = $('#myform').validationEngine('validate');
        if(!allCheck){
            return;
        }
        var passageType = $('#passageRealType').val();
        if(passageType == 1 && $('#userId').val() == -1){
            Boss.alert('若是独立通道，请选择用户！');
            return;
        }

        var selects = $('input[name=provinceItem]:checked');
        $('#provinceCount').html(selects.length);
        if(selects.length <= 0){
            Boss.alert('请选择省份！');
            return;
        }
        var codes = '';
        for(var i = 0;i<selects.length;i++){
            var province = $(selects[i]);
            codes += province.val() + ',';
        }
        codes = codes.substring(0,codes.length -1);
        $('#provinceCodes').val(codes);

        changeRuleInputName();
        $.ajax({
            url:'${BASE_PATH}/mms/passage/update',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('修改短信通道成功！',function(){
                        location.href = "${BASE_PATH}/mms/passage"
                    });

                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('修改短信通道异常!');
            }
        });
    }
</script>
</html>