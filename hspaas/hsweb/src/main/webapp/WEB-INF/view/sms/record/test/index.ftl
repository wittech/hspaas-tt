<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-短信测试</title>
<#include "/common/common.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<#include "/common/loading.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_dx','dx_send','dx_send_test');
});
</script>
</head>
<body>
<#include "/common/navigation.ftl"/>
<div class="content">
	<#include "/common/sidebar.ftl"/>	    

	<div class="main">
		<div class="breadcrumbs">
			<ul>
				<li><a href="javascript:void(0)">短信管理</a></li>
                <li class="active"><a href="${rc.contextPath}/sms/record/test/index">验证码测试</a></li>
			</ul>
		</div>
        
		<form id="myform">
			<input type="hidden" name="securityCode" value="${(security_code)!}">
			<div class="edit_box edit_basic">
				<h1>短信验证码测试</h1>
				<div class="edit_ctn">
					<div class="edit_field">
						<dl>
							<dd class="select_type">
								【华时科技】：${(content)!}${(security_code)!}。手机号码&nbsp;&nbsp;
                                <input type="text" id="mobile" name="mobile" value="${(mobile)!}"/>
                                <span id="mobileTip"></span><br/>
							</dd>						
						</dl>
					</div>
			</div>
		</form>
		<div class="edit_btn">
			<input type="button" value="发送短信验证码" id="validcode" class="bg-blue"><br/><br/>
			<span id="errTip" style="color:coral;"></span>
		</div>
		<br/>
	</div>
</div>
<script>
	$(function(){
		var countdown=10;
		$.go = function(){
			if (countdown == 0) {
				$("#validcode").attr("class", "bg-blue");
				$("#validcode").removeAttr("disabled");
				$("#validcode").val("发送短信验证码");
		        countdown = 60; 
		        return;
		    } else {
		    	$("#validcode").val("重新发送(" + countdown + "s)");
		    	$("#validcode").attr("class", "button bg-gray");
		        $("#validcode").attr("disabled", true);
		        countdown--; 
		    }
		    setTimeout("$.go()" ,1000);
		};
		
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
	
		$.formValidator.initConfig({formID:"myForm"});
		
		function send(){
			if ($.formValidator.pageIsValid('1')){
		    	$.ajax({
		    		url : "${rc.contextPath}/sms/record/test/send",
		    		dataType:"json",
					data:$('#myform').serialize(),
					type : "post",
					beforeSend:function(){
						_this=$('#validcode');
				        if(_this.hasClass('disabled')){
				            return false;
				        }
				        _this.text('发送中···').addClass('disabled').attr("class", "button bg-gray");
	            	},
		    		success : function(data){
		    			$('#validcode').removeClass('disabled').text('发送短信验证码').attr("class", "bg-blue");
	    				if(data==null){
							$("#errTip").html("<span class=\"text-red\">返回数据异常!</span>");
							return;
						}else if(data.code=="0"){
							$.go();
							$("#errTip").html(data.msg);
						}else{
							$("#errTip").html(data.msg);
						}
		    		}
		    	});
		    }
		};
		
		$("#validcode").click(function(){
			send();
		});
		
		$("#mobile").formValidator({
			onShow : "请输入您的手机号码",
			onFocus : "请输入您的手机号码",
			onCorrect : "",
		}).regexValidator({
			regExp : "mobile",
			dataType : "enum",
			onError : "请输入有效的手机号码"
        });
		
	});
	
</script>
</body>
</html>