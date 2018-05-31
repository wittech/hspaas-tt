/**
 * Created by wanghf on 2017/8/21.
 */
layui.use(['form', 'element', 'laytpl', 'code'], function() {
    var element = layui.element
        ,form = layui.form
        ,$ = layui.$
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer
        ,laytpl = layui.laytpl;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    layui.code();

    //预定义函数
    //本地存储中存在，有限读取本地存储，本地存储不存在，则从服务端获取默认配置
    var getDeveloper = function(host, developerId) {
        layer.load();
        $.ajax({
            url: server_domain+"/admin/apis/admin/developer",
            data: {
                "host":host,
                "developerId":developerId
            },
            type: "get",
            success: function (data) {
                layer.closeAll("loading");
                if(data.result){
                    $('#mid').val(data.result.mid);
                    $('#name').val(data.result.name);
                    $('#publicKey').val(data.result.publicKey);
                    $('#pushUrl').val(data.result.pushUrl);
                    $('#sdkUrl').val(data.result.sdkUrl);
                    $('#accessKey').val(data.result.accessKey);
                    $('#smsTemplate').val(data.result.smsTemplate);
                    $('#smsTemplate2').val(data.result.smsTemplate2);
                }
            },
            error: function (data) {
                layer.closeAll("loading");
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
            if(hosts[j]!=null && hosts[j].libUpcode == '0702'){
                var option = $("<option>").val(hosts[j].libEngname).text(hosts[j].libName+"["+hosts[j].libEngname+"]");
                $("#host").append(option);
            }
            form.render();
        }
    }

    //监听提交
    form.on('submit(developer)', function(data){
        layer.load();
        $.ajax({
            type : "POST",
            url : server_domain + "/admin/apis/admin/developer",
            data: data.field,
            dataType: "json",
            success : function(data){
                layer.closeAll("loading");
                if(data.result){
                    if(data.result.errno ==0){
                        layer.msg("更新成功");
                    }else{
                        layer.alert(data.result.errmsg);
                    }
                }
            },
            error : function(data) {
                layer.closeAll("loading");
                errorHandle(layer,data);
            }
        });

        return false;
    });

    form.on('submit(query)', function(data){
        var host = $('#host').val();
        var developerId = $('#developerId').val();
        getDeveloper(host, developerId);
        return false;
    });

    form.on('submit(configs)', function(data){
        layer.load();
        $.ajax({
            type : "POST",
            url : server_domain + "/admin/apis/admin/config",
            data: data.field,
            dataType: "json",
            success : function(data){
                layer.closeAll("loading");
                if(data.result){
                    if(data.result.errno ==0){
                        layer.msg("更新成功");
                    }else{
                        layer.alert(data.result.errmsg);
                    }
                }
            },
            error : function(data) {
                layer.closeAll("loading");
                errorHandle(layer,data);
            }
        });

        return false;
    });
});