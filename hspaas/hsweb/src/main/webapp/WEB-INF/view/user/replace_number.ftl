<form id="replace_form" method="post">
	<input type="hidden" name="id" id="password_form_id" value="${(id)!}"/>
    <div class="xc_container">
		<div class="xc_edit_con">
			<div class="xc_edit_field">
				<dl>
					<dt>手机号码</dt>
					<dd>
						<input type="text" id="mobile" maxlength="11" name="mobile"/><br/>
						<span id="mobileTip" class="error"></span>
					</dd>
				</dl>
			</div>
			<div class="xc_edit_field">
				<dl>
					<dt>图形验证码 </dt>
					<dd>
						<input type="text" class="fl text_s inputxt" placeholder="请输入验证码" name="code" id="code" maxlength="4" />
						<img id="img_code" src="${rc.contextPath}/verify_code/get" alt="验证码"/>
						<br/><span id="codeTip"></span>
					</dd>
				</dl>
			</div>
			<div class="xc_edit_field">
				<dl>
					<dt>短信动态码 </dt>
					<dd>
						<input type="text" class="fl text_s inputxt" placeholder="短信动态码" name="smsCode" id="smsCode" maxlength="6">
						<input type="button" id="btn_smscode" class="btn_smscode" value="获取短信动态码">
						<br/><span id="smsCodeTip"></span>
					</dd>
				</dl>
			</div>
		</div>
	</div>
</form>
<script>

	$(function(){
		$.formValidator.initConfig({formID:"replace_form", submitOnce : true, onSuccess:function(){
			$.ajax({
				url : "${rc.contextPath}/user/replace_number_submit",
				dataType:"json",
				data:$('#replace_form').serialize(),
				type : "post",
				success:function(data){
					$("#loading-indicator-undefined").hide();
					if(data){
						$('.clsBtn').trigger("click");
						Hs.alert("提交成功！", function() {
							Hs.close();
						});
					}else{
						Hs.alert("提交失败！");
					}
				}
			});
		}});
		
	
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
        
        $(".btn_smscode").on("click", function(){
			var result = $.formValidator.oneIsValid("mobile");
			if(!result.isValid){
				$.formValidator.setFailState("mobile", result.setting.onError);
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
	             	 	$.formValidator.setFailState("mobile", "短信处理失败");
	             	 }
	             }
	        });
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
	});
	
</script>