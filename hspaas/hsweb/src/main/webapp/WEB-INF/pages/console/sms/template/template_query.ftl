<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>短信模板查询 - 短信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="${rc.contextPath}/static/css/global.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${rc.contextPath}/static/css/table.css" />
    <link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body>
<div style="margin: 0;">
   
        <div class="admin-main">
            <form class="layui-form" action="">
            <blockquote class="layui-elem-quote">
                <a href="javascript:;" class="layui-btn layui-btn-normal layui-btn-sm" id="export">检索条件
                </a>
                <div style="float:right;">
                    <div class="layui-form-search" style="margin:0;">
                        <div class="layui-input-inline">
                            <input type="text" id="content" name="content" placeholder="请输入模板内容" autocomplete="off" class="layui-input">
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
                        <button lay-filter="search" class="layui-btn layui-btn-sm" lay-submit><i class="fa fa-search" aria-hidden="true"></i> 查询</button>
                    </div>
                </div>
            </blockquote>
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
					     {{# if(d.status == 0){ }}
						     <a class="layui-btn layui-btn-xs" lay-event="edit"><i class="layui-icon layui-icon-edit"></i>编辑</a>
						     <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="remove"><i class="layui-icon layui-icon-delete"></i>删除</a>
					     {{# } }}
					</script>
                </div>
            </fieldset>
        </div>
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
	    	searchObj.content = $.trim($("#content").val());
	    	
	        table.render({
	            id:tableId
	            ,elem: '#'+tableId
	            ,cellMinWidth: 50
	            ,toolbar: '#table-template-toolbar'
	            ,cols: [[
	                  {type: 'checkbox', fixed: 'left'}
		              ,{field: 'content', title: '模板内容'}
		              ,{field: 'status', title: '发送状态', width: 160, sort: true}
		              ,{field: 'createTime', title: '提交时间', width: 180, templet: '#date_format'}
		              ,{fixed: 'right', title:'操作', toolbar: '#table-template-operation', width:210}
		         ]]
	            ,url: server_domain + "/sms/template/page"
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
						   $(this).text("待审核")
						}else if($(this).text()=='1'){
						   $(this).text("审核通过")
						}else if($(this).text()=='2'){
						   $(this).text("审核失败")
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
	                modal.open('新增短信模板', server_domain+'/sms/template/add', 600, 300, {
	                    action: server_domain+'/sms/template/save',
	                    method: 'post',
	                    beforeSend : function() {
	                    	l_index = layer.load(1);
	                    },
	                    callback: function (data) {
	                    	layer.close(l_index);
	                    	modal.msgSuccess("保存成功");
	                    	table.reload(tableId);
	                    	/**
	                    	if(data.success == true) {
	                    		modal.msgSuccess("保存成功");
	                    		table.reload(tableId);
	                    	} else {
	                    		modal.msgError("保存失败");
	                    	}
	                    	*/
	                    }
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
	                        url: server_domain+'/sms/template/delete'
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
	
	    //监听行工具事件
	    table.on('tool('+tableId+')', function(obj){
	        var pdata=obj?obj.data:null;
	        if(obj.event === 'remove'){
	            modal.confirm("你确定删除数据吗?", function() {
	                $.ajax({
	                    url: server_domain+'/sms/template/delete'
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
	            modal.open('编辑模板信息', server_domain+'/sms/template/edit/?id='+pdata.id, 600, 300, {
	                action: server_domain+'/sms/template/update',
	                method: 'post',
	                beforeSend : function() {
                    	l_index = layer.load(1);
                    },
	                callback: function (data) {
	                	layer.close(l_index);
	                	modal.msgSuccess("修改成功");
                    	table.reload(tableId);
                    	
                    	/**
	                	if(data.success == true) {
                    		modal.msgSuccess("修改成功");
                    		table.reload(tableId);
                    	} else {
                    		modal.msgError("修改失败");
                    	}*/
	                }
	            });
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