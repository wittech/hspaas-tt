<!DOCTYPE html>
<html lang="zh-cn">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>华时融合平台管理中心</title>
	<link href="${rc.contextPath}/assets/css/css.css" rel="stylesheet" type="text/css" />
    <link href="${rc.contextPath}/assets/css/form.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${rc.contextPath}/assets/js/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="${rc.contextPath}/assets/js/common.js"></script>
</head>
<body>
<#include "/common/header.ftl"/>
<div class="content">
	<#include "/common/sidebar.ftl"/>	    
      <!--右侧main-->
		<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="javascript:void(0)">产品管理</a></li>
                    <li class="active"><a href="javascript:void(0)">短信管理</a></li>
				</ul>
			</div>
            
            <div class="main_tab_tit">
				<ul>
					<li onclick="">短信模板</li>
					<li class="active" onclick="">短信测试</li>
				</ul>
			</div>

			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>1、涉及房产、贷款、移民、成人用品、政治、色情、暴力、赌博以及其他违法信息不能发送。</p>
				<p>2、含有病毒、恶意代码、色情、反动等不良信息或有害信息</p>
				<p>3、冒充任何人或机构，或以虚伪不实的的方式谎称或使人误认为与任何人或任何机构有关。</p>
				<p>4、侵犯他人著作权或其他知识产权、或违反保密、雇佣或不披露协议披露他人商业秘密或保密信息。</p>
                <p>5、粗话、脏话等不文明的内容; 让短信接收者难以理解的内容。</p>
                <p>6、主题不明确的模板，如：您好#content#,亲爱的用户#content#</p>
                <p>7、营销、广告类的短信不能发送-这类短信为：通过一些方式（打折，促销等）吸引客户过来参与一些活动，或购买一些产品或服务。</p>
                <p>8、验证码类的短信模板，请务必在获取短信验证之前加 图片验证码。</p>
			</div>
			<!--说明state_box eof-->
			<form id="appForm" name="appForm" action="" method="post" class="">
			<!--添加模板-->
				<div class="edit_box edit_basic">
					<h1>短信测试</h1>
					<div class="edit_ctn">
						

                        <div class="edit_field">
							<dl>
								<dt><i class="star"></i>短信签名</dt>
								<dd><input type="text" id="title" maxlength="20"  name="" value="" placeholder="华时科技">
									<span id="title_error" class="error"></span>
								</dd>							</dl>
						</div>
                        
 
                        <div class="edit_field">
							<dl>
								<dt><i class="star"></i>短信内容</dt>
								<dd class="textarea_type"><textarea>您好，您的短信验证码为#code#,验证码3分钟内有效，请尽快完成操作!</textarea>
									<span id="content_error" class="error"></span>
								</dd>
							</dl>
						</div>
                        
                        <div class="edit_field">
							<dl>
								<dt><i class="star"></i>接收号码</dt>
								<dd><input type="text" id="sign" maxlength="30" disabled name="" value="13718816906">
									<span id="sign_error" class="error"></span>
								</dd>
							</dl>
						</div>
					</div>
				</div>
				<!--添加模板-->
				</form>
				<div class="edit_btn">
					<input type="submit" value="短信验证" class=" bg-yellow">
                    &nbsp;或&nbsp;
					<input type="button" value="语音验证" class=" bg-green">
				</div>
		</div>
		<!--右侧main-->
	 
</div>
</body>
</html>