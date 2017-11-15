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
                            <#assign routeType = 0>
                            <#include "/WEB-INF/views/sms/passage_group/province_cmcp_table_view.ftl">
                                <div class="form-group">
                                    <label class="col-xs-2 control-label">备注</label>
                                    <div class="col-xs-8">
                                        <textarea class="form-control" name="group.comments" id="comments" rows="5">${group.comments!''}</textarea>
                                    </div>
                                </div>
                            </div>
                        <#list routeTypes as r>
                            <#if r.getValue() != 0>
                                <input type="hidden" class="routeType" value="${r.getValue()}" />
                                <#assign routeType = r.getValue()>
                                <div id="group_${r.getValue()}" style="display:none" class="groupConfig">
                                    <#include "/WEB-INF/views/sms/passage_group/province_cmcp_table_view.ftl">
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

<div class="modal fade" id="passageModal">
    <div class="modal-dialog" style="width:800px">
        <div class="modal-content" style="width:800px">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                        aria-hidden="true">&times;</span></button>
                <h4 class="modal-title" id="passageModalTitle"></h4>
            </div>
            <div class="modal-body" id="passageModelBody">

            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="confirmSelect();">确定</button>
                <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
</body>
<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
<script type="text/javascript">

    var provinceCode = 0;
    var cmcp = 0;
    var routeType = 0;
    var split_tag = '#passage_split#';

    $(function(){
        $('#myform').validationEngine('attach',{promptPosition : "centerRight"});

        $('#passageModelBody').on('click','.sourceButton',function(){
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

        $('#passageModelBody').on('click','.targetButton',function(){
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

        $('#passageModelBody').on('click','.upButton',function() {
            var $this = $(this);
            var targetPassage = $this.parent().parent().parent().find('.targetPassage').eq(0);
            var target = targetPassage.find('option:selected');
            if(target.length > 1){
                Boss.alert('只能选择一个通道！');
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

        $('#passageModelBody').on('click','.downButton',function() {
            var $this = $(this);
            var targetPassage = $this.parent().parent().parent().find('.targetPassage').eq(0);
            var target = targetPassage.find('option:selected');
            if(target.length > 1){
                Boss.alert('只能选择一个通道！');
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

        $('#passageModelBody').on('dblclick','.sourcePassage option',function() {
            var $this = $(this);
            var html = appendOption($this.attr('value'),$this.html());
            var targetPassage = $this.parent().parent().parent().find('.targetPassage').eq(0);
            targetPassage.append(html);
            targetPassage.val($this.attr('value'))
            $this.remove();
        });

        $('#passageModelBody').on('dblclick','.targetPassage option',function() {
            var $this = $(this);
            var html = appendOption($this.attr('value'),$this.html());
            var sourcePassage = $this.parent().parent().parent().find('.sourcePassage').eq(0);
            sourcePassage.append(html);
            $this.remove();
        });

        $('.selectPassage').click(function(){
            var $this = $(this);
            var province = $this.parent().parent();
            provinceCode = province.attr('code');
            var provinceName = province.attr('name');
            cmcp = $this.attr('cmcp');
            var cmcpNames = ['','移动','电信','联通'];
            var cmcpName = cmcpNames[parseInt(cmcp)];

            var title = '正在设置['+provinceName+'-'+cmcpName+']通道';

            $.ajax({
                url:'${BASE_PATH}/sms/passage_group/passageList',
                data:{province_code:provinceCode,cmcp:cmcp},
                dataType:'html',
                async:false,
                type:'post',
                success:function(html){
                    $('#passageModelBody').html(html);
                },error:function(data){
                    Boss.alert('获取通道列表失败！');
                }
            });

            var currentPosition = $('#position_'+routeType+'_'+provinceCode+'_'+cmcp);
            var selects = currentPosition.find('input[name=passageInfo]');
            if(selects.length > 0){
                var targetOptions = '';
                for(var i = 0;i<selects.length;i++){
                    var select = $(selects[i]);
                    var passageInfo = select.val();
                    var infos = passageInfo.split(split_tag);
                    var passageId = infos[0];
                    var passageName = infos[1];
                    targetOptions += appendOption(passageId,passageName);
                    var sourceOptions = $('.sourcePassage').find('option');
                    for(var j = 0;j<sourceOptions.length;j++){
                        var source = $(sourceOptions[j]);
                        if(source.attr('value') == passageId){
                            source.remove();
                        }
                    }
                }
                $('.targetPassage').html(targetOptions);
            }

            $('#passageModalTitle').html(title);
            $('#passageModal').modal('show');
        });

        $('.finishSelectPassage').mouseover(function(){
            var $this = $(this);
            var passageInfos = $this.find('input[name=passageInfo]');
            if(passageInfos.length == 0){
                return;
            }
            var showHtml = "";
            for(var i = 0;i<passageInfos.length;i++){
                var passageInfo = $(passageInfos[i]).val();
                var infos = passageInfo.split(split_tag);
                var passageName = infos[1];
                showHtml += passageName + '<br>';
            }
            $this.attr('data-content',showHtml);
            $this.popover('show');
        });

        $('.finishSelectPassage').mouseout(function(){
            var $this = $(this);
            $this.popover('hide');
        });
    });

    function appendOption(value,name){
        var html = '<option value="'+value+'">'+name+'</option>';
        return html;
    }

    function groupProvincePassageData(){
        var routeTypes = $('.routeType');

        for(var i = 0;i<routeTypes.length;i++){
            var routeType = $(routeTypes[i]);
            var group_obj = $('#group_'+routeType.val());
            var passageObjs = group_obj.find('input[name=passageInfo]');
            if(passageObjs.length <= 0){
                continue;
            }

            for(var j = 0;j < passageObjs.length;j++){
                var passageInfo = $(passageObjs[j]).val();
                passageInfo = passageInfo + split_tag + routeType.val();
                var html = appendHiddenInput('route_type_'+routeType.val(),passageInfo);
                $('#myform').append(html);
            }
        }

    }

    function confirmSelect(){
        if(provinceCode == -1 || cmcp == 0){
            Boss.alert('省份或运营商未知，请重新选择设置！');
            return;
        }
        var selects = $('.targetPassage').find('option');
        var currentPosition = $('#position_'+routeType+'_'+provinceCode+'_'+cmcp);
        var html = '已选通道<font color="red">'+selects.length+'</font>个';
        if(selects.length <= 0){
            html = '';
        }
        for(var i = 0;i<selects.length;i++){
            var select = $(selects[i]);
            var passageId = select.attr('value');
            var passageName = select.html();
            var passageInfo = passageId + split_tag + passageName + split_tag + provinceCode + split_tag + cmcp;
            html += appendHiddenInput('passageInfo',passageInfo);
        }
        currentPosition.html(html)
        $('#passageModal').modal('hide');
    }

    function switchGroupSet(obj,type){
        $('.group-type-tab li').removeClass('active');
        $(obj).parent().addClass('active');
        $('.groupConfig').hide();
        routeType = type;
        $('#group_'+type).show();
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

        var passageGroupName = $('#passageGroupName').val();
        if($.trim(passageGroupName) == ''){
            Boss.alert('请输入通道组名称！');
            return;
        }
        groupProvincePassageData();
        $.ajax({
            url:'${BASE_PATH}/sms/passage_group/update',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('修改通道组成功！',function(){
                        location.href = "${BASE_PATH}/sms/passage_group"
                    });
                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('修改通道组异常!');
            }
        });
    }
</script>
</html>