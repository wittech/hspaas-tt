<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="10%">序号</th>
				<th width="20%">交易号</th>
				<th width="10%">充值号码</th>
				<th width="10%">运营商</th>
				<th width="20%">流量包(M)</th>
				<th width="20%">提交时间</th>
				<th width="10%">充值状态</th>
			</tr>
		</thead>
		<tbody>
        	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(p.customerTradeno)!}</td>
				<td>${(p.mobile)!}</td>
				<td>${(p.local)!}</td>
				<td>${(p.flowPackageZhaoText)!}</td>
				<td>${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
				<td>
					<#if p.trade_status = 3>
						<span class="badge bg-green">充值成功</span>
					<#elseif p.trade_status = 4>
						<span class="badge bg-red">充值失败</span>
					<#else>	
						<span class="badge bg-gray">处理中</span>
					</#if>
				</td>
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无充值记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>