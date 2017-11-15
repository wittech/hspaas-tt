<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="3%">序号</th>
				<th width="30%">套餐名称</th>
				<th width="10%">套餐价格（元）</th>
				<th width="50%">描述</th>
				<th width="8%">操作</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(p.name)!}</td>
				<td>${(p.sellMoney)!}</td>
				<td>${(p.description)!}</td>
				<td><a href="${rc.contextPath}/order/index/${(p.id)!}">购买</a></td>
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无产品记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>