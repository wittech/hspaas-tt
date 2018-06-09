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
                        <li> <a href="#"> 基础信息 </a> </li>
                        <li class="active">新增优先级词库</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">新增优先级词库</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-3 control-label">客户名称</label>
                                <div class="col-xs-5">
	                              	<select id="userId" name="smsPriorityWords.userId" class="form-control">
					    				<option value="0">全部</option> 
					    				<#if userList??>
							    			<#list userList as u>
							    				<option value="${u.id!''}" <#if userId??><#if u.id==userId>selected</#if></#if> >${u.name!''}-${u.userName!''}</option>
							    			</#list>
						    			</#if>
					    			</select>
					    		</div>
                            </div>
                            
                           <div class="form-group">
                                <label class="col-xs-3 control-label">状态</label>
                                <div class="col-xs-5">
                                	 <select id="status" name="smsPriorityWords.status" class="form-control">
	                                	<#if forbiddenWordsSwitch??>
	                                        <#list forbiddenWordsSwitch as a>
	                                            <option value="${a.value!''}">${a.title!''}</option>
	                                        </#list>
	                                    </#if>
	                                 </select>
                                </div>
                            </div>

							<div class="form-group">
                                <label class="col-xs-3 control-label">优先级</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100],custom[number],min[1]]" name="smsPriorityWords.priority" id="priority" placeholder="优先级[1-10]，越大越优先处理"/>
                                </div>
                            </div>

							<div class="form-group">
                                <label class="col-xs-3 control-label">词库内容</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100]]" name="smsPriorityWords.content" id="mobile" placeholder="请输入词库内容"/>
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
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
<script type="text/javascript">
    $(function(){
        $('#myform').validationEngine('attach',{promptPosition : "centerRight"});
    });

    function formSubmit(){
        var allCheck = $('#myform').validationEngine('validate');
        if(!allCheck){
            return;
        }
        $.ajax({
            url:'${BASE_PATH}/sms/priority_words/save',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('新优先级词库成功！',function(){
                        location.href = "${BASE_PATH}/sms/priority_words"
                    });
                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('新增优先级词库失败!');
            }
        });
    }

</script>
</html>