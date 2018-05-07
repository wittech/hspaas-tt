<!-- side -->
    <div class="layui-side my-side">
        <div class="layui-side-scroll">
            <!-- 左侧主菜单添加选项卡监听 -->
            <ul class="layui-nav layui-nav-tree" lay-filter="side-main">
                <li class="layui-nav-item layui-nav-itemed">
                    <a href="javascript:;"><i class="layui-icon">&#xe620;</i>短信平台</a>
                    <dl class="layui-nav-child">
                        <dd><a href="javascript:;" href-url="${rc.contextPath}/sms/send"><i class="layui-icon">&#xe621;</i>发送短信</a></dd>
                        <dd><a href="javascript:;" href-url="${rc.contextPath}/sms/send/list"><i class="layui-icon">&#xe621;</i>短信发送记录</a></dd>
                        <#-- 
                        	<dd><a href="javascript:;" href-url="demo/btn.html"><i class="layui-icon">&#xe621;</i>短信接收记录</a></dd>
                        	<dd><a href="javascript:;" href-url="demo/btn.html"><i class="layui-icon">&#xe621;</i>调用接口失败记录</a></dd>
                        -->
                    </dl>
                </li>
                <li class="layui-nav-item">
                    <a href="javascript:;"><i class="layui-icon">&#xe628;</i>用户基础信息</a>
                    <dl class="layui-nav-child">
                        <dd><a href="javascript:;" href-url="demo/login.html"><i class="layui-icon">&#xe621;</i>我的信息</a></dd>
                        <dd><a href="javascript:;" href-url="demo/register.html"><i class="layui-icon">&#xe621;</i>我的余额</a></dd>
                    </dl>
                </li>
            </ul>

        </div>
    </div>