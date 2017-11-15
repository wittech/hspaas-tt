var Recharge = {};
/**
 * 根据号码套餐获取优惠价
 */
Recharge.getDiscountPrice = function(index,mobile,speed){
	if(index!=0){
 		$("input[name='product']").eq(0).removeAttr("checked");
 	}
	$.ajax({
		url : Hs.base + "/fs/recharge/getDiscountPrice",
		dataType:"json",
		data:{
			mobile:mobile,
			speed:speed
		},
		type : "post",
		success : function(data){
			if(data!=null){
				//折扣价
				$("#out_price").val(data.discountPrice+ "元");
				//套餐id
				$("#p_id").val(data.prodcutId);
				//官方价格
				$("#official_price").val(data.officialPrice+ "元");
				//套餐名称
				$("#p_name").val(data.productName);
				$("#p_spend").val(speed);
				Recharge.batchVal(data,speed);
			}else{
				alert("系统异常！请稍后重试！");
			}
		}
	});
}

/**
 * 手机号码验证
 */
Recharge.batchVal = function(data,speed){
	//折扣价
	$("#move_out_price").val(data.discountPrice+ "元");
	//套餐id
	$("#move_p_id").val(data.prodcutId);
	//官方价格
	$("#move_official_price").val(data.officialPrice+ "元");
	//套餐名称
	$("#move_p_name").val(data.productName);
	$("#move_p_spend").val(speed);
	
	
	//折扣价
	$("#telecom_out_price").val(data.discountPrice+ "元");
	//套餐id
	$("#telecom_p_id").val(data.prodcutId);
	//官方价格
	$("#telecom_official_price").val(data.officialPrice+ "元");
	//套餐名称
	$("#telecom_p_name").val(data.productName);
	$("#telecom_p_spend").val(speed);
	
	
	//折扣价
	$("#unicom_out_price").val(data.discountPrice+ "元");
	//套餐id
	$("#unicom_p_id").val(data.prodcutId);
	//官方价格
	$("#unicom_official_price").val(data.officialPrice+ "元");
	//套餐名称
	$("#unicom_p_name").val(data.productName);
	$("#unicom_p_spend").val(speed);
}

/**
 * 手机号码验证
 */
Recharge.checkNumber = function(mobile){
	  return /^(13[0-9]|15[012356789]|17[678]|18[0-9]|14[57])[0-9]{8}$/.test(mobile);
}

/**
 * type 1、流量充值2、批量充值 3、接口充值
 */
Recharge.back_jump = function (url){
	location.href = Hs.base + url;
}

