<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-系统管理-添加地址</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript" src="${rc.contextPath}/assets/js/PCASClass.js"></script>
<script type="text/javascript">
$(function(){
	loadMenu('li_settings','settings','settings_address_book_list');
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
                <li class="active"><a href="${rc.contextPath}/settings/address_book/index">地址管理</a></li>
			</ul>
		</div>
		<form id="myForm" method="post">
		<input type="hidden" name="isDefault" id="isDefaults" value="0">
		<input type="hidden" name="id" id="id">
		<div class="edit_box edit_basic">
				<h1>地址添加</h1>
				<div class="edit_ctn">
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>姓名</dt>
							<dd><input type="text" id="name" placeholder="请输入姓名" maxlength="10" name="name" style="width: 215px;">
								<span id="nameTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>手机号码</dt>
							<dd><input type="text" id="mobile" placeholder="请输入手机号码" maxlength="11" name="mobile" style="width: 215px;">
								<span id="mobileTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>省</dt>
							<dd class="select_type">
								<select class="select" name="province" id="province"></select>
                                <span id="provinceTip" class="error"></span>
							</dd>						
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>城市</dt>
							<dd class="select_type">
								<select class="select" id="city" name="city">
                                </select>
                                <span id="cityTip" class="error"></span>
							</dd>						
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>区/县</dt>
							<dd class="select_type">
								<select class="select" id="area" name="area">
                                </select>
                                <span id="areaTip" class="error"></span>
							</dd>						
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>地址</dt>
							<dd><input type="text" id="address" class="address" placeholder="请输入地址" name="address">
								<span id="addressTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt>邮编</dt>
							<dd><input type="text" placeholder="请输入邮编" maxlength="7" name="zipCode" style="width: 215px;">
								<span id="zipCodeTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt>是否默认</dt>
							<dd><input type="checkbox" id="isDefault" onclick="checkStatus()" style="margin-top:13px;">
								<span id="isDefaultTip" class="error"></span>
							</dd>
						</dl>
					</div>
				</div>
				</div>
			<div class="edit_btn">
				<input type="button" value="保存" onclick="preservation(1);" class="bg-blue">
			</div>
		</div>
		</form>
		<br/>
	</div>
	<!--右侧main-->
</div>
<script>
	new PCAS("province","city","area");
	$(function(){
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
		$.formValidator.initConfig({formID:"myForm", submitOnce : true, onSuccess:function(){
			$.ajax({
				url : "${rc.contextPath}/settings/address_book/insert",
				dataType:"json",
				data:$('#myForm').serialize(),
				type : "post",
				success:function(data){
					if(data){
						Hs.alert("保存成功！", function() {
							location.href = "${rc.contextPath}/settings/address_book/index";
						});
						return;
					}else{
						Hs.alert("操作失败！");
					}
				}
			});
		}});
		
		$("#name").formValidator({
			onShow : "请输入姓名",
			onFocus : "请输入姓名",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入姓名"
		});
		
		$("#mobile").formValidator({
			onShow : "请输入手机号码",
			onFocus : "请输入手机号码",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入手机号码"
		}).regexValidator({
			regExp : "mobile",
			dataType : "enum",
			onError : "请输入有效的手机号码"
		});
		
		$("#province").formValidator({
			onShow : "请选择省",
			onFocus : "请选择省",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请选择省"
		});
		
		/**
		$("#city").formValidator({
			onShow : "请选择城市",
			onFocus : "请选择城市",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请选择城市"
		});
		
		$("#area").formValidator({
			onShow : "请选择区县",
			onFocus : "请选择区县",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请选择区县"
		});
		*/
		
		$("#address").formValidator({
			onShow : "请输入地址",
			onFocus : "请输入地址",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入地址"
		});
	});

	//保存
	function preservation(){
		$.formValidator.pageIsValid('1');
	}
	
	function checkStatus(type){
		var isChecked = $("#isDefault").is(':checked');
		if(isChecked){
			$("#isDefaults").val("1");
		}else{
			$("#isDefaults").val("0");
		}
		
	}
	  
</script>
</body>
</html>