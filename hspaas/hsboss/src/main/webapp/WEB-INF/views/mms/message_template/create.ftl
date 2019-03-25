<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8"/>
    <meta charset="utf-8">

    <title>融合平台</title>
    <link href="${BASE_PATH}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/style.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/dropzone.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/js/confirm/jquery-confirm.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/font-awesome.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/pace.min.css" rel="stylesheet">
    <script src="${BASE_PATH}/resources/js/bootstrap/pace.min.js"></script>
    <script src="${BASE_PATH}/resources/js/common.js"></script>
    <#include "/WEB-INF/views/common/select_search.ftl">
    <style type="text/css">
	      .progressBarContainer{
	         position:relative;
	         margin-top:10px;
	         height:8px;
	         background:#f6f6f6;
	         border-radius:3px;
	         width:100%;
	      }
	      .progressBarContainer>#loadingBar{
	         position:absolute;
	         height:8px;
	         background:#09bb07;
	         border-radius:3px;
	      }
	      .progressBarContainer>#percentBar{
	         position:absolute;
	         margin-top:22px;
	      }
	      
	      .frame_size {
    		color : #F00;
    	  } 
	</style>
</head>

<body>
<div id="container" class="effect mainnav-lg navbar-fixed mainnav-fixed">
    <#include "/WEB-INF/views/main/top.ftl">
    <div class="boxed">

        <div id="content-container">

            <div class="pageheader">
                <div class="breadcrumb-wrapper"><span class="label">所在位置:</span>
                    <ol class="breadcrumb">
                        <li><a href="#"> 管理平台 </a></li>
                        <li><a href="#"> 彩信管理 </a></li>
                        <li class="active">模板添加</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">模板添加</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-2 control-label">开户用户</label>
                                <div class="col-xs-4">
                                    <select id="userId" name="messageTemplate.userId"
                                            class="form-control selectpicker show-tick" data-live-search="true">
                                        <#if userList??>
                                            <#list userList as u>
                                                <option value="${u.userId!''}"
                                                        <#if userId?? && u.userId==userId>selected<#elseif task?? && task.userId?? && u.userId==task.userId>selected</#if>>${u.name!''}
                                                    -${u.username!''}</option>
                                            </#list>
                                        </#if>
                                    </select>
                                </div>
                                <label class="col-xs-1 control-label">路由类型</label>
                                <div class="col-xs-4">
                                    <select id="type" name="messageTemplate.routeType" class="form-control">
                                        <#if routeTypes??>
                                            <#list routeTypes as a>
                                                <option value="${a.getValue()!''}">${a.getName()!''}</option>
                                            </#list>
                                        </#if>
                                    </select>
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">模版名称</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required]" name="messageTemplate.name" id="name"
                                           placeholder="请输入模版名称，方便后续快速检索">
                                </div>
                                <label class="col-xs-1 control-label">模版标题</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required]" name="messageTemplate.title" id="title"
                                           placeholder="请输入模版标题">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">提交时间间隔</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[7],custom[number]]"
                                           name="messageTemplate.submitInterval" id="submitInterval" value="30"
                                           placeholder="请输入彩信提交时间间隔（同一号码）">
                                </div>
                                <label class="col-xs-1 control-label">提交次数上限</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[required,maxSize[5],custom[number]]"
                                           name="messageTemplate.limitTimes" id="limitTimes" value="10"
                                           placeholder="请输入彩信每天提交次数上限（同一号码）">
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-2 control-label">优先级</label>
                                <div class="col-xs-4">
                                    <input type="text"
                                           class="form-control validate[required,maxSize[10],custom[number],min[0]]"
                                           name="messageTemplate.priority" id="priority" value="5"
                                           placeholder="请输入模板优先级（越大越优先）">
                                </div>
                                <label class="col-xs-1 control-label">扩展号码</label>
                                <div class="col-xs-4">
                                    <input type="text" class="form-control validate[maxSize[20]]"
                                           name="messageTemplate.extNumber" id="extNumber" placeholder="模板扩展号码"/>
                                </div>
                            </div>
                        </div>
                    </form>
                    
                    <div class="panel-heading">
                        <h3 class="panel-title">彩信多帧内容（共<span class="frame_size">0</span>桢）</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                            	<label class="col-xs-1 control-label"></label>
                                <div class="col-xs-10">
	                            <div class="tab-base">
				                    <!--Nav Tabs-->
				                    <ul class="nav nav-tabs"></ul>
				
				                    <!--Tabs Content-->
				                    <div class="tab-content"></div>
				                </div>
				                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-5">
                                	<a href="javascript:void(0);" onclick="openFrameDialog();" class="btn btn-info btn-sm">加一桢</a>
                                    <button type="buton" onclick="formSubmit();" class="btn btn-primary btn-sm" name="buttonSubmit">提交
                                    </button>
                                </div>
                            </div>
                        </div>
                    </form>
                    
                    <div class="modal fade" id="frameModal">
			            <div class="modal-dialog" style="width:auto;height:auto;min-width:400px">
			                <div class="modal-content">
			                    <div class="modal-header">
			                        <button type="button" class="close" onclick="closeModal();"><span
			                                aria-hidden="true">&times;</span></button>
			                        <h4 class="modal-title">桢内容</h4>
			                    </div>
			                    
			                    <div class="modal-body" data-scrollbar="true" data-height="800" data-scrollcolor="#000"
			                         id="myModelBody">
			                        <select class="form-control" id="mediaType">
			                        	<#if mediaTypes??>
					    					<#list mediaTypes as mt>
					    						<option value="${mt.code!''}">${mt.title!''}(${mt.code!''})</option>
					    					</#list>
							    		</#if>
			                        </select>
			                        <br/>
			                        <textarea id="frameContent" style="display:none;" class="form-control" rows="6"></textarea>
			                        <form id="media-dropzone" action="#" class="dropzone dz-clickable" style="min-height:200px;display:none;">
                                        <div class="dz-default dz-message">
                                            <div class="dz-icon icon-wrap icon-circle icon-wrap-md"> <i class="fa fa-cloud-upload fa-2x"></i> </div>
                                            <div>
                                                <p class="dz-text">Drop files to upload</p>
                                                <p class="text-muted">or click to pick manually</p>
                                            </div>
                                        </div>
                                        <div class="fallback">
                                            <input id="file" name="file" type="file" accept='' single />
                                        </div>
                                        <div class='progressBarContainer'>
								 			<div id='loadingBar'></div>
								 			<div id="percentBar"></div>
								 		</div>
                                    </form>
			                    </div>
			                    <div class="modal-footer">
			                        <button type="button" class="btn btn-success" onclick="saveFrame();">添加</button>
			                        &nbsp;&nbsp;&nbsp;&nbsp;
			                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
			                    </div>
			                </div>
			            </div>
			        </div>

                </div>
            </div>
        </div>
        <#include "/WEB-INF/views/main/left.ftl">
    </div>
</div>
</body>
<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script>
<script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script>
<script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
<script type="text/javascript">
    function formSubmit() {
        var allCheck = $('#myform').validationEngine('validate');
        if (!allCheck) {
            return;
        }
        $.ajax({
            url: '${BASE_PATH}/mms/message_template/save',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
                if (data.result) {
                    Boss.alertToCallback('提交成功！', function () {
                        <#if task??>
                        location.href = "${BASE_PATH}/mms/record/under_way_list";
                        <#else>
                        location.href = "${BASE_PATH}/mms/message_template";
                        </#if>
                    });
                } else {
                    Boss.alert('提交失败！');
                }
            }, error: function (data) {
                Boss.alert('系统异常!请稍后重试！');
            }
        });
    }
    
    function saveFrame() {
    	addFrame(frameHtmlContent($("#mediaType").val(), $("#frameContent").val(), "mp3"));
    }
    
    function getFrameSize() {
    	return $(".nav-tabs").children("li").length;
    }
    
    function changeMediaType(type) {
    	if(type == "text") {
    		$("#frameContent").val("");
    		$("#frameContent").attr("style", "display:''");
    		$("#media-dropzone").attr("style", "display:none;");
    	} else {
    		$("#frameContent").attr("style", "display:none");
    		$("#media-dropzone").attr("style", "min-height:200px;");
    	}
    }
    
    function displayFrameSize(){
    	$(".frame_size").html(getFrameSize());
    }

	function closeModal() {
        $('#frameModal').modal('hide');
    }

    function openFrameDialog() {
    	changeMediaType("text");
        $('#frameModal').modal('show');
    }
    
    function frameHtmlContent(type, content, name) {
    	var html = "";
        if(type == "image") {
        	html = "<img width='320' height='160' src='"+content+"'/>";
        } else if(type == "vedio") {
        	html = "<video width='320' height='140' controls><source src='"+content+"' type='video/mp4'>您的浏览器不支持 video播放</video>";
        } else if(type == "audio") {
        	html = "<audio controls><source src='"+content+"' type='audio/mpeg'>您的浏览器不支持 audio播放</audio>";
        } else {
        	html = content;
        }
        
        return html;
    }
    
    function addFrame(content) {
        var index = getFrameSize() + 1;
        var liId = "li_frame-tab-" + index;
        var hrefId = "frame-tab-" + index;
        
        $(".nav-tabs li").attr("class", "");
        $(".tab-content div").attr("class", "tab-pane fade");
        
        $(".nav-tabs").append("<li class='active' id='"+liId+"'><a data-toggle='tab' href='#"+hrefId+"' aria-expanded='false'>"+index+"</a></li>");
        $(".tab-content").append("<div id='"+hrefId+"' class='tab-pane fade active in'>"+content+"</div>")
        
        displayFrameSize();
    }
    
    function removeFrame() {
        $('#frameModal').modal('show');
    }
    
    $("input[type=file]").on("change",function(){
        var file = $(this)[0].files[0];
        var form = new FormData();  
        form.append("media_type", $("#mediaType").val());
        form.append("file_original_name", file.name);                       
        form.append("file", file);

        $.ajax({
            url: '${BASE_PATH}/upload',
            type: 'POST', 
            data: form,
            processData: false,
            contentType: false,
            xhr: function() {
                var xhr = $.ajaxSettings.xhr();  
                if (xhr.upload) {
                    // 上传进度操作
                    xhr.upload.addEventListener('progress', function(e) {
                        var percentCount = ((e.loaded/e.total)*100).toFixed(0);
                        $('#loadingBar').css({"width":percentCount+"%"}); 
                        if(percentCount==100){
                           $("#percentBar").html("已完成");
                        }else{
                           $("#percentBar").html(percentCount+"%");
                        }
                    }, false);  
                }  
                return xhr;
            }
        }).done(function(res){
            console.log("上传成功");
            $("div#linkPreviewPDF").find("a").attr("href","http://192.168.217.12"+res.returnObj.url).end().show();
        }).fail(function(err){
            console.log("上传失败");
        });
    })
    
    $(function () {
        $('#myform').validationEngine('attach', {promptPosition: "topRight"});

        $('.blacklist').click(function () {
            $('#ignoreBlacklist').val($(this).val());
        });

        $('.fwords').click(function () {
            $('#ignoreForbiddenWords').val($(this).val());
        });
        
        $('#mediaType').on("change", function () {
            changeMediaType($(this).val());
        });
        
        displayFrameSize();
    });


</script>
</html>