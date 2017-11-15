<div class="row">
    <div class="col-lg-1"></div>
    <div class="col-lg-1">&nbsp;&nbsp;通道设置</div>
    <div class="col-lg-8">
        <table class="table table-bordered table-striped">
            <thead>
            <tr>
                <th width="10%">省份</th>
            <#list cmcps as c>
                <#if c.getCode() != 0 && c.getCode() != 4>
                    <th width="25%">${c.getTitle()}</th>
                </#if>
            </#list>
            </tr>
            </thead>
            <tbody>
            <#--<tr code="-1" name="其他">-->
                <#--<td><b>其他</b></td>-->
                <#--<td>-->
                    <#--<a class="selectPassage btn btn-xs btn-success" cmcp="1" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>-->
                    <#--<span id="position_${routeType}_-1_1">-->

                        <#--</span>-->
                <#--</td>-->
                <#--<td>-->
                    <#--<a class="selectPassage btn btn-xs btn-info" cmcp="2" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>-->
                    <#--<span id="position_${routeType}_-1_2">-->

                        <#--</span>-->
                <#--</td>-->
                <#--<td>-->
                    <#--<a class="selectPassage btn btn-xs btn-primary" cmcp="3" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>-->
                    <#--<span id="position_${routeType}_-1_3">-->

                        <#--</span>-->
                <#--</td>-->
            <#--</tr>-->
            <#assign provincePassageList = dataMap['route_type_'+routeType] />
            <#list provincePassageList as pl>
            <tr code="${pl.province.code}" name="${pl.province.name}">
                <td><b>${pl.province.name}</b></td>
                <#list pl.cmcpList as pc>

                <td>
                    <a class="selectPassage btn btn-xs btn-<#if pc.cmcp.getCode() == 1>success<#elseif pc.cmcp.getCode() == 2>info<#elseif pc.cmcp.getCode() == 3>primary</#if>" cmcp="${pc.cmcp.getCode()}" href="javascript:void(0);"><i class="fa fa-cog"></i>&nbsp;设置</a>
                    <span id="position_${routeType}_${pl.province.code}_${pc.cmcp.getCode()}" class="finishSelectPassage" style="cursor: pointer"
                          data-placement="top" data-html="true" data-toggle="popover" title="已选通道"
                        data-content=""
                    >
                        <#if pc.passageInfos?size gt 0>
                            已选通道<font color="red">${pc.passageInfos?size}</font>个
                            <#list pc.passageInfos as pp>
                            <input type="hidden" name="passageInfo" value="${pp}">
                            </#list>
                        </#if>
                    </span>
                </td>
                </#list>
            </tr>
            </#list>
            </tbody>
        </table>
    </div>
</div>