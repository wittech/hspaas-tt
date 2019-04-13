<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>彩信模板查询 - 彩信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="${rc.contextPath}/static/css/global.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${rc.contextPath}/static/css/table.css" />
    <link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
    
    <style>
    	
		.img_pre {
		    resize: vertical;
    		height : 100px;
		}
    	
    </style>
    
</head>

<body>
<div style="margin: 0;">
   
        <div class="admin-main">
            <form class="layui-form" action="">
	            <div style="margin: 6px 0px 6px 3px;">
	                <div class="layui-form-search">
	                    <div class="layui-input-inline">
	                        <input type="text" id="title" name="title" placeholder="请输入模板标题" autocomplete="off" class="layui-input">
	                    </div>
	                    <div class="layui-input-inline">
	                        <select name="status" lay-filter="status">
	                            <option value="" selected>请选择审批状态</option>
	                            <#if approveStatus??>
	                            <#list approveStatus as a>
	                            <option value="${(a.value)!}">${(a.title)!}</option>
	                            </#list>
	                            </#if>
	                        </select>
	                    </div>
	                    <button lay-filter="search" class="layui-btn" lay-submit><i class="fa fa-search" aria-hidden="true"></i> 查询</button>
	                </div>
	            </div>
            </form>
            <fieldset class="layui-elem-field">
                <div class="layui-field-box layui-form">
                    <table class="layui-hide" id="template-data-table" lay-filter="template-data-table"></table>
                    <script type="text/html" id="table-template-toolbar">
					    <div class="layui-btn-container">
					        <button class="layui-btn layui-btn-sm layuiadmin-btn-admin " lay-event="add">新增</button>
					        <button class="layui-btn layui-btn-sm layuiadmin-btn-admin layui-btn-danger" lay-event="removeAll">删除</button>
					    </div>
					</script>
					
					<script type="text/html" id="table-template-operation">
						 <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="preview"><i class="layui-icon layui-icon-cellphone-fine"></i>预览</a>
					     {{# if(d.status == 0){ }}
						     <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="remove"><i class="layui-icon layui-icon-delete"></i>删除</a>
					     {{# } else if(d.status == 2){ }}
					     	 <a class="layui-btn layui-btn-warm layui-btn-xs" lay-event="send"><i class="layui-icon layui-icon-release"></i>发送彩信</a>
					     {{# } }}
					</script>
                </div>
            </fieldset>
        </div>
    </div>
    
    <div id="content-preview" style="display: none;">
	</div>

<script type="text/javascript" src="/static/js/date_format.js"></script>

<script type="text/html" id="date_format">
	{{ getFormatDateByLong(d.createTime, "yyyy-MM-dd hh:mm:ss") }}
</script>

<script type="text/javascript" src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/layui.js"></script>
<script type="text/javascript">

	layui.config({
	    base: '${rc.contextPath}/static/plugins/layui2/lay/modules/'
	}).use(['element', 'table', 'form', 'modal'], function() {
	    var table = layui.table
	        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
	        ,layer = layui.layer
	        ,form = layui.form
	        ,admin = layui.admin
	        ,modal = layui.modal
	        ,element = layui.element
	        ,$ = layui.$;
	
	    //预定义参数
	    var searchObj = new Object();
	    var currpage = 1;
		var tableId = "template-data-table";
	
	    var loadTableData = function() {
	    	searchObj.currentPage = currpage;
	    	searchObj.status = $.trim($("#status").val());
	    	searchObj.title = $.trim($("#title").val());
	    	
	        table.render({
	            id:tableId
	            ,elem: '#'+tableId
	            ,cellMinWidth: 50
	            ,toolbar: '#table-template-toolbar'
	            ,cols: [[
	                  {type: 'checkbox', fixed: 'left'}
	                  ,{field: 'modelId', title: '模板Id'}
		              ,{field: 'title', title: '模板标题'}
		              ,{field: 'status', title: '审批状态', width: 160}
		              ,{field: 'createTime', title: '提交时间', width: 180, templet: '#date_format'}
		              ,{fixed: 'right', title:'操作', toolbar: '#table-template-operation', width:210}
		         ]]
	            ,url: server_domain + "/mms/template/page"
	            ,loading: true
	            ,where: searchObj
	            ,method: 'GET'
	            ,page: {
		            layout: ['count', 'prev', 'page', 'next', 'skip']
		            ,groups: 5
		             ,first: '1'
	            	,last: '尾页'
		         }
	            ,limit:20
	            ,even: true
	            ,done: function(res, curr, count){
	            	$("[data-field='status']").children().each(function(){
						if($(this).text()=='0'){
						   $(this).html("<a class='layui-btn layui-btn-primary layui-btn-xs'>待审核</a> ")
						}else if($(this).text()=='1'){
						   $(this).html("<a class='layui-btn layui-btn-warm layui-btn-xs'>平台处理中</a>")
						}else if($(this).text()=='2'){
						   $(this).html("<a class='layui-btn layui-btn-success layui-btn-xs'>审核通过</a>")
						}else if($(this).text()=='3'){
						   $(this).html("<a class='layui-btn layui-btn-danger layui-btn-xs'>审核失败</a>")
						}
	            	});
	            	
	                currpage = curr;
	            }
	        });
	    };
	    
	    loadTableData();
	    
	    var l_index;
	    
	    //头工具栏事件
	    table.on('toolbar('+tableId+')', function(obj){
	        var checkStatus = table.checkStatus(obj.config.id);
	        switch(obj.event){
	            case 'add':
		            parent.tab.tabAdd({
				         url: server_domain + 'mms/template/add',
				         title: '新增彩信模板'
				     });
	                break;
	            case 'removeAll':
	                var data = checkStatus.data;
	                if (data.length == 0) {
	                    modal.alertWarning("请至少选择一条记录");
	                    return;
	                }
	                var dsIds = [];
	                $.each(data,function(i,item){
	                    dsIds.push(item.id);
	                });
	                var ids = dsIds.join(",");
	                modal.confirm("确认要删除选中的" + data.length + "条数据吗?", function() {
	                    $.ajax({
	                        url: server_domain+'/mms/template/delete'
	                        ,type: 'post'
	                        ,beforeSend : function() {
		                    	l_index = layer.load(1);
		                    }
	                        ,data: { "ids": ids }
	                        ,success: function(data){
	                        	layer.close(l_index);
	                        	modal.msgSuccess("删除成功");
		                    	table.reload(tableId);
		                    	
		                    	/**
	                        	if(data.success == true) {
		                    		modal.msgSuccess("删除成功");
		                    		table.reload(tableId);
		                    	} else {
		                    		modal.msgError("删除失败");
		                    	}*/
	                        },error : function() {
	                        	layer.close(l_index);
		                    	modal.msgError("服务请求异常");
		                    }
	                    });
	                });
	                break;
	        };
	    });
	    
	    function preview(id) {
	    	$("#content-preview").html("");
	    	$.ajax({
                url: server_domain+'/mms/template/preview'
                ,type: 'get'
                ,dataType:'html'
                ,data: {
                    "id": id
                }
                ,beforeSend : function() {
                	l_index = layer.load(1);
                }
                ,success: function(data){
                	layer.close(l_index);
                	$("#content-preview").html(data);
	    			modal.popupRight('内容预览', 'content-preview', 360);
                },error : function() {
                	layer.close(l_index);
                	modal.msgError("服务请求异常");
                }
            });
	    }
	
	    //监听行工具事件
	    table.on('tool('+tableId+')', function(obj){
	        var pdata=obj?obj.data:null;
	        if(obj.event === 'remove'){
	            modal.confirm("你确定删除数据吗?", function() {
	                $.ajax({
	                    url: server_domain+'/mms/template/delete'
	                    ,type: 'post'
	                    ,data: {
	                        "ids": pdata.id
	                    }
	                    ,beforeSend : function() {
	                    	l_index = layer.load(1);
	                    }
	                    ,success: function(data){
	                    	layer.close(l_index);
	                    	modal.msgSuccess("删除成功");
	                    	table.reload(tableId);
	                    },error : function() {
	                    	layer.close(l_index);
	                    	modal.msgError("服务请求异常");
	                    }
	                });
	            });
	
	        } else if(obj.event === 'edit'){
	        	parent.tab.tabAdd({
			         url: server_domain + 'mms/template/edit?id='+pdata.id,
			         icon: 'fa-user',
			         title: '编辑彩信模板'
			     });
	        } else if(obj.event === 'send'){
	            modal.open('发送模板彩信', server_domain+'/mms/template/sendMms/?id='+pdata.id, 600, 300, {
	                action: server_domain+'/mms/send/byModel',
	                method: 'post',
	                beforeSend : function() {
                    	l_index = layer.load(1);
                    },
	                callback: function (data) {
	                	layer.close(l_index);
	                	modal.msgSuccess("发送成功");
                    	table.reload(tableId);
                    	
                    	/**
	                	if(data.success == true) {
                    		modal.msgSuccess("发送成功");
                    		table.reload(tableId);
                    	} else {
                    		modal.msgError("发送失败");
                    	}*/
	                }
	            });
	        } else if(obj.event === 'preview'){
	        	preview(pdata.id);
	        }
	        
	    });
	    
	    var reloadList = function() {
	    	searchObj.currentPage = currpage;
	        table.reload(tableId, {
	            where: searchObj
	            ,page: {
	                curr: currpage
	            }
	        });
	    };
	
	    form.on('submit(search)', function(data) {
	        searchObj = data.field;
	        currpage = 1;
	        reloadList();
	        return false;
	    });
	
	});

</script>
</body>

</html>