
layui.config({
    base: '/static/js/'
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
//    	var loading = layer.msg("数据加载中...");
    	var index = layer.load(1); //添加laoding,0-2两种方式
    	
    	searchObj.currentPage = currpage;
    	searchObj.status = $.trim($("#status").val());
    	
        table.render({
            id:tableId
            ,elem: '#'+tableId
            ,cellMinWidth: 50
            ,toolbar: '#table-template-toolbar'
            ,cols: [[
                  {type: 'checkbox', fixed: 'left'}
                  ,{field:'id', title:'ID', width:60, fixed: 'left', unresize: true, sort: true}
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
	            ,first: true
	            ,last: true
	         }
            ,even: true
            ,done: function(res, curr, count){
            	$("[data-field='status']").children().each(function(){
					if($(this).text()=='0'){
					   $(this).text("待审核")
					}else if($(this).text()=='1'){
					   $(this).text("审批通过")
					}else if($(this).text()=='2'){
					   $(this).text("审批失败")
					}
            	});
            	
                currpage = curr;
                layer.close(index);
            }
        });
    };
    
    loadTableData();
    
    //头工具栏事件
    table.on('toolbar('+tableId+')', function(obj){
        var checkStatus = table.checkStatus(obj.config.id);
        switch(obj.event){
            case 'add':
            	alert("sss");
                modal.open('新增短信模板', server_domain+'/sms/template/add', 800, '', {
                    action: server_domain+'/sms/template/save',
                    method: 'post',
                    callback: function (data) {
                        table.reload(tableId); //数据刷新
                        modal.msgSuccess("保存成功");
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
                    dsIds.push(item.dsId);
                });
                var ids = dsIds.join(",");
                modal.confirm("确认要删除选中的" + data.length + "条数据吗?", function() {
                    admin.req({
                        url: server_domain+'/sms/template/delete'
                        ,type: 'post'
                        ,data: { "ids": ids }
                        ,done: function(data){
                            table.reload(tableId);
                            modal.msgSuccess("删除成功");
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
                admin.req({
                    url: server_domain+'/sms/template/delete'
                    ,type: 'post'
                    ,data: {
                        "ids": pdata.id
                    }
                    ,done: function(data){
                        table.reload(tableId); //数据刷新
                        modal.msgSuccess("删除成功");
                    }
                });
            });

        } else if(obj.event === 'edit'){
            modal.open('编辑模板信息', server_domain+'/sms/template/edit/'+pdata.id, 800, '', {
                action: server_domain+'/sms/template/update',
                method: 'post',
                callback: function (data) {
                    table.reload(tableId); //数据刷新
                    modal.msgSuccess("保存成功");
                }
            });
        }
    });
    
    var reloadList = function() {
    	searchObj.currentPage = currpage;
    	
        if(searchObj.status){
            searchObj.status = searchObj.status=='0'?"":searchObj.status;
        }
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