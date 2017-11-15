<div style="padding:5px 20px 0">
	<div class="row" style="margin-top:-20px">
		<form id="userform" method="post">
		<input type="hidden" name="pn" id="userpn" value="1">
		<div class="row">
	    	<div class="col-md-4">
	    		<div class="input-group">
	    			<span class="input-group-addon">姓名</span>
	    			<input type="text" class="form-control" id="fullName" name="fullName" value="${fullName!''}" placeholder="输入姓名查询">
	    		</div>
	    	</div>
	    	<div class="col-md-4">
	    		<div class="input-group">
	    			<span class="input-group-addon">手机号码</span>
	    			<input type="text" class="form-control" id="mobile" name="mobile" value="${mobile!''}" placeholder="输入手机号码查询">
	    		</div>
	    	</div>
	    	<div class="col-md-4">
	    		<div class="input-group">
	    			<span class="input-group-addon">公司名称</span>
	    			<input type="text" class="form-control" id="company" name="company" value="${company!''}" placeholder="输入公司名称查询">
	    		</div>
	    	</div>
	    </div>
	    
	    <div class="row" style="margin-top:5px">
	    	<div class="col-md-4">
	    		<div class="input-group">
	    			<span class="input-group-addon">身份证号</span>
	    			<input type="text" class="form-control" id="cardNo" name="cardNo" value="${cardNo!''}" placeholder="输入身份证号查询">
	    		</div>
	    	</div>
	    	<div class="col-md-4">
	    		<div class="input-group">
	    			<span class="input-group-addon">状态</span>
	    			<select id="state" name="state" class="form-control">
	    				<option value="-1">==客户状态==</option>
	    				<option value="0" <#if state?? && state == '0'>selected</#if>>启用</option>
	    				<option value="1" <#if state?? && state == '1'>selected</#if>>已禁用</option>
	    			</select>
	    		</div>
	    	</div>
	    	<div class="col-md-4">
	    		<a class="btn btn-primary" onclick="userJumpPage(1);">查&nbsp;&nbsp;&nbsp;询</a>
	    	</div>
	    </div>
	    </form>
	</div>
	<div class="row" style="margin-top:10px">
	    <table class="table table-striped table-bordered" cellspacing="0" width="100%">
	        <thead>
	            <tr>
	            	<th>编号</th>
	                <th>姓名</th>
	                <th>手机号</th>
	                <th>公司名称</th>
	                <th>身份证号</th>
	                <th>操作</th>
	            </tr>
	        </thead>
	        <tbody>
	        	<#if page?? && page.list?size gt 0>
	        		<#list page.list as pl>
	                    <tr>
	                        <td>${(page.currentPage - 1) * page.pageSize + (pl_index+1)}</td>
	                        <td>${(pl.fullName)!''}</td>
	                        <td>${(pl.mobile)!''}</td>
	                        <td>${(pl.company)!''}</td>
	                        <td>${(pl.cardNo)!''}</td>
	                        <td>
	                        	<#if userId?? && userId == pl.userId>
	                        	<#else>
	                        		<a class="btn btn-primary btn-xs" href="javascript:void(0);" onclick="selectUser(${pl.userId},'${pl.fullName}','${pl.mobile}');"><i class="fa fa-edit"></i>&nbsp;选择 </a>
	                        	</#if>
	                        </td>
	                    </tr>
	            	</#list>
	            <#else>
	            <tr>
	                <td colspan="9">未查询到任何数据！</td>
	            </tr>
	            </#if>
	        </tbody>
	    </table>
	    <nav style="margin-top:-15px">
			${(page.showPageHtml)!}
		</nav>
	</div>
</div>