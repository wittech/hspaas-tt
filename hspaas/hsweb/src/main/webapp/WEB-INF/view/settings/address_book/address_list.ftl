<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
				<th width="5%"></th>
				<th width="15%">姓名/公司名称</th>
				<th width="25%">手机号码</th>
				<th width="50%">地址</th>
			</tr>
		</thead>
		<tbody>
			<#if addressList??>
				<#list addressList as p>
				<tr>
					<td><input name="address_group" type="radio" value="${(p.id)!}" id="a_${(p.id)!}" <#if p.isDefault==0><#else>checked</#if>/></td>
					<td id="name_${(p.id)!}">${(p.name)!}</td>
					<td id="mobile_${(p.id)!}">${(p.mobile)!}</td>
					<td id="address_${(p.id)!}">${(p.city)!}${(p.province)!}${(p.address)!}</td>
				</tr>
				</#list>
			</#if>
		</tbody>
	</table>
</div>
<script>

	function choose(){
	
		var id = $("input[type=radio][name=address_group]:checked").val();
		if(id == undefined || id == null)
			return;
			
		$("#address_book_id").val(id);
		$("#name").val($("#name_"+id).html());
		$("#mobile").val($("#mobile_"+id).html());
		$("#address").val($("#address_"+id).html());
		
		Hs.close();
	}
</script>
