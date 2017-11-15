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
                        <li class="active">用户账户余额管理</li>
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
                                    <select class="form-control" name="userBalance.userId" id="userId" value="${userBalance.userId}">
                                        <#if userList??>
	                                        <#list userList as u>
	                                        	<#if userBalance.userId==u.id>
	                                            	<option value="${u.id}" <#if userBalance.userId?? && userBalance.userId == u.id>selected</#if>>${u.name!''}</option>
	                                        	</#if>
	                                        </#list>
	                                     </#if>
                                    </select>
                                </div>
                            </div>
                           <div class="form-group">
                                <label class="col-xs-3 control-label">类型</label>
                                <div class="col-xs-5">
                                    <label class="form-radio form-icon"><input type="radio" id="type_1" name="type" value="1" onclick="changeType('1');" <#if userBalance??><#if userBalance.type==1>checked  <#else>disabled</#if></#if>>短信</label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon"><input type="radio" id="type_2" name="type" value="2" onclick="changeType('2');"  <#if userBalance??><#if userBalance.type==2>checked <#else>disabled</#if></#if>>流量</label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon"><input type="radio" id="type_3" name="type" value="3" onclick="changeType('3');"  <#if userBalance??><#if userBalance.type==3>checked <#else>disabled</#if></#if>>语音</label>&nbsp;&nbsp;
                                </div>
                                <input type="hidden" name="userBalance.type" value ='${userBalance.type!'1'}' id="type">
                            </div>

							<div class="form-group">
                                <label class="col-xs-3 control-label">当前余额</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100],custom[number]]" readonly="readonly" value="${userBalance.balance!'0'}">
                                	<span id="unitId">
                                		<#if userBalance??>
                                			<#if userBalance.type==1>
                                				（条）
                                			<#elseif userBalance.type==2>
                                				（M）
                                			<#else>
                                				（分钟）
                                			</#if>
                                		</#if>
                                	</span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">充扣值</label>
                                <div class="col-xs-5">
                                    <input type="text" onchange="balanceVal();" class="form-control validate[required,maxSize[100],custom[number]]" name="userBalance.balance" id="balance" placeholder="正数为充值，负数为扣值">
                                	<span id="unitId">
                                		<#if userBalance??>
                                			<#if userBalance.type==1>
                                				（条）
                                			<#elseif userBalance.type==2>
                                				（M）
                                			<#else>
                                				（分钟）
                                			</#if>
                                		</#if>
                                	</span>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="col-xs-3 control-label">单价</label>
                                <div class="col-xs-5">
                                    <input type="text" name="userBalance.price" id="priceId" onchange="balanceVal();" class="form-control validate[required,maxSize[100],custom[number]]" value="0">
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="col-xs-3 control-label">总价</label>
                                <div class="col-xs-5">
                                    <input type="text" name="userBalance.totalPrice" id="totalPriceId" class="form-control validate[required,maxSize[100],custom[number]]" readonly="readonly" value="0">
                                </div>
                            </div>
                            <#--
                             <div class="form-group">
                                <label class="col-xs-3 control-label">付费方式</label>
                                <div class="col-xs-5">
                                    <label class="form-radio form-icon"><input type="radio" class="payType" id="payType_1" name="payType" <#if userBalance.payType == 1>checked</#if> value="1">预付</label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon"><input type="radio" class="payType" id="payType_2" name="payType" <#if userBalance.payType == 2>checked</#if> value="2">后付</label>
                                </div>
                                <input type="hidden" name="userBalance.payType" id="payType" value="${userBalance.payType}">
                            </div>
							-->
							<div class="form-group">
                                <label class="col-xs-3 control-label">备注</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control validate[maxSize[1000]]" name="userBalance.remark" rows="3">${userBalance.remark!''}</textarea>
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

<div class="modal fade" id="myModal">
    <div class="modal-dialog" style="width:850px">
        <div class="modal-content" style="width:850px">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">选择用户</h4>
            </div>
            <div class="modal-body" id="myModelBody">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
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
    $(function(){
        $('#myform').validationEngine('attach',{promptPosition : "centerRight"});

//        $('input[name=type]').click(function(){
//            alert($(this).val());
//            $('#type').val($(this).val());
//        })
    });
    
    function balanceVal(){
    	var balance = $("#balance").val();
    	var priceId = $("#priceId").val();
    	if(balance !=null && balance !="" && balance !=undefined && 
    	priceId !=null && priceId !="" && priceId !=undefined){
    		$("#totalPriceId").val(balance*priceId);
    	}
    }

    function formSubmit(){
        var allCheck = $('#myform').validationEngine('validate');
        if(!allCheck){
            return;
        }
        $.ajax({
            url:'${BASE_PATH}/base/user_balance/update',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('冲扣值成功！',function(){
                        location.href = "${BASE_PATH}/base/user_balance?userId=${userBalance.userId!'0'}"
                    });
                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('冲扣值失败!');
            }
        });
    }

	function changeType(type){
		//$("#type").val(type);
		if(type=="1"){
			//短信
			$("#unitId").html("（条）");
		}else if(type=="2"){
			//流量
			$("#unitId").html("（M）");
		}else{
			//语音
			$("#unitId").html("（分钟）");
		}
	}

</script>
</html>