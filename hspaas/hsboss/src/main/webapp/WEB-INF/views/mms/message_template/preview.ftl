<h3>${(title)!}</h3>
<#if bodies??>
<#list bodies as t>
	<#if t.mediaType == "text">
		${(t.content)!}
	<#elseif t.mediaType == "image">
		<img src="${(t.content)!}" width="150px">
	<#elseif t.mediaType == "audio">
		<audio controls><source src="${(t.content)!}" type='audio/${(t.mediaName)!}'>您的浏览器不支持 audio播放</audio>
	<#elseif t.mediaType == "video">
		<video controls  height='140'><source src="${(t.content)!}" type='audio/${(t.mediaName)!}'>您的浏览器不支持 video播放</video>
	</#if>
	<br/>
</#list>
</#if>