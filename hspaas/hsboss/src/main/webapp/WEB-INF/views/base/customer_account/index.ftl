<!DOCTYPE html>
<html lang="en">

	<head>
		<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
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
						<div class="breadcrumb-wrapper"> <span class="label">所在位置:</span>
							<ol class="breadcrumb">
								<li> <a href="#"> 管理平台 </a> </li>
								<li> <a href="#"> 系统管理 </a> </li>
								<li class="active"> 客户帐号管理 </li>
							</ol>
						</div>
					</div>
					<div id="page-content">
						
						<div class="panel">
							<div class="panel-body">
								<div class="row">
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">查询条件</span>
							    			<input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">查询条件</span>
							    			<input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">查询条件</span>
							    			<input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
							    		</div>
							    	</div>
							    </div>
							    
							    <div class="row" style="margin-top:5px">
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">查询条件</span>
							    			<input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<div class="input-group">
							    			<span class="input-group-addon">查询条件</span>
							    			<input type="text" class="form-control" id="inputGroupSuccess4" placeholder="查询条件">
							    		</div>
							    	</div>
							    	<div class="col-md-4">
							    		<button class="btn btn-primary">查&nbsp;&nbsp;&nbsp;询</button>
							    	</div>
							    </div>
							</div>
						</div>


						<div class="panel">
                        <div class="panel-heading">
                            <div class="pull-right" style="margin-top: 10px;margin-right: 20px;">
                                <button class="btn btn-success">添加客户帐号</button>
                            </div>
                            <h3 class="panel-title">
                            <span>客户帐号列表</span>
                            </h3>
                           
                        </div>
                        <div class="panel-body">
                            <table id="demo-dt-basic" class="table table-striped table-bordered" cellspacing="0" width="100%">
                                <thead>
                                    <tr>
                                        <th>姓名</th>
                                        <th>地址</th>
                                        <th>公司</th>
                                        <th>部门</th>
                                        <th>开始时间</th>
                                        <th>日志</th>
                                        <th>操作</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>Jennifer Chang</td>
                                        <td>Regional Director</td>
                                        <td>Singapore</td>
                                        <td>28</td>
                                        <td>2010/11/14</td>
                                        <td>$357,650</td>
                                        <td>
                                        	<a class="btn btn-info btn-xs"><i class="fa fa-file"></i>&nbsp;查看 </a>
                                        	<a class="btn btn-primary btn-xs"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	<a class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除 </a>
                                        	<a class="btn btn-default btn-xs"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Brenden Wagner</td>
                                        <td>Software Engineer</td>
                                        <td>San Francisco</td>
                                        <td>28</td>
                                        <td>2011/06/07</td>
                                        <td>$206,850</td>
                                          <td>
                                        	<a class="btn btn-info btn-xs"><i class="fa fa-file"></i>&nbsp;查看 </a>
                                        	<a class="btn btn-primary btn-xs"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	<a class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除 </a>
                                        	<a class="btn btn-default btn-xs"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Fiona Green</td>
                                        <td>Chief Operating Officer (COO)</td>
                                        <td>San Francisco</td>
                                        <td>48</td>
                                        <td>2010/03/11</td>
                                        <td>$850,000</td>
                                          <td>
                                        	<a class="btn btn-info btn-xs"><i class="fa fa-file"></i>&nbsp;查看 </a>
                                        	<a class="btn btn-primary btn-xs"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	<a class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除 </a>
                                        	<a class="btn btn-default btn-xs"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Shou Itou</td>
                                        <td>Regional Marketing</td>
                                        <td>Tokyo</td>
                                        <td>20</td>
                                        <td>2011/08/14</td>
                                        <td>$163,000</td>
                                          <td>
                                        	<a class="btn btn-info btn-xs"><i class="fa fa-file"></i>&nbsp;查看 </a>
                                        	<a class="btn btn-primary btn-xs"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	<a class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除 </a>
                                        	<a class="btn btn-default btn-xs"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Michelle House</td>
                                        <td>Integration Specialist</td>
                                        <td>Sidney</td>
                                        <td>37</td>
                                        <td>2011/06/02</td>
                                        <td>$95,400</td>
                                          <td>
                                        	<a class="btn btn-info btn-xs"><i class="fa fa-file"></i>&nbsp;查看 </a>
                                        	<a class="btn btn-primary btn-xs"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	<a class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除 </a>
                                        	<a class="btn btn-default btn-xs"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>Donna Snider</td>
                                        <td>Customer Support</td>
                                        <td>New York</td>
                                        <td>27</td>
                                        <td>2011/01/25</td>
                                        <td>$112,000</td>
                                          <td>
                                        	<a class="btn btn-info btn-xs"><i class="fa fa-file"></i>&nbsp;查看 </a>
                                        	<a class="btn btn-primary btn-xs"><i class="fa fa-edit"></i>&nbsp;编辑 </a>
                                        	<a class="btn btn-danger btn-xs"><i class="fa fa-trash"></i>&nbsp;删除 </a>
                                        	<a class="btn btn-default btn-xs"><i class="fa fa-unlock-alt"></i>&nbsp;启用 </a>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
				</div>
				<#include "/WEB-INF/views/main/left.ftl">
			</div>

		</div>
		<script src="${BASE_PATH}/resources/js/bootstrap/jquery-2.1.1.min.js"></script>
		<script src="${BASE_PATH}/resources/js/confirm/jquery-confirm.js"></script> <script src="${BASE_PATH}/resources/js/pop/jquery-migrate-1.2.1.js"></script> <script src="${BASE_PATH}/resources/js/pop/yanue.pop.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/bootstrap.min.js"></script>
		<script src="${BASE_PATH}/resources/js/bootstrap/scripts.js"></script>
	</body>

</html>