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
				
					<header class="pageheader">
	                    <h3><i class="fa fa-home"></i> Dashboard </h3>
	                </header>

					<div id="page-content">
						
						<#--
						<h2>你好，${session.realName?if_exists}</h2>
						<h3>欢迎你使用华时融合平台...<h3>
						 -->
						<div class="row">
                        <div class="col-md-3 col-sm-6 col-xs-12">
                            <div class="panel">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-xs-9">
                                            <h3 class="nm"> <span class="timer" data-from="0" data-to="450" data-speed="5000" data-refresh-interval="50">450</span></h3>
                                            <p>提交总条数</p>
                                        </div>
                                        <div class="col-xs-3"> <i class="fa fa-shopping-cart fa-3x text-info"></i> </div>
                                    </div>
                                    <div class="progress progress-striped progress-xs">
                                        <div style="width: 4%;" aria-valuemax="100" aria-valuemin="0" aria-valuenow="60" role="progressbar" class="progress-bar"> <span class="sr-only">60% Complete</span> </div>
                                    </div>
                                    <p class="nm"> 环比昨日增长4% </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 col-sm-6 col-xs-12">
                            <div class="panel widget">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-xs-9">
                                            <h3 class="nm"> <span class="timer" data-from="0" data-to="599" data-speed="5000" data-refresh-interval="50">599</span></h3>
                                            <p>成功总条数</p>
                                        </div>
                                        <div class="col-xs-3"> <i class="fa fa-check fa-3x text-success"></i> </div>
                                    </div>
                                    <div class="progress progress-striped progress-xs nm">
                                        <div style="width: 93%;" aria-valuemax="100" aria-valuemin="0" aria-valuenow="60" role="progressbar" class="progress-bar progress-bar-success"> <span class="sr-only">60% Complete</span> </div>
                                    </div>
                                    <p class="nm"> 相比提交总条数占比 93% </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 col-sm-6 col-xs-12">
                            <div class="panel">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-xs-9">
                                            <h3 class="nm"> <span class="timer" data-from="0" data-to="65" data-speed="5000" data-refresh-interval="50">65</span></h3>
                                            <p>失败总条数</p>
                                        </div>
                                        <div class="col-xs-3"> <i class="fa fa-remove fa-3x text-danger"></i> </div>
                                    </div>
                                    <div class="progress progress-striped progress-xs nm">
                                        <div style="width: 60%;" aria-valuemax="100" aria-valuemin="0" aria-valuenow="60" role="progressbar" class="progress-bar progress-bar-danger"> <span class="sr-only">60% Complete</span> </div>
                                    </div>
                                    <p class="nm"> 4% higher than last month </p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-3 col-sm-6 col-xs-12">
                            <div class="panel widget">
                                <div class="panel-body">
                                    <div class="row">
                                        <div class="col-xs-9">
                                            <h3 class="nm"> <span class="timer" data-from="0" data-to="65" data-speed="5000" data-refresh-interval="50">65</span>%</h3>
                                            <p>未知总条数</p>
                                        </div>
                                        <div class="col-xs-3"> <i class="fa fa-question fa-3x text-warning"></i> </div>
                                    </div>
                                    <div class="progress progress-striped progress-xs nm">
                                        <div style="width: 60%;" aria-valuemax="100" aria-valuemin="0" aria-valuenow="60" role="progressbar" class="progress-bar progress-bar-warning"> <span class="sr-only">60% Complete</span> </div>
                                    </div>
                                    <p class="nm"> 4% higher than last month </p>
                                </div>
                            </div>
                        </div>
                    </div> 
						 
						 
					<div class="row">
                        <div class="col-md-6">
                            <div class="panel">
                                <div class="panel-heading">
                                    <h3 class="panel-title">Line Chart</h3>
                                </div>
                                <div class="panel-body">

                                    <!--Flot Line Chart placeholder-->
                                    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
                                    <div id="demo-flot-line" style="height: 212px; padding: 0px; position: relative;"><canvas class="flot-base" width="488" height="212" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 488px; height: 212px;"></canvas><div class="flot-text" style="position: absolute; top: 0px; left: 0px; bottom: 0px; right: 0px; font-size: smaller; color: rgb(84, 84, 84);"><div class="flot-x-axis flot-x1-axis xAxis x1Axis" style="position: absolute; top: 0px; left: 0px; bottom: 0px; right: 0px; display: block;"><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 11px; text-align: center;">1.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 52px; text-align: center;">2.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 94px; text-align: center;">3.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 135px; text-align: center;">4.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 177px; text-align: center;">5.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 218px; text-align: center;">6.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 260px; text-align: center;">7.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 301px; text-align: center;">8.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 343px; text-align: center;">9.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 381px; text-align: center;">10.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 422px; text-align: center;">11.0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 41px; top: 195px; left: 464px; text-align: center;">12.0</div></div><div class="flot-y-axis flot-y1-axis yAxis y1Axis" style="position: absolute; top: 0px; left: 0px; bottom: 0px; right: 0px; display: block;"><div class="flot-tick-label tickLabel" style="position: absolute; top: 182px; left: 7px; text-align: right;">0</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 152px; left: 7px; text-align: right;">5</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 122px; left: 0px; text-align: right;">10</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 92px; left: 0px; text-align: right;">15</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 62px; left: 0px; text-align: right;">20</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 32px; left: 0px; text-align: right;">25</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 2px; left: 0px; text-align: right;">30</div></div></div><canvas class="flot-overlay" width="488" height="212" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 488px; height: 212px;"></canvas><div class="legend"><div style="position: absolute; width: 94px; height: 57px; left: 34px; opacity: 0.85; background-color: rgb(255, 255, 255);"> </div><table style="position:absolute;top:NaNpx;left:34px;;font-size:smaller;color:#545454"><tbody><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid rgb(55,188,155);overflow:hidden"></div></div></td><td class="legendLabel">Pageviews</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid rgb(246,187,66);overflow:hidden"></div></div></td><td class="legendLabel">Unique Visitor</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid rgb(44,150,124);overflow:hidden"></div></div></td><td class="legendLabel">Visitor</td></tr></tbody></table></div></div>
                                    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

                                </div>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="panel">
                                <div class="panel-heading">
                                    <h3 class="panel-title">Bar Chart</h3>
                                </div>
                                <div class="panel-body">

                                    <!--Flot Bar Chart placeholder -->
                                    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
                                    <div id="placeholder1" style="height: 212px; padding: 0px; position: relative;"><canvas class="flot-base" width="488" height="212" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 488px; height: 212px;"></canvas><div class="flot-text" style="position: absolute; top: 0px; left: 0px; bottom: 0px; right: 0px; font-size: smaller; color: rgb(84, 84, 84);"><div class="flot-x-axis flot-x1-axis xAxis x1Axis" style="position: absolute; top: 0px; left: 0px; bottom: 0px; right: 0px; display: block;"><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 50px; top: 194px; left: 38px; text-align: center;">0</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 50px; top: 194px; left: 97px; text-align: center;">1</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 50px; top: 194px; left: 157px; text-align: center;">2</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 50px; top: 194px; left: 217px; text-align: center;">3</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 50px; top: 194px; left: 276px; text-align: center;">4</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 50px; top: 194px; left: 336px; text-align: center;">5</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 50px; top: 194px; left: 396px; text-align: center;">6</div><div class="flot-tick-label tickLabel" style="position: absolute; max-width: 50px; top: 194px; left: 455px; text-align: center;">7</div></div><div class="flot-y-axis flot-y1-axis yAxis y1Axis" style="position: absolute; top: 0px; left: 0px; bottom: 0px; right: 0px; display: block;"><div class="flot-tick-label tickLabel" style="position: absolute; top: 181px; left: 8px; text-align: right;">0</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 136px; left: 8px; text-align: right;">5</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 91px; left: 1px; text-align: right;">10</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 46px; left: 1px; text-align: right;">15</div><div class="flot-tick-label tickLabel" style="position: absolute; top: 1px; left: 1px; text-align: right;">20</div></div></div><canvas class="flot-overlay" width="488" height="212" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 488px; height: 212px;"></canvas></div>
                                    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

                                </div>
                            </div>
                        </div>
                    </div>
                    
                    
                    <div class="row">
                        <div class="col-lg-6">
                            <div class="panel">
                                <div class="panel-heading">
                                    <h3 class="panel-title">Pie Chart</h3>
                                </div>
                                <div class="panel-body">

                                    <!--Flot Pie Chart placeholder -->
                                    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
                                    <div id="demo-flot-pie" style="height: 212px; padding: 0px; position: relative;"><canvas class="flot-base" width="488" height="212" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 488px; height: 212px;"></canvas><canvas class="flot-overlay" width="488" height="212" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 488px; height: 212px;"></canvas><div class="legend"><div style="position: absolute; width: 73px; height: 76px; top: 5px; right: 5px; opacity: 0.85; background-color: rgb(255, 255, 255);"> </div><table style="position:absolute;top:5px;right:5px;;font-size:smaller;color:#545454"><tbody><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #177bbb;overflow:hidden"></div></div></td><td class="legendLabel">Comedy</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #a6c600;overflow:hidden"></div></div></td><td class="legendLabel">Action</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #8669CC;overflow:hidden"></div></div></td><td class="legendLabel">Adventure</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #f84f9a;overflow:hidden"></div></div></td><td class="legendLabel">Drama</td></tr></tbody></table></div></div>
                                    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

                                </div>
                            </div>
                        </div>
                        <div class="col-lg-6">
                            <div class="panel">
                                <div class="panel-heading">
                                    <h3 class="panel-title">Pie Chart</h3>
                                </div>
                                <div class="panel-body">

                                    <!--Flot Donut Chart placeholder -->
                                    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->
                                    <div id="demo-flot-donut" style="height: 212px; padding: 0px; position: relative;"><canvas class="flot-base" width="488" height="212" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 488px; height: 212px;"></canvas><canvas class="flot-overlay" width="488" height="212" style="direction: ltr; position: absolute; left: 0px; top: 0px; width: 488px; height: 212px;"></canvas><div class="legend"><div style="position: absolute; width: 73px; height: 76px; top: 5px; right: 5px; opacity: 0.85; background-color: rgb(255, 255, 255);"> </div><table style="position:absolute;top:5px;right:5px;;font-size:smaller;color:#545454"><tbody><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #177bbb;overflow:hidden"></div></div></td><td class="legendLabel">Comedy</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #a6c600;overflow:hidden"></div></div></td><td class="legendLabel">Action</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #8669CC;overflow:hidden"></div></div></td><td class="legendLabel">Adventure</td></tr><tr><td class="legendColorBox"><div style="border:1px solid #ccc;padding:1px"><div style="width:4px;height:0;border:5px solid #f84f9a;overflow:hidden"></div></div></td><td class="legendLabel">Drama</td></tr></tbody></table></div></div>
                                    <!-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ -->

                                </div>
                            </div>
                        </div>
                    </div>
                    
                    
                    <div class="row">
                    	<div class="col-md-4">
                            <div class="panel">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1 -->
                                            <div class="item active">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> Web Portal Redesign Portal </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 55%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Active </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 2 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> A/B Testing </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 65%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Complete </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 3 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> Navigation illustration </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 85%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> On Hold </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 4 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> App Usability Testing </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 45%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Active </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                            <div class="panel">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1-->
                                            <div class="item active left">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-twitter fa-4x text-azure"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>

                                            <!--Item 2-->
                                            <div class="item next left">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-facebook fa-4x text-primary"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>

                                            <!--Item 3-->
                                            <div class="item">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-google-plus fa-4x text-danger"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                            <div class="panel papernote">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1-->
                                            <div class="item next left">
                                                <h4>This is my note #1</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 2-->
                                            <div class="item">
                                                <h4>This is my note #2</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 3-->
                                            <div class="item">
                                                <h4>This is my note #3</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 4-->
                                            <div class="item active left">
                                                <h4>This is my note #4</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                        </div>
                        
                        
                        <div class="col-md-4">
                            <div class="panel">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1 -->
                                            <div class="item active">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> Web Portal Redesign Portal </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 55%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Active </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 2 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> A/B Testing </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 65%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Complete </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 3 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> Navigation illustration </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 85%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> On Hold </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 4 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> App Usability Testing </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 45%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Active </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                            <div class="panel">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1-->
                                            <div class="item active left">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-twitter fa-4x text-azure"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>

                                            <!--Item 2-->
                                            <div class="item next left">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-facebook fa-4x text-primary"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>

                                            <!--Item 3-->
                                            <div class="item">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-google-plus fa-4x text-danger"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                            <div class="panel papernote">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1-->
                                            <div class="item next left">
                                                <h4>This is my note #1</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 2-->
                                            <div class="item">
                                                <h4>This is my note #2</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 3-->
                                            <div class="item">
                                                <h4>This is my note #3</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 4-->
                                            <div class="item active left">
                                                <h4>This is my note #4</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                        </div>
                    
                    
                        <div class="col-md-4">
                            <div class="panel">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1 -->
                                            <div class="item active">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> Web Portal Redesign Portal </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 55%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Active </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 2 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> A/B Testing </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 65%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Complete </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 3 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> Navigation illustration </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 85%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> On Hold </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>

                                            <!--Item 4 -->
                                            <div class="item">
                                                <div class="text-bold pad-ver-5"> Task in Progress </div>
                                                <div class="pad-btm-5"> App Usability Testing </div>
                                                <div class="progress progress-sm">
                                                    <div style="width: 45%;" class="progress-bar progress-bar-primary"></div>
                                                </div>
                                                <div class="pad-ver-5"> Status : </div>
                                                <div class="pull-left text-lg"> Active </div>
                                                <div class="pull-right">
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-pause"></i> Pause </a>
                                                    <a href="#" class="btn btn-info btn-sm"> <i class="fa fa-check"></i> Complete </a>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                            <div class="panel">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1-->
                                            <div class="item active left">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-twitter fa-4x text-azure"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>

                                            <!--Item 2-->
                                            <div class="item next left">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-facebook fa-4x text-primary"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>

                                            <!--Item 3-->
                                            <div class="item">
                                                <div class="media"> <span class="pull-left"> <i class="fa fa-google-plus fa-4x text-danger"></i> </span>
                                                    <div class="media-body">
                                                        <p class="media-heading"><strong>Semantha Schwarz</strong> <small>1 hour ago</small></p>
                                                        <p>Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua consectetur adipisicing. </p>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                            <div class="panel papernote">
                                <div class="panel-body">
                                    <!--Carousel-->
                                    <!--===================================================-->
                                    <div id="demo-carousel" class="carousel slide" data-ride="carousel">
                                        <div class="carousel-inner">

                                            <!--Item 1-->
                                            <div class="item next left">
                                                <h4>This is my note #1</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 2-->
                                            <div class="item">
                                                <h4>This is my note #2</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 3-->
                                            <div class="item">
                                                <h4>This is my note #3</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>

                                            <!--Item 4-->
                                            <div class="item active left">
                                                <h4>This is my note #4</h4>
                                                <p>This is my note content... Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod </p>
                                            </div>
                                        </div>
                                    </div>
                                    <!--===================================================-->
                                    <!--End Carousel-->
                                </div>
                            </div>

                        </div>
                    </div>	 
						 
						 
						 
						 
						 
						<div class="row">
						<div id="main" style="height:600px"></div>
						</div>
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