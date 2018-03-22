# 短信接口文档

#

#







目录

| 文档变更记录 |
| --- |
| |
| 1 |
| |
| |
| |
| |
| |
| |
| |
| |
| |
| |
| |
| |
| |
| |
| |
| |
| |

概述

## 0.1协议说明

基于HTTP协议，通过HTTP的 POST 方式交换数据，推荐使用HTTP长连接,双方需要保证数据传输的完整性和安全性。安全认证采用数字签名的方式，数字签名根据密码、手机号、时间戳32位MD5生成。

## 0.2字符编码

服务器请求和返回都采用UTF-8编码。

# 1接口说明

## 1.1短信发送

### 1.1.1请求格式

| HTTP **方法** | URL |
| --- | --- |
| POST | http://api.hspaas.cn:8080/sms/send |
| 消息参数 | 描述 |
| appkey | 必填，用户接口账号 |
| appsecret | 必填，数字签名(接口密码、手机号、时间戳戳MD5加密生成32位小写)MD5(password+mobile+timestamp) |
| mobile | 必填，发信发送的目的号码，多个号码之间用半角逗号隔开，最大不超过1000个号码 |
| timestamp | 必填，时间戳，短信发送当前时间毫秒数，生成数字签名用，有效时间30秒，实时生成 |
| content | 必填，短信内容 |
| extNumber | 选填，扩展号，必须为数字 |
| attach | 选填，自定义信息，状态报告如需推送，将携带本数据一同推送 |
| callback | 非必填，自定义状态报告推送地址，如果用户推送地址设置为固定推送地址，则本值无效，以系统绑定的固定地址为准，否则以本地址为准 |

### 1.1.2响应格式

| 消息体 | 类型 | 描述 |
| --- | --- | --- |
| code | string | 响应码 |
| fee | string | 计费条数 |
| message | string | 响应码说明 |
| sid | string | 任务唯一id |

**返回报文示例** ：

{&quot;code&quot;:&quot;0&quot;, fee:&quot;1&quot;, &quot;message&quot;:&quot;调用成功&quot;, &quot;sid&quot;:&quot;1626409221425727489&quot;}

## 1.2余额查询

### 1.2.1请求格式

| HTTP **方法** | URL |
| --- | --- |
| POST | http://api.hspaas.cn:8080/sms/balance |
| 消息参数 | 描述 |
| appkey | 必填，用户接口账号 |
| appsecret | 必填，数字签名(接口密码、时间戳MD5加密生成32位小写)MD5(password+timestamp) |
| timestamp | 必填，时间戳，短信发送当前时间毫秒数，生成数字签名用，有效时间30秒，实时生成 |

### 1.2.2响应格式

| 消息体 | 类型 | 描述 |
| --- | --- | --- |
| code | string | 响应码(&quot;0&quot;为成功) |
| balance | string | 剩余短信条数 |
| type | string | 付费类型 1：预付，2：后付 |



**返回**** 报文示例 ****：**

{&quot;code&quot;: &quot;0&quot;, &quot;balance&quot;: &quot;25686&quot;,&quot;type&quot;:&quot;1&quot;}

## 1.3状态报告推送

### 1.3.1推送格式

| HTTP **方法** | URL |
| --- | --- |
| POST | 状态报告推送 接收提示由客户提供，如http://ip: port/receipt/status/强烈建议 配置开启http长连接以提高效率 |
| 报头 | 描述 |
| Accept | application/json |
| Content-Type | application/json;charset=utf-8 |
| 消息体 | 描述 |
| sid | 提交任务ID |
| mobile | 手机号码 |
| attach | 用户自定义内容（提交报文定义） |
| status | 状态码 |
| receiveTime | 接收时间（有可能为空） |
| errorMsg | 错误描述 |

### 1.3.2报文内容格式

#####   请求格式：  
```json  
{  
    "requestObject":  
    {  
        "username":"",//用户名  
        "password":"",//用户密码  
        "age":1 //年龄  
    }//请求对象  
}  
```  


[

{

&quot;receiveTime&quot;:&quot;2017-03-21 11:40:36&quot;,

&quot;mobile&quot;:&quot;18368031231&quot;,

&quot;sid&quot;:&quot;1688061909372241929&quot;,

&quot;status&quot;:&quot;DELIVRD&quot;

},

{

&quot;receiveTime&quot;:&quot;2017-03-21 11:40:36&quot;,

&quot;mobile&quot;:&quot;18768158605&quot;,

&quot;sid&quot;:&quot;1688061909372241929&quot;,

&quot;status&quot;:&quot;DELIVRD&quot;

}

]

注：以上格式仅为可视化直观，实际情况无需换行或添加空格操作等。

## 1.4上行短信推送

### 1.4.1推送格式

| HTTP **方法** | URL |
| --- | --- |
| POST | 状态报告推送 接收提示由客户提供，如http://ip: port/receipt/mo/强烈建议 配置开启http长连接以提高效率 |
| 报头 | 描述 |
| Accept | application/json |
| Content-Type | application/json;charset=utf-8 |
| 消息体 | 描述 |
| sid | 任务ID |
| destnationNo | 服务号 10690号码 |
| mobile | 用户手机号 |
| content | 上行短信内容 |
| receiveTime | 回执时间（可能为空） |

### 1.4.2报文内容格式

{

  &quot;sid&quot;: &quot;1626409221425727489&quot;,

  &quot;destnationNo&quot;: &quot;10690000010&quot;,

  &quot;mobile&quot;: &quot;139\*\*\*\*\*\*\*\*&quot;,

&quot;content&quot;: &quot;已收到短信，谢谢!&quot;

}

# 2附录-码表

## 2.1短信提交响应码respcode

| 代码 | 描述 |
| --- | --- |
| 0 | 成功 |
| H0001 | 用户请求参数不匹配 |
| H0002 | 参数内容编码不正确 |
| H0003 | 时间戳已过期 |
| H0004 | IP未报备 |
| H0005 | 账户无效 |
| H0006 | 账户冻结或停用 |
| H0007 | 账户鉴权失败 |
| H0008 | 账户计费异常 |
| H0009 | 账户余额不足 |
| H0010 | 点对点短信报文数据不符合 |
| H0011 | 模板点对点短信报文数据不符合 |
| H0100 | 未知异常，联系管理员 |