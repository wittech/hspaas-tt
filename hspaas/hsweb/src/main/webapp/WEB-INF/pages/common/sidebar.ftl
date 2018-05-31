<div class="layui-side layui-bg-black kit-side">
	<div class="layui-side-scroll">
		<div class="kit-side-fold"><i class="fa fa-navicon" aria-hidden="true"></i></div>
		<#--<div class="kit-side-title" id="systemTitle"></div>-->
		<!-- 左侧导航区域（可配合layui已有的垂直导航） -->
		<ul class="layui-nav layui-nav-tree" lay-filter="kitNavbar" kit-navbar>
			<li class="layui-nav-item layui-nav-itemed">
            <a class="" href="javascript:;"><i class="fa fa-plug" aria-hidden="true"></i><span> 短信平台</span></a>
            <dl class="layui-nav-child">
                <dd>
                    <a href="javascript:;" data-url="${rc.contextPath}/sms/send" data-title="短信发送" kit-target data-id='1'>
                        <i class="layui-icon">&#xe6c6;</i><span> 发送短信</span></a>
                </dd>
                <dd>
                    <a href="javascript:;" data-url="${rc.contextPath}/sms/send/query" data-icon="fa-user" data-title="短信发送记录" kit-target data-id='2'>
                    	<i class="layui-icon">&#xe658;</i><span> 短信发送记录</span></a>
                    </a>
                </dd>
            </dl>
        </li>
        <li class="layui-nav-item layui-nav-itemed">
            <a href="javascript:;"><i class="fa fa-plug" aria-hidden="true"></i><span> 用户基础信息</span></a>
            <dl class="layui-nav-child">
                <dd><a href="javascript:;" data-url="${rc.contextPath}/user/profile" data-icon="fa-user" data-title="我的信息" kit-target data-id='3'>
                	<i class="layui-icon">&#xe614;</i><span> </span> 我的信息</a>
                </dd>
                <#--
                <dd><a href="javascript:;" kit-target data-options="{url:'tab.html',icon:'&#xe658;',id:'7'}">
                	<i class="layui-icon">&#xe614;</i><span> </span> 我的余额</a>
                </dd>
                -->
            </dl>
        </li>
		</ul>
	</div>
</div>