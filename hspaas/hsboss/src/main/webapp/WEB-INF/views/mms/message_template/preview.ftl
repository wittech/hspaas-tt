

<h3 style="width:400px">${(title)!}</h3>
<#if bodies??>
<#list bodies as t>
<p style="width:400px">
	<#if t.mediaType == "text">
		${(t.content)!}
	<#elseif t.mediaType == "image">
		<img src="${(t.content)!}" width="350px">
	<#elseif t.mediaType == "audio">
		<audio controls width="350px"><source src="${(t.content)!}" type='audio/${(t.mediaName)!}'>您的浏览器不支持 audio播放</audio>
	<#elseif t.mediaType == "video">
		<video controls  width="350px"><source src="${(t.content)!}" type='audio/${(t.mediaName)!}'>您的浏览器不支持 video播放</video>
	</#if>
<p>
<hr>
</#list>
</#if>