<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="" name="description">
<title>华时融合平台 - 用户登录</title>
<#include "/common/assets.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator2.ftl"/>
<#include "/common/dialog.ftl"/>
</head>
<body>
	<#include "/common/header.ftl"/>
	<div class="login_page">
		<div class="inner">
			<div class="login_box">
				<div class="top_box clearfix">
					<div class="fl l">
						<div class="hd">华时融合平台登录</div>
						<div class="bd">
							<form id="form1">
								<ul>
									<span id="accountTip"></span>
									<li class="login_input">
										<i class="icon_login un"> </i>
                                    	<input type="text" placeholder="手机号码/邮箱" id="account" name="account">
									</li>
									<span id="passwordTip"></span>
									<li class="login_input">
										<i class="icon_login pwd"> </i>
										<input type="password" placeholder="密码" id="password" name="password" />
									</li>
									<li>
										<input type="button" class="submit_btn" value="登录" /> 
										<#-- <a href="javascript:void(0)" class="link">忘记密码？</a> -->
									</li>
								</ul>
							</form>
						</div>
					</div>
					<div class="fl r c">
						<p>关注公众号，了解更多优惠</p>
						<a href="javascript:void(0);"><img src="${rc.contextPath}/assets/images/qrcode_wechat.jpg" style="width:230px;height:230px;"></a>
					</div>
				</div>
				<div class="f_box">
					<p>还没有华时融合平台帐号？  <a href="${rc.contextPath}/register/fill_email">马上注册>></a></p>
				</div>
			</div>
		</div>
	</div>
	<#include "/common/footer.ftl"/>
	<script type="text/javascript">
	
		$(function(){
		
			$(document).ajaxStart(function(){$('.login_page').showLoading();}).ajaxComplete(function(){
				$('body').hideLoading();
			});
			
			$.login_load = function() {
				$(".submit_btn").css("background", "#cccccc");
				$(".submit_btn").attr("value", "登录中...");
				$(".submit_btn").attr("disabled", true);
			};
			
			$.login_unload = function() {
				$(".submit_btn").css("background", "#319acc");
				$(".submit_btn").attr("value", "登录");
				$(".submit_btn").attr("disabled", false);
			};
	
			$.formValidator.initConfig({formID:"form1", oneByOneVerify : true, onSuccess : function(){
				Hs.ajax({
		             url : "/login/verify",
		             data : $("#form1").serialize(),
		             beforeSend : function(){
		             	$.login_load();
		             },
		             success: function(data){
		             	 if(data){
		             	 	location.href = "${rc.contextPath}/console";
		             	 	return;
		             	 }
		             	 $.login_unload();
		             	 $.formValidator.setFailState("account", "用户名或密码输入错误");
		             }
		         });
			}});
			
			$("#account").formValidator({
				onShow : "",
				onFocus : "请输入您的邮箱或手机号码",
				onCorrect : "",
			}).inputValidator({
			 	min:1,
			 	empty:{leftEmpty:false,rightEmpty:false,emptyError:"您输入的用户名有误"},
			 	onError:"请输入您的邮箱或手机号码"
			});
			
			$("#password").formValidator({
				onShow : "",
				onFocus : "请输入您的登录密码",
				onCorrect : "",
			}).inputValidator({
			 	min:1,
			 	empty:{leftEmpty:false,rightEmpty:false,emptyError:"您输入的用户名有误"},
			 	onError:"请输入您的登录密码"
			});
			
			$("#account").focus();
			
			$(".submit_btn").on("click", function(){
				$.formValidator.pageIsValid('1');
			});
			
		});
		
	</script>
</body>
</html>