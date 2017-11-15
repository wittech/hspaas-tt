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
								<li> <a href="#"> 套餐管理 </a> </li>
								<li class="active">产品管理</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                            <!-- Panel heading -->
                            <div class="panel-heading">
                                <h3 class="panel-title">新增产品</h3>
                            </div>
                            <!-- Panel body -->
                                <form id="myform" class="form-horizontal">
                                  <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">产品名称</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[required,maxSize[100]]" name="product.name" id="name" placeholder="请输入产品名称">
                                        </div>
                                    </div>
                                      <div class="form-group">
                                        <label class="col-xs-3 control-label">产品类型</label>
                                        <div class="col-xs-5">
                                             <label class="form-radio form-icon"><input type="radio" class="productType" id="type_1" name="productType" checked value="1">短信</label>&nbsp;&nbsp;
                                             <label class="form-radio form-icon"><input type="radio" class="productType" id="type_2" name="productType" value="2">流量</label>&nbsp;&nbsp;
                                             <label class="form-radio form-icon"><input type="radio" class="productType" id="type_3" name="productType" value="3">语音</label>&nbsp;&nbsp;
                                        </div>
                                        <input type="hidden" name="product.type" id="productType" value="1">
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">金额</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[required,maxSize[100],custom[number]]" name="money" id="money" placeholder="请输入产品金额">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">数量</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[required,maxSize[100],custom[number]]" name="product.amount" id="amount" placeholder="请输入数量">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">单位</label>
                                        <div class="col-xs-5">
                                            <input type="text" class="form-control validate[required,maxSize[100]]" name="product.unit" value="条" id="unit" placeholder="请输入单位">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-3 control-label">备注</label>
                                        <div class="col-xs-5">
                                             <textarea class="form-control" name="product.remark" id="remark" rows="8"></textarea>
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
			
			$('.productType').click(function(){
				var $this = $(this);
				if($this.val() == 1){
					$('#unit').val('条');
				}else if($this.val() == 2){
					$('#unit').val('M');
				}else if($this.val() == 3){
					$('#unit').val('分钟');
				}
				$('#productType').val($this.val());
			});
		});
		
		function formSubmit(){
			var allCheck = $('#myform').validationEngine('validate');
			if(!allCheck){
				return;
			}
			$.ajax({
	  			url:'${BASE_PATH}/base/product/create',
	  			dataType:'json',
	  			data:$('#myform').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('新增产品成功！',function(){
                            location.href = "${BASE_PATH}/base/product"
						});
	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('新增产品失败!');
	  			}
	  		});
		}
	</script>
</html>