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
    <link href="${BASE_PATH}/resources/css/jquery.tagit.css" rel="stylesheet">
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
                        <li class="active">用户账户余额告警</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                   	 <input type="hidden" name="userBalance.id" value="${userBalance.id}">
                        <div class="panel-body">
                        	<div class="form-group">
                                <label class="col-xs-3 control-label">客户名称</label>
                                <div class="col-xs-5">
	                                <input type="text" readonly = "readonly" class="form-control" value="<#if userModel??>${userModel.name!''}</#if>">
                                </div>
                            </div>
                           <div class="form-group">
                                <label class="col-xs-3 control-label">类型</label>
                                <div class="col-xs-5">
                                	<input type="text" readonly = "readonly" class="form-control" value="<#if userBalance??><#if userBalance.type==1>短信<#elseif userBalance.type==2>流量 <#elseif userBalance.type==3>语音<#else>未知</#if></#if>">
                                </div>
                            </div>
							<div class="form-group">
                                <label class="col-xs-3 control-label">告警阀值</label>
                                <div class="col-xs-5">
                                    <input type="text" name="userBalance.threshold" id="threshold" class="form-control validate[required,maxSize[100],custom[number]]" value="${(userBalance.threshold)!}" placeholder="余额低于此值后发送告警短信">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-3 control-label">告警手机号码</label>
                                <div class="col-xs-5">
                                    <input type="text" name="userBalance.mobile" id="mobile" class="form-control validate[required]" value="${(userBalance.mobile)!}" placeholder="多个手机号码以半角,分割">
                                </div>
                            </div>
							<div class="form-group">
                                <label class="col-xs-3 control-label">备注</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control validate[maxSize[1000]]" name="userBalance.remark" rows="3">${(userBalance.remark)!}</textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-5">
                                    <a onclick="formSubmit();" class="btn btn-primary" name="buttonSubmit">提交</a>
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
            url:'${BASE_PATH}/base/user_balance/warning_submit',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('告警信息修改成功！',function(){
                        location.href = "${BASE_PATH}/base/user_balance?userId=${userBalance.userId!'0'}"
                    });
                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('告警信息修改失败!');
            }
        });
    }

</script>
</html>