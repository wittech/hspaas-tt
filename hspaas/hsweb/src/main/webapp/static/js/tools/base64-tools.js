/**
 * Created by wanghf on 2017/12/3.
 */
var configCacheName = 'config';
layui.use(['form', 'element'], function() {
    var element = layui.element //导航的hover效果、二级菜单等功能，需要依赖element模块
        ,form = layui.form
        ,$ = layui.$;

    //页面皮肤
    var skin = getUrlParam('skin');
    if(!skin){
        skin = layui.data('kit_skin').skin;
    }
    setSkin($, skin);
    
    var md5Instance = CryptoJS.algo.MD5.create();

    var gen_base64 = function(){
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
    };

    var getMd5 = function(){
        var file = document.getElementById("upload_file").files[0];
        var reader = new FileReader();
        //将文件以二进制形式读入页面
        reader.readAsBinaryString(file);
        reader.onload=function(f){
            md5Instance.update(CryptoJS.enc.Latin1.parse(f.target.result));
            var md5Value = md5Instance.finalize();
            $('#file_md5').val(md5Value);
        }
    };

    //监听提交
    form.on('submit(test)', function(data){
        gen_base64();
        getMd5();
        return false;
    });
});