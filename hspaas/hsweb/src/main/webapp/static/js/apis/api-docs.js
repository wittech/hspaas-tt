/**
 * Created by wanghf on 2017/8/21.
 */
var $;
layui.use(['form', 'element', 'laytpl', 'code'], function() {
    var element = layui.element
        ,form = layui.form
        ,layerTips = parent.layer === undefined ? layui.layer : parent.layer
        ,layer = layui.layer
        ,laytpl = layui.laytpl;
    $ = layui.$;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);

    //预定义函数
    var setResponse = function(serverHost, data) {
        $('#res_body').text(formatJson(data));
        if(data.errno != 0){
            return false;
        }
        var response_type = $('#responseType').val();
        if(response_type == 'imagebase64'){
            var rbody = JSON.parse(data);
            $('#imagebase64').attr('src','data:image/png;base64,'+rbody.data.imageData);
        }
        if(response_type == 'image'){
            $('#imageUrl').attr('src',serverHost + '&t='+new Date().getTime());
            return false;
        }
        if(response_type == 'URL'){
            var rbody = JSON.parse(data);
            $('#showUrl').attr('href',rbody.data.url);
            $('#showUrl').html('点击此处打开链接');
            return false;
        }
        if(response_type == 'PDF'){
            window.open(serverHost);
            return false;
        }
    };

    var openToken = function() {
        layerTips.open({
            type: 2,
            title: '获取TOKEN',
            area: ['600px', '420px'],
            btn: ['生成', '取消'],
            shade: bgshade,
            maxmin: true,
            content: 'apis/api-get-token.html?'+$("#urlParams").val(),
            zIndex: layerTips.zIndex,
            success: function(layero){
                layerTips.setTop(layero);
            },
            yes: function(index) {
                //触发表单的提交事件
                var body = layerTips.getChildFrame('body', index);
                var serverHost = body.find('select[id=hosts]').val();

                var requestBody = new Object();
                requestBody.account = body.find('input[id=account]').val();
                requestBody.contractId = body.find('input[id=contractId]').val();
                requestBody.expireAt = body.find('input[id=expireAt]').val();
                getToken(serverHost,JSON.stringify(requestBody))
                layerTips.close(index);
            },
            end: function () {
                var config = layui.data(configCacheName);
                var token = config.token;
                $('#signParams').text(token);
            }
        });
    };

    var getToken = function(serverHost,requestBody) {
        layer.load();
        $.ajax({
            type : "POST",
            url : server_domain + "/admin/apis/getToken",
            data: {
                "host":serverHost,
                "requestBody":requestBody
            },
            dataType: "json",
            success : function(data){
                $('#signParams').text(data.result);
                layer.closeAll();
            },
            error : function(data) {
                layer.closeAll();
                errorHandle(layer,data);
            }
        });
    };

    var id = getUrlParam('code');
    var isTest = getUrlParam('isTest');
    var from = getUrlParam('from');
    var token = getUrlParam('token');

    var getTpl = $('#demo').html();

    if ( typeof(FileReader) === 'undefined' ){
        alert("抱歉，你的浏览器不支持 FileReader，请使用现代浏览器操作！");
    }

    var config = layui.data(configCacheName);

    var hosts = config.hosts;

    if(isTest != 'false' && !isNotEmpty(hosts)){
        layer.alert("您当前没有接口访问权限");
        return false;
    }

    var response_type = 'json';
    var request_file = "false";
    var url = server_domain + "/admin/apis/infview?infId="+id;
    if(from === 'token'){
        url = server_domain + "/token/apis/infview?infId="+id+"&token="+token;
    }
    $.ajax({
        type : "GET",
        url : url,
        data: {},
        dataType: "json",
        async: false,
        success : function(data){
            if(data.result){
                var infDefine = data.result.infDefine;
                var infSystem = data.result;
                infDefine.startVsersion=infSystem.startVsersion;
                infDefine.startDate=infSystem.startDate;
                infDefine.infName=infSystem.infName;
                response_type = infDefine.responseType;
                request_file = infDefine.requestFile;
                infDefine.systemName = infSystem.systemName;
                infDefine.systemCode = infSystem.systemCode;
                var signTypes = new Array();
                var signType = infDefine.signType;
                var signs = signType.split(",");
                for (i=0;i<signs.length ;i++ ){
                    signTypes.push(signs[i]);
                }
                infDefine.signTypes = signTypes;
                laytpl(getTpl).render(infDefine, function(html){
                    $('#mainbody').html(html);
                    //测试页面设置host下拉列表
                    var system = infDefine.systemCode;
                    if(system){
                        $("#hosts").empty();
                        for(var j= 0;j<hosts.length;j++){
                            if(hosts[j] != null && system == hosts[j].libDesc){
                                var option = $("<option>").val(hosts[j].libCode).text(hosts[j].libName+"["+hosts[j].libEngname+"]");
                                $("#hosts").append(option);
                            }
                        }
                    }
                    //测试页面重置signType
                    $("#signType").empty();
                    for (i=0;i<signs.length ;i++ ){
                        var option = $("<option>").val(signs[i]).text(signs[i]);
                        $("#signType").append(option);
                    }
                    //渲染code和form
                    form.render();
                    layui.code({
                        encode: true, //是否转义html标签。默认不开启
                        about: false
                    });
                    $('#upload_file').change(function () {
                        gen_base64();
                        getMd5();
                    });
                    if(isTest === 'false'){
                        $('.test-tab').hide();
                    }
                });
            }
        },
        error : function(data) {
            errorHandle(layer,data);
            return;
        }
    });

    form.on('submit(calcuSign)', function(data){
        var serverHost = $('#hosts').val();
        var infId = $('#infId').val();
        var signType = $('#signType').val();

        var urlParams = $('#urlParams').val();
        var requestBody = $('#requestBody').val();
        if(requestBody && requestBody.indexOf('请替换为') > -1){
            layer.alert("请替换requestBody中需要替换的参数值");
            return false;
        }

        var base64 = '';
        var filemd5 = '';
        if(request_file=='1'){
            base64 = $('#base64_str').val();
            filemd5 = $('#file_md5').val();
            if(base64==''){
                layer.alert('请选择文件');
                return false;
            }
            requestBody = requestBody.replace("file_base64", base64);
            requestBody = requestBody.replace("file_md5", filemd5);
        }
        if(signType == "RSA" || signType == "accessKey"){
            if(serverHost == ''){
                layer.alert("请选择HOST");
                return false;
            }
            layer.load();
            var params = {
                "host":serverHost,
                "infId":infId,
                "signType":signType,
                "urlParams":urlParams,
                "requestBody":requestBody
            };
            $.ajax({
                type : "POST",
                contentType: "application/json; charset=utf-8",
                url : server_domain + "/admin/apis/calcuSign",
                data: JSON.stringify(params),
                dataType: "json",
                success : function(data){
                    $('#signParams').text(data.result);
                    layer.closeAll();
                },
                error : function(data) {
                    layer.closeAll();
                    errorHandle(layer,data);
                }
            });
        }else{
            openToken();
        }
        return false;
    });

    //监听提交
    form.on('submit(test)', function(data){
        var serverHost = $('#hosts').val();
        var infId = $('#infId').val();
        var signType = $('#signType').val();
        var urlParams = $('#urlParams').val();
        var requestBody = $('#requestBody').val();
        if(serverHost == ''){
            layer.alert("请选择HOST");
            return false;
        }
        var base64 = '';
        var filemd5 = '';
        if(request_file=='1'){
            base64 = $('#base64_str').val();
            filemd5 = $('#file_md5').val();
            if(base64==''){
                layer.alert('请选择文件');
                return false;
            }
            requestBody = requestBody.replace("file_base64", base64);
            requestBody = requestBody.replace("file_md5", filemd5);
        }
        var signParams = $('#signParams').val();
        if(!isNotEmpty(signParams)){
            layer.alert("请先计算参数签名");
            return false;
        }

        layer.load();
        var params = {
            "host":serverHost,
            "infId":infId,
            "signParams":signParams,
            "urlParams":urlParams,
            "requestBody":requestBody
        };
        //服务器内部发送http请求
        $.ajax({
            type : "POST",
            contentType: "application/json; charset=utf-8",
            url : server_domain + "/admin/apis/test",
            data: JSON.stringify(params),
            dataType: "json",
            success : function(data){
                layer.closeAll();
                if(data.result.responseBody){
                    $('#res_body').text(formatJson(data.result.responseBody));
                }
                $('#requestUrl').text(data.result.requestUrl);
                if(response_type == 'imagebase64'){
                    var rbody = JSON.parse(data.result.responseBody);
                    $('#imagebase64').attr('src','data:image/png;base64,'+rbody.data.imageData);
                }
                if(response_type == 'image'){
                    $('#imageUrl').attr('src',data.result.requestUrl + '&t='+new Date().getTime());
                    return false;
                }
                if(response_type == 'URL'){
                    var rbody = JSON.parse(data.result.responseBody);
                    $('#showUrl').attr('href',rbody.data.url);
                    $('#showUrl').html('点击此处打开链接');
                    return false;
                }
                if(response_type == 'PDF'){
                    window.open(data.result.requestUrl);
                    return false;
                }
            },
            error : function(data) {
                layer.closeAll();
                errorHandle(layer,data);
            }
        });

        return false;
    });
});

function gen_base64(){
    var file = document.getElementById("upload_file").files[0];
    var reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload=function(f){
        this.result = this.result.substring('base64,');
        var str = this.result;
        str = str.match(/;base64,(\S*)/)[1];
        $('#base64_str').val(str);
        $('#file_size').val(Math.round(reader.result.length/1024*1000)/1000 + " KB");
    };
}

var md5Instance = CryptoJS.algo.MD5.create();
function getMd5(){
    var file = document.getElementById("upload_file").files[0];
    var reader = new FileReader();
    //将文件以二进制形式读入页面
    reader.readAsBinaryString(file);
    reader.onload=function(f){
        md5Instance.update(CryptoJS.enc.Latin1.parse(f.target.result));
        var md5Value = md5Instance.finalize();
        $('#file_md5').val(md5Value);
    }
}

//格式化代码函数,已经用原生方式写好了不需要改动,直接引用就好
function formatJson(json, options) {
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
}

