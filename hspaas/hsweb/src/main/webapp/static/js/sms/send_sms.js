
layui.use(['form', 'element', 'laydate', 'upload'], function(){
    var form = layui.form
        ,layer = layui.layer
        ,element = layui.element
        ,$ = layui.$
        ,upload = layui.upload
        ,laydate = layui.laydate;

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
            beforeSend : function() {
            	
            },
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
    
    var showMobileCount = function() {
    	var val = $("#mobile").val();
    	if($.trim(val) == "") {
    		$("#count").html("0");
    	} else {
    		$("#count").html(val.split(",").length);
    	}
    };
    
    $("#mobile").bind({"input propertychange" : function(){
    	showMobileCount();
	},"blur" : function(){
		showMobileCount();
	}});
    
    
    //执行TXT上传实例
	upload.render({
		elem : '#txt_file',
		url : server_domain + "/sms/send/read_file/txt",
		accept : 'file',
		exts: 'txt|csv',
		multiple : false,
		before: function(){
			layer.load(0);
	    },
		done : function(res) {
			$("#mobile").val(res.mobiles);
			showMobileCount();
			layer.closeAll('loading');
		},
		error : function() {
			// 请求异常回调
			layer.closeAll('loading');
		}
	});
    
    //执行TXT上传实例
	upload.render({
		elem : '#excel_file',
		url : server_domain + "/sms/send/read_file/excel",
		accept : 'file',
		exts: 'xls|xlsx',
		before: function(){
			layer.load(0);
	    },
		done : function(res) {
			$("#mobile").val(res.mobiles);
			showMobileCount();
			layer.closeAll('loading');
		},
		error : function() {
			layer.closeAll('loading');
		}
	});
    
});