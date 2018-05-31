/**
 * Created by wanghf on 2017/9/18.
 */
layui.use(['form', 'element'], function(){
    var $ = layui.$
        ,form = layui.form
        ,layer = layui.layer
        ,element = layui.element;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    //预定义函数
    //服务端获取默认配置
    var getDefaultDeveloperConfig = function(code){
        layer.load();
        $.ajax({
            url: server_domain+"/admin/apis/config",
            data: {
                "host":code
            },
            type: "get",
            success: function (data) {
                layer.closeAll("loading");
                if(data.result){
                    $('#developerId').val(data.result.developerId);
                    $('#authKey').val(data.result.authKey);
                    $('#accessKey').val(data.result.accessKey);
                }
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var config = layui.data(configCacheName);
    var hosts = config.hosts;

    if(!isNotEmpty(hosts)){
        layer.alert("您当前没有接口访问权限");
        return false;
    }else{
        for(var j= 0;j<hosts.length;j++){
            if(hosts[j] != null){
                var option = $("<option>").val(hosts[j].libCode).text(hosts[j].libName+"["+hosts[j].libEngname+"]");
                $("#host").append(option);
            }
            form.render();
        }
    }

    form.on('select(host)', function(data){
        //加载配置
        getDefaultDeveloperConfig(data.value);
    });

    //监听提交
    form.on('submit(save)', function(data) {
        $.ajax({
            url: server_domain+"/admin/apis/config",
            data: data.field,
            type: "post",
            success: function (data) {
                layer.msg("保存成功!");
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
        return false;
    });
});
