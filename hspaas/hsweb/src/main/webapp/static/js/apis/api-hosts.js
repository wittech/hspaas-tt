/**
 * Created by wanghf on 2017/12/4.
 */
layui.use(['element', 'tree', 'table', 'form'], function() {
    var $ = layui.$,
        layerTips = parent.layer === undefined ? layui.layer : parent.layer,
        layer = layui.layer,
        form = layui.form,
        table = layui.table;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    var userCache = layui.data(userCacheName);
    token = userCache.token;

    //预定义参数
    var addBoxIndex = -1;
    var currpage = 1;

    var parentId = getUrlParam("parentId");
    var currentId = parentId;

    //预定义函数
    var loadLibraryTree = function(){
        $('#libraryTree').html('');//清空
        $.ajax({
            url: server_domain + "/admin/apis/library/tree",
            data: {
                "libUpcode":currentId
            },
            type: "GET",
            success: function (data) {
                if(data.result){
                    layui.tree({
                        elem: '#libraryTree', //传入元素选择器
                        nodes: data.result,
                        click: function(node){
                            parentId = node.navId;
                            if(parentId.length ==6){
                                loadLibraryData();
                                $('#parentDesc').html("["+node.title+"]");
                            }
                        }
                    });
                }
            },
            error: function (data) {
                errorHandle(layerTips,data);
            }
        });
    };

    var loadLibraryData = function() {
        if(parentId == "-1"){
            parentId = "00";
        }

        //执行渲染
        table.render({
            id:'dataTable'
            ,elem: '#data-list'
            ,height: 'full-140'
            ,cellMinWidth: 80
            ,cols: [[
                {field: 'libName', title: '名称', width:260, sort: true}
                ,{field: 'libEngname', title: 'HOST' }
                ,{field: 'libDesc', title: '所属API代码' , width:200}
                ,{fixed: 'right', title: '操作', align:'left', width:270,  toolbar: '#toolbar'}
            ]]
            ,url: server_domain + "/admin/apis/library/subList"
            ,where: {
                "libUpcode": parentId
            }
            ,method: 'GET'
            ,page: true
            ,done: function(res, curr, count){
                currpage = curr;
            }
        });
    };

    var editLibrary = function(title,id){
        if(addBoxIndex != -1)
            return;
        $('#libraryEdit').removeClass("layui-hide");
        addBoxIndex = layer.open({
            type: 1,
            title: title,
            content: $('#libraryEdit'),
            btn: ['保存', '取消'],
            area: ['500px', '360px'],
            shade: bgshade,
            maxmin: true,
            zIndex: layer.zIndex,
            success: function(layero, index) {
                layer.setTop(layero);
                //初始化页面数据
                loadLibraryInfo(layero,id);
                //弹出窗口成功后渲染表单
                var eform = layui.form;
                eform.render();
                eform.on('submit(save)', function(data) {
                    var libId = data.field.libId;
                    var saveUrl = server_domain + "/admin/apis/library/add";
                    if(libId){
                        saveUrl = server_domain + "/admin/apis/library/edit";
                    }
                    $.ajax({
                        url: saveUrl,
                        data: data.field,
                        type: "POST",
                        success: function (data) {
                            layerTips.alert('保存成功', {icon: 1});
                            layer.close(addBoxIndex);
                        },
                        error: function (data) {
                            errorHandle(layerTips,data);
                        }
                    });
                    return false;
                });
            },
            yes: function(index) {
                //触发表单的提交事件
                $('form.layui-form').find('button[lay-filter=save]').click();
            },
            end: function () {
                addBoxIndex = -1;
                $('#libraryEdit').addClass("layui-hide");
                //关闭弹窗后刷新页面数据
                loadLibraryTree()
                loadLibraryData();
            }
        });
    };

    var loadLibraryInfo = function(layero,libId) {
        //初始化参数
        layero.find('input[id=libUpcode]').val('00');
        layero.find('input[id=parentName]').val('业务字典');
        //清空上次数据
        layero.find('input[id=libId]').val("");
        layero.find('input[id=libName]').val("");
        layero.find('input[id=libEngname]').val("");
        layero.find('input[id=libDesc]').val("");
        if(parentId){
            $.ajax({
                url: server_domain + "/admin/apis/library/getByCode",
                data: {
                    "libCode":parentId
                },
                type: "GET",
                async: false,  //这里使用同步请求
                success: function (data) {
                    if(data.result){
                        layero.find('input[id=libUpcode]').val(parentId);
                        if(!libId){
                            layero.find('input[id=libCode]').val(data.result.nextChildCode);
                        }
                        layero.find('input[id=parentName]').val(data.result.libName);
                        layero.find('input[id=libDesc]').val(data.result.libDesc);
                    }
                },
                error: function (data) {
                    errorHandle(layerTips,data);
                }
            });
        }

        if(libId){
            $.ajax({
                url: server_domain + "/admin/apis/library/get",
                data: {
                    "libId":libId
                },
                type: "GET",
                async: false,  //这里使用同步请求
                success: function (data) {
                    if(data.result){
                        layero.find('input[id=libId]').val(data.result.libId);
                        layero.find('input[id=libUpcode]').val(data.result.libUpcode);
                        layero.find('input[id=libCode]').val(data.result.libCode);
                        layero.find('input[id=libName]').val(data.result.libName);
                        layero.find('input[id=libEngname]').val(data.result.libEngname);
                        if(data.result.libIsvalid == 1){
                            layero.find('input[id=libIsvalid]').attr("checked",true);
                        }else{
                            layero.find('input[id=libIsvalid]').attr("checked",false);
                        }
                    }
                    form.render();
                },
                error: function (data) {
                    errorHandle(layerTips,data);
                }
            });
        }
    };

    //加载字典树
    loadLibraryTree();

    //添加字典仅加载一次，不要重复加载
    $('#addLibrary').on('click', function() {
        editLibrary('新增字典',null);
    });

    //批量删除
    $('#delLibrary').on('click', function() {
        var ids = '';
        $('#content').children('tr').each(function() {
            var $that = $(this);
            var $cbx = $that.children('td').eq(0).children('input[type=checkbox]')[0].checked;
            if($cbx) {
                var n = $that.children('td:last-child').children('a[data-opt=edit]').data('id');
                ids += n + ',';
            }
        });
        if(ids == ''){
            layer.msg("请至少选择一条数据", { icon: 2 });
            return;
        }
        $.post(server_domain + "/admin/apis/library/deleteMore",
            { libIds: ids },
            function(data) {
                layer.msg("删除成功");
                loadLibraryData();
            }, "json")
            .error(function(data) {
                errorHandle(layer,data);
            });
    });

    //批量有效
    $('#validLibrary').on('click', function() {
        var ids = '';
        $('#content').children('tr').each(function() {
            var $that = $(this);
            var $cbx = $that.children('td').eq(0).children('input[type=checkbox]')[0].checked;
            if($cbx) {
                var n = $that.children('td:last-child').children('a[data-opt=edit]').data('id');
                ids += n + ',';
            }
        });
        if(ids == ''){
            layer.msg("请至少选择一条数据", { icon: 2 });
            return;
        }
        $.post(server_domain + "/admin/apis/library/validMore",
            { libIds: ids },
            function(data) {
                layer.msg("状态更新成功");
                loadLibraryData();
            }, "json")
            .error(function(data) {
                errorHandle(layer,data);
            });
    });

    table.on('tool(data-list)', function(obj){
        var data = obj.data;
        var event = obj.event;
        if(event === 'edit'){
            var libId = data.libId;
            editLibrary('修改字典', libId);
        }
        else if(event === 'del'){
            layer.confirm('确定要删除该Host吗？', function(index){
                obj.del();
                layer.close(index);
                var libId = data.libId;
                $.ajax({
                    url: server_domain + "/admin/apis/library/delete",
                    data: {
                        "libId":libId
                    },
                    type: "POST",
                    success: function (data) {
                        layerTips.msg("删除成功");
                        loadLibraryTree()
                        loadLibraryData();
                    },
                    error: function (data) {
                        errorHandle(layerTips,data);
                    }
                });
            });
        }
    });
});