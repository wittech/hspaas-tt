<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			 <tr class="even">
			 	<th width="5%">序号</th>
                <th  width="10%">订单号</th>
                <th  width="10%">订单类型</th>
                <th  width="10%">订单时间</th>
                <th  width="10%">支付方式</th>
                <th  width="10%">金额</th>
                <th  width="10%">状态</th>
             </tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as p>
			<tr>
				<td>${(p_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(p.orderNo)!}</td>
				<td><#if p.tradeType=="0">产品购买<#elseif p.tradeType=="1">账户金额充值</#if></td>
				<td>${(p.createTime?string("yyyy-MM-dd HH:mm:ss"))!}</td>
				<td><#if p.payType=="1">账户转账<#elseif p.payType=="2">线下转账<#elseif p.payType=="3">支付宝支付<#elseif p.payType=="4">微信支付</#if></td>
				<td>${(p.productFee)!}</td>
				<td><#if p.status=="0">待支付<#elseif p.status=="1">支付完成，待处理<#elseif p.status=="2">支付失败<#elseif p.status=="3">已完成<#elseif p.status=="4">处理失败</#if></td>
			</tr>
			</#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无充值账单记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>