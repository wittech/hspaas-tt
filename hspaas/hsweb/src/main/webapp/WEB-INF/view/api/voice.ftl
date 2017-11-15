<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-语音API</title>
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
			        <h2 class="page-title">语音API</h2>
			        <article class="doc">
			            <section class="one">
			                <h3 id="d1">1、发语音验证码</h3>
			                <code>
			                    URL：https://voice.yunpian.com/v2/voice/send.json
			                </code>
			                <p>功能说明：通过电话直呼到用户手机并语音播放验证码，默认最多播放2次。如：您的验证码是1234。
			                    系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>
			                    特别说明：验证码短信，请在手机验证环节，加入
			                    <a href="https://blog.yunpian.com/?p=50" target="_blank">图片验证码</a>，
			                    以免被 <a href="https://blog.yunpian.com/?p=60" target="_blank">恶意攻击</a>。
			                    <a href="https://blog.yunpian.com/?p=50" target="_blank">了解详情</a>
			                </p>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width: 90px;">参数名</th>
			                            <th style="width: 70px;">类型</th>
			                            <th style="width:60px;">是否必须</th>
			                            <th>描述</th>
			                            <th style="width: 270px;">示例</th>
			                        </tr>
			                        <tr>
			                            <td>apikey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户唯一标识</td>
			                            <td>9b11127a9701975c734b8aee81ee3526</td>
			                        </tr>
			                        <tr>
			                            <td>mobile</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>接收的手机号、固话（需加区号）</td>
			                            <td>15205201314<br>
			                                01088880000
			                            </td>
			                        </tr>
			                        <tr>
			                            <td>code</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>验证码，支持4~6位阿拉伯数字</td>
			                            <td>1234</td>
			                        </tr>
			                        <tr>
			                            <td>encrypt</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>加密方式
			                                <a href="/api2.0/usage.html#secret" target="_blank" class="api20link">使用加密</a></td>
			                            <td>tea</td>
			                        </tr>
			                        <tr>
			                            <td>_sign</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>签名字段 <a href="/api2.0/usage.html#secret" target="_blank" class="api20link">参考使用加密</a></td>
			                            <td>393d079e0a00912335adfe46f4a2e10f</td>
			                        </tr>
			                        <tr>
			                            <td>callback_url</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>本条语音验证码状态报告推送地址<br>默认不开放，如有需要请联系客服申请</td>
			                            <td>http://your_receive_url_address</td>
			                        </tr>
			                        <tr>
			                            <td>display_num</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>透传号码，为保证全国范围的呼通率，云片会自动选择最佳的线路，透传的主叫号码也会相应变化。<br>
			                                如需透传固定号码则需要单独注册报备，为了确保号码真实有效，客服将要求您使用报备的号码拨打一次客服电话</td>
			                            <td></td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>注：该接口加密时，需对手机号和验证码同时加密。</p>
			                <p>部分返回参数说明：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width:90px;">返回参数名</th>
			                            <th style="width: 80px;">类型</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>count</td>
			                            <td>Integer</td>
			                            <td>成功发送的语音呼叫次数</td>
			                        </tr>
			                        <tr>
			                            <td>fee</td>
			                            <td>Integer</td>
			                            <td>扣费金额，一次语音验证码呼叫扣一条短信</td>
			                        </tr>
			                        <tr>
			                            <td>sid</td>
			                            <td>String</td>
			                            <td>记录id，32位的唯一字符串</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>调用成功的返回值示例：</p>
			                <pre><code>{
							    "count": 1,   //成功发送的语音呼叫次数
							    "fee": 1,     //扣费金额，一次语音验证码呼叫扣一条短信
							    "sid": "931ee0bac7494aab8a422fff5c6be3ea"   //记录id，32位的唯一字符串
							}</code></pre>
			                <p>防骚扰过滤：默认开启。过滤规则：同1个手机发相同内容，30秒内最多发送1次，5分钟内最多发送3次。</p>
			                <h3 id="d2">2、获取状态报告</h3>
			                <code>
			                    URL：https://voice.yunpian.com/v2/voice/pull_status.json
			                </code>
			                <p>功能说明：开通此接口功能后，我们将为您独立再保存一份新生产的状态报告数据，保存时间为72小时。</p>
			                <p>您可以通过此接口获取新产生的状态报告。注意，已成功获取的数据将会删除，请妥善处理接口返回的数据。</p>
			                <p>备注：该接口为高级接口，默认不开放，如有需要请向客服申请开通。 </p>
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
			                            <td>page_size</td>
			                            <td>Integer</td>
			                            <td>否</td>
			                            <td>每页个数，最大100个，默认20个</td>
			                            <td>20</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>调用成功的返回值示例：</p>
			                <pre><code>[{
							    "sid": "931ee0bac7494aab8a422fff5c6be3ea", //语音记录id，32位的唯一字符串
							    "uid": null, //用户自定义id
							    "user_receive_time": "2015-07-01 14:29:12", //用户接收时间
							    "duration": 10, //通话时长
							    "error_msg": null, //接收失败的原因，如："用户未应答"
							    "mobile": "15205201314", //接收手机号
							    "report_status": "SUCCESS" //接收状态有:SUCCESS/FAIL
							},
							{
							    "sid": "821ee0bac7494aab8a422fff5c6be3ea",//语音记录id，32位的唯一字符串
							    "uid": null,//用户自定义id
							    "user_receive_time": "2015-07-01 14:30:09",//用户接收时间
							    "duration": 0,//通话时长
							    "error_msg": "用户未应答",//接收失败的原因，如："用户未应答"
							    "mobile": "15205201315",//接收手机号
							    "report_status": "FAIL"//接收状态有:SUCCESS/FAIL
							}]</code></pre>
			                <h3 id="d3">3、推送状态报告</h3>
			                <p>功能说明：开通此接口后，我们将为您实时推送最新的状态报告。您需要提供一个url地址，接受http post请求。
			                    该接口一次最多推送500个状态报告，为不影响推送速度，建议先接受数据后再做异步处理。</p>
			                <p>备注：该接口为高级接口，默认不开放，可以在云片用户后台开启，
			                    <a href="/admin/module/account_setting_pushurl" target="_blank">去开启</a>。
			                </p>
			                <p>推送方式：POST</p>
			                <p>推送的数据格式：</p>
			                <code>
			                    参数名 = 经过urlencode编码的数据
			                </code>
			                <p>形式如：</p>
			                <code>voice_status = urlencode(json)</code>
			                <p>服务端推送数据形式如下：</p>
			                <code>curl --data "voice_status=url_encode_json" http://your_receive_url_address</code>
			                <p>其中json数据示例为：</p>
			                <pre><code>[{
							    "sid": "6e77feeab89f445387308db604e2ec8d", //语音记录id，32位的唯一字符串
							    "uid": null, //用户自定义id
							    "user_receive_time": "2015-07-01 14:29:12",//用户接收时间
							    "duration": 10,//通话时长
							    "error_msg": null,//接收失败的原因，如："用户未应答"
							    "mobile": "15205201314",//接收手机号
							    "report_status": "SUCCESS" //接收状态有:SUCCESS/FAIL
							},{
							    "sid": "302e7fd51f6748cfa70a6fc6aea44c9f",//语音记录id，32位的唯一字符串
							    "uid": null,//用户自定义id
							    "user_receive_time": "2015-07-01 14:33:23",//用户接收时间
							    "duration": 0,//通话时长
							    "error_msg": "用户未应答",//接收失败的原因，如："用户未应答"
							    "mobile": "15205201315",//接收手机号
							    "report_status": "FAIL"//接收状态有:SUCCESS/FAIL
							}]</code></pre>
			                <p>接收到数据后，请从参数voice_status中取值，取到值使用urldecode解码后为实际json格式数据
			                    处理成功请返回字符串"SUCCESS"或"0"，其他返回值将被认为是失败。该接口失败重试次数为3次，每次间隔5分钟。</p>
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
	api_service("voice");
</script>
</html>