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
		<link href="${BASE_PATH}/resources/css/jquery.tagit.css" rel="stylesheet">
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
								<li> <a href="#"> 套餐管理 </a> </li>
								<li class="active">套餐管理</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                            <!-- Panel heading -->
                            <div class="panel-heading">
                                <h3 class="panel-title">修改套餐</h3>
                            </div>
                            <!-- Panel body -->
                                <form id="myform" class="form-horizontal">
                                	<input type="hidden" name="combo.id" value="${combo.id}" />
                                  <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">套餐名称</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[required,maxSize[100]]" name="combo.name" value="${combo.name}" id="name" placeholder="请输入套餐名称">
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">选择产品</label>
                                        <div class="col-xs-1">
                                           	<button type="button" onclick="selectProduct();" class="btn btn-info">选择产品</button>
                                        </div>
                                        <div class="col-xs-6">
                                         	<ul id="selectProductDiv" class="primary tagit ui-widget ui-widget-content ui-corner-all">
                                             </ul>
                                        </div>
                                        <input type="hidden" name="productIds" id="productIds" value=""/>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">原价</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[required,maxSize[100],custom[number]]" name="originalMoney" id="originalMoney" value="${combo.originalMoney}" placeholder="请输入套餐原价">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">售价</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[required,maxSize[100],custom[number]]" name="sellMoney" id="sellMoney" value="${combo.sellMoney}" placeholder="请输入套餐售价">
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">售卖时间</label>
                                        <div class="col-xs-5">
                                             <label class="form-radio form-icon"><input type="radio" class="sellTime" id="type_1" name="sellTime" <#if combo.isInTime == 0>checked</#if> value="0">长期有效</label>&nbsp;&nbsp;
                                             <label class="form-radio form-icon"><input type="radio" class="sellTime" id="type_2" name="sellTime" <#if combo.isInTime == 1>checked</#if> value="1">选择时间段</label>&nbsp;&nbsp;
                                        </div>
                                        <input type="hidden" name="combo.isInTime" id="isInTime" value="${combo.isInTime}">
                                    </div>
                                    
                                    <div class="form-group selectTime">
                                        <label class="col-xs-3 control-label">开始时间</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control" name="combo.startTime" onClick="WdatePicker()" value="<#if combo.isInTime == 1>${combo.endTime?string('yyyy-MM-dd')}</#if>" readonly id="startTime" placeholder="选择开始时间">
                                        </div>
                                    </div>
                                    <div class="form-group selectTime">
                                        <label class="col-xs-3 control-label">截至时间</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control" name="combo.endTime" value="<#if combo.isInTime == 1>${combo.endTime?string('yyyy-MM-dd')}</#if>" readonly onClick="WdatePicker()" id="endTime" placeholder="选择结束时间">
                                        </div>
                                    </div>
                                     <div class="form-group">
                                        <label class="col-xs-3 control-label">描述</label>
                                        <div class="col-xs-5">
                                             <textarea class="form-control" name="combo.description" id="description" rows="8">${combo.description!''}</textarea>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">备注</label>
                                        <div class="col-xs-5">
                                             <textarea class="form-control" name="combo.remark" id="remark" rows="8">${combo.remark!''}</textarea>
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
		
		
		<div class="modal fade" id="myModal">
		  <div class="modal-dialog">
		    <div class="modal-content">
		      <div class="modal-header">
		        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
		        <h4 class="modal-title">选择产品</h4>
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
			
			<#if combo.isInTime  == 0>
				$('.selectTime').hide();
			</#if>
			
			initProductTag();
			
			$('.sellTime').click(function(){
				var isInTime = $(this).val();
				if(isInTime == 0){
					$('.selectTime').hide();
				}else if(isInTime == 1){
					$('.selectTime').show();
				}
				$('#isInTime').val(isInTime);
			});
		});
		
		function initProductTag(){
			<#assign productIds = '' >
			var html = ''+
			 	<#list productList as pl>
               		'<li id="productItem${pl.id}" class="tagit-choice ui-widget-content ui-state-default ui-corner-all tagit-choice-editable">'+
                    	'<span class="tagit-label">${pl.name}-${pl.amount}${pl.unit!''}</span>'+
                    	'<a class="tagit-close" onclick="removeItem(${pl.id},${pl.money});">'+
                    		'<span class="text-icon">×</span>'+
                    		'<span class="ui-icon ui-icon-close"></span>'+
                    	'</a>'+
                    	'<input type="hidden" value="Tag1" name="tags" class="tagit-hidden-field">'+
                    	<#assign productIds = productIds + pl.id + ',' >
                    '</li>'+
               	</#list>
               	'';
			$('#productIds').val('${productIds}');
			$('#selectProductDiv').html(html);
			
		}
		
		function formSubmit(){
			var allCheck = $('#myform').validationEngine('validate');
			if(!allCheck){
				return;
			}
			var productIds = $('#productIds').val();
			if(productIds == ""){
                Boss.alert('请选择产品！');
				return;
			}
			var isInTime = $('#isInTime').val();
			var startTime = $('#startTime').val();
			var endTime = $('#endTime').val();	
		
			if(isInTime == 1 && (startTime == '' || endTime == '')){
                Boss.alert('请选择套餐的有效时间段！');
				return;
			}
			productIds = productIds.substring(0,productIds.length-1);
			$('#productIds').val(productIds);
			$.ajax({
	  			url:'${BASE_PATH}/base/combo/update',
	  			dataType:'json',
	  			data:$('#myform').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('修改套餐成功！',function(){
                            location.href = "${BASE_PATH}/base/combo";
						});

	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('修改套餐失败!');
	  			}
	  		});
		}
		
		function selectProduct(){
			var filterIds = $('#productIds').val();
			var productName = $('#productName').val();			
			$.ajax({
				url:'${BASE_PATH}/base/combo/productList',
				dataType:'html',
				data:{name:productName,filterIds:filterIds},
				type:'get',
				success:function(data){
					$('#myModelBody').html(data);
					$('#myModal').modal('show');
				},error:function(data){
                    Boss.alert('请求产品列表异常！');
				}
			});
		}
		
		
		function selectItem(obj,productId,productMemo,money){
			var html = '<li id="productItem'+productId+'" class="tagit-choice ui-widget-content ui-state-default ui-corner-all tagit-choice-editable">'+
                        	'<span class="tagit-label">'+productMemo+'</span>'+
                        	'<a class="tagit-close" onclick="removeItem('+productId+','+money+');">'+
                        		'<span class="text-icon">×</span>'+
                        		'<span class="ui-icon ui-icon-close"></span>'+
                        	'</a>'+
                        	'<input type="hidden" value="Tag1" name="tags" class="tagit-hidden-field">'+
                       '</li>';
			$('#selectProductDiv').append(html);
			$('#selectProductDiv').show();
			var productIds = $('#productIds').val();
			productIds += productId + ',';
			
			$('#productIds').val(productIds);
			var originalMoney = $('#originalMoney').val();
			if(originalMoney == ""){
				originalMoney = 0;
			}
			originalMoney = parseFloat(originalMoney);
			originalMoney += parseFloat(money);
			$('#originalMoney').val(originalMoney);
			
			$(obj).parent().html('<a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="removeSelectListItem(this,'+productId+',\''+productMemo+'\','+money+');"><i class="fa fa-trash"></i>&nbsp;移除 </a>');
			//$('#myModal').modal('hide');
		}
		
		function removeItem(productId,money){
			var newIds = "";
			var productIds = $('#productIds').val();
			var ids = productIds.split(',');
			for(var i = 0;i<ids.length;i++){
				if(ids[i] != productId){
					newIds += ids[i] + ",";
				}
			}
			$('#productIds').val(newIds);
			$('#productItem'+productId).remove();
			
			var originalMoney = $('#originalMoney').val();
			if(originalMoney == ""){
				originalMoney = 0;
			}
			originalMoney = parseFloat(originalMoney);
			originalMoney = originalMoney - parseFloat(money);
			$('#originalMoney').val(originalMoney);
			
			var li = $('#selectProductDiv li');
			if(li.length <= 0){
				$('#selectProductDiv').hide();
				$('#productIds').val('');
			}
		}
		
		function removeSelectListItem(obj,productId,productMemo,money){
			$(obj).parent().html('<a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="selectItem(this,'+productId+',\''+productMemo+'\','+money+');"><i class="fa fa-shopping-cart"></i>&nbsp;选择 </a>');
			removeItem(productId,money);
		}
	</script>
</html>