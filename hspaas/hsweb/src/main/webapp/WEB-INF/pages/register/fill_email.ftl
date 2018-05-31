<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta content="" name="description">
	<title>华时融合平台-用户注册-填写邮箱信息</title>
	<#include "/website/assets.ftl"/>
	<#include "/website/loading.ftl"/>
	<#include "/website/validator.ftl"/>
</head>
<body>
	<#include "/website/header.ftl"/>
	<div class="reg_page ">
		<div class="inner">
			<div class="reg_box reg_step1_box">
				<div class="step_box">
					<div class="tit">华时通行证注册</div>
					<ul>
						<li class="li_1 on">
							验证邮箱
						</li>
						<li class="li_2">
							信息提交
						</li>
						<li class="li_3">
							完成注册
						</li>
					</ul>
				</div>
				<form id="form1">
				<div class="message_box" id="box_input">
					<div class="d1">
						<#if message??>
						温馨提示：<span style="color:#F00;font-weight:700;font-size:18px;">${(message)!}</span><br/>点击「发送」按钮，注册账号
						<#else>
						Hspaas 将发送一封验证邮件到你的邮箱，此邮箱将作为登录用户名。<br/>点击「发送」按钮，注册账号
						</#if>
					</div>
					<div class="d2">
						<input id="email" name="email" type="text" class="text j_input" placeholder="您请输入您的的邮箱">
						<span id="emailTip"></span>
					</div>
					<div class="d3">
						<a href="javascript:void(0);" class="send_btn">发送</a>
					</div>
					<div class="d4">
						注：当您点击发送默认认同并遵守 <span class="s">《华时融合平台服务条款》</span> 的内容
					</div>
				</div>
				<div class="message_box message_sended_box" id="box_message" style="display:none;">
					<div class="d1">
						Hspaas 已将注册链接发送至您提供的邮箱内，请登录您的邮箱点击邮件内注册链写成后续信息补全。
					</div>
					<div class="d2">
						您的邮箱 <span id="send_email"></span>邮件已发送
					</div>
					<div class="d3">
						<a href="javascript:void(0);" class="login_btn">登录邮箱</a>
					</div>
				</div>
				</form>
			</div>
		</div>
	</div>
	<#include "/website/footer.ftl"/>
<script type="text/javascript">
	$(function(){
	
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
		
		$.formValidator.initConfig({formID:"form1", onSuccess:function(){
			var email = $.trim($("#email").val());
			Hs.ajax({
	             url : "/register/send_email",
	             data : {email : email},
	             success: function(data){
	             	 if(data){
	             	 	$("#box_input").hide();
	             	 	$("#send_email").html(email);
	             	 	$("#box_message").show();
	             	 	return;
	             	 }
	             	 Hs.alert("发送失败");
	             },
	             error : function(){
	             	Hs.alert("发送失败");
	             }
	        });
		}});
		
		$("#email").formValidator({
			onShow : "输入您的邮箱",
			onFocus : "输入您的邮箱",
			onCorrect : "",
		}).regexValidator({
			regExp : "email",
			dataType : "enum",
			onError : "您输入的邮箱有误"
		}).ajaxValidator({
			type : "POST",
            url: "${rc.contextPath}/register/email_exists",
            success: function (data) {
            	if(data=="true")
            		return "该邮箱已存在，请更换邮箱";
            	return true;
            },
            error: function(jqXHR, textStatus, errorThrown){alert("服务器交互失败，请重试"+errorThrown);},
            onError : "该邮箱已存在，请更换邮箱",
            onWait : "正在校验，请稍候...",
            buttons: $(".send_btn")
        });
	
		$(".send_btn").on("click", function(){
			$.formValidator.pageIsValid('1');
		});
		
		$(".login_btn").on("click", function(){
			var mail = $("#email").val();
			var begin = mail.indexOf("@");
			var end = mail.lastIndexOf(".");
			mail = mail.substring(begin+1,end);
			var url = "http://mail."+mail+".com";
			window.open(url, "_blank");
		});
	});
</script>
</body>
</html>