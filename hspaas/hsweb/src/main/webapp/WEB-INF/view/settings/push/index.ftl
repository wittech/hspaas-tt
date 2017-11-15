<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-系统设置-推送设置</title>
<#include "/common/common.ftl"/>
<link href="${rc.contextPath}/assets/css/news.css" rel="stylesheet" type="text/css" />
<link href="${rc.contextPath}/assets/css/push.css" rel="stylesheet" type="text/css" />
<#include "/common/dialog.ftl"/>
<#include "/common/validator.ftl"/>
<script type="text/javascript">
$(function(){
	loadMenu('li_settings','settings','settings_push');
});
</script>
</head>
<body>
<#include "/common/navigation.ftl"/>
<div class="content">
	<#include "/common/sidebar.ftl"/>   

	<div class="main">
		<div class="msg_sort">
				<input type="button" value="短信" onclick="query('1','all')" id="all" class="current">
				<input type="button" value="语音" onclick="query('4','not')" id="not">
				<input type="button" value="流量" onclick="query('3','read')" id="read">
		</div>
	<!--短信-->
	<div id="sms_id" style="display:none;" class="display_1">
		<div class="app-framework-cpt">
			    <!-- 短信状态-->
			    <div class="box-header" style="background-color: transparent;">
				    <h4 class="box-title push-title">短信状态报告推送：</h4>
				    <div class="switch-locator" style="left: 150px;">
				        <div class="switch-cpt checked" id="switch_cpt_1">
				    		<span class="switch-inner" data_type="1" data-disabled-cw="关闭" data-enabled-cw="开启"></span>
				    	</div>
				    </div>
				</div>
    	</div>
		<div class="box-body" style="padding: 10px 20px 0px;">
		<form id="appForm1" class="form-cpt fade-transition">
			<input type="hidden" name="type" value="1" id="typeById1">
			<input type="hidden" name="id" value="0" id="idBy1">
			<input type="hidden" name="status" id="statusById1" value="0">
			<div id="down_id">
				    <div class="inline-unit-cpt" >
					    <h5 class="unit-label"> </h5>
					    <div class="unit-content" style="padding-left: 10px;">
					        <div class="radio-cpt checked inb-tac ty-n1" id="radio-cpt_1" data_type="1">
							    <i class="sign-out">
							        <i class="sign-in"></i>
							    </i>
						                    不设置固定推送地址。在每个发送请求内传callback_url，
						    	<input type="radio" id="noStatusById" name="status" value="2" checked/>
							</div>
				            <span class="unit-content-field mid inb-tac" >
				                <a class="cl-bl" href="/api2.0/sms.html#c5" target="_blank" >详见API文档</a>
				            </span>
				    	</div>
					</div>
				    <div class="inline-unit-cpt" >
					    <h5 class="unit-label"> </h5>
					    <div class="unit-content" style="padding-left: 10px;">
					        <div class="radio-cpt inb-tac ty-n1" id="radio-cpt_2">
							    <i class="sign-out">
							        <i class="sign-in"></i>
							    </i>
						                    固定推送地址。无需每次发送请求都传callback_url
				    			<input type="radio" id="yesStatusById" name="status" value="1"/>
							</div>
				            <span class="unit-content-field mid inb-tac placeholder" >-</span>
				    	</div>
					</div>
				    <div class="inline-unit-cpt" >
					    <h5 class="unit-label"> </h5>
					    <div class="unit-content" style="padding-left: 25px;">
					        <div class="validator-cpt" >
							    <div class="input-cpt-wrapper"   style="width: 420px; display: inline-block;">
							    	<input class="text-input-cpt disabled" _v-c5dc28a0 id="text-input-cpt_1" type="text"  placeholder="请输入接收状态报告地址" name="url"/>
								</div>
				                <span class="unit-content-field inb-tac placeholder" >-</span>
							</div>
				    	<div>
					</div>
				    <div class="inline-unit-cpt" >
					    <h5 class="unit-label"> </h5>
					    <div class="unit-content" style="padding-left: 10px;">
					        <div _v-07af3210=""  class="button button-middle  normal ">
				    			<a href="javascript:update('1','1');">保存</a>
							</div>
				    	</div>
					</div>
				    <div class="inline-unit-cpt" >
					    <h5 class="unit-label"> </h5>
					    <div class="unit-content" style="padding-left: 10px;">
					        <div class="validator-cpt" ></div>
				    	</div>
					</div>
			</div>
			</form>
	 	</div>
	 	</div>
	    <!-- 上行短信-->
	    <form id="appForm2" class="form-cpt fade-transition">
			<input type="hidden" name="type" value="1" id="typeById2">
			<input type="hidden" name="id" value="0" id="idBy2">
			<input type="hidden" name="status" id="statusById2" value="0">
	    <div class="replay-locator" >
	        <div class="inline-unit-cpt" >
			    <h5 class="unit-label">上行短信推送 :</h5>
			    <div class="unit-content" style="padding-left: 10px;">
			        <span class="unit-content-field inb-tac" >接收推送的地址</span>
			    </div>
			</div>
		    <div class="inline-unit-cpt" >
			    <h5 class="unit-label"> </h5>
			    <div class="unit-content" style="padding-left: 10px;">
			        <div class="validator-cpt" >
					    <div class="input-cpt-wrapper"   style="width: 420px; display: inline-block;">
					    	<input class="text-input-cpt" id="text-input-cpt_2" type="text" _v-c5dc28a0 placeholder="请输入上行短信状态报告地址" name="url"/>
						</div>
		                <span class="unit-content-field inb-tac placeholder" >-</span>
					</div>
		    	</div>
			</div>
	        <div class="inline-unit-cpt" >
			    <h5 class="unit-label"> </h5>
			    <div class="unit-content" style="padding-left: 10px;">
			        <div _v-07af3210=""  class="button button-middle  normal ">
			   			 <a href="javascript:theUpdate('1','2');">保存</a>
					</div>
	    		</div>
			</div>
	        <div class="inline-unit-cpt" >
			    <h5 class="unit-label"> </h5>
			    <div class="unit-content" style="padding-left: 10px;">
			        <div class="validator-cpt" >
					</div>
	    		</div>
			</div>
	    </div>
	    </form>
	</div>
	</div>
	<!-- 语音-->
	<form id="appForm4" class="form-cpt fade-transition">
		<input type="hidden" name="type" value="4" id="typeById4">
		<input type="hidden" name="id" value="0" id="idBy4">
		<input type="hidden" name="status" id="statusById4" value="0">
	<div id="voiv_id" style="display:none;" class="display_4">
	    <div class="box-header" style="background-color: transparent;">
		    <h4 class="box-title push-title">语音状态报告推送：</h4>
		    <div class="switch-locator" style="left: 150px;">
		        <div class="switch-cpt checked" id="switch_cpt_3">
		    		<span class="switch-inner" data_type="3" data-disabled-cw="关闭" data-enabled-cw="开启"></span>
				</div>
			</div>
		</div>
		<div class="box-body" style="padding: 10px 20px 0px;" id="voio_id_s_4">
			    <div class="inline-unit-cpt" >
			    	<h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 10px;">
				        <div class="radio-cpt checked inb-tac ty-n1" id="radio-cpt_4">
						    <i class="sign-out">
						        <i class="sign-in"></i>
						    </i>
	                                                            不设置固定推送地址。在每个发送请求内传callback_url，
	    					<input type="radio" name="status" value="2" id="noStatusById4" _v-c5dc28a0 checked/>
						</div>
		                <span class="unit-content-field mid inb-tac" >
		                    <a class="cl-bl" href="/api2.0/sms.html#c5" target="_blank" >详见API文档</a>
		                </span>
	    			</div>
				</div>
	            <div class="inline-unit-cpt" >
				    <h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 10px;">
				        <div class="radio-cpt inb-tac ty-n1" id="radio-cpt_44">
						    <i class="sign-out">
						        <i class="sign-in"></i>
						    </i>
	                    	固定推送地址。无需每次发送请求都传callback_url
	    					<input type="radio" name="status" value="1" id="yesStatusById4" _v-c5dc28a0/>
						</div>
	                	<span class="unit-content-field mid inb-tac placeholder" >-</span>
	    			</div>
				</div>
	            <div class="inline-unit-cpt" >
				    <h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 25px;">
				        <div class="validator-cpt" >
						    <div class="input-cpt-wrapper"   style="width: 420px; display: inline-block;">
						    	<input class="text-input-cpt" _v-c5dc28a0 type="text" id="text-input-cpt_4" placeholder="请输入接收状态报告地址" name="url">
							</div>
	                    	<span class="unit-content-field inb-tac placeholder" >-</span>
						</div>
	    			</div>
				</div>
	            <div class="inline-unit-cpt" >
				    <h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 10px;">
				        <div _v-07af3210=""  class="button button-middle  normal ">
	   						  <a href="javascript:update('4','4');">保存</a>
						</div>
	    			</div>
				</div>
	            <div class="inline-unit-cpt" >
				    <h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 10px;">
				        <div class="validator-cpt" >
						</div>
	    			</div>
				</div>
		</div>
	</div>
	</form>
	
	<form id="appForm3" class="form-cpt fade-transition">
		<input type="hidden" name="type" value="3" id="typeById3">
		<input type="hidden" name="id" value="0" id="idBy3">
		<input type="hidden" name="status" id="statusById3" value="0">
	<div id="flow_id" style="display:none;" class="display_3">
	    <!-- 流量-->
	    <div class="box-header" style="background-color: transparent;">
		    <h4 class="box-title push-title">流量状态报告推送：</h4>
		    <div class="switch-locator" style="left: 150px;">
		        <div class="switch-cpt checked" id="switch_cpt_2">
		    		<span class="switch-inner" data_type="2" data-disabled-cw="关闭" data-enabled-cw="开启"></span>
				</div>
	    	</div>
		</div>
		<div class="box-body" style="padding: 10px 20px 20px;" id="flow_id_s_3">
			    <div class="inline-unit-cpt" >
			    	<h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 10px;">
				        <div class="radio-cpt inb-tac ty-n1 checked" id="radio-cpt_3">
						    <i class="sign-out">
						        <i class="sign-in"></i>
						    </i>
		                                                   不设置固定推送地址。在每个发送请求内传callback_url，
		   					 <input type="radio"  name="status" id="noStatusById3" value="2" _v-c5dc28a0 checked/>
						</div>
		                <span class="unit-content-field mid inb-tac" >
		                    <a class="cl-bl" href="/api2.0/sms.html#c5" target="_blank" >详见API文档</a>
		                </span>
		    		</div>
				</div>
	            <div class="inline-unit-cpt" >
				    <h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 10px;">
				        <div class="radio-cpt inb-tac ty-n1" id="radio-cpt_33">
						    <i class="sign-out">
						        <i class="sign-in"></i>
						    </i>
	                    	固定推送地址。无需每次发送请求都传callback_url
	    					<input type="radio" name="status" id="yesStatusById3" value="1"/>
						</div>
	                	<span class="unit-content-field mid inb-tac placeholder" >-</span>
	    			</div>
				</div>
	            <div class="inline-unit-cpt" >
				    <h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 25px;">
				        <div class="validator-cpt" >
						    <div class="input-cpt-wrapper"   style="width: 420px; display: inline-block;">
						    	<input class="text-input-cpt disabled" type="text" _v-c5dc28a0 id="text-input-cpt_3" placeholder="请输入接收状态报告地址" name="url"/>
							</div>
	                    	<span class="unit-content-field inb-tac placeholder" >-</span>
						</div>
	    			</div>
				</div>
	            <div class="inline-unit-cpt" >
				    <h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 10px;">
				        <div _v-07af3210=""  class="button button-middle  normal ">
	    					<a href="javascript:update('3','3');">保存</a>
						</div>
	    			</div>
				</div>
	            <div class="inline-unit-cpt" >
				    <h5 class="unit-label"> </h5>
				    <div class="unit-content" style="padding-left: 10px;">
				        <div class="validator-cpt" >
						</div>
	    			</div>
				</div>
		</div>
	</div>
	</form>
    <#--
    下面是数据获取DOM 分割线
    <h3 class="section-title" >
        <b class="title-main inb-tac" >数据获取</b>
        <span class="title-content inb-tac" >开启数据获取后,云片会将您需要的数据储存72小时。您可以调用接口来主动获取数据 </span>
        <span class="title-button inb-tac ti-10" > <a href="https://www.yunpian.com/api2.0/sms.html#c4" target="_blank" >详见API文档</a></span>
    </h3>
    <div class="pull-switch-locator" >
        <div class="inline-unit-cpt" >
		    <h5 class="unit-label">短信状态报告 :</h5>
		    <div class="unit-content" style="padding-left: 10px;">
		        <div class="switch-locator" >
		                <div class="switch-cpt checked" >
		    				<span class="switch-inner" data-disabled-cw="关闭" data-enabled-cw="开启"></span>
						</div>
            	</div>
    		</div>
		</div>
		<div class="inline-unit-cpt" >
		    <h5 class="unit-label">上行短信报告 :</h5>
		    <div class="unit-content" style="padding-left: 10px;">
		        <div class="switch-locator" >
		            <div class="switch-cpt checked" >
		    			<span class="switch-inner" data-disabled-cw="关闭" data-enabled-cw="开启"></span>
					</div>
            	</div>
    		</div>
		</div>
		<div class="inline-unit-cpt" >
		    <h5 class="unit-label">语音状态报告 :</h5>
		    <div class="unit-content" style="padding-left: 10px;">
		        <div class="switch-locator" >
		                <div class="switch-cpt checked" >
		    				<span class="switch-inner" data-disabled-cw="关闭" data-enabled-cw="开启"></span>
						</div>
            	</div>
    		</div>
		</div>
		<div class="inline-unit-cpt" >
		    <h5 class="unit-label">流量状态报告 :</h5>
		    <div class="unit-content" style="padding-left: 10px;">
		        <div class="switch-locator" >
	                <div class="switch-cpt checked" >
	    				<span class="switch-inner" data-disabled-cw="关闭" data-enabled-cw="开启"></span>
					</div>
            	</div>
    		</div>
		</div>
    </div>
    -->
	<!--右侧main-->
</div>
<script>
	function checkStatus(type){
		var isChecked;
		if(type==1){
			isChecked = $("#status").is(':checked');
			if(isChecked){
				$("#statusById").val("1");
			}else{
				$("#statusById").val("0");
			}
		}else{
			isChecked = $("#status2").is(':checked');
			if(isChecked){
				$("#statusById2").val("1");
			}else{
				$("#statusById2").val("0");
			}
		}
		
	}
	
	
	
	function theUpdate(type,p){
		$("#typeById"+p).val(p);
		if(verification(p)){
			$.ajax({
				url : "${rc.contextPath}/settings/push/update",
				dataType:"json",
				data:$('#appForm'+p).serialize(),
				type : "post",
				beforeSend : function(){
				},
				success:function(data){
					if(data){
						Hs.alert("保存成功！");
						return;
					}
					Hs.alert("操作失败！");
				}
			});
		}
	}

	function update(type,p){
		$("#typeById"+p).val(p);
		if(verification(p)){
			$.ajax({
				url : "${rc.contextPath}/settings/push/update",
				dataType:"json",
				data:$('#appForm'+p).serialize(),
				type : "post",
				beforeSend : function(){
				},
				success:function(data){
					if(data){
						Hs.alert("保存成功！", function(){
							location.href = "${rc.contextPath}/settings/push/index?type="+type;
						});
						return;
					}
					Hs.alert("操作失败！");
				}
			});
		}
	}
	
		function updateStatus(type,status){
			$("#typeById"+type).val(type);
			$.ajax({
				url : "${rc.contextPath}/settings/push/update",
				dataType:"json",
				data:$('#appForm'+type).serialize(),
				type : "post",
				beforeSend : function(){
				},
				success:function(data){
					if(data){
					}
				}
			});
	}
	
	$(function(){  
		$('#switch_cpt_1').bind("click",function(){
	      	if($(this).hasClass('checked')) {
	      		$("#statusById1").val("0");
	      		updateStatus("1","1");
	      		$("#down_id").hide();
				$(this).removeClass("checked");
			}else{
				$("#statusById1").val("2");
				updateStatus("1","0");
				$("#down_id").show();
				$(this).attr('class','switch-cpt checked');
			}
	   });
	   
	   $('#switch_cpt_2').bind("click",function(){
	      	if($(this).hasClass('checked')) {
	      		$("#statusById3").val("0");
	      		updateStatus("3","1");
	      		$("#flow_id_s_3").hide();
				$(this).removeClass("checked");
			}else{
				$("#statusById3").val("2");
				updateStatus("3","0");
				$("#flow_id_s_3").show();
				$(this).attr('class','switch-cpt checked');
			}
	   });
	   
	   $('#switch_cpt_3').bind("click",function(){
	      	if($(this).hasClass('checked')) {
	      		$("#statusById4").val("0");
	      		updateStatus("4","1");
	      		$("#voio_id_s_4").hide();
				$(this).removeClass("checked");
			}else{
				$("#statusById4").val("2");
				updateStatus("4","0");
				$("#voio_id_s_4").show();
				$(this).attr('class','switch-cpt checked');
			}
	   });
	   
	   $('#radio-cpt_1').bind("click",function(){
	   		$("#noStatusById").attr("checked","checked");
			$("#yesStatusById").removeAttr("checked");
	   		$("#statusById1").val($("#noStatusById").val());
	      	if($(this).hasClass('checked')) {
	      		$("#radio-cpt_2").removeClass("checked");
	      		$(this).removeClass("checked");
				$("#text-input-cpt_1").removeClass("disabled");
				$('#text-input-cpt_1').attr("readonly","true");
			}else{
				$("#radio-cpt_2").removeClass("checked");
				$('#text-input-cpt_1').attr("readonly","true");
				$(this).attr('class','radio-cpt inb-tac ty-n1 checked');
				$("#text-input-cpt_1").attr('class','text-input-cpt disabled');
			}
	   });
	   
	    $('#radio-cpt_2').bind("click",function(){
	    	$("#yesStatusById").attr("checked","checked");
			$("#noStatusById").removeAttr("checked");
	   		$("#statusById1").val($("#yesStatusById").val());
	      	if($(this).hasClass('checked')) {
	      		$("#radio-cpt_1").removeClass("checked");
	      		$(this).removeClass("checked");
				$("#text-input-cpt_1").attr('class','text-input-cpt disabled');
				$('#text-input-cpt_1').removeAttr("readonly");
			}else{
				$("#radio-cpt_1").removeClass("checked");
				$(this).attr('class','radio-cpt inb-tac ty-n1 checked');
				$("#text-input-cpt_1").removeClass("disabled");
				$('#text-input-cpt_1').removeAttr("readonly");
			}
	   });
	   
	    $('#radio-cpt_3').bind("click",function(){
	    	var noStatus =  $("#noStatusById3").attr("checked");
	    	if(noStatus=="checked"){
	    		$("#statusById3").val("2");
	    		$('#text-input-cpt_3').attr("readonly","true");
	    		$("#text-input-cpt_3").attr('class','text-input-cpt disabled');
	    		$("#noStatusById3").attr("checked","checked");
	    		$("#yesStatusById3").removeAttr("checked");
	    	}else{
	    		$("#statusById3").val("2");
	    		$('#text-input-cpt_3').attr("readonly","true");
	    		$("#text-input-cpt_3").attr('class','text-input-cpt disabled');
	    		$("#noStatusById3").attr("checked","checked");
	    		$("#yesStatusById3").removeAttr("checked");
	    	}
	    	if($(this).hasClass('checked')) {
	      		$("#radio-cpt_33").removeClass("checked");
			}else{
				$("#radio-cpt_33").removeClass("checked");
				$(this).attr('class','radio-cpt inb-tac ty-n1 checked');
			}
	    	/**
	    	$("#yesStatusById3").attr("checked","checked");
			$("#noStatusById3").removeAttr("checked");
	   		$("#statusById3").val($("#noStatusById3").val());
	   		$("#text-input-cpt_3").attr('class','text-input-cpt disabled');
	      	if($(this).hasClass('checked')) {
	      		$("#radio-cpt_33").removeClass("checked");
			}else{
				$("#radio-cpt_33").removeClass("checked");
				$(this).attr('class','radio-cpt inb-tac ty-n1 checked');
			}
			*/
	   });
	   
	    $('#radio-cpt_33').bind("click",function(){
	    	var yesStatus =  $("#yesStatusById3").attr("checked");
	    	if(yesStatus=="checked"){
	    		$("#statusById3").val("1");
	    		$('#text-input-cpt_3').removeAttr("readonly");
	    		$("#noStatusById3").removeAttr("checked");
	    		$("#yesStatusById3").attr("checked","checked");
	    		$("#text-input-cpt_3").removeClass("disabled");
	    	}else{
	    		$("#statusById3").val("1");
	    		$('#text-input-cpt_3').removeAttr("readonly");
	    		$("#noStatusById3").removeAttr("checked");
	    		$("#yesStatusById3").attr("checked","checked");
	    		$("#text-input-cpt_3").removeClass("disabled");
	    	}
	    	if($(this).hasClass('checked')) {
	      		$("#radio-cpt_3").removeClass("checked");
			}else{
				$("#radio-cpt_3").removeClass("checked");
				$(this).attr('class','radio-cpt inb-tac ty-n1 checked');
			}
	   });
	   
	   $('#radio-cpt_4').bind("click",function(){
	   		var noStatus =  $("#noStatusById4").attr("checked");
	    	if(noStatus=="checked"){
	    		$("#statusById4").val("2");
	    		$('#text-input-cpt_4').attr("readonly","true");
	    		$("#text-input-cpt_4").attr('class','text-input-cpt disabled');
	    		$("#noStatusById4").attr("checked","checked");
	    		$("#yesStatusById4").removeAttr("checked");
	    	}else{
	    		$("#statusById4").val("2");
	    		$('#text-input-cpt_4').attr("readonly","true");
	    		$("#text-input-cpt_4").attr('class','text-input-cpt disabled');
	    		$("#noStatusById4").attr("checked","checked");
	    		$("#yesStatusById4").removeAttr("checked");
	    	}
	    	/**
	    	$("#yesStatusById4").attr("checked","checked");
			$("#noStatusById4").removeAttr("checked");
	   		$("#statusById4").val($("#noStatusById3").val());
	   		$("#text-input-cpt_4").attr('class','text-input-cpt disabled');
	   		*/
	      	if($(this).hasClass('checked')) {
	      		$("#radio-cpt_44").removeClass("checked");
			}else{
				$("#radio-cpt_44").removeClass("checked");
				$(this).attr('class','radio-cpt inb-tac ty-n1 checked');
			}
	   });
	   
	   $('#radio-cpt_44').bind("click",function(){
	   		var yesStatus =  $("#yesStatusById4").attr("checked");
	    	if(yesStatus=="checked"){
	    		$("#statusById4").val("1");
	    		$('#text-input-cpt_4').removeAttr("readonly");
	    		$("#noStatusById4").removeAttr("checked");
	    		$("#yesStatusById4").attr("checked","checked");
	    		$("#text-input-cpt_4").removeClass("disabled");
	    	}else{
	    		$("#statusById4").val("1");
	    		$('#text-input-cpt_4').removeAttr("readonly");
	    		$("#noStatusById4").removeAttr("checked");
	    		$("#yesStatusById4").attr("checked","checked");
	    		$("#text-input-cpt_4").removeClass("disabled");
	    	}
	    	/**
	    	$("#noStatusById4").attr("checked","checked");
			$("#yesStatusById4").removeAttr("checked");
	   		$("#statusById4").val($("#yesStatusById3").val());
	   		$("#text-input-cpt_4").removeClass("disabled");
	   		*/
	      	if($(this).hasClass('checked')) {
	      		$("#radio-cpt_4").removeClass("checked");
			}else{
				$("#radio-cpt_4").removeClass("checked");
				$(this).attr('class','radio-cpt inb-tac ty-n1 checked');
			}
	   });
	}); 
	
	function verification(type){
		/**
			if(type=="1"){
				if($("#text-input-cpt_1").hasClass('disabled')) {
	      			$("#text-input-cpt_1").val("");
				}
			}else{
				if($("#text-input-cpt_"+type).hasClass('disabled')) {
	      			$("#text-input-cpt_"+type).val("");
				}
			}
		*/
		var checkedStatusV="";
		if(type==1){
			checkedStatusV=$('#yesStatusById').attr("checked");
		}else{
			checkedStatusV=$('#yesStatusById'+type).attr("checked");
		}
		if(checkedStatusV !=null && checkedStatusV !="" && checkedStatusV !=undefined){
			if(checkedStatusV=="checked"){
				if(type!=2){
					if($("#text-input-cpt_"+type).val()==null || $("#text-input-cpt_"+type).val()=="" || $("#text-input-cpt_"+type).val()==undefined){
						Hs.alert("请输入固定推送地址！");
						return false;
					}
				}
			}
			if($("#text-input-cpt_"+type).val()!=null && $("#text-input-cpt_"+type).val()!="" && $("#text-input-cpt_"+type).val()!=undefined){
				 var reg=/^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
				 if(!reg.test($("#text-input-cpt_"+type).val())){
					Hs.alert("请输入正确上行短信状态地址！");
					return false;
				 }
				 $("#urlById").val($("#text-input-cpt_"+type).val());
			}
		}
		if(type==2){
			if($("#text-input-cpt_"+type).val()!=null && $("#text-input-cpt_"+type).val()!="" && $("#text-input-cpt_"+type).val()!=undefined){
				 var reg=/^([hH][tT]{2}[pP]:\/\/|[hH][tT]{2}[pP][sS]:\/\/)(([A-Za-z0-9-~]+)\.)+([A-Za-z0-9-~\/])+$/;
				 if(!reg.test($("#text-input-cpt_"+type).val())){
					Hs.alert("请输入正确上行短信状态地址！");
					return false;
				 }
				 $("#urlById").val($("#text-input-cpt_"+type).val());
				 $("#statusById2").val("1");
			}else{
				$("#statusById2").val("2");
			}
		}
		return true;
	}
	
	query('${(type)!}');
	function query(type){
		display(type);
		$.ajax({
			type : "post",
			url : "${rc.contextPath}/settings/push/find_list",
			dataType:"json",
			data:$('#appForm'+type).serialize(), 
			success:function(data){
				$("#typeById").val(type);
				if(data !=null){
					var flag =true;
					$(data).each(function(i, d){
						if(type=="1"){
							if(d.type==type){
								flag=false;
								typeVal(d.status,d.id,d.url,String(d.type));
							}else if(d.type==2){
								$("#statusById2").val(d.status);
								$("#idBy2").val(d.id);
								$("#text-input-cpt_2").val(d.url);
							}
						}else{
							if(d.type==type){
								flag=false;
								typeVal(d.status,d.id,d.url,String(d.type));
								return false;
							}
						}
					});
					if(flag){
						typeVal("0","0","",type);
					}
				}else{
					typeVal("0","0","",type);
				}
			}
		});
	}
	
	function typeVal(status,id,url,type){
		$(".msg_sort input").removeClass("current");
		if(type !=null){
			switch(type){
				case '1':
					if(status=="1" || status=="2"){
						$('#switch_cpt_1').attr('class','switch-cpt checked');
						$("#down_id").show();
					}else{
						$('#switch_cpt_1').removeClass("checked");
						$("#down_id").hide();
					}
					$("#sms_id").show();
					$("#voiv_id").hide();
					$("#flow_id").hide();
					$("#all").attr('class','current');
			  		break;
			  	case '2':
			  		if(status=="1" || status=="2"){
			  		$('#switch_cpt_1').removeClass("checked");
						$("#down_id").hide();
			  			$("#sms_id").show();
			  			//$('#switch_cpt_1').attr('class','switch-cpt checked');
						//$("#down_id").show();
					}else{
						$('#switch_cpt_1').removeClass("checked");
						$("#down_id").hide();
						$("#sms_id").show();
					}
					$("#all").attr('class','current');
			  		break;
			  	case '3':
			  		if(status=="1" || status=="2"){
			  			$('#switch_cpt_2').attr('class','switch-cpt checked');
						$("#flow_id_s_3").show();
					}else{
						$('#switch_cpt_2').removeClass("checked");
						$("#flow_id_s_3").hide();
					}
			  		$("#sms_id").hide();
					$("#voiv_id").hide();
					$("#flow_id").show();
					$("#read").attr('class','current');
			  		break;
			  	case '4':
			  		if(status=="1" || status=="2"){
			  			$('#switch_cpt_3').attr('class','switch-cpt checked');
						$("#voio_id_s_4").show();
					}else{
						$('#switch_cpt_3').removeClass("checked");
						$("#voio_id_s_4").hide();
					}
			  		$("#sms_id").hide();
			  		$("#voiv_id").show();
					$("#flow_id").hide();
					$("#not").attr('class','current');
			  		break;	
			}
		}else{
			$("#all").attr('class','current');
		}
		checkedDisplay(type,status);
		$("#statusById"+type).val(status);
		$("#text-input-cpt_"+type).val(url);
		$("#idBy"+type).val(id);
	}
	
	function display(type){
		$("#display_1").hide();
		$("#display_3").hide();
		$("#display_4").hide();
		if(type !=2){
			$("#display_"+type).show();
		}
	}
	
	function checkedDisplay(type,status){
		switch(type){
			case '1':
				if(status=="1"){
					$("#yesStatusById").attr("checked","checked");
					$("#noStatusById").removeAttr("checked");
			      	if($('#radio-cpt_2').hasClass('checked')) {
			      		$("#radio-cpt_1").removeClass("checked");
					}else{
						$("#radio-cpt_1").removeClass("checked");
						$('#radio-cpt_2').attr('class','radio-cpt inb-tac ty-n1 checked');
						$("#text-input-cpt_1").removeClass("disabled");
					}
				}else{
					$("#noStatusById").attr("checked","checked");
					$("#yesStatusById").removeAttr("checked");
			      	if($('#radio-cpt_1').hasClass('checked')) {
						$("#radio-cpt_2").removeClass("checked");
						$("#text-input-cpt_1").attr('class','text-input-cpt disabled');
					}else{
						$("#radio-cpt_2").removeClass("checked");
						$('#radio-cpt_1').attr('class','radio-cpt inb-tac ty-n1 checked');
						$("#text-input-cpt_1").attr('class','text-input-cpt disabled');
					}
				}
		  		break;
		  		return false;
			case '2':
		  	case '3':
		  		if(status=="1"){
					$("#yesStatusById3").attr("checked","checked");
					$("#noStatusById3").removeAttr("checked");
					$("#text-input-cpt_3").removeClass("disabled");
			      	if($('#radio-cpt_33').hasClass('checked')) {
			      		$("#radio-cpt_3").removeClass("checked");
					}else{
						$('#radio-cpt_33').attr('class','radio-cpt inb-tac ty-n1 checked');
						$("#radio-cpt_3").removeClass("checked");
					}
				}else{
					$("#noStatusById3").attr("checked","checked");
					$("#yesStatusById3").removeAttr("checked");
					$("#text-input-cpt_3").attr('class','text-input-cpt disabled');
			      	if($('#radio-cpt_3').hasClass('checked')) {
			      		$("#radio-cpt_33").removeClass("checked");
					}else{
						$('#radio-cpt_3').attr('class','radio-cpt inb-tac ty-n1 checked');
						$("#radio-cpt_33").removeClass("checked");
					}
				}
		  		break;
		  		return false;
		  	case '4':
		  		if(status=="1"){
					$("#yesStatusById4").attr("checked","checked");
					$("#noStatusById4").removeAttr("checked");
					$("#text-input-cpt_4").removeClass("disabled");
			      	if($('#radio-cpt_44').hasClass('checked')) {
			      		$("#radio-cpt_4").removeClass("checked");
					}else{
						$('#radio-cpt_44').attr('class','radio-cpt inb-tac ty-n1 checked');
						$("#radio-cpt_4").removeClass("checked");
					}
				}else{
					$("#noStatusById4").attr("checked","checked");
					$("#yesStatusById4").removeAttr("checked");
					$("#text-input-cpt_4").attr('class','text-input-cpt disabled');
			      	if($('#radio-cpt_4').hasClass('checked')) {
			      		$("#radio-cpt_44").removeClass("checked");
					}else{
						$('#radio-cpt_4').attr('class','radio-cpt inb-tac ty-n1 checked');
						$("#radio-cpt_44").removeClass("checked");
					}
				}
		  		break;
		  		return false;
		}
	}
</script>
</body>
</html>