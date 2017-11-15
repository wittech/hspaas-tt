<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心 控制台 -选择支付方式</title>
<#include "/common/common.ftl"/>
<#include "/common/loading.ftl"/>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<script type="text/javascript">
$(function(){
	Pagination.go();
	$("#search").click(function(){
		Pagination.go();
	});
	loadMenu('li_finance','financial','recharge');
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
					<li><a href="">财务管理</a></li>
					<li class="active"><a href="javascript:void(0)">在线充值</a></li>
				</ul>
			</div>
            <div class="main_tab_tit">
				<ul>
				  <li><a href="${rc.contextPath}/account/consumption">充值账单</a></li>
				  <li><a href="${rc.contextPath}/account/recharge">在线充值</a></li>
				  <li class="active"><a href="${rc.contextPath}/product/index">产品报价</a></li>
				</ul>
			</div>
            
            <div class="recharge_step">
              <ul>
                <li class="current"><i class="num1">&nbsp;</i>选择套餐包</li>
                <li class="current"><i class="num2">&nbsp;</i>选择支付方式</li>
                <li><i class="num3">&nbsp;</i>进行付款</li>
                <li><i class="num4">&nbsp;</i>充值完成</li>
              </ul>
            </div>

			<div class="recharge_form">
              <form action="" method="post" id="order_form">
                <dl>
                  <dt>套餐名称：</dt>
                  <dd>
                     ${(combo.name)!}
                     <input type="hidden" name="comboId" value="${(combo.id)!}" />
                     <input type="hidden" name="tradeType" value="${(tradeTypeValue)!}"  />
                  </dd>
                </dl>
                <dl>
                  <dt>套餐价格：</dt>
                  <dd>￥${(combo.sellMoney)!}元 </dd>
                </dl>
                
                <dl>
                  <dt>套餐描述：</dt>
                  <dd>
                     ${(combo.description)!}
                  </dd>
                </dl>
                
                <dl>
                  <dt>支付方式：</dt>
                  <input type="hidden" id="pay_type" name="payType" value="3" />
                  <dd class="type">
                  <a href="javascript:void(0);" class="active" name="pay_types" val="3">
                  <img src="${rc.contextPath}/assets/images/alipay.png"><em>&nbsp;</em>
                  </a>
                  &nbsp;
                  <a href="javascript:void(0);" name="pay_types" val="2">
                  <img src="${rc.contextPath}/assets/images/company.png">
                  </a>
                  </dd>
                  <dd class="btn"><input type="button" value="充值" id="btn_pay"></dd>
                </dl>
                
                <dl>
                  <dt></dt>
                  <dd>
                      <span>
                      <input type="hidden" name="invoice" />
                       <input type="hidden" name="invoiceFlag" id="invoiceFlagById" value="0"/>
                      <label><input type="checkbox" id="invoice_flag">是否需要开具发票</label>
                      </span>
                  </dd>
                </dl>
                <dl style="display:none" class="fap_box">
                  <dt></dt>
                  <dd>
                  	<span><label><a href="javascript:addressAlert();" style="color:red;">我的地址薄</a></span>
                  	<input id="address_book_id" type="hidden" name="address_book_id"/>
                  </dd>
                  <dd>
                  	  发票抬头：<input type="text" name="invoiceTitle" id="company" maxlength="100" class="fap"/>
                  	  <span id="companyTip"></span>
                  </dd>
                  <dd>
                  	　收件人：<input type="text" id="name" name="name" maxlength="64" class="fap" style="width:120px"/>
                  	 <span id="nameTip"></span>
                  </dd>
                  <dd>
                  	  联系方式：<input type="text" id="mobile" name="mobile" maxlength="100" class="fap" style="width:120px"/>
                  	 <span id="mobileTip"></span>
                  </dd>
                  <dd>
                  	 收件地址：<input type="text" id="address" name="address" maxlength="200" class="fap" style="width:400px">
                  	<span id="addressTip"></span>
                  </dd>
                </dl>
              </form>
            </div>
		</div>
	<!--右侧main-->
</div>
<div id="alipay_content"></div>
<script type="text/javascript">

$(function(){

	 $("#invoice_flag").click(function(){
		  if($(this).prop("checked")){
		  	$("#invoiceFlagById").val(1);
			$(".fap_box").slideDown();
		  }else{
		  	$("#invoiceFlagById").val(0);
			$(".fap_box").hide();
		  }
	 });
	 
	 $("a[name='pay_types']").click(function(){
	 	var val = $(this).attr("val");
	 	$("#pay_type").val(val);
	 	
	 	$("a[name='pay_types']").each(function(i,d){
	 		if($(this).attr("val") == val)  {
	 			if($(this).attr("class") == "active"){
	 				return false;
	 			}else {
	 				$(this).addClass("active");
	 				$(this).append("<em>&nbsp;</em>")
	 			}
	 		} else {
	 			$(this).removeClass("active");
	 		}
	 	});
	 	
	 });
	 
	 $("#btn_pay").click(function(){
	 	if(validate()){
		 	$.ajax({
				url : "${rc.contextPath}/order/build",
				data : $("#order_form").serialize(),
				datatype : 'json',
				type : 'POST',
				beforeSend : function() {
				},
				success : function(data) {
					if(data == null) {
						Hs.alert("出错了！");
						return;
					}
					$('#alipay_content').html(data);
				},
				error : function() {
					Hs.alert("服务器数据交互失败！");
				},
				contentType : "application/x-www-form-urlencoded; charset=UTF-8"
			});
	 	 }
	 });
});

 function validate(){
 	  errTip();
	  if($("#invoice_flag").prop("checked")){
			if($("#company").val()==null || $("#company").val()==undefined || $("#company").val()==""){
				$('#companyTip').html("<span class='onError'>请输入发票抬头！</span>");
				$('#companyTip').show();
				return false;
			}
			if($("#name").val()==null || $("#name").val()==undefined || $("#name").val()==""){
				$('#nameTip').html("<span class='onError'>请输入收件人！</span>");
				$('#nameTip').show();
				return false;
			}
			if($("#mobile").val()==null || $("#mobile").val()==undefined || $("#mobile").val()==""){
				$('#mobileTip').html("<span class='onError'>请输入联系方式！</span>");
				$('#mobileTip').show();
				return false;
			}
			if($("#mobile").val()==null || $("#mobile").val()==undefined || $("#mobile").val()==""){
				$('#mobileTip').html("<span class='onError'>请输入联系方式！</span>");
				$('#mobileTip').show();
				return false;
			}
			if(!isMobile($("#mobile").val())){
				$('#mobileTip').html("<span class='onError'>请输入正确的联系方式！</span>");
				$('#mobileTip').show();
				return false;
			}
			if($("#address").val()==null || $("#address").val()==undefined || $("#address").val()==""){
				$('#addressTip').html("<span class='onError'>请输入收件地址！</span>");
				$('#addressTip').show();
				return false;
			}
			return true;
	    }
		return true;
	};
	
	function errTip(){
		 $('#companyTip').hide();
	 	 $('#nameTip').hide();
	 	 $('#mobileTip').hide();
	 	 $('#addressTip').hide();
	}
	
	function isMobile(m){
		return /^(13[0-9]|15[012356789]|17[03678]|18[0-9]|14[57])[0-9]{8}$/.test(m);
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
</script>
</body>
</html>