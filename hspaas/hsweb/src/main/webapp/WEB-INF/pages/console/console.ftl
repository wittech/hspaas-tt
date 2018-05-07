<!DOCTYPE html>
<html lang="en">
<head>
    <title>控制台首页 - 华时融合平台</title>
    <#include "/common/assets.ftl"/>
</head>
<body>

<!-- layout admin -->
<div class="layui-layout layui-layout-admin">
	<#include "/common/header.ftl"/>

	<#include "/common/sidebar.ftl"/>
	
	<!-- body -->
	<div class="layui-body my-body">
	    <div class="layui-tab layui-tab-card my-tab" lay-filter="card" lay-allowClose="true">
	        <ul class="layui-tab-title">
	            <li class="layui-this" lay-id="1"><span><i class="layui-icon">&#xe638;</i>欢迎页</span></li>
	        </ul>
	        <div class="layui-tab-content">
	            <div class="layui-tab-item layui-show">
	                <iframe id="iframe" src="${rc.contextPath}/dashboard" frameborder="0"></iframe>
	            </div>
	        </div>
	    </div>
	</div>
    
    <#include "/common/footer.ftl"/>
</div>

<#--
<div class="my-pay-box none">
    <div><img src="./frame/static/image/zfb.png" alt="支付宝"><p>支付宝</p></div>
    <div><img src="./frame/static/image/wx.png" alt="微信"><p>微信</p></div>
</div>
 -->

<!-- 右键菜单 -->
<div class="my-dblclick-box none">
    <table class="layui-tab dblclick-tab">
        <tr class="card-refresh">
            <td><i class="layui-icon">&#x1002;</i>刷新当前标签</td>
        </tr>
        <tr class="card-close">
            <td><i class="layui-icon">&#x1006;</i>关闭当前标签</td>
        </tr>
        <tr class="card-close-all">
            <td><i class="layui-icon">&#x1006;</i>关闭所有标签</td>
        </tr>
    </table>
</div>

<script type="text/javascript" src="${rc.contextPath}/assets/layui/layui.js"></script>
<script type="text/javascript" src="${rc.contextPath}/assets/static/js/vip_comm.js"></script>
<script type="text/javascript">
layui.use(['layer','vip_nav'], function () {

    // 操作对象
    var layer       = layui.layer
        ,vipNav     = layui.vip_nav
        ,$          = layui.jquery;

    // 顶部左侧菜单生成 [请求地址,过滤ID,是否展开,携带参数]
    vipNav.top_left('./json/nav_top_left.json','side-top-left',false);
    // 主体菜单生成 [请求地址,过滤ID,是否展开,携带参数]
    vipNav.main('./json/nav_main.json','side-main',true);

});
</script>
</body>
</html>