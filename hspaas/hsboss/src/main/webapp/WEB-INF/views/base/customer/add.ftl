<!DOCTYPE html>
<html lang="en">

<head>
    <meta http-equiv="content-type" content="text/html;charset=UTF-8"/>
    <meta charset="utf-8">

    <title>融合平台</title>
    <link href="${BASE_PATH}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/style.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/js/confirm/jquery-confirm.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/font-awesome.min.css" rel="stylesheet">
    <link href="${BASE_PATH}/resources/css/bootstrap/pace.min.css" rel="stylesheet">
    <script src="${BASE_PATH}/resources/js/bootstrap/pace.min.js"></script>
    <script src="${BASE_PATH}/resources/js/common.js"></script>
</head>

<body>
<div id="container" class="effect mainnav-lg navbar-fixed mainnav-fixed">
<#include "/WEB-INF/views/main/top.ftl">
    <div class="boxed">

        <div id="content-container">

            <div class="pageheader">
                <div class="breadcrumb-wrapper"><span class="label">所在位置:</span>
                    <ol class="breadcrumb">
                        <li><a href="#"> 管理平台 </a></li>
                        <li><a href="#"> 系统管理 </a></li>
                        <li class="active">客户信息管理</li>
                    </ol>
                </div>
            </div>
            <div id="page-content">
                <div class="panel">
                    <!-- Panel heading -->
                    <div class="panel-heading">
                        <div class="pull-right"  style="margin-top: 10px;margin-right: 20px;">
                        <#--<a onclick="formSubmit();" class="btn btn-primary">提&nbsp;&nbsp;交</a>-->
                        </div>
                        <h3 class="panel-title">
                            <span>新增客户</span>
                        </h3>
                    </div>
                    <!-- Panel body -->
                    <form id="myform" class="form-horizontal">
                        <div class="panel-body" style="background:#fcfcfc">


                            <div class="panel panel-info">
                                <div class="panel-heading" style="height:40px">
                                    <h1 class="panel-title" style="font-size:14px;line-height:40px">帐号信息</h1>
                                </div>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-xs-1 control-label">登录名</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[required,maxSize[100]]"
                                                   name="user.userName" id="userName" placeholder="请输入登录名">
                                        </div>

                                        <label class="col-xs-1 control-label">手机号</label>
                                        <div class="col-xs-4">
                                            <input type="text"
                                                   class="form-control validate[required,maxSize[13],integer]"
                                                   name="user.mobile" id="mobile" placeholder="请输入手机号">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-1 control-label">E-mail</label>
                                        <div class="col-xs-4">
                                            <input type="text"
                                                   class="form-control validate[optional,maxSize[100],custom[email]]"
                                                   id="email" name="user.email" placeholder="请输入E-mail">
                                        </div>
                                        <label class="col-xs-1 control-label">公司/个人</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[required,maxSize[100]]"
                                                   id="name" name="user.name" placeholder="请输入公司名称或个人姓名">
                                        </div>
                                    </div>
                                    <div class="form-group">

                                        <label class="col-xs-1 control-label">用户昵称</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[optional,maxSize[100]]"
                                                   id="knick" name="user.knick" placeholder="请输入用户昵称">
                                        </div>
                                    </div>
                                </div>
                            </div>


                            <div class="panel panel-info" style="margin-top:10px">
                                <div class="panel-heading" style="height:40px">
                                    <h1 class="panel-title" style="font-size:14px;line-height:40px">基础信息</h1>
                                </div>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-xs-1 control-label">姓名</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[required,maxSize[100]]"
                                                   name="userProfile.fullName" id="fullName" placeholder="请输入姓名">
                                        </div>

                                        <label class="col-xs-1 control-label">身份证号</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[optional,maxSize[18]]"
                                                   name="userProfile.cardNo" id="cardNo" placeholder="请输入身份证号">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-1 control-label">地址</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[optional,maxSize[100]]"
                                                   id="address" name="userProfile.address" placeholder="请输入地址">
                                        </div>
                                        <label class="col-xs-1 control-label">电话号码</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[optional,maxSize[100]]"
                                                   id="telephone" name="userProfile.telephone" placeholder="请输入电话号码">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-1 control-label">公司名称</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[optional,maxSize[100]]"
                                                   id="company" name="userProfile.company" placeholder="请输入公司名称">
                                        </div>
                                        <label class="col-xs-1 control-label">所在城市</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[optional,maxSize[100]]"
                                                   id="city" name="userProfile.city" placeholder="请输入所在城市">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-xs-1 control-label">邮编</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[optional,maxSize[8]]"
                                                   id="zipcode" name="userProfile.zipcode" placeholder="请输入邮编">
                                        </div>
                                        <label class="col-xs-1 control-label">生日</label>
                                        <div class="col-xs-4">
                                            <input type="text" class="form-control validate[optional,maxSize[100]]"
                                                   id="birthday" name="userProfile.birthday" placeholder="请输入生日">
                                        </div>
                                    </div>

                                    <div class="form-group">
                                        <label class="col-xs-1 control-label">性别</label>
                                        <div class="col-xs-4">
                                            <label class="form-radio form-icon"><input type="radio" id="nan"
                                                                                       name="gender" checked value="M">男</label>&nbsp;&nbsp;
                                            <label class="form-radio form-icon"><input type="radio" id="nv"
                                                                                       name="gender" value="W">女</label>&nbsp;&nbsp;
                                            <label class="form-radio form-icon"><input type="radio" id="weizhi"
                                                                                       name="gender"
                                                                                       value="N">未知</label>&nbsp;&nbsp;
                                        </div>
                                        <label class="col-xs-1 control-label">备注</label>
                                        <div class="col-xs-4">
                                            <textarea class="form-control" rows="4" name="userProfile.remark"
                                                      placeholder="请输入备注"></textarea>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        <#function getDefaultGroupId key>
                            <#list defaultGroupList as dl>
                                <#if dl.attrKey == key>
                                    <#return dl.attrValue>
                                </#if>
                            </#list>
                            <#return 0>
                        </#function>
                        <#macro init_amount key>
                            <#if balanceConfigMap?? && balanceConfigMap?size gt 0>
                                <#assign obj = balanceConfigMap[key] />
                                <#if obj??>
                                    <div class="form-group">

                                        <#assign groupList = "" />
                                        <#assign showName = "" />
                                        <#assign defaultGroupId = 0/>
                                    <#-- sms_amount flux_money voice_amount-->
                                        <#if key == 'voice_amount'>
                                            <#assign groupList = voicePassageGroupList?if_exists />
                                            <#assign showName = "语音通道组" />
                                            <#assign defaultGroupId = getDefaultGroupId('vs_default_passage_group')/>
                                        <#elseif key='sms_amount'>
                                            <#assign groupList = smsPassageGroupList?if_exists />
                                            <#assign showName = "短信通道组" />
                                            <#assign defaultGroupId = getDefaultGroupId('sms_default_passage_group')/>
                                        <#elseif key='mms_amount'>
                                            <#assign groupList = mmsPassageGroupList?if_exists />
                                            <#assign showName = "彩信通道组" />
                                            <#assign defaultGroupId = getDefaultGroupId('mms_default_passage_group')/>
                                        <#elseif key == 'flux_money'>
                                            <#assign groupList = fsPassageGroupList?if_exists />
                                            <#assign showName = "流量通道组" />
                                            <#assign defaultGroupId = getDefaultGroupId('fs_default_passage_group')/>
                                        </#if>


                                        <label class="col-xs-2 control-label">${showName}</label>
                                        <div class="col-xs-3">
                                            <select class="form-control" name="${key}_group_id">
                                                <#if groupList?? && groupList?size gt 0 && groupList != ''>
                                                    <#list groupList as gl>
                                                        <#assign gId = gl.id + '' />
                                                        <option value="${gl.id}"
                                                                <#if defaultGroupId == gId>selected</#if>>${gl.passageGroupName!''}</option>
                                                    </#list>
                                                <#else>
                                                    <option value="-1">请选择${showName}</option>
                                                </#if>
                                            </select>
                                        </div>

                                        <label class="col-xs-2 control-label">${obj.attrName}</label>
                                        <div class="col-xs-3">
                                            <input type="text"
                                                   class="form-control validate[required,maxSize[100],number,min[0]]"
                                                   value="${obj.attrValue?if_exists}" name="${obj.attrKey}"
                                                   id="${obj.attrKey}" placeholder="请输入${obj.attrName}">
                                        </div>
                                    </div>
                                </#if>
                            </#if>
                        </#macro>

                            <div class="panel panel-info" style="margin-top:10px">
                                <div class="panel-heading" style="height:40px">
                                    <h1 class="panel-title" style="font-size:14px;line-height:40px">短信设置</h1>
                                </div>
                                <div class="panel-body">
                                <@init_amount key='sms_amount'></@init_amount>

                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">短信发送状态报告URL</label>
                                        <div class="col-xs-7">
                                            <label class="form-radio form-icon"><input type="radio" id="sendUrlY"
                                                                                       name="sendUrlFlag" value="1">启用</label>&nbsp;&nbsp;
                                            <label class="form-radio form-icon"><input type="radio" id="sendUrlN"
                                                                                       name="sendUrlFlag" checked value="0">不启用</label>
                                        </div>
                                    </div>

                                    <div class="form-group sendUrlYDiv" style="display:none">
                                        <label class="col-xs-2 control-label"></label>
                                        <div class="col-xs-7">
                                            <label class="form-radio form-icon"><input type="radio" id="sendUrlType0"
                                                                                       name="sendUrlType" value="2">
                                                不设置固定推送地址，在每个发送请求内传callback_url</label>
                                        </div>
                                    </div>

                                    <div class="form-group sendUrlYDiv" style="display:none">
                                        <label class="col-xs-2 control-label"></label>
                                        <div class="col-xs-7">
                                            <label class="form-radio form-icon"><input type="radio" id="sendUrlType1"
                                                                                       name="sendUrlType" checked value="1">
                                                设置固定推送地址，无需每次发送请求都传callback_url</label>
                                        </div>
                                    </div>

                                    <div class="form-group sendUrlYDiv sendTypeDiv" style="display:none">
                                        <label class="col-xs-2 control-label"></label>
                                        <div class="col-xs-7">
                                            <input type="text" class="form-control validate[maxSize[100]]"
                                                   name="smsSendUrl" id="smsSendUrl" placeholder="请输入短信发送状态报告URL">
                                        </div>
                                    </div>



                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">短信上行状态报告URL</label>
                                        <div class="col-xs-7">
                                            <input type="text" class="form-control validate[maxSize[100]]"
                                                   name="smsUpUrl" id="smsUpUrl" placeholder="请输入短信上行状态报告URL">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">短信返还规则</label>
                                        <div class="col-xs-3">
                                            <select class="form-control" name="userSmsConfig.smsReturnRule">
                                            <#if smsReturnRules??>
                                                <#list smsReturnRules as a>
                                                    <option value="${a.value!''}">${a.title!''}</option>
                                                </#list>
                                            </#if>
                                            </select>
                                        </div>
                                        <label class="col-xs-2 control-label">第一条计费字数</label>
                                        <div class="col-xs-3">
                                            <input type="text"
                                                   class="form-control validate[required,maxSize[100],custom[number],min[1]]"
                                                   value="70" name="userSmsConfig.smsWords" id="smsWords"
                                                   placeholder="请输入计费字数">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">短信是否需要审核</label>
                                        <div class="col-xs-3">
                                            <select class="form-control" name="userSmsConfig.messagePass">
                                            <#if smsMessagePass??>
                                                <#list smsMessagePass as a>
                                                    <option value="${a.value!''}" selected>${a.title!''}</option>
                                                </#list>
                                            </#if>
                                            </select>
                                        </div>
                                        <label class="col-xs-2 control-label">短信超时时间（毫秒）</label>
                                        <div class="col-xs-3">
                                            <input type="text"
                                                   class="form-control validate[required,maxSize[100],custom[number],min[0]]"
                                                   name="userSmsConfig.smsTimeout" id="smsTimeout" value="5000"
                                                   placeholder="短信超时时间（毫秒）">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">模板是否需要报备</label>
                                        <div class="col-xs-3">
                                            <select class="form-control" name="userSmsConfig.needTemplate">
                                            <#if smsNeedTemplates??>
                                                <#list smsNeedTemplates as a>
                                                    <option value="${a.value!''}" selected>${a.title!''}</option>
                                                </#list>
                                            </#if>
                                            </select>
                                        </div>
                                        <label class="col-xs-2 control-label">自动提取短信模板</label>
                                        <div class="col-xs-3">
                                            <select class="form-control" name="userSmsConfig.autoTemplate">
                                            <#if smsPickupTemplates??>
                                                <#list smsPickupTemplates as a>
                                                    <option value="${a.value!''}">${a.title!''}</option>
                                                </#list>
                                            </#if>
                                            </select>
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">提交时间间隔</label>
                                        <div class="col-xs-3">
                                        	<input type="text"
                                                   class="form-control validate[maxSize[10],custom[number],min[0]]"
                                                   name="userSmsConfig.submitInterval" id="submitInterval" value="30"
                                                   placeholder="提交时间间隔（秒）">
                                        </div>
                                        <label class="col-xs-2 control-label">提交次数上限</label>
                                        <div class="col-xs-3">
                                            <input type="text"
                                                   class="form-control validate[maxSize[10],custom[number],min[0]]"
                                                   name="userSmsConfig.limitTimes" id="limitTimes" value="10"
                                                   placeholder="提交次数上限（次）">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">签名途径</label>
                                        <div class="col-xs-3">
                                            <select class="form-control" name="userSmsConfig.signatureSource">
                                            <#if smsSignatureSources??>
                                                <#list smsSignatureSources as a>
                                                    <option value="${a.value!''}">${a.title!''}</option>
                                                </#list>
                                            </#if>
                                            </select>
                                        </div>

                                        <label class="col-xs-2 control-label">付费方式</label>
                                        <div class="col-xs-3">

                                            <label class="form-radio form-icon"><input type="radio" id="smsPayType1"
                                                                                       name="smsPayType" checked value="1">
                                                预付</label>
                                            <label class="form-radio form-icon"><input type="radio" id="smsPayType2"
                                                                                       name="smsPayType" value="2">
                                                后付</label>
                                        </div>

                                    </div>

                                    <div class="form-group">
                                    	<label class="col-xs-2 control-label">扩展号码</label>
                                        <div class="col-xs-3">
                                            <input type="text"
                                                   class="form-control validate[maxSize[10],custom[number]]"
                                                   name="userSmsConfig.extNumber" id="smsTimeout" placeholder="接入号（数字）">
                                        </div>
                                        <label class="col-xs-2 control-label">签名内容</label>
                                        <div class="col-xs-3">
                                            <textarea class="form-control" rows="4"
                                                      name="userSmsConfig.signatureContent"
                                                      placeholder="请输入备注"></textarea>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            
                            <div class="panel panel-info" style="margin-top:10px">
                                <div class="panel-heading" style="height:40px">
                                    <h1 class="panel-title" style="font-size:14px;line-height:40px">彩信设置</h1>
                                </div>
                                <div class="panel-body">
                                <@init_amount key='mms_amount'></@init_amount>

                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">彩信发送状态报告URL</label>
                                        <div class="col-xs-7">
                                            <label class="form-radio form-icon"><input type="radio" id="mmsSendUrlY"
                                                                                       name="mmsSendUrlFlag" value="1">启用</label>&nbsp;&nbsp;
                                            <label class="form-radio form-icon"><input type="radio" id="mmsSendUrlN"
                                                                                       name="mmsSendUrlFlag" checked value="0">不启用</label>
                                        </div>
                                    </div>

                                    <div class="form-group mmsSendUrlYDiv" style="display:none">
                                        <label class="col-xs-2 control-label"></label>
                                        <div class="col-xs-7">
                                            <label class="form-radio form-icon"><input type="radio" id="mmsSendUrlType0"
                                                                                       name="mmsSendUrlType" value="2">
                                                不设置固定推送地址，在每个发送请求内传callback_url</label>
                                        </div>
                                    </div>

                                    <div class="form-group mmsSendUrlYDiv" style="display:none">
                                        <label class="col-xs-2 control-label"></label>
                                        <div class="col-xs-7">
                                            <label class="form-radio form-icon"><input type="radio" id="mmsSendUrlType2"
                                                                                       name="mmsSendUrlType" checked value="1">
                                                设置固定推送地址，无需每次发送请求都传callback_url</label>
                                        </div>
                                    </div>

                                    <div class="form-group mmsSendUrlYDiv sendTypeDiv" style="display:none">
                                        <label class="col-xs-2 control-label"></label>
                                        <div class="col-xs-7">
                                            <input type="text" class="form-control validate[maxSize[100]]"
                                                   name="mmsSendUrl" id="mmsSendUrl" placeholder="请输入彩信发送状态报告URL">
                                        </div>
                                    </div>



                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">彩信上行状态报告URL</label>
                                        <div class="col-xs-7">
                                            <input type="text" class="form-control validate[maxSize[100]]"
                                                   name="mmsUpUrl" id="mmsUpUrl" placeholder="请输入彩信上行状态报告URL">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">彩信返还规则</label>
                                        <div class="col-xs-3">
                                            <select class="form-control" name="userMmsConfig.mmsReturnRule">
                                            <#if smsReturnRules??>
                                                <#list smsReturnRules as a>
                                                    <option value="${a.value!''}">${a.title!''}</option>
                                                </#list>
                                            </#if>
                                            </select>
                                        </div>
                                        <label class="col-xs-2 control-label">扩展号码</label>
                                        <div class="col-xs-3">
                                            <input type="text"
                                                   class="form-control validate[maxSize[10],custom[number]]"
                                                   name="userMmsConfig.extNumber" id="extNumber" placeholder="接入号（数字）">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">彩信是否需要审核</label>
                                        <div class="col-xs-3">
                                            <select class="form-control" name="userMmsConfig.messagePass">
                                            <#if smsMessagePass??>
                                                <#list smsMessagePass as a>
                                                    <option value="${a.value!''}" selected>${a.title!''}</option>
                                                </#list>
                                            </#if>
                                            </select>
                                        </div>
                                        <label class="col-xs-2 control-label">彩信超时时间（毫秒）</label>
                                        <div class="col-xs-3">
                                            <input type="text"
                                                   class="form-control validate[required,maxSize[100],custom[number],min[0]]"
                                                   name="userMmsConfig.mmsTimeout" id="mmsTimeout" value="5000"
                                                   placeholder="彩信超时时间（毫秒）">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">提交时间间隔</label>
                                        <div class="col-xs-3">
                                        	<input type="text"
                                                   class="form-control validate[maxSize[10],custom[number],min[0]]"
                                                   name="userMmsConfig.submitInterval" id="submitInterval" value="30"
                                                   placeholder="提交时间间隔（秒）">
                                        </div>
                                        <label class="col-xs-2 control-label">提交次数上限</label>
                                        <div class="col-xs-3">
                                            <input type="text"
                                                   class="form-control validate[maxSize[10],custom[number],min[0]]"
                                                   name="userMmsConfig.limitTimes" id="limitTimes" value="10"
                                                   placeholder="提交次数上限（次）">
                                        </div>
                                    </div>
                                    
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">付费方式</label>
                                        <div class="col-xs-3">

                                            <label class="form-radio form-icon"><input type="radio" id="smsPayType1"
                                                                                       name="mmsPayType" checked value="1">
                                                预付</label>
                                            <label class="form-radio form-icon"><input type="radio" id="smsPayType2"
                                                                                       name="mmsPayType" value="2">
                                                后付</label>
                                        </div>
                                        
                                        <label class="col-xs-2 control-label"></label>
                                        <div class="col-xs-3"></div>

                                    </div>
                                    
                                </div>
                            </div>
                            
                            
                            

                            <div class="panel panel-info" style="margin-top:10px">
                                <div class="panel-heading" style="height:40px">
                                    <h1 class="panel-title" style="font-size:14px;line-height:40px">流量设置</h1>
                                </div>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">流量充值报告</label>
                                        <div class="col-xs-7">
                                            <input type="text" class="form-control validate[maxSize[255]]"
                                                   name="fluxUrl" id="fluxUrl" placeholder="请输入流量充值报告">
                                        </div>
                                    </div>

                                <@init_amount key='flux_money'></@init_amount>


                                <#if fluxConfigList?? && fluxConfigList?size gt 0>
                                    <#list fluxConfigList as lc>

                                        <#assign isEnd = true>

                                        <#if lc_index%2 == 0>
                                        <div class="form-group">
                                            <#assign isEnd = false>
                                        </#if>
                                        <label class="col-xs-2 control-label">${lc.attrName}</label>
                                        <div class="col-xs-3">
                                            <input type="text" class="form-control validate[required,maxSize[100]]"
                                                   id="${lc.attrKey}" value="${lc.attrValue}" name="${lc.attrKey}"
                                                   placeholder="请输入${lc.attrName}">
                                        </div>
                                        <#if isEnd>
                                        </div>
                                        </#if>
                                    </#list>
                                </#if>

                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">付费方式</label>
                                        <div class="col-xs-3">

                                            <label class="form-radio form-icon"><input type="radio" id="fsPayType1"
                                                                                       name="fsPayType" checked value="1">
                                                预付</label>
                                            <label class="form-radio form-icon"><input type="radio" id="fsPayType2"
                                                                                       name="fsPayType" value="2">
                                                后付</label>
                                        </div>

                                    </div>

                                </div>
                            </div>

                            <div class="panel panel-info" style="margin-top:10px">
                                <div class="panel-heading" style="height:40px">
                                    <h1 class="panel-title" style="font-size:14px;line-height:40px">语音设置</h1>
                                </div>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">语音发送报告</label>
                                        <div class="col-xs-7">
                                            <input type="text" class="form-control validate[maxSize[255]]"
                                                   name="voiceUrl" id="voiceUrl" placeholder="请输入语音发送报告">
                                        </div>
                                    </div>

                                <@init_amount key='voice_amount'></@init_amount>

                                    <div class="form-group">
                                        <label class="col-xs-2 control-label">付费方式</label>
                                        <div class="col-xs-3">

                                            <label class="form-radio form-icon"><input type="radio" id="vsPayType1"
                                                                                       name="vsPayType" checked value="1">
                                                预付</label>
                                            <label class="form-radio form-icon"><input type="radio" id="vsPayType2"
                                                                                       name="vsPayType" value="2">
                                                后付</label>
                                        </div>

                                    </div>
                                </div>
                            </div>

                            <div class="form-group" style="margin-top:15px">
                                <div class="col-xs-9 col-xs-offset-3">
                                    <label class="form-checkbox form-icon"><input type="checkbox" id="sendEmailFlag"
                                                                                  name="sendEmailFlag" value="1"
                                                                                  checked>发送邮件提醒</label>
                                    <button type="button" onclick="formSubmit();" class="btn btn-primary btn-lg"
                                            name="buttonSubmit">提&nbsp;&nbsp;交
                                    </button>
                                    &nbsp;&nbsp;&nbsp;&nbsp;
                                    <button type="button" onclick="history:back(-1);" class="btn btn-default btn-lg"
                                            name="buttonSubmit">返&nbsp;&nbsp;回
                                    </button>
                                </div>

                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    <#include "/WEB-INF/views/main/left.ftl">
    </div>
</div>
</body>
<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
<#include "/WEB-INF/views/common/form_validation.ftl">
<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
<script type="text/javascript">
    $(function () {

        $('input[name=sendUrlFlag]').click(function(){
           var $this = $(this);
            if($this.val() == 1){
                $('.sendUrlYDiv').show();
            }else{
                $('#sendUrlType1').click();
                $('.sendUrlYDiv').hide();
            }
        });
        
        $('input[name=sendUrlType]').click(function(){
            var $this = $(this);
            if($this.val() == 1){
                $('.sendTypeDiv').show();
            }else{
                $('.sendTypeDiv').hide();
            }
        });
        
        $('input[name=mmsSendUrlFlag]').click(function(){
           var $this = $(this);
            if($this.val() == 1){
                $('.mmsSendUrlYDiv').show();
            }else{
                $('#mmsSendUrlType2').click();
                $('.mmsSendUrlYDiv').hide();
            }
        });

        
        $('input[name=mmsSendUrlType]').click(function(){
            var $this = $(this);
            if($this.val() == 1){
                $('.mmsSendTypeDiv').show();
            }else{
                $('.mmsSendTypeDiv').hide();
            }
        });

        $('#myform').validationEngine('attach');
    });

    function formSubmit() {
        var allCheck = $('#myform').validationEngine('validate');
        if (!allCheck) {
            return;
        }

        $.ajax({
            url: '${BASE_PATH}/base/customer/create',
            dataType: 'json',
            data: $('#myform').serialize(),
            type: 'post',
            success: function (data) {
                if (data.result) {
                    Boss.alertToCallback('新增客户成功！',function(){
                        location.href = "${BASE_PATH}/base/customer";
                    });

                } else {
                    Boss.alert(data.message);
                }
            }, error: function (data) {
                Boss.alert('新增客户异常!');
            }
        });
    }
</script>
</html>