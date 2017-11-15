<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="10%">发票抬头</th>
				<th width="10%">税号</th>
				<th width="10%">收件人</th>
				<th width="8%">发票类型</th>
				<th width="8%">发票金额</th>
				<th width="5%">状态</th>	
				<th width="10%">发票时间</th>	
				<th width="20%">收货地址</th>	
				<#--<th width="10%">操作</th>-->	
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p.title)!}</td>
				<td>${(p.taxNumber)!}</td>
				<td>${(p.mailMan)!}</td>
				<td><#if p.type==0>普通发票<#else>纳税人发票</#if></td>
				<td>${(p.money)!}</td>
				<td><#if p.status==0>待处理<#else>已邮寄</#if></td>
				<td>${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
				<td>${(p.address)!}</td>
				<#--<td><a href="javascript:alertDetail('${(p.trackingNumber)!}','${(p.express)!}','${(p.memo)!}','<#if p.chargeType==0>到付<#else>预付</#if>','${(p.chargeMoney)!}');">详情</a></td>-->
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>