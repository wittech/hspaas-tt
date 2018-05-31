<div class="fl sider_nav" style="min-height:100px;">
	<dl>
		<dt>关于我们</dt>
		<dd>	
			<a href="${rc.contextPath}/api/company" id="company">公司介绍</a>
		</dd>
		<dd>	
			<a href="${rc.contextPath}/api/job" id="job">招贤纳士</a>
		</dd>
		<dd>	
			<a href="${rc.contextPath}/api/contact" id="contact">联系我们</a>
		</dd>
	</dl>
</div>
<script>
	function aboutService(id){
		$(".sider_nav dl dd "+id).removeClass("on");
		$('dl dd #'+id).attr("class","on");
	}
</script>