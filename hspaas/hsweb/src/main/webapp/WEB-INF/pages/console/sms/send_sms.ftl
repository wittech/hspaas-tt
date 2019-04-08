<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>短信发送 - 短信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="${rc.contextPath}/static/css/global.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${rc.contextPath}/static/css/form.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body class="kit-theme">
<div class="layui-fluid">
  <div class="layui-card">
    <div class="layui-card-body" style="padding: 15px;">
      <form class="layui-form" action="" lay-filter="component-form-group">
      	<div class="layui-form-item layui-form-text">
          <label class="layui-form-label">手机号码<br/><span id="count" class="layui-badge">0</span>
          </label>
          <div class="layui-input-block">
          	<textarea id="mobile" name="mobile" placeholder="多个手机号码间以英文 ,号分隔开" lay-verify="multiMobiles" class="layui-textarea"></textarea>
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label"></label>
          <div class="layui-input-block">
            <button type="button" class="layui-btn" id="txt_file">
			    <i class="layui-icon">&#xe67c;</i>TXT文件
			</button>
			 <button type="button" class="layui-btn layui-btn-danger" id="excel_file">
			    <i class="layui-icon">&#xe67c;</i>EXCEL文件
			</button>
          </div>
        </div>
        <div class="layui-form-item layui-form-text">
          <label class="layui-form-label">短信内容</label>
          <div class="layui-input-block">
          	<textarea id="content" name="content" placeholder="请输入短信内容，建议发送已报备模板内容" lay-verify="required" class="layui-textarea"></textarea>
          </div>
        </div>
        <div class="layui-form-item">
          <div class="layui-inline">
            <label class="layui-form-label"></label>
            <div class="layui-input-inline">
              	短信内容已输入<span id="words" class="layui-badge">0</span>字
            </div>
          </div>
        </div>
        
        <div class="layui-form-item">
          <div class="layui-input-block">
            <div class="layui-footer">
              <button class="layui-btn" lay-submit="" lay-filter="sendSms">发送短信</button>
              <button id="resetForm" class="layui-btn layui-btn-primary">重置</button>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>
<script type="text/javascript" src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/layui.js"></script>
<script type="text/javascript">
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
    
    var l_index;

    //监听提交
    form.on('submit(sendSms)', function(data) {
        $.ajax({
            url: server_domain + "/sms/send/submit",
            data: data.field,
            beforeSend : function() {
            	l_index = layer.load(1);
            },
            type: "POST",
            async: false,
            success: function (result) {
            	layer.close(l_index);
            	if(result.success) {
            		layer.msg('发送成功');
            	} else {
            		layer.msg('发送失败:[' + result.msg + "]");
            	}
            },
            error: function (data) {
            	layer.close(l_index);
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
    
    var showWordsCount = function() {
    	var val = $("#content").val();
    	if($.trim(val) == "") {
    		$("#words").html("0");
    	} else {
    		$("#words").html(val.length);
    	}
    };
    
    $("#mobile").bind({"input propertychange" : function(){
    	showMobileCount();
	},"blur" : function(){
		showMobileCount();
	}});
    
    $("#content").bind({"input propertychange" : function(){
    	showWordsCount();
	},"blur" : function(){
		showWordsCount();
	}});
    
    $("#resetForm").bind({"click" : function(){
    	$("#mobile").val("");
    	$("#content").val("");
    	$("#count").html("0");
    	$("#words").html("0");
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

</script>
</body>

</html>