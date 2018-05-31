<div class="layui-header">
	<div class="layui-logo">华时融合平台</div>
	<div class="layui-logo kit-logo-mobile">华时融合平台</div>
	<div class="admin-side-full">
		<i class="fa fa-arrows-alt" aria-hidden="true" title="全屏"></i>
	</div>
	<div class="admin-side-config">
		<i class="fa fa-cog" aria-hidden="true" title="配置开发者信息"></i>
	</div>
	<ul kit-one-level class="layui-nav layui-layout-left kit-nav top-nav">
	</ul>
	<ul class="layui-nav layui-layout-right kit-nav" style="margin-right:20px;">
		<li class="layui-nav-item">
			<a href="javascript:;" id="myAccount">
				<i class="layui-icon">&#xe63f;</i> 短信余额: ${(smsBalance)!"未知"}
			</a>
		</li>
		<li class="layui-nav-item" style="width:170px;">
			<a href="javascript:;">
				<span id="userName">${(Session["LOGIN_USER_SESSION_KEY"].mobile)!'18809099999'}</span>
			</a>
			<dl class="layui-nav-child">
				<dd id="myProfile">
					<a href="javascript:;"><i class="fa fa-user-circle" aria-hidden="true"></i> 个人信息</a>
				</dd>
				<dd id="updatePwd">
					<a href="javascript:;"><i class="fa fa-gear" aria-hidden="true"></i> 修改密码</a>
				</dd>
				<#-- 
				<dd id="lock">
					<a href="javascript:;">
						<i class="fa fa-lock" aria-hidden="true" style="padding-right: 3px;padding-left: 1px;"></i> 锁屏 (Alt+L)
					</a>
				</dd>
				-->
				<dd id="logout">
					<a href="javascript:;" data-url="${rc.contextPath}/logout">
						<i class="fa fa-sign-out" aria-hidden="true"></i> 注销
					</a>
				</dd>
			</dl>
		</li>
	</ul>
</div>