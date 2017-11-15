<!DOCTYPE html>
<html lang="zh-cn">
<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<title>华时融合平台管理中心</title>
	<link href="${rc.contextPath}/assets/css/css.css" rel="stylesheet" type="text/css" />
    <script type="text/javascript" src="${rc.contextPath}/assets/js/jquery-1.8.2.js"></script>
    <script type="text/javascript" src="${rc.contextPath}/assets/js/common.js"></script>
</head>
<body>
<#include "/common/header.ftl"/>
<div class="content">
	<#include "/common/sidebar.ftl"/>	    
      	<!--右侧main-->
		<div class="main">
			<div class="breadcrumbs">
				<ul>
					<li><a href="">产品管理</a></li>
					<li class="active"><a href="javascript:void(0)">语音管理</a></li>
				</ul>
			</div>
            
            <div class="main_tab_tit">
				<ul>
					<li onclick="">铃声</li>
					<li onclick="">语音通知</li>
                    <li class="active" onclick="">IVR导航</li>
				</ul>
			</div>

			<!--说明state_box bof-->
			<div class="state_box">
				<h1>说明</h1>
				<p>选择回拨铃声，每个应用只能上传一个欢迎语音，若重复上传则覆盖上一个语音。选择语音验证码欢迎音、语音通知、IVR导航语音，上传数量不限制。</p>

			</div>
			<!--说明state_box eof-->

			<!--搜索-->
			<div class="search_box">
			<div class="search app_search">
				<form id="queryForm" name="queryForm" action="" method="post">

			      	<input type="text" name="text" placeholder="应用名称/ID" value="">
                    <input type="button" class="bg-main" id="search" value="搜索">
			    
                </form>


			</div>
			<div class="search_link"><a href="" class="voice">添加语音</a></div>
		</div>
			<!--//搜索-->

			<!--表格列表-->
			<div class="table_box">
			<table cellpadding="0" cellspacing="0" border="0">
				<thead>
					<tr>
						<th>语音文件ID</th>
						<th>应用名称</th>
						<th>铃声名称</th>
						<th>创建时间</th>
						<th>状态</th>
						<th>操作</th>
					</tr>
				</thead>
	     
	      
	      	<tbody><tr>
	      	<td>暂无数据! &nbsp;&nbsp;<a href="" style="color:#4981bf">去上传语音</a></td>
	      	<td>&nbsp;</td>
	      	<td>&nbsp;</td>
	      	<td>&nbsp;</td>
	      	<td>&nbsp;</td>
	      	<td>&nbsp;</td>
	      	</tr>
	      
	      </tbody>
      </table>
       <div class="pagenum">
	<span>共<i>0</i>条记录 每页显示15条 当前1/0页</span>
</div>

     </div>
			
			<!--//表格列表-->
		</div>
		<!--右侧main-->
        
</div>
</body>
</html>