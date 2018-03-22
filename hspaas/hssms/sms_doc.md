#       用户注册接口文档  
  
### 1. 用户注册查询【前台】  
**接口地址**： /frnt/user/save.json  
**请求对象**： BaseOperationRequest< UserVO >  
**响应对象**： BaseOperationResponse< Long  >  
**参数校验**： 用户名，密码均不能为空  
**接口描述**： 用户注册，供前台调用  
**Auth-date**：[add by XXX 2016-5-26]  
  
  
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
  
#####   返回格式：  
```json  
{  
    "messageCode":"1",//错误代码（成功为1）  
    "responseObject":  
    {  
        1  
    },//返回对象  
    "messageDescription":"error",//错误信息  
    "responseError":  
    [  
        {  
            "message":"null",//提示信息  
            "fieldName":"name",//字段名  
            "rejectedValue":1//错误值  
        }  
    ]//验证错误内容  
}  
  
```  
  
#####   请求示例：  
```json  
{  
    "requestObject":  
    {  
        "username":"zhangsan",  
        "password":"123456",//用户密码  
        "age":1 //年龄  
    }//请求对象  
}  
  
```  
  
#####   响应示例：  
```json  
{  
    "messageCode":"1",//错误代码（成功为1）  
    "responseObject":  
    {  
        1  
    },//返回对象  
    "messageDescription":"error",//错误信息  
    "responseError":  
    [  
        {  
            "message":"null",//提示信息  
            "fieldName":"name",//字段名  
            "rejectedValue":1//错误值  
        }  
    ]//验证错误内容  
}  
```  
***  