<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-流量管理-批量流量充值</title>
<#include "/common/common.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<#include "/common/upload.ftl"/>
<script src="${rc.contextPath}/assets/js/fs/recharge/recharge.js" type="text/javascript"></script>
<script type="text/javascript">
$(function(){
	loadMenu('li_liuliang','flow','bach_recharge');
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
					<li><a href="javascript:void(0)">流量管理</a></li>
                    <li class="active"><a href="${rc.contextPath}/fs/recharge/batch_recharge">批量充值</a></li>
				</ul>
			</div>
            <div class="main_tab_tit">
				<ul>
					<li onclick="Recharge.back_jump('${rc.contextPath}/fs/recharge/create');">流量充值</li>
					<li class="active" onclick="Recharge.back_jump('${rc.contextPath}/fs/recharge/batch_recharge');">批量充值</li>
                    <li onclick="Recharge.back_jump('${rc.contextPath}/fs/recharge/inteface_recharge');">接口充值</li>
				</ul>
			</div>
			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>1、选择excel文件批量导入手机</p>
				<p>2、导入后会自动过滤运营商栏目</p>
				<p>3、导入成功后自动显示号码</p>
			</div>
			<!--说明state_box eof-->
			<form id="appForm" name="appForm" method="post" class="">
			<input type="hidden" id="p_id"/>
			<input type="hidden" id="p_name"/>
			<input type="hidden" id="p_spend"/>
			
			<input type="hidden" id="telecom_p_id"/>
			<input type="hidden" id="telecom_p_name"/>
			<input type="hidden" id="telecom_p_spend"/>
			
			<input type="hidden" id="unicom_p_id"/>
			<input type="hidden" id="unicom_p_name"/>
			<input type="hidden" id="unicom_p_spend"/>
			<!--添加模板-->
				<div class="edit_box edit_basic">
					<h1>批量充流量</h1>
					<div class="edit_ctn">
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
						<div id="move_div" style="display:none;">
							<div class="edit_field">
								<dl>
									<dt><i class="star"></i>手机号码（移动）</dt>
									<dd class="textarea_type">
										<textarea id="move_content" placeholder="待充值手机号码列表数据"></textarea>
										<span id="moveContentTip" class="error"></span>
									</dd>
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt><i class="star"></i>充值流量</dt>
									<dd class="select_type" id="move_type">
	                                   &nbsp;
									</dd>						
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt>充值人数</dt>
									<dd><input type="text" id="move_count" maxlength="30" value="0" disabled style="color:red">
									</dd>
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt>套餐原价</dt>
									<dd><input type="text" id="move_official_price" maxlength="8" size="34" value="" disabled style="color:red">
										<span id="moveOfficialPricetIP"></span>
									</dd>
								</dl>
							</div>
	                        <div class="edit_field">
								<dl>
									<dt>套餐折扣价</dt>
									<dd><input type="text" id="move_out_price" maxlength="8" size="34" value="" disabled style="color:red">
										<span id="moveOutPriceTip"></span>
									</dd>
								</dl>
							</div>
	                        <div class="edit_field">
								<dl>
									<dt>当前余额</dt>
									<dd>
										<input type="text" id="move_balance" maxlength="30" size="34" 
										<#if userbalance??>
											<#list userbalance as b>
												<#if b.type=="2">
													value="${(b.balance)!}"
												</#if>
											</#list>
										</#if> 
										disabled style="color:red">元
										<span id="moveBalanceTip"></span>
									</dd>
								</dl>
							</div>
							<div class="edit_btn">
								<input type="button" value="立即充值" class="confirm_btn" onclick="bacthRecharge('1');"/>
								<input type="button" value="取消" class="cancel_btn" onclick="batchClear('1');"/>
							</div>
						</div><br/>
						<div id="telecom_div" style="display:none;">
							<hr style=" height:2px;border:none;border-top:2px dotted #185598;" />
							<div class="edit_field">
								<dl>
									<dt><i class="star"></i>手机号码（电信）</dt>
									<dd class="textarea_type">
										<textarea id="telecom_content" placeholder="待充值手机号码列表数据"></textarea>
										<span id="telecomContentTip"></span>
									</dd>
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt><i class="star"></i>充值流量</dt>
									<dd class="select_type" id="telecom_type">
	                                   &nbsp;
									</dd>						
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt>充值人数</dt>
									<dd><input type="text" id="telecom_count" maxlength="30" value="0" disabled style="color:red">
									</dd>
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt>套餐原价</dt>
									<dd><input type="text" id="telecom_official_price" maxlength="8" size="34" value="" disabled style="color:red">
										<span id="telecomOfficialPriceTip"></span>
									</dd>
								</dl>
							</div>
	                        <div class="edit_field">
								<dl>
									<dt>套餐折扣价</dt>
									<dd><input type="text" id="telecom_out_price" maxlength="8" size="34" value="" disabled style="color:red">
										<span id="telecomOutPriceTip"></span>
									</dd>
								</dl>
							</div>
	                        <div class="edit_field">
								<dl>
									<dt>当前余额</dt>
									<dd>
										<input type="text" id="balance" maxlength="30" size="34" 
										<#if userbalance??>
											<#list userbalance as b>
												<#if b.type=="2">
													value="${(b.balance)!}"
												</#if>
											</#list>
										</#if> 
										 disabled style="color:red">元
										<span id="telecomBalanceTip"></span>
									</dd>
								</dl>
							</div>
							<div class="edit_btn">
								<input type="button" value="立即充值" class="confirm_btn" onclick="bacthRecharge('2');"/>
								<input type="button" value="取消" class="cancel_btn" onclick="batchClear('2');"/>
							</div>
						</div><br/>
						<div id="unicom_div" style="display:none;">
							<hr style=" height:2px;border:none;border-top:2px dotted #185598;" />
							<div class="edit_field">
								<dl>
									<dt><i class="star"></i>手机号码（联通）</dt>
									<dd class="textarea_type">
										<textarea id="unicom_content" placeholder="待充值手机号码列表数据"></textarea>
										<span id="unicomContenttip"></span>
									</dd>
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt><i class="star"></i>充值流量</dt>
									<dd class="select_type" id="unicom_type">
	                                   &nbsp;
									</dd>						
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt>充值人数</dt>
									<dd><input type="text" id="unicom_count" maxlength="30" value="0" disabled style="color:red">
									</dd>
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt>套餐原价</dt>
									<dd><input type="text" id="unicom_official_price" maxlength="8" size="34" value="" disabled style="color:red">
										<span id="unicomOfficialPriceTip"></span>
									</dd>
								</dl>
							</div>
	                        <div class="edit_field">
								<dl>
									<dt>套餐折扣价</dt>
									<dd><input type="text" id="unicom_out_price" maxlength="8" size="34" value="" disabled style="color:red">
										<span id="unicomOutPriceTip"></span>
									</dd>
								</dl>
							</div>
	                        <div class="edit_field">
								<dl>
									<dt>当前余额</dt>
									<dd>
										<input type="text" id="balance" maxlength="30" size="34" 
										<#if userbalance??>
											<#list userbalance as b>
												<#if b.type=="2">
													value="${(b.balance)!}"
												</#if>
											</#list>
										</#if> 
										disabled style="color:red">元
										<span id="balanceTip"></span>
									</dd>
								</dl>
							</div>
							<div class="edit_btn">
								<input type="button" value="立即充值" class="confirm_btn" onclick="batchRecharge('3');"/>
								<input type="button" value="取消" class="cancel_btn" onclick="batchClear('3');"/>
							</div>
						</div><br/>
				</div>
				<!--添加模板-->
				</form>
		</div>
		<!--右侧main-->
	</div>
<script>
	$(function () {
   		setTimeout(function(){
	        $("#uploadify").uploadify({
		    	'method' : 'post',
		        'swf': '${rc.contextPath}/assets/js/upload/uploadify.swf',
		        'uploader': '${rc.contextPath}/upload/transport',
		        'buttonText': "请选择需要充值的手机号码文件",
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
   		 $.ajax({
    		url : "${rc.contextPath}/fs/recharge/get_product",
    		dataType:"json",
			data:{
				filename : name
			},
			type : "post",
    		success : function(data){
    			if(data.number_report.success){
    				if(data.number_report.cmSize!="0"){
    					var product="";
						$(data.mobile_list).each(function(i, d){
							var mobile = data.number_report.cmNumbers.split(",")[0];
								product += "<label class='button";
								if(i==0){
									product += " active'";
								}
								product += "'><input name='product1' onclick='Recharge.getDiscountPrice("+i+","+mobile+","+d.parValue+");' class='product_radio' type='radio' parValue="+d+" discount="+data.outPriceOff;
								if(i==0){
									product += " checked='checked'";
									Recharge.getDiscountPrice(i,mobile,d.parValue);
								}
								product += ">"+d.parValue+"M</label>";
						});
						$("#move_type").html(product);
						$("#move_content").html(data.number_report.cmNumbers);
						$("#move_count").val(data.number_report.cmSize);
						$("#move_div").show();
    				}
    				if(data.number_report.ctSize!="0"){
    					var telecom_product="";
    					$(data.telecom_list).each(function(i, d){
    						var mobile = data.number_report.ctNumbers.split(",")[0];
								telecom_product += "<label class='button";
								if(i==0){
									telecom_product += " active'";
								}
								telecom_product += "'><input name='product2' onclick='Recharge.getDiscountPrice("+i+","+mobile+","+d.parValue+");' class='product_radio' type='radio' parValue="+d+" discount="+data.outPriceOff;
								if(i==0){
									telecom_product += " checked='checked'";
									Recharge.getDiscountPrice(i,mobile,d.parValue);
								}
								telecom_product += ">"+d.parValue+"M</label>";
						});
						$("#telecom_type").html(telecom_product);
    					$("#telecom_content").html(data.number_report.ctNumbers);
    					$("#telecom_count").val(data.number_report.ctSize);
    					$("#telecom_div").show();
    				}
    				if(data.number_report.cuSize!="0"){
    					var unicom_product="";
    					$(data.unicom_list).each(function(i, d){
    							var mobile = data.number_report.cuNumbers.split(",")[0];
								unicom_product += "<label class='button";
								if(i==0){
									unicom_product += " active'";
								}
								unicom_product += "'><input name='product3' onclick='Recharge.getDiscountPrice("+i+","+mobile+","+d.parValue+");' class='product_radio' type='radio' parValue="+d+" discount="+data.outPriceOff;
								if(i==0){
									unicom_product += " checked='checked'";
									Recharge.getDiscountPrice(i,mobile,d.parValue);
								}
								unicom_product += ">"+d.parValue+"M</label>";
						});
						$("#unicom_type").html(unicom_product);
    					$("#unicom_content").html(data.number_report.cuNumbers);
    					$("#unicom_count").val(data.number_report.cuSize);
    					$("#unicom_div").show();
    				}
    			}else{
    				alert(data.msg);
    			}
    		}
    	});
   }
   
	function batchClear(type){
		if(type=="1"){
			$("#move_content").val("");
			$("#move_count").val(0);
		}else if(type=="2"){
			$("#telecom_content").val("");
			$("#telecom_count").val(0);
		}else{
			$("#unicom_content").val("");
			$("#unicom_count").val(0);
		}
	}
   
     function batchRecharge(type){
     	var productId="";
     	var productName="";
     	var spend="";
     	var mobile="";
     	if(type=="1"){
     		productId =$("#p_id").val();
			productName =$("#p_name").val();
			spend =$("#p_spend").val();
			mobile =$("#move_content").val();
     	}else if(type=="2"){
     		productId =$("#telecom_p_id").val();
			productName =$("#telecom_p_name").val();
			spend =$("#telecom_p_spend").val();
			mobile =$("#telecom_content").val();
     	}else if(type=="3"){
     		productId =$("#unicom_p_id").val();
			productName =$("#unicom_p_name").val();
			spend =$("#unicom_p_spend").val();
			mobile =$("#unicom_content").val();
     	}
    	$.ajax({
    		url : "${rc.contextPath}/fs/recharge/batch_submit",
    		dataType:"json",
			data:{
				productId : productId,
				productName : productName,
				spend : spend,
				mobile : mobile
			},
			type : "post",
    		success : function(data){
    			if(data!=null){
    				var resultCode=data.resultCode;
    				if(resultCode=="10000"){
    					Hs.alert(data.resultMsg);
    				}else{
    					Hs.alert(data.resultMsg);
    				}
    			}else{
    				Hs.alert("系统异常！请稍后重试！");
    			}
    		}
    	});
    }
   
</script>
</body>
</html>