<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="5%">序号</th>
				<th width="20%">发送时间</th>
				<th width="20%">发送状态</th>
				<th width="20%">内容</th>
				<th width="15%">手机号</th>
				<th width="20%">接收状态</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
				<td>${(p.sendStatus)!}</td>
				<td>${(p.content)}（${(p.content?length)}字）</td>
				<td>${(p.mobile)!}</td>
				<#if p.receiveStatus??>
					<#if p.receiveStatus=="接收失败">
						<td><a href="javascript:void(0)" title="${(p.errMsg)!}" id="modal-18">${(p.receiveStatus)!}</a></td>
					<#elseif p.receiveStatus=="等待返回">
						<td><a href="javascript:void(0)" title="">${(p.receiveStatus)!}</a></td>
					<#else>
						<td>${(p.receiveStatus)!}</td>
					</#if>
				</#if>
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无发送记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>