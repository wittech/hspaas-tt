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
<script type="text/javascript">
layui.use(['form', 'element'], function(){
    var $ = layui.$
        ,form = layui.form
        ,layer = layui.layer
        ,laytpl = layui.laytpl
        ,element = layui.element;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    //自定义验证规则
    form.verify({
    	password: function(value){
        	var password = $.trim(value);
			if(password==null || password==undefined || password=="" || password.length < 6 || password.length > 16){
				return "请输入6-16位密码";
			}
        },
        
        newPass: function(value){
        	var newPass = $.trim(value);
			if(newPass==null || newPass==undefined || newPass=="" || newPass.length < 6 || newPass.length > 16){
				return "请输入6-16位密码";
			}
			
			if(newPass == $.trim($("#oldPwd").val())) {
				return "新密码和原密码不能相同";
			}
        },
        
        confirmPwd: function(value){
        	var confirmPwd = $.trim(value);
			if(confirmPwd==null || confirmPwd==undefined || confirmPwd=="" || confirmPwd.length < 6 || confirmPwd.length > 16){
				return "请输入6-16位密码";
			}
			
			if(confirmPwd != $.trim($("#newPwd").val())) {
				return "确认密码和新密码不一致";
			}
        }
    });
    
    //监听提交
    form.on('submit(save)', function(data) {
        $.ajax({
            url: server_domain + "/user/update_password",
            data: data.field,
            type: "POST",
            success: function (data) {
            	if(data) {
            		layer.alert('密码修改成功', {icon: 1});
            	} else {
            		layer.alert('密码修改失败', {icon: 2});
            	}
                
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
        return false;
    });
});
</script>
</body>

</html>