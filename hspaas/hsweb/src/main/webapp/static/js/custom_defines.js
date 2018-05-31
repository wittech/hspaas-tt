/**
 * Created by wanghf on 2017/4/21.
 */
var server_domain = '';

/**
 * 全局缓存表
 * @type {string}
 */
var loginCacheName = 'ls_session_user';
var userCacheName = 'ls_user_info';
var configCacheName = 'dev_config';

/**
 * 登录后的token
 */
var token;

/**
 * 弹框的背景透明度
 * @type {number}
 */
var bgshade = 0.6;

Array.prototype.remove=function(obj)
{
    for(var i=0,n=0;i<this.length;i++)
    {
        if(this[i].id!=obj.id)
        {
            this[n++]=this[i]
        }
    }
    this.length-=1
}

Array.prototype.pushOne=function(obj)
{
    var contain = false;
    for(var i=0; i<this.length; i++)
    {
        if(this[i].id == obj.id)
        {
            contain = true;
            break;
        }
    }
    if(!contain){
        this.push(obj);
    }
}

/**
 * iframe内部发生异常时，如需页面跳转，则需要父页面跳转
 * @param layer
 * @param data
 */
function errorHandle(layer,data){
    if(data.status == 400){
        if(data.responseJSON){
            var code = data.responseJSON.code;
            var message = data.responseJSON.message;
            if(code == 110002){
                layer.alert(message, {icon:2},
                    function(index, layero){
                        window.parent.location.href = "/pages/login.html";
                    }
                );
            }else{
                layer.msg(message, { icon: 2 });
            }
        }else{
            layer.msg(data.status+":"+data.statusText, { icon: 2 });
        }
    }else{
        layer.msg(data.status+":"+data.statusText, { icon: 2 });
    }

}

//获取url中的参数
function getUrlParam(name) {
    var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)"); //构造一个含有目标参数的正则表达式对象
    var r = window.location.search.substr(1).match(reg);  //匹配目标参数
    if (r != null) return unescape(r[2]); return null; //返回参数值
}

/**
 * 选择一个
 * @param layer 父页面的layer
 * @param idObj 父页面用来接收id的input对象
 * @param nameObj 父页面用来接收name的input对象
 * @param url 要打开的页面url
 * @param tips 未选择数据时的提示文字
 * @param width 弹出窗口的宽度
 * @param height 弹出窗口的高度
 */
function chooseOne(layer,url,tips,width,height,callbackFunc) {
    if(url.indexOf('?')>0){
        url = url + "&selectOne=true";
    }else{
        url = url + "?selectOne=true";
    }
    layer.open({
        type: 2,
        content: url,
        btn: ['确定', '取消'],
        area: [width+'px', height+'px'],
        shade: bgshade,
        maxmin: false,
        success: function(layero, index){
        },
        yes: function(index, layero) {
            //触发表单的提交事件
            var body = layer.getChildFrame('body', index);
            var iframeWin = window[layero.find('iframe')[0]['name']];//得到iframe页的窗口对象，执行iframe页的方法：
            var checkedArray = iframeWin.choosedData();//调用子页面的方法，得到子页面返回的ids
            if(checkedArray.length==1){
                var textArry = checkedArray[0].split('|');
                callbackFunc(textArry[0], textArry[1]);
                layer.close(index);
            }else{
                layer.msg(tips,{icon: 2});
            }
        }
    });
}

/**
 * 选择多个
 * @param layer 父页面的layer
 * @param url 要打开的页面url
 * @param tips 未选择数据时的提示文字
 * @param width 弹出窗口的宽度
 * @param height 弹出窗口的高度
 * @param callbackFunc 回调函数
 */
function chooseMore(layer,url,tips,width,height,callbackFunc) {
    if(url.indexOf('?')>0){
        url = url + "&selectOne=false";
    }else{
        url = url + "?selectOne=false";
    }
    layer.open({
        type: 2,
        content: url,
        btn: ['确定', '取消'],
        area: [width+'px', height+'px'],
        shade: bgshade,
        maxmin: false,
        success: function(layero, index){
        },
        yes: function(index, layero) {
            var idArray = new Array();
            var nameArray = new Array();
            //触发表单的提交事件
            var body = layer.getChildFrame('body', index);
            var iframeWin = window[layero.find('iframe')[0]['name']];//得到iframe页的窗口对象，执行iframe页的方法：
            var checkedArray = iframeWin.choosedData();//调用子页面的方法，得到子页面返回的ids
            if(checkedArray && checkedArray.length > 0){
                for(var i=0; i< checkedArray.length; i++ ){
                    var textArry = checkedArray[i].split('|');
                    idArray.push(textArry[0]);
                    nameArray.push(textArry[1]);
                }
                //执行回调
                callbackFunc(idArray,nameArray);
                layer.close(index);
            }else{
                layer.msg(tips,{icon: 2});
            }
        }
    });
}

function checkboxSelect($) {
    var ids = '';
    $('#content').children('tr').each(function() {
        var $that = $(this);
        var $cbx = $that.children('td').eq(0).children('input[type=checkbox]')[0];
        if($cbx.checked) {
            var n = $cbx.value;
            ids += n + ',';
        }
    });
    if(ids.lastIndexOf(',') > 0){
        ids = ids.substr(0,ids.length-1);
    }
    return ids;
}

function isNotEmpty(value) {
    if(typeof(value) == "undefined" ||  value == ''){
        return false;
    }
    return true;
}

function setSkin($, value) {
    if(value){
        var _target = $('link[kit-skin]')[0];
        _target.href = _target.href.substring(0, _target.href.lastIndexOf('/') + 1) + value + _target.href.substring(_target.href.lastIndexOf('.'));
    }
}