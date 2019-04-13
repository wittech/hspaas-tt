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
            	<div style="margin: 6px 0px 6px 3px;">
	                <div class="layui-form-search">
	                    <div class="layui-input-inline">
	                        <input type="text" id="sid" name="sid" placeholder="消息ID" autocomplete="off" class="layui-input">
	                    </div>
	                    <div class="layui-input-inline">
                            <input type="text" id="mobile" name="mobile" placeholder="手机号码" autocomplete="off" class="layui-input">
                        </div>
                        <div class="layui-input-inline">
                            <input type="text" id="startDate" name="startDate" autocomplete="off" class="layui-input" lay-verify="date" value="${(startDate)!}">
                        </div>
                        <div class="layui-input-inline">
                            <input type="text" id="endDate" name="endDate" autocomplete="off" class="layui-input" lay-verify="date" value="${(endDate)!}">
                        </div>
	                    <button lay-filter="search" class="layui-btn" lay-submit><i class="fa fa-search" aria-hidden="true"></i> 查询</button>
	                </div>
	            </div>
            </form>
            <fieldset class="layui-elem-field">
                <div class="layui-field-box layui-form">
                    <table class="layui-hide" id="send-data-table" lay-filter="dataTable"></table>
                    <script type="text/html" id="table-send-query-toolbar"></script>
                    
                    <script type="text/html" id="table-preview-operation">
						 <a class="layui-btn layui-btn-normal layui-btn-xs" lay-event="preview"><i class="layui-icon layui-icon-cellphone-fine"></i>预览</a>
						 {{# if(d.title != null){ }}
						 	{{d.title}}
						  {{# } }}
					</script>
                    
                </div>
            </fieldset>
        </div>
    </div>
    
    <div id="content-preview" style="display: none;"></div>

<script type="text/javascript" src="${rc.contextPath}/static/js/date_format.js"></script>

<script type="text/html" id="date_format">
	{{ getFormatDateByLong(d.createTime, "yyyy-MM-dd hh:mm:ss") }}
</script>

<script type="text/html" id="send_status_des">
	{{#  if(d.status == 0){ }}
	    <span class="layui-badge layui-bg-green">{{ "发送成功" }}</span>
	  {{#  } else { }}
	    <span class="layui-badge">{{ "发送失败" }}</span>
	  {{#  } }}
</script>

<script type="text/html" id="deliver_status_des">
	 {{# if(d.messageDeliver == undefined){ }}
	    <span class="layui-badge layui-bg-orange">{{ "待回执" }}</span>
	  {{# } else if(d.messageDeliver.status == 0){ }}
	    <span class="layui-badge layui-bg-green">{{ "回执成功" }}</span>
	  {{# } else { }}
	    <span class="layui-badge">{{ "回执失败" }}</span>
	  {{# } }}
</script>

<script type="text/javascript" src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/layui.js"></script>
<script type="text/javascript">

	layui.config({
	    base: '${rc.contextPath}/static/plugins/layui2/lay/modules/'
	}).use(['element', 'table', 'form', 'laydate', 'modal'], function() {
	    var table = layui.table
	        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
	        ,layer = layui.layer
	        ,form = layui.form
	        ,element = layui.element
	        ,laydate = layui.laydate
	        ,modal = layui.modal
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
	    
	    var tableId = "send-data-table";
	
	    var loadTableData = function() {
	    	searchObj.currentPage = currpage;
	    	searchObj.sid = $.trim($("#sid").val());
	    	searchObj.mobile = $.trim($("#mobile").val());
	    	searchObj.startDate = $.trim($("#startDate").val());
	    	searchObj.endDate = $.trim($("#endDate").val());
	    	
	        table.render({
	            id:tableId
	            ,elem: '#'+tableId
	            ,cellMinWidth: 50
	            ,toolbar: '#table-send-query-toolbar'
	            , cols: [[
	                  {type:'numbers'}
		              , {field: 'sid', title: 'sid'}
		              , {field: 'mobile', title: '手机号码'}
		              , {field: 'createTime', title: '发送时间', width:200, templet: '#date_format', sort: true}
		              , {field: 'status', title: '发送状态', templet: '#send_status_des', sort: true}
		              , {field: 'receiveStatus', title: '回执状态', sort: true, templet: '#deliver_status_des'}
		              , {field: 'title', title: '彩信标题', width : 400, toolbar: '#table-preview-operation'}
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
	    
	    loadTableData();
	    
	    function preview(sid) {
	    	alert("sss");
	    	$("#content-preview").html("");
	    	$.ajax({
	            url: server_domain+'/mms/template/previewBySid'
	            ,type: 'get'
	            ,dataType:'html'
	            ,data: {
	                "sid": sid
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
	    };
	    
	    //监听行工具事件
	    table.on('tool('+tableId+')', function(obj){
	        var pdata=obj?obj.data:null;
	        if(obj.event === 'preview'){
	        	preview(pdata.sid);
	        }
	        
	    });
	
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