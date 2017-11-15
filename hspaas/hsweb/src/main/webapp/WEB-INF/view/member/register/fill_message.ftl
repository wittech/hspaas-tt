<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="" name="description">
<title>华时融合平台-用户注册-填写注册详细信息</title>
<#include "/common/assets.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
</head>
<body>
	<#include "/common/header.ftl"/>
	<div class="reg_page ">
		<div class="inner">
			<div class="reg_box reg_step2_box">
				<div class="step_box">
					<div class="tit">华时通行证注册</div>
					<ul>
						<li class="li_1 on">
							验证邮箱
						</li>
						<li class="li_2 on">
							信息提交
						</li>
						<li class="li_3">
							完成注册
						</li>
					</ul>
				</div>
				<div class="info_box ">
					<form class="registerform" id="form1">
						<div class="d1">已验证邮箱<span>${(Session["EmailVerifyPassed"])!}</span></div>
						<ul>
							<li>
								<div class="t clearfix">
									<label class="fl"><i>* </i>登录密码</label>
									<input type="hidden" name="email" id="email" value="${(Session["EmailVerifyPassed"])!}" />
									<input type="password" class="fl text inputxt" placeholder="设置您的登录密码" id="password" name="password" />
									<span id="passwordTip"></span>
								</div>
							</li>
							<li>
								<div class="t clearfix">
									<label class="fl"><i>* </i>确认密码</label>
									<input type="password" class="fl text inputxt" placeholder="请再次输入您的密码" name="confirmPassword" id="confirmPassword"/>
									<span id="confirmPasswordTip"></span>
								</div>
							</li>
							<li>
								<div class="t clearfix">
									<label class="fl"><i>* </i>公司名称</label>
									<input type="text" class="fl text inputxt" placeholder="请输入您的公司名称" name="name" id="name" />
									<span id="nameTip"></span>
								</div>
							</li>
							<li>
								<div class="t clearfix">
									<label class="fl"><i>* </i>手机号码</label>
									<input type="text" class="fl text inputxt" placeholder="请输入您的手机号码" name="mobile" id="mobile" maxlength="11" />
									<span id="mobileTip"></span>
								</div>
							</li>
							<li>
								<div class="t clearfix">
									<label class="fl"><i>* </i>验证码</label>
									<input type="text" class="fl text_s inputxt" placeholder="请输入验证码" name="code" id="code" maxlength="4" />
									<img id="img_code" src="${rc.contextPath}/verify_code/get" alt="验证码"/>
									<span id="codeTip"></span>
								</div>
							</li>
							<li>
								<div class="t clearfix">
									<label class="fl"><i>* </i>短信动态码 </label>
									<input type="text" class="fl text_s inputxt" placeholder="短信动态码" name="smsCode" id="smsCode" maxlength="6">
									<input type="button" id="btn_smscode" class="btn_smscode" value="获取短信动态码">
									<span id="smsCodeTip"></span>
								</div>
							</li>
							
							<li>
								<div class="t clearfix">
									<input type="button" class="submit_btn" value="提交" />
								</div>
							</li>
						</ul>
					</form>
				</div>
			</div>
		</div>
	</div>
	<#include "/common/footer.ftl"/>
	<script type="text/javascript">
	$(function(){
	
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
	
		$.formValidator.initConfig({formID:"form1", onSuccess : function(){
			$.ajax({
	             type : "POST",
	             url : "${rc.contextPath}/register/commit",
	             data : $("#form1").serialize(),
	             dataType : "json",
	             success: function(data){
	             	 if(data){
	             	 	location.href = "${rc.contextPath}/register/complete";
	             	 } else {
	             	    Hs.alert("提交失败");
	             	 }
	             }
	         });
		}});
		
		$("#password").formValidator({
			onShow : "请输入6-18字符，数字、字母和符号组合",
			onFocus : "请输入6-18字符，数字、字母和符号组合",
			onCorrect : "",
		}).regexValidator({
			regExp : "password",
			dataType : "enum",
			onError : "必须是6-18字符，数字、字母和符号组合"
		}).inputValidator({
			empty : {leftEmpty:false,rightEmpty:false,emptyError:"只能包含数字、字母和符号（除空格）"}
		});
		
		$("#confirmPassword").formValidator({
			onShow : "请再次输入您的密码",
			onFocus : "请再次输入您的密码",
			onCorrect : "",
		}).inputValidator({
		 	min:1,
		 	empty:{leftEmpty:false,rightEmpty:false,emptyError:"请再次输入您的密码"},
		 	onError:"请再次输入您的密码"
		}).compareValidator({
			desID:"password",
			operateor:"=",
			onError:"两次密码输入不一致"
		});
		
		$("#name").formValidator({
			onShow : "请输入您的公司名称",
			onFocus : "请输入您的公司名称",
			onCorrect : "",
		}).inputValidator({
		 	min:1,
		 	empty:{leftEmpty:false,rightEmpty:false,emptyError:"公司名称不能为空字符，请输入"},
		 	onError:"公司名称不能为空，请输入"
		});
		
		$("#mobile").formValidator({
			onShow : "请输入您的手机号码",
			onFocus : "请输入您的手机号码",
			onCorrect : "",
		}).regexValidator({
			regExp : "mobile",
			dataType : "enum",
			onError : "请输入有效的手机号码"
		}).ajaxValidator({
		 	type : "POST",
            url: "${rc.contextPath}/register/mobile_exists",
            success: function (data) {
            	if(data=="true")
            		return "该手机号码已存在，请更换手机号码";
            	return true;
            },
            onError : "该手机号码已存在，请更换手机号码",
            onWait : "正在校验，请稍候...",
            error: function(jqXHR, textStatus, errorThrown){alert("服务器交互失败，请重试"+errorThrown);}
        });
		
		$("#code").formValidator({
			onShow : "请输入图形验证码内容",
			onFocus : "请输入图形验证码内容",
			onCorrect : "",
		}).inputValidator({
			min:4,
		 	empty:{leftEmpty:false,rightEmpty:false,emptyError:"请输入图形验证码内容"},
		 	onError:"请输入有效验证码"
		}).ajaxValidator({
            url: "${rc.contextPath}/verify_code/validate",
            type : "POST",
            success: function (data) {
            	if(data=="false")
            		return "验证码输入错误";
            	return true;
            },
            onError : "验证码输入错误",
            onWait : "正在校验，请稍候...",
            error: function(jqXHR, textStatus, errorThrown){alert("服务器交互失败，请重试"+errorThrown);}
        });
        
        $("#smsCode").formValidator({
			onShow : "请输入短信动态码",
			onFocus : "请输入短信动态码",
			onCorrect : "",
		}).inputValidator({
			min:6,
		 	empty:{leftEmpty:false,rightEmpty:false,emptyError:"请输入6位数字动态码"},
		 	onError:"请输入6位数字动态码"
		}).regexValidator({
			regExp : "num",
			dataType : "enum",
			onError : "请输入6位数字动态码"
		}).ajaxValidator({
            url: "${rc.contextPath}/verify_code/mcode_validate",
            type : "POST",
            success: function (data) {
            	if(data=="false")
            		return "短信动态码输入错误";
            	return true;
            },
            onError : "短信动态码输入错误",
            onWait : "正在校验，请稍候...",
            error: function(jqXHR, textStatus, errorThrown){alert("服务器交互失败，请重试"+errorThrown);}
        });
        
        $("#img_code").on("click", function(){
			$("#code").val("");        	
        	$(this).attr("src", "${rc.contextPath}/verify_code/get?r="+Math.random());
        });
        
        var countdown=60;
			
		$.go = function(){
			if (countdown == 0) {
				$("#btn_smscode").attr("class", "btn_smscode");
				$("#btn_smscode").removeAttr("disabled");
				$("#btn_smscode").val("获取短信动态码");
		        countdown = 60; 
		        return;
		    } else {
		    	$("#btn_smscode").val("重新发送(" + countdown + "s)");
		    	$("#btn_smscode").attr("class", "btn_smscode_w");
		        $("#btn_smscode").attr("disabled", true);
		        countdown--; 
		    }
		    setTimeout("$.go()" ,1000);
		};
		
		$(".btn_smscode").on("click", function(){
			var result = $.formValidator.oneIsValid("mobile");
			if(!result.isValid){
				$.formValidator.setFailState("mobile", result.setting.onError);
				return;
			}
			result = $.formValidator.oneIsValid("code");
			if(!result.isValid){
				$.formValidator.setFailState("code", result.setting.onError);
				return;
			}
			$.ajax({
	             type : "POST",
	             url : "${rc.contextPath}/verify_code/mcode",
	             data : {
	             	mobile : $.trim($("#mobile").val()),
	             	code : $.trim($("#code").val())
	             },
	             dataType : "json",
	             success: function(data){
	             	 if(data==0){
	             	 	$.go();
	             	 } else if(data==1){
	             	 	$.formValidator.setFailState("mobile", "手机号码验证失败");
	             	 } else if(data==2){
	             	 	$.formValidator.setFailState("code", "验证码输入错误或过期");
	             	 } else if(data==3){
	             	 	$.formValidator.setFailState("code", "发送过于频繁，请稍后");
	             	 } else{
	             	 	$.formValidator.setFailState("code", "短信处理失败");
	             	 }
	             }
	        });
		});
		
		$(".submit_btn").on("click", function(){
			$.formValidator.pageIsValid('1');
		});
		
	});


	</script>
</body>
</html>