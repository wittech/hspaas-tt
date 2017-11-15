<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="5%">序号</th>
				<th width="10%">手机号码</th>
				<th width="40%">短信内容</th>
				<th width="20%">服务号长号码</th>
				<th width="15%">接收时间</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(p.srcTerminalId)!}</td>
				<td>${(p.content)!}</td>
				<td>${(p.destnationId)!}</td>
				<td>${(p.receiveTimeText)!}</td>
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无接收记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>