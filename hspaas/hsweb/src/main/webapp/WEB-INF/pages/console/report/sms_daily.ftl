<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>短信报表统计 - 短信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="${rc.contextPath}/static/css/global.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${rc.contextPath}/static/css/table.css" />
    <link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body class="kit-theme">
<div style="margin: 0;">
   
        <div class="admin-main">
            <form class="layui-form" action="">
            <blockquote class="layui-elem-quote">
                <a href="javascript:;" class="layui-btn layui-btn-normal layui-btn-sm" id="export"></a>
                <div style="float:right;">
                    <div class="layui-form-search" style="margin:0;">
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
                        <button lay-filter="search" class="layui-btn layui-btn-sm" lay-submit><i class="fa fa-search" aria-hidden="true"></i> 查询</button>
                    </div>
                </div>
            </blockquote>
            </form>
            <fieldset class="layui-elem-field">
                <div class="layui-field-box layui-form">
                    <table class="layui-hide" id="dataTable" lay-filter="dataTable"></table>
                </div>
            </fieldset>
        </div>
    </div>

<script type="text/javascript" src="${rc.contextPath}/static/js/custom_defines.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/layui.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/js/sms/sms_daily.js?v=201812225556"></script>
</body>

</html>