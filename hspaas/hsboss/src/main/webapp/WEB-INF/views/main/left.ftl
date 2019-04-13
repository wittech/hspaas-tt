<nav id="mainnav-container">
	<div id="mainnav">

		<!--Menu-->
		<!--================================-->
		<div id="mainnav-menu-wrap">
			<div class="nano">
				<div class="nano-content">
					<#if leftMenu??>
					<ul id="mainnav-menu" class="list-group">

						<li class="list-header">${leftMenu.menuName}</li>

						<#list leftMenu.childList as lc>
							<#if lc.hasChildMenu>
								<li <#if secondMenuId == lc.id>class="active"</#if>>
									<a href="javascript:;">
										<#if lc.icon?? && lc.icon != "">
				                    		<i class="fa ${(lc.icon)!}"></i>
				                    	<#else>
				                    		<i class="fa fa-th"></i>
				                    	</#if>
										<span class="menu-title">${lc.menuName}</span>
										<i class="arrow"></i>
									</a>
									<ul class="collapse <#if secondMenuId == lc.id>in</#if>">
										<#list lc.childList as lcc>
											<li><a href="${BASE_PATH}${lcc.menuUrl?if_exists}" <#if thirdMenuId == lcc.id>style="color:#2986b8"</#if>><i class="fa fa-caret-right"></i>${lcc.menuName}</a></li>
										</#list>
									</ul>
								</li>
							<#else>
								<li <#if secondMenuId == lc.id>class="active"</#if>>
									<a href="${BASE_PATH}${lc.menuUrl?if_exists}">
										<#if lc.icon?? && lc.icon != "">
				                    		<i class="fa ${(lc.icon)!}"></i>
				                    	<#else>
				                    		<i class="fa fa-square"></i>
				                    	</#if>
										
										<span class="menu-title">
										<strong>${lc.menuName}</strong>
									</span>
									</a>
								</li>
							</#if>
						</#list>
					</ul>
					<#else>
						未找到左侧菜单
					</#if>
				</div>
			</div>
		</div>
	</div>
</nav>