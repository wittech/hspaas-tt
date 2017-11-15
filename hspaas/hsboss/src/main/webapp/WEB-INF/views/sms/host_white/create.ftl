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
                        <li class="active">ip白名单管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">新增白名单</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-2 control-label">开户用户</label>
                                <div class="col-xs-4">
                                    <select id="userId" name="hostWhiteList.userId" class="form-control">
                                    <#if userList??>
                                        <#list userList as u>
                                            <option value="${u.userId!''}">${u.name!''}-${u.username!''}</option>
                                        </#list>
                                    </#if>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">是否有效</label>
                                <div class="col-xs-4">
                                    <select id="type" name="hostWhiteList.status" class="form-control">
                                    <#if status??>
                                        <#list status as a>
                                            <option value="${a.getValue()!''}">${a.getTitle()!''}</option>
                                        </#list>
                                    </#if>
                                    </select>
                                </div>
                            </div>
							<div class="form-group batchContent">
                                <label class="col-xs-2 control-label">模板内容</label>
                                <div class="col-xs-6">
                                    <textarea class="form-control validate[required,maxSize[1000]]" id="ip"
                                              name="hostWhiteList.ip" rows="8" placeholder="请输入ip地址,多个ip每行一个分隔符" ></textarea>
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
        var arr = $('#ip').val().split("\n");
		var tip = "";
		$.each(arr, function(i){
			if(arr[i].length>0){
				if(!isIP(arr[i])){
			        tip = "请输入正确ip地址（第"+(i+1)+"行）"
			        return false;
				}
			}
		});
		if(tip != ""){
			alert(tip);
			return false;
		}
        $.ajax({
            url:'${BASE_PATH}/sms/host_white/add',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('新增白名单成功！',function(){
                        location.href = "${BASE_PATH}/sms/host_white"
                    });
                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('新增白名单失败!');
            }
        });
    }
    
    function isIP(ip){   
	    var re =  /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/   
	    return re.test(ip);
	}  

</script>
</html>