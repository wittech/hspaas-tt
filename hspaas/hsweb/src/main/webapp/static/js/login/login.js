/**
 * Created by wanghf on 2017/4/10.
 */
layui.use(['jquery', 'layer', 'form'], function() {
    var layer = layui.layer
        ,form = layui.form
        ,$ = layui.$;

    if (!navigator.cookieEnabled){
        alert('您的浏览器禁用了Cookie,将导致系统无法正常运行，请启用Cookie后再刷新页面');
    }

    //初始化参数
    var refreshImage = function() {
        $.ajax({
            url: server_domain + "/admin/user/image",
            data: null,
            type: "GET",
            success: function (data) {
                if(data.result){
                    var access_token = data.result.accessToken;
                    var image = data.result.captchaImage;
                    image = "data:image/png;base64,"+image;
                    $('#access_token').val(access_token);
                    $('#imageCodeImage').attr("src", image);
                }
            },
            error: function (data) {
                errorHandle(layer,data);
            }
        });
    };

    var loginCache = layui.data(loginCacheName);
    if(loginCache.userName && loginCache.password){
        $('#userName').val(loginCache.userName);
        $('#password').val(loginCache.password);
    }else{
        $('#userName').val('');
        $('#password').val('');
    }

    //初始化时生成图片
    //refreshImage();

    $('.verifyImg').on('click', function() {
        refreshImage();
    });

    $("#imageCode, #password, #userName").keydown(function(event) {
        if (event.keyCode == 13) {
            $(".submit_btn").click();
        }
    });

    $(document).keyup(function (e) {
        if (e.keyCode == 13) {
            $(".submit_btn").click();
        }
    });

    form.on('submit(login)',function(data){
        layer.load();
        var userName = data.field.userName;
        var password = data.field.password;
        //var imageCode = data.field.imageCode;
        var rememberMe = data.field.rememberMe;
        var access_token = data.field.access_token;
        if(loginCache.password === password){
            password = decrypt(password, userName);
        }
        $.ajax({
            url: server_domain+"/admin/user/login",
            data: {
                "userName" : userName
                ,"password" : password
                //,"imageCode" : imageCode
            },
            beforeSend: function(request) {
                //request.setRequestHeader("access_token", access_token);
            },
            headers: {
                "access_token": access_token
            },
            type: "post",
            success: function (data) {
                $('#access_token').val('');
                if(rememberMe){
                    layui.data(loginCacheName, {
                        key: 'userName',value: userName
                    });
                    password = encrypt(password, userName);
                    layui.data(loginCacheName, {
                        key: 'password',value: password
                    });
                }else{
                    layui.data(loginCacheName, {
                        key: 'userName',remove: true
                    });
                    layui.data(loginCacheName, {
                        key: 'password',remove: true
                    });
                }
                //将token记录在缓存中
                layui.data(userCacheName, {
                    key: 'token',value: data.result.token
                });
                layui.data(userCacheName, {
                    key: 'headPortrait',value: data.result.headPortrait
                });
                layui.data(userCacheName, {
                    key: 'userName',value: data.result.userName
                });
                layui.data(userCacheName, {
                    key: 'fullName',value: data.result.fullName
                });
                layui.data(userCacheName, {
                    key: 'homepage',value: data.result.homepage
                });
                //console.log(data);
                location.href='/pages/index.html';
            },
            error: function (data) {
                layer.closeAll();
                //slider.restart();
                //refreshImage();
                errorHandle(layer,data);
            }
        });
        return false;
    });
});

function encrypt(str, pwd) {
    if(pwd == null || pwd.length <= 0) {
        alert("Please enter a password with which to encrypt the message.");
        return null;
    }
    var prand = "";
    for(var i=0; i<pwd.length; i++) {
        prand += pwd.charCodeAt(i).toString();
    }
    var sPos = Math.floor(prand.length / 5);
    var mult = parseInt(prand.charAt(sPos) + prand.charAt(sPos*2) + prand.charAt(sPos*3) + prand.charAt(sPos*4) + prand.charAt(sPos*5));
    var incr = Math.ceil(pwd.length / 2);
    var modu = Math.pow(2, 31) - 1;
    if(mult < 2) {
        alert("Algorithm cannot find a suitable hash. Please choose a different password. \nPossible considerations are to choose a more complex or longer password.");
        return null;
    }
    var salt = Math.round(Math.random() * 1000000000) % 100000000;
    prand += salt;
    while(prand.length > 10) {
        prand = (parseInt(prand.substring(0, 10)) + parseInt(prand.substring(10, prand.length))).toString();
    }
    prand = (mult * prand + incr) % modu;
    var enc_chr = "";
    var enc_str = "";
    for(var i=0; i<str.length; i++) {
        enc_chr = parseInt(str.charCodeAt(i) ^ Math.floor((prand / modu) * 255));
        if(enc_chr < 16) {
            enc_str += "0" + enc_chr.toString(16);
        } else enc_str += enc_chr.toString(16);
        prand = (mult * prand + incr) % modu;
    }
    salt = salt.toString(16);
    while(salt.length < 8)salt = "0" + salt;
    enc_str += salt;
    return enc_str;
}

function decrypt(str, pwd) {
    if(str == null || str.length < 8) {
        alert("A salt value could not be extracted from the encrypted message because it's length is too short. The message cannot be decrypted.");
        return;
    }
    if(pwd == null || pwd.length <= 0) {
        alert("Please enter a password with which to decrypt the message.");
        return;
    }
    var prand = "";
    for(var i=0; i<pwd.length; i++) {
        prand += pwd.charCodeAt(i).toString();
    }
    var sPos = Math.floor(prand.length / 5);
    var mult = parseInt(prand.charAt(sPos) + prand.charAt(sPos*2) + prand.charAt(sPos*3) + prand.charAt(sPos*4) + prand.charAt(sPos*5));
    var incr = Math.round(pwd.length / 2);
    var modu = Math.pow(2, 31) - 1;
    var salt = parseInt(str.substring(str.length - 8, str.length), 16);
    str = str.substring(0, str.length - 8);
    prand += salt;
    while(prand.length > 10) {
        prand = (parseInt(prand.substring(0, 10)) + parseInt(prand.substring(10, prand.length))).toString();
    }
    prand = (mult * prand + incr) % modu;
    var enc_chr = "";
    var enc_str = "";
    for(var i=0; i<str.length; i+=2) {
        enc_chr = parseInt(parseInt(str.substring(i, i+2), 16) ^ Math.floor((prand / modu) * 255));
        enc_str += String.fromCharCode(enc_chr);
        prand = (mult * prand + incr) % modu;
    }
    return enc_str;
}