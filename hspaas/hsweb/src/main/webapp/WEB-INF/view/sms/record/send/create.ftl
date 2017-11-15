<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-短信发送</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<#include "/common/upload.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_dx','dx_send','dx_create');
});
</script>
</head>
<body>
<!--header-->	  
<#include "/common/navigation.ftl"/>
<!--//header-->	
<div class="content">
	<!--左侧side-->
		<#include "/common/sidebar.ftl"/>	 
		<!--//左侧side-->
		<!--右侧main-->
		<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="javascript:void(0)">产品管理</a></li>
                    <li class="active"><a href="${rc.contextPath}/sms/record/send/create">短信发送</a></li>
				</ul>
			</div>
            
            <div class="main_tab_tit">
				<ul>
					<li><a href="${rc.contextPath}/sms/template/index">短信模板</a></li>
					<li class="active"><a href="${rc.contextPath}/sms/record/send/create">短信发送</a></li>
				</ul>
			</div>
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
			<form id="myForm" method="post">
			<!--添加模板-->
				<div class="edit_box edit_basic">
					<h1>短信发送</h1>
					<div class="edit_ctn">
                        <div class="edit_field">
							<dl>
								<dt><i class="star"></i>手机号码</dt>
								<dd class="textarea_type">
									<textarea id="mobile" name="mobile" placeholder="待发送短信手机号码，多个手机号码间以英文 , 号分隔开"></textarea>
									<span id="mobileTip"></span>
								</dd>
							</dl>
						</div>
						<div class="edit_field">
							<dl>
								<dd>
									<span id="fileQueue"></span>
									<input type="file" id="uploadify"/>
									<input type="hidden" id="uploadFilePath" name="image" />
									<span id="flagPath_mess" style="width:440px;"></span>
									<span id="msg_image" class="error_msg"></span>
	    							<div class="item_box" id="img_country" style="text-align:center;">
                                </dd>
							</dl>
						</div>
                        <div class="edit_field">
							<dl>
								<dt><i class="star"></i>短信内容</dt>
								<dd class="textarea_type">
									<textarea id="context" name="content" placeholder="待发送短信内容，自动匹配模板"></textarea>
									<span id="contextTip"></span>
								</dd>
							</dl>
						</div>
					</div>
				</div>
				<!--添加模板 -->
				</form>
				<div class="edit_btn">
					<input type="button" value="发送" onclick="send();" class="send bg-blue">
                    &nbsp;或&nbsp;
					<input type="button" value="重置" id="reset" class=" bg-green">
					&nbsp;或&nbsp;
					<input type="button" value="过滤无效手机号码" id="filter_error" class=" bg-blue">
					<br/><br/>
					<span id="msg_tip" style="color:coral;"></span>
				</div>
		</div>
		<!--右侧main-->
	</div>
</body>
<script>
	$(function () {
		$(document).ajaxStart(function(){$('body').showLoading();}).ajaxComplete(function(){
			$('body').hideLoading();
		});
		$.formValidator.initConfig({formID:"myForm", submitOnce : true, onSuccess:function(){
				$.ajax({
					url : "/sms/record/send_sms",
					dataType:"json",
					data:$('#myForm').serialize(),
					type : "post",
					beforeSend : function(){
						_this=$('.send');
				        if(_this.hasClass('disabled')){
				            return false;
				        }
				        _this.text('发送中···').addClass('disabled').css('background','#c1c1c1');
					},
					success:function(data){
						$('.send').removeClass('disabled').text('发送').css('background','#319acc');
						if(data==null){
							$("#msg_tip").html("<span class=\"text-red\">返回数据异常!</span>");
							return;
						}else if(data.code=="0"){
							$("#errTip").html(data.msg);
						}else{
							clear();
							$("#msg_tip").html(data.msg);
						}
					},
					error : function(XMLHttpRequest, textStatus, errorThrown) {
						$('.send').removeClass('disabled').text('发送').css('background','#319acc');
						Hs.alert("数据执行失败！");
					},
				});
		}});
			
		$("#mobile").formValidator({
			onShow : "请输入手机号码",
			onFocus : "请输入手机号码",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入手机号码"
		});
			 
		$("#context").formValidator({
			onShow : "请输入内容",
			onFocus : "请输入内容",
			onCorrect : "",
		}).inputValidator({
			min:1,
			onError:"请输入内容"
		});
		
	   setTimeout(function(){
		   $("#uploadify").uploadify({
			    	'method' : 'post',
			        'swf': '${rc.contextPath}/assets/js/upload/uploadify.swf',
			        'uploader': '${rc.contextPath}/upload/transport',
			        'buttonText': "请选择需要发送的手机号码文件",
			    	'fileObjName' : 'file',
			        'height': 28,
			        'width': 450,
			        'fileTypeDesc': '类型',
			        'fileTypeExts': "*.xls;*.xlsx;",
			        'auto': true,
			         'multi': true,
		    		'simUploadLimit' : 50,
			        'fileSizeLimit' : '50MB',
			        'onUploadSuccess' : function(file, data, response) {
			        	if(data!=null){
			        		data = eval('(' + data + ')');
				        	readFile(data.realPath);
			        	}else{
			        		Hs.alert("文件上传失败！请稍后重试！");
			       }
			     }
			 });
		}, 10);
   	});
   
    function readFile(name){
    	Hs.ajax({
			url : "/sms/record/read_file",
			data:{
				fileName : name
			},
    		success : function(data){
    			if(data !=null && data !="" && data !=undefined){
    				$("#mobile").html(data.mobleNumbers);
    			}else{
    				Hs.alert("文件上传失败！请稍后重试！");
    			}
    		}
    	});
   }
   
   function send(){
   		if(!validate()){
   			return;
   		}
   		$.formValidator.pageIsValid('1');
   }
   
   $("#filter_error").click(function(){
    	var mobiles = $.trim($("#mobile").val());
    	if(mobiles==null || mobiles==undefined || mobiles==""){
			$('#mobileTip').html("<span class='onError'>请输入手机号码！</span>");
			$('#mobileTip').show();
			return false;
		}
    	var mobileN = "";
    	var ms = mobiles.split(",");
    	for(var i=0; i < ms.length; i++){
    		if(isMobile(ms[i]) && mobileN.indexOf(ms[i]) == -1){
    			mobileN += ms[i] + ",";
    			continue;
    		}
    	}
    	mobileN = mobileN == "" ? "" : mobileN.substring(0, mobileN.length - 1);
    	$("#mobile").val(mobileN).focus();
    });
   
   $("#reset").click(function(){
   		clear();
   })
   
   function clear(){
   		$("#mobile").val("");
   		$("#context").val("");
   }
   
   function validate(){
		var mobiles = $.trim($("#mobile").val());
		if(mobiles==null || mobiles==undefined || mobiles==""){
			$('#mobileTip').html("<span class='onError'>请输入手机号码！</span>");
			$('#mobileTip').show();
			return false;
		}
		if (!checkMobile()) {
			$('#mobileTip').html("<span class='onError'>请输入正确的手机号码！</span>");
			$('#mobileTip').show();
			return false;
		}
		return true;
	};
	
	function isMobile(m){
		return /^(13[0-9]|15[012356789]|17[03678]|18[0-9]|14[57])[0-9]{8}$/.test(m);
	}
	
	function checkMobile() {
		var mobiles = $.trim($("#mobile").val());
		var ms = mobiles.split(",");
		for(var i=0; i < ms.length; i++){
			if(!isMobile(ms[i]))
				return false;
		}
		return true;
	};
</script>
</html>