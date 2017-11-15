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
						<li> <a href="#"> 系统管理 </a> </li>
						<li class="active">修改密码 </li>
					</ol>
				</div>
			</div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">修改密码</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                    	<input type="hidden" name="id" value="${session.userId?if_exists}"/>
                        <div class="panel-body">
                        	<div class="form-group">
                                <label class="col-xs-3 control-label">账号</label>
                                <div class="col-xs-5">
                                	<input type="text" class="form-control" value="${session.loginName?if_exists}" readonly="readonly" disabled/>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">原密码</label>
                                <div class="col-xs-5">
                                    <input type="password" class="form-control validate[required,maxSize[20],number]" name="originalPassword" id="originalPassword" placeholder="请输入原密码"/>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="col-xs-3 control-label">新密码</label>
                                <div class="col-xs-5">
                                    <input type="password" class="form-control validate[required,maxSize[20],number]" name="newPassword" id="newPassword" placeholder="请输入新密码"/>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="col-xs-3 control-label">确认密码</label>
                                <div class="col-xs-5">
                                    <input type="password" class="form-control validate[required,maxSize[20],number]" name="secondaryNewPassword" id="secondaryNewPassword" placeholder="再次输入确认密码"/>
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
        if($("#newPassword").val().trim() !=$("#secondaryNewPassword").val().trim()){
            Boss.alert("两次密码输入不一致！请重新输入！");
        	return;
        }
        $.ajax({
            url:'${BASE_PATH}/boss/user/updatePassword',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('修改成功！',function(){
                        location.href = "${BASE_PATH}/main"
                    });
                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('修改失败!请稍后重试!');
            }
        });
    }

</script>
</html>