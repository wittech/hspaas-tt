/**
 * Created by wanghf on 2017/4/21.
 */
layui.config({
    base: '/static/js/'
}).use(['element', 'tree', 'form', 'code'], function() {
    var $ = layui.$
        ,element = layui.element
        ,layer = layui.layer;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);

    //预定义函数
    var loadInfo = function(url) {
        if(url){
            $.ajax({
                url: url,
                data: {},
                type: "GET",
                success: function (data) {
                    if(data.result){
                        $('title').text(data.result.infSystem.infName);
                        $('#content').html(data.result.chapterContent);
                        //目录
                        var siteDir = $('.site-dir');
                        if(siteDir[0] && $(window).width() > 750){
                            layer.open({
                                type: 1
                                ,content: siteDir
                                ,skin: 'layui-layer-dir'
                                ,area: 'auto'
                                ,maxHeight: $(window).height() - 300
                                ,title: '目录'
                                //,closeBtn: false
                                ,offset: 'r'
                                ,shade: false
                                ,success: function(layero, index){
                                    layer.style(index, {
                                        marginLeft: -15
                                    });
                                }
                            });
                            siteDir.find('li').on('click', function(){
                                var othis = $(this);
                                othis.find('a').addClass('layui-this');
                                othis.siblings().find('a').removeClass('layui-this');
                            });
                            layui.code({
                                about: false
                            });
                        }
                    }
                },
                error: function (data) {
                    errorHandle(layer,data);
                }
            });
        }
    };

    var code = getUrlParam("code");
    var token = getUrlParam('token');
    var from = getUrlParam('from');
    var url = server_domain + "/admin/apis/viewChapter?sysInfId="+code;
    if(from === 'token'){
        url = server_domain + "/token/apis/viewChapter?sysInfId="+code+"&token="+token;
    }
    loadInfo(url);

    $('a[data-opt=help]').on('click', function() {
        var msg = $(this).data('msg');
        layer.msg(msg);
    });
});