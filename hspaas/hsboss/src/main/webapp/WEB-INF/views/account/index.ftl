<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>登录</title>
    <link href="${BASE_PATH}/resources/css/bootstrap.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/signin.css" rel="stylesheet">
    <script type="text/javascript" src="${BASE_PATH}/resources/js/jquery-1.11.0.js"></script>
    <style type="text/css">
		.login_error{
			border:solid 1px;
			border-color:red;
		}
	</style>
  </head>
  <body style="font-family:'微软雅黑'">
    <div class="container">
      <form class="form-signin" id="myform">
        <h2 class="form-signin-heading">华时-融合平台 </h2>
        <label for="inputEmail" class="sr-only">登录名</label>
        <input type="email" id="loginName" name="loginName" class="form-control" placeholder="请输入登录名">
        <label for="inputPassword" class="sr-only">登录密码</label>
        <input type="password" id="password" name="password" class="form-control" placeholder="请输入登录密码">
        
        <div>
         <label for="inputAuthCode" class="sr-only">验证码</label>
         <input type="text" id="authCode" name="authCode" maxlength="4" style="width:170px" class="form-control validate[required,maxSize[4]" placeholder="验证码">
         <img src="${BASE_PATH}/account/validate_code" title="点击刷新验证码" onclick="refash_validate();" id="validate_image" class="img-thumbnail" style="width:125px;height:44px;float:left;position: relative; margin: -44px 175px;">
        </div>
        <button class="btn btn-lg btn-primary btn-block" onclick="formLogin();" style="margin-top:10px" type="button">登&nbsp;&nbsp;&nbsp;录</button>
      </form>
    </div>
  </body>
  <script type="text/javascript">
  
  		$(function(){
		
			$(document).keydown(function(event){ 
				if(event.keyCode == 13){
					formLogin();
				}
			}); 
		});
  
  		function formLogin(){
  			var loginName = $('#loginName').val();
  			if($.trim(loginName) == ""){
  				alert('请输入登录名！');
  				$('#loginName').focus();
  				return;
  			}
  			
  			var password = $('#password').val();
  			if($.trim(password) == ''){
  				alert('请输入登录密码！');
  				$('#password').focus();
  				return;
  			}
  			
  			var authCode = $('#authCode').val();
  			if($.trim(authCode) == ''){
  				alert('请输入验证码！');
  				$('#authCode').focus();
  				return;
  			}
  			
  			
  			$.ajax({
	  			url:'${BASE_PATH}/account/login',
	  			dataType:'json',
	  			data:$('#myform').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
	  					location.href = "${BASE_PATH}/main"
	  				}else{
	  					alert(data.message);
	  					$('.login_error').css('z-index',0);
	  					$('.login_error').removeClass('login_error');
	  					$('#'+data.input).css('z-index',1);
	  					$('#'+data.input).addClass('login_error');
	  					refash_validate();
	  					$("#"+data.input).focus();
	  				}
	  			},error:function(data){
	  				alert('登录失败!');
	  			}
	  		});
  		}
  	
  		function refash_validate(){
  			var url = "${BASE_PATH}/account/validate_code?rnd="+Math.random();
  			$('#validate_image').attr('src',url);
  		}
  	
  </script>
</html>
