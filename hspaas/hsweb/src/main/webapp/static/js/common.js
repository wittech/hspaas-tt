$(function(){
   
    $(".common_gaid li").click(function(){
        var index = $(this).index();
        $(this).find("a").addClass("on");
        $(this).siblings().find("a").removeClass("on");
        $(".j_item").hide().eq(index).show();
    })
    
    $(".qiehuan_modal .hd a").click(function(){
        var index = $(this).index();
        $(this).siblings().removeClass("current");
        $(this).addClass("current");
        $(".qiehuan_modal .inner .bd li").hide().eq(index).fadeIn();
    })

    $(".j_input").focus(function(){
        if($(this).val()==this.defaultValue){
            $(this).val("")
        }
    })

    $(".j_input").blur(function(){
        if($(this).val()==""){
            $(this).val(this.defaultValue)
        }
    })
})

