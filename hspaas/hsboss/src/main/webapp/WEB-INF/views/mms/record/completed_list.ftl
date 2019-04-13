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
    <script src="${BASE_PATH}/resources/js/My97DatePicker/WdatePicker.js"></script>
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
                        <li> <a href="#">  短信管理 </a> </li>
                        <li class="active"> 已完成短信任务 </li>
                    </ol>
                </div>
            </div>
            <div id="page-content">

                <div class="panel">
                    <div class="panel-body">
                        <form id="myform" method="post">
                            <input type="hidden" name="pn" id="pn" value="1"/>
                            <div class="row" style="margin-top:5px">
                            	<#--
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-addon">接口帐号</span>
                                        <input type="text" class="form-control" id="appKey" name="appKey"
                                               value="${appKey!''}" placeholder="输入接口帐号">
                                    </div>
                                </div>
                                -->
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-addon">所属用户</span>
                                        <input type="text" class="form-control" id="username" name="username"
                                               value="${username!''}" readonly style="background: #fff"
                                               placeholder="选择用户">
                                        <input type="hidden" name="userId" id="userId" value="${userId!-1}"/>
                                        <span class="input-group-btn">
                                            <button class="btn btn-info" type="button"
                                                    onclick="openUserList();">选择</button>
                                        </span>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-addon">手机号码</span>
                                        <input type="text" class="form-control" id="mobile" name="mobile"
                                               value="${mobile!''}" placeholder="输入手机号">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-addon">标题</span>
                                        <input type="text" class="form-control" id="title" name="title"
                                               value="${title!''}" placeholder="输入内容">
                                    </div>
                                </div>
                            </div>
                            <div class="row" style="margin-top:5px">
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-addon">开始时间</span>
                                        <input type="text" class="form-control" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="startDate"
                                               readonly style="background: #fff" name="startDate" value="${startDate!''}"
                                               placeholder="选择开始时间">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-addon">结束时间</span>
                                        <input type="text" class="form-control" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="endDate"
                                               readonly style="background: #fff" name="endDate" value="${endDate!''}"
                                               placeholder="选择结束时间">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <div class="input-group">
                                        <span class="input-group-addon">SID</span>
                                        <input type="text" class="form-control" id="sid" name="sid"
                                               value="<#if sid?? && sid gt 0>${sid?if_exists}</#if>" placeholder="输入SID">
                                    </div>
                                </div>
                            </div>
                            <div class="row" style="margin-top:5px">
                                 <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">模板ID</span>
                                        <input type="text" class="form-control" id="modelId" name="modelId"
                                               value="${(modelId)!}">
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <a class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</a>
                                    <a class="btn btn-default" onclick="commonClearForm();">重&nbsp;&nbsp;&nbsp;置</a>
                                </div>
                                <div class="col-md-4">
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="panel">
                    <div class="panel-heading">
                        <h3 class="panel-title">
                            <span>已完成短信任务记录</span>
                        </h3>

                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                            <thead>
                            <tr>
                                <th width="2%">序</th>
                                <th width="10%">SID</th>
                                <th width="5%">模式</th>
                                <th width="5%">返</th>
                                <th width="10%">客户名</th>
                                <th width="10%">手机号</th>
                                <th width="5%">错号</th>
                                <th width="5%">重号</th>
                                <th width="5%">来源</th>
                                <th width="10%">提交时间</th>
                                <th width="10%">分包时间</th>
                                <th width="15%">操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#assign complateChildTaskCheck = macro.doOper("10001003001") />
							<#assign sendRecordListCheck = macro.doOper("10001003002") />
                            <#list page.list as pl>
                            <tr>
                               <td rowspan="2" style="background: #fff;text-align: center;">${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>${(pl.sid)!''}</td>
                                <td>
                                	<button type="button" class="btn btn-info btn-xs">
                                		<#if pl.modelId?? && pl.modelId != ''>
                                       	 模<#else>普
	                                    </#if>
                                	</button>
                                </td>
                                <td>
                                	<button type="button" class="btn btn-success btn-xs">${(pl.returnFee)!'0'}</button>
                                </td>
                                <td>${(pl.userModel.name)!''}</td>
                                <td>
                                    <#if pl.mobiles?? && pl.mobiles?size gt 1>
                                    ${pl.firstMobile!''}...
                                    	<#--
                                    	<button type="button" onclick="showAllMobile('${pl.mobile!''}');" class="btn btn-primary btn-xs">${pl.mobiles?size}</button>
                                         -->
                                        <button type="button" class="btn btn-primary btn-xs">${pl.mobiles?size}</button>
                                    <#else>
                                    ${(pl.mobile)!''}
                                    </#if>
                                </td>
                                <td>
                                    <#if pl.showErrorMobiles?? && pl.showErrorMobiles?size gt 1>
                                    ${pl.showErrorFirstMobile!''}...
                                    	<#--
                                    	<button type="button" onclick="showAllMobile('${pl.errorMobiles!''}');"
                                                class="btn btn-danger btn-xs">${pl.showErrorMobiles?size}</button>
                                    	 -->
                                        <button type="button" class="btn btn-danger btn-xs">${pl.showErrorMobiles?size}</button>
                                    <#else>
                                    ${(pl.errorMobiles)!'--'}
                                    </#if>
                                </td>
                                 <td>
                                    <#if pl.showRepeatMobiles?? && pl.showRepeatMobiles?size gt 1>
                                      <#--
                                        <button type="button" onclick="showAllMobile('${pl.repeatMobiles!''}');"
                                                class="btn btn-danger btn-xs">${pl.showRepeatMobiles?size}</button>
                                      -->
                                       <button type="button" class="btn btn-danger btn-xs">${pl.showRepeatMobiles?size}</button>
                                    <#else>
                                    ${(pl.repeatMobiles)!'--'}
                                    </#if>
                                </td>
                                <td>
                                    <#if pl.appType == 1>
                                       	 W
                                    <#elseif pl.appType == 2>
                                        D
                                    <#elseif pl.appType == 3>
                                        B
                                    <#else>
                                                                                 未知
                                    </#if>
                                </td>
                                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                <td>
                                    <#if pl.processTime??>
                                    ${pl.processTime?string('yyyy-MM-dd HH:mm:ss')}
                                    <#else>
                                        --
                                    </#if>
                                </td>
                                <td>
                                	<#if complateChildTaskCheck>
                                    	<a href="${BASE_PATH}/mms/record/complate_child_task?sid=${pl.sid}" class="btn btn-info btn-xs">子任务</a>
                                    </#if>
                                    <#if sendRecordListCheck>
                                    	<a href="${BASE_PATH}/mms/record/send_record_list?sid=${pl.sid}" class="btn btn-success btn-xs">发送记录</a>
                                    </#if>
                                    <a href="javascript:preview('${(pl.modelId)!}', '${(pl.title)!}', '${(pl.body)!}');"
	                                       class="btn btn-pink btn-xs"><i class="fa fa-video-camera"></i>&nbsp;预览</a>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="13" align="right" style="word-break:break-all;">
                                    	<span style="word-break:break-all;">
	                                       ${pl.title!''}
	                                    </span>
                                </td>
                            </tr>
                            </#list>
                            </tbody>
                        </table>
                        <nav style="margin-top:-15px">
                        ${(page.showPageHtml)!}
                        </nav>
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
                        <h4 class="modal-title">手机号</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000" id="myModelBody">
                        <textarea id="all_mobile" class="form-control" rows="6"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>
        
        <div class="modal fade" id="previewModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">彩信内容</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="700" data-scrollcolor="#000" id="myPreviewModelBody">
                        
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>

        <div class="modal fade" id="userModal">
            <div class="modal-dialog" style="width:850px">
                <div class="modal-content" style="width:850px">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">选择用户</h4>
                    </div>
                    <div class="modal-body" id="userModelBody">

                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                        <button type="button" class="btn btn-success" onclick="clearUser();">清空</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div><!-- /.modal -->

    </div>
    <script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>  <script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
</body>
<script type="text/javascript">
	$(function(){

        $('span[data-toggle=popwordover]').mouseover(function () {
            var $this = $(this);
            if($this.attr('data-content') != ''){
                $this.popover('show');
            }
        });

        $('span[data-toggle=popwordover]').mouseout(function () {
            var $this = $(this);
            $this.popover('hide')
        });
        
    });
    
    function preview(modelId, title, resource) {
    	$.ajax({
            url:'${BASE_PATH}/mms/message_template/preview',
            data:{modelId : modelId, title:title, resource:resource},
            dataType:'html',
            type:'post',
            success:function(data){
                $("#myPreviewModelBody").html(data);
            },error:function(data){
                Boss.alert('请求失败！');
            }
        });
    
        $('#previewModal').modal('show');
    }

    function jumpPage(p){
        $('#pn').val(p);
        $('#myform').attr('action','${BASE_PATH}/mms/record/completed_list');
        $('#myform').submit();
    }

    function showAllMobile(mobile){
        $('#all_mobile').val(mobile);
        $('#myModal').modal('show');
    }

    function closeModal(){
        $('#myModal').modal('hide');
        $('#previewModal').modal('hide');
    }

    function openUserList() {
        var userId = $('#userId').val();
        $.ajax({
            url: '${BASE_PATH}/base/customer/commonUserList',
            dataType: 'html',
            type: 'POST',
            data: {userId: userId},
            success: function (data) {
                $('#userModelBody').html(data);
                $('#userModal').modal('show');
            }, error: function (data) {
                alert('请求用户列表异常！');
            }
        });
    }

    function selectUser(userId, fullName, mobile) {
        $('#userId').val(userId);
        $('#username').val(fullName);
        $('#userModal').modal('hide');
    }

    function clearUser() {
        $('#userId').val("");
        $('#username').val("");
        $('#userModal').modal('hide');
    }

    function userJumpPage(p){
        $('#userpn').val(p);
        var userId = $('#userId').val();
        $.ajax({
            url:'${BASE_PATH}/base/customer/commonUserList?userId='+userId,
            dataType:'html',
            data:$('#userform').serialize(),
            type:'POST',
            success:function(data){
                $('#userModelBody').html(data);
            },error:function(data){
                alert('请求用户列表异常！');
            }
        });
    }
</script>
</html>