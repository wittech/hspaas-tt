/**
 * Created by wanghf on 2017/5/11.
 */
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