<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心-系统管理-消息</title>
<link href="${rc.contextPath}/assets/css/news.css" rel="stylesheet" type="text/css" />


<#include "/common/common.ftl"/>
<script type="text/javascript">
$(function(){
	Pagination.go();
	loadMenu('li_settings','settings','settings_notification');
});
</script>
</head>
<body>
<#include "/common/navigation.ftl"/>
<div class="content">
	<#include "/common/sidebar.ftl"/>
		<!--//左侧side-->
    	<!--右侧main-->
    	<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="javascript:void(0)">系统管理</a></li>
	                <li class="active"><a href="${rc.contextPath}/notice/message/index">消息</a></li>
				</ul>
			</div>
			<form id="list_form" action="${rc.contextPath}/notice/message/list">
				<input type="hidden" name="status" id="status" value="-1">
				<div class="msg_sort">
					<input type="button" value="全部消息" onclick="query('-1','all')" id="all" class="current">
					<input type="button" value="未读消息" onclick="query('0','not')" id="not">
					<input type="button" value="已读消息" onclick="query('1','read')" id="read">
				</div>
				<div class="msg_box">
					<div id="list_container"></div>
				</div>
			</form>
		</div>
</div>
</body>
<script>
	function updateNotice(id,status,obj){
		var _this = $("#"+id);
		if(_this.hasClass('unfold')){
			shrinkage(id);
			_this.removeClass("unfold");
			return false;        
		}
		if(status=="0"){
			accordingTo(id);
			$("#"+id).addClass("unfold");
			$.ajax({
    			url:"/notice/message/read",
    			type:"post",
    			data:"id="+id,
    			dataType: "text",
    			success: function (data) {
    				if(data){
    					status=1;
    				}
              	}
    	  	});
		}else{
			accordingTo(id);
			$("#"+id).addClass("unfold");
		}
     }
     
     //显示
     function accordingTo(id){
     	$("#p_c_"+id).show();
		$("#p_d_"+id).show();
     }
     //隐藏
     function shrinkage(id){
     	$("#p_c_"+id).hide();
		$("#p_d_"+id).hide();
     }
     
     function remove(id){
      	  $.ajax({
    			url:"/notice/message/remove",
    			type:"post",
    			data:"id="+id,
    			dataType: "text",
    			success: function (data) {
    				if(data){
    					$("#"+id).remove();
    				}else{
    					Hs.alert("删除失败！");
    				}
              }
    	  });
     }
     
     function query(type,id){
     	$(".msg_sort input").removeClass("current");
		$("#"+id).attr('class','current');
     	$("#status").val(type);
     	Pagination.go();
     }
            
</script>
</html>