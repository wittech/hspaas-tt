layui.use(['element', 'table', 'form', 'laydate'], function() {
    var table = layui.table
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer
        ,form = layui.form
        ,element = layui.element
        ,laydate = layui.laydate
        ,$ = layui.$;

    //日期
    laydate.render({
        elem: '#startDate'
    });
    laydate.render({
        elem: '#endDate'
    });
    
    //预定义参数
    var searchObj = new Object();
    var currpage = 1;

    var loadTableData = function() {
//    	var loading = layer.msg("数据加载中...");
    	var index = layer.load(0); //添加laoding,0-2两种方式
    	
    	searchObj.currentPage = currpage;
    	searchObj.sid = $.trim($("#sid").val());
    	searchObj.mobile = $.trim($("#mobile").val());
    	searchObj.startDate = $.trim($("#startDate").val());
    	searchObj.endDate = $.trim($("#endDate").val());
    	
        table.render({
            id:'dataTable'
            ,elem: '#dataTable'
            ,cellMinWidth: 50
            , cols: [[
                  {type:'numbers'}
	              , {field: 'sid', title: 'sid'}
	              , {field: 'mobile', title: '手机号码'}
	              , {field: 'createTime', title: '发送时间', width:200, templet: '#date_format', sort: true}
	              , {field: 'status', title: '发送状态', templet: '#send_status_des', sort: true}
	              , {field: 'receiveStatus', title: '回执状态', sort: true, templet: '#deliver_status_des'}
	              , {field: 'content', title: '短信内容', width : 400}
	          ]]
            ,url: server_domain + "/sms/send/page"
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
                currpage = curr;
                layer.close(index);
            }
        });
    };

    var reloadList = function() {
    	searchObj.currentPage = currpage;
    	
        if(searchObj.status){
            searchObj.status = searchObj.status=='0'?"":searchObj.status;
        }
        table.reload('dataTable', {
            where: searchObj
            ,page: {
                curr: currpage
            }
        });
    };


    loadTableData();

    $('#openSearch').bind('click',function(){
        $('.layui-advance-search').toggle(300);
    });

    form.on('submit(search)', function(data) {
        searchObj = data.field;
        currpage = 1;
        reloadList();
        return false;
    });

});