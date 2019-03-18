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
								<li> <a href="#"> 基础信息 </a> </li>
								<li class="active">通道模板管理</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                            <!-- Panel heading -->
                            <div class="panel-heading">
                                <h3 class="panel-title">新增通道模板</h3>
                            </div>
                            <!-- Panel body -->
                            <form id="myform" class="form-horizontal">
                                  <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">模板名称</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[required,maxSize[100]]" name="template.name" id="name" placeholder="请输入模板名称">
                                        </div>
                                        <label class="col-xs-1 control-label">模板协议</label>
                                        <div class="col-xs-4">
                                            <select class="form-control" id="protocol" name="template.protocol">
                                                <#list protocolTypes as pl>
                                                    <option value="${pl.name()}">${pl.name()}</option>
                                                </#list>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">模板类型</label>
                                        <div class="col-xs-4">
                                        	<div class="input-group">
                                        		<select class="form-control" id="passageType" name="template.passageType">
                                        	   	    <#list passageTempateNames as pn>
										      		<option value="${(pn.value)!}">${(pn.getName())!}</option>
										            </#list>
										        </select>
										    </div>
                                        </div>
                                        <label class="col-xs-1 control-label">模板规则</label>
                                        <div class="col-xs-4">
                                        	<div class="input-group">
										      <select class="form-control" id="ruleType">
										      		<#list passageTemplateDetailTypes as t>
										      		<option value="${(t.value)!}">${(t.getName())!}</option>
										            </#list>
										      </select>
										      <span class="input-group-btn">
										        <button class="btn btn-default" type="button" onclick="setRuleDetail();">设置</button>
										      </span>
										    </div>
                                        </div>
                                    </div>
                                    
                                    <div id="setedRuleDiv" class="form-group" style="display:none">
                                    	<label class="col-xs-2 control-label">已设置模板规则</label>
                                    	<div class="col-xs-10">
                                    		
                                    	</div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <div class="col-xs-9 col-xs-offset-5">
                                            <a onclick="formSubmit();" class="btn btn-primary" name="buttonSubmit">提交</a>
                                        </div>
                                    </div>
                                </div>
                                <input type="hidden" name="templateDetails" id="templateDetails">
                                <div id="hiddenInputForm">
                                </div>
                             </form>
                        </div>
                    </div>
				</div>
				<#include "/WEB-INF/views/main/left.ftl">
			</div>
		</div>
		
		<div class="modal fade" id="myModal">
		  <div class="modal-dialog" style="width:auto;height:auto;min-width:890px">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" onclick="closeModal();"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title">设置模板详情</h4>
		      </div>
		      <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000" id="myModelBody">
		        	
		      </div>
		      <div class="modal-footer">
		        <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
		        &nbsp;&nbsp;&nbsp;&nbsp;
		        <button type="button" class="btn btn-success" onclick="addRule();">确定</button>
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
			$('#myform').validationEngine('attach',{promptPosition : "topRight"});
			
			$('.templateType').click(function(){
				$('#templateType').val($this.val());
			});
		});
		
		var ruleTypeTag = -1;

		function closeModal(){
            $('.ruleForms').hide();
			$('#myModal').modal('hide');
		}

		function checkRuleFormExists(){
			var rule_forms = $('.ruleForms');
            var ruleType = $('#ruleType').val();
			var form_id = 'detail_form_'+ruleType;
			for(var i = 0;i<rule_forms.length;i++){
				var form = $(rule_forms[i]);
				if(form.attr('id') == form_id){
					return true;
				}
			}
			return false;
		}

		function setRuleDetail(){
			var ruleType = $('#ruleType').val();
			if(checkRuleFormExists()){
				$('#detail_form_'+ruleType).show();
                $('#detail_form_'+ruleType).validationEngine('attach',{promptPosition : "topRight"});
                $('#myModal').modal('show');
				return;
			}
			ruleTypeTag = -1;
			$.ajax({
				url:'${BASE_PATH}/base/passage_template/ruleView',
				data:{ruleType:ruleType},
				dataType:'html',
				success:function(data){
					$('#myModelBody').append(data);
					$('#detail_form_'+ruleType).validationEngine('attach',{promptPosition : "topRight"});
					$('#myModal').modal('show');
				},error:function(data){
                    Boss.alert('获取模板规则详情失败！');
				}
			});
		}
		
		function changeRuleInputName(){
			var hasRules = $('.detailRuleType');
			var tagName = "rule_detail_";
			var templateDetails = "";
			var hiddenInputHtml = "";
			for(var i = 0;i<hasRules.length;i++){
				var callType = $(hasRules[i]).val();
				var ruleName = tagName+callType;
				templateDetails += ruleName + ',';
				hiddenInputHtml += getHiddenInput(ruleName+'.callType',callType);
				var url = $('#detail_url_'+callType).val();
				hiddenInputHtml += getHiddenInput(ruleName+'.url',url);
				var requestParams = $('#requestParam'+callType+' .form-group');
				var requestParamCount = requestParams.length;
				hiddenInputHtml += getHiddenInput('reqParam_'+ruleName,requestParamCount);
				for(var j = 0;j<requestParams.length;j++){
					var showName = $(requestParams[j]).find('input[name=showName]').eq(0).val();
					hiddenInputHtml += getHiddenInput('reqParam_'+ruleName+'_'+j+'.showName',showName);
					var requestName = $(requestParams[j]).find('input[name=requestName]').eq(0).val();
					hiddenInputHtml += getHiddenInput('reqParam_'+ruleName+'_'+j+'.requestName',requestName);
					var defaultValue = $(requestParams[j]).find('input[name=defaultValue]').eq(0).val();
					hiddenInputHtml += getHiddenInput('reqParam_'+ruleName+'_'+j+'.defaultValue',defaultValue);
				}
				
				var parseRule = $('#parseRule'+callType+' .form-group');
				var parseRuleCount = parseRule.length;
				hiddenInputHtml += getHiddenInput('parseRule_'+ruleName,parseRuleCount);
				for(var k = 0;k<parseRule.length;k++){
					var showName = $(parseRule[k]).find('input[name=showName]').eq(0).val();
					hiddenInputHtml += getHiddenInput('parseRule_'+ruleName+'_'+k+'.showName',showName);
					var parseName = $(parseRule[k]).find('input[name=parseName]').eq(0).val();
					hiddenInputHtml += getHiddenInput('parseRule_'+ruleName+'_'+k+'.parseName',parseName);
					var position = $(parseRule[k]).find('input[name=position]').eq(0).val();
					hiddenInputHtml += getHiddenInput('parseRule_'+ruleName+'_'+k+'.position',position);
				}
				
				var format_result = $('#detail_result_format_'+callType).val();
                format_result = format_result.replace(/"/g, "&quot;");
				hiddenInputHtml += getHiddenInput(ruleName+'.resultFormat',format_result);
				
				var detail_success = $('#detail_success_'+callType).val();
				hiddenInputHtml += getHiddenInput(ruleName+'.successCode',detail_success);
			}
			if(templateDetails == ""){
				return;
			}
			templateDetails = templateDetails.substring(0,templateDetails.length - 1);
			$('#templateDetails').val(templateDetails);
			$('#hiddenInputForm').html(hiddenInputHtml);
		}
		
		function getHiddenInput(name,value){
			var html = '<input type="text" style="display:none" name="'+name+'" value="'+value+'" />';
			return html;
		}
		
		function addRow(rowType){
			var ruleType = $('#ruleType').val();
			if(ruleTypeTag != -1){
				ruleType = ruleTypeTag;
			}
			var targetDiv = "requestParam"+ruleType;
			if(rowType == 2){
				targetDiv = "parseRule"+ruleType;
			}
			$.ajax({
				url:'${BASE_PATH}/base/passage_template/addRow',
				data:{rowType:rowType},
				dataType:'html',
				success:function(data){
					$('#'+targetDiv).append(data);
				},error:function(data){
                    Boss.alert('加行失败！');
				}
			})
		}
		
		function resetRuleTypeSelectOption(){
			var ruleCn = [];
			var ruleVal = [];
			<#list passageTemplateDetailTypes as t>
				ruleCn[${(t_index)!}] = "${(t.getName())!}";
				ruleVal[${(t_index)!}] = ${(t.getValue())!};
            </#list>
			
			var hasRules = $('.detailRuleType');
			var hasRuleTypes = "";
			for(var i = 0 ;i<hasRules.length;i++){
				var currentRule = $(hasRules[i]);
				var cr = parseInt(currentRule.val());
				ruleVal[cr - 1] = 0;
			}
			
			var newSeletOption= "";
			for(var i = 0;i < ruleVal.length;i++){
				if(ruleVal[i] <= 0){
					continue;
				}
				newSeletOption += '<option value="'+ruleVal[i]+'">'+ruleCn[i]+'</option>';
			}
			$('#ruleType').html(newSeletOption);
			
		}
		
		function removeRow(obj){
			$(obj).parent().parent().remove();
		}
		
		function addRule(){
			var ruleType = $('#ruleType').val();
			
			var allCheck = $('#detail_form_'+ruleType).validationEngine('validate');
			if(!allCheck){
				return;
			}
			
			if(ruleTypeTag != -1){
				ruleType = ruleTypeTag;
				$('#detail_form_'+ruleType).hide();
				$('#myModal').modal('hide');
				return;
			}
			var ruleHtml = $('#myModelBody').html();
			$('#ruleDetailDiv').append(ruleHtml);
			$('#detail_form_'+ruleType).hide();
			
			var ruleCn = [];
			<#list passageTemplateDetailTypes as t>
				ruleCn[${(t_index)!}] = "${(t.getName())!}";
            </#list>
			
			var showCn = ruleCn[parseInt(ruleType) - 1];
			var showTagHtml = '<div class="btn-group showRuleTag_'+ruleType+'">'+
                                  '<a class="btn btn-info btn-xs" onclick="showSelectedRule('+ruleType+');">'+showCn+'</a>'+
                                  '<a class="btn btn-default btn-xs" onclick="removeSelectedRule('+ruleType+');">删除</a>'+
                              '</div>';
			$('#setedRuleDiv').show();
			$('#setedRuleDiv .col-xs-10').append(showTagHtml);
			resetRuleTypeSelectOption();
			$('#myModal').modal('hide');
		}
		
		function showSelectedRule(ruleType){
			ruleTypeTag = ruleType;
			$('#detail_form_'+ruleType).show();
			$('#detail_form_'+ruleType).validationEngine('attach',{promptPosition : "topRight"});
			$('#myModal').modal('show');
		}
		
		function removeSelectedRule(ruleType){
			$('.showRuleTag_'+ruleType).remove();
			$('#detail_form_'+ruleType).remove();
			resetRuleTypeSelectOption();
			var hasRules = $('.detailRuleType');
			if(hasRules.length <= 0){
				$('#setedRuleDiv').hide();
			}
		}
		
		function formSubmit(){
			var allCheck = $('#myform').validationEngine('validate');
			if(!allCheck){
				return;
			}
			changeRuleInputName();
			$.ajax({
	  			url:'${BASE_PATH}/base/passage_template/create',
	  			dataType:'json',
	  			data:$('#myform').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('新增通道模板成功！',function(){
                            location.href = "${BASE_PATH}/base/passage_template"
						});

	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('新增通道模板失败!');
	  			}
	  		});
		}
	</script>
</html>