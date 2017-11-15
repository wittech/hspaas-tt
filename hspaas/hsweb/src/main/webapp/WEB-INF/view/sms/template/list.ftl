<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="5">序号</th>
				<th width="80">模板类型</th>
				<th width="200">模板内容</th>
                <th width="80">审核状态</th>
				<th width="80">审核原因</th>
				<th width="80">操作</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>
					<#if p.appType==0>验证码模板<#elseif p.appType==1>营销模板<#elseif p.appType==2>其它</#if>
				</td>
				<td>${(p.content)!}</td>
				<td>
					<#if p.status==0>待审核<#elseif p.status==1>审核成功<#elseif p.status==2>审核失败</#if>
				</td>
				<td>${(p.remark)!}</td>
				<td><a href="${rc.contextPath}/sms/template/matching?id=${(p.id)!}">模板测试</a></td>
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无模板记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>