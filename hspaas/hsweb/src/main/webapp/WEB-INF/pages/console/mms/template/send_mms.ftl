<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>发送模板彩信 - 彩信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="${rc.contextPath}/static/css/global.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${rc.contextPath}/static/css/form.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body>
<div class="layui-fluid" style="margin-top:5px;">
	<form class="layui-form" action="" id="form_data"  lay-filter="form-job-edit">
		<input id="id" name="id" type="hidden" value="${template.id!''}" />
		<input id="modelId" name="modelId" type="hidden" value="${template.modelId!''}" />
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
		<div class="layui-form-item layui-hide">
            <input type="button" lay-submit lay-filter="LAY-btn-submit" id="LAY-btn-submit" value="确认">
        </div>
	</form>
</div>

<script type="text/javascript" src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/layui.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/js/jquery-1.12.0.min.js"></script>

<script type="text/javascript">

	layui.config({
	    base: '${rc.contextPath}/static/plugins/layui2/lay/modules/'
	}).use(['form', 'element', 'upload'], function(){
		var $ = layui.$
                ,admin = layui.admin
                ,form = layui.form
                ,upload = layui.upload
                ,modal = layui.modal;
                
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
</script>
</body>
</html>