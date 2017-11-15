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
								<li> <a href="#"> 系统管理 </a> </li>
								<li class="active">用户管理</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                            <!-- Panel heading -->
                            <div class="panel-heading">
                                <h3 class="panel-title">新增用户</h3>
                            </div>
                            <!-- Panel body -->
                                <form id="myform" class="form-horizontal">
                                  <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">登录名</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[required,maxSize[100]]" name="bossUser.login_name" id="login_name" placeholder="请输入登录名">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">真实姓名</label>
                                        <div class="col-xs-5">
                                             <input type="text" class="form-control validate[required,maxSize[100]]" id="real_name" name="bossUser.real_name" placeholder="请输入真实姓名">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">手机号码</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[optional,maxSize[20]]" id="mobile" name="bossUser.mobile" placeholder="请输入手机号码">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">E-mail</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[optional,maxSize[50]]" id="email" name="bossUser.email" placeholder="请输入E-mail">
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">角色</label>
                                        <div class="col-xs-6">
                                        	<input type="hidden" id="roleIds" name="roleIds" value="">
											<#if roleList?size gt 0>
											 	<#list roleList as rl>
											    	<label class="form-checkbox form-icon"><input type="checkbox" id="role_select_${rl.id}" name="role_select" value="${rl.id}">${rl.role_name}</label>&nbsp;&nbsp;
											    </#list>
										    <#else>
										    	还没有系统角色，请先新增<button type="button" onclick="location.href='${BASE_PATH}/boss/role/add'" class="btn btn-warning btn-sm">新增角色</button>
										    </#if>
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
			var role_selects = $('input[name=role_select]');
			if(role_selects.length <= 0){
                Boss.alert('请先去新增角色!');
				return;
			}
			
			role_selects = $('input[name=role_select]:checked');
			if(role_selects.length <= 0){
                Boss.alert('请选择用户角色!');
				return;
			}
			
			var roleIds = "";
			for(var i = 0;i < role_selects.length;i++){
				roleIds += $(role_selects[i]).val() + ',';
			}
			roleIds = roleIds.substring(0,roleIds.length - 1);
			$('#roleIds').val(roleIds);
			
			$.ajax({
	  			url:'${BASE_PATH}/boss/user/create',
	  			dataType:'json',
	  			data:$('#myform').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('新增用户成功！',function(){
                            location.href = "${BASE_PATH}/boss/user"
						});
	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('新增用户请求异常!');
	  			}
	  		});
		}
	</script>
</html>