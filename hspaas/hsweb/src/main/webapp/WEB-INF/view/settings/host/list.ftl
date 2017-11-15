<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="5%">序号</th>
				<th width="15%">ip地址</th>
				<th width="30%">时间</th>
				<th width="50%">操作</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(p.ip)!}</td>
				<td>${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
				<td><a href="javascript:remove(${(p.id)!});">删除</a></td>
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="4" style="color:#F00;text-align:center;">暂无ip白名单记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>