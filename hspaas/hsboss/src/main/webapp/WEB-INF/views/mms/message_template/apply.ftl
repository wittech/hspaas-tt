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
                        <li><a href="#"> 模板管理 </a></li>
                        <li class="active">通道模板报备</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
						
				<div class="panel">
					<div class="panel-body">
						<form id="myform" method="post">
						    <div class="row" style="margin-top:5px">
						    	<div class="col-md-3">
						    		<div class="input-group">
						    			<span class="input-group-addon">通道名称</span>
						    			<input type="hidden" name="mmsPassageMessageTemplate.templateId" value="${(messageTemplate.id)!}" />
						    			<select class="form-control selectpicker show-tick" id="passageId" name="mmsPassageMessageTemplate.passageId" data-live-search="true">
			                        		<option value="">请选择通道名称</option>
				                        	<#if passageList??>
						    					<#list passageList?sort_by("name") as p>
						    						<#if p.status?? && p.status == 0>
						    						<option value="${p.id!''}">${p.name!''}</option>
						    						</#if>
						    					</#list>
								    		</#if>
				                        </select>
						    		</div>
					    		</div>
						    	<div class="col-md-2">
						    		<a class="btn btn-primary" onclick="formSubmit();">报&nbsp;&nbsp;&nbsp;备</a>
						    	</div>
						    </div>
					    </form>
					</div>
				</div>

				<div class="panel">
                <div class="panel-heading">
                    <h3 class="panel-title">
                    <span>已提交/报备完成通道模板列表</span>
                    </h3>
                   
                </div>
                <div class="panel-body">
                    <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                        <thead>
                            <tr>
                                <th>通道</th>
	                            <th>模版ID</th>
	                            <th>状态</th>
	                            <th>创建时间</th>
	                            <th>修改时间</th>
	                            <th>备注</th>
	                            <th>操作</th>
                            </tr>
                        </thead>
                        <tbody>
                        	<#if templates??>
                            <#list templates as t>
                                <tr>
                                    <td>${(t.passageName)!}</td>
                                    <td>${(t.passageModelId)!}
                                    	<input type="hidden" id="passageModelId${(t.id)!}" value="${(t.passageModelId)!}">
                                    	<input type="hidden" id="id${(t.id)!}" value="${(t.id)!}">
                                    </td>
                                    <td>${(t.statusText)!}
                                    	<input type="hidden" id="status${(t.id)!}" value="${(t.status)!}">
                                    </td>
                                    <td><#if t.createTime??>${t.createTime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
                                    <td><#if t.updateTime??>${t.updateTime?string('yyyy-MM-dd HH:mm:ss')}</#if></td>
                                    <td>${(t.remark)!}
                                    	<input type="hidden" id="remark${(t.id)!}" value="${(t.remark)!}">
                                    </td>
                                    <td>
                                        <a href="javascript:edit(${t.id});" class="btn btn-info btn-xs">&nbsp;修改</a>
                                    </td>
                                </tr>
                            </#list>
                        	</#if>
                        </tbody>
                    </table>
                </div>
            </div>
		</div>
		
		
		<div class="modal fade" id="myModal">
            <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" onclick="closeModal();"><span
                                aria-hidden="true">&times;</span></button>
                        <h4 class="modal-title">修改</h4>
                    </div>
                    <form id="editform" method="post">
                    <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000" id="myModelBody">
                    	<input type="hidden" name="mmsPassageMessageTemplate.id" id="id" >
                    	<input type="text" class="form-control validate[required]" name="mmsPassageMessageTemplate.passageModelId" id="passageModelId"
			                                           placeholder="请输入模板ID">
			                                           
			            <br/>
			            <input type="text" class="form-control" name="mmsPassageMessageTemplate.remark" id="remark" placeholder="请输入备注">
			            <br/>
			            <select class="form-control" id="status" name="mmsPassageMessageTemplate.status">
                            <#if passageTemplateStatus??>
				    		<#list passageTemplateStatus as a>
				    			<option value="${a.getValue()!''}">${a.getTitle()!''}</option>
				    		</#list>
				    		</#if>
                        </select>
                    </div>
                    </form>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-success" onclick="update();">保存</button>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                    </div>
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
    $(function () {
        $('#myform').validationEngine('attach', {promptPosition: "topRight"});
    });

    function formSubmit() {
    	if($("#passageId").val() == "") {
    		Boss.alert('请选择通道名称！')
    		return;
    	}
    
        $.ajax({
            url: '${BASE_PATH}/mms/message_template/passage_model',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
                if (data.result) {
                	Boss.alertToCallback('通道彩信模板报备已提交！',function(){
                        location.reload();
                    });
                
                } else {
                	Boss.alert('通道彩信模板报备失败！');
                }
            }, error: function (data) {
                Boss.alert('系统异常!请稍后重试！');
            }
        });
    }
    
    function edit(id){
    	$('#id').val($("#id"+id).val());
        $('#passageModelId').val($("#passageModelId"+id).val());
        $('#remark').val($("#remark"+id).val());
        $('#status').val($("#status"+id).val());
        $('#myModal').modal('show');
    }
    
    function closeModal() {
        $('#myModal').modal('hide');
    }
    
    function update() {
        var allCheck = $('#editform').validationEngine('validate');
        if (!allCheck) {
            return;
        }
        
        $.ajax({
            url: '${BASE_PATH}/mms/message_template/update_passage_model',
            data: $('#editform').serialize(),
            dataType: 'json',
            type: 'post',
            success: function (data) {
                Boss.alertToCallback(data.message,function(){
                    if (data.result) {
                        location.reload();
                    }
                });

            }, error: function (data) {
                Boss.alert('修改失败!');
            }
        });
    }
    
</script>
</html>