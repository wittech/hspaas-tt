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
    <script src="${BASE_PATH}/resources/js/My97DatePicker/WdatePicker.js"></script>
    <script src="${BASE_PATH}/resources/js/common.js"></script>
    <#include "/WEB-INF/views/common/select_search.ftl">
    <#include "/WEB-INF/views/common/macro.ftl">
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
                        <li class="active"> 彩信记录查询</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">

                <div class="panel">
                    <div class="panel-body">
                        <form id="myform" method="post">
                            <input type="hidden" name="pn" id="pn" value="1"/>
                            <div class="row" style="margin-top:5px">
                            	<div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">SID</span>
                                        <input type="text" class="form-control" id="sid" name="sid"
                                               value="${sid!''}" placeholder="输入SID">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">手机号码</span>
                                        <input type="text" class="form-control" id="mobile" name="mobile"
                                               value="${mobile!''}" placeholder="输入手机号码">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">彩信内容</span>
                                        <input type="text" class="form-control" id="content" name="content"
                                               value="${content!''}" placeholder="彩信内容">
                                    </div>
                                </div>
                                <div class="col-md-3">
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
                            </div>

                            <div class="row" style="margin-top:5px">
                            	
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">开始时间</span>
                                        <input type="text" class="form-control" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="startDate"
                                               readonly style="background: #fff" name="startDate" value="${startDate!''}"
                                               placeholder="选择发送开始时间">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">结束时间</span>
                                        <input type="text" class="form-control" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="endDate"
                                               readonly style="background: #fff" name="endDate" value="${endDate!''}"
                                               placeholder="选择发送结束时间">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">发送状态</span>
                                        <select class="form-control" name="sendStatus" id="sendStatus">
                                            <option value="-1" <#if sendStatus == -1>selected</#if>>选择状态</option>
                                            <option value="0" <#if sendStatus == 0>selected</#if>>成功</option>
                                            <option value="1" <#if sendStatus == 1>selected</#if>>失败</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">回执状态</span>
                                        <select class="form-control" name="deliverStatus" id="deliverStatus">
                                            <option value="-1" <#if deliverStatus == -1>selected</#if>>选择状态</option>
                                            <option value="2" <#if deliverStatus == 2>selected</#if>>待回执</option>
                                            <option value="0" <#if deliverStatus == 0>selected</#if>>成功</option>
                                            <option value="1" <#if deliverStatus == 1>selected</#if>>失败</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="row" style="margin-top:5px">
                            	
                                <div class="col-md-3">
                                	 <div class="input-group">
                                        <span class="input-group-addon">通道</span>
                                        <select class="form-control selectpicker show-tick" name="passageId" id="passageId" data-live-search="true">
                                            <option value="">全部</option>
                                            <#if passageList??>
						    					<#list passageList?sort_by("name") as p>
						    						<#if p.status?? && p.status == 0>
						    						<option value="${p.id!''}"
						    							<#if passageId??><#if "${p.id!''}"=="${passageId!''}">selected</#if></#if>
						    						>${p.name!''}</option>
						    						</#if>
						    					</#list>
								    		</#if>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <a class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</a>
                                    <a class="btn btn-default" onclick="commonClearForm();">重&nbsp;&nbsp;&nbsp;置</a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="panel">
                   
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0"
                               width="100%">
                            <thead>
                            <tr>
                                <th>序</th>
                                <th>SID</th>
                                <th>客户名</th>
                                <th>发送通道</th>
                                <th>手机号</th>
                                <th>扩展号码</th>
                                <th>发送状态</th>
                                <th>发送时间</th>
                                <th>回执状态</th>
                                <th>回执时间</th>
                                <th>推送</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#if page??>
                            <#list page.list as pl>
                            <tr>
                                <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                <td>${pl.sid?if_exists}</td>
                                <td>${(pl.userModel.name)!''}</td>
                                <td>${pl.passageName!''}</td>
                                <td>
                                    ${(pl.mobile?trim)!''}[<#if pl.cmcp?? && pl.cmcp == 1>移动<#elseif pl.cmcp?? && pl.cmcp?? && pl.cmcp == 2>电信<#elseif pl.cmcp?? && pl.cmcp == 3>联通<#else>未知</#if>]
                                </td>
                                <td>${(pl.destnationNo)!''}</td>
                                <td>
                                    <#if pl.status == 0>
                                       <a href="javascript:void(0);" class="btn btn-success btn-xs" remark="<#if pl.remark?? && pl.remark !=''>${pl.remark?html}</#if>" onclick="showSubmitRemark(this)">成功</a>
                                    <#else>
                                       <a href="javascript:void(0);" class="btn btn-danger btn-xs" remark="<#if pl.remark?? && pl.remark !=''>${pl.remark?html}</#if>" onclick="showSubmitRemark(this)">失败</a>
                                    </#if>
                                </td>
                                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                <td>
                                    <#if pl.messageDeliver??>
                                        <a href="javascript:void(0);" <#if pl.messageDeliver.status == 0>class="btn btn-success btn-xs"<#else>class="btn btn-danger btn-xs"</#if> onclick="showDeliverMessage('${pl.messageDeliver.createTime?string('yyyy-MM-dd HH:mm:ss')}',this);" data="<@getReportDes remark=pl.messageDeliver.remark />">
                                        <#if pl.messageDeliver.status == 0>
                                            回执成功
                                        <#else>
                                            ${pl.messageDeliver.statusCode!'回执失败'}
                                        </#if>
                                    <#else>
                                        待回执
                                    </#if>
                                </td>
                                <td>
                                    <#if pl.messageDeliver??>
                                        ${pl.messageDeliver.deliverTime}
                                    <#else>
                                        --
                                    </#if>
                                </td>
                                <td>
                                    <#if pl.needPush?? && pl.needPush>
                                        <#if pl.messagePush??>
                                            <a href="javascript:void(0);" data-placement="left" data-html="true" class="btn btn-<#if pl.messagePush.status == 0>success<#else>danger</#if> btn-xs" data-toggle="popover"  onclick="showMessagePush('${pl.messagePush.createTime?string('yyyy-MM-dd HH:mm:ss')}',this);" data='${pl.messagePush.content!''}' responseContent='${pl.messagePush.responseContent!''}' title="推送信息"
		                                               data-content="
		                                               推送状态：<#if pl.messagePush.status == 0>成功<#else>失败</#if><br>
		                                               推送次数：${pl.messagePush.retryTimes!1}<br>
		                                               推送时间：${pl.messagePush.createTime?string('yyyy-MM-dd HH:mm:ss')}<br>
		                                               响应时间：${pl.messagePush.responseMilliseconds?if_exists}ms
                                            ">
                                            <#if pl.messagePush.status == 0>成功<#else>失败</#if></a>
                                        <#else>
                                            待推送
                                        </#if>
                                    <#else>
                                        --
                                    </#if>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="12" align="right" style="word-break:break-all;">
                                    <a href="javascript:preview('${(pl.templateId)!}', '${(pl.title)!}', '${(pl.content)!}');"
	                                       class="btn btn-pink btn-xs"><i class="fa fa-video-camera"></i>&nbsp;预览</a>
                                    ${pl.title!''}
                                </td>
                            </tr>
                            </#list>
                            </#if>
                            </tbody>
                        </table>
                        <#if page??>
                        <nav style="margin-top:-15px">
                        ${(page.showPageHtml)!}
                        </nav>
                        </#if>
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

        <div class="modal fade" id="deliverModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">回执信息</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                         id="myModelBody">
                        <b>接收时间：</b><input type="text" id="deliverShowTime" class="form-control" value="" />
                        <b>回执报告：</b><textarea id="deliverRemark" class="form-control" rows="6" cols="20"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>
        
        <!--推送-->
        <div class="modal fade" id="messagePushModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closePushModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">推送信息</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                         id="myModelBody">
                        <b>推送时间：</b><input type="text" id="pushShowTime" class="form-control" value="" />
                        <b>推送内容：</b><input type="text" id="pushShowContent" class="form-control" value="" />
                        <b>回执内容：</b><textarea id="pushRemark" class="form-control" rows="6" cols="20"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" onclick="closePushModal();">关闭</button>
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

        <div class="modal fade" id="submitRemarkModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">状态信息</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                         id="myModelBody">
                        <b>信息报告：</b><textarea id="submitRemark" class="form-control" rows="6" cols="20"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>

    </div>
    <script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>  <script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
    <script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
    <#include "/WEB-INF/views/base/customer/pop_user_list.ftl">
</body>
<script type="text/javascript">

    function showSubmitRemark(obj){
        $('#submitRemark').val($(obj).attr('remark'));
        $('#submitRemarkModal').modal('show');
    }

    $(function () {
        $('a[data-toggle=popover]').mouseover(function () {
            var $this = $(this);
            $this.popover('show')
        });

        $('a[data-toggle=popover]').mouseout(function () {
            var $this = $(this);
            $this.popover('hide')
        });

        $('span[data-toggle=popover]').mouseover(function () {
            var $this = $(this);
            $this.popover('show')
        });

        $('span[data-toggle=popover]').mouseout(function () {
            var $this = $(this);
            $this.popover('hide')
        });
    })

    function jumpPage(p) {
        $('#pn').val(p);
        $('#myform').attr('action', '${BASE_PATH}/mms/record/send_record_list');
        $('#myform').submit();
    }

    function showDeliverMessage(time,obj){
        $('#deliverShowTime').val(time);
        $('#deliverRemark').val($(obj).attr('data'));
        $('#deliverModal').modal('show');
    }
    
    //推送
    function showMessagePush(time,obj){
        $('#pushShowTime').val(time);
        $('#pushShowContent').val($(obj).attr('responseContent'));
        $('#pushRemark').val($(obj).attr('data'));
        $('#messagePushModal').modal('show');
    }
    
    function closePushModal() {
        $('#messagePushModal').modal('hide');
        $('#pushRemark').modal('hide');
        $('#pushShowContent').modal('hide');
        $('#pushShowTime').modal('hide');
    }
    
    function preview(templateId, title, resource) {
    	$.ajax({
            url:'${BASE_PATH}/mms/message_template/preview',
            data:{templateId : templateId, title:title, resource:resource},
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
    

    function showAllMobile(mobile) {
        $('#all_mobile').val(mobile);
        $('#myModal').modal('show');
    }

    function closeModal() {
        $('#myModal').modal('hide');
        $('#deliverModal').modal('hide');
        $('#submitRemarkModal').modal('hide');
        $('#previewModal').modal('hide');
    }

    function showSubmitList(obj, id) {
        var open = $(obj).attr('data');
        if (open == 1) {
            $(obj).html('收缩');
            $(obj).attr('data', 0);
            $('.create_child_' + id).show();
        } else {
            $(obj).html('展开');
            $(obj).attr('data', 1);
            $('.create_child_' + id).hide();
        }
    }
</script>
</html>