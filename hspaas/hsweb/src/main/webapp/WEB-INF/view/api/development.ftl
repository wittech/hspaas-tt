<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-开发流程</title>
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
			        <h2 class="page-title">开发流程</h2>
			        <article class="doc">
			            <section class="one">
			                <h3>第一步：设置模板</h3>
			                <p>为保证通道提供稳定服务，我们需要您预先提供短信模板，审核通过后才能发送，审核只需要一次。</p>
			                <p>如果需要添加自定义短信模板，您可以：</p>
			                <p>1）通过后台添加。登录后进入“模板设置”菜单，进行添加</p>
			                <img src="" alt="">
			                <p>2）通过接口添加。添加模板介绍请参考：<a href="/api2.0/tpl.html#b2" class="api20link">添加模板接口</a></p>
			                <p>注意，模板添加后，云片客服将在半小时内进行审核，审核结果会以短信通知您，请耐心等待。</p>
			            </section>
			            <section class="two">
			                <h3>第二步：使用接口发短信</h3>
			                <p class="sendp">A（推荐）：使用智能匹配模式发短信</p>
			                <p>该接口能自动匹配已经审核通过的模板进行发送。接口介绍请参考：<a href="/api2.0/sms.html#c1" class="api20link">智能匹配模板短信接口</a></p>
			                <p>比如，您已经添加过模板：【云片网】您的验证码是<span class="black">#code#</span>，并且已经由云片客服审核通过
			                    那么，内容：【云片网】您的验证码是<span class="black">1234</span>，可以成功匹配到模板并发送。</p>
			                <p class="sendp">B（不推荐）：使用指定模板模式发短信</p>
			                <p>1）什么是模板发送？</p>
			                <div class="subp"><p>模板发送是指使用预先设置好短信模板，发送时只提交模板id和变量值，由云片短信平台组装成完整的短信内容进行发送。模板发送接口介绍请参考：<a href="/api2.0/sms.html#c2" class="api20link">模板发送接口</a></p></div>
			                <p>2）模板发送的变量替换流程说明</p>
			                <div class="subp">
			                    <p>比如模板<span class="black">id</span>为<span class="black">1</span>的默认模板，模板内容为：</p>
			                    <p>【#company#】您的验证码是<span class="black">#code#</span></p>
			                    <p>调用模板发送接口时需要用<span class="black">POST</span>方式提交两个参数：</p>
			                    <code>
			                        tpl_id:1<br>
			                        tpl_value:#code#=1234&amp;#company#=云片网
			                    </code>
			                    <p>注意:<span class="black">#code#=1234&amp;#company#=云片网</span>必须作为一个参数值传递</p>
			                    <p>代码形式如:</p>
			                    <code>
			                        tpl_value=urlencode("#code#=1234&amp;#company#=云片网")
			                    </code>
			                    <p>否则您可能会得到以下错误提示:</p>
			                    <code>
			                        code:2,<br>
			                        msg:请求参数格式错误,<br>
			                        detail:参数 tpl_value 格式不正确,<br>
			                        tpl_value模板值中键值必须成对出现
			                    </code>
			                    <p>提交后，系统会根据您提交的模板<span class="black">id 1</span>，找到对应的模板内容：</p>
			                    <p>【#company#】您的验证码是<span class="black">#code#</span></p>
			                    <p>然后，根据<span class="black">tpl_value</span>设置的变量值进行变量替换，即<span class="black"> #code# </span>替换为<span class="black">1234</span>，<span class="black">#company#</span>替换为<span class="black">云片网</span></p>
			                    <p>最终，您将收到的内容为：</p>
			                    <p><span class="black">【云片网】您的验证码是1234</span></p>
			                    <p>其他模板的使用方法类似，设置对应的模板id(tpl_id)和相关的变量值(tpl_value)即可。模板发送接口详细介绍请参考：<a href="/api2.0/sms.html#c2" class="api20link">模板发送接口</a></p>
			                </div>
			                <h3>第三步：以上两种接口的使用，我们均提供了示例代码供参考，点击查看 <a href="/api2.0/demo.html" class="api20link">代码示例</a></h3>
			                <p>常见失败请根据接口的返回值或者参考<a href="/api2.0/recode.html" class="api20link">返回值说明</a>进行排查</p>
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
	api_service("development");
</script>
</html>