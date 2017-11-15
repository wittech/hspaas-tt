
<#-- 转换回执状态报告信息中特殊符号 -->
<#macro getReportDes remark>
	<#assign report = remark>
	<#if remark?? && remark != ''>
		<#assign report = report?replace("\\", "&quot;")>
		<#assign report = report?replace("'", "&#39;") >
		<#assign report = report?replace("<", "&lt;") >
		<#assign report = report?replace(">", "&gt;") >
	</#if>
	${(report)!}
</#macro>