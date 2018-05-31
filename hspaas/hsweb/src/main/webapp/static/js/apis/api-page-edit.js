/**
 * Created by wanghf on 2017/4/21.
 */
layui.use(['element', 'layedit', 'form', 'upload', 'code'], function() {
    var $ = layui.$
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer
        ,layedit = layui.layedit
        ,form = layui.form;
    layui.code();

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    //创建一个编辑器
    layedit.set({
        uploadImage: {
            url: server_domain + '/admin/image/upload2',
            type: 'post' //默认post
        }
    });
    var editIndex = layedit.build('LAY_chapterContent_editor');
    form.verify({
        chapterContent: function(value) {
            layedit.sync(editIndex);
        }
    });

    //预定义参数
    var parentId = "99";
    var sysId = "";

    //预定义函数
    var loadInfo = function(parentId, id) {
        $.ajax({
            url: server_domain + "/admin/apis/infchapter",
            data: {
                "parentInfId":parentId,
                "sysInfId":id
            },
            type: "GET",
            success: function (data) {
                if(data.result){
                    $('#chapterId').val(data.result.chapterId);
                    $('#parentId').val(parentId);
                    $('#sysInfId').val(data.result.infSystem.infId);
                    $('#chapterTitle').val(data.result.infSystem.infName);
                    $('#chapterBrief').val(data.result.chapterBrief);
                    $('#LAY_chapterContent_editor').text(data.result.chapterContent);
                    $(window.frames["LAY_layedit_1"].document).find("body").html(data.result.chapterContent);
                }
                form.render();
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var userCache = layui.data(userCacheName);
    token = userCache.token;

    sysId = getUrlParam("sysId");
    parentId = getUrlParam("parentId");

    loadInfo(parentId, sysId);

    //监听提交
    form.on('submit(save)', function(data) {
        if(!parentId){
            layer.alert("页面加载无效，请关闭后重新打开", {icon: 2});
            return false;
        }
        data.field.parentInfId=parentId;
        data.field.sysInfId=$('#sysInfId').val();
        $.ajax({
            url: server_domain + "/admin/apis/infchapter",
            data: data.field,
            type: "POST",
            success: function (data) {
                layer.msg('保存成功');
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
        return false;
    });

});