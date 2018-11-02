<!DOCTYPE html>
<html lang="en">

	<head>
		<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
		<meta charset="utf-8">
		<title>融合平台</title>
		<link href="${BASE_PATH}/resources/css/bootstrap/bootstrap.min.css" rel="stylesheet">
		<link href="${BASE_PATH}/resources/css/bootstrap/style.css" rel="stylesheet">
		<link href="${BASE_PATH}/resources/css/bootstrap/font-awesome.min.css" rel="stylesheet">
		<link href="${BASE_PATH}/resources/css/bootstrap/pace.min.css" rel="stylesheet">
		<script src="${BASE_PATH}/resources/js/bootstrap/pace.min.js"></script>
	</head>

	<body>
		<div id="container" class="effect mainnav-lg navbar-fixed mainnav-fixed">
			<#include "/WEB-INF/views/main/top.ftl">
			<div class="boxed">

				<div id="content-container">

					<#--
					<div class="pageheader">
						<div class="breadcrumb-wrapper"> <span class="label">所在位置:</span>
							<ol class="breadcrumb">
								<li> <a href="#"> 管理平台 </a> </li>
								<li class="active"> 欢迎页 </li>
							</ol>
						</div>
					</div>
					-->
					<div id="page-content">
						
						<#--
						<h2>你好，${session.realName?if_exists}</h2>
						<h3>欢迎你使用华时融合平台...<h3>
						 -->
						
						<div id="main" style="height:600px"></div>
                    </div>
				</div>
				<#include "/WEB-INF/views/main/left.ftl">
			</div>

		</div>
		
		<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
		<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> 
        <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script>
        <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
		<script src="${BASE_PATH}/resources/js/echarts/dist/echarts.js"></script>
		<script type="text/javascript">
	        // 路径配置
	        require.config({
	            paths: {
	                echarts: '${BASE_PATH}/resources/js/echarts/dist'
	            }
	        });
	        
	        // 加载地图信息
	        function loadMap(cmData, ctData, cuData) {
		        require(
		            [
		                'echarts',
		                'echarts/chart/map'
		            ],
		            function (ec) {
		            	$("#main").css("height", ($(window).height() - $(window).height() / 10) + "px");
		                // 基于准备好的dom，初始化echarts图表
		                var myChart = ec.init(document.getElementById('main')); 
		                
		                var option = {
						    title : {
						        text: '全国短信发送统计',
						        subtext: '（昨日发送）',
						        x:'center'
						    },
						    tooltip : {
						        trigger: 'item'
						    },
						    legend: {
						        orient: 'vertical',
						        x:'left',
						        data:['移动','联通','电信']
						    },
						    dataRange: {
						        min: 0,
						        max: 2500,
						        x: 'left',
						        y: 'bottom',
						        text:['高','低'],           // 文本，默认为数值文本
						        calculable : true
						    },
						    toolbox: {
						        show: true,
						        orient : 'vertical',
						        x: 'right',
						        y: 'center',
						        feature : {
						            mark : {show: true},
						            dataView : {show: true, readOnly: false},
						            restore : {show: true},
						            saveAsImage : {show: true}
						        }
						    },
						    roamController: {
						        show: true,
						        x: 'right',
						        mapTypeControl: {
						            'china': true
						        }
						    },
						    series : [
						        {
						            name: '移动',
						            type: 'map',
						            mapType: 'china',
						            roam: false,
						            itemStyle:{
						                normal:{label:{show:true}},
						                emphasis:{label:{show:true}}
						            },
						            data: cmData
						        },
						        {
						            name: '联通',
						            type: 'map',
						            mapType: 'china',
						            itemStyle:{
						                normal:{label:{show:true}},
						                emphasis:{label:{show:true}}
						            },
						            data: cuData
						        },
						        {
						            name: '电信',
						            type: 'map',
						            mapType: 'china',
						            itemStyle:{
						                normal:{label:{show:true}},
						                emphasis:{label:{show:true}}
						            },
						            data: ctData
						        }
						    ]
						};
						
						// 为echarts对象加载数据 
	               		myChart.setOption(option); 
						                    
					}
					                    
		         );
	        };
	        
	        function getData() {
	        	 $.ajax({
		            url:'${BASE_PATH}/report/sms/province_cmcp_report',
		            dataType:'json',
		            type:'post',
		            success:function(data){
		                if(data.result && data.obj != undefined && data.obj != null){
		                  loadMap(data.obj.cmlist, data.obj.ctlist, data.obj.culist);
		                }
		            },error:function(data){
		            	console.log(data);
		            }
		        });
	        
	        };
	        
	        getData();
	        
	    </script>
	</body>

</html>