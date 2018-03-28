<#--
<h2>
	<a href="${rc.contextPath}/api/development" id="development" class="item api20link">开发流程</a>
</h2>
<h2><a href="${rc.contextPath}/api/explain" id="explain" class=" item api20link" id="">调用说明</a></h2>
<h2 class="cos" id="ul_account">
    <a href="${rc.contextPath}/api/account" id="account" class="item api20link">账户API</a>
    <ul class="sub-nav">
        <li><a id="a11" href="#a1">查看账户信息</a></li>
        <li><a id="a22" href="#a2">修改账户信息</a></li>
    </ul>
</h2>

<h2 class="cos" id="ul_template">
    <a href="${rc.contextPath}/api/template" id="template" class="item api20link">模板API <span class="shou"></span></a>
    <ul class="sub-nav">
        <li><a id="b11" href="#b1">取默认模板</a></li>
        <li><a id="b22" href="#b2">添加模板</a></li>
        <li><a id="b33" href="#b3">取模板</a></li>
        <li><a id="b44" href="#b4">修改模板</a></li>
        <li><a id="b55" href="#b5">删除模板</a></li>
    </ul>
</h2>
-->
<h2 class="cos" id="ul_sms">
    <a href="${rc.contextPath}/api/sms" id="sms" class=" item api20link">短信API</a>
    <ul class="sub-nav">
        <li><a id="c11" href="#c1">批量发送</a></li>
        <li><a id="c22" href="#c2">点对点短信发送</a></li>
        <li><a id="c33" href="#c3">模板点对点短信发送</a></li>
        <li><a id="c66" href="#c4">余额查询</a></li>
        <li><a id="c77" href="#c5">状态报告推送</a></li>
        <li><a id="c88" href="#c6">上行短信推送</a></li>
        <li><a id="c44" href="#c7">短信提交响应码respcode</a></li>
    </ul>
</h2>
<#-- 
<h2 class="cos" id="ul_voice">
    <a href="${rc.contextPath}/api/voice" id="voice" class=" item api20link">语音API</a>
    <ul class="sub-nav">
        <li><a id="d11" href="#d1">发语音验证码</a></li>
        <li><a id="d22" href="#d2">获取状态报告</a></li>
        <li><a id="d33" href="#d3">推送状态报告</a></li>
    </ul>
</h2>

<h2 class="cos" id="ul_flow">
    <a href="${rc.contextPath}/api/flow" id="flow" class=" item api20link">流量API</a>
    <ul class="sub-nav">
        <li><a id="e11" href="#e1">查询流量包</a></li>
        <li><a id="e22" href="#e2">充值流量</a></li>
        <li><a id="e33" href="#e3">获取状态报告</a></li>
        <li><a id="e44" href="#e4">推送状态报告</a></li>
    </ul>
</h2>

<h2 class="cos" id="ul_conversation">
    <a href="${rc.contextPath}/api/conversation" id="conversation" class=" item api20link">隐私通话API</a>
    <ul class="sub-nav">
        <li><a id="f11" href="#f1">绑定</a></li>
        <li><a id="f22" href="#f2">解绑</a></li>
        <li><a id="f33" href="#f3">话单获取</a></li>
        <li><a id="f44" href="#f4">推送话单</a></li>
    </ul>
</h2>
<h2><a href="${rc.contextPath}/api/return_explain" id="return_explain" class=" item api20link">返回值说明</a></h2>
<h2 class="cos"  id="ul_demo">
    <a href="${rc.contextPath}/api/demo" id="demo" class=" item api20link">代码示例</a>
    <ul id="lanTab" class="sub-nav">
        <li class="active"><a href="javascript:void(0);">Java</a></li>
        <li><a href="javascript:void(0);">python</a></li>
        <li><a href="javascript:void(0);">C#</a></li>
        <li><a href="javascript:void(0);">C/C++</a></li>
        <li><a href="javascript:void(0);">shell/bash</a></li>
        <li><a href="javascript:void(0);">php</a></li>
        <li><a href="javascript:void(0);">asp</a></li>
        <li><a href="javascript:void(0);">nodejs</a></li>
        <li><a href="javascript:void(0);">go</a></li>
        <li><a href="javascript:void(0);">ruby</a></li>
    </ul>
</h2>

<h2><a href="${rc.contextPath}/api/contribution" id="contribution" class=" item api20link">贡献代码</a></h2>
<h2 class="cos"  id="ul_problem">
    <a href="${rc.contextPath}/api/problem" id="problem" class=" item api20link">常见问题</a>
    <ul class="sub-nav">
        <li><a id="faq11" href="#faq1" class="item">A短信办理类</a></li>
        <li><a id="faq22" href="#faq2" class="item">B短信使用类</a></li>
        <li><a id="faq33" href="#faq3" class="item">C专用通道类</a></li>
        <li><a id="faq44" href="#faq4" class="item">D技术开发类</a></li>
        <li><a id="faq55" href="#faq5" class="item">E语音验证码类</a></li>
        <li><a id="faq66" href="#faq6" class="item">F国际短信使用类</a></li>
        <li><a id="faq77" href="#faq7" class="item">G手机流量使用类</a></li>
    </ul>
</h2>

<h2><a href="${rc.contextPath}/api/download" id="download" class=" item api20link">SDK下载</a></h2>
<h2 class="englishapi" style="display: none;">
    <a href="/download/Yunpian_API_V2_Docs.pdf">Download API Docs</a>
</h2>
-->
<script>
	function api_service(id){
		$(".apileft h2 a").removeClass("active");
		$('#'+id).attr("class","active");
		$('#'+id).attr("style","color:rgb(255, 255, 255);background:rgb(50, 134, 193);");
		$("h2 .sub-nav").hide();
		$("#ul_"+id+"  .sub-nav").show();
	}
</script>
