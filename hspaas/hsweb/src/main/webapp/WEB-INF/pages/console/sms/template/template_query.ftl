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

<body class="kit-theme">
<div style="margin: 0;">
   
        <div class="admin-main">
            <form class="layui-form" action="">
            <blockquote class="layui-elem-quote">
                <a href="javascript:;" class="layui-btn layui-btn-normal layui-btn-sm" id="export">
                </a>
                <div style="float:right;">
                    <div class="layui-form-search" style="margin:0;">
                        <div class="layui-input-inline">
                            <select name="status" lay-filter="status">
                                <option value="-1" selected>请选择审批状态</option>
                                <#if approveStatus??>
                                <#list approveStatus as a>
                                <option value="${(a.value)!}">${(a.title)!}</option>
                                </#list>
                                </#if>
                            </select>
                        </div>
                        <div class="layui-input-inline">
                            <input type="text" id="mobile" name="mobile" placeholder="请输入模板内容" autocomplete="off" class="layui-input-search">
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
					    <a class="layui-btn layui-btn-xs" lay-event="edit"><i class="layui-icon layui-icon-edit"></i>编辑</a>
					    <a class="layui-btn layui-btn-danger layui-btn-xs" lay-event="remove"><i class="layui-icon layui-icon-delete"></i>删除</a>
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
<script type="text/javascript" src="${rc.contextPath}/static/plugins/layui2/modal.js"></script>
<script type="text/javascript" src="${rc.contextPath}/static/js/sms/template_query.js?v=2018122333"></script>
</body>

</html>