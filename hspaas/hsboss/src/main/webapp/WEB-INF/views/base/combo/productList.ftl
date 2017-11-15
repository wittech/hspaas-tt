<table class="table table-striped table-bordered" cellspacing="0" width="100%">
    <thead>
        <tr>
        	<th>编号</th>
            <th>产品名称</th>
            <th>类型</th>
            <th>金额</th>
            <th>数量</th>
            <th>操作</th>
        </tr>
    </thead>
    <tbody>
    <#list productList as pl>
        <tr>
            <td>${(pl_index+1)}</td>
            <td>${pl.name}</td>
            <td>
            	<#if pl.type == 1>
            		短信
            	<#elseif pl.type == 2>
            		流量
            	<#elseif pl.type == 3>
            		语音
            	<#else>
            		未知
            	</#if>
            </td>
            <td>${pl.money}</td>
            <td>${pl.amount}&nbsp;${pl.unit!''}</td>
            <td>
            		<#assign cId = pl.id + "" />
			    	<#assign isExists = false />
			    	<#list  filterIds?split(',') as sId>
			    		<#if sId == cId>
			    			<#assign isExists = true>
			    			<a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="removeSelectListItem(this,${pl.id},'${pl.name}-${pl.amount}${pl.unit!''}',${pl.money});"><i class="fa fa-trash"></i>&nbsp;移除 </a>
			    		</#if>
			    	</#list>
			    	<#if !isExists>
			    		<a class="btn btn-default btn-xs" href="javascript:void(0);" onclick="selectItem(this,${pl.id},'${pl.name}-${pl.amount}${pl.unit!''}',${pl.money});"><i class="fa fa-shopping-cart"></i>&nbsp;选择 </a>
			    	</#if>
            </td>
        </tr>
    </#list>
    
    </tbody>
</table>