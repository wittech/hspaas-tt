<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>彩信模板添加 - 彩信平台 - 华时融合平台</title>
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
    		height : 300px;
    		margin-top:5px;
		}
		
		.audio_pre {
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
    <div class="layui-card-header">添加彩信模板信息</div>
    <div class="layui-card-body" style="padding: 15px;">
      <form id="myform" class="layui-form" action="" lay-filter="component-form-group" enctype="multipart/form-data">
      	<div class="layui-form-item">
          <label class="layui-form-label">模板名称</label>
          <div class="layui-input-block">
            <input type="text" name="name" id="name" lay-verify="required" autocomplete="off" placeholder="方便记录模板，类似标签" class="layui-input">
          </div>
        </div>
        <div class="layui-form-item">
          <label class="layui-form-label">模板标题</label>
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
               <a href="javascript:addAudio();" class="layui-btn layui-btn-primary layui-btn-radius">+音频数据</a>
               <a href="javascript:addVideo();" class="layui-btn layui-btn-radius">+视频数据</a>
               <a class="layui-btn layui-btn-warm layui-btn-radius" lay-submit="" lay-filter="saveModel">保存模板</a>
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

	layui.use(['form', 'element'], function(){
	    var form = layui.form
	        ,layer = layui.layer
	        ,element = layui.element
	        ,$ = layui.$;
	
	    //自定义验证规则
	    form.verify({
	    	mediaContent: function(value){
	    		var size = $("#mms_content .layui-form-item").size();
	    		if(size == 0) {
	    			return "请点击按钮添加彩信内容";
	    		}
	    	
	        }
	    });
	    
	    var l_index;
	
	    //监听提交
	    form.on('submit(saveModel)', function(data) {
	        $("#myform").ajaxSubmit({
	            url: server_domain + "/mms/template/save",
	            beforeSend : function() {
	            	l_index = layer.load(1);
	            },
	            type: "POST",
	            async: false,
	            success: function (result) {
	            	layer.close(l_index);
	            	if(result.success) {
	            		layer.msg('保存成功');
	            	} else {
	            		layer.msg('保存失败:[' + result.msg + "]");
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
			 	} else if(thisType.indexOf("video") != -1 ) {
			 	   con.html($("<video class='img_pre' controls><source src='"+e.target.result+"' type='"+thisType+"'>您的浏览器不支持 audio播放</video>"));
			 	} else if(thisType.indexOf("audio") != -1 ) {
			 	   con.html($("<audio class='audio_pre' controls><source src='"+e.target.result+"' type='"+thisType+"'>您的浏览器不支持 video播放</audio>"));
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
			title = "选择图片[jpg/gif/png/bmp]";
			accept = "image/*";
		} else if(type == "audio") {
			title = "选择音频[amr/mpeg/mp3/aac]";
			accept = "audio/*";
		} else if(type == "video") {
			title = "选择视频[支持mp4/3gp]";
			accept = "video/*";
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
	
	function addAudio() {
		template("audio", "");
	};
	
	function addVideo() {
		template("video", "");
	};
	
	
</script>
</body>
</html>