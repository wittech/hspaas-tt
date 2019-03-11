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
								<li class="active">用户运行中通道编辑</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                            <!-- Panel heading -->
                            <div class="panel-heading">
                                <h3 class="panel-title">用户运行中通道编辑</h3>
                            </div>
                            <!-- Panel body -->
                               <form id="myform" class="form-horizontal">
                                  <input type="hidden" name="smsPassageAccess.id" value="${smsPassageAccess.id!''}">
                                  <div class="panel-body">
	                                  <div class="form-group">
					    			  		<label class="col-xs-2 control-label">通道名称</label>
								    		<div class="col-xs-4">
								    			<select id="passageId" name="smsPassageAccess.passageId"  onchange="findPassageParameter();" class="form-control">
								    				<#if passageList??>
										    			<#list passageList as p>
										    				<option value="${p.id!''}" <#if p.id==smsPassageAccess.passageId>selected</#if>>${p.name!''}</option>
										    			</#list>
									    			</#if>
								    			</select>
								    		</div>
								    	</div>
								    	<div class="form-group">
					    			  		<label class="col-xs-2 control-label">通道状态</label>
								    		<div class="col-xs-4">
								    			<#if smsPassageAccess.status == 0>
		                                            <span class="label label-success">使用中</span>
		                                        <#else>
		                                        	<span class="label label-danger">停用</span>
		                                        </#if>
								    		</div>
								    	</div>
							    		<div class="form-group">
	                                        <label class="col-xs-2 control-label">通道地址</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.url" value="${smsPassageAccess.url!''}" id="url" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">结果格式</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.resultFormat" value="${smsPassageAccess.resultFormat!''}" id="resultFormat" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">成功代码</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.successCode" value="${smsPassageAccess.successCode!''}" id="successCode" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">定位解析</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.position" value="${smsPassageAccess.position!''}" id="position" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">路由类型</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.routeType" value="${smsPassageAccess.routeTypeText!''}" id="routeType" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">省份名称</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.provinceName" value="${smsPassageAccess.provinceName!''}" id="provinceName" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">运营商</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.cmcpName" value="${smsPassageAccess.cmcpName!''}" id="cmcpName" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">号码分包数</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.mobileSize" value="${smsPassageAccess.mobileSize!''}" id="mobileSize" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">流速</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.packetsSize" value="${smsPassageAccess.packetsSize!''}" id="packetsSize" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">最大连接数</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.connectionSize" value="${smsPassageAccess.connectionSize!''}" id="connectionSize" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">请求超时时间（毫秒）</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.readTimeout" value="${smsPassageAccess.readTimeout!''}" id="connectionSize" readonly="true" />
	                                        </div>
			                        	</div>
			                        	<div class="form-group">
	                                        <label class="col-xs-2 control-label">接入号码</label>
	                                        <div class="col-xs-4">
	                                            <input type="text" class="form-control validate[required]" name="smsPassageAccess.accessCode" value="${smsPassageAccess.accessCode!''}" id="accessCode" readonly="true" />
	                                        </div>
			                        	</div>
	                                    <div class="form-group">
	                                        <label class="col-xs-2 control-label">通道参数</label>
	                                        <div class="col-xs-5">
	                                             <textarea class="form-control validate[required,maxSize[1000]]" readonly="true" id="paramsDefinition" name="smsPassageAccess.paramsDefinition" rows="8">${smsPassageAccess.paramsDefinition!''}</textarea>
	                                        </div>
	                                    </div>
	                                    <div class="form-group">
	                                        <div class="col-xs-9 col-xs-offset-3">
	                                            <#--<button type="button" onclick="formSubmit();" class="btn btn-primary" name="buttonSubmit">提交</button>-->
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
	  			url:'${BASE_PATH}/mms/passage_access/update',
	  			dataType:'json',
	  			data:$('#myform').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data){
                        Boss.alert('提交成功！',function(){
                            location.href = "${BASE_PATH}/mms/passage_access"
						});
	  				}else{
                        Boss.alert('提交失败！');
	  				}
	  			},error:function(data){
                    Boss.alert('系统异常!请稍后重试！');
	  			}
	  		});
		}
		
		function findPassageParameter(){
			$.ajax({
	  			url:'${BASE_PATH}/mms/passage_control/findPassageParameter',
	  			dataType:'json',
	  			data:{
	  				passageId :$("#passageId").val()
	  			},
	  			type:'post',
	  			success:function(data){
	  				var flag=true;
	  				if(data !=null){
	  					$.each(data, function(index, d) {
	  						if(d.callType=="1"){
	  							$("#url").val(d.url);
	  							$("#paramsDefinition").html(d.paramsDefinition);
	  							$("#params").html(d.params);
	  							$("#resultFormat").val(d.resultFormat);
	  							$("#successCode").val(d.successCode);
	  							$("#position").val(d.position);
	  							flag=false;
	  						}
	  					});
	  				}
	  			},error:function(data){
                    Boss.alert('系统异常!请稍后重试！');
	  			}
	  		});
		}
		
		$(function(){
			findPassageParameter();
		})
	</script>
</html>