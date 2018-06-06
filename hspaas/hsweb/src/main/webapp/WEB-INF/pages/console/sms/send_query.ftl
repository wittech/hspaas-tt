<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>短信发送记录 - 短信平台 - 华时融合平台</title>
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
                    <i class="fa fa-file-excel-o fa-pr5" aria-hidden="true"></i>
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
                        <button lay-filter="search" class="layui-btn layui-btn-sm" lay-submit><i class="fa fa-search" aria-hidden="true"></i> 查询</button>
                        <a href="javascript:;" id="openSearch" class="layui-btn layui-btn-sm layui-btn-primary">更多条件查询</a>
                    </div>
                </div>
            </blockquote>
            <div class="layui-advance-search">
                    <div class="layui-form-search">
                        <div class="layui-inline">
                            <label class="layui-search-label">开始日期</label>
                            <div class="layui-input-inline">
                                <input type="text" id="startDate" name="startDate" autocomplete="off" class="layui-input-search" value="${(startDate)!}">
                            </div>
                        </div>
                        <div class="layui-inline">
                            <label class="layui-search-label">截止日期</label>
                            <div class="layui-input-inline">
                                <input type="text" id="endDate" name="endDate" autocomplete="off" class="layui-input-search" value="${(endDate)!}">
                            </div>
                        </div>
                        <#--
                        <div class="layui-inline">
                            <label class="layui-search-label">发送状态</label>
                            <div class="layui-input-inline layui-select-search">
                                <select name="status" lay-filter="status">
                                    <option value="0">请选择状态</option>
                                    <option value="1">发送成功</option>
                                    <option value="2">发送失败</option>
                                </select>
                            </div>
                        </div>
                        -->
                    </div>
            </div>
            </form>
            <fieldset class="layui-elem-field">
                <div class="layui-field-box layui-form">
                    <!-- data list box -->
                    <table class="layui-hide" id="dataTable" lay-filter="dataTable"></table>
                    <!-- data list toolbar -->
                    <script type="text/html" id="toolbar">
                        <a href="javascript:;" lay-event="view" class="layui-btn layui-btn-normal layui-btn-xs">预览</a>
                        <a href="javascript:;" lay-event="edit" class="layui-btn layui-btn-xs">修改</a>
                        <a href="javascript:;" lay-event="del" class="layui-btn layui-btn-danger layui-btn-xs">注销</a>
                    </script>
                </div>
            </fieldset>
        </div>
    </div>

<script type="text/javascript" src="/static/js/date_format.js"></script>

<script type="text/html" id="send_status">
	{{ getFormatDateByLong(d.createTime, "yyyy-MM-dd hh:mm:ss") }}
</script>

<script type="text/html" id="deliver_status">
	{{ getFormatDateByLong(d.createTime, "yyyy-MM-dd hh:mm:ss") }}
</script>

<script type="text/html" id="date_format">
	{{ getFormatDateByLong(d.createTime, "yyyy-MM-dd hh:mm:ss") }}
</script>

<script type="text/javascript" src="/static/js/custom_defines.js"></script>
<script type="text/javascript" src="/static/plugins/layui2/layui.js"></script>
<script type="text/javascript" src="/static/js/sms/send_query.js?v=201806030212"></script>
</body>

</html>