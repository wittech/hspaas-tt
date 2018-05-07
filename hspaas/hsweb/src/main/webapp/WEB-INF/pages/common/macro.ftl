<#macro pager current_page total_record total_page>
	<#assign start_page = current_page - 4>
	<#if total_page lt 1>
		<#assign start_page = 1>
	</#if>
	<#assign end_page = current_page + 4>
	<#if end_page gt total_page>
		<#assign end_page = total_page>
	</#if>
	<div class="pagination">
		<#if current_page lte 8>
			<#assign start_page = 1>
		</#if>
	    <#if total_page - current_page lt 8>
			<#assign end_page = total_page>
		</#if>
		
		<#-- 上一页页码显示 -->
		<#if current_page gt 1>
		<span class="c regular" data-page="${current_page - 1}" data-name="1.16.1">‹</span>
		<#else>
		<span class="c disable">‹</span>
		</#if>
		
		<#-- 中间页码显示 -->
        <#if current_page gt 8>
        	<span <#if p == current_page>class="c active"<#else>class="c regular"</#if> data-page="1" data-name="1.16.1">1</span>
        	<span <#if p == current_page>class="c active"<#else>class="c regular"</#if> data-page="2" data-name="1.16.1">2</span>
        	<span class="disable">...</span>
        </#if>
        <#if end_page gt start_page>
			<#list start_page..end_page as p>
			<span <#if p == current_page>class="c active"<#else>class="c regular"</#if> data-page="${p}" data-name="1.16.1">${p}</span>
        	</#list>
		</#if>
		<#if total_page - current_page gte 8>
			<span class="disable">...</span>
			<span <#if p == current_page>class="c active"<#else>class="c regular"</#if> data-page="${total_page-1}" data-name="1.16.1">${total_page-1}</span>
			<span <#if p == current_page>class="c active"<#else>class="c regular"</#if> data-page="${total_page}" data-name="1.16.1">${total_page}</span>
		</#if>
		
		<#-- 下一页页码显示 -->
    	<#if current_page lt total_page>
    		<span class="c regular" data-page="${current_page + 1}" data-name="1.16.1">›</span>
    	<#else>
    		<span class="c disable">›</span>
    	</#if>
		<span>共${total_page}页，</span>
		<span>到第</span>
		<input type="text" id="jump_page" style="height:30px;">
		<span>页</span>
		<span class="jumper" data-page="${total_page}">GO</span>
    </div>
</#macro>

<#macro approve_status status>
	<#if status == 1>
		<span class="badge bg-green">审批通过</span>
	<#elseif status == 2>
		<span class="badge bg-red">审批失败</span>
	<#else>
		<span class="badge bg-gray">待审核</span>
	</#if>
</#macro>

<#macro account_status status>
	<#if status == 1>
		<span class="badge bg-red">冻结</span>
	<#elseif status == 2>
		<span class="badge bg-gray">注销</span>
	<#else>
		<span class="badge bg-green">启用</span>
	</#if>
</#macro>