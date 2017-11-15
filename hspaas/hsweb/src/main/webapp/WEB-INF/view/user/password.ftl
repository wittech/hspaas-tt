<form id="password_form" method="post">
	<input type="hidden" name="id" id="password_form_id" value="${(id)!}"/>
    <div class="xc_container">
		<div class="xc_edit_con">
			<div class="xc_edit_field">
				<dl>
					<dt>原密码</dt>
					<dd>
						<input type="password" id="plainPassword" maxlength="30" name="plainPassword"/><br/>
						<span id="plainPasswordTip" class="error"></span>
					</dd>
				</dl>
			</div>
			<div class="xc_edit_field">
				<dl>
					<dt>新密码</dt>
					<dd>
						<input type="password" id="newPassword" maxlength="30" name="newPassword"/><br/>
						<span id="newPasswordTip" class="error"></span>
					</dd>
				</dl>
			</div>
			<div class="xc_edit_field">
				<dl>
					<dt>确认密码</dt>
					<dd>
						<input type="password" id="confirmPassword" maxlength="30" name="confirmPassword"/><br/>
						<span id="confirmPasswordTip" class="error"></span>
					</dd>
				</dl>
			</div>
		</div>
	</div>
</form>
<script>

	$(function(){
		$.formValidator.initConfig({formID:"password_form", submitOnce : true, onSuccess:function(){
			$.ajax({
				url : "${rc.contextPath}/user/update_password",
				dataType:"json",
				data:{
					'plainPassword' :$("#plainPassword").val(),
					'newPassword' :$("#newPassword").val()
				},
				type : "post",
				success:function(data){
					$("#loading-indicator-undefined").hide();
					if(data){
						$('.clsBtn').trigger("click");
						Hs.alert("修改成功！", function() {
							Hs.close();
						});
					}else{
						Hs.alert("用户名或密码输入错误！");
					}
				}
			});
		}});
		
	
		$("#plainPassword").formValidator({
			onShow : "请输入原密码！",
			onFocus : "请输入原密码！",
			onCorrect : "",
		}).regexValidator({
			regExp : "password",
			dataType : "enum",
			onError : "原密码必须是6-18字符，数字、字母和符号组合！"
		}).inputValidator({
			empty : {leftEmpty:false,rightEmpty:false,emptyError:"只能包含数字、字母和符号（除空格）"}
		}).ajaxValidator({
			type : "POST",
            url: "${rc.contextPath}/user/password_exists",
            success: function (data) {
            	if(data=="false")
            		return "原密码输入错误！请重新输入！";
            	return true;
            },
            error: function(jqXHR, textStatus, errorThrown){alert("服务器交互失败，请重试"+errorThrown);},
            onError : "原密码输入错误！请重新输入！",
            onWait : "正在校验，请稍候...",
            buttons: $(".send_btn")
        });
		
		$("#newPassword").formValidator({
			onShow : "请输入新密码！",
			onFocus : "请输入新密码！",
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
			desID:"newPassword",
			operateor:"=",
			onError:"两次密码输入不一致"
		});
	});
	
</script>