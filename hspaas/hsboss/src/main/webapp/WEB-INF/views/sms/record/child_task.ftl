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
                        <li><a href="#"> 短信管理 </a></li>
                        <li class="active"> 短信子任务</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">

                <div class="panel">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            <span>子任务列表</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0"
                               width="100%">
                            <thead>
                            <tr>
                                <th>序</th>
                                <th>运营商</th>
                                <th>省份名称</th>
                                <th>通道</th>
                                <th>手机号</th>
                                <th>状态</th>
                                <th>分包信息</th>
                                <th>创建时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list taskList as pl>
                            <tr>
                                <td rowspan="2" style="background: #fff;text-align: center;">${(pl_index+1)}</td>
                                <td>
                                    <#if pl.cmcp == 1>移动
                                    <#elseif pl.cmcp == 2>电信
                                    <#elseif  pl.cmcp == 3>联通
                                    <#else>未知
                                    </#if>
                                </td>
                                <td>${(pl.provinceName)!'无'}</td>
                                <td>${(pl.passageName)!'无'}</td>
                                <td>
                                    <#if pl.mobiles?? && pl.mobiles?size gt 1>
                                    ${pl.firstMobile!''}...
                                        <button type="button" onclick="showAllMobile('${pl.mobile!''}');"
                                                class="btn btn-primary btn-xs">${pl.mobiles?size}</button>
                                    <#else>
                                    ${(pl.mobile)!''}
                                    </#if>
                                </td>
                                <td>
                                    <#if pl.status == 0>
                                        	待处理
                                    <#elseif pl.status == 1>
                                       	 自动处理
                                    <#elseif pl.status == 2>
                                        	手动处理
                                    <#elseif pl.status == 3>
                                        驳回
                                    <#else >
                                        	未知
                                    </#if>
                                </td>
                                <td>
                                    ${(pl.remark)!}
                                </td>
                                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                <td>
                                    <#if pl.status == 0>
                                         <a href="javascript:void(0);"
                                      		 class="btn btn-warning btn-xs" onclick="refuseTask(${pl.id});">驳回</a>
                                         <a href="javascript:void(0);"
                                           class="btn btn-danger btn-xs" onclick="taskPass(${pl.id});">强制通过</a>
                                        <#if pl.isPassageError()>
                                        <a href="javascript:void(0);"
                                           class="btn btn-success btn-xs" onclick="openPassageList(${pl.id},${pl.cmcp});">分配通道</a>
                                        </#if>
                                        <#--
                                        <#if pl.isTemplateError()>
                                        <a href="${BASE_PATH }/sms/message_template/create?sid=${pl.sid}&childTaskId=${pl.id}"
                                           class="btn btn-success btn-xs">模板报备</a>
                                        </#if>
                                        <#if pl.isWordError()>
                                        <a  href="${BASE_PATH }/sms/message_template/edit?id=${pl.messageTemplateId}&sid=${pl.sid}&childTaskId=${pl.id}"
                                           class="btn btn-success btn-xs">敏感词导白</a>
                                        </#if>
                                        -->
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="10" align="right" style="word-break:break-all;">
                                    ${pl.content!''}
                                </td>
                            </tr>
                            </#list>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        <#include "/WEB-INF/views/main/left.ftl">
        </div>

        <div class="modal fade" id="myModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">选择通道</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                         id="myModelBody">
                        <select class="form-control" id="finalPassageId">

                        </select>
                        <input type="hidden" id="taskChildId" value="-1" />
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success" onclick="saveFinalPassage();">保存</button>
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>


        <div class="modal fade" id="mobileModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">手机号</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                         id="myModelBody">
                        <textarea id="all_mobile" class="form-control" rows="6"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>

    </div>
    <script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
    <script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
</body>
<script type="text/javascript">


    function showAllMobile(mobile) {
        $('#all_mobile').val(mobile);
        $('#mobileModal').modal('show');
    }

    function refuseTask(childId){
        Boss.confirm('确定要驳回吗？',function(){
            $.ajax({
                url:'${BASE_PATH}/sms/record/refuseTask',
                data:{childId:childId},
                dataType:'json',
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            location.reload();
                        }
                    });
                },error:function(data){
                    Boss.alert('驳回失败！');
                }
            })
        });
    }


    function taskPass(childId){
        Boss.confirm('确定要强制通过吗？',function(){
            $.ajax({
                url:'${BASE_PATH}/sms/record/pass_task',
                data:{childId:childId,status:2},
                dataType:'json',
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            location.reload();
                        }
                    });
                },error:function(data){
                    Boss.alert('强制通过失败！');
                }
            })
        });
    }

    function openPassageList(childId,cmcp){
        $.ajax({
            url:'${BASE_PATH}/sms/record/passage_list',
            data:{cmcp:cmcp},
            type:'post',
            dataType:'json',
            success:function(data){
                var html = '';
                for(var i = 0;i<data.length;i++){
                    var name = data[i].name;
                    var style = '';
                    if(data[i].cmcp == 4){
                        style = 'style="background:#ccc"';
                    }
                    html += '<option value="'+data[i].id+'" '+style+'>'+name+'</option>';
                }
                $('#finalPassageId').html(html);
                $('#taskChildId').val(childId);
                $('#myModal').modal('show');
            },error:function(data){
                Boss.alert('获取通道失败！');
            }
        })
    }

    function saveFinalPassage(){
        var passageId = $('#finalPassageId').val();
        var childId = $('#taskChildId').val();
        $.ajax({
            url:'${BASE_PATH}/sms/record/update_passage',
            data:{childId:childId,passageId:passageId},
            dataType:'json',
            success:function(data){
                Boss.alertToCallback(data.message,function(){
                    if(data.result){
                        location.reload();
                    }
                });
            },error:function(data){
                Boss.alert('切换通道失败！');
            }
        })
    }

    function closeModal() {
        $('#myModal').modal('hide');
        $('#mobileModal').modal('hide');
    }
</script>
</html>