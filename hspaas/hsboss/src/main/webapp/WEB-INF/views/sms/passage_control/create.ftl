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
								<li class="active">通道轮训控制添加</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                            <!-- Panel heading -->
                            <div class="panel-heading">
                                <h3 class="panel-title">通道轮训控制添加</h3>
                            </div>
                            <!-- Panel body -->
                                <form id="myform" class="form-horizontal">
                                  <input type="hidden" name="smsPassageControl.parameterId" id="parameterIdById">
                                  <div class="panel-body">
                                  	<div class="form-group">
				    			  		<label class="col-xs-1 control-label">通道名称</label>
							    		<div class="col-xs-4">
							    			<select id="passageId" name="smsPassageControl.passageId" onchange="findPassageParameter();" class="form-control">
							    				<#if passageList??>
									    			<#list passageList as p>
									    				<option value="${p.id!''}">${p.name!''}</option>
									    			</#list>
									    		</#if>
							    			</select>
							    		</div>
							    	</div>
                                  	<div class="form-group">
				    			  		<label class="col-xs-1 control-label">状态</label>
							    		<div class="col-xs-4">
							    			<select id="status" name="smsPassageControl.status" class="form-control">
							    				<#if passageStatus??>
									    			<#list passageStatus as a>
									    				<option value="${a.value!''}">${a.title!''}</option>
									    			</#list>
									    		</#if>
							    			</select>
							    		</div>
							    	</div>
							    	<div class="form-group">
                                        <label class="col-xs-1 control-label">轮训表达式</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[required,maxSize[100]]" name="smsPassageControl.cron" id="cron" placeholder="请输入轮训表达式">
                                        	<span>案例：五秒中执行一次  0/5 * * * * ?</span>
                                        </div>
			                        </div>
                                    <div class="form-group">
                                        <label class="col-xs-1 control-label">通道参数</label>
                                        <div class="col-xs-5">
                                             <textarea class="form-control validate[required,maxSize[1000]]" name="smsPassageControl.parameterId" readonly="true" id="parameterId" rows="8"></textarea>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <div class="col-xs-9 col-xs-offset-3">
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
		});
		
		function formSubmit(){
			var allCheck = $('#myform').validationEngine('validate');
			if(!allCheck){
				return;
			}
			$.ajax({
	  			url:'${BASE_PATH}/sms/passage_control/add',
	  			dataType:'json',
	  			data:$('#myform').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data==1){
                        Boss.alertToCallback('提交成功！',function(){
                            location.href = "${BASE_PATH}/sms/passage_control"
						});
	  				}else if(data==2){
                        Boss.alert('提交失败！通道已存在！');
	  				}else if(data==3){
                        Boss.alert('提交失败！通道已存在！目前状态为（停用）请开启之前通道状态！');
	  				}else{
                        Boss.alert('提交失败！系统异常!请稍后重试！');
	  				}
	  			},error:function(data){
                    Boss.alert('系统异常!请稍后重试！');
	  			}
	  		});
		}
		
		function findPassageParameter(){
			$.ajax({
	  			url:'${BASE_PATH}/sms/passage_control/findPassageParameter',
	  			dataType:'json',
	  			data:{
	  				passageId :$("#passageId").val()
	  			},
	  			type:'post',
	  			success:function(data){
	  				var flag=true;
	  				if(data !=null){
	  					$.each(data, function(index, d) {
	  						if(d.callType=="6"){
	  							$("#parameterIdById").val(d.id);
	  							$("#parameterId").html(d.paramsDefinition);
	  							flag=false;
	  						}
	  					});
	  					if(flag){
	  						$("#parameterIdById").html("");
	  						$("#parameterId").html("");
                            Boss.alert('当前通道未检索到相关参数！不允许添加！');
	  					}
	  				}
	  			},error:function(data){
                    Boss.alert('系统异常!请稍后重试！');
	  			}
	  		});
		}
		
		$(function(){
			findPassageParameter();
		});
		
	</script>
</html>