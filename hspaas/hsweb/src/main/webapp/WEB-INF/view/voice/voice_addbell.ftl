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
					<li><a href="">产品管理</a></li>
					<li class="active"><a href="javascript:void(0)">语音管理</a></li>
				</ul>
			</div>
            
            <div class="main_tab_tit">
				<ul>
					<li class="active" onclick="">铃声</li>
					<li onclick="">语音通知</li>
                    <li onclick="">IVR导航</li>
				</ul>
			</div>

			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>选择回拨铃声，每个应用只能上传一个欢迎语音，若重复上传则覆盖上一个语音。选择语音验证码欢迎音、语音通知、IVR导航语音，上传数量不限制。</p>

			</div>
			<!--说明state_box eof-->

			<!--配置-->
			<form id="save" name="save" onsubmit="return checkSubmit(this);" action="" method="post">

			<!--基础配置-->
			<div class="edit_box edit_basic">
				<h1>添加铃声</h1>
				<div class="edit_ctn">
                
                    <div class="edit_field">
                        <dl>
                            <dt><i class="star"></i>语音类型</dt>
                            <dd class="select_type">
                                <select class="select">
                                	<option>请选择应用</option>
                                    <option>回拨铃声</option>
                                    <option>语音验证码</option>
                                </select>
                            </dd>						
                        </dl>
                    </div>
                    
                    <div class="edit_field">
                        <dl>
                            <dt><i class="star"></i>所属应用</dt>
                            <dd class="select_type">
                                <select class="select">
                                    <option>请选择应用</option>
                                    <option>测试DEMO</option>
                                </select>
                            </dd>						
                        </dl>
                    </div>
                    

					<div class="edit_field">
						<dl>
							<dt><i class="star"></i>语音文件</dt>
								<dd><span class="radio"><input type="radio" name="" value="2">文本</span></dd>
								<dd><textarea style="min-width:208px;" id="content"  name="" placeholder="限500字以内"></textarea>
									<span id="content_error" class="error"></span>
								</dd>
								<dd><span class="radio"><input type="radio" name="" checked="checked" value="1">录音文件</span></dd>
								<dd><div class="file file_short"><input type="text" class="file_txt" name="file_txt"><input type="button" value="浏览..." class="file_btn"><input type="file" value="" class="file_1 file_1_short" onblur="checkFile(this)" id="callBackFile" name="voice" ></div>
									<span id="file_error" class="error"></span>
								</dd>
							<dd><span class="tips">1、选择回拨铃声类型，每个应用只能上传一个欢迎语音，若重复上传则覆盖上一个语音。选择语音验证码欢迎音上传个数不限制。<br>2、语音的格式必须是wav或mp3(不支持直接改后缀名)，文件大小不能超过1M。</span></dd>
						</dl>
					</div>
				</div>
			</div>
			<!--基础配置-->

			<!--编辑提交按钮 bof-->
			<div class="edit_btn">
				<input name="id" type="hidden" value=""> 
				<input id="subi" type="submit" value="保存" class="confirm_btn">
				<input type="button" value="取消" onclick="history.go(-1)" class="cancel_btn">
			</div>
			<!--编辑提交按钮 bof-->
		</form>
		<!--//配置-->
		</div>
		<!--右侧main-->
        
</div>
</body>
</html>