<div class="side">
	<#--
	<div class="profile">
		<dl>
			<dt><img src="${rc.contextPath}/assets/images/photo.png" width="80" height="80"></dt>
			<dd>13718816906</dd>
			<dd class="user">北京惠尔睿智科技有限公司</dd>
		</dl>
		<a href="" class="edit">&nbsp;</a>
	</div>
	-->
	<div class="quick_link">
		<a href="${rc.contextPath}/notice/message/index" class="msg">消 息<span id="countMsg">0</span></a>
		<a href="${rc.contextPath}/product/index" class="add_app">套餐购买</a>
	</div>
		
	<div class="menu">
		<div class="tab">
			<ul><!-- class="current"-->
			<#--
				<li id="li_kzt">
					<a href="javascript:void(0)">
						<i class="i1">&nbsp;</i>
						控制台
					</a>
				</li>
				-->
				<li id="li_dx">
					<a href="javascript:void(0)">
						<i class="i2">&nbsp;</i>
						短信管理
					</a>
				</li>
				<#--
				<li id="li_liuliang">
					<a href="javascript:void(0)">
						<i class="i3">&nbsp;</i>
						流量管理
					</a>
				</li>
				<li id="li_yuyin">
					<a href="javascript:void(0)">
						<i class="i4">&nbsp;</i>
						语音管理
					</a>
				</li>
				-->
				<#--
				<li id="li_weixin">
					<a href="javascript:void(0)">
						<i class="i5">&nbsp;</i>
						微推管理
					</a>
				</li>
				-->
				<li id="li_finance">
					<a href="javascript:void(0)">
						<i class="i6">&nbsp;</i>
						财务管理
					</a>
				</li>
				<#--
				<li id="li_jishu">
					<a href="javascript:void(0)">
						<i class="i7">&nbsp;</i>
						技术信息
					</a>
				</li>
				-->
                <li id="li_service">
					<a href="javascript:void(0)">
						<i class="i8">&nbsp;</i>
						客服系统
					</a>
				</li>
                <li id="li_settings">
					<a href="javascript:void(0)">
						<i class="i9">&nbsp;</i>
						系统设置
					</a>
				</li>
			</ul>
		</div>
		<div class="tab_ctn">
			<#--
			<dl style="display: none;" id="console">
				<dd id="console_product_list"><a href="${base}/product/index"><i class="i11">&nbsp;</i>产品报价</a></dd>
			</dl>
			-->
			<dl id="dx_send">
				<dd id="dx_create" class="current"><a href="${base}/sms/record/send/create"><i class="i21">&nbsp;</i>短信发送</a></dd>
				<dd id="dx_template"><a href="${base}/sms/template/index"><i class="i21">&nbsp;</i>短信模板</a></dd>
				<dd id="dx_task"><a href="${base}/sms/task/index"><i class="i25">&nbsp;</i>任务管理</a></dd>
				<dd id="dx_send"><a href="${base}/sms/record/send/index"><i class="i21">&nbsp;</i>成功记录</a></dd>
				<dd id="dx_send_error"><a href="${base}/sms/record/error/index"><i class="i22">&nbsp;</i>失败记录</a></dd>
				<dd id="dx_send_receive"><a href="${base}/sms/record/receive/index"><i class="i23">&nbsp;</i>接收记录</a></dd>
				<dd id="dx_send_test"><a href="${base}/sms/record/test/index"><i class="i24">&nbsp;</i>验证码测试</a></dd>
				<dd id="dx_send_massage_white_list"><a href="${base}/settings/mobile/white/index"><i class="i25">&nbsp;</i>手机白名单</a></dd>
				<#--<dd id="dx_send_massage_black_list"><a href="${base}/settings/mobile/black/index"><i class="i31">&nbsp;</i>手机黑名单</a></dd>-->
			</dl>
			<#--
			<dl id="flow" style="display: none;">
				<dd id="my_order"><a href="${base}/fs/record/order/index"><i class="i32">&nbsp;</i>我的订单</a></dd>
				<dd id="single_recharge"><a href="${base}/fs/recharge/create"><i class="i33">&nbsp;</i>流量充值</a></dd>
				<dd id="bach_recharge"><a href="${base}/fs/recharge/batch_recharge"><i class="i41">&nbsp;</i>批量充值</a></dd>
			</dl>
			<dl id="vs" style="display: none;">
				<dd id="record"><a href="${base}/vs/record/index"><i class="i42">&nbsp;</i>语音记录</a></dd>
				<dd id="send"><a href="${base}/vs/record/send"><i class="i43">&nbsp;</i>证码测试</a></dd>
			</dl>
			-->
			<#--
            <dl style="display: none;">
				<dd><a href="客服系统-独享通道申请.html"><i class="i44">&nbsp;</i>独享通道申请</a></dd>
				<dd><a href="客服系统-协议合同.html"><i class="i45">&nbsp;</i>协议合同</a></dd>
				<dd><a href="客服系统-产品报价.html"><i class="i46">&nbsp;</i>产品报价</a></dd>
                <dd><a href="客服系统-高级用户申请.html"><i class="i47">&nbsp;</i>高级用户申请</a></dd>
                <dd><a href="客服系统-意见反馈.html"><i class="i48">&nbsp;</i>意见反馈</a></dd>
			</dl>
			-->
            <dl id="financial" style="display: none;">
				<dd id="information"><a href="${base}/account/information"><i class="i50">&nbsp;</i>账户信息</a></dd>
				<dd id="recharge"><a href="${base}/account/recharge"><i class="i51">&nbsp;</i>账户充值</a></dd>
				<dd id="consumption_list"><a href="${base}/order/consumption"><i class="i52">&nbsp;</i>消费账单</a></dd>
				<dd id="financial_balance_list" class="current"><a href="${base}/finance/balance/index"><i class="i53">&nbsp;</i>发票管理</a></dd>
			</dl>
			<#--
			 <dl style="display: none;">
				<dd><a href="技术信息-短信技术文档.html"><i class="i41">&nbsp;</i>短信技术文档</a></dd>
				<dd><a href="技术信息-语音技术文档.html"><i class="i42">&nbsp;</i>语音技术文档</a></dd>
				<dd><a href="技术信息-流量技术文档.html"><i class="i43">&nbsp;</i>流量技术文档</a></dd>
			</dl>
			-->
			<dl id="service" style="display: none;">
				<dd id="service_opinion"><a href="${base}/service/opinion/index"><i class="i54">&nbsp;</i>意见反馈</a></dd>
				<dd id="service_agreement_contract"><a href="${base}/service/agreement_contract/index"><i class="i55">&nbsp;</i>协议合同</a></dd>
			</dl>
			<dl id="settings" style="display: none;">
				<dd id="user_basis"><a href="${base}/user/basis"><i class="i56">&nbsp;</i>基础信息</a></dd>
				<dd id="settings_balance_remind"><a href="${base}/settings/balance_remind/index"><i class="i57">&nbsp;</i>余额提醒</a></dd>
				<dd id="settings_push"><a href="${base}/settings/push/index"><i class="i58">&nbsp;</i>推送设置</a></dd>
				<dd id="settings_notification"><a href="${base}/notice/message/index"><i class="i59">&nbsp;</i>消息记录</a></dd>
				<dd id="settings_address_book_list"><a href="${base}/settings/address_book/index"><i class="i60">&nbsp;</i>地址管理</a></dd>
				<dd id="settings_host_white_list"><a href="${base}/settings/host/index"><i class="i61">&nbsp;</i>ip白名单</a></dd>
			</dl>
		</div>
	</div>
	<#--
	<script type="text/javascript">
		var bid = $("body").attr("id");
		var menus = bid.split("-");
		var mainMn = menus[0];
		var sonMn = menus[1];
		$("#m-"+mainMn).addClass("current");
		$("#m-"+mainMn+sonMn).parent("dl").show().siblings("dl").hide();
		$("#m-"+mainMn+sonMn).addClass("current").parent("dl").siblings("dl").find("dd").removeClass("current");
	</script> -->
	<script type="text/javascript">
		function loadMenu(parent,parent_dl,childNode){
			$(".tab_ctn dl").hide();
			$(".tab_ctn #"+parent_dl).show();
			$(".tab ul li").removeClass("current");	
			$("#"+parent).addClass("current");
			$("#"+parent_dl+" dd").removeClass("current");
			$("#"+parent_dl+" #"+childNode).removeClass("current");
			$("#"+parent_dl+" #"+childNode).addClass("current");
		};
		
		function message(){
			$.post("${rc.contextPath}/notice/message/unread", function(data) {
			   $("#countMsg").html(data);
			});
		};
		
		setTimeout("message()", 30000);
	</script> 
</div>