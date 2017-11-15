<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-短信模板匹配</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_dx','dx_send','dx_template');
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
                 <li class="active"><a href="javascript:void(0)">短信模板</a></li>
			</ul>
		</div>
        <#--include "/common/sms_navigation.ftl"/>-->
		<!--说明state_box bof-->
		<div class="state_box">
			<h1>说明</h1>
			<p>1、涉及房产、贷款、移民、成人用品、政治、色情、暴力、赌博以及其他违法信息不能发送。</p>
			<p>2、含有病毒、恶意代码、色情、反动等不良信息或有害信息</p>
			<p>3、冒充任何人或机构，或以虚伪不实的的方式谎称或使人误认为与任何人或任何机构有关。</p>
			<p>4、侵犯他人著作权或其他知识产权、或违反保密、雇佣或不披露协议披露他人商业秘密或保密信息。</p>
            <p>5、粗话、脏话等不文明的内容; 让短信接收者难以理解的内容。</p>
            <p>6、主题不明确的模板，如：您好#content#,亲爱的用户#content#</p>
            <p>7、营销、广告类的短信不能发送-这类短信为：通过一些方式（打折，促销等）吸引客户过来参与一些活动，或购买一些产品或服务。</p>
            <p>8、验证码类的短信模板，请务必在获取短信验证之前加 图片验证码。</p>
		</div>
		<!--说明state_box eof-->
		<form id="myForm">
		<!--添加模板-->
			<div class="edit_box edit_basic">
				<h1>添加模板</h1>
				<div class="edit_ctn">
					<input type="hidden" name="id" value="${(msmMessageTemokate.id)!}" />
                    <div class="edit_field">
						<dl>
							<dt><i class="star"></i>模板内容</dt>
							<dd class="textarea_type">
								<textarea disabled id="content">${(msmMessageTemokate.content)!}</textarea>
							</dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>模板测试</dt>
							<dd class="textarea_type">
								<textarea id="contentTest" name="content"></textarea>
								<span id="contentTestTip" class="error"></span>
							</dd>
							<dd><span class="tips">模板内容变量以两个半角的#号包围，如：#1234#</span></dd>
						</dl>
					</div>
					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>测试结果：</dt>
							<dd class="textarea_type">
								<span id="contentResult" style="margin: 12px;" class="error"></span>
							</dd>
						</dl>
					</div>
				</div>
			</div>
			<!--添加模板-->
		</form>
		<div class="edit_btn">
			<input type="submit" value="匹配" onclick="preservation();" class="confirm_btn">
		</div>
	</div>
	<!--右侧main-->
</div>
</body>
<script>
	$(function(){
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
		$.formValidator.initConfig({formID:"myForm", submitOnce : true, onSuccess:function(){
			$.ajax({
				url : "${rc.contextPath}/sms/template/match",
				dataType:"json",
				data:$('#myForm').serialize(),
				type : "post",
				beforeSend : function(){
					_this=$('.confirm_btn');
				    if(_this.hasClass('disabled')){
				        return false;
				    }
				    _this.text('匹配中···').addClass('disabled').css('background','#c1c1c1');
				},
				success:function(data){
					$('.confirm_btn').removeClass('disabled').text('匹配').css('background','#319acc');
					if(data){
						$("#contentResult").html("匹配成功");
					}else{
						$("#contentResult").html("匹配失败");
					}
				}
			});
		}});
		
		$("#contentTest").formValidator({
			onShow : "请输入测试模板内容",
			onFocus : "请输入测试模板内容",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入测试模板内容"
		});
	});

	//保存
	function preservation(){
		$.formValidator.pageIsValid('1');
	}
	
</script>
</html>