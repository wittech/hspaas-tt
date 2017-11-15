<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-系统设置-基础信息</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_settings','settings','user_basis');
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
				<li><a href="javascript:void(0)">系统管理</a></li>
				<li class="active"><a href="${rc.contextPath}/user/basis">基础信息</a></li>
			</ul>
		</div>
		<form id="myForm" method="post">
			<input type="hidden" name="id" id="userBaseId" value="${(userBase.id)!}">
	        <div class="edit_box edit_basic developer_info">
				<h1>基础信息</h1>
				<div class="edit_ctn">
					<div class="edit_field">
						<dl class="dl1">
							<dt>用户账号</dt>
							<dd><span class="txt">${(user.email)!}</span></dd>
						</dl>
						<#--
						<dl>
							<dt>开发者认证：</dt>
							<dd>
								<span class="txt">注册用户<a href="/user/oAuthDispather" class="do">立即认证</a></span>
							</dd>
						</dl>
							-->						
					</div>
					<div class="edit_field">
						<dl>
							<dt>公司名称</dt>
							<dd><span class="txt">${(userBase.company)!}</span></dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt>注册手机号</dt>
							<dd><span class="txt">${(Session["LOGIN_USER_SESSION_KEY"].mobile)!}</span><a href="javascript:replaceNumber();" class="blue float_link">变更手机号</a></dd>
						</dl>
					</div>
	                <div class="edit_field">
	                   <dl>
	                       <dt><i class="star"></i>邮寄地址</dt>
	                       <dd>
	                       		<input type="text" id="address" maxlength="30" name="address" value="${(userBase.address)!}">
	                            <span id="sign_error" class="error"></span>
	                       </dd>
	                    </dl>
	                </div>
					<div class="edit_field">
						<dl>
							<dt>登录密码</dt>
							<dd><span class="txt">*******</span><a href="javascript:updatePassword();" data_id="${(userBase.id)!}" class="blue float_link">修改密码</a></dd>
						</dl>
					</div>
				</div>
			</div>
			<div class="edit_btn">
				<input type="button" onclick="update();" value="保存" id="base_button_id" class="bg-blue">
			</div>
		</form>
	</div>
   <!--右侧main-->
</div>
<script>
	function replaceNumber(){
		window.wxc.xcConfirm("${rc.contextPath}/user/replace_number?id=${(userBase.id)!}", window.wxc.xcConfirm.typeEnum.form, {
			title : "手机号码变更",
			height : 500,
			width : 700,
			onOk:function(v){
				$.formValidator.pageIsValid('1');
			}
		});
	}
	
	function updatePassword(){
		window.wxc.xcConfirm("${rc.contextPath}/user/password", window.wxc.xcConfirm.typeEnum.form, {
			title : "密码修改",
			height : 400,
			width : 550,
			onOk:function(v){
				$.formValidator.pageIsValid('1');
			}
		});
	}
	
	//保存
	function update(){
		$.ajax({
			url : "${rc.contextPath}/user/update_submit",
			dataType:"json",
			data:$('#myForm').serialize(),
			type : "post",
			beforeSend : function(){
			},
			success:function(data){
				if(data){
					Hs.alert("保存成功！", function() {
							Hs.close();
					});
				}else{
					Hs.alert("保存失败！");
				}
			}
		});
	}
</script>
<script>
</script>
</body>
</html>