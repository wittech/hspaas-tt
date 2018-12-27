layui.use(['element', 'tree', 'table', 'form', 'laydate'], function() {
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
    	var index = layer.load(1); //添加laoding,0-2两种方式
    	
    	searchObj.currentPage = currpage;
    	searchObj.startDate = $.trim($("#startDate").val());
    	searchObj.endDate = $.trim($("#endDate").val());
    	
        table.render({
            id:'dataTable'
            ,elem: '#dataTable'
            ,cellMinWidth: 50
            ,totalRow: true
            ,loading: true
            ,cols: [[
                  {type:'numbers'}
                  , {field: 'statDate', title: '日期',  sort: true, totalRowText: '总计'}
	              , {field: 'submitCount', title: '提交数量',  sort: true, totalRow: true}
	              , {field: 'billCount', title: '计费数',  sort: true, totalRow: true}
	              , {field: 'successCount', title: '成功数量',  sort: true, totalRow: true}
	          ]]
            ,url: server_domain + "/report/sms_daily_query"
            ,where: searchObj
            ,method: 'GET'
            ,page: false
            ,even: true
            ,done: function(res, curr, count){
                currpage = curr;
                layer.close(index);
            }
        });
    };
    
    loadTableData();

    var reloadList = function() {
    	searchObj.currentPage = currpage;
    	
        table.reload('dataTable', {
            where: searchObj
            ,page: false
        });
    };

    form.on('submit(search)', function(data) {
        searchObj = data.field;
        currpage = 1;
        reloadList();
        return false;
    });

});