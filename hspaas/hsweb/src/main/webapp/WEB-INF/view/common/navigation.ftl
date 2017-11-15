<script type="text/javascript" src="${rc.contextPath}/assets/js/lhuashi-1.0.1.js"></script>
<div class="header">
	<div class="logo"><a href=""><img src="${rc.contextPath}/assets/images/logo2.png" alt="华时融合平台"/></a></div>
	<div class="nav">
		<ul>
			<li><a href="${rc.contextPath}/console">首页</a></li>
			<li><a href="${rc.contextPath}/api/download">下载</a></li>
			<li><a href="${rc.contextPath}/api/development">文档</a></li>
			<li><a href="${rc.contextPath}/api/help">帮助</a></li>
			<li><a href="${rc.contextPath}/api/about">联系</a></li>
		</ul>
		<a href="${rc.contextPath}/user/basis" style="color: #ffffff;margin-left: 340px;;">${(Session["LOGIN_USER_SESSION_KEY"].email)!}</a>
		<div class="header_right">
			<a href="javascript:Hs.logout();" class="log_out" id="logout" title="退出登录"></a>
		</div>
	</div>		
</div>