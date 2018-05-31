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
    var parentId = "99";
    var sysId = "";

    //预定义函数
    var loadInfo = function(parentId, sysId) {
        if(!isNotEmpty(parentId)){
            layer.alert("页面加载无效，请关闭后重新打开", {icon: 2});
            return;
        }
        //加载接口定义
        $.ajax({
            url: server_domain + "/admin/apis/infdefine",
            data: {
                "parentInfId":parentId,
                "sysInfId":sysId
            },
            type: "GET",
            async: false,
            success: function (data) {
                if(data.result){
                    var infSystem = data.result;
                    var infDefine = data.result.infDefine;
                    $('#infId').val(infDefine.infId);
                    $('#parentId').val(infSystem.parentInfId);
                    $('#sysInfId').val(infSystem.infId);
                    $('#systemName').val(infSystem.systemName);
                    $('#systemCode').val(infSystem.systemCode);
                    if(infDefine.newDef){
                        form.render();
                        return;
                    }
                    $('#startVsersion').val(infSystem.startVsersion);
                    $('#startDate').val(infSystem.startDate);
                    $('#infName').val(infSystem.infName);
                    $('#infMethod').val(infDefine.infMethod);
                    $('#infType').val(infDefine.infType);
                    $('#urlParams').val(infDefine.urlParams);
                    $('#requestFile').val(infDefine.requestFile);
                    $('#infDesc').text(infDefine.infDesc);
                    $('#requestCode').text(infDefine.requestCode);
                    $('#responseType').val(infDefine.responseType);
                    $('#responseCode').text(infDefine.responseCode);
                    $('#responseErrorCode').text(infDefine.responseErrorCode);
                    $('#testParams').text(infDefine.testParams);
                    //重置signType
                    var signType = infDefine.signType;
                    var signs = signType.split(",");
                    for (i=0;i<signs.length ;i++ ){
                        $("[name=signType]:checkbox[value="+signs[i]+"]").attr("checked", true);
                    }
                    //加载请求参数
                    getParamsList(infDefine.reqParamsList, 'request');
                    //加载响应参数
                    getParamsList(infDefine.resParamsList, 'response');
                    //加载错误编码列表
                    getErrnoList(infDefine.errnoList);
                    //初始化demo
                    var codeType = $('#codeType').val();
                    if(codeType != ''){
                        loadDemoCode(codeType);
                    }
                }
                form.render();
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var getParamsList = function(list, source) {
        var reqtpl = $('#reqtpl').html();
        var restpl = $('#restpl').html();
        if(list){
            if(source == 'request'){
                laytpl(reqtpl).render(list, function(html){
                    $('#reqContent').html(html);
                    //渲染code和form
                    form.render();
                });
            }else {
                laytpl(restpl).render(list, function(html){
                    $('#resContent').html(html);
                    //渲染code和form
                    form.render();
                });
            }
        }
    };

    var getErrnoList = function(list) {
        var errnotpl = $('#errnotpl').html();
        if(list){
            laytpl(errnotpl).render(list, function(html){
                $('#errnoContent').html(html);
                //渲染code和form
                form.render();
            });
        }
    };

    var idx = -1000;
    var addParamsList = function(source) {
        var list = new Array();
        var param = new Object();
        param.paramId = idx;
        param.paramCode = "";
        param.paramName = "";
        param.paramLength = "";
        param.paramType = "String";
        param.isNeed="1";
        param.paramDefault="";
        param.paramDesc = "";
        param.isNew = true;
        list.push(param);
        var reqtpl = $('#reqtpl').html();
        var restpl = $('#restpl').html();
        if(list){
            if(source == 'request'){
                laytpl(reqtpl).render(list, function(html){
                    $('#reqContent').append(html);
                    //渲染code和form
                    form.render();
                });
            }else {
                laytpl(restpl).render(list, function(html){
                    $('#resContent').append(html);
                    //渲染code和form
                    form.render();
                });
            }
        }
        idx++;
    };

    var saveParams = function(id,source) {
        var prefix = "";
        var param = new Object();
        if(source == 'request'){
            prefix = "req";
        }else {
            prefix = "res";
        }
        param.paramSource = source;
        param.infId = $('#infId').val();
        param.paramId = id;
        param.paramCode = $('#'+prefix+'ParamCode'+id).val();
        param.paramName = $('#'+prefix+'ParamName'+id).val();
        param.paramLength = $('#'+prefix+'ParamLength'+id).val();
        param.paramType = $('#'+prefix+'ParamType'+id).val();
        param.isNeed = $('#'+prefix+'IsNeed'+id)[0].checked?"1":"0";//0-可为空 1-不为空
        param.paramDefault = $('#'+prefix+'ParamDefault'+id).val();
        param.paramDesc = $('#'+prefix+'ParamDesc'+id).val();
        if(param.paramCode == ''){
            layer.alert('参数编码不能为空', { icon: 2 });
            return;
        }
        if(param.paramName == ''){
            layer.alert('中文名称不能为空', { icon: 2 });
            return;
        }
        $.ajax({
            url: server_domain + "/admin/apis/infparams",
            data: param,
            type: "POST",
            success: function (data) {
                layer.msg('保存成功');
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var deleteParams = function(paramId,source) {
        var prefix = "";
        if(source == 'request'){
            prefix = "req";
        }else {
            prefix = "res";
        }
        $.ajax({
            url: server_domain + "/admin/apis/deleteParams",
            data: {
                "paramId":paramId
            },
            type: "POST",
            success: function (data) {
                layer.msg('删除成功');
                $('#'+prefix+'Param'+paramId).remove();
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var saveErrno = function(id) {
        var param = new Object();
        param.infId = $('#infId').val();
        param.errId = id;
        param.httpStatus = $('#httpStatus'+id).val();
        param.errNo = $('#errNo'+id).val();
        param.errMsg = $('#errMsg'+id).val();
        param.errDesc = $('#errDesc'+id).val();
        if(param.errNo == ''){
            layer.alert('错误编码不能为空', { icon: 2 });
            return;
        }
        if(param.errMsg == ''){
            layer.alert('错误信息不能为空', { icon: 2 });
            return;
        }
        $.ajax({
            url: server_domain + "/admin/apis/inferrno",
            data: param,
            type: "POST",
            success: function (data) {
                layer.msg('保存成功');
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var addErrnoList = function() {
        var list = new Array();
        var param = new Object();
        param.errId = idx;
        param.httpStatus = "200";
        param.errNo = "";
        param.errMsg = "";
        param.errDesc = "";
        param.isNew = true;
        list.push(param);
        var errnotpl = $('#errnotpl').html();
        if(list){
            laytpl(errnotpl).render(list, function(html){
                $('#errnoContent').append(html);
                //渲染code和form
                form.render();
            });
        }
        idx++;
    };

    var deleteErrno = function(errId) {
        $.ajax({
            url: server_domain + "/admin/apis/deleteErrno",
            data: {
                "errId":errId
            },
            type: "POST",
            success: function (data) {
                layer.msg('删除成功');
                $('#err'+errId).remove();
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var loadDemoCode = function(codeType) {
        $('#codeContent').text('');
        var infId = $('#infId').val();
        if(infId == ''){
            layer.alert('页面加载无效，请重新加载', {icon: 2});
            return;
        }
        if(codeType){
            //加载接口定义
            $.ajax({
                url: server_domain + "/admin/apis/infdemocode",
                data: {
                    "infId":infId,
                    "codeType":codeType
                },
                type: "GET",
                success: function (data) {
                    if(data.result){
                        $('#codeContent').text(data.result.codeContent);
                    }
                },
                error: function (data) {
                    errorHandle(layer,data);
                }
            });

        }
    };

    var saveDemoCode = function() {
        var infId = $('#infId').val();
        var codeType = $('#codeType').val();
        var codeContent = $('#codeContent').val();
        if(infId == ''){
            layer.alert('页面加载无效，请重新加载', {icon: 2});
            return;
        }
        if(codeType){
            //加载接口定义
            $.ajax({
                url: server_domain + "/admin/apis/infdemocode",
                data: {
                    "infId":infId,
                    "codeType":codeType,
                    "codeContent":codeContent
                },
                type: "POST",
                success: function (data) {
                    layer.msg('保存成功');
                },
                error: function (data) {
                    errorHandle(layer,data);
                }
            });

        }
    };

    var saveTestParams = function() {
        var testParams = $('#testParams').val();
        var infId = $('#infId').val();
        if(infId == ''){
            layer.alert('页面加载无效，请重新加载', {icon: 2});
            return;
        }
        if(testParams == ''){
            layer.alert('请填写测试代码', {icon: 2});
            return;
        }
        $.ajax({
            url: server_domain + "/admin/apis/testParams",
            data: {
                "infId": infId,
                "testParams": testParams
            },
            type: "POST",
            success: function (data) {
                layer.msg('保存成功');
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var userCache = layui.data(userCacheName);
    token = userCache.token;

    parentId = getUrlParam("parentId");
    sysId = getUrlParam("sysId");
    layer.load();

    loadInfo(parentId, sysId);

    layer.closeAll('loading');

    $('#addReqParamBtn').on('click', function() {
        //添加请求参数
        addParamsList('request');
    });

    $('#addResParamBtn').on('click', function() {
        //添加响应参数
        addParamsList('response');
    });

    $('#addErrnoBtn').on('click', function() {
        //添加响应参数
        addErrnoList();
    });

    $('#saveTest').on('click', function() {
        //保存test参数
        saveTestParams();
    });

    $('#saveCode').on('click', function() {
        //保存demo代码
        saveDemoCode();
    });

    form.on('select(codeType)', function(data){
        //加载demo代码
        loadDemoCode(data.value);
    });

    //监听提交
    form.on('submit(saveDefine)', function(data) {
        if(!parentId){
            layer.alert("页面加载无效，请关闭后重新打开", {icon: 2});
            return false;
        }

        //参数签名方式处理
        var RSASignType = $('#RSASignType')[0].checked?$('#RSASignType').val():"";
        var accessKeySignType = $('#accessKeySignType')[0].checked?$('#accessKeySignType').val():"";
        var TOKENSignType = $('#TOKENSignType')[0].checked?$('#TOKENSignType').val():"";
        data.field.signType = RSASignType+ "," + accessKeySignType + "," + TOKENSignType;
        if(data.field.signType.length <= 2){
            layer.alert("请选择参数签名方式", {icon: 2});
            return false;
        }
        data.field.parentInfId = parentId;
        data.field.sysInfId = $('#sysInfId').val();
        $.ajax({
            url: server_domain + "/admin/apis/infdefine",
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

    $('a[data-opt=saveParam]').on('click', function() {
        var paramId = $(this).data('id');
        var type = $(this).data('type');
        saveParams(paramId, type);
    });

    $('a[data-opt=delParam]').on('click', function() {
        var paramId = $(this).data('id');
        var type = $(this).data('type');
        deleteParams(paramId, type);
    });

    $('a[data-opt=saveErrno]').on('click', function() {
        var errId = $(this).data('id');
        saveErrno(errId);
    });

    $('a[data-opt=delErrno]').on('click', function() {
        var errId = $(this).data('id');
        deleteErrno(errId);
    });
});
