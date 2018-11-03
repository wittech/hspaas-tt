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
								<li> <a href="#"> 系统管理 </a> </li>
								<li class="active"> 客户基本信息 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
							
								<form id="myform" method="post">
								<input type="hidden" name="pn" id="pn" value="1">
								<div class="row">
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">姓名</span>
							    			<input type="text" class="form-control" id="fullName" name="fullName" value="${fullName!''}" placeholder="输入姓名查询">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">手机号码</span>
							    			<input type="text" class="form-control" id="mobile" name="mobile" value="${mobile!''}" placeholder="输入手机号码查询">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">公司名称</span>
							    			<input type="text" class="form-control" id="company" name="company" value="${company!''}" placeholder="输入公司名称查询">
							    		</div>
							    	</div>
							    </div>
							    
							    <div class="row" style="margin-top:5px">
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">身份证号</span>
							    			<input type="text" class="form-control" id="cardNo" name="cardNo" value="${cardNo!''}" placeholder="输入身份证号查询">
							    		</div>
							    	</div>
							    	<div class="col-md-2">
							    		<div class="input-group">
							    			<span class="input-group-addon">状态</span>
							    			<select id="state" name="state" class="form-control">
							    				<option value="-1">--选择--</option>
							    				<option value="1" <#if state?? && state == '1'>selected</#if>>启用</option>
							    				<option value="2" <#if state?? && state = '2'>selected</#if>>已禁用</option>
							    			</select>
							    		</div>
							    	</div>
                                    <div class="col-md-4">
                                        <div class="input-group">
                                            <span class="input-group-addon">接口帐号</span>
                                            <input type="text" class="form-control" id="appkey" name="appkey" value="${appkey!''}" placeholder="输入接口帐号">
                                        </div>
                                    </div>
							    	<div class="col-md-2">
							    		<button class="btn btn-primary" onclick="jumpPage(1);">查&nbsp;&nbsp;&nbsp;询</button>
                                        <a class="btn btn-default" onclick="clearForm();">重&nbsp;&nbsp;&nbsp;置</a>
							    	</div>
							    </div>
							    </form>
							    
							</div>
						</div>


						<div class="panel">
                        <div class="panel-heading">
                        	<#if macro.doOper("1001001001")>
	                            <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
	                                <a class="btn btn-success" href="${BASE_PATH}/base/customer/add">新增客户</a>
	                            </div>
                            </#if>
                            <h3 class="panel-title">
                            <span>客户信息列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                    	<th width="5%">编号</th>
                                        <th width="10%">公司名称</th>
                                        <th width="10%">姓名</th>
                                        <th width="6%">手机号</th>
                                        <th width="5%">身份证号</th>
                                        <th width="4%">状态</th>
                                        <th width="10%">开户时间</th>
                                        <th width="20%">操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#assign balanceCheck = macro.doOper("1001001003001") />
									<#assign editCheck = macro.doOper("1001001002") />
									<#assign templateCheck = macro.doOper("1001001004") />
									<#assign disabledCheck = macro.doOper("1001001005") />
                                	<#if page?? && page.list?size gt 0>
                                		<#list page.list as pl>
		                                    <tr>
		                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
		                                        <td>${(pl.company)!''}<#if pl.label?? && pl.label != ''><span class="label label-danger">禁止操作</span></#if></td>
		                                        <td>${(pl.fullName)!''}</td>
		                                        <td>${(pl.mobile)!''}</td>
		                                        <td>${(pl.cardNo)!''}</td>
		                                        <td>
		                                        	<#if pl.state?? && pl.state == '0'>
			                                            <span class="label label-success">正常</span>
			                                        <#else>
			                                        	<span class="label label-danger">已禁用</span>
			                                        </#if>&nbsp;
		                                        	
												</td>
												<td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
		                                        <td>
		                                        	<#if editCheck>
		                                        		<a class="btn btn-primary btn-xs" href="${BASE_PATH}/base/customer/edit?id=${pl.userId}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
		                                        	</#if>
		                                        	<#--
														<a class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除 </a>
		                                        	-->
		                                        	<#if balanceCheck>
		                                        		<a class="btn btn-success btn-xs" href="${BASE_PATH}/base/user_balance?userId=${pl.userId}"><i class="fa fa-edit"></i>&nbsp;余额 </a>
		                                        	</#if>
		                                        	<#if templateCheck>
		                                        		<a class="btn btn-info btn-xs" href="${BASE_PATH}/sms/message_template/create?userId=${pl.userId}"><i class="fa fa-edit"></i>&nbsp;模板 </a>
		                                        	</#if>
		                                        	<#if disabledCheck>
			                                        	<#if pl.state == '0'>
			                                        		<a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="disabled(${pl.userId},'1');"><i class="fa fa-lock"></i>&nbsp;禁用 </a>
			                                        	<#else>
			                                        		<a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="disabled(${pl.userId},'0');"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
			                                        	</#if>
		                                        	</#if>
		                                        </td>
		                                    </tr>
                                    	</#list>
                                    <#else>
                                    <tr>
                                        <td colspan="9">未查询到任何数据！</td>
                                    </tr>
                                    </#if>
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

		</div>
		<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
		<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
		<script type="text/javascript">

			function clearForm(){
				var inputs = $('input');
				for(var i = 0;i<inputs.length;i++){
					var input = $(inputs[i]);
					input.val('');
				}
				var selects = $('select');
                for(var i = 0;i<selects.length;i++){
                    var select = $(selects[i]);
                    select.val(-1);
                }
			}
		
			function jumpPage(p){
				$('#pn').val(p);
				$('#myform').attr('action','${BASE_PATH}/base/customer/index');
				$('#myform').submit();
			}
		
			function disabled(id,flag){
				var msg = flag == 1 ? "禁用" : "启用";
				Boss.confirm("确定要"+msg+"该客户吗？",function(){
					$.ajax({
						url:'${BASE_PATH}/base/customer/disabled',
						dataType:'json',
						type:'post',
						data:{id:id,flag:flag},
						success:function(data){
                            Boss.alertToCallback(data.message,function(){
                                if(data.result){
                                    location.reload();
                                }
                            });
						},error:function(data){
                            Boss.alert('操作异常！');
						}
					});
				});
			}
		</script>
	</body>
	
</html>