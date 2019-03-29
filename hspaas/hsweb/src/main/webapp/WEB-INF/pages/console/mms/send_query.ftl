<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>彩信发送记录 - 彩信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="/static/css/global.css" media="all">
    <link rel="stylesheet" href="/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="/static/css/table.css" />
    <link rel="stylesheet" href="/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body class="kit-theme">
<div style="margin: 0;">
   
        <div class="admin-main">
            <form class="layui-form" action="">
            <blockquote class="layui-elem-quote">
            	
                <a href="javascript:;" class="layui-btn layui-btn-normal layui-btn-sm" id="export">
                <#--
                    <i class="fa fa-file-excel-o fa-pr5" aria-hidden="true"></i>
                     -->
                </a>
                
                <div style="float:right;">
                    <div class="layui-form-search" style="margin:0;">
                    	<label class="layui-search-label">检索条件</label>
                        <div class="layui-input-inline">
                            <input type="text" id="sid" name="sid" placeholder="消息ID" autocomplete="off" class="layui-input-search">
                        </div>
                        <div class="layui-input-inline">
                            <input type="text" id="mobile" name="mobile" placeholder="手机号码" autocomplete="off" class="layui-input-search">
                        </div>
                        <div class="layui-input-inline">
                            <input type="text" id="startDate" name="startDate" autocomplete="off" class="layui-input-search" lay-verify="date" value="${(startDate)!}">
                        </div>
                        <div class="layui-input-inline">
                            <input type="text" id="endDate" name="endDate" autocomplete="off" class="layui-input-search" lay-verify="date" value="${(endDate)!}">
                        </div>
                        <button lay-filter="search" class="layui-btn layui-btn-sm" lay-submit><i class="fa fa-search" aria-hidden="true"></i> 查询</button>
                    </div>
                </div>
            </blockquote>
            </form>
            <fieldset class="layui-elem-field">
                <div class="layui-field-box layui-form">
                    <table class="layui-hide" id="dataTable" lay-filter="dataTable"></table>
                    <script type="text/html" id="table-send-query-toolbar"></script>
                </div>
            </fieldset>
        </div>
    </div>

<script type="text/javascript" src="${rc.contextPath}/static/js/date_format.js"></script>

<script type="text/html" id="date_format">
	{{ getFormatDateByLong(d.createTime, "yyyy-MM-dd hh:mm:ss") }}
</script>

<script type="text/html" id="send_status_des">
	{{#  if(d.status === 0){ }}
	    <span class="background-color: #5FB878;">{{ "发送成功" }}</span>
	  {{#  } else { }}
	    <span style="background-color: #FF5722; color: #fff;">{{ "发送失败" }}</span>
	  {{#  } }}
</script>

<script type="text/html" id="deliver_status_des">
	 {{# if(d.messageDeliver == undefined){ }}
	    {{ "待回执" }}
	  {{# } else if(d.messageDeliver.status == 0){ }}
	    <span class="background-color: #5FB878;">{{ "回执成功" }}</span>
	  {{# } else { }}
	    <span style="background-color: #FF5722; color: #fff;">{{ "回执失败" }}</span>
	  {{# } }}
</script>

<script type="text/javascript" src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/layui.js"></script>
<script type="text/javascript">
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
    	searchObj.currentPage = currpage;
    	searchObj.sid = $.trim($("#sid").val());
    	searchObj.mobile = $.trim($("#mobile").val());
    	searchObj.startDate = $.trim($("#startDate").val());
    	searchObj.endDate = $.trim($("#endDate").val());
    	
        table.render({
            id:'dataTable'
            ,elem: '#dataTable'
            ,cellMinWidth: 50
            ,toolbar: '#table-send-query-toolbar'
            , cols: [[
                  {type:'numbers'}
	              , {field: 'sid', title: 'sid'}
	              , {field: 'mobile', title: '手机号码'}
	              , {field: 'createTime', title: '发送时间', width:200, templet: '#date_format', sort: true}
	              , {field: 'status', title: '发送状态', templet: '#send_status_des', sort: true}
	              , {field: 'receiveStatus', title: '回执状态', sort: true, templet: '#deliver_status_des'}
	              , {field: 'title', title: '彩信标题', width : 400}
	          ]]
            ,url: server_domain + "/mms/send/page"
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
                currpage = curr;
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
</script>
</body>

</html>