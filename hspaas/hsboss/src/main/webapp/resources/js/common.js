/**
 * Created by youngmeng on 2016/12/28.
 */
function commonClearForm(){
    var inputs = $('input');
    for(var i = 0;i<inputs.length;i++){
        var input = $(inputs[i]);
        input.val('');
    }
    var selects = $('select');
    for(var i = 0;i<selects.length;i++){
        var select = $(selects[i]);
        var option = select.find('option').eq(0);
        select.val(option.attr('value'));
    }
}

var Boss = {};

Boss.alert = function(message){
    $.alert({
        title: '提示',
        content: message
    });
}

Boss.alertToCallback = function(message,callback){
    $.alert({
        title: '提示',
        content: message,
        confirm: function(){
            callback();
        }
    });
}

Boss.confirm = function(message,confirmCallBack){
    $.confirm({
        title: '确认提示',
        content: message,
        confirm: function(){
            confirmCallBack();
        },
        cancel: function(){

        }
    });
}

Boss.confirmAndCancelCallback = function(message,confirmCallBack,cancelCallBack){
    $.confirm({
        title: '确认提示',
        content: message,
        confirm: function(){
            confirmCallBack();
        },
        cancel: function(){
            cancelCallBack();
        }
    });
}
