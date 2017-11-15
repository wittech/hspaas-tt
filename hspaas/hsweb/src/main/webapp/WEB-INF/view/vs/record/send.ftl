<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-短信测试</title>
<#include "/common/common.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_yuyin','vs','send');
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
				<li><a href="javascript:void(0)">语音管理</a></li>
                <li class="active"><a href="${rc.contextPath}/vs/record/send">语音验证码测试</a></li>
			</ul>
		</div>
        
		<form id="myform">
			<input type="hidden" name="securityCode" value="${(security_code)!}">
			<div class="edit_box edit_basic">
				<h1>语音验证码测试</h1>
				<div class="edit_ctn">
					<div class="edit_field">
						<dl>
							<dd class="select_type">
								【华时科技】：${(content)!}${(security_code)!}。手机号码&nbsp;&nbsp;
                                <input type="text" id="mobile" name="mobile" value="${(mobile)!}"/>
                                <span id="mobileTip"></span>
							</dd>						
						</dl>
					</div>
			</div>
		</form>
		<div class="edit_btn">
			<input type="button" value="发送语音验证码" id="validcode" class="bg-blue">
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
		    		url : "${rc.contextPath}/vs/record/sendTest",
		    		dataType:"json",
					data:$('#myform').serialize(),
					type : "post",
		    		success : function(data){
		    			alert(data);
		    			$.go();
	    				if(data==null){
							$("#mobileTip").html("<span class=\"text-red\">返回数据异常!</span>");
							return;
						}else if(data[0].mobile == null || data[0].mobile == undefined){
							$("#mobileTip").html("<span class=\"text-red\">"+data+"</span>");
							return;
						}else{
							var msg = "报告数据如下：<br/>-----------------------------------------------<br/>";
							$.each(data, function(i, d){
								var cla = "text-red";
								if(d.code == 0)
									 cla = "text-green";
								msg += "<span class='"+cla+"'>"+d.mobile + " : "+d.msg+"</span><br/>";
							});
							$("#mobileTip").html(msg+"-----------------------------------------------");
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