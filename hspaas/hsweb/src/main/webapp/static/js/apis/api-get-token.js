/**
 * Created by wanghf on 2017/8/26.
 */
layui.use(['form', 'element'], function() {
    var element = layui.element
        ,form = layui.form
        ,$ = layui.$
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);

    var config = layui.data(configCacheName);

    var contractId = getUrlParam('contractId');
    var account = getUrlParam('account');
    $("#contractId").val(contractId);
    $("#account").val(account);

    var timestamp = parseInt($.now()/1000+(3600 * 24 * 7));
    $("#expireAt").val(timestamp);

    var developerId = config.MixedApideveloperId;
    var authKey = config.MixedApiauthKey;
    var accessKey = config.MixedApiaccessKey;
    if(!isNotEmpty(developerId)){
        layer.alert("请配置开发者ID");
        return false;
    }

    var hosts;
    $.ajax({
        url: server_domain + "/admin/system/library/list?libUpcode=0702",
        data: {},
        type: "GET",
        async: false,
        success: function (data) {
            hosts = data.result;
            $("#hosts").empty();
            for(var j= 0;j<hosts.length;j++){
                var option = $("<option>").val(hosts[j].libEngname).text(hosts[j].libName+"["+hosts[j].libEngname+"]");
                $("#hosts").append(option);
            }
            form.render();
        },
        error: function (data) {
        }
    });
});