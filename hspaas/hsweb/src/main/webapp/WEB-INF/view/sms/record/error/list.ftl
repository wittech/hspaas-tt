<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="5%">序号</th>
				<th width="50%">短信内容</th>
				<th width="20%">提交时间</th>
				<th width="30%">错误描述</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>
                        ${p.content!''}
                        <span style="color:red">
                        [字数：${p.content?length}]
                        </span>
                </td>
				<td>${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
				<td><span class="tag  bg-red" style="color:red;">${(p.respCode)!}:${(p.errorText)!}</span></td>
			</tr>
			<tr>
                    <td colspan="5" style="word-break:break-all;text-align:right;">
                        ${p.mobile!''}
                    </td>
             </tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无错误记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>