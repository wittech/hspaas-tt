<!DOCTYPE html>
<html lang="en">

	<head>
		<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
		<meta charset="utf-8">

		<title>融合平台</title>
		<link href="${BASE_PATH}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">
        <link href="${BASE_PATH}/resources/css/bootstrap/style.css" rel="stylesheet">
        <link href="${BASE_PATH}/resources/js/confirm/jquery-confirm.css" rel="stylesheet">
        <link href="${BASE_PATH}/resources/ztree/css/zTreeStyle/zTreeStyle.css" rel="stylesheet">
		<link href="${BASE_PATH}/resources/css/bootstrap/font-awesome.min.css" rel="stylesheet">
		<link href="${BASE_PATH}/resources/css/bootstrap/pace.min.css" rel="stylesheet">
        <script type="text/javascript" src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
        <script type="text/javascript" src="${BASE_PATH}/resources/ztree/js/jquery.ztree.core.js"></script>
        <script type="text/javascript" src="${BASE_PATH}/resources/ztree/js/jquery.ztree.excheck.js"></script>
        <script type="text/javascript" src="${BASE_PATH}/resources/ztree/js/jquery.ztree.exedit.js"></script>
		<script type="text/javascript" src="${BASE_PATH}/resources/js/bootstrap/pace.min.js"></script>
        <script type="text/javascript" src="${BASE_PATH}/resources/js/common.js"></script>
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
								<li class="active">角色管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                        <div class="panel-heading">
							<#if macro.doOper("6002001")>
								<div class="pull-right"  style="margin-top: 10px;margin-right: 20px;">
									<button class="btn btn-success" onclick="location.href='${BASE_PATH}/boss/role/add'">新增角色</button>
								</div>
							</#if>
                            <h3 class="panel-title">
                            <span>角色列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                   <tr>
										<th>#</th>
										<th>角色名称</th>
										<th>创建时间</th>
										<th>创建用户</th>
										<th>操作</th>
						 			</tr>
                                </thead>
                                <tbody>
									<#assign deleteCheck = macro.doOper("6002003") />
									<#assign editCheck = macro.doOper("6002002") />
									<#assign authCheck = macro.doOper("6002004") />
                                	<#list page.list as pl>
									<tr>
										<td>${(page.pageNumber - 1) * page.pageSize + (pl_index+1)}</td>	
										<td>${pl.role_name}</td>	
										<td>${pl.created_at?string('yyyy-MM-dd HH:mm:ss')}</td>	
										<td>${pl.created}</td>	
										<td>
											<#if authCheck>
                                            	<a class="btn btn-success btn-xs" href="javascript:void(0);" onclick="setAuth(${pl.id});"><i class="fa fa-gear"></i>&nbsp;设置权限 </a>
											</#if>
											<#if editCheck>
                                                <a class="btn btn-primary btn-xs" href="${BASE_PATH}/boss/role/edit?id=${pl.id}"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
											</#if>
											<#if deleteCheck>
												<a class="btn btn-danger btn-xs" href="javascript:void(0);" onclick="deleteById(${pl.id});"><i class="fa fa-trash"></i>&nbsp;删除 </a>
											</#if>
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
		</div>


		<div class="modal fade" id="setAuthModal">
			<div class="modal-dialog" style="width:350px;height: 600px">
				<div class="modal-content" style="width:350px;height: 710px">
					<div class="modal-header">
						<button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
								aria-hidden="true">&times;</span></button>
						<h4 class="modal-title">设置权限</h4>
					</div>
					<div class="modal-body" id="setAuthModelBody"   style="height:600px;overflow:auto;">
                            <ul id="authTree" class="ztree"></ul>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-success" onclick="saveAuth();">确定</button>
						<button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
					</div>
				</div><!-- /.modal-content -->
			</div><!-- /.modal-dialog -->
		</div>


	</body>
	<script type="text/javascript" src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script>
	<script type="text/javascript" src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script>
	<script type="text/javascript" src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
	<script type="text/javascript" src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
	<script type="text/javascript" src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
	<script type="text/javascript">
	
		function jumpPage(pn){
			location.href = "${BASE_PATH}/boss/role/index?pn="+pn;
		}

        var setting = {
            check: {
                enable: true,
                autoCheckTrigger: true
            },
            data: {
                simpleData: {
                    enable: true
                }
            }
        };

        var code ,rMenu,zTree;

        function setCheck() {
            var zTree = $.fn.zTree.getZTreeObj("authTree"),
                    py = $("#py").attr("checked")? "p":"",
                    sy = $("#sy").attr("checked")? "s":"",
                    pn = $("#pn").attr("checked")? "p":"",
                    sn = $("#sn").attr("checked")? "s":"",
                    type = { "Y":"ps", "N":"ps"};
            zTree.setting.check.chkboxType = type;
            showCode('setting.check.chkboxType = { "Y" : "ps", "N" : "ps" };');
        }

        function showCode(str) {
            if (!code) code = $("#code");
            code.empty();
            code.append("<li>"+str+"</li>");
        }

        var currentRoleId = -1;

        function saveAuth() {
            var nodes = zTree.getCheckedNodes(true);

            var arr = new Array();
            for(var i =0; i<nodes.length; i++){
                if(nodes[i].code){
                    arr.push(nodes[i].id);
                }
            }

            if(currentRoleId <= 0) {
                Boss.alert('未选择授权的角色！');
				return;
			}

            $.ajax({
                url:'${BASE_PATH}/boss/role/saveAuth',
                dataType:'json',
                type:'post',
                data:{id:currentRoleId,operIds:arr.join(',')},
                success:function(data){
                    Boss.alertToCallback(data.message,function(){
                        if(data.result){
                            location.reload();
                        }
                    });
                },error:function(data){
                    Boss.alert('保存权限异常！');
                }
            });
		}

		var NODE_TYPE_CONSTANT = {CHECK:1,NO_CHECK:2,HALF:3};

		function setNodeCheck(node){
			return NODE_TYPE_CONSTANT.NO_CHECK;
		}

		function refashZtreeCheck() {
            zTree = $.fn.zTree.getZTreeObj("authTree");
            var nodes = zTree.getNodes();
			for(var i = 0;i<nodes.length;i++) {
				var node = nodes[i];
				if(node.level == 0) {
					alert(node.name + "-" + node.level);
				}
			}


//            for(var i =0; i<nodes.length; i++){
//                var parent2 = nodes[i].getParentNode();
//				alert(parent2.name+":"+parent2.level);
//                if(parent2.checked){
//                    continue;
//                }
//                parent2.checked = "checked";
//
//                var parent1 = parent2.getParentNode();
//                if(parent1.checked){
//                    continue;
//                }
//                parent1.checked = "checked";
//                $("#"+parent1.tId+"_check").removeClass("checkbox_false_part").addClass("checkbox_true_part");
//                //console.info(parent1);
//            }
		}

		function setAuth(id){
            currentRoleId = id;
			$.ajax({
				url:'${BASE_PATH}/boss/role/authTree',
				dataType:'json',
				type:'get',
				data:{id:id},
				success:function(data){
                    $.fn.zTree.init($("#authTree"), setting, data);
                    zTree = $.fn.zTree.getZTreeObj("authTree");
                    setCheck();
                    $("#py").bind("change", setCheck);
                    $("#sy").bind("change", setCheck);
                    $("#pn").bind("change", setCheck);
                    $("#sn").bind("change", setCheck);
					$('#setAuthModal').modal('show');
				},error:function(data){
					Boss.alert('请求权限数据异常！');
				}
			});
		}
		
		function deleteById(id){
            Boss.confirm('确定要删除该角色吗？',function(){
				$.ajax({
					url:'${BASE_PATH}/boss/role/delete',
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
                        Boss.alert('删除角色请求失败！');
					}
				})
			});
		}
		
	</script>

</html>