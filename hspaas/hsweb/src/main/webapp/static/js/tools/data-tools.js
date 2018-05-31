/**
 * Created by wanghf on 2017/12/3.
 */
var configCacheName = 'config';
layui.use(['form', 'element'], function() {
    var element = layui.element //导航的hover效果、二级菜单等功能，需要依赖element模块
        ,form = layui.form
        ,$ = layui.jquery
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    //格式化代码函数,已经用原生方式写好了不需要改动,直接引用就好
    var formatJson = function(json, options) {
        var reg = null,
            formatted = '',
            pad = 0,
            PADDING = '    ';
        options = options || {};
        options.newlineAfterColonIfBeforeBraceOrBracket = (options.newlineAfterColonIfBeforeBraceOrBracket === true) ? true : false;
        options.spaceAfterColon = (options.spaceAfterColon === false) ? false : true;
        if (typeof json !== 'string') {
            json = JSON.stringify(json);
        } else {
            json = JSON.parse(json);
            json = JSON.stringify(json);
        }
        reg = /([\{\}])/g;
        json = json.replace(reg, '\r\n$1\r\n');
        reg = /([\[\]])/g;
        json = json.replace(reg, '\r\n$1\r\n');
        reg = /(\,)/g;
        json = json.replace(reg, '$1\r\n');
        reg = /(\r\n\r\n)/g;
        json = json.replace(reg, '\r\n');
        reg = /\r\n\,/g;
        json = json.replace(reg, ',');
        if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
            reg = /\:\r\n\{/g;
            json = json.replace(reg, ':{');
            reg = /\:\r\n\[/g;
            json = json.replace(reg, ':[');
        }
        if (options.spaceAfterColon) {
            reg = /\:/g;
            json = json.replace(reg, ':');
        }
        (json.split('\r\n')).forEach(function (node, index) {
                var i = 0,
                    indent = 0,
                    padding = '';

                if (node.match(/\{$/) || node.match(/\[$/)) {
                    indent = 1;
                } else if (node.match(/\}/) || node.match(/\]/)) {
                    if (pad !== 0) {
                        pad -= 1;
                    }
                } else {
                    indent = 0;
                }

                for (i = 0; i < pad; i++) {
                    padding += PADDING;
                }

                formatted += padding + node + '\r\n';
                pad += indent;
            }
        );
        return formatted;
    };

    //监听提交
    form.on('submit(json2data)', function(data){

        var url = server_domain +'/tools/encodeData';
        var params = data.field.json_params;
        if(params=='' || params == null){
            layer.alert('输入不能为空');
            return false;
        }

        layer.load();
        $.ajax({
            type : "POST",
            url : url,
            data: {
                "jsonString":params
            },
            dataType: "json",
            success : function(data){
                $('#data_body').text(data.result);
                layer.closeAll();
            },
            error : function(data) {
                $('#data_body').text(data.result);
                layer.closeAll();
            }
        });
        return false;
    });

    form.on('submit(data2json)', function(data){

        var url = server_domain + '/tools/decodeData';
        var params = data.field.data_params;
        if(params=='' || params == null){
            layer.alert('输入不能为空');
            return false;
        }

        layer.load();
        $.ajax({
            type : "POST",
            url : url,
            data: {
                "data":params
            },
            dataType: "json",
            success : function(data){
                $('#json_body').text(data.result);
                layer.closeAll();
            },
            error : function(data) {
                $('#json_body').text(data.result);
                layer.closeAll();
            }
        });
        return false;
    });
});