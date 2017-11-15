<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-系统设置-白名单</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_dx','dx_send','dx_send_massage_white_list');
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
				<li class="active"><a href="${rc.contextPath}/settings/mobile/white/index">手机白名单</a></li>
			</ul>
		</div>
		<form id="myForm" method="post">
		<input type="hidden" name="id" value="${(white.id)!}">
		<div class="edit_box edit_basic">
				<h1>手机白名单设置</h1>
				<div class="edit_ctn">
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>手机号码：</dt>
							<dd class="textarea_type">
								<textarea style="height:200px;min-width: 460px;" id="mobile" placeholder="请输入手机号码,多个号码每行一个分隔符" name="mobile"></textarea>
								<span id="mobileTip" class="error"></span>
							</dd>
						</dl>
					</div>
				</div>
		</div>
		<div class="edit_btn">
			<input type="button" value="保存" onclick="preservation();" class="bg-blue">
		</div>
		</form>
		<br/>
	</div>
	<!--右侧main-->
</div>
<script>
	$(function(){
		isAvaiableMobile = function(){
			var arr = $('#mobile').val().split("\n");
			var contents = "";
			$.each(arr, function(i){
				if(arr[i].length>0){
					if(arr[i].length !=11){
						contents = "请输入正确手机号码！（第"+(i+1)+"行）"
						return false;
					}
					if(arr[i].length==11){
						if(!/^13[0-9]{9}|15[012356789][0-9]{8}|18[0256789][0-9]{8}|147[0-9]{8}$/.test(arr[i])){
					        contents = "请输入正确手机号码（第"+(i+1)+"行）"
				        	return false;
						}
					}
				}
			});
			if(contents == "")
				return true;
			return contents;
		};
		$("#mobile").focus();
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
		$.formValidator.initConfig({formID:"myForm"});
		
		$("#mobile").formValidator({
			onShow : "请输入手机号码（多个号码请换行输入）",
			onFocus : "请输入手机号码（多个号码请换行输入）",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入手机号码（多个号码请换行输入）"
		}).functionValidator({
			fun:isAvaiableMobile
		});
	});

	function preservation(){
		if ($.formValidator.pageIsValid('1')){
			$.ajax({
				url : "${rc.contextPath}/settings/mobile/white/batchInsert",
				dataType:"json",
				data:$('#myForm').serialize(),
				type : "post",
				beforeSend : function(){
				},
				success:function(data){
					if(data.result_code=="fail"){
						Hs.alert("保存失败！"+data.result_msg);
					}else if(data.result_code=="success"){
						Hs.alert("保存成功！", function() {
							location.href = "${rc.contextPath}/settings/mobile/white/index";
						});
						return;
					}else{
						Hs.alert("保存失败！");
					}
				}
			});
		}
	}
</script>
</body>
</html>