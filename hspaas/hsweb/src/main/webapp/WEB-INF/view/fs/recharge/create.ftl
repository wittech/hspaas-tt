<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-流量管理-流量充值</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script src="${rc.contextPath}/assets/js/fs/recharge/recharge.js" type="text/javascript"></script>
<script type="text/javascript">
$(function(){
	loadMenu('li_liuliang','flow','single_recharge');
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
		<!--右侧main-->
		<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="javascript:void(0)">流量管理</a></li>
                    <li class="active"><a href="${rc.contextPath}/fs/recharge/create">流量充值</a></li>
				</ul>
			</div>
            
            <div class="main_tab_tit">
				<ul>
					<li class="active" onclick="Recharge.back_jump('${rc.contextPath}/fs/recharge/create');">流量充值</li>
					<li onclick="Recharge.back_jump('${rc.contextPath}/fs/recharge/batch_recharge');">批量充值</li>
                    <li onclick="Recharge.back_jump('${rc.contextPath}/fs/recharge/inteface_recharge');">接口充值</li>
				</ul>
			</div>
			<!--说明state_box eof-->
			<form id="myForm" method="post">
				<input type="hidden" id="p_id"/>
				<input type="hidden" id="p_name"/>
				<input type="hidden" id="p_spend"/>
				<!--添加模板-->
					<div class="edit_box edit_basic">
						<h1>单号充流量</h1>
						<div class="edit_ctn">
	                    	<div class="edit_field">
								<dl>
									<dt><i class="star"></i>手机号码</dt>
									<dd><input type="text" id="mobile" name="mobile" size="34" maxlength="11" data-validate="required:请输入手机号码,regexp#^(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$:请输入11位有效手机号码">
										<span id="local" class="tag bg-yellow" style="display: none;background-color: #f60;font-size: 75%;padding: 0.1em 0.5em 0.2em;color: #fff;"></span>
										<span id="mobileTip"></span>
									</dd>
								</dl>
							</div>
	                        <div class="edit_field">
								<dl>
									<dt><i class="star"></i>充值流量</dt>
									<dd class="select_type" id="product_content">
	                                   &nbsp;
									</dd>						
								</dl>
							</div>
							<div class="edit_field">
								<dl>
									<dt>套餐原价</dt>
									<dd><input type="text" id="official_price" maxlength="8" size="34" value="" disabled style="color:red">
										<span id="officialPriceTip"></span>
									</dd>
								</dl>
							</div>
	                        <div class="edit_field">
								<dl>
									<dt>套餐折扣价</dt>
									<dd><input type="text" id="out_price" maxlength="8" size="34" value="" disabled style="color:red">
										<span id="outPriceTip"></span>
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
						</div>
					</div>
					<div class="edit_btn">
						<input type="button" value="立即充值" class="confirm_btn bg-blue" onclick="rechage();">
						<input type="button" value="取消" class="cancel_btn" onclick="history.go(-1)">
					</div>
			</form>
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
			url : "/fs/recharge/product",
			dataType:"json",
			data:$('#myForm').serialize(),
			type : "post",
			beforeSend : function(){
				_this=$('.confirm_btn');
			     if(_this.hasClass('disabled')){
			        return false;
			     }
			     _this.text('充值中···').addClass('disabled').css('background','#c1c1c1');
			},
			success:function(data){
				$('.confirm_btn').removeClass('disabled').text('立即充值').css('background','#7fb7ef');
				if(data!=null){
	    			var resultCode=data.resultCode;
	    			if(resultCode=="10000"){
	    				Hs.alert("充值成功！");
	    			}else{
	    				Hs.alert("充值失败！");
	    			}
	    		}else{
	    			Hs.alert("系统异常！请稍后重试！");
				}
			}
		});
	}});
	
	$("#mobile").formValidator({
		onShow : "请输入手机号码",
		onFocus : "请输入手机号码",
		onCorrect : "",
	}).regexValidator({
		regExp : "mobile",
		dataType : "enum",
		onError : "请输入正确的手机号码"
    });
});

$(function() {
	$("#mobile").change(function () {
	   var mobile = $.trim($(this).val());
	   if (!Recharge.checkNumber(mobile)) {
			$('#mobileTip').html("<span class='onError'>请输入正确的手机号码！</span>");
			$('#mobileTip').show();
			return false;
	   }
	   $.ajax({
			url : "/fs/recharge/product",
			data : {
				mobile : mobile,
			},
			dataType:"json",
			type : "post",
			success : function(data){
				if(data == null) {
					Hs.alert("操作异常！请稍后重试！");
					return;
				}
				$("#local").html(data.result_operator).show();
				var product="";
				$(data.result_list).each(function(i, d){
						product += "<label class='button";
						if(i==0){
							product += " active'";
						}
						product += "'><input name='product' onclick='Recharge.getDiscountPrice("+i+","+mobile+","+d.parValue+");' class='product_radio' type='radio' parValue="+d+" discount="+data.outPriceOff;
						if(i==0){
							product += " checked='checked'";
						}
						product += ">"+d.parValue+"M</label>";
						if(i ==0){
							Recharge.getDiscountPrice(i,mobile,d.parValue);
						}
				});
				$("#product_content").html(product);
			}
		});
    });
});

function rechage(){
	$.formValidator.pageIsValid('1');
}
</script>
</html>