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
                        <li> <a href="#"> 短信管理 </a> </li>
                        <li class="active">通道管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">修改通道组</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <input type="hidden" name="group.id" value="${group.id}">
                        <div class="panel-body">
                            <ul class="nav nav-tabs group-type-tab" role="tablist" style="margin-bottom: 20px">
                            <#list routeTypes as r>
                                <li role="presentation" <#if r_index == 0>class="active" style="color:red"</#if>><a href="javascript:void(0);" onclick="switchGroupSet(this,${r.getValue()});">${r.getName()}</a></li>
                            </#list>
                            </ul>
                            <div id="group_0" class="groupConfig">
                                <input type="hidden" class="routeType" value="0" />
                                <div class="form-group">
                                    <label class="col-xs-2 control-label">通道组名称</label>
                                    <div class="col-xs-8">
                                        <input type="text" class="form-control validate[required,maxSize[100]]" value="${group.passageGroupName!''}" name="group.passageGroupName" id="passageGroupName" placeholder="请输入通道组名称">
                                    </div>
                                </div>
                            <#assign currentRouteType = 0>
                            <#include "/WEB-INF/views/sms/passage_group/groupItems.ftl">
                                <div class="form-group">
                                    <label class="col-xs-2 control-label">备注</label>
                                    <div class="col-xs-8">
                                        <textarea class="form-control" name="group.comments" id="comments" rows="5">${group.comments!''}</textarea>
                                    </div>
                                </div>
                            </div>
                        <#list routeTypes as r>
                            <#if r.getValue() != 0>
                                <#assign currentRouteType = r.getValue()>
                                <input type="hidden" class="routeType" value="${r.getValue()}" />
                                <div id="group_${r.getValue()}" style="display:none" class="groupConfig">
                                    <#include "/WEB-INF/views/sms/passage_group/groupItems.ftl">
                                </div>
                            </#if>
                        </#list>

                            <div class="form-group">
                                <div class="col-xs-11 col-xs-offset-6">
                                    <button type="button" onclick="formSubmit();" class="btn btn-primary" name="buttonSubmit">提交</button>
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

        $('.sourceButton').click(function(){
            var $this = $(this);
            var sourcePassage = $this.parent().parent().parent().find('.sourcePassage').eq(0);
            var sources = sourcePassage.find('option:selected');
            var html = "";
            for(var i = 0;i<sources.length;i++){
                var source = $(sources[i]);
                html += appendOption(source.attr('value'),source.html());
                source.remove();
            }
            var targetPassage = $this.parent().parent().parent().find('.targetPassage').eq(0);
            targetPassage.append(html);
        });

        $('.targetButton').click(function(){
            var $this = $(this);
            var targetPassage = $this.parent().parent().parent().find('.targetPassage').eq(0);
            var targets = targetPassage.find('option:selected');
            var html = "";
            for(var i = 0;i<targets.length;i++){
                var target = $(targets[i]);
                html += appendOption(target.attr('value'),target.html());
                target.remove();
            }
            var sourcePassage = $this.parent().parent().parent().find('.sourcePassage').eq(0);
            sourcePassage.append(html);
        });

        $('.upButton').on('click','',function() {
            var $this = $(this);
            var targetPassage = $this.parent().parent().parent().find('.targetPassage').eq(0);
            var target = targetPassage.find('option:selected');
            if(target.length > 1){
                alert('只能选择一个通道！');
                return;
            }
            var allTargets = targetPassage.find('option');
            for(var i = 0;i<allTargets.length;i++){
                var current = $(allTargets[i]);
                if(i == 0 && current.attr('value') == target.attr('value')){
                    return;
                }
                if(current.attr('value') == target.attr('value')){
                    var previous = allTargets[i-1];
                    allTargets[i-1] = current;
                    allTargets[i] = previous;
                    break;
                }
            }
            var html = '';
            for(var i = 0;i<allTargets.length;i++){
                var cp = $(allTargets[i]);
                html += appendOption(cp.attr('value'),cp.html());
            }
            targetPassage.html(html);
            targetPassage.find('option:selected').attr('selected',false);
            targetPassage.val(target.attr('value'));
        });

        $('.downButton').on('click','',function() {
            var $this = $(this);
            var targetPassage = $this.parent().parent().parent().find('.targetPassage').eq(0);
            var target = targetPassage.find('option:selected');
            if(target.length > 1){
                alert('只能选择一个通道！');
                return;
            }
            var allTargets = targetPassage.find('option');
            for(var i = 0;i<allTargets.length;i++){
                var current = $(allTargets[i]);
                if(i == (allTargets.length - 1) && current.attr('value') == target.attr('value')){
                    return;
                }
                if(current.attr('value') == target.attr('value')){
                    var next = allTargets[i+1];
                    allTargets[i+1] = current;
                    allTargets[i] = next;
                    break;
                }
            }
            var html = '';
            for(var i = 0;i<allTargets.length;i++){
                var cp = $(allTargets[i]);
                html += appendOption(cp.attr('value'),cp.html());
            }
            targetPassage.html(html);
            targetPassage.find('option:selected').attr('selected',false);
            targetPassage.val(target.attr('value'));
        });

        $('.sourcePassage').on('dblclick','option',function() {
            var $this = $(this);
            var html = appendOption($this.attr('value'),$this.html());
            var targetPassage = $this.parent().parent().parent().find('.targetPassage').eq(0);
            targetPassage.append(html);
            targetPassage.val($this.attr('value'))
            $this.remove();
        });

        $('.targetPassage').on('dblclick','option',function() {
            var $this = $(this);
            var html = appendOption($this.attr('value'),$this.html());
            var sourcePassage = $this.parent().parent().parent().find('.sourcePassage').eq(0);
            sourcePassage.append(html);
            $this.remove();
        });

        removeSourcePassageByTarget();
    });

    function removeSourcePassageByTarget(){
        var cmcps = $('.cmcps');
        for(var i = 0;i<cmcps.length;i++){
            var cmcp = $(cmcps[i]);
            var targets = cmcp.find('.targetPassage option');
            if(targets == 0){
                return;
            }
            var sources = cmcp.find('.sourcePassage option');
            for(var j = 0;j<sources.length;j++){
                var source = $(sources[j]);
                var sourceValue = source.attr('value');
                var isExists = false;
                for(var k = 0;k<targets.length;k++){
                    var target = $(targets[k]);
                    var targetValue = target.attr('value');
                    if(sourceValue == targetValue){
                        isExists = true;
                        break;
                    }
                }
                if(isExists){
                    source.remove();
                }
            }
        }

    }

    function appendOption(value,name){
        var html = '<option value="'+value+'">'+name+'</option>';
        return html;
    }

    function switchGroupSet(obj,type){
        $('.group-type-tab li').removeClass('active');
        $(obj).parent().addClass('active');
        $('.groupConfig').hide();
        $('#group_'+type).show();
    }

    function appendOption(value,text){
        var option = '<option value="'+value+'">'+text+'</option>';
        return option;
    }

    function disponseSelectGroupToSubmit(){
        var routeTypes = $('.routeType');
        for(var i = 0;i<routeTypes.length;i++){
            var routeType = $(routeTypes[i]);
            var group_obj = $('#group_'+routeType.val());
            var cmcps = group_obj.find('.cmcps');
            var html = "";
            var index = 0;
            for(var j = 0;j<cmcps.length;j++){
                var cmcp = $(cmcps[j]);
                var cmcp_val = cmcp.find('.cmcpType').eq(0).val();
                var passage_selects = cmcp.find('.targetPassage option');
                for(var k = 0;k<passage_selects.length;k++){
                    var passage = $(passage_selects[k]);
                    var value = passage.attr('value');
                    var name = "route_type_"+routeType.val()+"_"+index;
                    html += appendHiddenInput(name+'.passageId',value);
                    html += appendHiddenInput(name+'.priority',value);
                    html += appendHiddenInput(name+'.cmcp',cmcp_val);
                    index++;
                }
            }
            html += appendHiddenInput('route_type_'+routeType.val()+'_count',index);
            $('#myform').append(html);
        }
    }

    function appendHiddenInput(name,value){
        var html = '<input type="hidden" value="'+value+'" name="'+name+'">';
        return html;
    }

    function formSubmit(){
        var allCheck = $('#myform').validationEngine('validate');
        if(!allCheck){
            return;
        }
        var group_obj = $('#group_0');
        var cmcps = group_obj.find('.cmcps');
        var length = cmcps.length - 1;
        var has = true;
        for(var i = 0;i<=length;i++){
            if(i == 3){
                break;
            }
            var cmcp = $(cmcps[i]);
            var passage_selects = cmcp.find('.targetPassage option');
            if(passage_selects.length <= 0){
                has = false;
                break;
            }
        }
        if(!has){
            alert("默认路由通道组中，三网通道至少选择一个！");
            return;
        }
        disponseSelectGroupToSubmit();
        $.ajax({
            url:'${BASE_PATH}/mms/passage_group/update',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    alert('修改通道组成功！');
                    location.href = "${BASE_PATH}/mms/passage_group"
                }else{
                    alert(data.message);
                }
            },error:function(data){
                alert('修改通道组异常!');
            }
        });
    }
</script>
</html>