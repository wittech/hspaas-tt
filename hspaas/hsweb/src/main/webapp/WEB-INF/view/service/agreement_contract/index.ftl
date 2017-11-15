<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-客服系统-协议合同</title>
<#include "/common/common.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_service','service','service_agreement_contract');
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
					<a href="${rc.contextPath}/service/agreement_contract/index">协议合同</a>
				</li>
			</ul>
		</div>
        <div class="main_tab_tit">
			<ul>
			  <li id="smsContractId" onclick="contractSwitch('1','${base}/model/sms-contract.xlsx');" class="active">短信协议合同</li>
              <li id="voiceContractId" onclick="contractSwitch('2','${base}/model/voice-contract.xlsx');" class="">语音协议合同</li>
              <li id="trafficContractId" onclick="contractSwitch('3','${base}/model/traffic-contract.xlsx');">流量协议合同</li>
			</ul>
		</div>
        <!--内容部分-->
		<!--基础配置-->
		<div class="edit_box edit_basic contract_content">
			<h1>短信协议合同</h1>
			<div class="edit_ctn">
           		<p>短信协议合同内容</p>
                <p><a href="javascript:download('${base}/model/sms-contract.xlsx')" class="green">短信协议合同下载</a></p>
			</div>
		</div>
		<div class="edit_box edit_basic">
			<div class="edit_ctn contract_img">
           		<p><img src="/assets/images/alipay.png" alt="华时融合平台"></p>
			</div>
		</div>
		<!--编辑提交按钮 bof-->
		<div class="edit_btn">
		</div>
		<!--编辑提交按钮 bof-->
    <!--//内容部分-->
	</div>
    <!--右侧main-->
</div>
<script>
	//切换
	function download(url){
		 window.open(url); 
	}
	//合同切换
	function contractSwitch(type,url){
		var message ="";
		var message_img="";
		if(type==1){//javascript:download('"+url+"')
			message+="<h1>短信协议合同</h1><div class='edit_ctn'><p>短信协议合同内容</p><p><a href=\"javascript:download('"+url+"')\" class='green'>短信协议合同下载</a></p></div>";
			$("#voiceContractId").removeClass("active");
			$("#trafficContractId").removeClass("active");
			$("#smsContractId").addClass("active");
			message_img+="<p><img src='/assets/images/alipay.png' alt='短信文档'></p>";
		}else if(type==2){
			message+="<h1>语音协议合同</h1><div class='edit_ctn'><p>语音协议合同内容</p>"
				+"<p><a href=\"javascript:download('"+url+"')\" class='green'>语音协议合同下载</a></p>"
				+"</div>";
			$("#smsContractId").removeClass("active");
			$("#trafficContractId").removeClass("active");
			$("#voiceContractId").addClass("active");
			message_img+="<p><img src='/assets/images/logo.png' alt='语音文档'></p>";
		}else if(type==3){
			message+="<h1>流量协议合同</h1><div class='edit_ctn'><p>流量协议合同内容</p>"
				+"<p><a href=\"javascript:download('"+url+"')\" class='green'>流量协议合同下载</a></p>"
				+"</div>";
			$("#smsContractId").removeClass("active");
			$("#voiceContractId").removeClass("active");
			$("#trafficContractId").addClass("active");
			message_img+="<p><img src='/assets/images/app_step_bg.png' alt='流量文档'></p>";
		}
		$(".contract_content").empty();
		$(".contract_content").append(message);
		$(".contract_img").empty();
		$(".contract_img").append(message_img);
	}
</script>
</body>
</html>