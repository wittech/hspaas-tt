<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
			 	<th>编号</th>
                <th>SID</th>
                <th>手机号</th>
                <th>发送状态</th>
                <th>回执状态</th>
                <th>回执时间</th>
                <th>是否推送</th>
                <th>创建时间</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as pl>
			<tr>
				<td>${(pl_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(pl.sid?if_exists)!}</td>
                <td>
                    ${(pl.mobile)!''}
                    [<#if pl.cmcp == 1>
                        移动
                    <#elseif pl.cmcp == 2>
          	电信              
                    <#elseif pl.cmcp == 3>
         	  联通             
                    <#else>
                        未知
                    </#if>]
                </td>
                <td>
                    <#if pl.status == 0>
                        成功
                    <#else>
                        失败
                    </#if>
                </td>
                <td>
                    <#if pl.messageDeliver?? && pl.messageDeliver gt 0>
                        <#if pl.messageDeliver.status == 0>
                            回执成功
                        <#else>
                            回执失败
                        </#if>
                    <#else>
                        未回执
                    </#if>
                </td>
                <td>
                    <#if pl.messageDeliver?? && pl.messageDeliver gt 0>
                    	${(pl.messageDeliver.deliverTime)!''}
                    <#else>
                        --
                    </#if>
                </td>
                <td>
                    <#if pl.needPush>
                        <#if pl.messagePush??>
                            <a href="javascript:void(0);" class="btn btn-danger btn-xs" data-toggle="popover"
                              title="
                               推送状态：<#if pls.messagePush.status == 1>成功<#else>失败</#if>     重试次数：${pls.messagePush.retryTimes!0}     推送时间：<#if pls.messagePush.createTime??>${pls.messagePush.createTime?string('yyyy-MM-dd HH:mm:ss')}</#if>
                            ">是</a>
                        <#else>
                        [未推送]
                        </#if>
                    <#else>
                        否
                    </#if>
                </td>
                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
            </tr>
            <tr>
                    <td colspan="8" style="word-break:break-all;text-align:right;">
                        ${pl.content!''}
                        <span style="color:red">
                        [字数：${pl.content?length}]
                        </span>
                    </td>
             </tr>
             </#list>
			<#else>
			<tr>
				<td colspan="6" style="color:#F00;text-align:center;">暂无发送记录！</td>
			</tr>
			</#if>
		</tbody>
	</table>
</div>

<#if page.totalPage gt 0>
	<@GM.pager current_page=page.currentPage total_record=page.totalRecord total_page=page.totalPage />
</#if>