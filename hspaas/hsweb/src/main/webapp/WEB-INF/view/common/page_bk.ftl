var Pagination = {
	go : function(currentPage){
		if(currentPage == undefined || currentPage == null)
			currentPage = 1;
		$.ajax({
			url : $("#list_form").attr("action"),
			dataType:"html",
			data:"currentPage=" + currentPage + "&" + $("#list_form").serialize(),
			type : "post",
			beforeSend : function(){
				Pagination.loading();
			},
			success:function(data){
				Pagination.unloading();
				$("#list_container").html(data);
				Pagination.page_bind();
				Pagination.jump_bind();
			},
			error : function(){
				Pagination.unloading();
			}
		});
	},
		
	loading : function() {
		$('#list_container').empty();
		$('#list_container').append("<div style='text-align:center;height:50px;'><img src='${rc.contextPath}/assets/images/loading.gif' /></div>");
	},
	
	unloading : function() {
		$('#list_container').empty();
	},
	
	jump_bind : function(){
		$(".jumper").click(function() {
			$("#jump_page").css({"color":"#888","border":"1px #ccc solid"});
			var jumpPage = $("#jump_page").val();
			if(jumpPage == undefined || jumpPage == null || jumpPage == "" || isNaN(jumpPage)){
				$("#jump_page").css({"color":"#F00","border":"1px solid #F00"});
				return;
			}
			var totalPage = $(this).attr("data-page");
			if(totalPage == undefined)
				return;
			if(parseInt(jumpPage) > parseInt(totalPage)){
				$("#jump_page").css({"color":"#F00","border":"1px solid #F00"});
				return;
			}	
			Pagination.go(jumpPage);
		});
	},
	
	page_bind : function(){
		$("#list_container span[data-name='1.16.1']").click(function() {
			var page = $(this).attr("data-page");
			if(page == undefined)
				return;
			Pagination.go(page);
		});
	}
};

var Pagination={go:function(currentPage){if(currentPage==undefined||currentPage==null)currentPage=1;$.ajax({url:$("#list_form").attr("action"),dataType:"html",data:"currentPage="+currentPage+"&"+$("#list_form").serialize(),type:"post",beforeSend:function(){Pagination.loading()},success:function(data){Pagination.unloading();$("#list_container").html(data);Pagination.page_bind();Pagination.jump_bind()},error:function(){Pagination.unloading()}})},loading:function(){$('#list_container').empty();$('#list_container').append("<div style='text-align:center;height:50px;'><img src='${rc.contextPath}/assets/images/loading.gif' /></div>")},unloading:function(){$('#list_container').empty()},jump_bind:function(){$(".jumper").click(function(){$("#jump_page").css({"color":"#888","border":"1px #ccc solid"});var jumpPage=$("#jump_page").val();if(jumpPage==undefined||jumpPage==null||jumpPage==""||isNaN(jumpPage)){$("#jump_page").css({"color":"#F00","border":"1px solid #F00"});return}var totalPage=$(this).attr("data-page");if(totalPage==undefined)return;if(parseInt(jumpPage)>parseInt(totalPage)){$("#jump_page").css({"color":"#F00","border":"1px solid #F00"});return}Pagination.go(jumpPage)})},page_bind:function(){$("#list_container span[data-name='1.16.1']").click(function(){var page=$(this).attr("data-page");if(page==undefined)return;Pagination.go(page)})}};
eval(function(p,a,c,k,e,d){e=function(c){return(c<a?"":e(parseInt(c/a)))+((c=c%a)>35?String.fromCharCode(c+29):c.toString(36))};if(!''.replace(/^/,String)){while(c--)d[e(c)]=k[c]||e(c);k=[function(e){return d[e]}];e=function(){return'\\w+'};c=1;};while(c--)if(k[c])p=p.replace(new RegExp('\\b'+e(c)+'\\b','g'),k[c]);return p;}('d 2={m:0(4){8(4==c||4==q)4=1;$.G({L:$("#p").n("M"),K:"s",5:"4="+4+"&"+$("#p").H(),I:"F",J:0(){2.e()},z:0(5){2.h();$("#7").s(5);2.v();2.o()},A:0(){2.h()}})},e:0(){$(\'#7\').t();$(\'#7\').y("<r D=\'E-B:C;Y:Z;\'><W X=\'${12.13}/10/11/e.V\' /></r>")},h:0(){$(\'#7\').t()},o:0(){$(".P").u(0(){$("#a").k({"j":"#Q","g":"f #N l"});d 3=$("#a").O();8(3==c||3==q||3==""||T(3)){$("#a").k({"j":"#9","g":"f l #9"});b}d i=$(x).n("5-6");8(i==c)b;8(w(3)>w(i)){$("#a").k({"j":"#9","g":"f l #9"});b}2.m(3)})},v:0(){$("#7 U[5-R=\'1.S.1\']").u(0(){d 6=$(x).n("5-6");8(6==c)b;2.m(6)})}};',62,66,'function||Pagination|jumpPage|currentPage|data|page|list_container|if|F00|jump_page|return|undefined|var|loading|1px|border|unloading|totalPage|color|css|solid|go|attr|jump_bind|list_form|null|div|html|empty|click|page_bind|parseInt|this|append|success|error|align|center|style|text|post|ajax|serialize|type|beforeSend|dataType|url|action|ccc|val|jumper|888|name|16|isNaN|span|gif|img|src|height|50px|assets|images|rc|contextPath'.split('|'),0,{}))
