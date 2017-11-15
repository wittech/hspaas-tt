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
                        <li class="active">黑白名单管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">新增白名单</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-3 control-label">客户名称</label>
                                <div class="col-xs-1">
                                    <button type="button" onclick="openUserList();" class="btn btn-info">选择客户</button>
                                </div>
                                <div class="col-xs-6">
                                    <ul id="selectUserDiv" style="display:none" class="primary tagit ui-widget ui-widget-content ui-corner-all">

                                    </ul>
                                </div>
                                <input type="hidden" value="-1" name="whiteList.userId" id="userId" />
                            </div>

                            <#--<div class="form-group">-->
                                <#--<label class="col-xs-3 control-label">类型</label>-->
                                <#--<div class="col-xs-5">-->
                                    <#--<label class="form-radio form-icon"><input type="radio" id="type_0" name="type" checked value="0">默认</label>&nbsp;&nbsp;-->
                                    <#--<label class="form-radio form-icon"><input type="radio" id="type_1" name="type" value="1">短信</label>&nbsp;&nbsp;-->
                                    <#--<label class="form-radio form-icon"><input type="radio" id="type_2" name="type" value="2">流量</label>&nbsp;&nbsp;-->
                                    <#--<label class="form-radio form-icon"><input type="radio" id="type_3" name="type" value="3">语音</label>&nbsp;&nbsp;-->
                                <#--</div>-->
                                <#--<input type="hidden" name="whiteList.type" value = '0' id="type">-->
                            <#--</div>-->

                            <div class="form-group">
                                <label class="col-xs-3 control-label">手机号码</label>
                                <div class="col-xs-5">
                                	<textarea class="form-control validate[required]" id="mobile"
                                              name="whiteList.mobile" rows="8" placeholder="请输入手机号码，多个手机号码换行输入"></textarea>
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

<div class="modal fade" id="myModal">
    <div class="modal-dialog" style="width:850px">
        <div class="modal-content" style="width:850px">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
                <h4 class="modal-title">选择用户</h4>
            </div>
            <div class="modal-body" id="myModelBody">

            </div>
            <div class="modal-footer">
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
    $(function(){
        $('#myform').validationEngine('attach',{promptPosition : "centerRight"});

//        $('input[name=type]').click(function(){
//            alert($(this).val());
//            $('#type').val($(this).val());
//        })
    });

    function formSubmit(){
        var allCheck = $('#myform').validationEngine('validate');
        if(!allCheck){
            return;
        }
        /**
        var userId = $("#userId").val();
        if(userId == "-1"){
            alert("请选择白名单客户！");
            return;
        }
         */
        $.ajax({
            url:'${BASE_PATH}/sms/white_list/create',
            dataType:'json',
            data:$('#myform').serialize(),
            type:'post',
            success:function(data){
                if(data.result){
                    Boss.alertToCallback('新增白名单成功！',function(){
                        location.href = "${BASE_PATH}/sms/white_list"
                    });

                }else{
                    Boss.alert(data.message);
                }
            },error:function(data){
                Boss.alert('新增白名单失败!');
            }
        });
    }

    function openUserList(){
        var userId = $('#userId').val();
        $.ajax({
            url:'${BASE_PATH}/base/notification_message/userList',
            dataType:'html',
            type:'POST',
            data:{userId:userId},
            success:function(data){
                $('#myModelBody').html(data);
                $('#myModal').modal('show');
            },error:function(data){
                Boss.alert('请求用户列表异常！');
            }
        });
    }

    function userJumpPage(p){
        $('#userpn').val(p);
        var userId = $('#userId').val();
        $.ajax({
            url:'${BASE_PATH}/sms/white_list/userList?userId='+userId,
            dataType:'html',
            data:$('#userform').serialize(),
            type:'POST',
            success:function(data){
                $('#myModelBody').html(data);
            },error:function(data){
                Boss.alert('请求用户列表异常！');
            }
        });
    }

    function selectUser(userId,fullName,mobile){
        var html = '<li id="userDiv'+userId+'" class="tagit-choice ui-widget-content ui-state-default ui-corner-all tagit-choice-editable">'+
                '<span class="tagit-label">'+fullName+'</span>'+
                '<a class="tagit-close" onclick="removeItem('+userId+');">'+
                '<span class="text-icon">×</span>'+
                '<span class="ui-icon ui-icon-close"></span>'+
                '</a>'+
                '<input type="hidden" value="Tag1" name="tags" class="tagit-hidden-field">'+
                '</li>';
        $('#selectUserDiv').html(html);
        $('#selectUserDiv').show();
        $('#userId').val(userId);
        $('#myModal').modal('hide');
    }

    function removeItem(userId){
        $("#userId").val("-1");
        $('#userDiv'+userId).remove();
        $('#selectUserDiv').hide();
    }

</script>
</html>