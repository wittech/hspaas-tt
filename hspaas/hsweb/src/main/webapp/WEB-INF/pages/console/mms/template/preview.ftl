<div class="layui-card">
    <div class="layui-card-body">
        <div class="components-search-list-messageText">
            <h3>${(template.title)!}</h3>
            
            <#if template?? && template.bodies??>
			<#list template.bodies as t>
			<p>
				<#if t.mediaType == "text">
					${(t.content)!}
				<#elseif t.mediaType == "image">
					<img src="${(t.content)!}" style="height:350px;">
				<#elseif t.mediaType == "audio">
					<audio controls style="width:350px;"><source src="${(t.content)!}" type='audio/${(t.mediaName)!}'>您的浏览器不支持 audio播放</audio>
				<#elseif t.mediaType == "video">
					<video controls style="width:350px;height:350px;"><source src="${(t.content)!}" type='audio/${(t.mediaName)!}'>您的浏览器不支持 video播放</video>
				</#if>
			</p>
			<hr>
			</#list>
			</#if>
        </div>
        <hr>
    </div>
</div>