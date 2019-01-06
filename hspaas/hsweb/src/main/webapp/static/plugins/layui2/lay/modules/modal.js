/**

 @Name：layuiAdmin 公共业务
 @Author：贤心
 @Site：http://www.layui.com/admin/
 @License：LPPL

 */

layui.define(function(exports){
    var $ = layui.$
        ,layer = layui.layer
        ,admin = layui.admin;

    /** 消息状态码 */
    var web_status = {
        SUCCESS: 0,
        FAIL: 500
    };

    /** 弹窗状态码 */
    var modal_status = {
        SUCCESS: "success",
        FAIL: "error",
        WARNING: "warning"
    };

    // 弹出层封装处理
    var modal = {
        // 校验封装处理
        // 判断返回标识是否唯一 false 不存在 true 存在
        unique: function (value) {
            if (value == "0") {
                return true;
            }
            return false;
        },
        // 判断字符串是否为空
        isEmpty: function (value) {
            if (value == null || this.trim(value) == "") {
                return true;
            }
            return false;
        },
        // 判断一个字符串是否为非空串
        isNotEmpty: function (value) {
            return !modal.isEmpty(value);
        },
        // 是否显示数据 为空默认为显示
        visible: function (value) {
            if (modal.isEmpty(value) || value == true) {
                return true;
            }
            return false;
        },
        // 空格截取
        trim: function (value) {
            if (value == null) {
                return "";
            }
            return value.toString().replace(/(^\s*)|(\s*$)|\r|\n/g, "");
        },
        // 指定随机数返回
        random: function (min, max) {
            return Math.floor((Math.random() * max) + min);
        },
        // 显示图标
        icon: function(type) {
            var icon = "";
            if (type == modal_status.WARNING) {
                icon = 0;
            } else if (type == modal_status.SUCCESS) {
                icon = 1;
            } else if (type == modal_status.FAIL) {
                icon = 2;
            } else {
                icon = 3;
            }
            return icon;
        },
        // 消息提示
        msg: function(content, type) {
            if (type != undefined) {
                layer.msg(content, { icon: modal.icon(type), time: 1000, shift: 5 });
            } else {
                layer.msg(content);
            }
        },
        // 错误消息
        msgError: function(content) {
            modal.msg(content, modal_status.FAIL);
        },
        // 成功消息
        msgSuccess: function(content) {
            modal.msg(content, modal_status.SUCCESS);
        },
        // 警告消息
        msgWarning: function(content) {
            modal.msg(content, modal_status.WARNING);
        },
        // 弹出提示
        alert: function(content, type) {
            layer.alert(content, {
                icon: modal.icon(type),
                title: "系统提示",
                btn: ['确认'],
                btnclass: ['btn btn-primary'],
            });
        },
        // 消息提示并刷新父窗体
        msgReload: function(msg, type) {
            layer.msg(msg, {
                    icon: modal.icon(type),
                    time: 500,
                    shade: [0.1, '#8F8F8F']
                },
                function() {
                    modal.reload();
                });
        },
        // 错误提示
        alertError: function(content) {
            modal.alert(content, modal_status.FAIL);
        },
        // 成功提示
        alertSuccess: function(content) {
            modal.alert(content, modal_status.SUCCESS);
        },
        // 警告提示
        alertWarning: function(content) {
            modal.alert(content, modal_status.WARNING);
        },
        // 关闭窗体
        close: function () {
            var index = parent.layer.getFrameIndex(window.name);
            parent.layer.close(index);
        },
        // 确认窗体
        confirm: function (content, callback) {
            layer.confirm(content, {
                icon: 3,
                title: "系统提示",
                btn: ['确认', '取消']
            }, function (index) {
                layer.close(index);
                callback(true);
            });

        },
        // 弹出层指定宽度
        open: function (title, url, width, height, callbackOptions) {
            //如果是移动端，就使用自适应大小弹窗
            if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                width = 'auto';
                height = 'auto';
            }
            if (modal.isEmpty(title)) {
                title = false;
            };
            if (modal.isEmpty(url)) {
                url = "/404.html";
            };
            if (modal.isEmpty(width)) {
                width = 800;
            };
            if (modal.isEmpty(height)) {
                height = ($(window).height() - 50);
            };
            layer.open({
                type: 2,
                maxmin: true,
                shade: 0.3,
                title: title,
                fix: false,
                area: [width + 'px', height + 'px'],
                content: url,
                shadeClose: true,
                btn: ['确定', '关闭'],
                yes: function (index, layero) {
                    var iframeWindow = window['layui-layer-iframe'+ index]
                        ,submitID = 'LAY-btn-submit'
                        ,submit = layero.find('iframe').contents().find('#'+ submitID);

                    //监听提交
                    
                    iframeWindow.layui.form.on('submit('+ submitID +')', function(data){
                        var field = data.field; //获取提交的字段
                        if(callbackOptions){
                            if(callbackOptions.action){
                                $.ajax({
                                    url: callbackOptions.action
                                    ,data: field
                                    ,type: callbackOptions.method
                                    ,beforeSend : function() {
                                    	callbackOptions.beforeSend();
                                    }
                                    ,success: function(data){
                                        callbackOptions.callback(data);
                                        layer.close(index);//关闭弹层
                                    },error : function() {
            	                    	modal.msgError("服务请求异常");
            	                    }
                                });
                            }else {
                                callbackOptions.callback();
                                layer.close(index);//关闭弹层
                            }
                        }else{
                            layer.close(index);//关闭弹层
                        }
                    });
                    submit.trigger('click');
                }, cancel: function () {
                    return true;
                }
            });
        },
        // 弹出层指定宽度
        openNobtn: function (title, url, width, height, callback) {
            //如果是移动端，就使用自适应大小弹窗
            if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                width = 'auto';
                height = 'auto';
            }
            if (modal.isEmpty(title)) {
                title = false;
            };
            if (modal.isEmpty(url)) {
                url = "/404.html";
            };
            if (modal.isEmpty(width)) {
                width = 800;
            };
            if (modal.isEmpty(height)) {
                height = ($(window).height() - 50);
            };
            layer.open({
                type: 2,
                maxmin: true,
                shade: 0.3,
                title: title,
                fix: false,
                area: [width + 'px', height + 'px'],
                content: url,
                shadeClose: true,
                btn: null,
                yes: function (index, layero) {
                    return true;
                }, cancel: function () {
                    if(callback){
                        callback();
                    }
                    return true;
                }
            });
        },
        // 右侧弹出
        popupRight: function (title, id, width, callback) {
            if (modal.isEmpty(title)) {
                title = false;
            };
            if (modal.isEmpty(width)) {
                width = 300;
            };
            return layer.open({
                type: 1
                ,offset: 'r'
                ,title: title
                ,id: 'popupRight'+id
                ,area: width+'px'
                ,content: $('#'+id)
                ,btn: null
                ,closeBtn: false
                ,shade: 0.1
                ,anim: -1
                ,shadeClose: true
                ,move: false
                ,skin: 'layui-anim layui-anim-rl layui-layer-adminRight2'
                ,yes: function (index, layero) {
                    return true;
                }
                ,cancel: function () {
                    if(callback){
                        callback();
                    }
                    return true;
                }
            });
        },
        // 弹出层全屏
        openFull: function (title, url, width, height, callbackOptions) {
            //如果是移动端，就使用自适应大小弹窗
            if (navigator.userAgent.match(/(iPhone|iPod|Android|ios)/i)) {
                width = 'auto';
                height = 'auto';
            }
            if (modal.isEmpty(title)) {
                title = false;
            };
            if (modal.isEmpty(url)) {
                url = "/404.html";
            };
            if (modal.isEmpty(width)) {
                width = 800;
            };
            if (modal.isEmpty(height)) {
                height = ($(window).height() - 50);
            };
            var index = layer.open({
                type: 2,
                maxmin: true,
                shade: 0.3,
                title: title,
                fix: false,
                area: [width + 'px', height + 'px'],
                content: url,
                shadeClose: true,
                btn: ['确定', '关闭'],
                yes: function (index, layero) {
                    var iframeWindow = window['layui-layer-iframe'+ index]
                        ,submitID = 'LAY-btn-submit'
                        ,submit = layero.find('iframe').contents().find('#'+ submitID);

                    //监听提交
                    
                    
                    iframeWindow.layui.form.on('submit('+ submitID +')', function(data){
                        var field = data.field; //获取提交的字段
                        if(callbackOptions){
                            $.ajax({
                                url: callbackOptions.action
                                ,data: field
                                ,type: callbackOptions.method
                                ,beforeSend : function() {
                                	callbackOptions.beforeSend();
                                }
                                ,success: function(data){
                                    callbackOptions.callback(data);
                                    layer.close(index);//关闭弹层
                                },error : function() {
        	                    	modal.msgError("服务请求异常");
        	                    }
                            });
                        }else{
                            layer.close(index);//关闭弹层
                        }
                    });

                    submit.trigger('click');
                }, cancel: function () {
                    return true;
                }
            });
            layer.full(index);
        },
        // 重新加载
        reload: function () {
            parent.location.reload();
        }
    };

    //对外暴露的接口
    exports('modal', modal);
});