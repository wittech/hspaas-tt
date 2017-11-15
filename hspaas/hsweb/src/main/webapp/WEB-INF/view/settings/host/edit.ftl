<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-系统设置-ip白名单编辑</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_settings','settings','settings_host_white_send');
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
				<li><a href="${rc.contextPath}/settings/host/index">ip白名单记录</a></li>
				<li class="active"><a href="${rc.contextPath}/settings/host/create">ip白名单</a></li>
				<li><a href="${rc.contextPath}/settings/mobile/white/index">白名单记录</a></li>
				<li><a href="${rc.contextPath}/settings/mobile/white/create">白名单</a></li>
				<li><a href="${rc.contextPath}/settings/mobile/black/index">黑名单记录</a></li>
				<li><a href="${rc.contextPath}/settings/mobile/black/create">黑名单</a></li>
			</ul>
		</div>
		<form id="myForm" method="post">
		<input type="hidden" name="id" value="${(hostWhiteList.id)!}">
		<input type="hidden" name="status" value="${(hostWhiteList.status)!}">
		<div class="edit_box edit_basic">
				<h1>ip白名单设置</h1>
				<div class="edit_ctn">
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>ip地址：</dt>
							<dd class="textarea_type">
								<textarea style="height:200px;min-width: 460px;" placeholder="请输入ip地址" id="ip" name="ip">${(hostWhiteList.ip)!}</textarea>
								<span id="ipTip" class="error"></span>
							</dd>
						</dl>
					</div>
				</div>
		</div>
		<div class="edit_btn">
			<input type="button" value="保存" onclick="update();" class="bg-blue">
		</div>
		</form>
		<br/>
	</div>
	<!--右侧main-->
</div>
<script>
	$(function(){
		isAvaiableIps = function(){
			var arr = $('#ip').val().split("\n");
			var tip = "";
			$.each(arr, function(i){
				if(arr[i].length>0){
					if(!isIP(arr[i])){
				        tip = "请输入正确ip地址"
				        return false;
					}
				}
			});
			if(tip == "")
				return true;
			return tip;
		};
		$("#ip").focus();
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
		$.formValidator.initConfig({formID:"myForm"});
		$("#ip").formValidator({
			onShow : "请输入ip地址",
			onFocus : "请输入ip地址",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入ip地址"
		}).functionValidator({
			fun:isAvaiableIps
		});
	});

	//保存
	function update(){
		if ($.formValidator.pageIsValid('1')){
			$.ajax({
				url : "${rc.contextPath}/settings/host/update",
				dataType:"json",
				data:$('#myForm').serialize(),
				type : "post",
				beforeSend : function(){
				},
				success:function(data){
					if(data==2){
						Hs.alert("当前ip地址重复请修改！");
					}else if(data==1){
						Hs.alert("保存成功！", function() {
							location.href = "${rc.contextPath}/settings/host/index";
						});
						return;
					}else{
						Hs.alert("保存失败");
					}
				}
			});
		}
	}
</script>
</body>
</html>