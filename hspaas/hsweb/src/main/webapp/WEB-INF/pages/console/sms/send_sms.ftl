<!DOCTYPE html>
<html lang="en">
<head>
    <title>发送短信 - 华时融合平台</title>
    <#include "/common/assets.ftl"/>
</head>
<body class="body">
	<fieldset class="layui-elem-field layui-field-title" style="margin-top: 20px;">
	    <legend>短信发送</legend>
	</fieldset>

<form class="layui-form" action="">
     <div class="layui-form-item layui-form-text">
        <label class="layui-form-label">手机号码</label>
        <div class="layui-input-block">
            <textarea id="mobile" name="mobile" placeholder="多个手机号码间以英文 ,号分隔开" lay-verify="multiMobiles" class="layui-textarea"></textarea>
        </div>
    </div>
     <div class="layui-form-item layui-form-text">
        <label class="layui-form-label">短信内容
        	<button class="layui-btn layui-btn-mini layui-btn-normal">测试内容</button>
        </label>
        <div class="layui-input-block">
            <textarea id="context" name="content" placeholder="请输入短信内容，建议发送已报备模板内容" lay-verify="required" class="layui-textarea"></textarea>
            
        </div>
    </div>
    <div class="layui-form-item">
        <div class="layui-input-block">
            <button class="layui-btn" lay-submit="" lay-filter="sendSms">立即发送</button>
            <button type="reset" class="layui-btn layui-btn-primary">重置</button>
        </div>
    </div>
</form>

<script src="${rc.contextPath}/assets/layui/layui.js" charset="utf-8"></script>
<script>
    layui.use(['form', 'layedit'], function(){
        var form = layui.form
                ,layer = layui.layer
                ,layedit = layui.layedit;

        // 验证手机号码
        form.verify({
            multiMobiles: function(value){
            	var mobiles = $.trim(value);
				if(mobiles==null || mobiles==undefined || mobiles==""){
					return "请输入手机号码";
				}
				
				var regex = /^(13[0-9]|15[012356789]|166|17[05678]|18[0-9]|14[579]|19[89])[0-9]{8}$/;
				
				var ms = mobiles.split(",");
				for(var i=0; i < ms.length; i++){
					if(!regex.test(ms[i]))
						return "手机号码: [" + ms[i] +"] 格式不正确";
				}
            }
        });

        //监听提交
        form.on('submit(sendSms)', function(data){
            layer.alert(JSON.stringify(data.field), {
                title: '最终的提交信息'
            });
            return false;
        });


    });
</script>
</body>
</html>