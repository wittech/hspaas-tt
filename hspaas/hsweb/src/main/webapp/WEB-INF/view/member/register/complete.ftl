<!DOCTYPE HTML>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta content="" name="description">
	<title>华时融合平台-用户注册-注册完成</title>
	<#include "/common/assets.ftl"/>
	<#include "/common/validator.ftl"/>
</head>
<body>
	<#include "/common/header.ftl"/>
	<div class="reg_page ">
		<div class="inner">
			<div class="reg_box reg_step1_box">
				<div class="step_box">
					<div class="tit">华时通行证注册</div>
					<ul>
						<li class="li_1 on">
							验证邮箱
						</li>
						<li class="li_2 on">
							信息提交
						</li>
						<li class="li_3 on">
							完成注册
						</li>
					</ul>
				</div>
				<div class="message_box message_sended_box message_succ">
					<div class="d1">
						恭喜您，<span style="font-size: 24px;color: #319acc;margin:">${(email)!}</span>，注册完成！<br/><br/>
						欢迎您注册成为华时融合平台的开发者！如您在使用过程中有任何疑问请联系我们。
					</div>
					<div class="d3">
						<a href="${rc.contextPath}/login" class="">立即登录</a>
					</div>
				</div>
			</div>
		</div>
	</div>
	<#include "/common/footer.ftl"/>
</body>
</html>