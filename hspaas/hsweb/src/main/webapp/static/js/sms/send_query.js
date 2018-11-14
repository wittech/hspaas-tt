/**
 * Created by wanghf on 2017/5/3.
 */
layui.use(['element', 'tree', 'table', 'form', 'laydate'], function() {
    var table = layui.table
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer
        ,form = layui.form
        ,element = layui.element
        ,laydate = layui.laydate
        ,$ = layui.$;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
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
	              , {field: 'sid', title: 'sid', width: 200}
	              , {field: 'mobile', title: '手机号码', width: 120}
	              , {field: 'createTime', title: '发送时间', width: 180, templet: '#date_format'}
	              , {field: 'status', title: '发送状态', width: 120,
	            	  templet: '<div>{{ d.status == 0 ?"发送成功":"发送失败" }}</div>', sort: true}
//	              , {field: 'messageDeliver', title: '回执时间', width: 180,
//	            	  templet: '<div>{{ d.messageDeliver != undefined ? d.messageDeliver.deliverTime:"--" }}</div>'}
	              , {field: 'receiveStatus', title: '回执状态', width: 120, sort: true,
	            	  templet: '<div>{{ d.messageDeliver == undefined ? "待回执" : (d.messageDeliver.status == 0 ? "回执成功" : "回执失败") }}</div>'}
	              , {field: 'content', title: '短信内容'}
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


    //加载顶级部门下的员工列表
    loadTableData();

    $('#openSearch').bind('click',function(){
        $('.layui-advance-search').toggle(300);
    });

    $('#export').bind('click',function(){
        searchObj.depId = parentId;
        location.href = server_domain + "/admin/report/employee/list";
    });

    form.on('submit(search)', function(data) {
        searchObj = data.field;
        currpage = 1;
        reloadList();
        return false;
    });

});