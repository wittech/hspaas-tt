<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-模板API</title>
<#include "/common/assets.ftl"/>
<link href="${rc.contextPath}/assets/css/api.css" rel="stylesheet" type="text/css" />
<link href="${rc.contextPath}/assets/css/api_content.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<#include "/common/header.ftl"/>
	<div class="syzn_box">
		<div class="inner clearfix">
			<div id="user" class="main">
				<nav class="apileft">
				<#include "/common/api_menu.ftl"/>    
				</nav>
				<div class="content">
			        <h2 class="page-title">模板API</h2>
			        <article class="doc">
			            <section class="one">
			                <h3 id="b1">1、取默认模板</h3>
			                <code>
			                    URL：https://sms.yunpian.com/v2/tpl/get_default.json
			                </code>
			                <p>功能说明：用于获取系统提供的默认短信模板。使用默认模板，你无需创建模板便可进行模板短信发送，可以加快你接入的速度。</p>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>参数名</th>
			                            <th>类型</th>
			                            <th>是否必须</th>
			                            <th>描述</th>
			                            <th>示例</th>
			                        </tr>
			                        <tr>
			                            <td>apikey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户唯一标识</td>
			                            <td>9b11127a9701975c734b8aee81ee3526</td>
			                        </tr>
			                        <tr>
			                            <td>tpl_id</td>
			                            <td>Long</td>
			                            <td>否</td>
			                            <td>模板id，64位长整形。指定id时返回id对应的默认<br>模板。未指定时返回所有默认模板</td>
			                            <td>1</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>1)指定tpl_id时，调用成功的返回值示例：</p>
			                <pre><code>{
							    "tpl_id": 1,
							    "tpl_content": "【#company#】您的验证码是#code#",
							    "check_status": "SUCCESS",
							    "reason": null
							}</code></pre>
			                <p>2)未指定tpl_id时，调用成功的返回值示例：</p>
			                <pre><code>[{
							    "tpl_id": 1,
							    "tpl_content": "【#company#】您的验证码是#code#",
							    "check_status": "SUCCESS",
							    "reason ": null
							}, {
							    "tpl_id": 2,
							    "tpl_content": "【#company#】您的验证码是#code#。如非本人操作，请忽略本短信",
							    "check_status": "SUCCESS",
							    "reason ": null
							}]</code></pre>
			                <p>当前系统提供的默认短信模板有：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>模板id(tpl_id)</th>
			                            <th>模板内容</th>
			                            <th>模板参数示例(tpl_value)</th>
			                            <th>模板参数值含义</th>
			                        </tr>
			                        <tr>
			                            <td>1</td>
			                            <td>【#company#】您的验证码是#code#</td>
			                            <td>#code#=1234&amp;#company#=云片网</td>
			                            <td>code:验证码<br>company:公司名或产品名</td>
			                        </tr>
			                        <tr>
			                            <td>2</td>
			                            <td>【#company#】您的验证码是#code#。如非本人操作，请忽略本短信</td>
			                            <td>同上</td>
			                            <td>同上</td>
			                        </tr>
			                        <tr>
			                            <td>3</td>
			                            <td>【#company#】亲爱的#name#，您的验证码是#code#。如非本人操作，请忽略本短信</td>
			                            <td>#name#=Jacky&amp;#code#=1234&amp;<br>#company#=云片网</td>
			                            <td>name:会员名，其他同上</td>
			                        </tr>
			                        <tr>
			                            <td>4</td>
			                            <td>【#company#】亲爱的#name#，您的验证码是#code#。有效期为#hour#小时，请尽快验证</td>
			                            <td>#name#=苍老师&amp;#code#=1234<br>&amp;#hour#=1&amp;#company#=云片网</td>
			                            <td>hour:时长，其他同上</td>
			                        </tr>
			                        <tr>
			                            <td>5</td>
			                            <td>【#company#】感谢您注册#app#，您的验证码是#code#</td>
			                            <td>#app#=云片短信平台&amp;#code#=1234&amp;#company#=云片网</td>
			                            <td>app:产品名或网站名，其他同上</td>
			                        </tr>
			                        <tr>
			                            <td>6</td>
			                            <td>【#company#】欢迎使用#app#，您的手机验证码是#code#。本条信息无需回复</td>
			                            <td>同上</td>
			                            <td>同上</td>
			                        </tr>
			                        <tr>
			                            <td>7</td>
			                            <td>【#company#】正在找回密码，您的验证码是#code#</td>
			                            <td>#code#=1234&amp;#company#=云片网</td>
			                            <td>同上</td>
			                        </tr>
			                        <tr>
			                            <td>8</td>
			                            <td>【#company#】激活码是#code#。如非本人操作，请致电#tel#</td>
			                            <td>#code#=1234&amp;#tel#=400-081-2798#company#=云片网</td>
			                            <td>tel:电话号码，其他同上</td>
			                        </tr>
			                        <tr>
			                            <td>9</td>
			                            <td>【#company#】#code#(#app#手机动态码，请完成验证)，如非本人操作，请忽略本短信</td>
			                            <td>#code#=1234&amp;#app#=云片短信平台&amp;#company#=云片网</td>
			                            <td>同上</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <h3 id="b2">2、添加模板</h3>
			                <code>
			                    URL：https://sms.yunpian.com/v2/tpl/add.json
			                </code>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>参数名</th>
			                            <th>类型</th>
			                            <th>是否必须</th>
			                            <th>描述</th>
			                            <th>示例</th>
			                        </tr>
			                        <tr>
			                            <td>apikey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户唯一标识</td>
			                            <td>9b11127a9701975c734b8aee81ee3526</td>
			                        </tr>
			                        <tr>
			                            <td>tpl_content</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>模板内容，必须以带符号【】的签名开头</td>
			                            <td>【云片网】您的验证码是#code#</td>
			                        </tr>
			                        <tr>
			                            <td>notify_type</td>
			                            <td>Integer</td>
			                            <td>否</td>
			                            <td>审核结果短信通知的方式:
			                                <br>0表示需要通知,默认;
			                                <br>1表示仅审核不通过时通知;
			                                <br>2表示仅审核通过时通知;
			                                <br>3表示不需要通知
			                            </td>
			                            <td>1</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>调用成功的返回值示例：</p>
			                <pre><code>{
							    "tpl_id": 1,                 //模板id
							    "tpl_content": "【云片网】您的验证码是#code#", //模板内容
							    "check_status": "CHECKING",     //审核状态：CHECKING/SUCCESS/FAIL
							    "reason": null                  //审核未通过的原因
							}</code></pre>
			                <h3 id="b3">3、取模板</h3>
			                <code>
			                    URL：https://sms.yunpian.com/v2/tpl/get.json
			                </code>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>参数名</th>
			                            <th>类型</th>
			                            <th>是否必须</th>
			                            <th>描述</th>
			                            <th>示例</th>
			                        </tr>
			                        <tr>
			                            <td>apikey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户唯一标识</td>
			                            <td>9b11127a9701975c734b8aee81ee3526</td>
			                        </tr>
			                        <tr>
			                            <td>tpl_id</td>
			                            <td>Long</td>
			                            <td>否</td>
			                            <td>模板id，64位长整形。指定id时返回id对应的<br>模板。未指定时返回所有模板</td>
			                            <td>1</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>1)指定id时，调用成功的返回值示例：</p>
			                <pre><code>{
							    "tpl_id": 1,
							    "tpl_content": "您的验证码是#code#",
							    "check_status": "FAIL",
							    "reason": "模板开头必须带签名，如【云片网络】" //审核未通过的原因
							}</code></pre>
			                <p>2)未指定id时，调用成功的返回值示例：</p>
			                <pre><code>[{
							    "tpl_id": 1,
							    "tpl_content": "您的验证码是#code#",
							    "check_status": "FAIL",
							    "reason ": "模板开头必须带签名，如【云片网】"
							},
							{
							    "tpl_id": 2,
							    "tpl_content": "【云片网】您的验证码是#code#。如非本人操作，请忽略本短信",
							    "check_status": "SUCCESS",
							    "reason ": null
							}]</code></pre>
			                <h3 id="b4">4、修改模板</h3>
			                <code>
			                    URL：https://sms.yunpian.com/v2/tpl/update.json
			                </code>
			                <p>注意：模板成功修改之后需要重新审核才能使用！同时提醒您如果修改了变量，请重新务必测试，以免替换出错！</p>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width: 80px;">参数名</th>
			                            <th style="width: 40px;">类型</th>
			                            <th style="width: 60px;">是否必须</th>
			                            <th>描述</th>
			                            <th>示例</th>
			                        </tr>
			                        <tr>
			                            <td>apikey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户唯一标识</td>
			                            <td>9b11127a9701975c734b8aee81ee3526</td>
			                        </tr>
			                        <tr>
			                            <td>tpl_id</td>
			                            <td>Long</td>
			                            <td>是</td>
			                            <td>模板id，64位长整形，指定id时返回id对应的模板。未指定时返回所有模板</td>
			                            <td>9527</td>
			                        </tr>
			                        <tr>
			                            <td>tpl_content</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>模板id，64位长整形。指定id时返回id对应的模板。未指定时返回所有模板模板内容，必须以带符号【】的签名开头</td>
			                            <td>【云片网】您的验证码是#code#</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>调用成功的返回值示例：</p>
			                <pre><code>{
							    "tpl_id": 9527,                 //模板id
							    "tpl_content": "【云片网】您的验证码是#code#", //模板内容
							    "check_status": "CHECKING",     //审核状态：CHECKING/SUCCESS/FAIL
							    "reason": null                  //审核未通过的原因
							}</code></pre>
			                <h3 id="b5">5、删除模板</h3>
			                <code>
			                    URL：https://sms.yunpian.com/v2/tpl/del.json
			                </code>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width: 80px;">参数名</th>
			                            <th style="width: 40px;">类型</th>
			                            <th style="width: 60px;">是否必须</th>
			                            <th>描述</th>
			                            <th>示例</th>
			                        </tr>
			                        <tr>
			                            <td>apikey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户唯一标识</td>
			                            <td>9b11127a9701975c734b8aee81ee3526</td>
			                        </tr>
			                        <tr>
			                            <td>tpl_id</td>
			                            <td>Long</td>
			                            <td>是</td>
			                            <td>模板id，64位长整形。指定id时返回id对应的模板。未指定时返回所有模板</td>
			                            <td>9527</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>调用成功的返回值示例：</p>
			                <pre><code>{
							    "tpl_id": 9527,                 //模板id
							    "tpl_content": "【云片网】您的验证码是#code#", //模板内容
							    "check_status": "CHECKING",     //审核状态：CHECKING/SUCCESS/FAIL
							    "reason": null                  //审核未通过的原因
							}</code></pre>
			            </section>
			        </article>
			    </div>
			</div>
		</div>
	</div>
	<#include "/common/footer.ftl"/>
</body>
<script>
	//头部菜单选择
	service("api");
	api_service("template");
</script>
</html>