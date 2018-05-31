<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>短信发送 - 短信平台 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="${rc.contextPath}/static/css/global.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="${rc.contextPath}/static/css/form.css" media="all">
    <link rel="stylesheet" href="${rc.contextPath}/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body class="kit-theme">
<div style="margin: 0;">
    <blockquote class="layui-elem-quote">
        <p>短信发送</p>
    </blockquote>
    <form class="layui-form" action="">
        <input type="hidden" id="empId" name="empId">
        <div class="layui-tab layui-tab-card layui-tab-width">
            <div class="layui-tab-content" style="min-height: 230px;">
                <!--- 基本信息 --->
                <div class="layui-tab-item layui-show">
                    <div class="layui-div-left">
                        <div class="layui-form-item">
                            <label class="layui-form-label">手机号码</label>
                            <div class="layui-input-block">
                                <input type="text" id="mobile" name="mobile" lay-verify="multiMobiles" autocomplete="off" placeholder="多个手机号码间以英文 ,号分隔开" class="layui-input">
                            </div>
                        </div>
                        <div>
                            <label class="layui-form-label">短信内容</label>
                            <div class="layui-input-block">
                                <textarea id="content" name="content" placeholder="请输入短信内容，建议发送已报备模板内容" lay-verify="required" class="layui-textarea"></textarea>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="layui-form-button">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="sendSms">发送</button>
                <button type="reset" id="resetForm" class="layui-btn layui-btn-primary">重置</button>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript" src="/static/js/custom_defines.js"></script>
<script type="text/javascript" src="/static/plugins/layui2/layui.js"></script>
<script type="text/javascript" src="/static/js/sms/send_sms.js?v=20171204"></script>
</body>

</html>