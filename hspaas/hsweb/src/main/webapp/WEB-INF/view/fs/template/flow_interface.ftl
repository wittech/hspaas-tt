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
                    <li class="active"><a href="javascript:void(0)">流量管理</a></li>
				</ul>
			</div>
            
            <div class="main_tab_tit">
				<ul>
					<li onclick="">单号充值</li>
					<li onclick="">批量充值</li>
                    <li class="active" onclick="">接口充值</li>
				</ul>
			</div>

			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>1、单条充流量</p>
				<p>2、查询余额</p>
				<p>3、查询状态</p>
                <p>4、修改登录密码</p>
                <p>5、<a href="">下载接口文档</a></p>
			</div>
			<!--说明state_box eof-->
			<form id="appForm" name="appForm" action="" method="post" class="">
			<!--添加模板-->
				<div class="edit_box edit_basic">
					<h1>接口充流量</h1>
					<div class="edit_ctn">
                    
                    	<div class="edit_field">
							<dl>
								<dt><i class="star"></i>接口通行证</dt>
								<dd><input type="text" id="title" maxlength="20" value="1441726849302">
									<span id="title_error" class="error"></span>
								</dd>
								<dd><span class="tips">用于API协议鉴权</span></dd>
							</dl>
						</div>
                        <div class="edit_field">
							<dl>
								<dt><i class="star"></i>接口密码</dt>
								<dd><input type="password" id="title" maxlength="20" disabled value="ddcf00e99f7ff4edc7fb480e128d1976">
									<span id="title_error" class="error"></span>
								</dd>
							</dl>
						</div>

                        <div class="edit_field">
							<dl>
								<dt><i class="star"></i>回执报告URL</dt>
								<dd><input type="text" id="title" style="width:400px" value="http://api.tmall.com/liuliang/notify_url.htm">
									<span id="title_error" class="error"></span>
								</dd>
							</dl>
						</div>

					</div>
				</div>
				<!--添加模板-->
				</form>
			<div class="edit_btn">
					<input type="hidden" name="templateId" value="">
					<input type="submit" value="修改URL" class="confirm_btn">
					<input type="button" value="取消" class="cancel_btn" onclick="history.go(-1)">
				</div>
		</div>
		<!--右侧main-->
        

	 
</div>
</body>
</html>