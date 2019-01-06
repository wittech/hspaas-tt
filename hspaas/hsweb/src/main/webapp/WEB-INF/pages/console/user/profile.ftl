<!doctype html>
<html>
<head>
    <meta charset="utf-8">
    <title>我的信息 - 华时融合平台</title>
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
        <p>基本信息</p>
    </blockquote>
    <div class="layui-tab layui-tab-card layui-tab-width">
        <div class="layui-tab-content" style="min-height: 200px;">
            <!--- 基本信息 --->
            <div class="layui-tab-item layui-show">
                <div class="layui-div-left">
                    <div class="layui-form-item">
                        <label class="layui-form-label"><span class="required">登录账号</span></label>
                        <div class="layui-input-block">
                            <input type="text" readonly="readonly" autocomplete="off" class="layui-input" value="${(user.email)!(user.mobile)}">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label"><span class="required">开发者账号</span></label>
                        <div class="layui-input-block">
                            <input type="text" readonly="readonly" autocomplete="off" class="layui-input" value="${(developer.appKey)!}">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">公司名称</label>
                        <div class="layui-input-block">
                            <input type="text" readonly="readonly" autocomplete="off" class="layui-input" value="${(userBase.company)!}">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">注册手机号</label>
                        <div class="layui-input-block">
                            <input type="text" readonly="readonly" autocomplete="off" class="layui-input" value="${(Session["LOGIN_USER_SESSION_KEY"].mobile)!}">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <#--
    <blockquote class="layui-elem-quote">
        <p>短信推送信息</p>
    </blockquote>
    <div class="layui-tab layui-tab-card layui-tab-width">
        <div class="layui-tab-content" style="min-height: 200px;">
            <div class="layui-tab-item layui-show">
                <div class="layui-div-left">
                    <div class="layui-form-item">
                        <label class="layui-form-label"><span class="required">用户账号</span></label>
                        <div class="layui-input-block">
                            <input type="text" readonly="readonly" autocomplete="off" class="layui-input" value="${(user.email)!(user.mobile)}">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">公司名称</label>
                        <div class="layui-input-block">
                            <input type="text" readonly="readonly" autocomplete="off" class="layui-input" value="${(userBase.company)!}">
                        </div>
                    </div>
                    <div class="layui-form-item">
                        <label class="layui-form-label">注册手机号</label>
                        <div class="layui-input-block">
                            <input type="text" readonly="readonly" autocomplete="off" class="layui-input" value="${(Session["LOGIN_USER_SESSION_KEY"].mobile)!}">
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
     -->
    
</div>
<script type="text/javascript" src="/static/js/custom_defines.js"></script>
<script type="text/javascript" src="/static/plugins/layui2/layui.js"></script>
</body>

</html>