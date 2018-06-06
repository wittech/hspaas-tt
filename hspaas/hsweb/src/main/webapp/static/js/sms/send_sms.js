
layui.use(['form', 'element', 'laydate', 'upload'], function(){
    var form = layui.form
        ,layer = layui.layer
        ,element = layui.element
        ,$ = layui.$
        ,upload = layui.upload
        ,laydate = layui.laydate;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);

    //自定义验证规则
    form.verify({
    	multiMobiles: function(value){
        	var mobiles = $.trim(value);
			if(mobiles==null || mobiles==undefined || mobiles==""){
				return "请输入手机号码";
			}
			
			var regex = /^(13[0-9]|15[012356789]|166|17[05678]|18[0-9]|14[579]|19[89])[0-9]{8}$/;
			
			var ms = mobiles.split(",");
			for(var i=0; i < ms.length; i++){
				if(!regex.test(ms[i]))
					return "手机号码: [" + ms[i] +"] 格式不正确";
			}
        }
    });

    //监听提交
    form.on('submit(sendSms)', function(data) {
        $.ajax({
            url: server_domain + "/sms/send/submit",
            data: data.field,
            type: "POST",
            success: function (result) {
            	if(result.code == "0") {
            		layer.msg('发送成功');
            	} else {
            		layer.msg('发送失败:[' + result.msg + "]");
            	}
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
        return false;
    });
});