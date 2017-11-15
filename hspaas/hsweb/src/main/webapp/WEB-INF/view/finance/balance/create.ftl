<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-发票管理-开发票</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_finance','financial','financial_balance_list');
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
				<li><a href="javascript://">财务管理</a></li>
                <li class="active"><a href="${rc.contextPath}/finance/balance/index">发票管理</a></li>
			</ul>
		</div>
		<form id="myForm" method="post">
		<input type="hidden" name="id" value="">
		<div class="edit_box edit_basic">
				<h1>开发票</h1>
				<span><label><a href="javascript:addressAlert();" style="color:red;">我的地址薄</a></span>
				<div class="edit_ctn">
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>开票类型</dt>
							<dd class="select_type">
                                <select class="select" name="type" id="type" onchange="invoiceType();">
                                	<option value="">请选择</option>
                                    <option value="0">普通发票</option>
                                    <option value="1">增值税发票</option>
                                </select>
                                <span id="typeTip" class="error"></span>
							</dd>						
						</dl>
					</div>
					<div class="edit_field" id="taxNumberDiv" style="display:none;">
						<dl>
							<dt><i class="star"></i>税号</dt>
							<dd><input type="text" id="taxNumber" placeholder="请输入税号" maxlength="50" name="taxNumber" style="width: 217px;">
								<span id="taxNumberTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>发票抬头</dt>
							<dd><input type="text" id="title" placeholder="请输入发票抬头" maxlength="50" name="title" style="width: 217px;">
								<span id="titleTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>收件人</dt>
							<dd><input type="text" id="name" placeholder="请输入是收件人" maxlength="10" name="mailMan" style="width: 217px;">
								<span id="nameTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>联系方式</dt>
							<dd><input type="text" id="mobile" placeholder="请输入联系方式" maxlength="11" name="mobile" style="width: 217px;">
								<span id="mobileTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>开票金额</dt>
							<dd><input type="text" id="money" placeholder="请输入开票金额" maxlength="10" name="money" style="width: 217px;">
								<span id="moneyTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>收货地址</dt>
							<dd>
								<input type="text" id="address" placeholder="请输入地址" name="address" style="width: 217px;">
								<span id="addressTip" class="error"></span>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>备注：</dt>
							<dd class="textarea_type">
								<textarea style="height:200px;min-width: 460px;" id="memo" placeholder="请输入备注" name="memo"></textarea>
								<span id="memoTip" class="error"></span>
							</dd>
						</dl>
					</div>
				</div>
		</div>
		<div class="edit_btn">
			<input type="button" value="保存" onclick="preservation(1);" class="bg-blue">
		</div>
		</form>
		<br/>
	</div>
	<!--右侧main-->
</div>
<script>
	$(function(){
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
		$.formValidator.initConfig({formID:"myForm", submitOnce : true, onSuccess:function(){
			$.ajax({
				url : "${rc.contextPath}/finance/balance/save",
				dataType:"json",
				data:$('#myForm').serialize(),
				type : "post",
				beforeSend : function(){
				},
				success:function(data){
					if(data){
						Hs.alert("保存成功！", function(){
				    	 	location.href = "${rc.contextPath}/finance/balance/index";
				    	});
						return;
					}else{
						Hs.alert("没有可开票余额！");
					}
				}
			});
		}});
		
		$("#type").formValidator({
			onShow : "请选择发票类型",
			onFocus : "请选择发票类型",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请选择发票类型"
		});
		
		$("#taxNumber").formValidator({
			onShow : "请输入税号",
			onFocus : "请输入税号",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入税号"
		});
		
		$("#title").formValidator({
			onShow : "请输入发票抬头",
			onFocus : "请输入发票抬头",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入发票抬头"
		});
		
		$("#name").formValidator({
			onShow : "请输入收件人",
			onFocus : "请输入收件人",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入收件人"
		});
		
		$("#mobile").formValidator({
			onShow : "请输入联系方式",
			onFocus : "请输入联系方式",
			onCorrect : "",
		}).regexValidator({
			regExp : "mobile",
			dataType : "enum",
			onError : "请输入正确的联系方式"
    	});
		
		$("#money").formValidator({
			onShow : "请输入发票金额",
			onFocus : "请输入发票金额",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入发票金额",
		}).regexValidator({
			regExp : "num",
			dataType : "enum",
			onError : "请输入数字"
		});
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
	
	function addressAlert(){
		window.wxc.xcConfirm("${rc.contextPath}/settings/address_book/address_list", window.wxc.xcConfirm.typeEnum.list, {
			title : "地址管理",
			height : 400,
			width : 600,
			onOk:function(v){
				choose();
			}
		});
	}
	
	//发票类型
	function invoiceType(){
		var type =$("#type").val();
		if(type=="1"){
			$("#taxNumberDiv").show();
		}else{
			$("#taxNumber").val("");
			$("#taxNumberDiv").hide();
		}
	}
</script>
</body>
</html>