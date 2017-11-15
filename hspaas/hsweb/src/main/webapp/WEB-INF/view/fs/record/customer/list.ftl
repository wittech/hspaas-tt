<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="100">序号</th>
				<th width="100">类型</th>
				<th width="100">发生金额(元)</th>
				<th width="100">余额(元)</th>
				<th width="120">发生时间</th>
				<th width="140">备注</th>
			</tr>
		</thead>
		<tbody>
        	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(p.feeTypeText)!}</td>
				<td>${(p.feeOffset)!}</td>
				<td>${(p.feeResult)!}</td>
				<td>${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
				<td>${(p.des)!}</td>
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