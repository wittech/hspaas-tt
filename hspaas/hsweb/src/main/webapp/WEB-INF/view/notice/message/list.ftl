<table cellpadding="0" cellspacing="0" border="0">
	<tr>
			<th>消息内容</th>
			<th>通知时间</th>
			<th></th>
	</tr>
	<#if page.list?? && page.totalPage gt 0>
		<#list page.list as p>
		<tr id="${(p.id)!}" class="msg_list read " onclick="updateNotice(${(p.id)!},'${(p.status)!}');">
			<td width="70%">
				<div class="msg_txt">
					<h1>${(p.title)!}</h1><!--unfold block-->
					<p id="p_c_${(p.id)!}" style="display: none;">
						<span class="blue">${(p.content)!}</span><br>
					</p>
					<p id="p_d_${(p.id)!}" style="display: none;">
					华时科技团队<br>
					${(p.createTime?string("yyyy-MM-dd"))!}
					</p>
				</div>
			</td>
			<td width="26%" class="date">
				${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}
			</td>
			<td width="4%" class="icon">
				<p><a href="javascript:remove(${(p.id)!});" class="del">&nbsp;</a></p>
			</td>
		</tr>
		</#list>
	<#else>
		<tr class="msg_list">
			<td colspan="3"> 暂无数据!</td>
		</tr>
	</#if>
</table>
<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>
