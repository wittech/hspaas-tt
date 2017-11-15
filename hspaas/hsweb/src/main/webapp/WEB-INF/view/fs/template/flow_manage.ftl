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
					<li class="active" onclick="">单号充值</li>
					<li onclick="">批量充值</li>
                    <li onclick="">接口充值</li>
				</ul>
			</div>

			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>1、目前只支持全国流量</p>
				<p>2、流量说明二</p>
				<p>3、流量说明三</p>
			</div>
			<!--说明state_box eof-->
			<form id="appForm" name="appForm" action="" method="post" class="">
			<!--添加模板-->
				<div class="edit_box edit_basic">
					<h1>单号充流量</h1>
					<div class="edit_ctn">
                    
                    
                    	<div class="edit_field">
							<dl>
								<dt><i class="star"></i>手机号码</dt>
								<dd><input type="text" id="title" maxlength="20" onblur="checkTitle(this)" name="smsTemplate.name" value="">
									<span id="title_error" class="error"></span>
								</dd>
							</dl>
						</div>
                        
						<div class="edit_field">
							<dl>
								<dt><i class="star"></i>流量类型</dt>
								<dd class="select_type">
                                    <select class="select">
                                        <option>全国流量</option>
                                        <option>其他</option>
                                    </select>
								</dd>						
							</dl>
						</div>

                        
                        
                        <div class="edit_field">
							<dl>
								<dt><i class="star"></i>充值流量</dt>
								<dd class="select_type">
                                    <select class="select">
                                        <option>50M</option>
                                        <option>100M</option>
                                    </select>
								</dd>						
							</dl>
						</div>
                        

						<div class="edit_field">
							<dl>
								<dt>套餐原价</dt>
								<dd><input type="text" id="sign" maxlength="30" value="10元" disabled style="color:red">
									<span id="sign_error" class="error"></span>
								</dd>
							</dl>
						</div>
                        <div class="edit_field">
							<dl>
								<dt>套餐折扣价</dt>
								<dd><input type="text" id="sign" maxlength="30" value="9.7元" disabled style="color:red">
									<span id="sign_error" class="error"></span>
								</dd>
							</dl>
						</div>
                        <div class="edit_field">
							<dl>
								<dt>当前余额</dt>
								<dd><input type="text" id="sign" maxlength="30" value="9909.84元" disabled style="color:red">
									<span id="sign_error" class="error"></span>
								</dd>
							</dl>
						</div>
                        
                        
					</div>
				</div>
				<!--添加模板-->
				</form>
			<div class="edit_btn">
					<input type="hidden" name="templateId" value="">
					<input type="submit" value="立即充值" class="confirm_btn">
					<input type="button" value="取消" class="cancel_btn" onclick="history.go(-1)">
				</div>
		</div>
		<!--右侧main-->
        

	 
</div>
</body>
</html>