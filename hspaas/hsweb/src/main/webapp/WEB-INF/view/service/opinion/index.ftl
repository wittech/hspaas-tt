<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-客服系统-意见反馈</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_service','service','service_opinion');
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
				<li><a href="javascript:void(0)">客服管理</a></li>
				<li class="active">
					<a href="${rc.contextPath}/service/opinion/index">意见反馈</a>
				</li>
			</ul>
		</div>
		<form id="myForm" method="post">
		<div class="edit_box edit_basic">
				<h1>意见反馈</h1>
				<div class="edit_ctn">
					<div class="edit_field">
                        <dl>
                            <dt><i class="star"></i>反馈标题</dt>
                            <dd><input type="text" id="title" maxlength="30" name="title" style="width:218px;"/>
                                <span id="titleTip" class="error"></span>
                            </dd>
                        </dl>
                    </div>
                    <div class="edit_field">
						<dl>
							<dt><i class="star"></i>反馈类型</dt>
							<dd class="select_type">
                                <select id="type" class="select" name="type">
                                	<#if feeBackTypes??>
                                		<option value="">请选择类型</option>
				                        <#list feeBackTypes as p>
				                        	<option value="${p.value}">${p.title}</option>
				                        </#list>
			                        </#if>
                                </select>
                                <span id="typeTip" class="error"></span>
							</dd>						
						</dl>
					</div>
                    <div class="edit_field">
                    <dl>
                        <dt><i class="star"></i>反馈内容</dt>
                        <dd><textarea id="white" name="content"></textarea>
                        	<span id="contentTip" class="error"></span>
                        </dd>
                        <dd><span class="tips">请认真填写意见建议的内容，以至帮助我们共同提升服务质量。</span></dd>
                        <dd><span id="wl_error" class="error" style="display:none"></span></dd>
                    </dl>
                   </div>
				</div>
		</div>
		</form>
		<div class="edit_btn">
			<input type="submit" value="提交" onclick="preservation(1);" class="bg-blue">
		</div>
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
				url : "${rc.contextPath}/service/opinion/preservation",
				dataType:"json",
				data:$('#myForm').serialize(),
				type : "post",
				beforeSend : function(){
				},
				success:function(data){
					if(data){
						Hs.alert("提交成功！"); 
					}else{
						Hs.alert("提交失败！");
					}
				}
			});
		}});
		
		$("#title").formValidator({
			onShow : "请输入标题",
			onFocus : "请输入标题",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入标题"
		});
		
		$("#type").formValidator({
			onShow : "请选择类型",
			onFocus : "请选择类型",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请选择类型"
		});
		
		$("#white").formValidator({
			onShow : "请输入内容",
			onFocus : "请输入内容",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入内容"
		});
		
	});

	//保存
	function preservation(){
		$.formValidator.pageIsValid('1');
	}
	
	function isIP(ip){   
	    var re =  /^(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])\.(\d{1,2}|1\d\d|2[0-4]\d|25[0-5])$/   
	    return re.test(ip);
	}  
</script>
</body>
</html>