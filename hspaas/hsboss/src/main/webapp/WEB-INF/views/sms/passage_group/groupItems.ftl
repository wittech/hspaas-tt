<#list cmcpList as cpl>
    <div class="form-group cmcps">
        <input type="hidden" class="cmcpType"  value="${cpl.cmcp.getCode()}">
        <label class="col-xs-2 control-label">${cpl.cmcp.getTitle()}通道</label>
        <div class="col-xs-3">
            <select class="form-control sourcePassage" style="height: 160px;" multiple="multiple">
                <#list cpl.passageList as cp>
                    <option value="${cp.id}">
                    ${cp.name!''}
                        <#if cp.type == 1>
                            &nbsp;[独]
                        </#if>
                        <#if cp.cmcp == 4>
                            &nbsp;[全]
                        </#if>
                    </option>
                </#list>
            </select>
        </div>
        <div class="col-xs-1" style="text-align: center">
            <div class="row" style="margin-top:20px">
                <a class="btn btn-default btn-xs sourceButton" >&nbsp;&nbsp;选&nbsp;&nbsp;择&nbsp;&nbsp;>>&nbsp;&nbsp;</a>
            </div>
            <div class="row" style="margin-top:30px">
                <a class="btn btn-default btn-xs targetButton">&nbsp;&nbsp;<<&nbsp;&nbsp;取&nbsp;&nbsp;消&nbsp;&nbsp;</a>
            </div>
        </div>
        <div class="col-xs-3">
            <select class="form-control targetPassage" style="height: 160px;"  multiple="multiple">
                <#if currentRouteType?? && group?? && group.detailList?? && group.detailList?size gt 0>
                    <#list group.detailList as gd>
                        <#if gd.routeType == currentRouteType && gd.cmcp ==  cpl.cmcp.getCode()>
                            <option value="${gd.smsPassage.id}" <#if gd.smsPassage.status?? && gd.smsPassage.status == 1>style="background: red"</#if>>
                            ${gd.smsPassage.name!''}
                                <#if gd.smsPassage.type == 1>
                                    &nbsp;[独]
                                </#if>
                                <#if gd.smsPassage.cmcp == 4>
                                    &nbsp;[全]
                                </#if>
                            </option>
                        </#if>
                    </#list>
                </#if>
            </select>
        </div>
        <div class="col-xs-1" style="text-align: center">
            <div class="row" style="margin-top:20px">
                <a class="btn btn-info btn-xs upButton">向上&nbsp;&nbsp;↑</a>
            </div>
            <div class="row" style="margin-top:30px">
                <a class="btn btn-warning btn-xs downButton">向下&nbsp;&nbsp;↓</a>
            </div>
        </div>
    </div>
</#list>