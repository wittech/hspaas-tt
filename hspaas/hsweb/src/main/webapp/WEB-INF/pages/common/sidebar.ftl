<div class="layui-side layui-bg-black kit-side">
	<div class="layui-side-scroll">
		<div class="kit-side-fold"><i class="fa fa-navicon" aria-hidden="true"></i></div>
		<#--<div class="kit-side-title" id="systemTitle"></div>-->
		<!-- 左侧导航区域（可配合layui已有的垂直导航） -->
		<ul class="layui-nav layui-nav-tree" lay-shrink="all" id="LAY-system-side-menu" lay-filter="layadmin-system-side-menu">
			<li class="layui-nav-item">
	            <a class="" href="javascript:;"><i class="fa fa-text-width" aria-hidden="true"></i><span> 短信平台</span></a>
	            <dl class="layui-nav-child">
	                <dd>
	                    <a href="javascript:;" data-url="${rc.contextPath}/sms/send" data-title="短信发送" kit-target data-id='11'>
	                        <span>发送短信</span></a>
	                </dd>
	                <dd>
	                    <a href="javascript:;" data-url="${rc.contextPath}/sms/send/query" data-title="短信发送记录" kit-target data-id='12'>
	                    	<span>短信发送记录</span></a>
	                    </a>
	                </dd>
	                <dd>
	                    <a href="javascript:;" data-url="${rc.contextPath}/sms/template" data-title="短信模板管理" kit-target data-id='13'>
	                    	<span> 短信模板管理</span></a>
	                    </a>
	                </dd>
	            </dl>
	        </li>
	        <li class="layui-nav-item">
	            <a class="" href="javascript:;"><i class="fa fa-image" aria-hidden="true"></i><span> 彩信平台</span></a>
	            <dl class="layui-nav-child">
	                <dd>
	                    <a href="javascript:;" data-url="${rc.contextPath}/mms/send" data-title="彩信发送" kit-target data-id='21'>
	                        <span>发送彩信</span></a>
	                </dd>
	                <dd>
	                    <a href="javascript:;" data-url="${rc.contextPath}/mms/send/query" data-title="彩信发送记录" kit-target data-id='22'>
	                    	<span>彩信发送记录</span></a>
	                    </a>
	                </dd>
	                <dd>
	                    <a href="javascript:;" data-url="${rc.contextPath}/mms/template" data-title="彩信模板管理" kit-target data-id='23'>
	                    	<span> 彩信模板管理</span></a>
	                    </a>
	                </dd>
	            </dl>
	        </li>
	        <#--
	        <li class="layui-nav-item">
	            <a href="javascript:;"><i class="fa fa-list" aria-hidden="true"></i><span> 报表统计</span></a>
	            <dl class="layui-nav-child">
	                <a href="javascript:;" data-url="${rc.contextPath}/report/sms_daily" data-title="短信发送统计" kit-target data-id='31'>
	                	<span> 短信发送统计</span></a>
	                </a>
	            </dl>
	        </li>
	         -->
	        <li class="layui-nav-item">
	            <a href="javascript:;"><i class="fa fa-address-book" aria-hidden="true"></i><span> 用户基础信息</span></a>
	            <dl class="layui-nav-child">
	                <a href="javascript:;" data-url="${rc.contextPath}/user/profile" data-title="我的信息" kit-target data-id='41'>
	                	<span> 我的信息</span></a>
	                </a>
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