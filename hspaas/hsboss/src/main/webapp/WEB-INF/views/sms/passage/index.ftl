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
								<li> <a href="#">  短信管理 </a> </li>
								<li class="active"> 通道管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
								<form id="myform" method="post">
									<input type="hidden" name="pn" id="pn" value="1"/>
								    <div class="row" style="margin-top:5px">
								    	<div class="col-md-4">
								    		<div class="input-group">
								    			<span class="input-group-addon">通道名称</span>
								    			<input type="text" class="form-control" id="keyword" name="keyword" value="${keyword!''}" placeholder="输入通道名称...">
								    		</div>
								    	</div>
								    	<div class="col-md-4">
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
                                <a class="btn btn-success" href="${BASE_PATH}/sms/passage/add">添加通道</a>
                            </div>
                            <h3 class="panel-title">
                            <span>通道列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>编号</th>
                                        <th>通道名称</th>
                                        <th>通道编码</th>
                                        <th>运营商</th>
                                        <th>通道类型</th>
                                        <th>优先级</th>
                                        <th>创建时间</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                	<#list page.list as pl>
                                    <tr>
                                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
                                        <td>
                                        <#if pl.status?? && pl.status == 0>
                                            <span class="label label-success">使用中</span>
                                        <#else>
                                        	<span class="label label-danger">停用</span>
                                        </#if>&nbsp;${(pl.name)!''}
                                        </td>
                                        <td>${(pl.code)!''}</td>
										<td>
                                        <#if pl.cmcp == 1>
                                            移动
                                        <#elseif pl.cmcp == 2>
                                            电信
                                        <#elseif  pl.cmcp == 3>
                                            联通
                                        <#elseif  pl.cmcp == 4>
                                            全网
                                        <#else>
                                            未知
                                        </#if>
                                        </td>
                                        <td>
											<#if pl.type == 1>
												独立通道
											<#else>
												公共通道
											</#if>
										</td>
										<td>${pl.priority!'--'}</td>
                                        <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                                        <td>
                                        	<a class="btn btn-primary btn-xs" href="${BASE_PATH}/sms/passage/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	&nbsp;
                                        	<a class="btn btn-danger btn-xs" href="javascript:void(0);" onclick="deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除</a>
                                            &nbsp;
                                            <#if pl.status == 0>
                                                <a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="disabled(${pl.id},'1');"><i class="fa fa-lock"></i>&nbsp;禁用 </a>
                                            <#else>
                                                <a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="disabled(${pl.id},'0');"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
                                            </#if>
                                            <a class="btn btn-warning btn-xs" href="javascript:void(0);" onclick="kill(${pl.id});"><i class="fa fa-refresh"></i>&nbsp;断连接</a>
                                            &nbsp;
                                            <a class="btn btn-info btn-xs" href="javascript:void(0);" onclick="testPassage(${pl.id});"><i class="fa fa-bug"></i>&nbsp;测试通道 </a>
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



            <div class="modal fade" id="testPassageModal">
                <div class="modal-dialog" style="width:auto;height:auto;min-width:420px">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" onclick="closeModal();"><span
                                    aria-hidden="true">&times;</span></button>
                            <h4 class="modal-title">测试通道</h4>
                        </div>
                        <div class="modal-body" data-scrollbar="true" data-height="500" data-scrollcolor="#000"
                             id="myModelBody">
                            <b>手机号码：</b><textarea id="testMobile" class="form-control" rows="5" cols="20"></textarea>
                            <b>短信内容：</b><textarea id="testContent" class="form-control" rows="6" cols="20"></textarea>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-default" onclick="closeModal();">关闭</button>
                            &nbsp;
                            <button type="button" class="btn btn-success" onclick="sendTestSms();">发送</button>
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
		function jumpPage(p){
			$('#pn').val(p);
			$('#myform').attr('action','${BASE_PATH}/sms/passage/index');
			$('#myform').submit();
		}
		
		function deleteById(id){
			Boss.confirm("确定要删除该通道吗？",function(){
				$.ajax({
					url:'${BASE_PATH}/sms/passage/delete',
					type:'post',
					dataType:'json',
					data:{id:id},
					success:function(data){
						Boss.alertToCallback(data.message,function(){
                            if(data.result){
                                location.reload();
                            }
                        });
					},error:function(data){
						Boss.alert("删除通道失败！");
					}
				});
			});
		}

		function disabled(id,flag){
            var msg  = "禁用";
            if(flag == 0){
                msg = "启用";
            }
            Boss.confirm('确定要'+msg+'该通道吗？',function(){
                $.ajax({
                    url:'${BASE_PATH}/sms/passage/disabled',
                    type:'post',
                    dataType:'json',
                    data:{id:id,flag:flag},
                    success:function(data){
                        Boss.alertToCallback(data.message,function(){
                            if(data.result){
                                location.reload();
                            }
                        });
                    },error:function(data){
                        Boss.alert(msg+"通道失败！");
                    }
                });
            });
        }
        
        function kill(id){
            Boss.confirm('确定要断开该通道连接吗？',function(){
                $.ajax({
                    url:'${BASE_PATH}/sms/passage/kill',
                    type:'post',
                    dataType:'json',
                    data:{id:id},
                    success:function(data){
                        Boss.alertToCallback(data.message,function(){
                            if(data.result){
                                location.reload();
                            }
                        });
                    },error:function(data){
                        Boss.alert("断开通道连接失败！");
                    }
                });
            });
        }

        function closeModal(){
            $('#testPassageModal').modal('hide');
        }

        var testPassageId = -1;

        function testPassage(id){
            testPassageId = id;
            
            var code = Math.floor(Math.random() * 1000000);
            $('#testContent').val("【华时科技】您的验证码为"+code+"，请尽快完成后续操作。");
            $('#testPassageModal').modal('show');
		}

		function sendTestSms(){
            if(testPassageId == -1){
                Boss.alert('请选择需要测试通道！');
                return;
            }
            var mobile = $('#testMobile').val();
            if(mobile == ''){
                Boss.alert('请输入手机号码！');
                return;
            }
            var content = $('#testContent').val();
            if(content == ''){
                Boss.alert('请输入短信内容！');
                return;
            }
            var realMobiles = "";
            var mobiles = mobile.split('\n');
            for(var i = 0;i<mobiles.length;i++){
                if(mobiles[i] != ''){
                    realMobiles += mobiles[i] + ',';
                }
            }
            realMobiles = realMobiles.substring(0,realMobiles.length-1);
            
            $.ajax({
                url:'${BASE_PATH}/sms/passage/test',
                data:{'mobile':realMobiles,'content':content,'passageId':testPassageId},
                dataType:'json',
                type:'post',
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            testPassageId = -1;
                            $('#testMobile').val('');
                            $('#testContent').val('');
                            closeModal();
                        }
                    });
                },error:function(data){
                    Boss.alert('测试通道失败！');
                }
            })
        }
	</script>
</html>