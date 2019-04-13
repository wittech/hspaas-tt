<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>发送彩信 - 彩信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="${rc.contextPath}/static/css/global.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${rc.contextPath}/static/css/form.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
    
    <style>
    	
		.layui-footer {
			background-color: #fff;
		    padding: 10px 0;
		}
		
		.layui-footer {
		    position: fixed;
		    bottom: 0;
		    height: 44px;
		    line-height: 44px;
		}
		
		.layui-textarea-con {
		    min-height: 150px;
		    height: auto;
		    resize: vertical;
    		width: 500px;
		}
		
		.img_pre {
			min-height: 150px;
		    resize: vertical;
    		height : 300px;
    		margin-top:5px;
		}
		
		.file {
		  opacity:0;
		  filter:alpha(opacity=0);
		  position:absolute;
		  left:0;
    	  top:0;
		}
    	
    </style>
</head>

<body>
<div class="layui-fluid">
  <div class="layui-card">
    <div class="layui-card-body" style="padding: 15px;">
      <form id="myform" class="layui-form" action="" lay-filter="component-form-group" enctype="multipart/form-data">
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
        <div class="layui-form-item">
          <label class="layui-form-label">彩信标题</label>
          <div class="layui-input-block">
            <input type="text" name="title" id="title" lay-verify="required" autocomplete="off" placeholder="请输入模板标题" class="layui-input">
          </div>
        </div>
        
        <div class="layui-form-item">
          <div class="layui-block">
            <label class="layui-form-label"></label>
            <div class="layui-input-block">
     			<input type="text" name="con" lay-verify="mediaContent" placeholder="请点击下方的操作按钮加入您的模板内容，彩信内容为可为多帧" class="layui-input" style="border:0px;" readonly="readonly">
            </div>
          </div>
        </div>
	  	<div id="mms_content"></div>
	  	
        <div class="layui-form-item">
          <div class="layui-input-block">
            <div class="layui-footer">
               <a href="javascript:addText();" class="layui-btn layui-btn-primary layui-btn-radius">+文本数据</a>
               <a href="javascript:addImage();" class="layui-btn layui-btn-radius">+图片数据</a>
               <a class="layui-btn layui-btn-warm layui-btn-radius" lay-submit="" lay-filter="sendMms"><i class="layui-icon layui-icon-release"></i>发送彩信</a>
            </div>
          </div>
        </div>
      </form>
    </div>
  </div>
</div>

<script type="text/javascript" src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/layui.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/js/jquery.form.js"></script>

<script type="text/javascript">

	layui.use(['form', 'element', 'upload'], function(){
	    var form = layui.form
	        ,layer = layui.layer
	        ,element = layui.element
	        ,upload = layui.upload
	        ,$ = layui.$;
	
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
	        },
	    	mediaContent: function(value){
	    		var size = $("#mms_content .layui-form-item").size();
	    		if(size == 0) {
	    			return "请点击按钮添加彩信内容";
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
	    
	
	    //监听提交
	    form.on('submit(sendMms)', function(data) {
	        $("#myform").ajaxSubmit({
	            url: server_domain + "/mms/send/submit",
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
	            },
	            contentType : "application/x-www-form-urlencoded; charset=utf-8"
	        });
	        return false;
	    });
	});
	
	function remove(id) {
		$("#frame_"+ id).remove();
	};
	
	function bindFile(btnId, fileId, containerId){
	     var inputJ = $("#"+fileId),
		 input  = inputJ[0],
		 con    = $("#"+containerId);
	
	 	 inputJ.change(function(e){
		 var file     = e.target.files[0],
			 thisType = file.type,
			 thisSize = file.size,
			 reader   = new FileReader();
			 
			 reader.readAsDataURL(file);
	
			 //文件加载成功以后，渲染到页面
			 reader.onload = function(e) {
			 	if(thisType.indexOf("image") != -1 ) {
			 	   con.html($("<img class='img_pre' src='"+e.target.result+"'>"));
			 	}
			 }
	 	});
	 	
	 	$("#"+fileId).css("width", $("#"+btnId).css("width")).css("height", $("#"+btnId).css("height"));
	
	};
	
	function getFileDes(type, id) {
		if(type == "text") {
			return "";
		}
	
		var title = "";
		var accept = "";
		if(type == "image") {
			title = "选择图片（支持jpg/gif/png/bmp）";
			accept = "image/*";
		} else {
			return "";
		}
		
		return "<a class='layui-btn layui-btn-primary layui-btn-sm' id='btn_"+id+"'>" +
				    	"<i class='layui-icon'>&#xe67c;</i>" + title +
				        "<input name='files' lay-verify='required' type='file' id='file_"+id+"' class='file' accept='"+accept+"'>"+
					"</a>";
	};
	
	function template(type, element) {
		var id = Math.random().toString().replace(".", "");
		var header = getFileDes(type, id);
		
		$("#mms_content").append($("<div class='layui-form-item' id='frame_"+id+"'>" +
	        "<label class='layui-form-label'><a class='layui-btn layui-btn-sm layui-btn-danger' href=javascript:remove('"+id+"')><i class='layui-icon'></i> 移除</a></label>"+
	        	 "<input type='hidden' name='mediaTypes' value='"+type+"'>"+
	        "<div class='layui-input-block'>"+
	        	header + "<div id='container_"+id+"'></div>" +
	        	element +
	        "</div>"+
	    "</div>"));
	    
	    if(header != "") {
			bindFile("btn_"+id, "file_" +id, "container_" + id);
		}
	};
	
	function addText() {
		template("text", "<textarea name='contents' lay-verify='required' class='layui-textarea-con' placeholder='请输入彩信内容'></textarea>");
	};
	
	function addImage() {
		template("image", "");
	};
	
</script>
</body>
</html>