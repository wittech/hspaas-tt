<div class="table_box">
	<table cellpadding="0" cellspacing="0" border="0">
		<thead>
			<tr class="even">
			 	<th>编号</th>
                <th>SID</th>
                <th>计</th>
                <th>返</th>
                <th>接口帐号</th>
                <th>手机号</th>
                <th>错号</th>
                <th>重号</th>
                <th>分包状态</th>
                <th>创建时间</th>
                <th>提交时间</th>
                <th>操作</th>
			</tr>
		</thead>
		<tbody>
          	<#if page.list?? && page.totalPage gt 0>
			<#list page.list as pl>
			<tr>
				<td>${(pl_index+1+(page.currentPage-1)*page.pageSize)!}</td>
				<td>${(pl.sid)!''}</td>
                <td>
                	<span style="color:red;">${(pl.fee)!'0'}</span>
                </td>
                <td>
                	<span style="color:red;">${(pl.returnFee)!'0'}</span>
                </td>
                <td>${(pl.userModel.appkey)!''}</td>
                <td>
                    <#if pl.mobiles?? && pl.mobiles?size gt 1>
                    ${pl.firstMobile!''}...
                        <a href="javascript:void(0);" title="${pl.mobile!''}">${pl.mobiles?size}个</a>
                    <#else>
                    ${(pl.mobile)!''}
                    </#if>
                </td>
                <td>
                    <#if pl.showErrorMobiles?? && pl.showErrorMobiles?size gt 1>
                    ${pl.showErrorFirstMobile!''}...
                        <a href="javascript:void(0);" title="${pl.errorMobiles!''}">${pl.showErrorMobiles?size}个</a>
                    <#else>
                    ${(pl.errorMobiles)!'--'}
                    </#if>
                </td>
                <td>
                    <#if pl.showRepeatMobiles?? && pl.showRepeatMobiles?size gt 1>
                        <a href="javascript:void(0);" title="${pl.repeatMobiles!''}">${pl.showRepeatMobiles?size}</a>
                    </#if>
                </td>
                <td>

                    <a href="javascript:void(0);" data-placement="top" class="btn btn-default btn-xs" data-toggle="popover" 
                       title="${pl.remark!''}">
                    <#if pl.processStatus == 0>
                        正在分包
                    <#elseif pl.processStatus == 1>
                        分包完成，待发送
                    <#elseif pl.processStatus == 2>
                        分包异常，待处理
                    <#elseif pl.processStatus == 3>
                        分包失败，终止
                    <#else>
                        未知
                    </#if>
                    </a>
                </td>
                <td>${pl.createTime?string('yyyy-MM-dd HH:mm:ss')}</td>
                <td>
                    <#if pl.processTime??>
                    ${pl.processTime?string('yyyy-MM-dd HH:mm:ss')}
                    <#else>
                        --
                    </#if>

                </td>
                <td>
                   <a href=javascript:sendRecord('${pl.sid}'); class="btn btn-success btn-xs">发送记录</a>
                </td>
            </tr>
            <tr>
                <td colspan="12" align="right">
                    <span style="word-break:break-all;float: right;">
                    ${pl.finalContent!''} <span style="color:red;">[字数：${pl.finalContent?length}]</span>
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

<script type="text/javascript">
	function sendRecord(sid){
		var startDate = $("#starDate").val();
		var endDate = $("#endDate").val();
		window.location.href="${rc.contextPath}/sms/record/send/index?sid="+sid+"&startDate="+startDate+"&endDate="+endDate;
	}
</script>