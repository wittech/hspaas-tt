<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="5%">序号</th>
				<th width="5%">姓名</th>
				<th width="10%">手机号码</th>
				<th width="10%">省份</th>
				<th width="10%">城市</th>
				<th width="10%">区/县</th>
				<th width="20%">地址</th>
				<th width="10%">创建时间</th>
				<th width="10%">是否默认</th>
				<th width="10%">操作</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(p.name)!}</td>
				<td>${(p.mobile)!}</td>
				<td>${(p.province)!}</td>
				<td>${(p.city)!}</td>
				<td>${(p.area)!}</td>
				<td>${(p.address)!}</td>
				<td>${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
				<#--<td>${(p.modifyTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>-->
				<td><#if p.isDefault==0>--<#else>默认</#if></td>
				<td><a href="${rc.contextPath}/settings/address_book/edit?id=${(p.id)!}">修改</a><a href="javascript:remove(${(p.id)!})">删除</a></td>
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无地址记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>