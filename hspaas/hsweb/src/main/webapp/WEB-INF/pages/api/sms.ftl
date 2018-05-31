<!DOCTYPE HTML>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="keywords">
<meta content="华时融合平台，提供更简单，更稳定，更快速" name="description">
<title>华时融合平台—API&文档-短信API</title>
<#include "/website/assets.ftl"/>
<link href="${rc.contextPath}/static/css/api.css" rel="stylesheet" type="text/css" />
<link href="${rc.contextPath}/static/css/api_content.css" rel="stylesheet" type="text/css" />
</head>
<body>
	<#include "/website/header.ftl"/>
	<div class="syzn_box">
		<div class="inner clearfix">
			<div id="user" class="main">
				<nav class="apileft">
				<#include "/website/api_menu.ftl"/>    
				</nav>
				<div class="content">
			        <h2 class="page-title">短信API</h2>
			        <article class="doc">
			            <section class="one">
			              <h3 id="c1">1、批量发送</h3>
				          <code>
			                    URL：http://api.hspaas.cn:8080/sms/send
			                </code>
			                <p>功能说明：该接口要求提前在华时融合平台后台添加模板，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>特别说明：验证码短信，请在手机验证环节，加入图片验证码，以免被恶意攻击。
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
			                        </tr>
			                        <tr>
			                            <td>appkey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户接口账号</td>
			                        </tr>
			                        <tr>
			                            <td>appsecret</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>
			                               	数字签名(接口密码、手机号、时间戳生成32位小写MD5加摘要)，MD5(password+mobile+timestamp)
			                            </td>
			                        </tr>
			                        <tr>
			                            <td>mobile</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>发信发送的目的号码，多个号码之间用半角逗号隔开，最大不超过1000个号码</td>
			                        </tr>
			                            <td>timestamp</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>时间戳（自1970-01-01 00:00:00至今的毫秒值），短发送当前时间毫秒数，生成数字签名用，有效时间30秒，实时生成</td>
			                        </tr>
			                        <tr>
			                            <td>content</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>短信内容</td>
			                        </tr>
			                        <tr>
			                            <td>extNumber</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>扩展号，必须为数字</td>
			                        </tr>
			                        <tr>
			                            <td>attach</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>自定义信息，状态报告如需推送，将携带本数据一同推送</td>
			                        </tr>
			                         <tr>
			                            <td>callback</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>自定义状态报告推送地址，如果用户推送地址设置为固定推送地址，则本值无效，以系统绑定的固定地址为准，否则以本地址为准</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>响应格式：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width:90px;">消息体</th>
			                            <th style="width: 80px;">类型</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>code</td>
			                            <td>string</td>
			                            <td>响应码</td>
			                        </tr>
			                        <tr>
			                            <td>fee</td>
			                            <td>string</td>
			                            <td>计费条数</td>
			                        </tr>
			                        <tr>
			                            <td>message</td>
			                            <td>string</td>
			                            <td>响应码说明</td>
			                        </tr>
			                        <tr>
			                            <td>sid</td>
			                            <td>string</td>
			                            <td>任务唯一id</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>返回报文示例：</p>
			                <pre><code>{	
	"code": 0,
	"fee": "1",
	"message": "调用成功",
	"sid": "1626409221425727489"
}</code></pre>
			                <p>防骚扰过滤：默认开启。过滤规则：同1个手机发相同内容，30秒内最多发送1次，5分钟内最多发送3次。</p>
			                <h3 id="c2">2、点对点短信发送</h3>
			      			<code>
			                    URL：http://api.hspaas.cn:8080/sms/p2p
			                </code>
			                <p>功能说明：<span class="black">该接口要求提前在华时融合平台后台添加模板</span>，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width: 90px;">参数名</th>
			                            <th style="width: 70px;">类型</th>
			                            <th style="width:60px;">是否必须</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>appkey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户接口账号</td>
			                        </tr>
			                        <tr>
			                            <td>appsecret</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>
			                               	数字签名(接口密码加时间戳生成32位小写MD5加摘要)MD5(password+timestamp)
			                            </td>
			                        </tr>
			                        <tr>
			                            <td>timestamp</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>时间戳（同上），短信发送当前时间毫秒数，生成数字签名用，有效时间30秒，实时生成</td>
			                        </tr>
			                                                                                                                                                                                                                                                                                                                                    <tr>
			                            <td>body</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>报文内容，内容包含手机号码和内容，格式参考2.2.2</td>
			                        </tr>
			                        <tr>
			                            <td>extNumber</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>扩展号，必须为数字</td>
			                        </tr>
			                        <tr>
			                            <td>attach</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>自定义信息，状态报告如需推送，会携带本数据</td>
			                        </tr>
			                         <tr>
			                            <td>callback</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>自定义状态报告推送地址，如果用户推送地址设置为固定推送地址，则本值无效，以系统绑定的固定地址为准，否则以本地址为准</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>响应格式：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width:90px;">消息体</th>
			                            <th style="width: 80px;">类型</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>code</td>
			                            <td>String</td>
			                            <td>响应码</td>
			                        </tr>
			                        <tr>
			                            <td>fee</td>
			                            <td>int</td>
			                            <td>计费条数</td>
			                        </tr>
			                        <tr>
			                            <td>message</td>
			                            <td>String</td>
			                            <td>响应码说明</td>
			                        </tr>
			                        <tr>
			                            <td>sid</td>
			                            <td>String</td>
			                            <td>任务唯一id</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>返回报文示例：</p>
			                <pre><code>{
	"code": 0,
	"message": "调用成功",
	"fee": 2,
	"sid": "1626409221425727492",
}</code></pre>
			                <p>防骚扰过滤：默认开启。过滤规则：同1个手机发相同内容，30秒内最多发送1次，5分钟内最多发送3次。</p>
			                
			                <h3 id="c3">3、模板点对点短信发送</h3>
			                <code>
			                    URL：http://api.hspaas.cn:8080/sms/p2p_template
			                </code>
			                <p>功能说明：该接口要求提前在华时融合平台后台添加模板，提交短信时，系统会自动匹配审核通过的模板，匹配成功任意一个模板即可发送。
			                    系统已提供的默认模板添加签名后可以直接使用。</p>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width: 90px;">参数名</th>
			                            <th style="width: 70px;">类型</th>
			                            <th style="width:60px;">是否必须</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>appkey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户接口账号</td>
			                        </tr>
			                        <tr>
			                            <td>appsecret</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>
			                            	  数字签名(接口密码加上时间戳生成32位小写MD5加摘要)MD5(password+timestamp)
			                            </td>
			                        </tr>
			                        <tr>
			                            <td>content</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>模板内容，模板变量内容为#args#(不可更改其他)，短信内容参考2.3.2</td>
			                        </tr>
			                            <td>body</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>报文内容，内容包含手机号码和参数内容，#args#（变量内容）数量不超过10个,mobile（手机号码）数量不超过1000个</td>
			                        </tr>
			                        <tr>
			                            <td>timestamp</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>时间戳（同上），短信发送当前时间毫秒数，生成数字签名用，有效时间30秒，实时生成</td>
			                        </tr>
			                        <tr>
			                            <td>extNumber</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>扩展号，必须为数字</td>
			                        </tr>
			                         <tr>
			                            <td>attach</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>自定义信息，状态报告如需推送，会携带本数据</td>
			                        </tr>
			                         <tr>
			                            <td>callback</td>
			                            <td>String</td>
			                            <td>否</td>
			                            <td>自定义状态报告推送地址，如果用户推送地址设置为固定推送地址，则本值无效，以系统绑定的固定地址为准，否则以本地址为准</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>响应格式：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width:90px;">消息体</th>
			                            <th style="width: 80px;">类型</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>code</td>
			                            <td>String</td>
			                            <td>响应码</td>
			                        </tr>
			                        <tr>
			                            <td>fee</td>
			                            <td>int</td>
			                            <td>计费条数</td>
			                        </tr>
			                        <tr>
			                            <td>message</td>
			                            <td>String</td>
			                            <td>响应码说明</td>
			                        </tr>
			                        <tr>
			                            <td>sid</td>
			                            <td>String</td>
			                            <td>任务唯一id</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>返回报文示例：</p>
			                <pre><code>{
	"code": "4",
	"fee": 2,
	"message": "调用成功",
	"sid": "1626409221425727432"
}</code></pre>
			                <p>防骚扰过滤：默认开启。过滤规则：同1个手机发相同内容，30秒内最多发送1次，5分钟内最多发送3次。</p>
			                <h3 id="c4">4、余额查询</h3>
			                <code>
			                    URL：http://api.hspaas.cn:8080/sms/balance
			                </code>
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
			                            <td>appkey</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>用户接口账号</td>
			                        </tr>
			                        <tr>
			                            <td>appsecret</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>数字签名(接口密码加时间戳生成32位小写MD5加摘要)MD5(password+timestamp)</td>
			                        </tr>
			                         <tr>
			                            <td>timestamp</td>
			                            <td>String</td>
			                            <td>是</td>
			                            <td>时间戳（同上），短信发送当前时间毫秒数，生成数字签名用，有效时间30秒，实时生成</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>响应格式：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width:90px;">消息体</th>
			                            <th style="width: 80px;">类型</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>code</td>
			                            <td>String</td>
			                            <td>响应码(“0”为成功)</td>
			                        </tr>
			                        <tr>
			                            <td>balance</td>
			                            <td>int</td>
			                            <td>剩余短信条数</td>
			                        </tr>
			                        <tr>
			                            <td>type</td>
			                            <td>String</td>
			                            <td>付费类型 1：预付，2：后付</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>返回报文示例：</p>
			                <pre><code>{
	"code": "0", 
	"balance": 25686, 
	"type": 1
}</code></pre>
			
			                <h3 id="c5">5、推送状态报告</h3>
			                <p>功能说明：状态报告推送 接收提示由客户提供如http://ip:port /receipt/status/强烈建议 配置开启http长连接以提高效率。</p>
			                <p>推送方式：POST </p>
			                <p>推送的数据格式：</p>
			                <code>
			                   	 报头=Accept(application/json)<br>
			                   	Content-Type(application/json;charset=utf-8)
			                </code>
			                <p>响应格式：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th style="width:90px;">消息体</th>
			                            <th style="width: 80px;">类型</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>sid</td>
			                            <td>String</td>
			                            <td>提交任务ID</td>
			                        </tr>
			                        <tr>
			                            <td>mobile</td>
			                            <td>String</td>
			                            <td>手机号码</td>
			                        </tr>
			                        <tr>
			                            <td>attach</td>
			                            <td>String</td>
			                            <td>用户自定义内容（提交报文定义）</td>
			                        </tr>
			                        <tr>
			                            <td>status</td>
			                            <td>String</td>
			                            <td>状态码</td>
			                        </tr>
			                        <tr>
			                            <td>receiveTime</td>
			                            <td>String</td>
			                            <td>接收时间（有可能为空）</td>
			                        </tr>
			                        <tr>
			                            <td>errorMsg</td>
			                            <td>String</td>
			                            <td>错误描述</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>报文内容格式：</p>
			                <pre><code>[{
	"receiveTime": "2017-03-21 11:40:36",
	"mobile":"18368031231",
	"sid":"1688061909372241929",
	"status":"DELIVRD"
}, {
	"receiveTime":"2017-03-21 11:40:36",
	"mobile":"18768158605",
	"sid":"1688061909372241929",
	"status":"DELIVRD"
}]</code></pre>
			                <p>注：以上格式仅为可视化直观，实际情况无需换行或添加空格操作等</p>
			
			                <h3 id="c6">6、上行短信推送</h3>
			                <code>URL：http://ip:port/receipt/mo/</code>
			                <p>功能说明：状态报告推送 接收提示由客户提供，如http://ip:port/receipt/mo/强烈建议 配置开启http长连接以提高效率。</p>
			                <p>访问方式：POST </p>
			                <p>参数：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>参数名</th>
			                            <th>类型</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>sid</td>
			                            <td>String</td>
			                            <td>任务ID</td>
			                        </tr>
			                        <tr>
			                            <td>destnationNo</td>
			                            <td>Integer</td>
			                            <td>服务号 10690号码</td>
			                        </tr>
			                         <tr>
			                            <td>mobile</td>
			                            <td>String</td>
			                            <td>用户手机号</td>
			                        </tr>
			                         <tr>
			                            <td>content</td>
			                            <td>String</td>
			                            <td>上行短信内容</td>
			                        </tr>
			                         <tr>
			                            <td>receiveTime</td>
			                            <td>String</td>
			                            <td>回执时间（可能为空）</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			                <p>报文内容格式：</p>
			                <pre><code>{
	"sid": "1626409221425727489",
	"destnationNo": "10690000010",
	"mobile": "139********",
	"content": "已收到短信，谢谢!"
}</code></pre>
			
			                <h3 id="c7">7、短信提交响应码respcode</h3>
			                <p>响应码：</p>
			                <div class="can">
			                    <table>
			                        <tbody><tr>
			                            <th>代码</th>
			                            <th>描述</th>
			                        </tr>
			                        <tr>
			                            <td>0</td>
			                            <td>成功</td>
			                        </tr>
			                        <tr>
			                            <td>H0001</td>
			                            <td>用户请求参数不匹配</td>
			                        </tr>
			                        <tr>
			                            <td>H0002</td>
			                            <td>参数内容编码不正确</td>
			                        </tr>
			                        <tr>
			                            <td>H0003</td>
			                            <td>时间戳已过期</td>
			                        </tr>
			                        <tr>
			                            <td>H0004</td>
			                            <td>IP未报备</td>
			                        </tr>
			                        <tr>
			                            <td>H0005</td>
			                            <td>账户无效</td>
			                        </tr>
			                        <tr>
			                            <td>H0006</td>
			                            <td>账户冻结或停用</td>
			                        </tr>
			                        <tr>
			                            <td>H0007</td>
			                            <td>账户鉴权失败</td>
			                        </tr>
			                         <tr>
			                            <td>H0008</td>
			                            <td>账户鉴权失败</td>
			                        </tr>
			                         <tr>
			                            <td>H0009</td>
			                            <td>账户余额不足</td>
			                        </tr>
			                         <tr>
			                            <td>H0010</td>
			                            <td>点对点短信报文数据不符合</td>
			                        </tr>
			                        <tr>
			                            <td>H0011</td>
			                            <td>模板点对点短信报文数据不符合</td>
			                        </tr>
			                          <tr>
			                            <td>H00100</td>
			                            <td>未知异常，联系管理员</td>
			                        </tr>
			                    </tbody></table>
			                </div>
			    </div>
			</div>
		</div>
	</div>
	<#include "/website/footer.ftl"/>
</body>
<script>
	//头部菜单选择
	service("api");
	api_service("sms");
</script>
</html>