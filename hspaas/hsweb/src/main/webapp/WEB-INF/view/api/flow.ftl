<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-流量API</title>
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
			        <h2 class="page-title">流量API</h2>
			        <article class="doc">
			            <section class="one">
			                <h3 id="e1">1、查询流量包</h3>
			                <code>
			                    URL：https://flow.yunpian.com/v2/flow/get_package.json
			                </code>
			                <p>功能说明：向指定的手机号查询对应的流量包 </p>
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
			                            <td>carrier</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>运营商ID<br>传入该参数则获取指定运营商的流量包，<br>否则获取所有运营商的流量包</td>
			                            <td>移动：10086<br>联通：10010<br>电信：10000</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>调用成功的返回值示例：</p>
			                <pre><code>[
							    {
							        "sn": 1008601, //流量包唯一ID
							        "carrier_price": 3, //运营商报价
							        "discount": 0.95, //云片折扣，云片价格为：carrier_price * discount
							        "capacity": 10, //流量包数据流量大小，单位M
							        "carrier": 10086, //运营商代号
							        "name": "中国移动10M" //流量包完整名称
							    },
							    {
							        "sn": 1001001,
							        "carrier_price": 3,
							        "discount": 0.95,
							        "capacity": 20,
							        "carrier": 10010,
							        "name": "中国联通20M"
							    },
							    {
							        "sn": 1000001,
							        "carrier_price": 1,
							        "discount": 0.95,
							        "capacity": 5,
							        "carrier": 10000,
							        "name": "中国电信5M"
							    }
							]</code></pre>
			
			                <h3 id="e2">2、充值流量</h3>
			                <code>
			                    URL：https://flow.yunpian.com/v2/flow/recharge.json
			                </code>
			                <p>功能说明：向指定的手机号充值对应的流量包 </p>
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
			                            <td>mobile</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>接收的手机号（仅支持大陆号码）</td>
			                            <td>15205201314</td>
			                        </tr>
			                        <tr>
			                            <td>sn</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>
			                                流量包的唯一ID
			                                <a class="streamid" href="/price.html#streamTable" target="_blank">点击查看</a>
			                            </td>
			                            <td>1008601</td>
			                        </tr>
			                        <tr>
			                            <td>encrypt</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>
			                                加密方式
			                                <a href="/api2.0/usage.html#secret" target="_blank" class="api20link">使用加密</a>
			                            </td>
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
			                            <td>本条流量充值的状态报告推送地址<br>默认不开放，如有需要请联系客服申请</td>
			                            <td>http://your_receive_url_address</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>注：该接口加密时，仅需对手机号加密。</p>
			                <p>调用成功的返回值示例：</p>
			                <pre><code>{
							    "count": 1,//成功提交充值请求的数量
							    "fee": 2.85,//扣费金额
							    "sid": "a1cdcd5a6eac4a6da42e2d4394c76003"//记录id，32位的唯一字符串
							}</code></pre>
			                <p>防刷过滤：默认开启。过滤规则：同1个手机充值相同内容，30秒内最多充值1次，5分钟内最多充值3次。</p>
			                <h3 id="e3">3、获取状态报告</h3>
			                <code>
			                    URL：https://flow.yunpian.com/v2/flow/pull_status.json
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
			                <pre><code>[
							    {
							        "sid": "58a79967659b4248980dc456603adfc5", // 充值记录id，32位的唯一字符串
							        "user_receive_time": "2015-11-18 14:12:04", // 接收时间
							        "error_msg": "052", // 接收失败的错误信息
							        "mobile": "15205201314", // 接收手机号
							        "report_status": "FAIL", // 接收状态有:SUCCESS/FAIL
							        "sn" : 1008601  //流量包的唯一ID
							    },
							    {
							        "sid": "181e48a78b534c708a511057d60de5dd",
							        "user_receive_time": null,
							        "error_msg": "Package error",
							        "mobile": "15205201314",
							        "report_status": "FAIL",
							        "sn" : 1008601
							    },
							    {
							        "sid": "75c9077fc1844a2381be6c9e01b135c6",
							        "user_receive_time": "2015-11-19 16:02:44",
							        "error_msg": null,
							        "mobile": "15205201314",
							        "report_status": "SUCCESS",
							        "sn" : 1008601
							    }
							]</code></pre>
			                <h3 id="e4">4、推送状态报告</h3>
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
			                <code>flow_status = urlencode(json)</code>
			                <p>服务端推送数据形式如下：</p>
			                <code>curl --data "flow_status=url_encode_json" http://your_receive_url_address</code>
			                <p>其中json数据示例为：</p>
			                <pre><code>[
							    {
							        "sid": "16da4a1e83a149718f9e2d46037a6ec7", // 充值记录id，32位的唯一字符串
							        "user_receive_time": null, // 接收时间
							        "error_msg": "Package error", // 接收失败的错误信息
							        "mobile": "15205201314", // 接收手机号
							        "report_status": "FAIL", // 接收状态有:SUCCESS/FAIL
							        "sn" : 1008601  //流量包的唯一ID
							    },
							    {
							        "sid": "4e57c8ad39934dab9934009a0d8930b3",
							        "user_receive_time": "2015-11-19 16:11:40",
							        "error_msg": "052",
							        "mobile": "15205201314",
							        "report_status": "FAIL",
							        "sn" : 1008601
							    },
							    {
							        "sid": "a0fd80aec0ef4bdb9ac852bb4078fce2",
							        "user_receive_time": "2015-11-19 16:23:35",
							        "error_msg": null,
							        "mobile": "15205201314",
							        "report_status": "SUCCESS",
							        "sn" : 1008601
							    }
							]</code></pre>
			                <p>接收到数据后，请从参数flow_status中取值，取到值使用urldecode解码后为实际json格式数据
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
	api_service("flow");
</script>
</html>