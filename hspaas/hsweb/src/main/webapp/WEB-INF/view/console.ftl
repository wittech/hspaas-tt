<!DOCTYPE html>
<html lang="zh-cn">
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>华时融合平台管理中心</title>
<link href="${rc.contextPath}/assets/css/css.css" rel="stylesheet" type="text/css" />
<link href="${rc.contextPath}/assets/css/form.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${rc.contextPath}/assets/js/jquery-1.12.0.min.js"></script>
<script type="text/javascript" src="${rc.contextPath}/assets/js/common.js"></script>
<#include "/common/validator.ftl"/>
<#include "/common/dialog.ftl"/>
<#include "/common/chart.ftl"/>
</head>
<body>
<#include "/common/navigation.ftl"/>
<div class="content">
		    
   <#include "/common/sidebar.ftl"/>
    
    <!--右侧main-->
		<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="#">开发者控制台</a></li>
					<li class="active"><a href="#">控制台</a></li>
				</ul>
			</div>
			<div class="note_box">
				<p>
		       	 	公告：华时融合平台正式上线啦！
		         <a href="">查看详情</a>
		        </p>
			</div>

			<div class="item_box">
				<div class="item_left">
					<div class="box box1">
						<div class="box11">
							<h1>账号信息</h1>
							<div class="list">
							  <p>
								<span class="bold">登录帐号：</span><span id="span_token">${(Session["LOGIN_USER_SESSION_KEY"].email)!}/${(Session["LOGIN_USER_SESSION_KEY"].mobile)!}</span>
							  </p>
                              <p>
								<span class="bold">接口账号：</span>${(developer.appKey)!}
							  </p>
							  <p>
								<span class="bold">接口密码：</span><span id="span_token">*******</span>
                                <input type="button" value="查看密码" id="validcode" class="bg-blue s">
							  </p>
							  <#--
                             	 <p>
	                              	<input type="button" value="状态推送地址" onclick="back_jump(1);" id="push_address" class="bg-blue s">&nbsp;
	                              	<input type="button" value="手机黑名单" onclick="back_jump(2);" id="mobile_black" class="bg-green s">&nbsp;
	                              	<input type="button" value="手机白名单" onclick="back_jump(3);" id="mobile_white" class="bg-gray s">
                                </p>
                                -->
                                <#if accountBalance?? && accountBalance lt 10>
                                <span class="reminder">您的余额不足10元，请充值！</span>
                                </#if>
							</div>
						</div>
						<div class="box12">
							<p>
                                <b>账户余额：</b>
                                <span>￥${(accountBalance)!}元</span>
                                <#--<input type="button" value="充值" onclick="pay();" class="bg-blue s" />-->
                            </p>
                            <p>
                                <b>短信余额：</b>
                                <span>${(messageBalance)!}条</span>
                                <span class="boxOK">
	                                <#if balanceRemind?? && balanceRemind?size gt 0>
	                                	<#list balanceRemind as b>
		                                	<#if b.type=="1">
			                                    <#if b.status=="1">
			                                    	已开启短信提醒
			                                    </#if>
			                                 </#if>
			                             </#list>
	                                <#else>
	                                	<input type="button" value="设置" onclick="back_jump(6);" class="bg-blue s">&nbsp;
	                                </#if>
                                </span>
                            </p>
                            <p>
                                <b>流量余额：</b>
                                <span>￥${(fluxBalance)!}元</span>
                                <span class="boxOK">
	                               <#if balanceRemind?? && balanceRemind?size gt 0>
	                                	<#list balanceRemind as b>
		                                	<#if b.type=="2">
			                                    <#if b.status=="1">
			                                    	已开启流量提醒
			                                    </#if>
			                                 </#if>
			                             </#list>
	                                <#else>
	                                	<input type="button" value="设置" onclick="back_jump(6);" class="bg-orangered s">&nbsp;
	                                </#if>
                                </span>
                            </p>
                            <p>
                                <b>语音余额：</b>
                                <span>${(voiceBalance)!}条</span>
                                 <span class="boxOK">
	                                 <#if balanceRemind?? && balanceRemind?size gt 0>
	                                	<#list balanceRemind as b>
		                                	<#if b.type=="3">
			                                    <#if b.status=="1">
			                                    	已开启语音提醒
			                                    </#if>
			                                 </#if>
			                             </#list>
	                                <#else>
	                                	<input type="button" value="设置" onclick="back_jump(6);" class="bg-vs s">&nbsp;
	                                </#if>
                                </span>
                            </p>
							<p style="padding:10px 0px 0px 13px">
								<input type="button" value="账户充值" onclick="back_jump(5,1);" id="balance_reminder" class="bg-blue s">
                              	<input type="button" value="余额提醒" onclick="back_jump(5,1);" id="balance_reminder" class="bg-gray s">
							</p>
						</div>
					</div>
                    <div class="box box2">
						<h1>应用发布步骤</h1>
						<div class="step">
							<ul>
								<li class="step1">&nbsp;</li>
								<li class="step_space" style="margin-left: 18.25px; margin-right: 18.25px;">&nbsp;</li>
								<li class="step2">&nbsp;</li>
								<li class="step_space" style="margin-left: 18.25px; margin-right: 18.25px;">&nbsp;</li>
								<li class="step3">&nbsp;</li>
								<li class="step_space" style="margin-left: 18.25px; margin-right: 18.25px;">&nbsp;</li>
								<li class="step4">&nbsp;</li>
								<li class="step_space" style="margin-left: 18.25px; margin-right: 18.25px;">&nbsp;</li>
								<li class="step5">&nbsp;</li>
							</ul>
						</div>
					</div>
					
				</div>
				<div class="item_right">
					
					<div class="box">
                    	<h1>公告信息</h1>
						<div class="conts">
                        <ul>
                        	<#if notifications??>
                        	<#list notifications as n>
                        	<li><a href="javascript:void(0)">${(n.title)!}</a></li>
                        	</#list>
                        	</#if>
                        </ul>
                        </div>
					</div>
                    
                    <!--客服系统-->
                    <div class="chart_box">
  
                    	<div class="tit">
                    		<h1 style="padding-left:0; width:80px">客服系统</h1>
                            <ul class="kfli"> 
                              <li class="active" id="sms" onclick="getTodayConsumeInfo('sms','1')">短信</li>
                              <li id="flows" onclick="getTodayConsumeInfo('flows','2')">流量</li>
                              <li id="voice" onclick="getTodayConsumeInfo('voice','3')">语音</li>
                            </ul>
                        </div>
                       <form id="list_form">
                       		<input type="hidden" id="platformType" name="platformType" value="1"/>
	                        <div class="ctn">
	                        	<select id="quickType" name="quickType">
	                            	<option value="1">手机号查询</option>
	                                <option value="2">状态返回值查询</option>
	                            </select>
	                            <input type="text" id="content" class="input" name="content"/>
	                            <input type="button" class="bg-blue" id="button_serch" onclick="quickSearch();" value="搜索" />
	                        </div>
	                        <div class="conts" id="div_ul" style="padding-top:7px; height:104px; overflow:auto">
		                        <ul>
		                        </ul>
	                        </div>
                        </form>
					</div>
                    <!--//客服系统-->
                    
				</div>
			</div>
			
            <!--统计部分-->
            <div class="chart_box">
		          <div class="tit">
		          <h1>消费数据</h1>
		            <ul class="xf_ul"> 
		              <li class="active" id="l1" onclick="consumReport('1');">短信</li>
		              <li id="l2" onclick="consumReport('2');">流量</li>
                      <li id="l3" onclick="consumReport('3');">语音</li>
		            </ul>
		          </div>
		          <div class="ctn">
		            <div style="height:300px" id="container">
                    	
                    </div>
		          </div>
			</div>
            
            <!--//统计部分-->
			
		</div>
	<!--右侧main-->
</div>	 
   
 <!--弹层-->
  <div class="background_box" style="display: none;">&nbsp;</div>
  <div class="float_box addnum_box" style="display: none;">
    <div class="float_tit">
      <h1>余额提醒</h1>
    </div>
    <div class="float_ctn">
     <form  action="/app/testNumAdd" method="post" name="phoneForm" id="phone_form">
      <div class="float_field">
        <dl>
          <dt><i class="star"></i>短信</dt>
          <dd><input type="text" id="phone"  name="testWhiteListObj.mobile"/><span id="phone_error" class="error" style="display:none">格式错误</span></dd>
          <dd><span class="tips">输入格式例如：2000 （表示低于2000条自动提醒）</span></dd> 
        </dl>
      </div>
      <div class="float_field">
        <dl>
          <dt><i class="star"></i>流量</dt>
          <dd><input type="text" id="phone"  name="testWhiteListObj.mobile"/><span id="phone_error" class="error" style="display:none">格式错误</span></dd>
          <dd><span class="tips">输入格式例如：2000 （表示低于2000条自动提醒）</span></dd> 
        </dl>
      </div>
      <div class="float_field">
        <dl>
          <dt><i class="star"></i>语音</dt>
          <dd><input type="text" id="phone"  name="testWhiteListObj.mobile"/><span id="phone_error" class="error" style="display:none">格式错误</span></dd>
          <dd><span class="tips">输入格式例如：2000 （表示低于2000条自动提醒）</span></dd> 
        </dl>
      </div>
      <div class="float_btn">
        <input type="button" value="取消" class="cancel_btn" />
        <input type="submit" value="确定" class="confirm_btn" />
      </div>
      </form>
    </div>
  </div>
  <!--//弹层-->   
</body>
<script>
	//1、推送状态 2、手机黑名单 3、手机白名单 4、资费套餐 5、余额提醒 6、余额提醒
	function back_jump(type,parm){
		switch(type){
			case 1:
  				location.href ="${rc.contextPath}/settings/push/index";
 			 	break;
			case 2:
  				location.href ="${rc.contextPath}/settings/mobile/black/index";
 			 	break;
 			case 3:
  				location.href ="${rc.contextPath}/settings/mobile/white/index";
 			 	break;
 			case 4:
  				location.href ="${rc.contextPath}/product/index";
 			 	break;
 			case 5:
  				location.href ="${rc.contextPath}/settings/balance_remind/index?type="+parm;
 			 	break;
		}
	}
	
	function pay(){
		var html = "上限阈值：<input type='text' id='notice_num' placeholder ='请设置上限阀值' /><br/>手机号码：<input type='text' id='notice_mobile' placeholder ='请设置通知手机号码' /><br/>";
	
		window.wxc.xcConfirm(html, window.wxc.xcConfirm.typeEnum.custom, {
			title : "设置短信余额提醒",
			onOk:function(v){
				window.wxc.xcConfirm("提交成功!", window.wxc.xcConfirm.typeEnum.success);
				//window.wxc.xcConfirm("提交失败!", window.wxc.xcConfirm.typeEnum.error);
			}
		});
	};
	
	function quickSearch(){
		if($("#quickType").val()==null || $("#quickType").val()==undefined || $("#quickType").val()==""){
			Dialog.alert("输入框不能为空！");
			return false;
		}
		Hs.ajax({
    		url : "/quick_search",
			data:$('#list_form').serialize(),
    		success : function(data){
				if(data == null){
					$("#div_ul").html("<ul><span class=\"text-red\">未检索到数据!</span></ul>");
					return;
				}
				
				var msg = "";
				if($("#quickType").val()=="1"){
					if($("#platformType").val()=="1"){
						msg += "<li>"+data.mobile+":"+data.content+"</li><br/>";
					}else if($("#platformType").val()=="2"){
						msg += "<li>"+data.tradeNo+":"+data.prodcutName+"</li><br/>";
					}else if($("#platformType").val()=="3"){
						msg += "<li>"+data.mobile+":"+data.sendStatus+"</li><br/>";
					}
				}else if($("#quickType").val()=="2"){
					msg += "<li><span style='font-weight: bold'>"+data.name+"</span><span>"+data.description+"</span></li><br/>";
				}
				$("#div_ul").html("<ul>" + msg + "</ul>");
    		}, 
    		error : function(t, s) {
    			$("#div_ul").html("<ul><span class=\"text-red\">未检索到数据!</span></ul>");
				return;
    		}
    	});
	};
	
	function consumReport(type) {
		$(".xf_ul li").removeClass("active");
		$("#l"+type).attr("class","active");
		Hs.ajax({
    		url : "/report/lastest/time_chart",
			data: {type : type},
    		success : function(data){
    			if(!HsChart.validate(data.series))
					return;
				$('#container').highcharts({
			        title: {
			            text: data.title,
			            x: -20
			        },
			        xAxis: {
			            categories: data.xlable
			        },
			        yAxis: {
			            title: {
			                text: data.ylable
			            },
			            plotLines: [{
			                value: 0,
			                width: 1,
			                color: '#808080'
			            }]
			        },
			        tooltip: {
			            valueSuffix: data.unit
			        },
			        legend: {
			            layout: 'vertical',
			            align: 'right',
			            verticalAlign: 'middle',
			            borderWidth: 0
			        },
			        series: data.series
			    });
    		}
    	});
	};
	
	consumReport(1);
	
</script>
</html>