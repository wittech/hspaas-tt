<!DOCTYPE html>
<html lang="en">

	<head>
		<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
		<meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no">
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
						<div class="breadcrumb-wrapper"> <span class="label">所在位置:</span>
							<ol class="breadcrumb">
								<li> <a href="#"> 管理平台 </a> </li>
								<li> <a href="#"> 系统管理 </a> </li>
								<li class="active">客户信息管理</li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						<div class="panel">
                            <!-- Panel heading -->
                            <div class="panel-heading">
                                <h3 class="panel-title">
                                	<span>编辑客户</span>
                                </h3>
                            </div>
                            <!-- Panel body -->
                                  <div class="panel-body form-horizontal" style="background:#fcfcfc">
                                  
                                  
	                                  <div class="panel panel-info">
		                             	   <div class="panel-heading" style="height:40px">
                                               <div class="pull-right" style="margin-top: 3px;margin-right: 20px;">
                                                   <a onclick="userFormSubmit();" class="btn btn-primary">保存帐号信息</a>
                                               </div>
		                                	    <h1 class="panel-title" style="font-size:14px;line-height:40px">
		                                	    	帐号信息
		                                	    </h1>
		                            	   </div>  
		                            	   <div class="panel-body"> 
		                            	   		<form id="userForm">
		                            	   		<input type="hidden" name="user.id" value="${user.id}">
			                                    <div class="form-group">
			                                        <label class="col-xs-1 control-label">登录名</label>
			                                        <div class="col-xs-4">
			                                            <input type="text" class="form-control validate[required,maxSize[100]]" name="user.userName" value="${(user.userName)!''}" id="userName" placeholder="请输入登录名">
			                                        </div>
			                                        
			                                        <label class="col-xs-1 control-label">登录密码</label>
			                                        <div class="col-xs-4">
			                                            <input type="password" class="form-control validate[optional,maxSize[20],minSize[6]]" name="user.password" value="" id="userPassword" placeholder="修改用户登录密码">
			                                        </div>
			                                    </div>

												<div class="form-group">

                                                    <label class="col-xs-1 control-label">手机号</label>
                                                    <div class="col-xs-4">
                                                        <input type="text" class="form-control validate[required,maxSize[13],integer]" name="user.mobile" value="${(user.mobile)!''}" id="mobile" placeholder="请输入手机号">
                                                    </div>

													<label class="col-xs-1 control-label">用户昵称</label>
													<div class="col-xs-4">
														<input type="text" class="form-control validate[optional,maxSize[100]]" id="knick" name="user.knick" value="${(user.knick)!''}" placeholder="请输入用户昵称">
													</div>
												</div>
			                                    <div class="form-group">
			                                        <label class="col-xs-1 control-label">E-mail</label>
			                                        <div class="col-xs-4">
			                                             <input type="text" class="form-control validate[optional,maxSize[100],custom[email]]" id="email" value="${(user.email)!''}" name="user.email" placeholder="请输入E-mail">
			                                        </div>
			                                        <label class="col-xs-1 control-label">公司/个人</label>
			                                        <div class="col-xs-4">
			                                             <input type="text" class="form-control validate[required,maxSize[100]]" id="name" name="user.name" value="${(user.name)!''}" placeholder="请输入公司名称或个人姓名">
			                                        </div>
			                                    </div>
                                                <div class="form-group">
                                                        <label class="col-xs-1 control-label">App Key</label>
                                                        <div class="col-xs-4">
                                                            <input type="text" class="form-control" id="developerAppkey" value="${(developer.appKey)!''}" name="developer.appKey">
                                                            <input type="hidden" name="developer.id" value="${(developer.id)!'0'}">
                                                        </div>
                                                        <label class="col-xs-1 control-label">Secret</label>
                                                        <div class="col-xs-4">
                                                            <input type="text" class="form-control validate[optional,maxSize[50],minSize[6]]" id="developerSecret" value="${(developer.appSecret)!''}"  name="developer.appSecret" value="" placeholder="更新用户开发帐号appsecret">
                                                        	<input type="hidden" id="saltById" name="developer.salt"/>
                                                        	<a href="javascript:generatePassword();" class="btn btn-primary">生成密码</a>
                                                        </div>
                                                    </div>
			                                    </form>
	                                    	</div>
	                                    </div>
                                    
                                    
                                    	<div class="panel panel-info" style="margin-top:10px">
		                             	   <div class="panel-heading" style="height:40px">
                                               <div class="pull-right" style="margin-top: 3px;margin-right: 20px;">
                                                   <a onclick="userProfileFormSubmit();" class="btn btn-primary">保存基础信息</a>
                                               </div>
                                               <h1 class="panel-title" style="font-size:14px;line-height:40px">
		                                	    	基础信息
		                                	    </h1>
		                            	   </div>  
		                            	   <div class="panel-body"> 
		                            	   <form id="userProfileForm">	
		                            	   <input type="hidden" name="userProfile.userId" value="${user.id}">
			                                    <div class="form-group">
			                                        <label class="col-xs-1 control-label">姓名</label>
			                                        <div class="col-xs-4">
			                                            <input type="text" class="form-control validate[required,maxSize[100]]" name="userProfile.fullName" value="${(userProfile.fullName)!''}" id="fullName" placeholder="请输入姓名">
			                                        </div>
			                                        
			                                        <label class="col-xs-1 control-label">身份证号</label>
			                                        <div class="col-xs-4">
			                                            <input type="text" class="form-control validate[optional,maxSize[18]]" name="userProfile.cardNo" value="${(userProfile.cardNo)!''}" id="cardNo" placeholder="请输入身份证号">
			                                        </div>
			                                    </div>
			                                    <div class="form-group">
			                                        <label class="col-xs-1 control-label">地址</label>
			                                        <div class="col-xs-4">
			                                             <input type="text" class="form-control validate[optional,maxSize[100]]" id="address" name="userProfile.address" value="${(userProfile.address)!''}" placeholder="请输入地址">
			                                        </div>
			                                        <label class="col-xs-1 control-label">电话号码</label>
			                                        <div class="col-xs-4">
			                                             <input type="text" class="form-control validate[optional,maxSize[100]]" id="telephone" name="userProfile.telephone" value="${(userProfile.telephone)!''}" placeholder="请输入电话号码">
			                                        </div>
			                                    </div>
			                                    <div class="form-group">
			                                        <label class="col-xs-1 control-label">公司名称</label>
			                                        <div class="col-xs-4">
			                                             <input type="text" class="form-control validate[optional,maxSize[100]]" id="company" name="userProfile.company" value="${(userProfile.company)!''}" placeholder="请输入公司名称">
			                                        </div>
			                                        <label class="col-xs-1 control-label">所在城市</label>
			                                        <div class="col-xs-4">
			                                             <input type="text" class="form-control validate[optional,maxSize[100]]" id="city" name="userProfile.city" value="${(userProfile.city)!''}" placeholder="请输入所在城市">
			                                        </div>
			                                    </div>
			                                    
			                                    <div class="form-group">
			                                        <label class="col-xs-1 control-label">邮编</label>
			                                        <div class="col-xs-4">
			                                             <input type="text" class="form-control validate[optional,maxSize[8]]" id="zipcode" name="userProfile.zipcode" value="${(userProfile.zipcode)!''}" placeholder="请输入邮编">
			                                        </div>
			                                        <label class="col-xs-1 control-label">生日</label>
			                                        <div class="col-xs-4">
			                                             <input type="text" class="form-control validate[optional,maxSize[100]]" id="birthday" name="userProfile.birthday" value="${(userProfile.birthday)!''}" placeholder="请输入生日">
			                                        </div>
			                                    </div>
			                                    
			                                    <div class="form-group">
			                                        <label class="col-xs-1 control-label">性别</label>
			                                        <div class="col-xs-4">
			                                             <label class="form-radio form-icon"><input type="radio" id="nan" name="gender" <#if userProfile.gender?? && userProfile.gender == 'M'> checked</#if> value="M">男</label>&nbsp;&nbsp;
			                                             <label class="form-radio form-icon"><input type="radio" id="nv" name="gender" <#if userProfile.gender?? && userProfile.gender == 'W'> checked</#if> value="W">女</label>&nbsp;&nbsp;
			                                             <label class="form-radio form-icon"><input type="radio" id="weizhi" name="gender" <#if userProfile.gender?? && userProfile.gender == 'N'> checked</#if> value="N">未知</label>&nbsp;&nbsp;
			                                        </div>
			                                        <label class="col-xs-1 control-label">备注</label>
			                                        <div class="col-xs-4">
			                                             <textarea class="form-control" rows="4" name="userProfile.remark" placeholder="请输入备注">${(userProfile.remark)!''}</textarea>
			                                        </div>
			                                    </div>
			                                    </form>
	                                    	</div>
	                                    </div>

									  <#function getGroupId type>
                                            <#if passageList?? && passageList?size gt 0>
										  <#list passageList as dl>
											  <#if dl.type == type>
												  <#return dl.passageGroupId>
											  </#if>
										  </#list>
                                            </#if>
										  <#return 0>
									  </#function>
	                                    
	                                    <#macro init_amount key>
	                                    	<#if balanceMap?? && balanceMap?size gt 0>
	                                    		   <#assign obj = balanceMap[key] />
	                                    		   <#if obj??>
	                                    		   <div class="form-group">


                                                       <#assign groupList = "" />
                                                       <#assign showName = "" />
                                                       <#assign defaultGroupId = 0/>
                                                       <#assign groupKey = ""/>
                                                   <#-- sms_amount flux_money voice_amount-->
                                                       <#if key == 'balance_3'>
                                                           <#assign groupList = voicePassageGroupList?if_exists />
                                                           <#assign showName = "语音通道组" />
                                                           <#assign defaultGroupId = getGroupId(3)/>
                                                           <#assign groupKey = "vsGroupId"/>
                                                       <#elseif key='balance_1'>
                                                           <#assign groupList = smsPassageGroupList?if_exists />
                                                           <#assign showName = "短信通道组" />
                                                           <#assign defaultGroupId = getGroupId(1)/>
                                                           <#assign groupKey = "smsGroupId"/>
                                                       <#elseif key == 'balance_2'>
                                                           <#assign groupList = fsPassageGroupList?if_exists />
                                                           <#assign showName = "流量通道组" />
                                                           <#assign defaultGroupId = getGroupId(2)/>
                                                           <#assign groupKey = "fsGroupId"/>
                                                       <#elseif key='balance_4'>
                                                           <#assign groupList = mmsPassageGroupList?if_exists />
                                                           <#assign showName = "彩信通道组" />
                                                           <#assign defaultGroupId = getGroupId(1)/>
                                                           <#assign groupKey = "mmsGroupId"/>
                                                       </#if>


                                                       <label class="col-xs-2 control-label">${showName}</label>
                                                       <div class="col-xs-3">
                                                           <select class="form-control" name="${groupKey}">
                                                               <#if groupList?? && groupList?size gt 0 && groupList != ''>
                                                                   <#list groupList as gl>
                                                                       <option value="${gl.id}" <#if defaultGroupId == gl.id>selected</#if>>${gl.passageGroupName!''}</option>
                                                                   </#list>
                                                               <#else>
                                                                   <option value="-1">请选择${showName}</option>
                                                               </#if>
                                                           </select>
                                                       </div>

														<#-- 
			                                        	<label class="col-xs-2 control-label">
			                                        		<#if key=="balance_1">
			                                        			短信余额
			                                        		<#elseif key=="balance_2">
			                                        			流量余额
			                                        		<#elseif key =="balance_3">
			                                        			语音余额
			                                        		<#else>
			                                        			默认余额
			                                        		</#if>
			                                        	</label>
			                                        	-->
			                                        	<div class="col-xs-3">
			                                            	<input type="hidden" class="form-control validate[required,maxSize[100],number,min[0]]" value="${obj?if_exists}" name="${key}" id="${key}" placeholder="请输入...">
			                                        	</div>
			                                       </div>
			                                       </#if>
	                                    	</#if>
	                                    </#macro>
	                                    
	                                    
	                                    <div class="panel panel-info" style="margin-top:10px">
		                             	   <div class="panel-heading" style="height:40px">
                                               <div class="pull-right"  style="margin-top: 3px;margin-right: 20px;">
                                                   <a onclick="smsFormSubmit();" class="btn btn-primary">保存短信设置</a>
                                               </div>
		                                	    <h1 class="panel-title" style="font-size:14px;line-height:40px">
		                                	    	短信设置
		                                	    </h1>
		                            	   </div>  
		                            	   <div class="panel-body"> 
		                            	  	 <form id="smsForm">	
		                            	  	 <input type="hidden" name="userId" value="${user.id}">
                                             <@init_amount key='balance_1'></@init_amount>

                                                 <#assign status = 0 />
                                                 <#assign url = " "/>

                                                 <#list configList as cl>
                                                    <#if cl.type == 1>
                                                        <#assign status = cl.status/>
                                                        <#assign url = cl.url/>
                                                    </#if>
                                                 </#list>

                                                 <div class="form-group">
                                                     <label class="col-xs-2 control-label">短信发送状态报告URL</label>
                                                     <div class="col-xs-7">
                                                         <label class="form-radio form-icon"><input type="radio" id="sendUrlY"
                                                                                                    name="sendUrlFlag" <#if status != 0>checked</#if> value="1">启用</label>&nbsp;&nbsp;
                                                         <label class="form-radio form-icon"><input type="radio" id="sendUrlN"
                                                                                                    name="sendUrlFlag" <#if status == 0>checked</#if> value="0">不启用</label>
                                                     </div>
                                                 </div>

                                                 <div class="form-group sendUrlYDiv"  <#if status == 0>style="display:none"</#if>>
                                                     <label class="col-xs-2 control-label"></label>
                                                     <div class="col-xs-7">
                                                         <label class="form-radio form-icon"><input type="radio" id="sendUrlType0"
                                                                                                    name="sendUrlType" <#if status == 2> checked</#if> value="2">
                                                             不设置固定推送地址，在每个发送请求内传callback_url</label>
                                                     </div>
                                                 </div>

                                                 <div class="form-group sendUrlYDiv" <#if status == 0>style="display:none"</#if>>
                                                     <label class="col-xs-2 control-label"></label>
                                                     <div class="col-xs-7">
                                                         <label class="form-radio form-icon"><input type="radio" id="sendUrlType1"
                                                                                                    name="sendUrlType" <#if status == 0 || status == 1> checked</#if> value="1">
                                                             设置固定推送地址，无需每次发送请求都传callback_url</label>
                                                     </div>
                                                 </div>

                                                 <div class="form-group sendUrlYDiv sendTypeDiv" <#if status == 0 || status == 2>style="display:none"</#if>>
                                                     <label class="col-xs-2 control-label"></label>
                                                     <div class="col-xs-7">
                                                         <input type="text" class="form-control validate[maxSize[100]]"
                                                                name="smsSendUrl" id="smsSendUrl" value="${url!''}" placeholder="请输入短信发送状态报告URL">
                                                     </div>
                                                 </div>

			                                    
			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">短信上行状态报告URL</label>
			                                        <div class="col-xs-7">
			                                            <input type="text" class="form-control validate[maxSize[100]]" value="${(configMap['config_2'])!''}" name="smsUpUrl" id="smsUpUrl" placeholder="请输入短信上行状态报告URL">
			                                        </div>
			                                    </div>
			                                    
			                                    <div class="form-group">
			                                    	<input type="hidden" name="userSmsConfig.id" value="${userSmsConfig.id!''}">
			                                        	<label class="col-xs-2 control-label">短信返还规则</label>
			                                        	<div class="col-xs-3">
		                                                    <select class="form-control" name="userSmsConfig.smsReturnRule">
		                                                        <#if smsReturnRules??>
							                                        <#list smsReturnRules as a>
							                                            <option value="${a.value!''}" <#if userSmsConfig??><#if userSmsConfig.smsReturnRule==a.value>selected</#if></#if>>${a.title!''}</option>
							                                        </#list>
							                                    </#if>
		                                                    </select>
			                                        	</div>
		                                               <label class="col-xs-2 control-label">第一条计费字数</label>
		                                               <div class="col-xs-3">
		                                                   <input type="text" class="form-control validate[required,maxSize[100],custom[number],min[1]]" name="userSmsConfig.smsWords" id="smsWords" value="${userSmsConfig.smsWords!''}" placeholder="请输入计费字数">
		                                               </div>
			                                       </div>
			                                       <div class="form-group">
			                                        	<label class="col-xs-2 control-label">短信是否需要审核</label>
			                                        	<div class="col-xs-3">
		                                                    <select class="form-control" name="userSmsConfig.messagePass">
		                                                        <#if smsMessagePass??>
							                                        <#list smsMessagePass as a>
							                                            <option value="${a.value!''}" <#if userSmsConfig??><#if userSmsConfig.messagePass==true && a.value==1>selected</#if></#if>>${a.title!''}</option>
							                                        </#list>
							                                    </#if>
		                                                    </select>
			                                        	</div>
		                                               <label class="col-xs-2 control-label">短信超时时间（毫秒）</label>
		                                               <div class="col-xs-3">
		                                                   <input type="text" class="form-control validate[required,maxSize[100],custom[number],min[0]]" name="userSmsConfig.smsTimeout" id="smsTimeout" value="${userSmsConfig.smsTimeout!''}" placeholder="短信超时时间（毫秒）">
		                                               </div>
			                                       </div>
			                                        <div class="form-group">
			                                        	<label class="col-xs-2 control-label">模板是否需要报备</label>
			                                        	<div class="col-xs-3">
		                                                    <select class="form-control" name="userSmsConfig.needTemplate">
		                                                        <#if smsNeedTemplates??>
							                                        <#list smsNeedTemplates as a>
							                                            <option value="${a.value!''}" <#if userSmsConfig??><#if userSmsConfig.needTemplate==true && a.value==1>selected</#if></#if>>${a.title!''}</option>
							                                        </#list>
							                                    </#if>
		                                                    </select>
			                                        	</div>
		                                               <label class="col-xs-2 control-label">自动提取短信模板</label>
		                                               <div class="col-xs-3">
		                                                    <select class="form-control" name="userSmsConfig.autoTemplate">
		                                                        <#if smsPickupTemplates??>
							                                        <#list smsPickupTemplates as a>
							                                            <option value="${a.value!''}" <#if userSmsConfig??><#if userSmsConfig.autoTemplate ==true && a.value==1>selected</#if></#if>>${a.title!''}</option>
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
				                                                   name="userSmsConfig.submitInterval" id="submitInterval" value="${userSmsConfig.submitInterval!''}"
				                                                   placeholder="提交时间间隔（秒）">
				                                        </div>
				                                        <label class="col-xs-2 control-label">提交次数上限</label>
				                                        <div class="col-xs-3">
				                                            <input type="text"
				                                                   class="form-control validate[maxSize[10],custom[number],min[0]]"
				                                                   name="userSmsConfig.limitTimes" id="limitTimes" value="${userSmsConfig.limitTimes!''}"
				                                                   placeholder="提交次数上限（次）">
				                                        </div>
				                                    </div>
			                                       
			                                        <div class="form-group">
			                                        	<label class="col-xs-2 control-label">签名途径</label>
			                                        	<div class="col-xs-3">
		                                                    <select class="form-control" name="userSmsConfig.signatureSource">
		                                                       	<#if smsSignatureSources??>
							                                        <#list smsSignatureSources as a>
							                                            <option value="${a.value!''}" <#if userSmsConfig??><#if userSmsConfig.signatureSource==a.value>selected</#if></#if>>${a.title!''}</option>
							                                        </#list>
							                                    </#if>
		                                                    </select>
			                                        	</div>
		                                               <label class="col-xs-2 control-label">付费方式</label>
		                                               <div class="col-xs-3">

                                                           <label class="form-radio form-icon"><input type="radio" id="smsPayType1"
                                                                                                      name="smsPayType" <#if payTypeMap['payType_1'] == 1> checked</#if> value="1">
                                                               预付</label>
                                                           <label class="form-radio form-icon"><input type="radio" id="smsPayType2"
                                                                                                      name="smsPayType" <#if payTypeMap['payType_1'] == 2> checked</#if> value="2">
                                                               后付</label>
					                                   </div>
			                                       </div>

													 <div class="form-group">
													 	<label class="col-xs-2 control-label">扩展号码</label>
				                                        <div class="col-xs-3">
				                                            <input type="text"
				                                                   class="form-control validate[maxSize[10],custom[number]]"
				                                                   name="userSmsConfig.extNumber" id="smsTimeout" placeholder="扩展号码（数字）"  value="${userSmsConfig.extNumber!''}" />
				                                        </div>
														 <label class="col-xs-2 control-label">签名内容</label>
														 <div class="col-xs-3">
                                                             <textarea class="form-control" rows="4" name="userSmsConfig.signatureContent" placeholder="请输入备注">${userSmsConfig.signatureContent!''}</textarea>
														 </div>
														 <#--
														 <label class="col-xs-2 control-label">签名内容</label>
														 <div class="col-xs-3">
															 <textarea class="form-control" rows="4" name="userSmsConfig.signatureContent" placeholder="请输入备注">${userSmsConfig.signatureContent!''}</textarea>
														 </div>
														 -->
													 </div>
			                                  	</form>
	                                    	</div>
	                                    </div>
	                                    
	                                    
	                                    <div class="panel panel-info" style="margin-top:10px">
		                             	   <div class="panel-heading" style="height:40px">
                                               <div class="pull-right"  style="margin-top: 3px;margin-right: 20px;">
                                                   <a onclick="mmsFormSubmit();" class="btn btn-primary">保存彩信设置</a>
                                               </div>
		                                	    <h1 class="panel-title" style="font-size:14px;line-height:40px">
		                                	    	彩信设置
		                                	    </h1>
		                            	   </div>
			                                
			                                <form id="mmsForm">
			                                <input type="hidden" name="userId" value="${user.id}">
			                                <div class="panel-body">
			                                	<@init_amount key='balance_4'></@init_amount>

                                                 <#assign status = 0 />
                                                 <#assign url = " "/>

                                                 <#list configList as cl>
                                                    <#if cl.type == 5>
                                                        <#assign status = cl.status/>
                                                        <#assign url = cl.url/>
                                                    </#if>
                                                 </#list>
			
			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">彩信发送状态报告URL</label>
			                                        <div class="col-xs-7">
			                                        	<input type="hidden" name="userMmsConfig.id" value="${userMmsConfig.id!''}">
			                                            <label class="form-radio form-icon"><input type="radio" id="mmsSendUrlY"
			                                                                                       name="mmsSendUrlFlag" <#if status != 0>checked</#if> value="1">启用</label>&nbsp;&nbsp;
			                                            <label class="form-radio form-icon"><input type="radio" id="mmsSendUrlN"
			                                                                                       name="mmsSendUrlFlag" <#if status == 0>checked</#if> value="0">不启用</label>
			                                        </div>
			                                    </div>
			                                    <div class="form-group mmsSendUrlYDiv" <#if status == 0>style="display:none"</#if>>
			                                        <label class="col-xs-2 control-label"></label>
			                                        <div class="col-xs-7">
			                                            <label class="form-radio form-icon"><input type="radio" id="mmsSendUrlType0"
			                                                                                       name="mmsSendUrlType" <#if status == 2> checked</#if> value="2">
			                                                不设置固定推送地址，在每个发送请求内传callback_url</label>
			                                        </div>
			                                    </div>
			                                    
			                                    <div class="form-group mmsSendUrlYDiv" <#if status == 0>style="display:none"</#if>>
			                                        <label class="col-xs-2 control-label"></label>
			                                        <div class="col-xs-7">
			                                            <label class="form-radio form-icon"><input type="radio" id="mmsSendUrlType1"
			                                                                                       name="mmsSendUrlType" <#if status == 0 || status == 1> checked</#if> value="1">
			                                                设置固定推送地址，无需每次发送请求都传callback_url</label>
			                                        </div>
			                                    </div>
			
			                                    <div class="form-group mmsSendUrlYDiv mmsSendTypeDiv" <#if status == 0 || status == 2>style="display:none"</#if>>
			                                        <label class="col-xs-2 control-label"></label>
			                                        <div class="col-xs-7">
			                                            <input type="text" class="form-control validate[maxSize[100]]"
			                                                   name="mmsSendUrl" id="mmsSendUrl" value="${url!''}" placeholder="请输入彩信发送状态报告URL">
			                                        </div>
			                                    </div>
			
			
			
			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">彩信上行状态报告URL</label>
			                                        <div class="col-xs-7">
			                                            <input type="text" class="form-control validate[maxSize[100]]"
			                                                   name="mmsUpUrl" id="mmsUpUrl" placeholder="请输入彩信上行状态报告URL" value="${(configMap['config_6'])!''}">
			                                        </div>
			                                    </div>
			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">彩信返还规则</label>
			                                        <div class="col-xs-3">
			                                            <select class="form-control" name="userMmsConfig.mmsReturnRule">
			                                            <#if smsReturnRules??>
			                                                <#list smsReturnRules as a>
			                                                    <option value="${a.value!''}" <#if userMmsConfig??><#if userMmsConfig.mmsReturnRule==a.value>selected</#if></#if>>${a.title!''}</option>
			                                                </#list>
			                                            </#if>
			                                            </select>
			                                        </div>
			                                        <label class="col-xs-2 control-label">扩展号码</label>
			                                        <div class="col-xs-3">
			                                            <input type="text"
			                                                   class="form-control validate[maxSize[10],custom[number]]"
			                                                   name="userMmsConfig.extNumber" id="extNumber" placeholder="接入号（数字）"  value="${(userMmsConfig.extNumber)!}">
			                                        </div>
			                                    </div>
			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">彩信是否需要审核</label>
			                                        <div class="col-xs-3">
			                                            <select class="form-control" name="userMmsConfig.messagePass">
			                                            <#if smsMessagePass??>
			                                                <#list smsMessagePass as a>
			                                                    <option value="${a.value!''}" <#if userMmsConfig??><#if userMmsConfig.messagePass==true && a.value==1>selected</#if></#if>>${a.title!''}</option>
			                                                </#list>
			                                            </#if>
			                                            </select>
			                                        </div>
			                                        <label class="col-xs-2 control-label">彩信超时时间（毫秒）</label>
			                                        <div class="col-xs-3">
			                                            <input type="text"
			                                                   class="form-control validate[required,maxSize[100],custom[number],min[0]]"
			                                                   name="userMmsConfig.mmsTimeout" id="mmsTimeout" value="5000"
			                                                   placeholder="彩信超时时间（毫秒）" value="${(userMmsConfig.mmsTimeout)!}">
			                                        </div>
			                                    </div>
			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">提交时间间隔</label>
			                                        <div class="col-xs-3">
			                                        	<input type="text"
			                                                   class="form-control validate[maxSize[10],custom[number],min[0]]"
			                                                   name="userMmsConfig.submitInterval" id="submitInterval" value="30"
			                                                   placeholder="提交时间间隔（秒）" value="${(userMmsConfig.submitInterval)!}">
			                                        </div>
			                                        <label class="col-xs-2 control-label">提交次数上限</label>
			                                        <div class="col-xs-3">
			                                            <input type="text"
			                                                   class="form-control validate[maxSize[10],custom[number],min[0]]"
			                                                   name="userMmsConfig.limitTimes" id="limitTimes" value="10"
			                                                   placeholder="提交次数上限（次）" value="${(userMmsConfig.limitTimes)!}">
			                                        </div>
			                                    </div>
			                                    
			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">付费方式</label>
			                                        <div class="col-xs-3">
		                                                <label class="form-radio form-icon"><input type="radio" id="mmsPayType"
                                                                                                  name="mmsPayType" <#if payTypeMap['payType_4'] == 1> checked</#if> value="1">
                                                           	预付</label>
                                                       <label class="form-radio form-icon"><input type="radio" id="mmsPayType2"
                                                                                                      name="mmsPayType" <#if payTypeMap['payType_4'] == 2> checked</#if> value="2">
			                                                                                                                              后付</label>
			                                                
			                                        </div>
			                                        
			                                        <label class="col-xs-2 control-label"></label>
			                                        <div class="col-xs-3"></div>
			                                    </div>
			                                </div>
			                                </form>
			                            </div>
	                                    
	                                    <div class="panel panel-info" style="margin-top:10px;display:none;">
		                             	   <div class="panel-heading" style="height:40px">
                                               <div class="pull-right"  style="margin-top: 3px;margin-right: 20px;">
                                                   <a onclick="fsFormSubmit();" class="btn btn-primary">保存流量设置</a>
                                               </div>
		                                	    <h1 class="panel-title" style="font-size:14px;line-height:40px">
		                                	    	流量设置
		                                	    </h1>
		                            	   </div>  
		                            	   <div class="panel-body"> 	
		                            	   <form id="fsForm">
		                            	   <input type="hidden" name="userId" value="${user.id}">
                                                 <@init_amount key='balance_2'></@init_amount>

			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">流量充值报告</label>
			                                        <div class="col-xs-7">
			                                            <input type="text" class="form-control validate[maxSize[255]]" value="${(configMap['config_3'])!''}" name="fluxUrl" id="fluxUrl" placeholder="请输入流量充值报告">
			                                        </div>
			                                    </div>
			                                    

		                                    	<#list discountProperty as dp>
		                                    	
		                                    		<#assign isEnd = true>
		                                    	
		                                    		<#if dp_index%2 == 0>
				                                    <div class="form-group">
				                                    <#assign isEnd = false>
				                                    </#if>
				                                        <label class="col-xs-2 control-label">${discountName[dp_index]}</label>
				                                        <div class="col-xs-3">
				                                             <input type="text" class="form-control validate[maxSize[100]]" id="${dp}" name="userFluxDiscount.${dp}" value="${(fluxMap[dp])!''}" placeholder="请输入${discountName[dp_index]}">
				                                        </div>
				                                    <#if isEnd>
				                                    </div>
				                                    </#if>
		                                    	</#list>
                                               <div class="form-group">
                                                   <label class="col-xs-2 control-label">付费方式</label>
                                                   <div class="col-xs-3">

                                                       <label class="form-radio form-icon"><input type="radio" id="fsPayType1"
                                                                                                  name="fsPayType" <#if payTypeMap['payType_2'] == 1> checked</#if> value="1">
                                                           预付</label>
                                                       <label class="form-radio form-icon"><input type="radio" id="fsPayType2"
                                                                                                  name="fsPayType" <#if payTypeMap['payType_2'] == 2> checked</#if> value="2">
                                                           后付</label>
                                                   </div>

                                               </div>
		                                    	</form>
	                                    	</div>
	                                    </div>
	                                    
	                                    <div class="panel panel-info" style="margin-top:10px;display:none;">
		                             	   <div class="panel-heading" style="height:40px">
                                               <div class="pull-right" style="margin-top: 3px;margin-right: 20px;">
                                                   <a onclick="vsFormSubmit();" class="btn btn-primary">保存语音设置</a>
                                               </div>
		                                	    <h1 class="panel-title" style="font-size:14px;line-height:40px">
		                                	    	语音设置
		                                	    </h1>
		                            	   </div>  
		                            	   <div class="panel-body"> 
		                            	   	<form id="vsForm">	
		                            	   	<input type="hidden" name="userId" value="${user.id}">
                                            <@init_amount key='balance_3'></@init_amount>
			                                    <div class="form-group">
			                                        <label class="col-xs-2 control-label">语音发送报告</label>
			                                        <div class="col-xs-7">
			                                            <input type="text" class="form-control validate[required,maxSize[255]]" value="${(configMap['config_4'])!''}" name="voiceUrl" id="voiceUrl" placeholder="请输入语音发送报告">
			                                        </div>
			                                    </div>

                                                <div class="form-group">
                                                    <label class="col-xs-2 control-label">付费方式</label>
                                                    <div class="col-xs-3">

                                                        <label class="form-radio form-icon"><input type="radio" id="vsPayType1"
                                                                                                   name="vsPayType" <#if payTypeMap['payType_3'] == 1> checked</#if> value="1">
                                                            预付</label>
                                                        <label class="form-radio form-icon"><input type="radio" id="vsPayType2"
                                                                                                   name="vsPayType" <#if payTypeMap['payType_3'] == 2> checked</#if> value="2">
                                                            后付</label>
                                                    </div>

                                                </div>
			                                    </form>
	                                    	</div>
	                                    </div>
                                </div>
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
		$(function(){
			$('#userForm').validationEngine('attach');
			$('#userProfileForm').validationEngine('attach');
			$('#smsForm').validationEngine('attach');
			$('#vsForm').validationEngine('attach');
			$('#fsForm').validationEngine('attach');


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
	                $('#mmsSendUrlType1').click();
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
		});
		
		function userFormSubmit(){
			var allCheck = $('#userForm').validationEngine('validate');
			if(!allCheck){
				return;
			}

			/**
			var password = $('#userPassword').val();
            var is_update = true;
            if(password != '' && !confirm('确定要修改用户的登录密码吗？')){
                is_update = false;
            }

            if(!is_update){
                return;
            }
			
			
            var secret = $('#developerSecret').val();
            if(secret != '' && !confirm('确定要修改用户的Secret吗？')){
                is_update = false;
            }

            if(!is_update){
                return;
            }
            */
			
			$.ajax({
	  			url:'${BASE_PATH}/base/customer/updateUser',
	  			dataType:'json',
	  			data:$('#userForm').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('修改客户成功！',function(){
                            location.reload();
                        });

	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('客户异常!');
	  			}
	  		});
		}
		
		
		function userProfileFormSubmit(){
			var allCheck = $('#userProfileForm').validationEngine('validate');
			if(!allCheck){
				return;
			}
			
			$.ajax({
	  			url:'${BASE_PATH}/base/customer/updateBasic',
	  			dataType:'json',
	  			data:$('#userProfileForm').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('修改客户成功！',function(){
                            location.reload();
                        });
	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('修改客户异常!');
	  			}
	  		});
		}
		
		function smsFormSubmit(){
			var allCheck = $('#smsForm').validationEngine('validate');
			if(!allCheck){
				return;
			}
			
			$.ajax({
	  			url:'${BASE_PATH}/base/customer/updateSms',
	  			dataType:'json',
	  			data:$('#smsForm').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('修改短信设置成功！',function(){
                            location.reload();
                        });

	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('修改短信设置异常!');
	  			}
	  		});
		}
		
		function mmsFormSubmit(){
			var allCheck = $('#mmsForm').validationEngine('validate');
			if(!allCheck){
				return;
			}
			
			$.ajax({
	  			url:'${BASE_PATH}/base/customer/updateMms',
	  			dataType:'json',
	  			data:$('#mmsForm').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('修改彩信设置成功！',function(){
                            location.reload();
                        });

	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('修改彩信设置异常!');
	  			}
	  		});
		}
		
		function fsFormSubmit(){
			var allCheck = $('#fsForm').validationEngine('validate');
			if(!allCheck){
				return;
			}
			
			$.ajax({
	  			url:'${BASE_PATH}/base/customer/updateFs',
	  			dataType:'json',
	  			data:$('#fsForm').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('修改流量设置成功！',function(){
                            location.reload();
                        });
	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('修改流量设置异常!');
	  			}
	  		});
		}
		
		function vsFormSubmit(){
			var allCheck = $('#vsForm').validationEngine('validate');
			if(!allCheck){
				return;
			}
			
			$.ajax({
	  			url:'${BASE_PATH}/base/customer/updateVs',
	  			dataType:'json',
	  			data:$('#vsForm').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                        Boss.alertToCallback('修改语音设置成功！',function(){
                            location.reload();
                        });
	  				}else{
                        Boss.alert(data.message);
	  				}
	  			},error:function(data){
                    Boss.alert('修改语音设置异常!');
	  			}
	  		});
		}
		
		//密码生成
		function generatePassword(){
			$.ajax({
	  			url:'${BASE_PATH}/base/customer/generatePassword',
	  			dataType:'json',
	  			data:$('#vsForm').serialize(),
	  			type:'post',
	  			success:function(data){
	  				if(data.result){
                       $("#developerSecret").val(data.obj.appSecret);
                       $("#saltById").val(data.obj.salt);
                       Boss.alert("密码生成成功！");
	  				}else{
                        Boss.alert("密码生成失败！请稍后重试！");
	  				}
	  			},error:function(data){
                    Boss.alert('密码生成异常!');
	  			}
	  		});
		}
		
	</script>
</html>