<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8" />
    <meta charset="utf-8">

    <title>融合平台</title>
    <link href="${BASE_PATH}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/style.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/js/confirm/jquery-confirm.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/font-awesome.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/jquery.tagit.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/pace.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/jquery-ui.min.css" rel="stylesheet">
    <script src="${BASE_PATH}/resources/js/bootstrap/pace.min.js"></script>
    <script src="${BASE_PATH}/resources/js/common.js"></script>
</head>

<body>
<div id="container" class="effect mainnav-lg navbar-fixed mainnav-fixed">
<#include "/WEB-INF/views/main/top.ftl">
    <div class="boxed">

        <div id="content-container">

            <div class="pageheader">
                <div class="breadcrumb-wrapper"> <span class="label">所在位置:</span>
                    <ol class="breadcrumb">
                        <li> <a href="#"> 管理平台 </a> </li>
                        <li> <a href="#"> 短信管理 </a> </li>
                        <li class="active">敏感词管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">新增敏感词</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-3 control-label">敏感词</label>
                                <div class="col-xs-5">
                                	<input type="text" class="form-control validate[required,maxSize[32]]"
                                           name="forbiddenWords.word" id="word" placeholder="输入敏感词">
                                    <a href="javascript:void(0);" onclick="insertCode();" class="btn btn-success btn-sm">通配敏感词符号(.*)</a>
                                </div>
                            </div>
                            
                            <div class="form-group">
                                <label class="col-xs-3 control-label">标签</label>
                                <div class="col-xs-5">
                                	<input type="text" class="form-control validate[required,maxSize[32]]"
                                           name="forbiddenWords.label" id="label" placeholder="用于显示敏感词区分" />
                                </div>
                            </div>
				    		
                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-5">
                                    <a onclick="formSubmit();" class="btn btn-primary" name="buttonSubmit">提交</a>
                                </div>
                            </div>

                        </div>
                    </form>
                </div>
            </div>
        </div>
    <#include "/WEB-INF/views/main/left.ftl">
    </div>
</div>

</body>
<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/jquery-ui.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> 
<script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> 
<script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
<script type="text/javascript">
	
	var source = [];
	
	<#if wordLables??>
	<#list wordLables as s >  
       source.push("${(s)!}");
    </#list>
	</#if>
	
    $(function(){
        $('#myform').validationEngine('attach',{promptPosition : "centerRight"});
        
        // 加载自动完成事件
        $("#label").autocomplete({source});
    });

    function formSubmit(){
        var allCheck = $('#myform').validationEngine('validate');
        if(!allCheck){
            return;
        }
        
        $.ajax({
            url:'${BASE_PATH}/sms/forbidden_word/create',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('新增敏感成功！',function(){
                        location.href = "${BASE_PATH}/sms/forbidden_word"
                    });
                }else{
                    Boss.alert("新增敏感失败");
                }
            },error:function(data){
                Boss.alert('新增敏感词失败!');
            }
        });
    }
    
    (function ($) {
        $.fn.extend({
            insertAtCaret: function (myValue) {
                var $t = $(this)[0];
                if (document.selection) {
                    this.focus();
                    sel = document.selection.createRange();
                    sel.text = myValue;
                    this.focus();
                } else
                    if ($t.selectionStart || $t.selectionStart == '0') {
                        var startPos = $t.selectionStart;
                        var endPos = $t.selectionEnd;
                        var scrollTop = $t.scrollTop;
                        $t.value = $t.value.substring(0, startPos) + myValue + $t.value.substring(endPos, $t.value.length);
                        this.focus();
                        $t.selectionStart = startPos + myValue.length;
                        $t.selectionEnd = startPos + myValue.length;
                        $t.scrollTop = scrollTop;
                    } else {
                        this.value += myValue;
                        this.focus();
                    }
            }
        })
    })(jQuery);
    
    function insertCode(){
    	$("#word").insertAtCaret(".*");
    };

</script>
</html>