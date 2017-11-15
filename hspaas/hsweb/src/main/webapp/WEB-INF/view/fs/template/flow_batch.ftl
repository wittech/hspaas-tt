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
					<li class="active" onclick="">批量充值</li>
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
					<h1>批量充流量</h1>
					<div class="edit_ctn">
                    
                    
                    	<div class="edit_field">
							<dl>
								<dt><i class="star"></i>手机号码</dt>
								<dd class="textarea_type"><textarea>待充值手机号码列表数据</textarea>
									<span id="content_error" class="error"></span>
								</dd>
                                <dd>
                                <a class="input-file bg-yellow" href="javascript:void(0);">
                                    + 浏览TXT文件
                                    <input size="100" type="file" name="file_txt" id="file_txt">
                                </a>
                                <a class="input-file bg-main" href="javascript:void(0);">
                                    + 浏览Excel文件
                                    <input size="100" type="file" name="file_excel" id="file_excel">
                                </a>
                                <a class="input-file bg-gray" style="border-color:#aaa;font-size:14px;" id="filter_error" href="javascript:void(0);">- 过滤无效手机号码</a>
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
								<dt>充值人数</dt>
								<dd><input type="text" id="sign" maxlength="30" value="2000" disabled style="color:red">
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