<!DOCTYPE html>
<html lang="zh-cn">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>华时融合平台管理中心</title>
	<link href="${rc.contextPath}/assets/css/css.css" rel="stylesheet" type="text/css" />
    <link href="${rc.contextPath}/assets/css/form.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="${rc.contextPath}/assets/js/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="${rc.contextPath}/assets/js/form.js"></script>
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
					<li><a href="#">应用管理</a></li>
					<li class="active"><a href="#">创建应用</a></li>
				</ul>
			</div>
			
			<form id="appForm" name="appForm" action="" method="post" class="">

				<!--基础配置 bof-->
				<div class="edit_box edit_basic">
					<h1>基础配置</h1>
					<div class="edit_ctn">
						<div class="edit_field">
							<dl>
								<dt><i class="star"></i>应用名称</dt>
								<dd>
									<input type="text" id="appName" name="">
									<span class="error" id="name_error" style="display:none">错误：应用名称不合法</span>
								</dd>
								<input type="hidden" value="" id="happName">
								<dd><span class="tips">当前账户下要求唯一 ，不超过10个汉字或20个字符</span></dd>
							</dl>
						</div>
                        
						<div class="edit_field">
							<dl>
								<dt><i class="star"></i>公司名称</dt>
								<dd><input type="text" id="title" maxlength="20" onblur="checkTitle(this)" name="smsTemplate.name" value="">
									<span id="title_error" class="error"></span>
								</dd>
								<dd><span class="tips">限20字符，模板备注用，只做识别使用</span></dd>
							</dl>
						</div>

						<div class="edit_field">
							<dl>
								<dt><i class="star"></i>应用类型</dt>
								<dd class="select_type">
                                    <select class="select">
                                        <option>网站</option>
                                        <option>移动应用</option>
                                        <option>其他</option>
                                    </select>
								</dd>						
							</dl>
						</div>

						<div class="edit_field">
							<dl>
								<dt><i class="star"></i>所属行业</dt>
								<dd class="select_type">
									<select class="select">
                                        <option>电子商务</option>
                                        <option>金融</option>
                                        <option>在线社区</option>
                                        <option>房地产</option>
                                        <option>医疗</option>
                                        <option>交通汽车</option>
                                        <option>旅游</option>
                                    </select>
								
							  </dd>						
							</dl>
						</div>

						<div class="edit_field">
							<dl>
								<dt>服务器白名单</dt>
								<dd><textarea id="white"></textarea></dd>
								<input type="hidden" name="whiteListStr" id="whiteStr">
								<dd><span class="tips">允许IP地址，以英文输入法分号分隔，例如：8.8.8.8; 8.8.8.8 设定白名单地址后，云之讯服务器在识别该应用请求时将只接收白名单内服务器发送的请求，能有效提升帐号安全性。 如未设置默认不生效</span></dd>
								<dd><span id="wl_error" class="error" style="display:none"></span></dd>
							</dl>
						</div>
					</div>
				</div>
				<!--基础配置 eof-->

				<!--高级配置 bof-->
				<div class="edit_box edit_advanced">
					<h1>高级配置</h1>
					<div class="edit_ctn">
						<div class="edit_field">
									<dl>
										<dt>
											回调地址功能配置
											<span class="link">（了解<a href="">回调地址</a>）</span>
										</dt>
										
										   
											<dd style="padding-bottom: 0px;">
												<h2>
													<input type="checkbox" name="cbfun" value="1">
													呼叫请求
													<span class="ask">
														<i class="ask_icon">&nbsp;</i>
														<em class="ask_ctn">
															涉及纯语音、直拨、回拨业务，若勾选配置此回调地址，云平台会向此地址发起呼叫鉴权请求。(需对语音通话录音时，必须启用呼叫请求)
														</em>
													</span>										
												</h2>
												<div class="set_box" style="">
													<input type="text" value="" name="cbfunurl" placeholder=" 输入回调地址 http://">
													
												</div>
											</dd>
										
											<dd style="padding-bottom: 0px;">
												<h2>
													<input type="checkbox" name="cbfun" value="2">
													呼叫接通 
													<span class="ask">
														<i class="ask_icon">&nbsp;</i>
														<em class="ask_ctn">
															若勾选配置此回调地址，云平台会向此地址发起呼叫建立通知，可主动推送拨通消息到开发者服务器。
														</em>
													</span>										
												</h2>
												<div class="set_box" style="display: none;">
													<input type="text" value="" name="cbfunurl" placeholder=" 输入回调地址 http://">
													<div class="select">
														<span id="zcbfunmethod1" class="select_txt" style="width: 88px;">POST<i>&nbsp;</i></span>
														<ul style="width: 98px;">
															<li onclick="methodOpt('Post','1')" style="width: 88px;">POST</li>
														</ul>
													</div>
												</div>
											</dd>
										
											<dd style="padding-bottom: 0px;">
												<h2>
													<input type="checkbox" name="cbfun" value="3">
													呼叫结束
													<span class="ask">
														<i class="ask_icon">&nbsp;</i>
														<em class="ask_ctn">
															若勾选配置此回调地址，云平台会向此地址发起呼叫挂机通知，可主动推送挂断消息到开发者服务器。
														</em>
													</span>										
												</h2>
												<div class="set_box" style="display: none;">
													<input type="text" value="" name="cbfunurl" placeholder=" 输入回调地址 http://">
													<div class="select">
														<span id="zcbfunmethod2" class="select_txt" style="width: 88px;">POST<i>&nbsp;</i></span>
														<ul style="width: 98px;">
															<li onclick="methodOpt('Post','2')" style="width: 88px;">POST</li>
														</ul>
													</div>
												</div>
											</dd>
										
											<dd style="padding-bottom: 0px;">
												<h2>
													<input type="checkbox" name="cbfun" value="4">
													语音通知回调地址
													<span class="ask">
														<i class="ask_icon">&nbsp;</i>
														<em class="ask_ctn">
															语音通知成功后会回调此地址，将通话状态通知到开发者服务器。
														</em>
													</span>										
												</h2>
												<div class="set_box" style="display: none;">
													<input type="text" value="" name="cbfunurl" placeholder=" 输入回调地址 http://">
													<div class="select">
														<span id="zcbfunmethod3" class="select_txt" style="width: 88px;">POST<i>&nbsp;</i></span>
														<ul style="width: 98px;">
															<li onclick="methodOpt('Post','3')" style="width: 88px;">POST</li>
														</ul>
													</div>
												</div>
											</dd>
										
											<dd style="padding-bottom: 0px;">
												<h2>
													<input type="checkbox" name="cbfun" value="5">
													短信回调地址
													<span class="ask">
														<i class="ask_icon">&nbsp;</i>
														<em class="ask_ctn">
															短信验证码发送成功后会回调此地址，将发送状态通知到开发者服务器。
														</em>
													</span>										
												</h2>
												<div class="set_box" style="display: none;">
													<input type="text" value="" name="cbfunurl" placeholder=" 输入回调地址 http://">
													<div class="select">
														<span id="zcbfunmethod4" class="select_txt" style="width: 88px;">POST<i>&nbsp;</i></span>
														<ul style="width: 98px;">
															<li onclick="methodOpt('Post','4')" style="width: 88px;">POST</li>
														</ul>
													</div>
												</div>
											</dd>
										
											<dd style="padding-bottom: 0px;">
												<h2>
													<input type="checkbox" name="cbfun" value="6">
													语音验证码回调地址
													<span class="ask">
														<i class="ask_icon">&nbsp;</i>
														<em class="ask_ctn">
															语音验证码发送成功后会回调此地址，将语音验证码通话状态通知到开发者服务器。
														</em>
													</span>										
												</h2>
												<div class="set_box" style="display: none;">
													<input type="text" value="" name="cbfunurl" placeholder=" 输入回调地址 http://">
													<div class="select">
														<span id="zcbfunmethod5" class="select_txt" style="width: 88px;">POST<i>&nbsp;</i></span>
														<ul style="width: 98px;">
															<li onclick="methodOpt('Post','5')" style="width: 88px;">POST</li>
														</ul>
													</div>
												</div>
											</dd>
										
											<dd style="padding-bottom: 0px;">
												<h2>
													<input type="checkbox" name="cbfun" value="7">
													通知类业务回调地址
													<span class="ask">
														<i class="ask_icon">&nbsp;</i>
														<em class="ask_ctn">
															涉及漫游呼转离线来电Push续活通知、漫游呼转未接来电通知、漫游关闭通知业务，若勾选配置此回调地址，云平台处理成功后会回调此地址。
														</em>
													</span>										
												</h2>
												<div class="set_box" style="display: none;">
													<input type="text" value="" name="cbfunurl" placeholder=" 输入回调地址 http://">
													<div class="select">
														<span id="zcbfunmethod6" class="select_txt" style="width: 88px;">POST<i>&nbsp;</i></span>
														<ul style="width: 98px;">
															<li onclick="methodOpt('Post','6')" style="width: 88px;">POST</li>
														</ul>
													</div>
												</div>
											</dd>
		                 
									</dl>
						</div>
                        <div class="edit_field">
							<dl>
								<dt>验证最大值</dt>
								<dd><input type="text" id="appSignature" name="app.appSignature"/> 次</dd>
							</dl>
						</div>

					</div>
				</div>
				<!--高级配置 eof-->

				<!--编辑提交按钮 bof-->
				<div class="edit_btn">
					<input type="button" value="保存" class="confirm_btn" id="subi">
					<input type="button" value="取消" class="cancel_btn" onclick="history.go(-1)">
				</div>
				<!--编辑提交按钮 bof-->
			</form>

<!--展开隐藏列表项 bof-->
		<script type="text/javascript">
		function appNameExist(appname,fun){
			$.ajax({
				url:"/app/appNameExist",
				type:"post",
				data:"appName="+appname,
				dataType: "text",
				success: function (data) {
		          if(data>0){
		        	  $("#name_error").fadeIn();
		              $("#name_error").text("应用名称已存在");
		  			  $("#appName").focus();
		  			  $("#happName").val(2);
		          }else{
		          	 $("#name_error").hide();
		          	 $("#happName").val(1);
		          	 if(fun==99){
		          		$("#subi").unbind("click");
		          		$("#subi").val("保存中...");
		          		$("#appForm").submit();
		          	 }
		          }
		        },
		        error: function (msg) {
		        	 $(".form_tips span").text("");
		        }
			});
		}

		</script>
		<!--展开隐藏列表项 eof-->



		</div>
		<!--右侧main-->
        

	 
</div>
</body>
</html>