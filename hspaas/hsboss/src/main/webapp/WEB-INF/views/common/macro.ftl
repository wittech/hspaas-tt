<#-- 转换回执状态报告信息中特殊符号 -->
<#macro getReportDes remark>
	<#compress>
	<#assign report = remark>
	<#if remark?? && remark != ''>
		<#if remark?index_of("\\") != -1>
			<#assign report = report?replace("\\", "")>
		</#if>
		
		<#if remark?index_of("'") != -1>
			<#assign report = report?replace("'", "&#39;")>
		</#if>

		<#if remark?index_of("\"") != -1>
			<#assign report = report?replace("\"", "&quot;")>
		</#if>
		
		<#if remark?index_of("<") != -1>
			<#assign report = report?replace("<", "&lt;")>
		</#if>
		
		<#if remark?index_of(">") != -1>
			<#assign report = report?replace(">", "&gt;")>
		</#if>
		
	</#if>${(report)!}
	</#compress>
</#macro>

<#function test>
	<#assign str = "abcd" />
	<#return str />
</#function>

<#function doOper code>
    <#return session.doOper("${code}") />
</#function>