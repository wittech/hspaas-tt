/**
 * Created by wanghf on 2017/4/21.
 */
layui.use(['element', 'tree','form', 'laytpl'], function() {
    var $ = layui.$
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer
        ,form = layui.form
        ,element = layui.element
        ,laytpl = layui.laytpl;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    //预定义参数
    var addBoxIndex = -1;
    var parentId;

    //预定义函数
    var loadTopApi = function() {
        $.ajax({
            url: server_domain + "/admin/apis/infapi",
            data: {},
            type: "GET",
            success: function (data) {
                if(data.result){
                    var d = data.result;
                    $('#infType').empty();
                    for(var i=0; i < d.length; i++){
                        $('#infType').append("<option value='"+d[i].menuId+"'>"+d[i].menuName+"</option>");
                        if(i==0){
                            loadMenuTree(d[i].menuId);
                        }
                    }
                }
            },
            error: function (data) {
                errorHandle(layerTips,data);
            }
        });
    };

    //加载左侧菜单树，绑定菜单点击事件
    var loadMenuTree = function(){
        $('#navs').html('');
        var code = $("#infType").val();
        $.ajax({
            url: server_domain + "/admin/apis/inftree?parentId="+code,
            data: {},
            type: "GET",
            success: function (data) {
                if(data.result){
                    layui.tree({
                        elem: '#navs', //传入元素选择器
                        nodes: data.result,
                        click: function(node){
                            $('#addMenu').removeClass("layui-hide");
                            $('#addApi').removeClass("layui-hide");
                            $('#addPage').removeClass("layui-hide");
                            parentId = node.navId;
                            loadMenuList(node.navId);
                            $('#parentDesc').html("["+node.title+"]");

                        }
                    });
                }
            },
            error: function (data) {
                errorHandle(layerTips,data);
            }
        });
    };

    //加载子菜单数据列表，初始化分页数据，绑定按钮事件
    var loadMenuList = function(parentid) {
        $.ajax({
            url: server_domain + "/admin/apis/infList",
            data: {
                "parentId": parentid
            },
            type: "GET",
            success: function (data) {
                if(data.result){
                    var tpl = $('#tpl').html();
                    laytpl(tpl).render(data.result, function(html){
                        $('#content').html(html);
                        form.render();
                        form.on('switch(systemDesc)', function(obj){
                            var sysInfId = this.value;
                            var openStatus = 'CLOSE';
                            if(obj.elem.checked){
                                openStatus = 'OPEN';
                            }
                            $.ajax({
                                url: server_domain + "/admin/apis/updateInfOpen",
                                data: {
                                    "sysInfId":sysInfId,
                                    "openStatus":openStatus
                                },
                                type: "POST",
                                success: function (data) {
                                    layer.tips('更新成功', obj.othis);
                                },
                                error: function (data) {
                                    errorHandle(layer,data);
                                }
                            });

                        });
                        $('#content').children('tr').each(function() {
                            var $that = $(this);
                            $that.children('td:last-child').children('a[data-opt=catalog]').on('click', function() {
                                var id  = $(this).data('id');
                                editMenu('修改目录',parentid,id);
                            });
                            $that.children('td:last-child').children('a[data-opt=edit]').on('click', function() {
                                var sysInfId  = $(this).data('id');
                                var defineType  = $(this).data('path');
                                if(defineType == '' || defineType == null){
                                    layer.msg("当前目录没有对应的内容，请新建内容");
                                    return;
                                }else{
                                    //新增
                                    if(defineType == 'docs'){
                                        //接口定义
                                        openDocsEditor(sysInfId,parentid);
                                    }else{
                                        //内容编辑
                                        openPageEditor(sysInfId,parentid);
                                    }
                                }
                            });
                            $that.children('td:last-child').children('a[data-opt=copy]').on('click', function() {
                                var sysInfId  = $(this).data('id');
                                moveOrCopy('复制接口',parentid, sysInfId, 'cp');
                            });
                            $that.children('td:last-child').children('a[data-opt=move]').on('click', function() {
                                var sysInfId  = $(this).data('id');
                                moveOrCopy('移动接口',parentid, sysInfId, 'mv');
                            });
                            $that.children('td:last-child').children('a[data-opt=del]').on('click', function() {
                                var sysInfId  = $(this).data('id');
                                layer.confirm('您确定要删除该接口定义吗?', {icon: 3, title:'提示'}, function(index){
                                    $.ajax({
                                        url: server_domain + "/admin/apis/deleteInf",
                                        data: {
                                            "sysInfId": sysInfId
                                        },
                                        type: "POST",
                                        success: function (data) {
                                            loadMenuTree();
                                            loadMenuList(parentid);
                                        },
                                        error: function (data) {
                                            errorHandle(layerTips,data);
                                        }
                                    });
                                    layer.close(index);
                                });
                            });
                            $that.children('td:nth-last-child(2)').children('a').on('click', function() {
                                var sysInfId  = $(this).data('id');
                                var seq  = $(this).data('seq');
                                var opt  = $(this).data('opt');
                                if(opt=='up'){
                                    if(seq == 1){
                                        layer.msg('到顶了');
                                        return;
                                    }
                                }
                                $.ajax({
                                    url: server_domain + "/admin/apis/updateInfSeq",
                                    data: {
                                        "sysInfId": sysInfId,
                                        "order": opt,
                                        "seq": seq
                                    },
                                    type: "POST",
                                    success: function (data) {
                                        loadMenuTree();
                                        loadMenuList(parentid);
                                    },
                                    error: function (data) {
                                        errorHandle(layerTips,data);
                                    }
                                });
                            });
                        });
                    });
                }
            },
            error: function (data) {
                errorHandle(layerTips,data);
            }
        });
    };

    //打开编辑窗口，需要加载添加页面内容，并重新渲染页面元素
    var editMenu = function(title,parentid,id) {
        if(addBoxIndex != -1)
            return;
        $.get('/pages/apis/api-catalog-edit.html', null, function(mform) {
            //注意这里要用当前layer打开对话框，如果用父layer打开，会导致form.render()失效
            addBoxIndex = layer.open({
                type: 1,
                title: title,
                content: mform,
                btn: ['保存', '取消'],
                area: ['520px', '460px'],
                shade: bgshade,
                maxmin: true,
                zIndex: layerTips.zIndex,
                success: function(layero, index) {
                    layerTips.setTop(layero);
                    //初始化页面数据
                    loadManuInfo(layero,parentid,id);
                    //弹出窗口成功后渲染表单
                    var eform = layui.form;
                    eform.verify({
                        title: function(value) {
                            if(value.length < 4) {
                                return '菜单名称至少得4个字符';
                            }
                        }
                    });
                    eform.render();
                    eform.on('submit(save)', function(data) {
                        if(data.field.systemDesc){
                            data.field.systemDesc = 'OPEN';
                        }else{
                            data.field.systemDesc = 'CLOSE';
                        }
                        $.ajax({
                            url: server_domain + "/admin/apis/infSystem/save",
                            data: data.field,
                            type: "POST",
                            success: function (data) {
                                layer.close(addBoxIndex);
                                layer.msg('保存成功');
                                //加载菜单列表
                                loadMenuList(parentid);
                            },
                            error: function (data) {
                                errorHandle(layer,data);
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
                }
            });
        });
    };

    //加载页面元素默认值
    var loadManuInfo = function(layero,parentId,sysInfId) {
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
                    layero.find('input[id=parentInfId]').val(parentId);
                    layero.find('input[id=systemCode]').val(data.result.systemCode);
                    layero.find('input[id=parentName]').val(data.result.parent.infName);
                    layero.find('input[id=infId]').val(data.result.infId);
                    layero.find('input[id=infName]').val(data.result.infName);
                    layero.find('input[id=seq]').val(data.result.seq);
                    layero.find('input[id=sysId]').val(data.result.sysId);
                    layero.find('input[id=defineId]').val(data.result.defineId);
                    layero.find('input[id=defineType]').val(data.result.defineType);
                    form.render();
                }
            },
            error: function (data) {
                errorHandle(layerTips,data);
            }
        });
    };

    //打开编辑窗口，需要加载添加页面内容，并重新渲染页面元素
    var moveOrCopy = function(title, parentId, sysInfId, operType) {
        layerTips.open({
            type: 2,
            title: title,
            area: ['500px', '400px'],
            shade: bgshade,
            maxmin: true,
            content: 'apis/api-catalog-copy.html?parentId='+parentId+'&sysInfId='+sysInfId+"&operType="+operType,
            zIndex: layerTips.zIndex,
            success: function(layero){
                layerTips.setTop(layero);
            },
            end: function () {
                loadMenuTree();
                loadMenuList(parentId);
            }
        });
    };

    var openPageEditor = function(sysId, parentid) {
        var fullindex = layerTips.open({
            type: 2,
            title: '编辑内容',
            area: ['1000px', '600px'],
            shade: bgshade,
            maxmin: false,
            content: server_domain + "/pages/apis/api-page-edit.html?parentId="+parentid+"&sysId="+sysId,
            zIndex: layerTips.zIndex,
            success: function(layero){
                layerTips.setTop(layero);
            },
            end: function () {
                layerTips.closeAll();
                loadMenuList(parentid);
            }
        });
        layerTips.full(fullindex);
    };

    var openDocsEditor = function(sysId,parentid) {
        var fullindex = layerTips.open({
            type: 2,
            title: '编辑接口',
            area: ['1000px', '600px'],
            shade: bgshade,
            maxmin: false,
            content: server_domain + "/pages/apis/api-docs-edit.html?parentId="+parentid+"&sysId="+sysId,
            zIndex: layerTips.zIndex,
            success: function(layero){
                layerTips.setTop(layero);
            },
            end: function () {
                loadMenuList(parentid);
            }
        });
        layerTips.full(fullindex);
    };

    var userCache = layui.data(userCacheName);
    token = userCache.token;

    //加载菜单树
    loadTopApi();

    //刷新树
    $('#refreshTree').on('click', function() {
        loadMenuTree();
    });

    //添加菜单仅加载一次，不要重复加载
    $('#addMenu').on('click', function() {
        if(parentId){
            editMenu('新增目录',parentId,null);
        }
    });
    $('#addPage').on('click', function() {
        //内容编辑
        openPageEditor('', parentId);
    });
    $('#addApi').on('click', function() {
        //接口定义
        openDocsEditor('', parentId);
    });

    $('#infType').change(function(){
        loadMenuTree();
    })
});