/**
 * Created by wanghf on 2017/5/3.
 */
layui.use(['element', 'form'], function() {
    var $ = layui.$
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer
        ,form = layui.form;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);

    //预定义函数
    //加载api定义
    var loadInf = function(parentId,sysInfId) {
        //初始化页面参数
        $.ajax({
            url: server_domain + "/admin/apis/infSystem/get",
            data: {
                "sysInfId":sysInfId,
                "parentInfId":parentId
            },
            async: false,
            type: "GET",
            success: function (data) {
                if(data.result){
                    $('#infId').val(data.result.infId);
                    $('#infName').val(data.result.infName);
                }
            },
            error: function (data) {
                errorHandle(layerTips,data);
            }
        });
    };

    //加载api类别
    var loadInfTop = function(){
        $.ajax({
            url: server_domain + "/admin/apis/infTop",
            data: {},
            type: "GET",
            success: function (data) {
                if(data.result){
                    $("#system").empty();
                    for(var i=0; i< data.result.length; i++){
                        var top = data.result[i];
                        $("#system").append("<option value='"+top.id+"'>"+top.name+"</option>");
                        if(i==0){
                            loadInfDir(top.id);
                        }
                    }
                    form.render();
                }
            },
            error: function (data) {
                errorHandle(layerTips,data);
            }
        });
    };

    //加载api接口
    var loadInfDir = function(parentId){
        $.ajax({
            url: server_domain + "/admin/apis/infdir?parentId="+parentId,
            data: {},
            type: "GET",
            success: function (data) {
                if(data.result){
                    $("#targetInfId").empty();
                    for(var i=0; i< data.result.length; i++){
                        var top = data.result[i];
                        $("#targetInfId").append("<option value='"+top.id+"'>"+top.name+"</option>");
                    }
                    form.render();
                }
            },
            error: function (data) {
                errorHandle(layerTips,data);
            }
        });
    };

    var parentId = getUrlParam("parentId");
    var sysInfId = getUrlParam("sysInfId");
    var operType = getUrlParam("operType"); //cp- 复制  mv-移动
    var tips = "复制";
    if(operType == 'cp'){
        $(".copy").show();
        tips = "复制";
    }else{
        tips = "移动";
    }
    $("#submitBtn").html(tips);
    $("#operType").val(operType);
    loadInf(parentId,sysInfId);
    loadInfTop();

    form.on('select(system)', function(data){
        loadInfDir(data.value);
    });

    //监听提交
    form.on('submit(save)', function(data) {
        if(!parentId){
            layer.alert("页面加载无效，请关闭后重新打开", {icon: 2});
            return false;
        }

        //参数签名方式处理
        $.ajax({
            url: server_domain + "/admin/apis/infcopy",
            data: data.field,
            type: "POST",
            success: function (data) {
                layerTips.closeAll();
                layer.msg(tips+'成功');
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
        return false;
    });

});
