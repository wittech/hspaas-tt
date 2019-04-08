<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>彩信模板修改 - 彩信平台 - 华时融合平台</title>
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
		    text-align: center;
		    box-shadow: 0 -1px 2px 0 rgba(0,0,0,.05);
		}
		
		.layui-footer {
		    position: fixed;
		    left: 200px;
		    right: 0;
		    bottom: 0;
		    height: 44px;
		    line-height: 44px;
		    padding: 0 15px;
		}
		
		.layui-textarea-con {
		    min-height: 150px;
		    height: auto;
		    resize: vertical;
    		width: 100%;
		}
		
		.img_pre {
			min-height: 150px;
		    resize: vertical;
		    height: 100%;
    		width: 100%;
		}
		
		.file {
		  opacity:0;
		  filter:alpha(opacity=0);
		  font-size:18px;
		  position:absolute;
		  right:0;
		}
    
    	#LAY-component-grid-stack .layui-card-body{ text-align: center; height: 160px;}
    	
    </style>
</head>

<body>
<div class="layui-fluid">
  <div class="layui-card">
    <div class="layui-card-header">添加彩信模板信息</div>
    <div class="layui-card-body" style="padding: 15px;">
      <form class="layui-form" action="" lay-filter="component-form-group">
      	<div class="layui-form-item">
          <label class="layui-form-label">模板名称</label>
          <div class="layui-input-block">
          	<input type="hidden" name="id" id="id" value="${(template.id)!}">
            <input type="text" name="name" id="name" lay-verify="title" lay-verify="required" autocomplete="off" placeholder="方便记录模板，类似标签" class="layui-input" value="${(template.name)!}">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">模板标题</label>
          <div class="layui-input-block">
            <input type="text" name="title" id="title" lay-verify="title" lay-verify="required" autocomplete="off" placeholder="请输入模板标题" class="layui-input" value="${(template.title)!}">
          </div>
        </div>
        
        <div class="layui-form-item">
          <div class="layui-inline">
            <label class="layui-form-label"></label>
            <div class="layui-input-block">彩信内容信息（请点击下方的操作按钮加入您的模板内容！）</div>
          </div>
        </div>
        <div class="layui-fluid" id="LAY-component-grid-stack">
  			<div class="layui-row layui-col-space10" id="mms_content">
  			</div>
  		</div>
        <div class="layui-form-item">
          <div class="layui-input-block">
            <div class="layui-footer">
               <a href="javascript:addText();" class="layui-btn layui-btn-primary layui-btn-radius">加文本数据</a>
               <a href="javascript:addImage();" class="layui-btn layui-btn-radius">加图片数据</a>
               <a href="javascript:addAudio();" class="layui-btn layui-btn-primary layui-btn-radius">加音频数据</a>
               <a href="javascript:addVideo();" class="layui-btn layui-btn-radius">加视频数据</a>
               <a class="layui-btn layui-btn-warm layui-btn-radius" lay-submit="" lay-filter="component-form-demo1">保存模板</a>
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

<script type="text/javascript">

	layui.use(['form', 'element', 'upload'], function(){
	    var form = layui.form
	        ,layer = layui.layer
	        ,element = layui.element
	        ,$ = layui.$
	        ,upload = layui.upload;
	
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
	            	if(result.code == "0") {
	            		layer.msg('保存成功');
	            	} else {
	            		layer.msg('保存失败:[' + result.msg + "]");
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
		
	});
	
	function remove(id) {
		$("#frame_"+ id).remove();
	};
	
	function bindFile(fileId, containerId){
	     var inputJ = $("#"+fileId),
		 input  = inputJ[0],
		 con    = $("#"+containerId);
	
	 	 inputJ.change(function(e){
		 var file     = e.target.files[0],
			 thisType = file.type,
			 thisSize = file.size,
			 reader   = new FileReader();
			 
			 alert(thisType);
			 reader.readAsDataURL(file);
	
			 //文件加载成功以后，渲染到页面
			 reader.onload = function(e) {
			 	if(thisType.indexOf("image") != -1 ) {
			 	   con.html($("<img class='img_pre' src='"+e.target.result+"'>"));
			 	} else if(thisType.indexOf("video") != -1 ) {
			 	   con.html($("<video class='img_pre' controls><source src='"+e.target.result+"' type='"+thisType+"'>您的浏览器不支持 audio播放</video>"));
			 	} else if(thisType.indexOf("audio") != -1 ) {
			 	   con.html($("<audio class='img_pre' controls><source src='"+e.target.result+"' type='"+thisType+"'>您的浏览器不支持 video播放</audio>"));
			 	}
			 }
	 	});    
	
	};
	
	function getFileDes(type, id) {
		if(type == "") {
			return "";
		}
	
		var title = "";
		var accept = "";
		if(type == "image") {
			title = "选择图片文件（支持jpg/gif/bmp/png）";
			accept = "image/*";
		} else if(type == "audio") {
			title = "选择音频文件（支持amr、mpeg、mp3、aac）";
			accept = "audio/*";
		} else if(type == "video") {
			title = "选择视频文件（支持mp4、3gp）";
			accept = "video/*";
		} else {
			return "";
		}
		
		return "<a class='layui-btn layui-btn-primary layui-btn-sm'>" +
				    	"<i class='layui-icon'>&#xe67c;</i>" + title +
				        "<input name='files' lay-verify='required' type='file' id='file_"+id+"' class='file' accept='"+accept+"'>"+
					"</a>";
	};
	
	function template(type, element) {
		var id = Math.random().toString().replace(".", "");
		var header = getFileDes(type, id);
		
		$("#mms_content").append($("<div class='layui-col-md4' id='frame_"+id+"'><div class='layui-card'>" +
	        "<div class='layui-card-header'>"+
	            "<a class='layui-btn layui-btn-sm layui-btn-danger' href=javascript:remove('"+id+"')><i class='layui-icon'></i> 移除</a>"+
	        	 header +
	        "</div>"+
	        "<div class='layui-card-body' id='container_"+id+"'>"+
	        	element +
	        "</div>"+
	      "</div>"+
	    "</div>"));
	    
	    if(header != "") {
			bindFile("file_" +id, "container_" + id);
		}
	};
	
	function addText() {
		template("", "<textarea name='contents' lay-verify='required' class='layui-textarea-con' placeholder='请输入彩信内容'></textarea>");
	};
	
	function addImage() {
		template("image", "");
	};
	
	function addAudio() {
		template("audio", "");
	};
	
	function addVideo() {
		template("video", "");
	};
	
	
</script>
</body>
</html>