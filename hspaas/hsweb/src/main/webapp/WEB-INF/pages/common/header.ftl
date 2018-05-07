<!-- header -->
<div class="layui-header my-header">
    <a href="${rc.contextPath}/">
        <!--<img class="my-header-logo" src="" alt="logo">-->
        <div class="my-header-logo">华时融合平台</div>
    </a>
    <div class="my-header-btn">
        <button class="layui-btn layui-btn-small btn-nav"><i class="layui-icon">&#xe65f;</i></button>
    </div>

    <!-- 顶部左侧添加选项卡监听 -->
    <ul class="layui-nav" lay-filter="side-top-left">
        <!--<li class="layui-nav-item"><a href="javascript:;" href-url="demo/btn.html"><i class="layui-icon">&#xe621;</i>按钮</a></li>
        <li class="layui-nav-item">
            <a href="javascript:;"><i class="layui-icon">&#xe621;</i>基础</a>
            <dl class="layui-nav-child">
                <dd><a href="javascript:;" href-url="demo/btn.html"><i class="layui-icon">&#xe621;</i>按钮</a></dd>
                <dd><a href="javascript:;" href-url="demo/form.html"><i class="layui-icon">&#xe621;</i>表单</a></dd>
            </dl>
        </li>-->
    </ul>

    <!-- 顶部右侧添加选项卡监听 -->
    <ul class="layui-nav my-header-user-nav" lay-filter="side-top-right">
        <li class="layui-nav-item"><a href="javascript:;" class="pay" href-url="">当前余额</a></li>
        <li class="layui-nav-item">
            <a class="name" href="javascript:;"><img src="${rc.contextPath}/assets/static/image/code.png" alt="logo"> ${(Session["LOGIN_USER_SESSION_KEY"].mobile)!'18809099999'} </a>
            <dl class="layui-nav-child">
                <dd><a href="javascript:;" href-url="${rc.contextPath}/user/profile"><i class="layui-icon">&#xe621;</i>我的信息</a></dd>
                <dd><a href="${rc.contextPath}/logout"><i class="layui-icon">&#x1006;</i>退出</a></dd>
            </dl>
        </li>
    </ul>

</div>