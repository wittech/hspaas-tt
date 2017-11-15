<div class="header">
	<div class="inner clearfix">
		<div class="fl header_logo">
			<a href="${rc.contextPath}/"></a>
		</div>
		<div class="fl nav">
			<ul class="clearfix">                              
				<li>
					<a href="${rc.contextPath}/" class="on">首页</a>
				</li>
				<li>
					<a href="javascript:void(0)">产品与服务</a>
					<div class="sub_nav">
							<dl>
								<dt>短信/验证码</dt>
								<dd><a href="${rc.contextPath}/sms">短信通知</a></dd>
								<dd><a href="${rc.contextPath}/sms">短信验证码</a></dd>
							</dl>
							<dl>
								<dt>流量</dt>
								<dd><a href="${rc.contextPath}/flow">流量营销</a></dd>
							</dl>
							<dl>
								<dt>语音</dt>
								<dd><a href="${rc.contextPath}/voice">语音通知</a></dd>
								<dd><a href="${rc.contextPath}/voice">语音验证码</a></dd>
							</dl>
							<dl>
								<dt>微推</dt>
								<dd><a href="${rc.contextPath}/wecp">创业者</a></dd>
								<dd><a href="${rc.contextPath}/wecp">中小企业主</a></dd>
								<dd><a href="${rc.contextPath}/wecp">网店店主</a></dd>
								<dd><a href="${rc.contextPath}/wecp">线下店铺</a></dd>
							</dl>
					</div>
				</li>
				<li>
					<a href="${rc.contextPath}/api/development" id="api">API&文档</a>
				</li>
				<li>
					<a href="${rc.contextPath}/api/help" id="help">帮助支持</a>
				</li>
				<li>
					<a href="${rc.contextPath}/api/about" id="about">关于我们</a>
				</li>
			</ul>
		</div>
		<div class="fr f_fn">
			<div class="t clearfix">
				<#if Session["LOGIN_USER_SESSION_KEY"].email??>
					<a href="${rc.contextPath}/console" class="fl console_link">进入开发者中心</a>
				<#else>
					<a href="${rc.contextPath}/login" class="fl login_link">登录</a>
					<a href="${rc.contextPath}/register/fill_email" class="fl reg_link">注册</a>
				</#if>
			</div>
		</div>
	</div>
</div>
<script>
	function service(id){
		$(".clearfix li a").removeClass("on");
		$('li #'+id).attr("class","on");
	}
</script>