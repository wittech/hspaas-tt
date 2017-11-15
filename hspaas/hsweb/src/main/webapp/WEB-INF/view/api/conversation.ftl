<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-隐私通话API</title>
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
			        <h2 class="page-title">隐私通话API</h2>
			        <article class="doc">
			            <section class="one">
			                <h3 id="f1">1、绑定</h3>
			                <code>
			                    URL: https://call.yunpian.com/v2/call/bind.json
			                </code>
			                <p>功能说明：绑定号码 </p>
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
			                            <td>from</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>需要绑定的号码</td>
			                            <td>+8615012341234</td>
			                        </tr>
			                        <tr>
			                            <td>to</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>需要绑定的号码</td>
			                            <td>+8615011112222</td>
			                        </tr>
			                        <tr>
			                            <td>duration</td>
			                            <td>Intger</td>
			                            <td>是</td>
			                            <td>有效时长，单位：秒</td>
			                            <td>600</td>
			                        </tr>
			                        <tr>
			                            <td>area_code</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>区号，期望anonymous_number所属的地区</td>
			                            <td>+8621（021）</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			
			                <p>注:</p>
			                <p>1.from、to不存在方向问题，可以任意顺序传入</p>
			                <p>2.目前不支持area_code，后续开放</p>
			                <p>3.所有号码格式按照E.164格式规范，若传入格式不符合E.164规范则按照大陆号码格式处理成E.164</p>
			
			                <p>调用成功的返回值示例：</p>
			                <pre><code>{
							    "message_id": "8dba7c44e0ee4062ab2ac61d3e9ce7cf",
							    "anonymous_number": "+862112345678"
							}</code></pre>
			
			                <h3 id="f2">2、解绑</h3>
			                <code>
			                    URL: https://call.yunpian.com/v2/call/unbind.json
			                </code>
			                <p>功能说明：解绑号码 </p>
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
			                            <td>from</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>需要绑定的号码</td>
			                            <td>+8615012341234</td>
			                        </tr>
			                        <tr>
			                            <td>to</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>需要绑定的号码</td>
			                            <td>+8615011112222</td>
			                        </tr>
			                        <tr>
			                            <td>duration</td>
			                            <td>Intger</td>
			                            <td>否</td>
			                            <td>延迟解绑的时间，单位：秒，默认为0，0表示立即解除绑定</td>
			                            <td>0</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			
			                <p>注:</p>
			                <p>1.from、to不存在方向问题，可以任意顺序传入</p>
			                <p>2.所有号码格式按照E.164格式规范，若传入格式不符合E.164规范则按照大陆号码格式处理成E.164</p>
			
			                <pre><code>{
							    "code": 0,
							    "msg": "OK"
							}</code></pre>
			
			                <h3 id="f3">3、话单获取</h3>
			                <code>
			                    URL: https://call.yunpian.com/v2/call/pull.json
			                </code>
			                <p>功能说明：话单获取</p>
			                <p>访问方式：POST </p>
			                <p>备注：需要联系客服开通</p>
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
			                            <td>page_size</td>
			                            <td>Integer</td>
			                            <td>否</td>
			                            <td>每页个数，最大100个，默认20个</td>
			                            <td>20</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>调用成功的返回值示例：</p>
							<pre><code>[
							    {
							        "id": "f2a4783f8d0e43cd89bd0804d8a9e7fb",
							        "message_id": "e31538c3553c4867ab118e52f939ec31",
							        "from": "+8615012341234",
							        "to": "+8615011112222",
							        "user_start_time": "2016-04-01 22:29:37", // 主叫发起呼叫的时间
							        "user_receive_time": "2016-04-01 22:29:46",// 被叫应答的时间
							        "call_end_time": "2016-04-01 22:29:53", // 通话结束的时间
							        "duration": 7, // 通话时长：end_time - receive_time
							        "fee" : 0.15 , // 费用，单位：元
							        "status": "SUCCESS", // 接听状态有:SUCCESS/FAIL
							        "error_msg": null, // 接收失败的原因，如："用户未应答"
							        "anonymous_number": "02112345678" // 匿名号码
							    }
							]</code></pre>
			                <h3 id="f4">4、推送话单</h3>
			                <!--<p>功能说明：开通此接口后，我们将为您实时推送最新的状态报告。您需要提供一个url地址，接受http post请求。-->
			                    <!--该接口一次最多推送500个状态报告，为不影响推送速度，建议先接受数据后再做异步处理。</p>-->
			                <!--<p>备注：该接口为高级接口，默认不开放，可以在云片用户后台开启，-->
			                    <!--<a href="/admin/module/account_setting_pushurl" target="_blank">去开启</a>。-->
			                <!--</p>-->
			                <p>推送方式：POST</p>
			                <p>推送的数据格式：</p>
			                <code>
			                    参数名 = 经过urlencode编码的数据
			                </code>
			                <p>形式如：</p>
			                <code>voice_call_status = urlencode(json)</code>
			                <p>服务端推送数据形式如下：</p>
			                <code>curl --data "voice_call_status=url_encode_json" http://your_receive_url_address</code>
			                <p>其中json数据示例为：</p>
			                <pre><code>[
							    {
							        "id": "f2a4783f8d0e43cd89bd0804d8a9e7fb",
							        "message_id": "e31538c3553c4867ab118e52f939ec31", // 语音绑定记录id，32位的唯一字符串
							        "from": "+8615012341234",
							        "to": "+8615011112222",
							        "user_start_time" : "2016-04-01 14:29:12", // 主叫发起呼叫的时间
							        "user_receive_time": "2016-04-01 14:29:18", // 被叫应答时间
							        "call_end_time": "2016-04-01 14:30:18", // 被叫应答时间
							        "duration": 60, // 通话时长，单位：秒
							        "fee" : 0.15 , // 话费，单位：元
							        "anonymous_number" : "+862112345678", // 匿名号码，主叫呼叫的匿名号码
							        "status": "SUCCESS" ,//接听状态有:SUCCESS/FAIL
							        "error_msg": null //接收失败的原因，如："用户未应答"
							    }
							]</code></pre>
			                <p>接收到数据后，请从参数voice_call_status中取值，取到值使用urldecode解码后为实际json格式数据
			                    处理成功请返回字符串"SUCCESS"或"0"，其他返回值将被认为是失败。该接口失败重试次数为3次，每次间隔5分钟。</p>
			
			                <!--<h3 id="f5">5、修改获取被叫号码的回调地址</h3>-->
			                <!--<code>-->
			                    <!--URL: https://call.yunpian.com/v2/call/set_receiver_callback.json-->
			                <!--</code>-->
			                <!--<p>功能说明：修改获取被叫号码的回调地址</p>-->
			                <!--<p>访问方式：POST </p>-->
			                <!--<p>参数：</p>-->
			                <!--<div class="can">-->
			                    <!--<table>-->
			                        <!--<tr>-->
			                            <!--<th>参数名</th>-->
			                            <!--<th>类型</th>-->
			                            <!--<th>是否必须</th>-->
			                            <!--<th>描述</th>-->
			                            <!--<th>示例</th>-->
			                        <!--</tr>-->
			                        <!--<tr>-->
			                            <!--<td>apikey</td>-->
			                            <!--<td>String</td>-->
			                            <!--<td>是</td>-->
			                            <!--<td>用户唯一标识</td>-->
			                            <!--<td>9b11127a9701975c734b8aee81ee3526</td>-->
			                        <!--</tr>-->
			                        <!--<tr>-->
			                            <!--<td>anonymous_number</td>-->
			                            <!--<td>String</td>-->
			                            <!--<td>是</td>-->
			                            <!--<td>需要修改回调地址的匿名号码</td>-->
			                            <!--<td>+862155367039</td>-->
			                        <!--</tr>-->
			                        <!--<tr>-->
			                            <!--<td>callback_url</td>-->
			                            <!--<td>String</td>-->
			                            <!--<td>是</td>-->
			                            <!--<td>回调地址</td>-->
			                            <!--<td>http://your_receiver_callback_url</td>-->
			                        <!--</tr>-->
			                    <!--</table>-->
			                <!--</div>-->
			                <!--<p>调用成功的返回值示例：</p>-->
			                <!--<pre><code>{-->
			    <!--"anonymous_number": "+862112345678",-->
			    <!--"callback_url": "http://your_receiver_callback_url"-->
			<!--}</code></pre>-->
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
	api_service("conversation");
</script>
</html>