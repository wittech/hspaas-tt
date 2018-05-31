/**
 * Created by wanghf on 2017/12/3.
 */
layui.use(['form', 'element'], function() {
    var element = layui.element //导航的hover效果、二级菜单等功能，需要依赖element模块
        ,form = layui.form
        ,$ = layui.$;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    //监听提交
    form.on('submit(rsaSubmit)', function(data){

        var url = server_domain +'/tools/genRSAKeys';

        layer.load();
        $.ajax({
            type : "POST",
            url : url,
            data: data.field,
            dataType: "json",
            success : function(data){
                $('#publicKey').text(data.result.publicKey);
                $('#privateKey').text(data.result.privateKey);
                layer.closeAll();
            },
            error : function(data) {
                errorHandle(layer,data);
                layer.closeAll();
            }
        });
        return false;
    });

    //监听提交
    form.on('submit(rsaVerify)', function(data){

        var url = server_domain +'/tools/rsaVerify';
        var verifyPublicKey = $('#verifyPublicKey').val();
        var verifyPrivateKey = $('#verifyPrivateKey').val();
        if(verifyPublicKey == '' || verifyPrivateKey == ''){
            layer.alert('请输入公钥和私钥');
            return false;
        }
        layer.load();
        $.ajax({
            type : "POST",
            url : url,
            data: {
                "verifyPublicKey": verifyPublicKey,
                "verifyPrivateKey": verifyPrivateKey
            },
            dataType: "json",
            success : function(data){
                $('#verifyResult').val(data.result);
                layer.closeAll();
            },
            error : function(data) {
                $('#verifyResult').val(data.result);
                layer.closeAll();
            }
        });
        return false;
    });
});