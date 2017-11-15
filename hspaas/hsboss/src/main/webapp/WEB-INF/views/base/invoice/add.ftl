<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8"/>
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
                <div class="breadcrumb-wrapper"><span class="label">所在位置:</span>
                    <ol class="breadcrumb">
                        <li><a href="#"> 管理平台 </a></li>
                        <li><a href="#"> 基础信息 </a></li>
                        <li class="active">发票管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <h3 class="panel-title">新增发票</h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body">
                            <div class="form-group">
                                <label class="col-xs-3 control-label">发票抬头</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100]]" name="record.title" id="title" placeholder="请输入发票抬头" />
                                </div>
                            </div>
                            <div class="form-group">
                                <label class="col-xs-3 control-label">发票类型</label>

                                <div class="col-xs-5">
                                    <label class="form-radio form-icon">
                                        <input type="radio" id="type_1" name="type" checked value="0">
                                        普通发票
                                    </label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon">
                                        <input type="radio" id="type_2" name="type" value="1">
                                        增值税专用发票
                                    </label>&nbsp;&nbsp;
                                </div>
                                <input type="hidden" name="record.type" id="recordType" value="0" />
                            </div>

                            <div class="form-group" id="taxNumberDiv" style="display:none">
                                <label class="col-xs-3 control-label">税号</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control" name="record.taxNumber" id="taxNumber" placeholder="请输入税号" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">所属用户</label>

                                <div class="col-xs-5">
                                    <a href="javascript:void(0);" onclick="openUserList();" id="selectUserBtn" class="btn btn-info btn-xs">选择用户</a>
                                </div>
                                <input type="hidden" name="record.userId" id="userId" value="-1" />
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">开票金额</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,custom[number],min[10]]" name="record.money" id="money" placeholder="请输入发票金额" />
                                </div>
                                <label class="col-xs-2 control-label" id="showBalanceDiv" style="display:none;text-align: left;color:red;font-weight: bold">
                                    最多可开金额448元
                                </label>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">邮寄地址</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required]" name="record.address" id="address" placeholder="请输入邮寄地址" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">邮政编码</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required]" name="record.zipCode" id="zipCode" placeholder="请输入邮编" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">收件人</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[20]]" name="record.mailMan" id="mailMan" placeholder="请输入收件人" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">手机号</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[20]]" name="record.mobile" id="mobile" placeholder="请输入手机号码" />
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">状态处理</label>
                                <div class="col-xs-5">
                                    <label class="form-radio form-icon">
                                        <input type="radio" id="status_1" name="status" checked value="0">
                                        待处理
                                    </label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon">
                                        <input type="radio" id="status_2" name="status" value="1">
                                        邮寄
                                    </label>&nbsp;&nbsp;
                                </div>
                                <input type="hidden" name="record.status" id="status" value="0" />
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">支付费用类型</label>
                                <div class="col-xs-5">
                                    <label class="form-radio form-icon">
                                        <input type="radio" id="chargeType_1" name="chargeType" checked value="0">
                                        到付
                                    </label>&nbsp;&nbsp;
                                    <label class="form-radio form-icon">
                                        <input type="radio" id="chargeType_2" name="chargeType" value="1">
                                        预付
                                    </label>&nbsp;&nbsp;
                                </div>
                                <input type="hidden" name="record.chargeType" id="chargeType" value="0" />
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">快递单号</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100]]"
                                           name="record.trackingNumber" id="trackingNumber" placeholder="请输入快递单号">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">快递公司</label>
                                <div class="col-xs-5">
                                    <input type="text" class="form-control validate[required,maxSize[100]]"
                                           name="record.express" id="express" placeholder="请输入快递公司">
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">用户备注</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control" name="record.memo" id="memo" rows="5"></textarea>
                                </div>
                            </div>

                            <div class="form-group">
                                <label class="col-xs-3 control-label">回复备注</label>
                                <div class="col-xs-5">
                                    <textarea class="form-control" name="record.remark" id="remark" rows="5"></textarea>
                                </div>
                            </div>
                            <div class="form-group">
                                <div class="col-xs-9 col-xs-offset-3">
                                    <button type="button" onclick="formSubmit();" class="btn btn-primary"
                                            name="buttonSubmit">提交
                                    </button>
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
    $(function () {
        $('#myform').validationEngine('attach', {promptPosition: "centerRight"});

        $('input[name=chargeType]').click(function(){
            $('#chargeType').val($(this).val());
        });

        $('input[name=status]').click(function(){
            $('#status').val($(this).val());
        });

        $('input[name=type]').click(function(){
            $('#recordType').val($(this).val());
            if($(this).val() == 1){
                $('#taxNumberDiv').show();
            }else{
                $('#taxNumberDiv').hide();
            }
        });
    });

    function openUserList(){
        var userId = $('#userId').val();
        $.ajax({
            url:'${BASE_PATH}/base/customer/commonUserList',
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

    var balance = 0;

    function userJumpPage(p){
        $('#userpn').val(p);
        var userId = $('#userId').val();
        $.ajax({
            url:'${BASE_PATH}/base/customer/commonUserList?userId='+userId,
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
        $('#selectUserBtn').html(fullName);
        $('#userId').val(userId);
        $.ajax({
            url:'${BASE_PATH}/base/invoice/userBalance',
            dataType:'json',
            data:{userId:userId},
            type:'POST',
            success:function(data){
                balance = data.money;
                $('#showBalanceDiv').html("最多可开金额"+data.money+"元");
                $('#showBalanceDiv').show();
            },error:function(data){
                Boss.alert('请求用户列表异常！');
            }
        });
        $('#myModal').modal('hide');
    }

    function formSubmit() {
        var allCheck = $('#myform').validationEngine('validate');
        if (!allCheck) {
            return;
        }
        var userId = $('#userId').val();
        if(userId == -1){
            Boss.alert('请选择用户！');
            return;
        }
        var money = parseFloat($('#money').val());
        balance = parseFloat(balance);
        if(money > balance || balance <= 0){
            Boss.alert('可开具发票的余额不足，请重新输入！');
            return;
        }
        $.ajax({
            url: '${BASE_PATH}/base/invoice/create',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
                if (data.result) {
                    Boss.alertToCallback('新增发票成功！',function(){
                        location.href = "${BASE_PATH}/base/invoice"
                    });
                } else {
                    Boss.alert(data.message);
                }
            }, error: function (data) {
                Boss.alert('新增发票失败!');
            }
        });
    }
</script>
</html>