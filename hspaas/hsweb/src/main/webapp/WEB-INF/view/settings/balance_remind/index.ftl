<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-系统设置-余额提醒</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<link href="${rc.contextPath}/assets/css/news.css" rel="stylesheet" type="text/css" />
<script type="text/javascript">
$(function(){
	loadMenu('li_settings','settings','settings_balance_remind');
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
				<li class="active"><a href="${rc.contextPath}/settings/balance_remind/index">余额提醒</a></li>
			</ul>
		</div>
			<form id="appForm">
				<input type="hidden" name="type" value="1" id="typeById">
				<input type="hidden" name="id" value="0" id="idBy">
				<input type="hidden" name="status" id="statusById" value="0">
				<div class="msg_sort">
					<input type="button" value="短信" onclick="query('1','all')" id="all">
					<input type="button" value="流量" onclick="query('2','not')" id="not">
					<input type="button" value="语音" onclick="query('3','read')" id="read">
				</div>
				<div class="edit_box edit_basic">
					<h1>余额提醒设置</h1>
					<div class="edit_ctn">
						<div class="edit_field">
							<dl>
								<dt><i class="star"></i>手机号码</dt>
								<dd><input type="text" id="mobile" onblur="checkSign(this)" placeholder="设置您的手机号码" name="mobile" style="width:300px;" value="${(balance.mobile)!}"/>
									<span id="mobileTip"></tip>
								</dd>
							</dl>

							<dl>
								<dt id="display_1" style="display:none;"><i class="star"></i>阀值（短信条数）</dt>
								<dt id="display_2" style="display:none;"><i class="star"></i>阀值（流量M）</dt>
								<dt id="display_3" style="display:none;"><i class="star"></i>阀值（语音分钟）</dt>
								<dd><input type="text" id="limitValue" onblur="checkSign(this)" placeholder="设置您的阀值（短信条数）" name="limitValue" style="width:300px;" value="${(balance.limitValue)!}"/>
									<span id="limitValueTip"></tip>
								</dd>
							</dl>
							<dl>
								<dt><i class="star"></i>状态</dt>
								<dd>
									<span style="position: relative;bottom: -9px;">
									<input type="checkbox" id="status" onclick="checkStatus()" value="0"/>
									<span id="sign_error" class="error"></span>
									<span class="tips">状态说明：如果勾选则会推送报告记录否则不会推送记录</span>
									</span>
								</dd>
								<!--<dd><span class="tips">状态说明：如果勾选则会推送报告记录否则不会推送记录</span></dd>-->
								<div class="b">
									<span id="statusTip"></tip>
								</div>
							</dl>
						</div>
					</div>
				</div>
				<div class="edit_btn">
					<input type="button" value="立即提交" onclick="update(1);" class="bg-blue">
				</div>
			</form>
	</div>
	<!--右侧main-->
</div>
<script>
	$(function(){
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
		$.formValidator.initConfig({formID:"appForm"});
		
		$("#mobile").formValidator({
			onShow : "可更换其它号码",
			onFocus : "可更换其它号码",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入手机号码"
		}).regexValidator({
			regExp : "mobile",
			dataType : "enum",
			onError : "请输入有效手机号码"
		}).inputValidator({
			empty : {leftEmpty:false,rightEmpty:false,emptyError:"可更换其它号码"}
		});
		
		$("#limitValue").formValidator({
			onShow : "请输入阀值（短信条数）",
			onFocus : "请输入阀值（短信条数）",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入阀值（短信条数）"
		}).regexValidator({
			regExp : "num",
			dataType : "enum",
			onError : "阀值必须为数字"
		}).inputValidator({
			empty : {leftEmpty:false,rightEmpty:false,emptyError:"请输入阀值（短信条数）"}
		});
	});
	function checkStatus(){
		if ($.formValidator.pageIsValid('1')){
			var limitValue = $("#limitValue").val();
			if(limitValue==null || limitValue=="" || limitValue==undefined){
				Hs.alert("请输入阀值在进行选择状态！");
				return false;
			}else{
				if(limitValue<=0){
					$("#statusById").val("0");
					$("#status").attr("checked",false);
					Hs.alert("阀值必须大于0才可以开启状态！");
					return false;
				}
			}
			var isChecked = $("#status").is(':checked');
			if(isChecked){
				$("#statusById").val("1");
			}else{
				$("#statusById").val("0");
			}
		}
	}

	function update(){
		if ($.formValidator.pageIsValid('1')){
			var isChecked = $("#status").is(':checked');
			if(isChecked){
				if($("#limitValue").val()<=0){
					$("#statusById").val("0");
					$("#status").attr("checked",false);
					Hs.alert("阀值必须大于0才可以开启状态！");
					return false;
				}
			}
			$.ajax({
				type : "post",
				url : "${rc.contextPath}/settings/balance_remind/update",
				dataType:"json",
				data:$('#appForm').serialize(),
				beforeSend : function(){
				},
				success:function(data){
					if(data){
						Hs.alert("提交成功！",function() {
							location.href = "${rc.contextPath}/settings/balance_remind/index?type="+$("#typeById").val();
						});
						return;
					}else{
						Hs.alert("提交失败！", window.wxc.xcConfirm.typeEnum.error);
					}
				}
			});
		}
	}
	
	query('${(type)!}');
	function query(type){
		display(type);
		$.ajax({
			type : "post",
			url : "${rc.contextPath}/settings/balance_remind/find_list",
			dataType:"json",
			data:$('#appForm').serialize(),
			success:function(data){
				$("#typeById").val(type);
				if(data !=null){
					var flag =true;
					$(data).each(function(i, d){
						if(d.type==type){
							flag=false;
							typeVal(d.status,d.id,d.mobile,d.limitValue,type);
						  	return false;
						}
					});
					if(flag){
						typeVal("0","0","","0",type);
					}
				}else{
					typeVal("0","0","","0",type);
				}
			}
		});
	}
	
	function typeVal(status,id,mobile,limitValue,type){
		$(".msg_sort input").removeClass("current");
		if(type !=null){
			switch(type){
				case "1":
					$("#all").attr('class','current');
			  		break;
				case "2":
					$("#not").attr('class','current');
			  		break;
			  	case "3":
					$("#read").attr('class','current');
			  		break;	
			}
		}else{
			$("#all").attr('class','current');
		}
		if(status !=null && status=="1"){
			$("#status").attr("checked",true);
		}else{
			$("#status").attr("checked",false);
		}
		$("#statusById").val(status);
		$("#mobile").val(mobile);
		$("#limitValue").val(limitValue);
		$("#idBy").val(id);
	}
	
	function display(type){
		$("#display_1").hide();
		$("#display_2").hide();
		$("#display_3").hide();
		$("#display_"+type).show();
	}
	
</script>
</body>
</html>