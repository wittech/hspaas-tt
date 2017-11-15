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
                        <li> <a href="#"> 基础信息 </a> </li>
                        <li class="active">参数配置管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">修改配置</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <input type="hidden" name="config.id" value="${config.id}">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-3 control-label">配置名称</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100]]" name="config.attrName" value="${config.attrName!''}" id="title" placeholder="请输入配置名称">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-3 control-label">配置Key</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100]]" name="config.attrKey" readOnly value="${config.attrKey!''}" id="attrKey" placeholder="请输入配置Key">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-3 control-label">配置值</label>
                                <div class="col-xs-5">
                                    <#assign specialValue = ""/>
                                    <#if config.type == 'USER_DEFAULT_PASSAGE_GROUP' && groupList?? && groupList?size gt 0>
                                        <#assign specialValue = groupList/>
                                    </#if>

                                    <#if specialValue?? && specialValue?exists && specialValue != "">
                                        <select class="form-control" name="config.attrValue" id="attrValue">
                                            <#list specialValue as sv>
                                                <#assign strId = sv.id + '' />
                                                <option value="${sv.id}" <#if config.attrValue?? && config.attrValue == strId>selected</#if>>${sv.passageGroupName!''}</option>
                                            </#list>
                                        </select>
                                    <#else>
                                        <input class="form-control validate[required,maxSize[100]]" name="config.attrValue" id="attrValue" value="${config.attrValue!''}"/>
                                    </#if>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-3 control-label">备注</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control" name="config.remark" id="remark" rows="7">${config.remark!''}</textarea>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-3">
                                    <button type="button" onclick="formSubmit();" class="btn btn-primary" name="buttonSubmit">提交</button>
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
    $(function(){
        $('#myform').validationEngine('attach',{promptPosition : "centerRight"});
    });

    function formSubmit(){
        var allCheck = $('#myform').validationEngine('validate');
        if(!allCheck){
            return;
        }
        $.ajax({
            url:'${BASE_PATH}/base/system_config/update',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('修改配置成功！',function(){
                        location.href = "${BASE_PATH}/base/system_config/index?currentType=${config.type!''}"
                    });

                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('修改配置失败!');
            }
        });
    }
</script>
</html>