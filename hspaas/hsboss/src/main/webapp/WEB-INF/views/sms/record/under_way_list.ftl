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
                        <li class="active">待处理短信任务</li>
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
                                               value="<#if sid?? && sid gt 0>${sid?if_exists}</#if>" placeholder="输入SID">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">所属用户</span>
                                        <select class="form-control selectpicker show-tick" id="userId" name="userId" data-live-search="true">
				                        	<option value="-1">--选择用户--</option>
				                        	<#if userList??>
					    					<#list userList as p>
					    						<option value="${p.id!''}" <#if userId?? && userId == p.id>selected</#if>>${p.name!''}</option>
					    					</#list>
								    		</#if>
				                        </select>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">手机号码</span>
                                        <input type="text" class="form-control" id="mobile" name="mobile"
                                               value="${mobile!''}" placeholder="输入手机号">
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">短信内容</span>
                                        <input type="text" class="form-control" id="content" name="content"
                                               value="${content!''}" placeholder="输入内容">
                                    </div>
                                </div>
                            </div>
                            <div class="row" style="margin-top:5px">
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">分包状态</span>
                                        <select class="form-control" name="processStatus" id="processStatus">
                                            <option value="-1">--选择分包状态--</option>
                                            <option value="0" <#if processStatus == 0>selected</#if>>正在分包</option>
                                            <option value="1" <#if processStatus == 1>selected</#if>>分包完成，待发送</option>
                                            <option value="2" <#if processStatus == 2>selected</#if>>分包异常，待处理</option>
                                            <option value="3" <#if processStatus == 3>selected</#if>>分包失败，终止</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">审核状态</span>
                                        <select name="approveStatus" id="approveStatus" class="form-control">
                                            <option value="-1">--选择审核状态--</option>
                                            <option value="0" <#if approveStatus == 0>selected</#if>>待审核</option>
                                            <option value="1" <#if approveStatus == 1>selected</#if>>自动通过</option>
                                            <option value="2" <#if approveStatus == 2>selected</#if>>手动通过</option>
                                        </select>
                                    </div>
                                </div>
                                <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">开始时间</span>
                                        <input type="text" class="form-control" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="startDate"
                                               readonly style="background: #fff" name="startDate" value="${startDate!''}"
                                               placeholder="选择开始时间">
                                    </div>
                                </div>
                                 <div class="col-md-3">
                                    <div class="input-group">
                                        <span class="input-group-addon">结束时间</span>
                                        <input type="text" class="form-control" onClick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})" id="endDate"
                                               readonly style="background: #fff" name="endDate" value="${endDate!''}"
                                               placeholder="选择结束时间">
                                    </div>
                                </div>
                            </div>
                            <div class="row" style="margin-top:5px">
                                <div class="col-md-2">
                                    <a class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</a>
                                    <a class="btn btn-default" onclick="commonClearForm();">重&nbsp;&nbsp;&nbsp;置</a>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>

                <div class="panel">
                    <div class="panel-heading">
                        <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
                            <a href="javascript:void(0);" class="btn btn-success" onclick="batchPass();">审核通过</a>
                            &nbsp;
                            <a href="javascript:void(0);" class="btn btn-mint" onclick="batchPassWithSameContent();">同内容批放</a>
                            &nbsp;
                            <a href="javascript:void(0);" class="btn btn-danger" onclick="batchRefuse();">驳回</a>
                            &nbsp;
                            <a href="javascript:void(0);" class="btn btn-danger" onclick="batchRefuseWithSameContent();">同内容驳回</a>
                            &nbsp;
                            <a href="javascript:void(0);" class="btn btn-warning" onclick="openSwitchPassage();">切换通道</a>
                            &nbsp;
                            <a href="javascript:void(0);" class="btn btn-info" onclick="batchRepeatTask();">重新分包</a>
                            &nbsp;
                            <a href="javascript:void(0);" class="btn btn-default" onclick="batchEditContent();">修改内容</a>
                        </div>
                        <h3 class="panel-title">
                            <span>待处理短信任务</span>
                        </h3>
                    </div>
                    <div class="panel-body">
                        <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0"
                               width="100%">
                            <thead>
                            <tr onclick="selectAllRow();">
                                <th><input type="checkbox" id="selectAll"></th>
                                <th>SID</th>
                                <#--<th>客户名</th>-->
                                <th>模式</th>
                                <th>计</th>
                                <th>返</th>
                                <th>客户名</th>
                                <th>手机号</th>
                                <th>错号</th>
                                <th>重号</th>
                                <th>分包状态</th>
                                <th>来源</th>
                                <th>提交时间</th>
                                <th>分包时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list page.list as pl>
                            <tr onclick="choose(this);">
                                <td rowspan="2"
                                    style="background: #fff;text-align: center;">
                                ${(page.currentPage - 1) * page.pageSize + (pl_index+1)}
                                    <input type="checkbox" name="checkItem" sid="${pl.sid}" id="item_${pl.id}" value="${pl.id}">
                                </td>
                                <td>${(pl.sid)!''}</td>
                                <td>
                                	<button type="button" class="btn btn-info btn-xs">
                                		<#if pl.submitType == 0>
                                       	 批
	                                    <#elseif pl.submitType == 1>
	                                        	点
	                                    <#elseif pl.submitType == 2>
	                                        	模
	                                    <#else>
	                                                                                 批
	                                    </#if>
                                	</button>
                                </td>
                                <td>
                                	<button type="button" class="btn btn-primary btn-xs">${(pl.fee)!'0'}</button>
                                </td>
                                <td>
                                	<button type="button" class="btn btn-success btn-xs">${(pl.returnFee)!'0'}</button>
                                </td>
                                <#--<td>${(pl.userProfile.fullName)!''}</td>-->
                                <td>${(pl.userModel.name)!''}</td>
                                <td>
                                    <#if pl.mobiles?? && pl.mobiles?size gt 1>
                                    ${pl.firstMobile!''}...
                                    	<#--
                                        <button type="button" onclick="showAllMobile('${pl.mobile!''}');"
                                                class="btn btn-primary btn-xs">${pl.mobiles?size}</button>
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
                                    </#if>
                                </td>
                                <td>

                                    <a href="javascript:void(0);" data-placement="top" <#--data-html="true"--> class="btn btn-default btn-xs" data-toggle="popover" title="分包信息"
                                       data-content="${pl.remark!''}">
                                    <#if pl.processStatus == 0>
                                        	正在分包
                                    <#elseif pl.processStatus == 1>
                                        	分包完成，待发送
                                    <#elseif pl.processStatus == 2>
                                        	分包异常，待处理
                                    <#elseif pl.processStatus == 3>
                                        	分包失败，终止
                                    <#else>
                                        	未知
                                    </#if>
                                    </a>
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
                                    <a href="${BASE_PATH}/sms/record/child_task?sid=${pl.sid}"
                                       class="btn btn-info btn-xs">子任务</a>
                                     
                                    <#if pl.submitType != 1 && pl.submitType != 2>
                                    	<a href="${BASE_PATH }/sms/message_template/create?sid=${pl.sid}" class="btn btn-primary btn-xs"><#if pl.isTemplateError()>模板报备<#else>模板叠加</#if></a>
	                                    <#if pl.isWordError()>
	                                    	<#if pl.messageTemplateId?? && pl.messageTemplateId gt 0>
			                                    <a  href="${BASE_PATH }/sms/message_template/edit?id=${pl.messageTemplateId}&sid=${pl.sid}"
			                                       class="btn btn-default btn-xs">敏感词导白</a>
			                                <#else>
			                                	<a href="${BASE_PATH }/sms/message_template/create?sid=${pl.sid}" class="btn btn-default btn-xs">敏感词导白</a>
			                                </#if>
	                                    </#if>
                                    </#if>
                                   <#--
                                    &nbsp;
                                    <a href="javascript:void(0);" onclick="repeatTask(${pl.id});"
                                       class="btn btn-info btn-xs">重新分包</a>
                                    &nbsp;
                                    <a href="javascript:void(0);" onclick="mainTaskForcePass('${pl.sid}');"
                                       class="btn btn-danger btn-xs">强制通过</a>
                                    &nbsp;
                                    <a href="javascript:void(0);" onclick="mainTaskForcePass('${pl.sid}');"
                                       class="btn btn-danger btn-xs">驳回</a>
                                    &nbsp;
                                    <a href="javascript:void(0);" onclick="editContent(${pl.sid},this);"
                                       class="btn btn-warning btn-xs">修改内容</a>
                                       -->
                                    <#--&nbsp;-->
                                    <#--<a href="${BASE_PATH}/sms/record/send_record_list?sid=${pl.sid}" class="btn btn-success btn-xs">发送记录</a>-->
                                </td>
                            </tr>
                            <tr>
                                <td colspan="13" align="right">
                                	<input type="hidden" id="${pl.sid}_content" value="${pl.finalContent!''}" />
                                	<#if pl.forbiddenWordLabels??>
                                	<#list pl.forbiddenWordLabels as forbiddenWordLabel>
                                		<span class="label <#if forbiddenWordLabel.word?index_of(".*") == -1>label-warning<#else>label-danger</#if>" style="float:left;margin-right:3px;" data-toggle="popwordover" title="${forbiddenWordLabel.label!''}标签敏感词" data-content="${forbiddenWordLabel.word!''}">${forbiddenWordLabel.label!''}</span>&nbsp;
                                	</#list>
	                                	<span style="word-break:break-all;">
	                                	<#assign content="${pl.finalContent!''}">
	                                	<#list pl.forbiddenWords?split(",") as word>
	                                		<#if word?? && word?contains(".*")>
	                                			<#list word?split(".*") as wildcardWord>
	                                				<#if wildcardWord?? && wildcardWord != "">
	                                					<#assign content= content?replace(wildcardWord, "<span style='color:#FF0000;border-bottom:1px solid #FF0000;'>${(wildcardWord)!}</span>")>
	                                				</#if>
	                                			</#list>
	                                		<#else>
	                                			<#assign content= content?replace(word, "<span style='color:#E9967A'>${(word)!}</span>")>
	                                		</#if>
	                                		
	                                    </#list>
	                                    ${content!''}
	                                    </span>
                                	<#else>
	                                	<span style="word-break:break-all;">
	                                       ${pl.finalContent!''}
	                                    </span>
                                	</#if>
                                    
                                    <span style="color:#808080">
                                    [字数：${pl.finalContent?length}]
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

        <div class="modal fade" id="contentModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">修改内容</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                         id="myModelBody">
                        <textarea id="edit_content" class="form-control" rows="6"></textarea>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success" onclick="saveContent();">保存</button>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="modal fade" id="contentPassModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">短信内容</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000" id="myModelBody">
                        <textarea id="sms_content" class="form-control" rows="6"></textarea>
                    </div>
                    <div class="modal-footer">
                    	<input type="checkbox" id="content_like"><span onclick="chooseLike();">内容是否模糊匹配</span>&nbsp;&nbsp;
                        <button type="button" class="btn btn-success" onclick="sameContentPass();">确定</button>
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>

        <div class="modal fade" id="contentRefuseModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">短信内容</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000" id="myModelBody">
                        <textarea id="sms_refuse_content" class="form-control" rows="6"></textarea>
                    </div>
                    <div class="modal-footer">
                        <input type="checkbox" id="content_refuse_like"><span onclick="chooseRefuseLike();">内容是否模糊匹配</span>&nbsp;&nbsp;
                        <button type="button" class="btn btn-success" onclick="sameContentRefuse();">确定</button>
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
                </div><!-- /.modal-content -->
            </div><!-- /.modal-dialog -->
        </div>

        <div class="modal fade" id="passageModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">切换通道</h4>
                    </div>
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                         id="myModelBody">
                        <select class="form-control selectpicker show-tick" id="switchPassageId" data-live-search="true">
                        	<#if passageList??>
		    					<#list passageList?sort_by("name") as p>
		    						<#if p.status?? && p.status == 0>
		    						<option value="${p.id!''}">${p.name!''}</option>
		    						</#if>
		    					</#list>
				    		</#if>
                        </select>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success" onclick="batchSwitchPassage();">保存</button>
                        &nbsp;&nbsp;&nbsp;&nbsp;
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

    $(function(){

        $('#selectAll').change(function(){
            var $this = $(this);
            if($this.prop('checked')){
                $('input[name=checkItem]').prop('checked',true);
            }else{
                $('input[name=checkItem]').prop('checked',false);
            }
        });
        
        $('a[data-toggle=popover]').mouseover(function () {
            var $this = $(this);
            if($this.attr('data-content') != ''){
                $this.popover('show');
            }
        });

        $('a[data-toggle=popover]').mouseout(function () {
            var $this = $(this);
            $this.popover('hide')
        });
        
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
    
    function selectAllRow() {
    	if($("#selectAll").prop('checked')){
        	$("#selectAll").prop('checked', false);
            $('input[name=checkItem]').prop('checked',false);
        }else{
        	$("#selectAll").prop('checked', true);
            $('input[name=checkItem]').prop('checked',true);
        }
    }

    function getSelectIds(){
        var selects = $('input[name=checkItem]:checked');
        if(selects.length <= 0){
            return '';
        }
        var ids = '';
        for(var i = 0;i<selects.length;i++){
            var selectId = $(selects[i]).val();
            ids += selectId + ',';
        }
        ids = ids.substring(0,ids.length - 1);
        return ids;
    }
    
   <#-- 获取选中SID集合信息 -->
    function getSelectSids(){
        var selects = $('input[name=checkItem]:checked');
        if(selects.length <= 0){
            return '';
        }
        var sids = '';
        for(var i = 0;i<selects.length;i++){
            sids += $(selects[i]).attr("sid") + ',';
        }
        return sids.substring(0,sids.length - 1);
    }

    function batchPass(){
        var ids = getSelectIds();
        if(ids == ''){
            Boss.alert('请选择需要处理的任务！');
            return;
        }
        Boss.confirm('确定要审核通过吗？',function(){
            $.ajax({
                url:'${BASE_PATH}/sms/record/batchPass',
                data:{ids:ids},
                dataType:'json',
                type:'post',
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        location.reload();
                    });

                },error:function(data){
                    Boss.alert('批量通过失败！');
                }
            });
        });
    }
    
    function batchPassWithSameContent() {
    	var ids = getSelectIds();
        if(ids == ''){
            Boss.alert('请选择需要处理的任务！');
            return;
        }
        var realId = ids.split(',')[0];
        
        var obj = $('#item_'+realId);
        var sid = obj.attr('sid');
        $('#sms_content').val($("#"+sid+"_content").val());
        $('#contentPassModal').modal('show');
    }

    // 同内容批量驳回
    function batchRefuseWithSameContent() {
        var ids = getSelectIds();
        if(ids == ''){
            Boss.alert('请选择需要处理的任务！');
            return;
        }
        var realId = ids.split(',')[0];

        var obj = $('#item_'+realId);
        var sid = obj.attr('sid');
        $('#sms_refuse_content').val($("#"+sid+"_content").val());
        $('#contentRefuseModal').modal('show');
    }

    function batchRefuse(){
        var ids = getSelectIds();
        if(ids == ''){
            Boss.alert('请选择需要处理的任务！');
            return;
        }

        Boss.confirm('确定要驳回吗？',function(){
            $.ajax({
                url:'${BASE_PATH}/sms/record/batchRefuse',
                data:{ids:ids},
                dataType:'json',
                type:'post',
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            location.reload();
                        }
                    });

                },error:function(data){
                    Boss.alert('批量驳回失败！');
                }
            });
        });
    }

    function openSwitchPassage(){
        var ids = getSelectIds();
        if(ids == ''){
            Boss.alert('请选择需要处理的任务！');
            return;
        }
        $('#passageModal').modal('show');
    }
    
    function choose(obj) {
    	var chk = $(obj).find(':checkbox').eq(0);
    	if(chk.is(":checked")) {
    		chk.attr("checked", false);
    	} else {
    		chk.attr("checked", true);
    	}
    }
    
    function chooseLike() {
        var chk = $("#content_like");
        if(chk.is(":checked")) {
            chk.attr("checked", false);
        } else {
            chk.attr("checked", true);
        }
    }

    function chooseRefuseLike() {
        var chk = $("#content_refuse_like");
        if(chk.is(":checked")) {
            chk.attr("checked", false);
        } else {
            chk.attr("checked", true);
        }
    }

    function batchSwitchPassage(){
        var ids = getSelectIds();
        if(ids == ''){
            Boss.alert('请选择需要处理的任务！');
            return;
        }

        var switchPassageId = $('#switchPassageId').val();
        $.ajax({
            url:'${BASE_PATH}/sms/record/batchSwitchPassage',
            data:{ids:ids,switchPassageId:switchPassageId},
            dataType:'json',
            type:'post',
            success:function(data){
                Boss.alertToCallback(data.message,function(){
                    if(data.result){
                        location.reload();
                    }
                });

            },error:function(data){
                Boss.alert('批量切换通道失败！');
            }
        });
    }

    function repeatTask(id){
        Boss.confirm('确定要重新分包吗？',function(){
            $.ajax({
                url:'${BASE_PATH}/sms/record/repeatTask',
                data:{id:id},
                dataType:'json',
                type:'post',
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            location.reload();
                        }
                    });
                },error:function(data){
                    Boss.alert('重新分包失败！');
                }
            });
        });
    }

    function jumpPage(p) {
        $('#pn').val(p);
        $('#myform').attr('action', '${BASE_PATH}/sms/record/under_way_list');
        $('#myform').submit();
    }

    function showAllMobile(mobile) {
        $('#all_mobile').val(mobile);
        $('#myModal').modal('show');
    }

    function closeModal() {
        $('#myModal').modal('hide');
        $('#contentModal').modal('hide');
        $('#userModal').modal('hide');
        $('#passageModal').modal('hide');
        $('#contentPassModal').modal('hide');
        $('#contentRefuseModal').modal('hide');
    }

    function editContent(sid) {
        $('#edit_content').val($("#"+sid+"_content").val());
        $('#contentModal').modal('show');
    }

    function mainTaskForcePass(sid){
        Boss.confirm('强制通过会将该任务下的所有子任务全部执行，确定要执行该操作吗？',function(){
            $.ajax({
                url:'${BASE_PATH}/sms/record/main_task_force_pass',
                data:{'sid':sid},
                type:'post',
                dataType:'json',
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            location.reload();
                        }
                    });

                },error:function(data){
                    Boss.alert('主任务强制通过失败！');
                }
            })
        });
    }

    function saveContent() {
        var sids = getSelectSids();
        if(sids == ''){
            Boss.alert('请选择需要修改内容的任务！');
            return;
        }
        
        var finalContent = $('#edit_content').val();
        if ($.trim(finalContent) == '') {
            Boss.alert('请输入短信内容！');
            return;
        }
        $.ajax({
            url: '${BASE_PATH}/sms/record/batchUpdateContent',
            data: {'sidArray': sids, 'content': finalContent},
            dataType: 'json',
            type: 'post',
            success: function (data) {
                Boss.alertToCallback(data.message,function(){
                    if (data.result) {
                        location.reload();
                    }
                });

            }, error: function (data) {
                Boss.alert('保存失败!');
            }
        });
    }
    
    /**同短信内容审批通过**/    
    function sameContentPass() {
        var finalContent = $('#sms_content').val();
        if ($.trim(finalContent) == '') {
            Boss.alert('请输入短信内容！');
            return;
        }
        
        var likePattern = 0;
        if($('#content_like').is(':checked')) {
		    likePattern = 1;
		}
		
        $.ajax({
            url: '${BASE_PATH}/sms/record/sameContentPass',
            data: {'like_pattern': likePattern, 'content': finalContent},
            dataType: 'json',
            type: 'post',
            success: function (data) {
                Boss.alertToCallback(data.message,function(){
                    if (data.result) {
                        location.reload();
                    }
                });

            }, error: function (data) {
                Boss.alert('批放失败!');
            }
        });
    }


    /**同短信内容驳回**/
    function sameContentRefuse() {
        var finalContent = $('#sms_refuse_content').val();
        if ($.trim(finalContent) == '') {
            Boss.alert('请输入短信内容！');
            return;
        }

        var likePattern = 0;
        if($('#content_refuse_like').is(':checked')) {
            likePattern = 1;
        }

        $.ajax({
            url: '${BASE_PATH}/sms/record/sameContentReject',
            data: {'like_pattern': likePattern, 'content': finalContent},
            dataType: 'json',
            type: 'post',
            success: function (data) {
                Boss.alertToCallback(data.message,function(){
                    if (data.result) {
                        location.reload();
                    }
                });

            }, error: function (data) {
                Boss.alert('批放失败!');
            }
        });
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
                Boss.alert('请求用户列表异常！');
            }
        });
    }

    function batchEditContent(){
        var ids = getSelectIds();
        if(ids == ''){
            Boss.alert('请选择主任务！');
            return;
        }
        var idArray = ids.split(',');
        var realId = idArray[0];
        var obj = $('#item_'+realId);
        var sid = obj.attr('sid');
        editContent(sid);
    }

    function batchRepeatTask(){
        var ids = getSelectIds();
        if(ids == ''){
            Boss.alert('请选择主任务！');
            return;
        }
//        var idArray = ids.split(',');
//        var realId = idArray[0];
//        repeatTask(realId);
        Boss.confirm('确定要重新分包吗？',function(){
            $.ajax({
                url:'${BASE_PATH}/sms/record/batchRepeatTask',
                data:{ids:ids},
                dataType:'json',
                type:'post',
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            location.reload();
                        }
                    });
                },error:function(data){
                    Boss.alert('重新分包失败！');
                }
            });
        });
    }
</script>
</html>