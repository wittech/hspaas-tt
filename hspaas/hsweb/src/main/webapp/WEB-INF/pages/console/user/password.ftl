<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>修改密码 - 华时融合平台</title>
    <meta name="renderer" content="webkit">
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <link rel="stylesheet" href="/static/plugins/layui2/css/layui.css" media="all" />
    <link rel="stylesheet" href="/static/css/global.css" media="all">
    <link rel="stylesheet" href="/static/plugins/font-awesome/css/font-awesome.min.css">
    <link rel="stylesheet" href="/static/css/form.css" media="all">
    <link rel="stylesheet" href="/static/build/css/themes/default.css" media="all" id="skin" kit-skin />
</head>

<body class="kit-theme">
<div style="margin: 15px;">
    <blockquote class="layui-elem-quote">
        <p>提示：如忘记密码，请联系管理员重置密码</p>
    </blockquote>
    <form class="layui-form">
        <div class="layui-form-item">
            <label class="layui-form-label">旧密码</label>
            <div class="layui-input-block">
                <input type="password" id="oldPwd" lay-verify="password" placeholder="请输入旧密码" name="oldPwd" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">新密码</label>
            <div class="layui-input-block">
                <input type="password" id="newPwd" lay-verify="newPass" placeholder="请输入新密码" name="newPwd" autocomplete="off" class="layui-input">
            </div>
        </div>
        <div class="layui-form-item">
            <label class="layui-form-label">确认密码</label>
            <div class="layui-input-block">
                <input type="password" id="confirmPwd" lay-verify="confirmPwd" placeholder="请再次输入一遍新密码" autocomplete="off" name="confirmPwd" class="layui-input">
            </div>
        </div>
        <div class="layui-form-button">
            <div class="layui-input-block">
                <button class="layui-btn" lay-submit="" lay-filter="save">修改</button>
            </div>
        </div>
    </form>
</div>
<script type="text/javascript" src="/static/js/custom_defines.js"></script>
<script type="text/javascript" src="/static/plugins/layui2/layui.js"></script>
<script type="text/javascript" src="/static/js/user/update_pwd.js?v=20171204"></script>
</body>

</html>